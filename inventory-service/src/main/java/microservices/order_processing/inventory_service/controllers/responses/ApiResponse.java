package microservices.order_processing.inventory_service.controllers.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO-класс, представляющий стандартный ответ API.
 * <p>Используется для унифицированного представления результата выполнения запроса с информацией об успехе операции
 * и сопутствующим сообщением.</p>
 */
@Getter
@Setter
@AllArgsConstructor
public class ApiResponse {
    private boolean success;
    private String message;
}
