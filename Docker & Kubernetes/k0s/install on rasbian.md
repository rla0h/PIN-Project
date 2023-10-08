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