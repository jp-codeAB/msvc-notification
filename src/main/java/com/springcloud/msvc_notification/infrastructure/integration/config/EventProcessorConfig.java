package com.springcloud.msvc_notification.infrastructure.integration.config;

import com.springcloud.msvc_notification.domain.ports.in.IEventHandlerPort;
import com.springcloud.msvc_notification.domain.model.PaymentStatusEvent;
import com.springcloud.msvc_notification.shared.event.OrderStatusEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
@RequiredArgsConstructor
public class EventProcessorConfig {

    private final IEventHandlerPort eventHandlerPort;

    @Bean
    public Consumer<PaymentStatusEvent> paymentStatusUpdate() {
        return eventHandlerPort::handlePaymentStatusUpdate;
    }

    @Bean
    public Consumer<OrderStatusEvent> processOrderEvent() {
        return eventHandlerPort::handleOrderStatusUpdate;
    }
}