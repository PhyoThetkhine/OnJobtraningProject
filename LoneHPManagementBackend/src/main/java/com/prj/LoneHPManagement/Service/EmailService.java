package com.prj.LoneHPManagement.Service;

public interface EmailService {
    void sendOtpEmail(String toEmail, String otp);
    void sendPassword(String toEmail, String password,String userCode);
}
