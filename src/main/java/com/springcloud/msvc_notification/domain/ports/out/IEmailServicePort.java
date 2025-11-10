package com.springcloud.msvc_notification.domain.ports.out;

import com.springcloud.msvc_notification.domain.model.Notification;

public interface IEmailServicePort {
    void sendEmail(Notification request);
}