package com.example.jobportal.service;

import com.example.jobportal.dto.*;
import java.util.List;

public interface AdminService {

    void deleteAdminAccount(Long adminUserId);

    void resetRecruiterReport(Long recruiterId);

    void deleteRecruiter(Long recruiterId);

    List<ProfileResponse> getReportedRecruiters();

}
