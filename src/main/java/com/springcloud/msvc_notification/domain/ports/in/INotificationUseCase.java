package com.springcloud.msvc_notification.domain.ports.in;

import com.springcloud.msvc_notification.domain.model.Notification;

public interface INotificationUseCase {
    void sendNotification(Notification request);
}