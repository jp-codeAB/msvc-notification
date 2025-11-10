package com.springcloud.msvc_notification.domain.model;

import lombok.Getter;
import lombok.Builder;
import java.time.LocalDateTime;

@Getter
@Builder
public class PaymentStatusEvent {
    private String eventId;
    private Long orderId;
    private Long customerId;
    private String customerEmail;
    private String paymentStatus;
    private LocalDateTime timestamp;
}