RTI DDS ARM
===

### How to Get RTI DDS ARM version?
- Nas에 올려져있음
- PINLab > 2014~2022 > Pin_Lab > 연구개발_툴 > DDS > RTI > 6.1.1
- 아래 두개 파일을 받아 run파일로 RTI 설치 후 pkg를 추가 설치해주면 된다.
  - rti_connext_dds-6.1.1-lm-target-armv8Linux4gcc7.3.0.rtipkg
  - rti_connext_dds-6.1.1-lm-x64Linux4gcc7.3.0.run
- 관련 문서는 아래에 나와있다.
- [How do I create connext dds application rti code generator and build it my embedded target arm?](https://community.rti.com/kb/how-do-i-create-connext-dds-application-rti-code-generator-and-build-it-my-embedded-target-arm)
### RTI ARM Docker_file
```Dockerfile
FROM arm64v8/ubuntu:20.04

RUN apt-get update && apt-get -y install openjdk-17-jdk && apt-get install git -
y
WORKDIR /
# RUN git clone https://github.com/wolfcw/libfaketime.git
# WORKDIR /libfaketime/src
# RUN make install

# ENV LD_PRELOAD="/usr/local/lib/faketime/libfaketime.so.1"
# ENV FAKETIME_NO_CACHE=1
# ENV FAKETIME="2021-10-01 10:00:00"

WORKDIR /rti
COPY [".", "."]
ENV LD_LIBRARY_PATH "/rti/lib"
```

- java -ea -cp classes:$DDS_ROOT/lib/java/*:/home/pin/eclipse-workspace/NWT_RTI/bin:classes RecloserTopicPublisher
- java -ea -cp classes:/rti/lib/*:/rti/bin:classes RecloserTopicPublisher

- java -ea -classpath ".:/rti/rti_connext_dds-rpi/lib/java/nddsjava.jar" RecloserTopicPublisher
- java -ea -classpath ".:/rti/rti_connext_dds-rpi/lib/java/nddsjava.jar" RecloserTopicSubscriber 


RTI reference LINK
===
- DDS overview & RTPS
  - [참고링크](https://lab-notes.tistory.com/entry/DDS-DDS%EC%99%80-RTPS-%EA%B0%9C%EB%85%90%EC%A0%95%EB%A6%AC)
- RTI's default Protocol is RTPS
  - [참고링크](https://community.rti.com/static/documentation/connext-dds/5.2.0/doc/manuals/connext_dds/html_files/RTI_ConnextDDS_CoreLibraries_UsersManual/index.htm#UsersManual/Application_Discovery.htm#dcps_181524751_156833%3FTocPath%3DPart%25201%253A%2520Introduction%7CData-Centric%2520Publish-Subscribe%2520Communications%7C_____6)
- RTI error about : **Reliable large data requires asynchronous write**
  - [참고링크](https://community.rti.com/kb/what-error-reliable-large-data-requires-asynchronous-write)
- What is the maximum message size supported by RTI Connext 4.x and above?
  - [참고링크1](https://community.rti.com/kb/what-maximum-message-size-supported-rti-connext-4x-and-above)
  - [참고링크2](https://community.rti.com/static/documentation/connext-dds/5.2.0/doc/manuals/connext_dds/html_files/ RTI_ConnextDDS_CoreLibraries_UsersManual/Content/UsersManual/ASYNCHRONOUS_PUBL_Qos.htm#sending_2410472787_1293344)
- Discovery Traffic Summary
  - [참고링크](https://community.rti.com/static/documentation/connext-dds/5.2.0/doc/manuals/connext_dds/html_files/RTI_ConnextDDS_CoreLibraries_UsersManual/Content/UsersManual/Discovery_Traffic_Summary.htm)
- RTI + Wireshark
  - [참고링크](https://community.rti.com/static/documentation/wireshark/current/doc/wireshark_features.html)
- RTI QoS Strict.Reliable
  - [참고링크](https://community.rti.com/static/documentation/connext-dds/5.2.0/doc/manuals/connext_dds/html_files/RTI_ConnextDDS_CoreLibraries_UsersManual/Content/UsersManual/RELIABILITY_QosPolicy.htm)