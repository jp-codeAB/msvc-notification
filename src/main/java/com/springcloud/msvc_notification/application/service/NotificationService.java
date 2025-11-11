package com.springcloud.msvc_notification.application.service;

import com.springcloud.msvc_notification.domain.model.Notification;
import com.springcloud.msvc_notification.domain.model.PaymentStatusEvent;
import com.springcloud.msvc_notification.domain.ports.in.IEventHandlerPort;
import com.springcloud.msvc_notification.domain.ports.in.INotificationUseCase;
import com.springcloud.msvc_notification.domain.ports.out.IEmailServicePort;
import com.springcloud.msvc_notification.shared.event.OrderStatusEvent;
import com.springcloud.msvc_notification.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService implements IEventHandlerPort, INotificationUseCase {

    private final IEmailServicePort emailServicePort;

    @Override
    public void handlePaymentStatusUpdate(PaymentStatusEvent event) {
        log.info("Processing asynchronous payment status update. Order ID: {} | Status: {}", event.getOrderId(), event.getPaymentStatus());
        String subject;
        String body;
        String customerEmail = event.getCustomerEmail();

        switch (event.getPaymentStatus()) {
            case "CONFIRMED":
                subject = "✅ ¡Pago Confirmado! Su Orden ARKA está en preparación.";
                body = String.format(
                        "Estimado cliente,\n\n" +
                                "Le confirmamos que hemos recibido su pago con éxito para la **orden #%d**. 🎉\n\n" +
                                "Su pedido ya se encuentra en la etapa de preparación en nuestro almacén. Le notificaremos nuevamente " +
                                "cuando su orden sea despachada y se encuentre en camino.\n\n" +
                                "Gracias por confiar en ARKA.\n\n" +
                                "Atentamente,\n" +
                                "El equipo ARKA",
                        event.getOrderId());
                break;
            case "FAILED":
                subject = "❌ Pago Rechazado. ¡Atención con su Orden ARKA!";
                body = String.format(
                        "Estimado cliente,\n\n" +
                                "Lamentamos informarle que el intento de pago para la **orden #%d** ha sido **rechazado**.\n\n" +
                                "Su orden está actualmente retenida. Por favor, revise los datos de su método de pago " +
                                "y realice un nuevo intento lo antes posible para evitar la cancelación.\n\n" +
                                "Si tiene alguna duda, no dude en contactarnos.\n\n" +
                                "Atentamente,\n" +
                                "El equipo ARKA",
                        event.getOrderId());
                break;
            default:
                log.warn("Payment status {} is not configured for email notification. Skipping.", event.getPaymentStatus());
                return;
        }
        Notification request = Notification.builder()
                .toEmail(customerEmail)
                .subject(subject)
                .body(body)
                .build();
        try {
            emailServicePort.sendEmail(request);
        } catch (Exception e) {

            log.error("Error sending email for Order ID: {}. Triggering stream retry/DLQ.", event.getOrderId());
            throw new BusinessException("Failed to process event notification and send email.", e);
        }
    }

    @Override
    public void sendNotification(Notification request) {
        log.info("Processing synchronous email request to: {}", request.getToEmail());
        emailServicePort.sendEmail(request);
    }

    @Override
    public void handleOrderStatusUpdate(OrderStatusEvent event) {
        log.info("Processing order status update. Order ID: {} | Status: {} | Email: {}",
                event.orderId(), event.newStatus(), event.customerEmail());
        String subject;
        String body;
        String customerEmail = event.customerEmail();

        switch (event.newStatus().toUpperCase()) {
            case "PENDING":
                subject = "🎉 ¡Orden Creada con Éxito! ";
                body = String.format(
                        "Estimado cliente,\n\n" +
                                "¡Gracias por su compra! Su **orden #%d** ha sido creada exitosamente y se encuentra actualmente en estado **PENDIENTE de pago**.\n\n" +
                                "**Fecha de Creación:** %s\n\n" +
                                "Una vez que su pago sea confirmado, su orden pasará a la etapa de preparación y le enviaremos un correo de confirmación de pago.\n\n" +
                                "Atentamente,\n" +
                                "El equipo ARKA",
                        event.orderId(),
                        event.eventTimestamp());
                break;
            case "CONFIRMED":
                subject = "Orden Confirmada con Éxito.";
                body = String.format(
                        "Estimado cliente,\n\n" +
                                "Su **orden #%d** ha sido marcada como **CONFIRMADA** y está siendo procesada.\n" +
                                "Pronto pasará a despacho.\n\n" +
                                "Atentamente,\n" +
                                "El equipo ARKA",
                        event.orderId());
                break;

            case "IN_DISPATCH":
                subject = "🚚 ¡Tu Orden ARKA Está en Camino!";
                body = String.format(
                        "Estimado cliente,\n\n" +
                                "¡Excelentes noticias! Su **orden #%d** ha sido **despachada** y está en manos de nuestra transportadora. 📦\n\n" +
                                "Recibirá su paquete en los próximos días hábiles.\n\n" +
                                "Atentamente,\n" +
                                "El equipo ARKA",
                        event.orderId());
                break;

            case "DELIVERED":
                subject = "🥳 ¡Orden Entregada! Gracias por su compra en ARKA.";
                body = String.format(
                        "Estimado cliente,\n\n" +
                                "Confirmamos que su **orden #%d** ha sido **entregada** con éxito. Esperamos que disfrute sus productos. 👍\n\n" +
                                "Si tiene algún inconveniente o necesita realizar una devolución, por favor contacte a nuestro equipo de soporte.\n\n" +
                                "Atentamente,\n" +
                                "El equipo ARKA",
                        event.orderId());
                break;

            case "CANCELLED":
                subject = "🚫 Su Orden ARKA ha sido Cancelada.";
                body = String.format(
                        "Estimado cliente,\n\n" +
                                "Su **orden #%d** ha sido **cancelada** a solicitud o debido a la expiración del plazo de pago.\n\n" +
                                "Si la cancelación fue un error o si tiene alguna pregunta sobre el reembolso (si aplica), por favor contacte a nuestro equipo de soporte.\n\n" +
                                "Atentamente,\n" +
                                "El equipo ARKA",
                        event.orderId());
                break;

            default:
                log.warn("Order status {} is not configured for email notification. Skipping.", event.newStatus());
                return;
        }

        Notification request = Notification.builder()
                .toEmail(customerEmail)
                .subject(subject)
                .body(body)
                .build();
        try {
            emailServicePort.sendEmail(request);
        } catch (Exception e) {
            log.error("Error sending email for Order ID: {}. Triggering stream retry/DLQ.", event.orderId());
            throw new BusinessException("Failed to process event notification and send email.", e);
        }
    }
}