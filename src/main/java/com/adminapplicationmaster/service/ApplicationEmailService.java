package com.adminapplicationmaster.service;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;

/**
 * Application Email Service
 */
@Service
@Slf4j
public class ApplicationEmailService {
    
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private TemplateEngine templateEngine;
    
    @Value("${app.mail.default-from:from@example.com}")
    private String defaultFromAddress;
    
    /**
     * Send simple text email
     */
    public void sendSimpleEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(defaultFromAddress);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            
            mailSender.send(message);
            log.info("Simple email sent to: {}", to);
        } catch (Exception e) {
            log.error("Error sending simple email to: {}", to, e);
            throw new RuntimeException("Failed to send email", e);
        }
    }
    
    /**
     * Send HTML email with template
     */
    public void sendHtmlEmail(String to, String subject, String templateName, Context context) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            
            helper.setFrom(defaultFromAddress);
            helper.setTo(to);
            helper.setSubject(subject);
            
            // Process the template
            String htmlContent = templateEngine.process(templateName, context);
            helper.setText(htmlContent, true);
            
            mailSender.send(mimeMessage);
            log.info("HTML email sent to: {} using template: {}", to, templateName);
        } catch (MessagingException e) {
            log.error("Error sending HTML email to: {}", to, e);
            throw new RuntimeException("Failed to send email", e);
        }
    }
    
    /**
     * Send HTML email with custom from address
     */
    public void sendHtmlEmail(String from, String to, String subject, String templateName, Context context) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            
            helper.setFrom(Objects.requireNonNullElse(from, defaultFromAddress));
            helper.setTo(to);
            helper.setSubject(subject);
            
            String htmlContent = templateEngine.process(templateName, context);
            helper.setText(htmlContent, true);
            
            mailSender.send(mimeMessage);
            log.info("HTML email sent to: {} from: {} using template: {}", to, from, templateName);
        } catch (MessagingException e) {
            log.error("Error sending HTML email to: {}", to, e);
            throw new RuntimeException("Failed to send email", e);
        }
    }
    
    /**
     * Send email with both HTML and plain text versions
     */
    public void sendMultipartEmail(String to, String subject, String htmlTemplateName, String textTemplateName, Context context) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            
            helper.setFrom(defaultFromAddress);
            helper.setTo(to);
            helper.setSubject(subject);
            
            // Process both templates
            String htmlContent = templateEngine.process(htmlTemplateName, context);
            String textContent = templateEngine.process(textTemplateName, context);
            
            helper.setText(textContent, htmlContent);
            
            mailSender.send(mimeMessage);
            log.info("Multipart email sent to: {}", to);
        } catch (MessagingException e) {
            log.error("Error sending multipart email to: {}", to, e);
            throw new RuntimeException("Failed to send email", e);
        }
    }
    
    /**
     * Get default from address
     */
    public String getDefaultFromAddress() {
        return defaultFromAddress;
    }
}