# OpenDDS Pub

#! /usr/bin/bash
while :
do
   echo "Starting OpenDDS Publisher (^C to quit)"
   java -ea -cp classes:/fep_OpenDDS/Fep_module/*:/fep_OpenDDS/lib/*:.:classes -Djava.library.path=/fep_OpenDDS/lib FEP_Publisher -DCPSConfigFile tcp.ini -r
   sleep 5
done

# OpenDDS Sub

#! /usr/bin/bash
while :
do
   echo "Starting OpenDDS Subscriber (^C to quit)"
   java -ea -cp classes:/fep_OpenDDS/Fep_module/*:/fep_OpenDDS/lib/*:.:classes -Djava.library.path=/fep_OpenDDS/lib FEP_Subscriber -DCPSConfigFile tcp.ini -r
   sleep 5
done

# RTI DDS Pub

#! /usr/bin/bash
while :
do
   echo "Starting RTI DDS Publisher (^C to quit)"
   LD_PRELOAD="/usr/local/lib/faketime/libfaketime.so.1" FAKETIME="-2y" make -f makefile_FEP_x64Linux4gcc7.3.0 FepTopicPublisher
   sleep 5
done

# RTI DDS Sub

#! /usr/bin/bash
while :
do
   echo "Starting RTI DDS Subscriber (^C to quit)"
   LD_PRELOAD="/usr/local/lib/faketime/libfaketime.so.1" FAKETIME="-2y" make -f makefile_FEP_x64Linux4gcc7.3.0 FepTopicSubscriber
   sleep 5
done

# CIMadaptor

#! /usr/bin/bash
while :
do
   echo "Starting CIM Adaptor (^C to quit)"
   ./cimadaptor
   sleep 5
done

# IEC 61850 Server
#! /usr/bin/bash
while :
do
   echo "Starting IEC 61850 Server (^C to quit)"
   ./scl_srvr_ld
   sleep 15
done

# IEC 61850 Client
#! /usr/bin/bash
while :
do
   echo "Starting IEC 61850 Client (^C to quit)"
   ./cositcps0_ld -m cim
   sleep 15
done

# DNPMaster
#! /usr/bin/bash
while :
do
   echo "Starting DNPMaster (^C to quit)"
   ./DNPMaster    
   sleep 3 
done

# DNPSlave
#! /usr/bin/bash
while :
do
   echo "Starting DNPSlave (^C to quit)"
   ./DNPSlave     
   sleep 3 
done