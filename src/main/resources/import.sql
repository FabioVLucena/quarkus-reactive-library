insert into book (id, title, quantity) values (1, 'Adventure Craft', 20);
insert into category (id, description) values (1, 'Fantasy');
insert into book_category (id, book_id, category_id, register_date) values (1, 1, 1, current_timestamp);