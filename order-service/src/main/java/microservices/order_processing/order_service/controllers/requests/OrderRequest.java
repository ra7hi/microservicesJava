package microservices.order_processing.order_service.controllers.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * DTO-запрос на создание заказа.
 * Содержит список продуктов с указанием их количества и уникальный идентификатор заказа.
 * Используется клиентом при отправке запроса на оформление заказа.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequest {
    /**
     * Список продуктов, включённых в заказ.
     * Каждый элемент содержит {@link ProductOrderRequest} с идентификатором продукта и его количеством.
     */
    private List<ProductOrderRequest> productsRequest;
    private String orderId;
}
