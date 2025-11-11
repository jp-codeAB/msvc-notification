package com.springcloud.msvc_notification.domain.ports.in;

import com.springcloud.msvc_notification.domain.model.PaymentStatusEvent;
import com.springcloud.msvc_notification.shared.event.OrderStatusEvent;

public interface IEventHandlerPort {
    void handlePaymentStatusUpdate(PaymentStatusEvent event);
    void handleOrderStatusUpdate(OrderStatusEvent event);
}