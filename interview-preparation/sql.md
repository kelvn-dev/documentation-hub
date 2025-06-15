# SQL

from/join
where
group by
having
select
distinc
order by
limit/offset

## Scenario Based Interview Questions

Given 2 tables like this:

users
| Column   | Type     | Description      |
| -------- | -------- | ---------------- |
| user_id  | INT (PK) | Primary key      |
| name     | VARCHAR  | Name of the user |

orders
| Column      | Type     | Description                     |
| ----------- | -------- | ------------------------------- |
| order_id    | INT (PK) | Primary key                     |
| user_id     | INT (FK) | Foreign key referencing `users` |
| order_date  | DATE     | Date of the order               |
| amount      | DECIMAL  | Order amount                    |

Propose index solution to improve this query:
select user.name, TOCHAR(order.order_date, yyyy-mm) as month, sum(order.amount) as total_amount 
from users user join orders order on user.user_id = order.user_id
group by TOCHAR(order.order_date, yyyy-mm), user.user_id
order by month desc

Sample results:

id, month, total_amount
kelvin, 2025-12, 10
kelvin, 2025-11, 9
alice, 2025-12, 10

we should add composite index of user_id and order_date for table orders, because:
- Join optimization: index user_id will speeds up the lookup of related orders for each user
- Grouping optimization: index order_date will group data more efficiently after filtering by user_id
- Index scan range: The composite index allows a range scan on user_id, then efficiently accesses order_date 

How to query with row number return
How to find duplicate row

## Index
Cơ chế hoạt động của hash index: khi lưu record, dùng hàm băm để tính giá trị băm của primary key sau đó lưu vào bảng băm. Bảng băm cho phép truy cập nhanh đến địa chỉ lưu trữ của bản ghi thực tế

## Other
bảng tạm bảng dc sinh ra để lưu kết quả tạm trong quá trình thực hiện 1 câu truy vấn

view là bảng ảo dùng để lưu kết quả câu truy vấn

diffence between DDL and DML: DDL (Data definition language) dùng để define cấu trúc của 1 cơ sở dữ liệu như các lệnh create update table, DML (Data manipulation language) dùng để thao tác dữ liệu như insert, update

diffence between union and join: union trả về 2 data set từ 2 table trong khi join trả về 1 data set gồm nhiều column từ nhiều table

diffence between union and union all: union detect duplicate bằng cách so sánh kiểu dữ liệu và content của toàn bộ column và chỉ return distinct trong khi union all trả về toàn bộ

diffence between where and having: having chỉ được dùng khi có group by, và có thể dùng với các aggregrate funtion như min, max, sum, avg, ...

all types of join

