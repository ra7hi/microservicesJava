package microservices.order_processing.notification_service.dto;

import lombok.Data;
import lombok.Builder;

/**
 * DTO, представляющий товар в составе заказа, используемый в микросервисе уведомлений notification-service.
 * Используется для детализации состава заказа при сохранении заказа.
 */
@Data
@Builder
public class OrderItemsDto {
    private Long productId;

    private Long quantity;

    private Double price;

    private Double sale;
}
