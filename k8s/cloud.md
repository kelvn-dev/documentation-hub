# Digital Ocean

A step-by-step guideline demo gitops workflow using argocd and cloud cluster

## Setup manifest repo

Create a seperate repo to store manifest files such as k8s deployment, service definition files or helm charts

Setup webhook

- In Github

  - Settings -> Webhooks -> Add Webhook

  - Payload URL: https://argocd.meikocn.com/api/webhook (/api/webhook is required for proper handlers)

  - Content type: application/json

  - Which events would you like to trigger this webhook: Just the push event

## Setup Argo CD Server

Check current context and switch if need
```
k config current-context
k config use-context <CONTEXT_NAME>
```

deploy to Kubernetes cluster
```
k create namespace argocd
k apply -n argocd -f https://raw.githubusercontent.com/argoproj/argo-cd/stable/manifests/install.yaml
```

expose it as a LoadBalancer 
```
k patch svc argocd-server -n argocd -p '{"spec": {"type": "LoadBalancer"}}'
```

or use Cloudflare Tunnels as Ingress (https://blog.rullo.fr/use-cloudflare-tunnels-as-an-ingress-for-k8s-df21cefc4403)

get password
```
k -n argocd get secret argocd-initial-admin-secret -o jsonpath="{.data.password}" | base64 -d
```

config argocd application + sync this application to create pod, service, …

## Update pipeline script

Update jenkin or github action script to edit image's tag of meikocn-api pod, then open PR or commit directly to trigger auto-sync in argocd