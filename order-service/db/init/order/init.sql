-- Создание enum-типа для ролей (Role)
DO $$ BEGIN
    CREATE TYPE role_enum AS ENUM ('ADMIN', 'USER');
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

DO $$ BEGIN
    CREATE TYPE reservation_status AS ENUM ('RESERVED', 'CONFIRMED', 'RELEASED');
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

DO $$ BEGIN
    CREATE TYPE saga_status AS ENUM (
        'STARTED',
        'INVENTORY_RESERVED',
        'ORDER_CREATED',
        'COMPLETED',
        'FAILED',
        'COMPENSATING',
        'COMPENSATED'
        );
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

CREATE TABLE IF NOT EXISTS reservations (
                                            id SERIAL PRIMARY KEY,
                                            saga_id VARCHAR(255) NOT NULL ,
                                            product_id BIGINT NOT NULL ,
                                            quantity BIGINT NOT NULL ,
                                            status reservation_status NOT NULL ,
                                            created_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS saga_states (
                                           saga_id VARCHAR(255) PRIMARY KEY,
                                           order_id VARCHAR(255),
                                           user_id BIGINT,
                                           status saga_status,
                                           created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                           updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS users (
                                     id SERIAL PRIMARY KEY,
                                     username VARCHAR(255) NOT NULL UNIQUE,
                                     password VARCHAR(255) NOT NULL,
                                     email VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS user_roles (
                                          user_id BIGINT NOT NULL,
                                          roles role_enum NOT NULL,
                                          CONSTRAINT fk_user_roles_user
                                              FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
