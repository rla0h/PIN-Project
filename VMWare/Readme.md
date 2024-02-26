Using VMWare ESXI

-----
# 원격 (xrdp) 설정... 20.04 기준
```bash
sudo apt-get update && sudo apt-get upgrade -y
sudo ufw allow 3389
sudo apt-get install xrdp -y
sudo adduser xrdp ssl-cert
sudo service xrdp restart
sudo nano /etc/xrdp/startwm.sh
-> 밑에서 3번째줄 fi 밑에
unset DBUS_SESSION_BUS_ADDRESS
unset XDG_RUNTIME_DIR

sudo service xrdp restart
sudo apt-get install xfce4 -y
sudo reboot
```