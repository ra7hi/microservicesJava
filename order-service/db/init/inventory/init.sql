DO $$ BEGIN
    CREATE TYPE reservation_status AS ENUM ('RESERVED', 'CONFIRMED', 'RELEASED');
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

CREATE TABLE IF NOT EXISTS product (
                                       id SERIAL PRIMARY KEY,
                                       name VARCHAR(255) NOT NULL,
                                       price DOUBLE PRECISION NOT NULL,
                                       quantity BIGINT NOT NULL,
                                       sale DOUBLE PRECISION NOT NULL
);

CREATE TABLE IF NOT EXISTS reservations (
                                            id SERIAL PRIMARY KEY,
                                            saga_id VARCHAR(255) NOT NULL,
                                            product_id BIGINT NOT NULL,
                                            quantity BIGINT NOT NULL,
                                            status reservation_status NOT NULL,
                                            created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);