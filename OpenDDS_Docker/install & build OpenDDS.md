OpenDDS with Ubuntu 20.04
=

# How to Install OpenDDS on Ubuntu 20.04
## [Reference Link](https://github.com/OpenDDS/OpenDDS/blob/branch-DDS-3.23/java/INSTALL)
## Spec & Prepare to Install
* Spec
  * OS
    * Ubuntu 20.04
  * JDK
    * jdk17
  * DB jar
    * PostgreSQL 42.5.4
  * IDE
    * Eclipse IDE
  * OpenDDS Version
    * OpenDDS 3.23.1
* Prepare
  * Perl
    * Already install on Ubuntu
    * If Not install using -> apt install perl
  * ACE TAO
    * If you install opendds, ace_tao compiler will be install too
  * Jave Install
    * jdk17 install
    * sudo apt-get install openjdk-17-jdk
  * GCC Install & G++ install
    * apt install build-essential -y

## Install
* Install OpenDDS 3.23.1  
  * Using curl
    ```bash
    $ curl -LJO https://github.com/OpenDDS/OpenDDS/archive/DDS-3.23.1.tar.gz
    $ tar -xzvf OpenDDS-DDS-3.23.1.tar.gz
    ```
  * Start
    ```bash
    cd $DDS_ROOT
       sudo ./configure --verbose --java --doc-group3 --features=java_pre_jpms=0
       # if ace error
       sudo ./configure --verbose --java --doc-group3 --features=java_pre_jpms=0 --ace=download
       # make
       sudo make
       # apply setenv
       source ./setenv.sh
    cd $DDS_ROOT/java/tests/multirepo
                ./run_test.pl
    ```
## DDS Communication

### Practice Example using Messenger_idl
* copy messenger_idl_test.jar, libmessenger_idl_test.so, libmessenger_idl_test.so.3.23.1 (Three files) to $DDS_ROOT/lib
* copy $DDS_ROOT/bin/repo.ior file to $DDS_ROOT/java/tests/messenger/publisher
* copy $DDS_ROOT/bin/repo.ior file to $DDS_ROOT/java/tests/messenger/subscriber
* Start to Repository 
  ```bash
  $DDS_ROOT/java/tests/messenger/publisher/DCPSInfoRepo -o repo.ior
  ```
* Start to Publisher
  ```bash
  $ $JAVA_HOME/bin/java -ea -cp classes:$DDS_ROOT/lib/*:classes -Djava.library.path=$DDS_ROOT/lib TestPublisher -DCPSConfigFile ../tcp.ini -w
  ```
* start to Subscriber
  ```bash
  $JAVA_HOME/bin/java -ea -cp classes:$DDS_ROOT/lib/*:classes -Djava.library.path=$DDS_ROOT/lib TestSubscriber -r
  ```
* Then, Success to Communicate with Pub & Sub
* ***repo.ior must exist in folder***
  

### IDL build
* NWT_IDL
    ```c
    /* NetworkTopology_RecloserTopic */
    module NWT{

        enum UnitSymbol {
            UnitSymbol_none,
            UnitSymbol_m
        };

        struct IdentifiedObject {
            string description;
            string mRID;
            string name;
            string aliasName;
        };

        struct Recloser {
            IdentifiedObject io;
            UnitSymbol us;
        };

        /* Topic */

        @topic
        struct RecloserTopic {
            Recloser r;
            @key
            long topicCount;
        };

    };
    ```
* Build IDL 
  * [Refernece Link](https://opendds.readthedocs.io/en/dds-3.25/devguide/java_bindings.html)
    ```bash
    $ACE_ROOT/bin/generate_export_file.pl NWT > NWT_Export.h
    ```
  * Create an MPC file, NWT.mpc:
    ```
    project: dcps_test_java {
        idlflags     += -Wb,stub_export_include=NWT_Export.h \
                        -Wb,stub_export_macro=NWT_Export
        dcps_ts_flags+= -Wb,export_macro=NWT_Export
        idl2jniflags += -Wb,stub_export_include=NWT_Export.h \
                        -Wb,stub_export_macro=NWT_Export
        dynamicflags += NWT_BUILD_DLL

        specific {
            jarname = NWT
        }

        TypeSupport_Files {
            NWT.idl
        }
    }
    ```
  * Run MPC to generate platform-specific build files.
    ```bash
    $ACE_ROOT/bin/mwc.pl -type gnuace
    ```
  * Compile the generated C++ and Java code
    ```bash
    make
    ```
  * Copy .so file about NWT idl to lib folder
  * Start like Messenger_IDL
* To Connection Another IP
  * Using DCPSInfoRepo
    ```bash
    # Start at one Computer (Pub or Sub)
    $ DCPSInfoRepo -ORBListenEndpoints iiop://:12345
    
    # Publisher
    $ java -ea -cp classes:/home/pin/lib/*:/home/pin/eclipse-workspace/NWT/bin:classes -Djava.library.path=$DDS_ROOT/lib NWT_TestPublisher -DCPSInfoRepo localhost:12345 -w

    # Subscriber
    $ java -ea -cp classes:/home/pin/lib/*:/home/pin/eclipse-workspace/NWT/bin:classes -Djava.library.path=$DDS_ROOT/lib NWT_TestSubscriber -DCPSInfoRepo <Publisher_IP>:12345 -r
    ```

# Here is Dockerfile
```md
FROM arm64v8/ubuntu:20.04

RUN apt-get update && apt-get upgrade -y

RUN apt-get install openjdk-17-jdk -y && apt-get install curl -y
RUN apt install build-essential -y
WORKDIR /DDS
RUN curl -LJO https://github.com/OpenDDS/OpenDDS/archive/DDS-3.23.1.tar.gz
RUN tar -xzvf OpenDDS-DDS-3.23.1.tar.gz
RUN rm OpenDDS-DDS-3.23.1.tar.gz
COPY [".", "."]
WORKDIR /OpenDDS-DDS-3.23.1
```