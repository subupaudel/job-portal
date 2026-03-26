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

            String url = uploadResult.get("secure_url").toString();
            String publicId = uploadResult.get("public_id").toString();

            return url + "|" + publicId;
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
                            "resource_type", "raw"   //
                    )
            );

            String url = uploadResult.get("secure_url").toString();
            String publicId = uploadResult.get("public_id").toString();

            return url + "|" + publicId; // consistent with image method

        } catch (IOException e) {
            throw new JobException("PDF upload failed");
        }
    }

    public void deleteFile(String publicId) {
        try {
            if (publicId == null || publicId.isEmpty()) return;

            cloudinary.uploader().destroy(
                    publicId,
                    ObjectUtils.asMap("invalidate", true)
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete file", e);
        }
    }

}
