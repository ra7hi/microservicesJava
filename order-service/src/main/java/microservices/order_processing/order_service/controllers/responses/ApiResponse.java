package microservices.order_processing.order_service.controllers.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Универсальный ответ API для операций, не возвращающих объект.
 * Содержит флаг успешности выполнения и сообщение.
 */
@Getter
@Setter
@AllArgsConstructor
public class ApiResponse {
    private boolean success;
    private String message;
}
