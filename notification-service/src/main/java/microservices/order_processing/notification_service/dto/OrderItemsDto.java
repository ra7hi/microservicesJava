package microservices.order_processing.notification_service.dto;

import lombok.*;

@Data
@Builder
public class OrderItemsDto {
    private Long productId;

    private Long quantity;

    private Double price;

    private Double sale;
}
