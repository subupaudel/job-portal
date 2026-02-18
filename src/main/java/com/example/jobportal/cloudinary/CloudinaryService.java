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

    /**
     * Uploads a file to Cloudinary and returns the secure URL
     */
    public String uploadImage(MultipartFile imageFile) {
        try {
            if (imageFile == null || imageFile.isEmpty()) {
                throw new RuntimeException("Image file is empty");
            }

            String contentType = imageFile.getContentType();
            if (contentType == null || !contentType.startsWith("image")) {
                throw new JobException("Only image files are allowed");
            }

            Map uploadResult = cloudinary.uploader().upload(
                    imageFile.getBytes(),
                    ObjectUtils.asMap(
                            "folder", "recruiters/logos",
                            "resource_type", "image"
                    )
            );

            return uploadResult.get("secure_url").toString();

        } catch (IOException e) {
            throw new JobException("Image upload failed");
        }
    }

    /**
     * Upload a PDF (resume) to Cloudinary
     */
    public String uploadPDF(MultipartFile pdfFile) {
        try {
            if (pdfFile == null || pdfFile.isEmpty()) {
                throw new RuntimeException("PDF file is empty");
            }

            String contentType = pdfFile.getContentType();
            if (!"application/pdf".equals(contentType)) {
                throw new RuntimeException("Only PDF files are allowed");
            }

            Map uploadResult = cloudinary.uploader().upload(
                    pdfFile.getBytes(),
                    ObjectUtils.asMap(
                            "folder", "seekers/resumes",
                            "resource_type", "auto"
                    )
            );

            return uploadResult.get("secure_url").toString();

        } catch (IOException e) {
            throw new JobException("PDF upload failed");
        }
    }

    /**
     * Optional: Delete file by publicId
     */
    public void deleteFile(String publicUrl) {
        try {
            if (publicUrl == null || publicUrl.isEmpty()) return;

            String[] parts = publicUrl.split("/");
            String filename = parts[parts.length - 1];
            String publicId = filename.substring(0, filename.lastIndexOf('.'));
            cloudinary.uploader().destroy("recruiters/logos/" + publicId, ObjectUtils.emptyMap());

        } catch (Exception e) {
            throw new RuntimeException("Failed to delete image", e);
        }
    }
}
