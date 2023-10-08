Install K3S on Rasbian
===

[참조링크](https://medium.com/thinkport/how-to-build-a-raspberry-pi-kubernetes-cluster-with-k3s-76224788576c)
- ***Warning***
  - **라즈비안은 ARM 계열의 OS 이기 때문에 후에 Docker Image를 받을때에도 Arm 기반의 OS가 들어간 Docker Image를 받아야합니다.**
  - Docker File 에서 Arm 기반의 OS를 받을 때에도 Host OS 도 ARM 기반의 OS 이어야 합니다.
  - Windows 11 Vmware 에서는 ARM 기반의 OS를 올리지 못한다.
  - **즉 라즈베리에 Docker Image를 받겠다? -> ARM 기반의 OS에서 도커를 설치하고 도커파일에 ARM 기반의 OS를 받고 다시 이미지로 푸쉬한후 라즈베리에서 불러와 사용**
# Install K3S

[참고사이트](https://docs.tigera.io/calico/latest/getting-started/kubernetes/k3s/multi-node-install)

## Master
```bash
$ sudo apt update
$ sudo nano /boot/cmdline.txt
# add this
cgroup_enable=cpuset cgroup_memory=1 cgroup_enable=memory

$ sudo dphys-swapfile swapoff
$ sudo dphys-swapfile uninstall
$ sudo update-rc.d dphys-swapfile remove
$ sudo reboot

$ sudo nano /etc/resolv.conf
add NAMESERVER 8.8.8.8
add NAMESERVER 8.8.4.4

# Install k3s
# $ sudo curl –sfL https://get.k3s.io | sh -
# $ curl -sfL https://get.k3s.io | K3S_KUBECONFIG_MODE="644" INSTALL_K3S_EXEC="--flannel-backend=none --cluster-cidr=192.168.0.0/16 --disable-network-policy --disable=traefik" sh -

$ sudo curl -sfL https://get.k3s.io | INSTALL_K3S_EXEC=" --flannel-backend=none --disable-network-policy --cluster-cidr=192.168.0.0/16" sh -

$ ufw allow 6443/tcp
$ ufw allow 6443/udp
# and add worker node ..
# check the node
$ sudo kubectl get node

# check the token
$ sudo cat /var/lib/rancher/k3s/server/node-token
```

## Worker
```bash
$ sudo apt update
$ sudo nano /boot/cmdline.txt
# add this
cgroup_enable=cpuset cgroup_memory=1 cgroup_enable=memory

$ sudo dphys-swapfile swapoff
$ sudo dphys-swapfile uninstall
$ sudo update-rc.d dphys-swapfile remove
$ sudo reboot

# Install k3s
# $ sudo curl –sfL https://get.k3s.io | sh -

$ curl -sfL https://get.k3s.io | K3S_URL=https://<SERVER_IP>:6443 K3S_TOKEN=<TOKEN> K3S_NODE_NAME=<NODE_NAME> sh -

# Checkt get nodes on Master
$ sudo kubectl get nodes

## Instatll CNI plugins
### Calico
```bash
$ sudo kubectl apply -f https://raw.githubusercontent.com/projectcalico/calico/v3.26.1/manifests/calico.yaml
# check the Installation
$ watch kubectl get pods --all-namespaces

# if core_dns, calico ..etc all run you can update pods
# and testing using netcat
$ apt install netcat
# server
$ nc -l <port>
# client
$ nc <Server_IP> <port>
```

### add hosts name
```bash
# 서로 netcat 통신, cni 통신 등 Pod 끼리의 통신은 확인 되었지만 어째선지, DCPSInfoRepo에 찍먹(?)만 하고 바로 TCPConnection:Close 가 발생하였다. 알고보니 서로의 HOST Name(?)을 몰라서 일어나게 된 현상이었따.

$ vim /etc/hosts
# ADD all Pods Namespace LIKE this
# add..
172.16.235.135  repo-pod
172.16.189.71   opendds-sub-0
172.16.235.134  opendds-pub-0

# 서로의 POD 를 각 Pod (Pub, sub, repo)에다 추가해준 뒤 통신해보니 잘되었따.
```