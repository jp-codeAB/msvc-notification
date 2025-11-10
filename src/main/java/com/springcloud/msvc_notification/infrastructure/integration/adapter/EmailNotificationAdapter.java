package com.springcloud.msvc_notification.infrastructure.integration.adapter;

import com.springcloud.msvc_notification.domain.model.Notification;
import com.springcloud.msvc_notification.domain.ports.out.IEmailServicePort;
import com.springcloud.msvc_notification.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailNotificationAdapter implements IEmailServicePort {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    @Override
    public void sendEmail(Notification request) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(senderEmail);
        message.setTo(request.getToEmail());
        message.setSubject(request.getSubject());
        message.setText(request.getBody());

        try {
            mailSender.send(message);
            log.info("Email was sent successfully to: {}", request.getToEmail());
        } catch (Exception e) {
            // Se propaga una excepción de negocio/compartida para un manejo coherente
            log.error("ERROR sending email to {}: {}", request.getToEmail(), e.getMessage());
            throw new BusinessException("Failed to send email notification.", e);
        }
    }

}