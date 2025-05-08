# Cloudflare Tunnel

A step-by-step guideline for setting up macbook as a server using Cloudflare Tunnel

## What is a tunnel and how it works

A tunnel is a secure connection between your internet and your local host

![](images/cloudflare-tunnel.webp)

## Setup tunnel and expose local service

- Install cloudflared
  ```
  brew install cloudflared
  ```

- Authenticate cloudflared
  ```
  cloudflared login
  ```

- Create tunnel
  ```
  cloudflared tunnel create meiko-tunnel
  ```

- Config tunnel
  ```
  vi ~/.cloudflared/config.yml
  ```

  ```
  tunnel: <ID_TUNNEL>
  credentials-file: ~/.cloudflared/<ID_TUNNEL>.json
  ingress:
    - hostname: api.meikocn.com
      service: http://localhost:8080
      originRequest:
        noTLSVerify: true
    - hostname: ssh.meikocn.com
      service: ssh://localhost:22
      originRequest:
        noTLSVerify: true
    - hostname: "*"
      service: http_status:404
  ```

- Config DNS
  ```
  cloudflared tunnel route dns <ID_TUNNEL> api.meikocn.com
  ```

- Run tunnel
  ```
  cloudflared tunnel run meiko-tunnel
  ```

- Delete tunnel
  ```
  cloudflared tunnel delete meiko-tunnel
  ```

## CI/CD pipeline

- generate a pair of ssh keys
  ```
  ssh-keygen -t ed25519 -C "meiko-server" -f meiko-server-key
  ```

- add public key to ~/.ssh/authorized_keys (authorized_keys holds the list of public keys seperated by new line that are allowed to SSH in to the user account)

- add private key to action secret
