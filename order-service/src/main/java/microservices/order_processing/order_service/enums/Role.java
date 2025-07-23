package microservices.order_processing.order_service.enums;

import lombok.Getter;

@Getter
public enum Role {
    ADMIN("ADMIN"),
    USER("USER");

    private final String authority;

    Role(String authority) {
        this.authority = authority;
    }
}
