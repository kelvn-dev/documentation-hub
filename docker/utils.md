# Docker command utils

Common docker commands, tips and tricks

## Command

- Compose up with specified .env and docker-compose file
  ```
  docker compose -f docker-compose.yml --env-file compose.env up
  ```

- Clean up existing deploy
  ```
  docker compose down --volumes
  ```

  ```
  docker compose -f docker-compose.yml down && docker volumes ls -qf dangling=true | xargs docker volume rm
  ```