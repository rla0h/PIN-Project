K3S with Ubuntu os
==========
[참조링크](https://computingforgeeks.com/install-kubernetes-on-ubuntu-using-k3s/)
#  how to install k3s on ubuntu
## Spec
* OS
    - Ubuntu 20.04
* VMware
    - Vmware Workstation PRO
* Local OS
    - Ubuntu

### Step 1. Update Ubuntu System
```bash
$ sudo apt-get update
$ sudo apt -y upgrade && sudo systemctl reboot
```

### Step 2. Install Docker on Ubuntu 20.04
```bash
$ sudo apt-get update
$ sudo apt install apt-transport-https ca-certificates curl software-properties-common -y
$ curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -
$ sudo add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/ubuntu focal stable"

# Install Docker CE
$ sudo apt install docker-ce -y

# Check Docker system
$ sudo systemctl start docker
$ sudo systemctl enable docker

# add usermod
$ sudo usermod -aG docker ${USER}
$ newgrp docker
```

### Step 3. Setup the Master k3s Node
```bash
$ curl -sfL https://get.k3s.io | sh -s - --docker

# check docker system
$ systemctl status docker

# check k3s node
$ sudo kubectl get nodes -o wide
```

### Step 4. Allow ports on firewall
```bash
$ sudo ufw allow 6443/tcp
$ sudo ufw allow 443/tcp
```

### Step 5. Install k3s on worker nodes and connect them to the master
```bash
# Master
$ sudo cat /var/lib/rancher/k3s/server/node-token

# worker
$ curl -sfL http://get.k3s.io | K3S_URL=https://<master_IP>:6443 K3S_TOKEN=<join_token> sh -s - --docker
$ sudo systemctl status k3s-agent
```