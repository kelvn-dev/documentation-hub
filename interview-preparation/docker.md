# Docker 

## Core

Docker là open source cho phép đóng gói ứng dụng và tất cả các dependency của nó, giúp ứng dụng có thể chạy một cách nhất quán trên nhiều môi trường khác nhau

## Components

- Docker Client: là cli để gửi các lệnh như docker build, docker pull đến Docker Daemon
- Docker Daemon: nhận các lệnh từ Docker client và thực hiện các tác vụ
- Docker Objects: Các đối tượng Docker bao gồm Docker images, containers, networks và volumes

Docker Engine is the runtime that builds and runs containers. It includes the Docker daemon, REST API, and CLI

Docker CLI can be on a remote system and connect to a remote Docker Engine using the -H option
docker -H=10.123.2.1:2375 run nginx

## Containers vs. Virtual Machines

A VM includes a full OS while container shares the host OS kernel. which consume less resource, and starts much faster. Containers provide process-level isolation rather than hardware-level

## CMD vs ENTRYPOINT

CMD provides default arguments

ENTRYPOINT sets the command that will always be executed

If you execute docker run with additional command, it will be appeded to ENTRYPOINT and override CMD

Dockerfile
FROM ubuntu
ENTRYPOINT ["/usr/bin/my-app"]
CMD ["start", "--config", "/etc/my-app.conf"]

docker run my-image stop --force

## COPY vs ADD

COPY is used to copy files or directories from build context into image

ADD can do the same plus:
- Automatically extract compressed tar archives from build context into image
- Download files from remote URLs (discouraged)

## How to optimize image size

- minimal base images (alpine)
- multi-stage builds by separating the build environment from the runtime environment
- .dockerignore