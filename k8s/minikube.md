# Minikube

A step-by-step guideline to expose pod using minikube with cloudflared

## Expose minikube server using cloudflare tunnel

https://github.com/STRRL/cloudflare-tunnel-ingress-controller?tab=readme-ov-file

## Troubleshoot

### Exposing

Minikube creates a virtual machine (VM) or container that runs Kubernetes.
That means the NodePorts (like 30080) are opened inside that VM — not directly on your local localhost
So http://localhost:30080 → ❌ doesn’t reach Minikube

minikube service meikocn-service

then use the opened url to setup cloudflared

If you’re on a cloud cluster (e.g., AKS, GKE, EKS), access via http://<node-public-ip>:30080

### Mounted data

all PersistentVolumeClaims (PVCs) are actually mounted inside Minikube’s VM, not directly on host filesystem.

To check where exactly your PVC is mounted:
k describe pvc db-pvc

Look for: Volume: pvc-64df6e81-f3b5-47d2-a4a4-4bfa420aa177

Then describe that volume:
k describe pv pvc-64df6e81-f3b5-47d2-a4a4-4bfa420aa177
Look for: 
Source:
  Type:          HostPath (bare host directory volume)
  Path:          /tmp/hostpath-provisioner/default/db-pvc

minikube ssh
switching to root inside the VM: sudo -i
cd /tmp/hostpath-provisioner/default/db-pvc
