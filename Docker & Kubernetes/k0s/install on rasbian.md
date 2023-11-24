Install k0s on Rasbian
===

[참조링크](https://medium.com/thinkport/how-to-build-a-raspberry-pi-kubernetes-cluster-with-k3s-76224788576c)
- ***Warning***
  - **라즈비안은 ARM 계열의 OS 이기 때문에 후에 Docker Image를 받을때에도 Arm 기반의 OS가 들어간 Docker Image를 받아야합니다.**
  - Docker File 에서 Arm 기반의 OS를 받을 때에도 Host OS 도 ARM 기반의 OS 이어야 합니다.
  - Windows 11 Vmware 에서는 ARM 기반의 OS를 올리지 못한다.
  - **즉 라즈베리에 Docker Image를 받겠다? -> ARM 기반의 OS에서 도커를 설치하고 도커파일에 ARM 기반의 OS를 받고 다시 이미지로 푸쉬한후 라즈베리에서 불러와 사용**

# Before install
[install kubectl](https://kubernetes.io/docs/tasks/tools/install-kubectl-linux/)
    
# Install k0s
[Official Link](https://docs.k0sproject.io/v1.23.6+k0s.2/raspberry-pi4/)
[Calico set LINK 1](https://www.mirantis.com/blog/how-to-set-up-k0s-kubernetes-a-quick-and-dirty-guide/)
[Calico set LINK 2](https://docs.k0sproject.io/v1.20.6+k0s.0/configuration/)
```bash
sudo k0s kubectl apply -f https://docs.projectcalico.org/manifests/calico.yaml
```

# when you start new terminal
* command export KUBECONFIG=$(pwd)/config.yaml

## k0s master
* Why doesn't kubectl get nodes list the k0s controllers?
  * As a default, the control plane does not run kubelet at all, and will not accept any workloads, so the controller will not show up on the node list in kubectl. If you want your controller to accept workloads and run pods, you do so with: **k0s controller --enable-worker** (recommended only as test/dev/POC environments).
* 기본적으로 k0s controller가 get node 했을 때 안보임. k0s controller --enable-worker를 해주면 서버와 같이 controller에 관한 로그들이 쫘라락 뜨게됨
  * 대신 k0s가 시작된 상태라면 k0s stop을 해주고 실행
  * 다른 k0s master node 터미널에서 get node를 한다면 master worker1 worker2 출력이 될것임

## k0s install using k0sctl <-- calico can use
* [Reference Link](https://ko.linux-console.net/?p=20569)
* Calico setup -> [Link](https://kengz.gitbook.io/blog/setting-up-a-private-kubernetes-cluster-with-k0sctl)

## create SSH keygen (only root user)
* master node
```bash 
$ sudo su
$ ssh-keygen
$ ssh-copy-id root@(MASTER_NODE hostname)
$ ssh-copy-id root@(WORKER_NODE1 hostname)
$ ssh-copy-id root@(WORKER_NODE2 hostname)
# create k0sctl.yaml file <-- default cni : kube-router
$ k0sctl-linux-arm64 init > k0sctl.yaml
# or using "My file" <-- can use calico
# start k0sctl
$ k0sctl-linux-arm64 apply --config k0sctl.yaml
```

* Worker node
```bash
# root 계정에 password 설정 해줘야함
$ passwd
```