Install K3S on Rasbian
===

[참조링크](https://medium.com/thinkport/how-to-build-a-raspberry-pi-kubernetes-cluster-with-k3s-76224788576c)

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


