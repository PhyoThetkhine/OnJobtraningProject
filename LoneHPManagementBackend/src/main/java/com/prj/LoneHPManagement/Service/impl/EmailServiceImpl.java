package com.prj.LoneHPManagement.Service.impl;

import com.prj.LoneHPManagement.Service.EmailService;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {
    @Autowired
    private JavaMailSender mailSender;
    private  final static Logger logger = LoggerFactory.getLogger(EmailService.class);
    public void sendOtpEmail(String toEmail, String otp) {

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("Your OTP Code");
            message.setText("Dear User,\n\nYour OTP code is: " + otp + "\n\nThis OTP is valid for 2 minutes.\n\nRegards,\nYourApp Team");
            mailSender.send(message);
            logger.info("Sent OTP to {}", toEmail);
            // System.out.println("OTP"+otp);
        } catch (Exception e) {
            logger.error("Error sending OTP to {}: {}", toEmail, e.getMessage());
        }
    }
    @Override
    public void sendPassword(String toEmail, String password,String userCode) {
        try {
            // Create a MimeMessage for HTML email content
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            // Set recipient and subject
            helper.setTo(toEmail);
            helper.setSubject("Your Account Details");

            // Create the HTML content including both password and user code
            String htmlContent = "<html><body>"
                    + "<p>Dear User,</p>"
                    + "<p>Your account has been created successfully. Here are your account details:</p>"
                    + "<p><strong>User Code: " + userCode + "</strong></p>"
                    + "<p><strong>Password: " + password + "</strong></p>"
                    + "<p>Please keep this information safe and do not share it with anyone.</p>"
                    + "<p>Regards,<br>YourApp Team</p>"
                    + "</body></html>";

            // Set the HTML content to the email
            helper.setText(htmlContent, true);  // 'true' indicates HTML content

            // Send the message
            mailSender.send(mimeMessage);

            logger.info("Sent password email to {}", toEmail);
        } catch (Exception e) {
            logger.error("Error sending password email to {}: {}", toEmail, e.getMessage());
        }
    }
}
