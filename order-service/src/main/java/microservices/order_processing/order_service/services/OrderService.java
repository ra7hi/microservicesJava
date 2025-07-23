package microservices.order_processing.order_service.services;

import microservices.order_processing.order_service.controllers.requests.OrderRequest;
import microservices.order_processing.order_service.controllers.responses.OrderResponse;

public interface OrderService {
    OrderResponse processOrderCreation(String username, OrderRequest orderRequest);
}
