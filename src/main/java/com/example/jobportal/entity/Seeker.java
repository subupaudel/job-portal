package com.example.jobportal.entity;

import com.example.jobportal.enums.Qualification;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "seekers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Seeker {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    private String firstName;
    private String lastName;
    private String phone;
    private String skills;
    private String experience;

    @Enumerated(EnumType.STRING)
    private Qualification qualification;
}
