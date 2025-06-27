package microservices.order_processing.order_service.entities;

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
