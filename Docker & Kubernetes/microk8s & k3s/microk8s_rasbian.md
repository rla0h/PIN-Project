
MicroK8S (with Rasbian os)
==========
- ***Warning***
  - **라즈비안은 ARM 계열의 OS 이기 때문에 후에 Docker Image를 받을때에도 Arm 기반의 OS가 들어간 Docker Image를 받아야합니다.**
  - Docker File 에서 Arm 기반의 OS를 받을 때에도 Host OS 도 ARM 기반의 OS 이어야 합니다.
  - Windows 11 Vmware 에서는 ARM 기반의 OS를 올리지 못한다.
  - **즉 라즈베리에 Docker Image를 받겠다? -> ARM 기반의 OS에서 도커를 설치하고 도커파일에 ARM 기반의 OS를 받고 다시 이미지로 푸쉬한후 라즈베리에서 불러와 사용**
## How to Install 
```bash
$ sudo nano /boot/cmdline.txt

# add cgroup (infront of txt file)
cgroup_enable=memory cgroup_memory=1

$ sudo reboot

# install microk8s
$ sudo snap install microk8s --classic --channel=1.25

# setting iptable
$ sudo iptables-legacy -F && sudo iptables-legacy -t nat -F && sudo iptables-legacy -t mangle -F &&  sudo iptables-legacy -X
$ sudo update-alternatives --set iptables /usr/sbin/iptables-legacy
$ sudo update-alternatives --set ip6tables /usr/sbin/ip6tables-legacy

# after join cluster
# if you have error try this
$ sudo cp -f /var/snap/microk8s/current/credentials/client.config /.kube/


# Join the group
$ sudo usermod -a -G microk8s $USER
$ sudo chown -f -R $USER ~/.kube

$ su - $USER

# Check the status
$ microk8s status --wait-ready

# Access Kubernetes
$ microk8s kubectl get nodes
$ microk8s kubectl get services
$ alias kubectl='microk8s kubectl'


# then try join cluster
```


[metrics-server github site](https://github.com/kubernetes-sigs/metrics-server)
```bash
# try "kubectl top nodes"

#1 error: Metrics API not availabe

$ kubectl apply -f https://github.com/kubernetes-sigs/metrics-server/releases/latest/download/high-availability-1.21+.yaml

#2 error: Error from server (ServiceUnavailable): the server is currently unable to handle the request (get nodes.metrics.k8s.io)

# edit metrics-server.yaml
$ kubectl edit deploy -n kube-system metrics-server

# change version
--v0.5.0

# add spec.template.spec.containers -args:
--kubelet-insecure-tls
--kubelet-preferred-address-types=InternalIP

# add spec.template.spec.containers:
hostNetwork: true
```

* 노드 두개 각각에서 kubectl top node를 해서 확인이 가능할때 add-node를 통해 멀티노드를 만들어준다

```bash
# add host name about workers
```
### **change worker node's hostname (default = raspberrypi) to worker1 and worker2**
```bash
$ sudo nano /etc/hosts
<worker1_IP>  worker1
<Worker2_IP>  worker2
# add node at master
$ microk8s add-node
# 이렇게 하면 토큰 IP 며 join하라는 명령어가 출력 그걸 worker node에 복사

# worker
$ join ~

# check top node
$ kubectl top nodes
```
