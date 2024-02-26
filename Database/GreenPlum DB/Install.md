Greenplum Database Installation
---
- [Config](#config)
- [Install Greenplum DB dev](#install-greenplum-db-dev)
- [Modify hostname \& hostfile](#modify-hostname--hostfile)
- [Configure GreenPlum DB](#configure-greenplum-db)

---
# Config
* OS(Master, Seg1, Seg2)
  * Ubuntu 18.04 LTS


# Install Greenplum DB dev
* [Visit Github Site](https://github.com/greenplum-db/gpdb/releases)
* install .deb file on Ubuntu

# Modify hostname & hostfile
* master
```bash
sudo hostnamectl set-hostname master
sudo nano /etc/hosts

127.0.0.1       localhost
#127.0.1.1      pin <======== 주석 

master_ip master
seg1_ip seg1
seg2_ip seg2
```
* seg1
```bash
sudo hostnamectl set-hostname seg1
sudo nano /etc/hosts

#127.0.1.1 <========= 주석

seg1_ip seg1
seg2_ip seg2
```
* seg2
```bash
sudo hostnamectl set-hostname seg2
sudo nano /etc/hosts

#127.0.1.1 <========= 주석

seg1_ip seg1
seg2_ip seg2
```

# Configure GreenPlum DB
* deb 설치하면 자동으로 symlink 적용도 됨.
* GPDB path -> /usr/local/greenplum-db-6.26.3
* About Your Greenplum Database Installation
  * greenplum_path.sh — This file contains the environment variables for Greenplum Database. 
  * GPDB-LICENSE.txt — Greenplum license agreement.
  * bin — This directory contains the Greenplum Database management utilities. This directory also contains the PostgreSQL client and server programs, most of which are also used in Greenplum Database.
  * demo — This directory contains the Greenplum demonstration programs.
  * docs — The Greenplum Database documentation (PDF files).
  * etc — Sample configuration file for OpenSSL.
  * ext — Bundled programs (such as Python) used by some Greenplum Database utilities.
  * include — The C header files for Greenplum Database.
  * lib — Greenplum Database and PostgreSQL library files.
  * sbin — Supporting/Internal scripts and programs.
  * share — Shared files for Greenplum Database.
* Disable UFW
```bash
sudo ufw disable
```

* Create master, seg1, seg2 data folder
```bash
mkdir /home/pin/data/master
mkdir /home/pin/data1/
mkdir /home/pin/data2/
```
* Set Envrionment
```bash
source /usr/local/greenplum-db-6.26.3/greenplum_path.sh
export MASTER_DATA_DIRECTORY=/home/pin/data/master/gpseg-1
```
* Copy ssh-id
```bash
ssh-keygen -t rsa -b 4096
ssh-copy-id seg1
ssh-copy-id seg2
```
* Create configfile
```bash
cd /usr/local/greenplum-db-6.26.3/docs/cli_help/gpconfigs/
```
```bash
# create
nano all_hosts
master
seg1
seg2
```
```bash
# create
nano seg_hosts
seg1
seg2
```

```bash
# modify
nano gpinitsystem_config

declare -a DATA_DIRECTORY=(/home/pin/data1/primary /home/pin/data2/primary)

MASTER_HOSTNAME=master

MASTER_DIRECTORY=/home/pin/data/master

MIRROR_PORT_BASE=7000

declare -a MIRROR_DATA_DIRECTORY=(/home/pin/data1/mirror /home/pin/data2/mirror)

MACHINE_LIST_FILE=/usr/local/greenplum-db-6.26.3/docs/cli_help/gpconfigs/seg_hosts
```

```bash
# configuration
gpssh-exkeys -f all_hosts
gpssh -f seg_hosts -e 'mkdir -p /home/pin/data1/primary'
gpssh -f seg_hosts -e 'mkdir -p /home/pin/data1/mirror'
gpssh -f seg_hosts -e 'mkdir -p /home/pin/data2/primary'
gpssh -f seg_hosts -e 'mkdir -p /home/pin/data2/mirror'
gpinitsystem -c gpinitsystem_config 
```

```bash
# test
psql postgres
-------------------------
\l
\q

gpstate -e
psql postgres
------------------------
select * from gp_segment_configuration;
\q
```

* if you restart
```
gpstart
```