package com.springcloud.msvc_notification.domain.model;

import lombok.Getter;
import lombok.Builder;

@Getter
@Builder
public class Notification {
    private String toEmail;
    private String subject;
    private String body;
}