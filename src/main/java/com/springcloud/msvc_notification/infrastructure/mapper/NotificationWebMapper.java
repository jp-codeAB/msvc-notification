package com.springcloud.msvc_notification.infrastructure.mapper;

import com.springcloud.msvc_notification.domain.model.Notification;
import com.springcloud.msvc_notification.infrastructure.web.dto.EmailRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NotificationWebMapper {

    Notification toDomain(EmailRequest dto);
}