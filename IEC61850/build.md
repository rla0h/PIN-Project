# How to Build MMSlite
```
* USE two Ubuntu Desktop (Client, Server)
* Version : MMSlite_secure_6.4000
* Ubuntu 20.04 (Client, Server)
* OpenSSL version : OpenSSL 1.1.1f  31 Mar 2020
```
## bash script
[script file](./build.md)

## Error
* non openssl header
```bash
sudo apt-get purge openssl libssl-dev -y
sudo apt-get apt-get install openssl libssl-dev -y
openssl version
```
* non sys/~ header
```bash
sudo apt-get install g++-multilib libc6-dev-i386
```

## Client Server Execute
* Change Client IP
```bash
cd /MY_MMS_PATH/sis_cli/network/
siscostackcfg.xml -> (435, 456)
<IP_Address>SERVER_IP</IP_Address>
```
* Change Server IP
```bash
cd /MY_MMS_PATH/sis_ser/network/
siscostackcfg.xml -> (300)
<Local_SSL_Certificate Active="Yes">SISCO_2016_2048_SSL_B<Local_SSL_Certificate>
siscostackcfg.xml -> (435, 456)
<IP_Address>0.0.0.0</IP_Address> or <IP_Address>Client_IP</IP_Address>
```
* portforwarding and Firewalld 3782
```bash
sudo iptables -I INPUT 1 -p tcp --dport 3782 -j ACCEPT
sudo iptables -I OUTPUT 1 -p tcp --dport 3782 -j ACCEPT
```

* Execute
```bash
Server
cd /MMS_MY_PATH/mvl/usr/scl_srvr/
sudo sudo ./scl_srvr_snap_thr_ld

Client
cd /MMS_MY_PATH/mvl/usr/client/
sudo ./cositcps_snap_thr_ld -r DeviceB
```
