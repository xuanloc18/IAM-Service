package dev.cxl.iam_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    public void SendEmail(String email, String otp) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setText(otp.toString());
        javaMailSender.send(message);
    }
}
