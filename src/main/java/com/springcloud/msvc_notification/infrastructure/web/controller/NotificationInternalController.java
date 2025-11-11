package com.springcloud.msvc_notification.infrastructure.web.controller;

import com.springcloud.msvc_notification.domain.model.Notification;
import com.springcloud.msvc_notification.domain.ports.in.INotificationUseCase;
import com.springcloud.msvc_notification.infrastructure.mapper.NotificationWebMapper;
import com.springcloud.msvc_notification.infrastructure.web.dto.EmailRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notifications/internal")
@RequiredArgsConstructor
public class NotificationInternalController {

    private final INotificationUseCase notificationUseCase;
    private final NotificationWebMapper mapper;

    @PostMapping
    public ResponseEntity<String> sendEmail(@RequestBody EmailRequest request){

        Notification domainRequest = mapper.toDomain(request);

        notificationUseCase.sendNotification(domainRequest);

        return ResponseEntity.ok("Email sent successfully (synchronous request)");
    }
}