package microservices.order_processing.order_service.controllers.requests;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderRequest {
    private List<Long> productIds;
}
