# System design

## Auth0
full flow authentication là user login success thì auth0 là issuer sẽ dựa trên thuật toán bất đối xứng RS256 dùng private key để gen token và trả về, FE dùng token này để request data từ SpringBoot server through restapi, lúc này SpringBoot server là 1 resource server và sẽ verify token bằng cách đi fetch public key từ endpoint JWK (Json web key) Set URI do auth0 cung cấp. Ngoài ra server còn có thể implement thêm các bước validation như issuer, aud, exp, ...

Use case nếu social login ở app B r dùng token để call api ở app A ?
Flow social login là:
- auth0 redirect tới identity provider login page, vd như gg
- user login success thì gg sẽ trả về authorization code cho auth0
- auth0 dùng authorization code để request access token hoặc optionally refresh token và lưu lại
- auth0 tiếp tục dùng access token này để call api gg lấy user detail, sau đó tự gen token và trả về
Vậy dùng social login thì token dc trả về vẫn được kí bằng auth0 private key nên token từ app khác k verify được. Còn nếu 2 app dùng cùng 1 auth0 tenant thì validate field aud sẽ chứa clientId của app B chứ k phải app A.

## Docker
Docker là open source cho phép đóng gói ứng dụng và tất cả các dependency của nó, giúp ứng dụng có thể chạy một cách nhất quán trên nhiều môi trường khác nhau
- Docker Client: là cli để gửi các lệnh như docker build, docker pull, docker run đến Docker Daemon
- Docker Daemon: nhận các lệnh từ Docker client và thực hiện các tác vụ
- Docker registry: là nơi lưu trữ các Docker image. Docker Hub là một registry công cộng phổ biến
- Docker Objects: Các đối tượng Docker bao gồm Docker images, containers, networks và volumes
- Docker image: chứa tất cả các thành phần cần thiết để chạy một ứng dụng, được xây dựng từ một file cấu hình gọi là Dockerfile
- Docker containers: bao gồm các instance đang chạy của Docker images

## Kubernete
Là 1 open source orchestration giúp tự động hóa deployment, scaling và quản lý các ứng dụng đã dc container hóa. Gồm 2 loại node:
  - Master Node là trái tim, quản lý toàn bộ cluster.
  - Các Worker Node là các machines để chạy các Containers (Pods)

Minikube dùng để setup 1 k8s cluster dưới máy local => k cần cloud

## Design real-time leaderboard (bid car or similar things...) to show top 10 for 1M users
Challenge: aggregate data by userId and sum scores of each user

Combine websocket, rabbitmq, postgre and redis sorted set
- Bước 1: save bid data vào postgre db và publish 1 cái message vào queue để update tiền bid xe trong redis sorted set 
- Bước 2: Sau khi update redis sorted set xong thì tiếp tục publish message cho consumer của bên leaderboard để check leaderboard có thay đổi hay k, nếu có thì cache leaderboard sau khi dc thay đổi và broadcast thay đổi đó tới websocket (dùng STOMP). Ở bước này trước khi check sự thay đổi của leaderboard thì mình có implement 1 cơ chế throttle để quản lý tần số thay đổi leaderboard vì lỡ như 1 giây có 1000 request bid cùng lúc thì k lẽ mình để UI update 1000 lần trong 1s là k hợp lý. Mình check từ thời điểm gần nhất mà leaderboard dc update đến hiện tại phải cách ít nhất nửa giây thì mới check leaderboard có thay đổi hay k.
- Bước 3: sau khi broadcast thay đổi của leaderboard tới websocket thì FE nhận data từ socket rồi update lại UI thoi