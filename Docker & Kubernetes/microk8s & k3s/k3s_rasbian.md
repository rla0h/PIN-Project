Install K3S on Rasbian
===

[참조링크](https://medium.com/thinkport/how-to-build-a-raspberry-pi-kubernetes-cluster-with-k3s-76224788576c)
- ***Warning***
  - **라즈비안은 ARM 계열의 OS 이기 때문에 후에 Docker Image를 받을때에도 Arm 기반의 OS가 들어간 Docker Image를 받아야합니다.**
  - Docker File 에서 Arm 기반의 OS를 받을 때에도 Host OS 도 ARM 기반의 OS 이어야 합니다.
  - Windows 11 Vmware 에서는 ARM 기반의 OS를 올리지 못한다.
  - **즉 라즈베리에 Docker Image를 받겠다? -> ARM 기반의 OS에서 도커를 설치하고 도커파일에 ARM 기반의 OS를 받고 다시 이미지로 푸쉬한후 라즈베리에서 불러와 사용**
# Install K3S

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
# Install k3s
$ sudo curl –sfL https://get.k3s.io | sh -
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
$ sudo curl –sfL https://get.k3s.io | sh -

# Stop K3S service
$ sudo systemctl stop k3s
# Start Add Worker Node to Master
$ sudo k3s agent --server https://<ip_address>:<port> --token <master_token> --node-name <NODE_NAME>

# Checkt get nodes on Master
$ sudo kubectl get nodes
# If wokernode's role is none, You shoudl add role using this
$ sudo kubectl label nodes <Node_Name> kubernetes.io/role=<Role_Name>
```


