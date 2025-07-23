CREATE TABLE IF NOT EXISTS orders (
                                      id VARCHAR(255) PRIMARY KEY,
                                      total_price DOUBLE PRECISION,
                                      user_id BIGINT,
                                      order_date TIMESTAMP
);

CREATE TABLE IF NOT EXISTS order_items (
                                           id SERIAL PRIMARY KEY,
                                           order_id VARCHAR(255) NOT NULL,
                                           product_id BIGINT NOT NULL,
                                           quantity BIGINT NOT NULL,
                                           price DOUBLE PRECISION NOT NULL,
                                           sale DOUBLE PRECISION NOT NULL,
                                           CONSTRAINT fk_order_items_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
);
