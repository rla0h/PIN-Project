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


### how to change Time?
- License를 사용하기 위해선 Cluster의 Time을 바꿔줘야 한다 License에 맞게..
- Pod가 적용 되어있는 Worker Node의 시간을 변경해주면됨
```bash
# Authority to change time
sudo timedatectl set-ntp false
# chage data like..
sudo date -s '2023-01-30 10:00:00'
```

- rti_arm pod 에서 make를 할때는 반드시 현재시간에 맞추고 해야한다.
- java -ea -classpath ".:/rti/rti_connext_dds-rpi/lib/java/nddsjava.jar" RecloserTopicPublisher -s 100
- java -ea -classpath ".:/rti/rti_connext_dds-rpi/lib/java/nddsjava.jar" RecloserTopicSubscriber -s 100