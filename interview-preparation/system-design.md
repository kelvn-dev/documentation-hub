# System design

## Design real-time leaderboard (bid car or similar things...) to show top 10 for 1M users
Challenge: aggregate data by userId and sum scores of each user

Combine websocket, rabbitmq, postgre and redis sorted set
- Bước 1: save bid data vào postgre db và publish 1 cái message vào queue để update tiền bid xe trong redis sorted set 
- Bước 2: Sau khi update redis sorted set xong thì tiếp tục publish message cho consumer của bên leaderboard để check leaderboard có thay đổi hay k, nếu có thì cache leaderboard sau khi dc thay đổi và broadcast thay đổi đó tới websocket (dùng STOMP). Ở bước này trước khi check sự thay đổi của leaderboard thì mình có implement 1 cơ chế throttle để quản lý tần số thay đổi leaderboard vì lỡ như 1 giây có 1000 request bid cùng lúc thì k lẽ mình để UI update 1000 lần trong 1s là k hợp lý. Mình check từ thời điểm gần nhất mà leaderboard dc update đến hiện tại phải cách ít nhất nửa giây thì mới check leaderboard có thay đổi hay k.
- Bước 3: sau khi broadcast thay đổi của leaderboard tới websocket thì FE nhận data từ socket rồi update lại UI thoi

## Distributed lock

Applications often run behind k8s clusters with multiple replicas, so multiple instances may attempt to perform operations on the same resource simultaneously

### Redis-based

Acquire lock: SET lock_key unique_value NX PX expiration_milliseconds

— NX: set key if not exist
— PX: expiration to prevent deadlocks

Ex: SET payment_lock ca4492e5-6756-4d62-9717-9631982f8b22 NX PX 30000

Release lock: first check if it is still the holder of the lock, then deletes the key using the DEL command

This simple redis lock rely on a single Redis master instance => use Redlock Algorithm:

when a client attempts to acquire the lock, each instance send the SET command and the lock is only considered acquired if client successfully obtains it from a majority of the Redis instances (N/2 + 1)

To simplify the implementation of the Redlock algorithm, use Redisson library

### Zookeper-based

acquire lock: create znode under lock path

release lock: delete znode

Think of the lock path as a lock id

If a client crashes, its znode is auto-deleted, releasing the lock

