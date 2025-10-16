INSERT INTO items (name, normal_price, required_quantity, special_price)
VALUES ('A', 40, 3, 30),
       ('B', 10, 2, 7.5),
       ('C', 30, 4, 20),
       ('D', 25, 2, 23.5),
       ('E', 13, 3, 11.5),
       ('F', 8, 4, 6),
       ('G', 28, 3, 25),
       ('H', 100, 2, 92),
       ('I', 350, 3, 320),
       ('J', 4, 5, 2.8);

INSERT INTO bundle_discounts (first_item_id, second_item_id, discount_amount, active)
VALUES (1, 5, 5, true),
       (7, 9, 12, true),
       (4, 10, 3, true),
       (2, 8, 8, true),
       (3, 6, 4, true);