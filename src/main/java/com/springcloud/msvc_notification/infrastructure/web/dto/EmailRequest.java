package com.springcloud.msvc_notification.infrastructure.web.dto;

public record EmailRequest(String toEmail, String subject, String body) {}
