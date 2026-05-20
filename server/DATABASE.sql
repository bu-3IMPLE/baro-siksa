DROP DATABASE IF EXISTS barosiksa;

CREATE DATABASE barosiksa;

USE barosiksa;

-- 1. members 테이블
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

-- 2. Restaurants 테이블
CREATE TABLE restaurants
(
    restaurant_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(100)   NOT NULL,
    address       VARCHAR(255)   NOT NULL,
    latitude      DECIMAL(10, 8) NOT NULL,
    longitude     DECIMAL(11, 8) NOT NULL,
    phone_number  VARCHAR(20),
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 3. Ingredients 테이블
CREATE TABLE ingredients
(
    ingredient_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name_kr       VARCHAR(50) NOT NULL,
    name_en       VARCHAR(50) NOT NULL
);

-- 4. Reservations 테이블
CREATE TABLE reservations
(
    reservation_id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id        BIGINT      NOT NULL,
    restaurant_id    BIGINT      NOT NULL,
    reservation_time DATETIME    NOT NULL,
    status           VARCHAR(20) NOT NULL, -- 예: 'PENDING', 'CONFIRMED', 'CANCELLED'
    created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (member_id) REFERENCES members (member_id),
    FOREIGN KEY (restaurant_id) REFERENCES restaurants (restaurant_id)
);

-- 5. Reviews 테이블
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

-- 6. Menus 테이블 (보완점 3: Audit 컬럼 추가)
CREATE TABLE menus
(
    menu_id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    restaurant_id BIGINT       NOT NULL,
    name          VARCHAR(100) NOT NULL,
    price         INT          NOT NULL,
    description   TEXT,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,                             -- [추가] 등록일
    updated_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- [추가] 수정일
    FOREIGN KEY (restaurant_id) REFERENCES restaurants (restaurant_id)
);

-- 7. Reservation_items 테이블 (보완점 1: ordered_price 추가 / 보완점 3: Audit 컬럼 추가)
CREATE TABLE reservation_items
(
    reservation_item_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    menu_id             BIGINT NOT NULL,
    reservation_id      BIGINT NOT NULL,
    quantity            INT    NOT NULL,
    ordered_price       INT    NOT NULL,                                                 -- [추가] 주문 당시의 메뉴 가격 스냅샷 (가장 중요)
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,                             -- [추가] 등록일
    updated_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- [추가] 수정일
    FOREIGN KEY (menu_id) REFERENCES menus (menu_id),
    FOREIGN KEY (reservation_id) REFERENCES reservations (reservation_id)
);

-- 8. Menu_Ingredients 테이블 (보완점 2: 대리키PK menu_ingredient_id 추가)
CREATE TABLE menu_ingredients
(
    menu_ingredient_id BIGINT AUTO_INCREMENT PRIMARY KEY,  -- [추가] JPA 매핑 편의를 위한 독립 PK
    menu_id            BIGINT NOT NULL,
    ingredient_id      BIGINT NOT NULL,
    FOREIGN KEY (menu_id) REFERENCES menus (menu_id),
    FOREIGN KEY (ingredient_id) REFERENCES ingredients (ingredient_id),
    UNIQUE KEY uq_menu_ingredient (menu_id, ingredient_id) -- 중복 연결 방지 제약조건
);