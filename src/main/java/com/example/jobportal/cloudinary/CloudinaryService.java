package com.example.jobportal.cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.jobportal.exception.JobException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;

    // ---------------- UPLOAD IMAGE ----------------
    public String uploadImage(MultipartFile file) {
        validateFile(file, "image");

        try {
            Map uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", "recruiters/logos",
                            "resource_type", "image"
                    )
            );
            return buildResult(uploadResult);
        } catch (IOException e) {
            throw JobException.badRequest("Image upload failed");
        }
    }

    // ---------------- UPLOAD PDF ----------------
    public String uploadPDF(MultipartFile file) {
        validateFile(file, "pdf");

        try {
            Map uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", "seekers/resumes",
                            "resource_type", "image",
                            "format", "pdf",
                            "use_filename", true,
                            "unique_filename", false

                    )
            );
            return buildResult(uploadResult);
        } catch (IOException e) {
            throw JobException.badRequest("PDF upload failed");
        }
    }

    // ---------------- DELETE FILE ----------------
    public void deleteFile(String publicId) {
        if (publicId == null || publicId.isEmpty()) return;

        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.asMap("invalidate", true));
        } catch (Exception e) {
            throw JobException.badRequest("Failed to delete file");
        }
    }

    // ---------------- VALIDATION ----------------
    private void validateFile(MultipartFile file, String type) {
        if (file == null || file.isEmpty()) {
            throw type.equals("image") ?
                    JobException.badRequest("Image file is empty") :
                    JobException.badRequest("PDF file is empty");
        }

        String contentType = file.getContentType();
        if ("image".equals(type) && (contentType == null || !contentType.startsWith("image"))) {
            throw JobException.badRequest("Only image files are allowed");
        } else if ("pdf".equals(type) && !"application/pdf".equals(contentType)) {
            throw JobException.badRequest("Only PDF files are allowed");
        }
    }

    // ---------------- HELPER ----------------
    private String buildResult(Map uploadResult) {
        String url = uploadResult.get("secure_url").toString();
        String publicId = uploadResult.get("public_id").toString();
        return url + "|" + publicId;
    }
}