DROP DATABASE IF EXISTS barosiksa;
CREATE DATABASE barosiksa;
USE barosiksa;

-- 1. members 테이블 (종속성 없음)
CREATE TABLE members
(
    member_id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    username        VARCHAR(50)  NOT NULL,
    email           VARCHAR(100) NOT NULL UNIQUE,
    password        VARCHAR(255) NOT NULL,
    role            VARCHAR(255) NOT NULL DEFAULT 'USER',
    food_preference TEXT,
    is_deleted      TINYINT(1)   NOT NULL DEFAULT 0,
    created_at      TIMESTAMP             DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP             DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 2. Restaurants 테이블 (members 참조)
CREATE TABLE restaurants
(
    restaurant_id    BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id        BIGINT         NOT NULL,
    name             VARCHAR(100)   NOT NULL,
    category         VARCHAR(50)    NOT NULL,
    address          VARCHAR(255)   NOT NULL,
    latitude         DECIMAL(10, 8) NOT NULL,
    longitude        DECIMAL(11, 8) NOT NULL,
    phone_number     VARCHAR(20),
    description      TEXT,
    open_time        TIME           NOT NULL,
    close_time       TIME           NOT NULL,
    break_start_time TIME,
    break_end_time   TIME,
    closed_days      VARCHAR(100),
    is_deleted       TINYINT(1)     NOT NULL DEFAULT 0,
    created_at       TIMESTAMP               DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP               DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (member_id) REFERENCES members (member_id)
);

-- 3. Ingredients 테이블 (종속성 없음)
CREATE TABLE ingredients
(
    ingredient_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(50) NOT NULL UNIQUE,
    is_allergenic TINYINT(1)  NOT NULL DEFAULT 0,
    created_at    TIMESTAMP            DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP            DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 4. Restaurant_Ingredients 테이블 (restaurants, ingredients 참조)
CREATE TABLE restaurant_ingredients
(
    restaurant_ingredient_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    restaurant_id            BIGINT NOT NULL,
    ingredient_id            BIGINT NOT NULL,
    origin                   VARCHAR(50),
    stock_quantity           INT    NOT NULL DEFAULT 0,
    created_at               TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    updated_at               TIMESTAMP       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (restaurant_id) REFERENCES restaurants (restaurant_id),
    FOREIGN KEY (ingredient_id) REFERENCES ingredients (ingredient_id),
    UNIQUE KEY uq_rest_ingred (restaurant_id, ingredient_id)
);

-- 5. Menus 테이블 (restaurants 참조)
CREATE TABLE menus
(
    menu_id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    restaurant_id BIGINT       NOT NULL,
    name          VARCHAR(100) NOT NULL,
    price         INT          NOT NULL,
    description   TEXT,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (restaurant_id) REFERENCES restaurants (restaurant_id)
);

-- 6. Menu_Ingredients 테이블 (menus, restaurant_ingredients 참조)
CREATE TABLE menu_ingredients
(
    menu_ingredient_id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    menu_id                  BIGINT NOT NULL,
    restaurant_ingredient_id BIGINT NOT NULL,
    FOREIGN KEY (menu_id) REFERENCES menus (menu_id),
    FOREIGN KEY (restaurant_ingredient_id) REFERENCES restaurant_ingredients (restaurant_ingredient_id),
    UNIQUE KEY uq_menu_ingredient (menu_id, restaurant_ingredient_id)
);

-- 7. Reservations 테이블 (members, restaurants 참조)
CREATE TABLE reservations
(
    reservation_id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id        BIGINT      NOT NULL,
    restaurant_id    BIGINT      NOT NULL,
    reservation_time DATETIME    NOT NULL,
    status           VARCHAR(20) NOT NULL,
    created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (member_id) REFERENCES members (member_id),
    FOREIGN KEY (restaurant_id) REFERENCES restaurants (restaurant_id)
);

-- 8. Reservation_items 테이블 (menus, reservations 참조)
CREATE TABLE reservation_items
(
    reservation_item_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    menu_id             BIGINT NOT NULL,
    reservation_id      BIGINT NOT NULL,
    quantity            INT    NOT NULL,
    ordered_price       INT    NOT NULL,
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (menu_id) REFERENCES menus (menu_id),
    FOREIGN KEY (reservation_id) REFERENCES reservations (reservation_id)
);

-- 9. Reviews 테이블 (members, restaurants 참조)
CREATE TABLE reviews
(
    review_id     BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id     BIGINT NOT NULL,
    restaurant_id BIGINT NOT NULL,
    rating        INT    NOT NULL,
    comment       TEXT,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (member_id) REFERENCES members (member_id),
    FOREIGN KEY (restaurant_id) REFERENCES restaurants (restaurant_id)
);