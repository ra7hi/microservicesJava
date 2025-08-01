package microservices.order_processing.order_service.enums;

import lombok.Getter;
/**
 * Перечисление ролей пользователя в системе.
 * Используется для управления доступом и авторизацией.
 * <p>
 * Каждая роль хранит строковое представление (authority), которое используется системой безопасности.
 * </p>
 * <ul>
 *     <li>{@link #ADMIN} — Администратор системы, имеет расширенные права доступа (редактирование, создание, удаление пользователей).</li>
 *     <li>{@link #USER} — Обычный пользователь, имеет стандартные права.</li>
 * </ul>
 */
@Getter
public enum Role {
    ADMIN("ADMIN"),
    USER("USER");

    private final String authority;

    Role(String authority) {
        this.authority = authority;
    }
}
