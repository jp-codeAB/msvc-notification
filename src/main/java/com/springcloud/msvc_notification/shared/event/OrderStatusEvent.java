package com.springcloud.msvc_notification.shared.event;

import java.time.LocalDateTime;

public record OrderStatusEvent(
        Long orderId,
        String customerEmail,
        String newStatus,
        LocalDateTime eventTimestamp
) {
}