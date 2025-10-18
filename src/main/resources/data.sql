INSERT INTO item (name, normal_price, required_quantity, special_price)
VALUES ('Apple', 40, 3, 30),
       ('Banana', 10, 2, 7.5),
       ('Orange', 30, 4, 20),
       ('Strawberry', 25, 2, 23.5),
       ('Pear', 13, 3, 11.5),
       ('Grape', 8, 4, 6),
       ('Watermelon', 28, 3, 25),
       ('Pineapple', 100, 2, 92),
       ('Cherry', 350, 3, 320),
       ('Mango', 4, 5, 2.8);

INSERT INTO bundle_discount (first_item_id, second_item_id, discount_amount)
VALUES (1, 5, 5),
       (7, 9, 12),
       (4, 10, 3),
       (2, 8, 8),
       (3, 6, 4);