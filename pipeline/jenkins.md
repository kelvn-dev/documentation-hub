# Jenkins pipeline

A step-by-step guideline for setting ci/cd pipeline jenkins

## Expose jenkins server using cloudflare tunnel

- Update tunnel config
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
    - hostname: jenkins.meikocn.com
      service: http://localhost:8090
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
  cloudflared tunnel route dns <ID_TUNNEL> jenkins.meikocn.com
  ```

- Run tunnel
  ```
  cloudflared tunnel run meiko-tunnel
  ```

## Config CI/CD pipeline 

- In jenkins UI

  - Create github credential for private repo: Manage Jenkins -> Credentials -> global -> add type Username with password

  - Create New Item of type Multibranch Pipeline

  - At Branch Sources section: Add source -> Github -> Input Repository HTTPS URL

  - At Build Configuration section: Mode -> by Jenkinsfile / Script Path -> Jenkinsfile

- In Github

  - Settings -> Webhooks -> Add Webhook

  - Payload URL: https://jenkins.meikocn.com/github-webhook/ (/github-webhook/ is required for proper handlers)

  - Content type: application/json

  - Which events would you like to trigger this webhook: Just the push event

- In source code

  - Create Jenkinsfile at project root

## Bonus

- In the context of jenkins installed through brew, to make aws available, need to Add Homebrew bin path to Jenkins global environment

  - Manage Jenkins → System

  - Scroll to Global properties → Environment variables

  - Check “Environment variables” and add: PATH | /opt/homebrew/bin:/usr/local/bin:/usr/bin:/bin:/usr/sbin:/sbin