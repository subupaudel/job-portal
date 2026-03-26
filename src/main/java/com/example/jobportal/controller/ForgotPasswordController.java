package com.example.jobportal.controller;

import com.example.jobportal.dto.ChangePassword;
import com.example.jobportal.dto.MailBody;
import com.example.jobportal.entity.ForgotPassword;
import com.example.jobportal.entity.User;
import com.example.jobportal.repository.ForgotPasswordRepository;
import com.example.jobportal.repository.UserRepository;
import com.example.jobportal.service.EmailService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.Random;

@RestController
@RequestMapping("/forgotPassword")
public class ForgotPasswordController {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final ForgotPasswordRepository forgotPasswordRepository;
    private final PasswordEncoder passwordEncoder;

    public ForgotPasswordController(UserRepository userRepository, EmailService emailService, ForgotPasswordRepository forgotPasswordRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.forgotPasswordRepository = forgotPasswordRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/verifyMail/{email}")
    public ResponseEntity<String> verifyMail(@PathVariable("email") String email){
        User user = userRepository.findByEmail(email).orElseThrow(()->new UsernameNotFoundException("User not found"));


        int otp = otpGenerator();
        MailBody mailBody = MailBody.builder()
                .to(email)
                .subject("OTP for Forgot Password")
                .text("Your OTP for Forgot Password is : "+ otp)
                .build();

        ForgotPassword forgotPassword = ForgotPassword.builder()
                .otp(otp)
                .expirationTime(new Date(System.currentTimeMillis() + 60 * 600000))
                .user(user)
                .build();

        emailService.sendSimpleMessage(mailBody);
        forgotPasswordRepository.save(forgotPassword);

        return ResponseEntity.ok().body("Email sent for verification");
    }

    @PostMapping("/verifyOTP/{otp}/{email}")
    public ResponseEntity<String> verifyOtp(@PathVariable Integer otp, @PathVariable String email){
        User user = userRepository.findByEmail(email).orElseThrow(()->new UsernameNotFoundException("User not found"));

        ForgotPassword fp = forgotPasswordRepository.findByOtpAndUser(otp, email).orElseThrow(()-> new RuntimeException("Invalid OTP"));

        if(fp.getExpirationTime().before(Date.from(Instant.now()))) {
            forgotPasswordRepository.deleteById(fp.getFpid());
            return new ResponseEntity<>("OTP has expired",HttpStatus.EXPECTATION_FAILED);
        }

        return ResponseEntity.ok().body("OTP verified");
    }

    @PostMapping("/changePassword/{email}")
    public ResponseEntity<String> changePassword(@RequestBody ChangePassword changePassword,  @PathVariable("email") String email){

        if (!Objects.equals(changePassword.password(), changePassword.repeatPassword())){
            return new ResponseEntity<>("Passwords do not match",HttpStatus.BAD_REQUEST);
        }

        String encodedPassword = passwordEncoder.encode(changePassword.password());
        userRepository.updatePasswordByEmail(email, encodedPassword);
        return new ResponseEntity<>("Password changed",HttpStatus.OK);

    }

    private Integer otpGenerator(){
        Random random = new Random();
        return random.nextInt(100_000, 999_999);
    }
}
