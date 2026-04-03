package com.example.jobportal.service;

import com.example.jobportal.dto.*;
import java.util.List;

public interface AdminService {
    // delete admin's own account
    void deleteAdminAccount(Long adminUserId);

    // reset recruiter report count
    void resetRecruiterReport(Long recruiterId);

    // delete recruiter account
    void deleteRecruiter(Long recruiterId);

    // get all reported recruiters
    List<ProfileResponse> getReportedRecruiters();

}
