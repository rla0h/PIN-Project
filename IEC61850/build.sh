#!/bin/sh
cli_dir=$(pwd)/sis_cli
ser_dir=$(pwd)/sis_ser
sudo apt-get install build-essential -y
sudo apt-get install dos2unix -y
sudo apt-get install g++-multilib libc6-dev-i386 -y
find . -type f -print0 | xargs -0 dos2unix
cd cmd/gnu_snap/
chmod +x ./init_device_a.sh ./init_device_b.sh mmslite_snap_thr.sh
sed -i "s|/usr/sisco/|$cli_dir/|g" -i init_device_a.sh
sed -i "s|/usr/sisco/|$ser_dir/|g" -i init_device_b.sh

cd ../../mvl/usr/scl_srvr/
sed -i "s|/usr/sisco|$ser_dir|g" -i scl_srvr.c
cd ../client/
sed -i "s|/usr/sisco|$cli_dir|g" -i client.c

cd ../../../cmd/gnu_snap/
./init_device_a.sh
./init_device_b.sh
CC_MODE=64 ./mmslite_snap_thr.sh LINUX