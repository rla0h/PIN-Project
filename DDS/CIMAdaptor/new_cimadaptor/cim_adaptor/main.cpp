#include<stdio.h>
#include<stdlib.h>
#include<string.h>
#include<unistd.h>
#include<arpa/inet.h>
#include<sys/socket.h>

#include <cstdlib>
#include <iostream>

#include "cim.h"
#include "mapper.h"
#include "parser.h"

struct FEP {
  int protocol;
  int final;
  char varName[255];
  char value[255];
};

struct CIM {
  uint8_t cimName; ///0 : ACLineSegment, 1 : Switch
  char data[512];
};

int main() {
  struct FEP raw_data;
  struct CIM cimdata;
  int16_t setValueI;
  float setValueF;
  char *argv[3] = {"9999","127.0.0.1", "12345"};
  

  int str_len;
  /////FEP - CIM Mapper
  struct sockaddr_in serv_adr, clnt_adr;
  int serv_sock;
  socklen_t clnt_adr_sz;

  /////CIM Mapper - Publisher
	struct sockaddr_in pub_adr, from_adr;
  int sock;
	socklen_t adr_sz;


  
  sku::ACLineSegment* acls;
  sku::Switch* sw;
  sku::ScadaToCimMapper mapper;
  int g40v2_count = 0, g10v2_count = 0;

  acls = (sku::ACLineSegment *)malloc(sizeof(sku::ACLineSegment));
  sw = (sku::Switch *)malloc(sizeof(sku::Switch));

  auto len = acls->get_Length();
  auto r = acls->get_r();
  auto r0 = acls->get_r0();
  auto x = acls->get_x();
  auto x0 = acls->get_x0();

  
  clnt_adr_sz = sizeof(clnt_adr);




  //FEP로 부터 오는 데이터 처리할 UDP 서버 설정
  serv_sock = socket(PF_INET, SOCK_DGRAM, 0);
  if (serv_sock == -1) {
    printf("UDP socket creation error\n");
    exit(1);
  }

  memset(&serv_adr, 0, sizeof(serv_adr));
  serv_adr.sin_family = AF_INET;
  serv_adr.sin_addr.s_addr = htonl(INADDR_ANY);
  serv_adr.sin_port = htons(atoi(argv[0]));

  if (bind(serv_sock, (struct sockaddr*)&serv_adr, sizeof(serv_adr)) == -1) {
    perror("bind() error\n");
    exit(1);
  }


  //Publisher로 매핑 완료한 CIM 데이터를 보낼 UDP 클라이언트 설정
  sock = socket(PF_INET, SOCK_DGRAM, 0);
  if (sock == -1) {
    perror("socket() error");
    exit(1);
  }
  memset(&pub_adr, 0, sizeof(pub_adr));
  pub_adr.sin_family = AF_INET;
  pub_adr.sin_port = htons(atoi(argv[2]));

  if(inet_aton(argv[1], &pub_adr.sin_addr) == 0) {
    fprintf(stderr, "inet_aton() failed\n");
    exit(1);
  }

  memset(&from_adr, 0, sizeof(from_adr));
  from_adr.sin_family = AF_INET;
  from_adr.sin_addr.s_addr = htonl(INADDR_ANY);
  from_adr.sin_port = htons(0);

  char buf[512];

  while (1)
  {
    str_len = recvfrom(serv_sock, &raw_data, sizeof(raw_data), 0,(struct sockaddr*)&clnt_adr, &clnt_adr_sz);
    
    if(raw_data.protocol == 0) {
      //DNP Mapping
      if(strcmp(raw_data.varName,"g40v2") == 0) {
        if(g40v2_count == 0) {  
          len.unit_ = static_cast<sku::UnitSymbol>(atoi(raw_data.value));
        }
        else if(g40v2_count == 1){
          len.multiplier_ = (sku::UnitMultiplier)atoi(raw_data.value);
        }
        else if(g40v2_count == 2){
          len.value_ = atof(raw_data.value);
        }
        else if(g40v2_count == 3){
          r.unit_ = (sku::UnitSymbol)atoi(raw_data.value);
        }
        else if(g40v2_count == 4){
          r.multiplier_ = (sku::UnitMultiplier)atoi(raw_data.value);
        }
        else if(g40v2_count == 5){
          r.value_ = atof(raw_data.value);
        }
        else if(g40v2_count == 6){
          r0.unit_ = (sku::UnitSymbol)atoi(raw_data.value);
        }
        else if(g40v2_count == 7){
          r0.multiplier_ = (sku::UnitMultiplier)atoi(raw_data.value);
        }
        else if(g40v2_count == 8){
          r0.value_ = atof(raw_data.value);
        }
        else if(g40v2_count == 9){
        x.unit_ = (sku::UnitSymbol)atoi(raw_data.value);
        }
        else if(g40v2_count == 10){
          x.multiplier_ = (sku::UnitMultiplier)atoi(raw_data.value);
        }
        else if(g40v2_count == 11){
          x.value_ = atof(raw_data.value);
        }
        else if(g40v2_count == 12){
          x0.unit_ = (sku::UnitSymbol)atoi(raw_data.value);
        }
        else if(g40v2_count == 13){
          x0.multiplier_ = (sku::UnitMultiplier)atoi(raw_data.value);
        }
        else if(g40v2_count == 14) {
          x0.value_ = atof(raw_data.value);
          g40v2_count = -1;
          acls->set_Length(len);
          acls->set_r(r);
          acls->set_r0(r0);
          acls->set_x(x);
          acls->set_x0(x0);
          char buf1[512], buf2[512], buf3[512], buf4[512], buf5[512], buf6[512], buf7[512], buf8[512], buf9[512], buf10[512], buf11[512], buf12[512], buf13[512], buf14[512], buf15[512];
          sprintf(buf, "DNP");
          if(sendto(sock, buf, strlen(buf), 0,(struct sockaddr*)&pub_adr, sizeof(pub_adr))==-1){
              perror("sendto");
          }
          sprintf(buf1, "len_unit");
          if(sendto(sock, buf1, strlen(buf1), 0,(struct sockaddr*)&pub_adr, sizeof(pub_adr))==-1){
              perror("sendto");
          }
          sprintf(buf1, "%d", len.unit_);
          if(sendto(sock, buf1, strlen(buf1), 0,(struct sockaddr*)&pub_adr, sizeof(pub_adr))==-1){
              perror("sendto");
          }
          sprintf(buf2, "len_multi");
          if(sendto(sock, buf2, strlen(buf2), 0,(struct sockaddr*)&pub_adr, sizeof(pub_adr))==-1){
              perror("sendto");
          }
          sprintf(buf2, "%d", len.multiplier_);
          if(sendto(sock, buf2, strlen(buf2), 0,(struct sockaddr*)&pub_adr, sizeof(pub_adr))==-1){
              perror("sendto");
          }
          sprintf(buf3, "len_value");
          if(sendto(sock, buf3, strlen(buf3), 0,(struct sockaddr*)&pub_adr, sizeof(pub_adr))==-1){
              perror("sendto");
          }
          sprintf(buf3, "%f", len.value_);
          if(sendto(sock, buf3, strlen(buf3), 0,(struct sockaddr*)&pub_adr, sizeof(pub_adr))==-1){
              perror("sendto");
          }
          sprintf(buf4, "r_unit");
          if(sendto(sock, buf4, strlen(buf4), 0,(struct sockaddr*)&pub_adr, sizeof(pub_adr))==-1){
              perror("sendto");
          }
          sprintf(buf4, "%d", r.unit_);
          if(sendto(sock, buf4, strlen(buf4), 0,(struct sockaddr*)&pub_adr, sizeof(pub_adr))==-1){
              perror("sendto");
          }
          sprintf(buf5, "r_multi");
          if(sendto(sock, buf5, strlen(buf5), 0,(struct sockaddr*)&pub_adr, sizeof(pub_adr))==-1){
              perror("sendto");
          }
          sprintf(buf5, "%d", r.multiplier_);
          if(sendto(sock, buf5, strlen(buf5), 0,(struct sockaddr*)&pub_adr, sizeof(pub_adr))==-1){
              perror("sendto");
          }
          sprintf(buf6, "r_value");
          if(sendto(sock, buf6, strlen(buf6), 0,(struct sockaddr*)&pub_adr, sizeof(pub_adr))==-1){
              perror("sendto");
          }
          sprintf(buf6, "%f", r.value_);
          if(sendto(sock, buf6, strlen(buf6), 0,(struct sockaddr*)&pub_adr, sizeof(pub_adr))==-1){
              perror("sendto");
          }
          sprintf(buf7, "r0_unit");
          if(sendto(sock, buf7, strlen(buf7), 0,(struct sockaddr*)&pub_adr, sizeof(pub_adr))==-1){
              perror("sendto");
          }
          sprintf(buf7, "%d", r0.unit_);
          if(sendto(sock, buf7, strlen(buf7), 0,(struct sockaddr*)&pub_adr, sizeof(pub_adr))==-1){
              perror("sendto");
          }
          sprintf(buf8, "r0_multi");
          if(sendto(sock, buf8, strlen(buf8), 0,(struct sockaddr*)&pub_adr, sizeof(pub_adr))==-1){
              perror("sendto");
          }
          sprintf(buf8, "%d", r0.multiplier_);
          if(sendto(sock, buf8, strlen(buf8), 0,(struct sockaddr*)&pub_adr, sizeof(pub_adr))==-1){
              perror("sendto");
          }
          sprintf(buf9, "r0_value");
          if(sendto(sock, buf9, strlen(buf9), 0,(struct sockaddr*)&pub_adr, sizeof(pub_adr))==-1){
              perror("sendto");
          }
          sprintf(buf9, "%f", r0.value_);
          if(sendto(sock, buf9, strlen(buf9), 0,(struct sockaddr*)&pub_adr, sizeof(pub_adr))==-1){
              perror("sendto");
          }
          sprintf(buf10, "x_unit");
          if(sendto(sock, buf10, strlen(buf10), 0,(struct sockaddr*)&pub_adr, sizeof(pub_adr))==-1){
              perror("sendto");
          }
          sprintf(buf10, "%d", x.unit_);
          if(sendto(sock, buf10, strlen(buf10), 0,(struct sockaddr*)&pub_adr, sizeof(pub_adr))==-1){
              perror("sendto");
          }
          sprintf(buf11, "x_multi");
          if(sendto(sock, buf11, strlen(buf11), 0,(struct sockaddr*)&pub_adr, sizeof(pub_adr))==-1){
              perror("sendto");
          }
          sprintf(buf11, "%d", x.multiplier_);
          if(sendto(sock, buf11, strlen(buf11), 0,(struct sockaddr*)&pub_adr, sizeof(pub_adr))==-1){
              perror("sendto");
          }
          sprintf(buf12, "x_value");
          if(sendto(sock, buf12, strlen(buf12), 0,(struct sockaddr*)&pub_adr, sizeof(pub_adr))==-1){
              perror("sendto");
          }
          sprintf(buf12, "%f", x.value_);
          if(sendto(sock, buf12, strlen(buf12), 0,(struct sockaddr*)&pub_adr, sizeof(pub_adr))==-1){
              perror("sendto");
          }
          sprintf(buf13, "x0_unit");
          if(sendto(sock, buf13, strlen(buf13), 0,(struct sockaddr*)&pub_adr, sizeof(pub_adr))==-1){
              perror("sendto");
          }
          sprintf(buf13, "%d", x0.unit_);
          if(sendto(sock, buf13, strlen(buf13), 0,(struct sockaddr*)&pub_adr, sizeof(pub_adr))==-1){
              perror("sendto");
          }
          sprintf(buf14, "x0_multi");
          if(sendto(sock, buf14, strlen(buf14), 0,(struct sockaddr*)&pub_adr, sizeof(pub_adr))==-1){
              perror("sendto");
          }
          sprintf(buf14, "%d", x0.multiplier_);
          if(sendto(sock, buf14, strlen(buf14), 0,(struct sockaddr*)&pub_adr, sizeof(pub_adr))==-1){
              perror("sendto");
          }
          sprintf(buf15, "x0_value");
          if(sendto(sock, buf15, strlen(buf15), 0,(struct sockaddr*)&pub_adr, sizeof(pub_adr))==-1){
              perror("sendto");
          }
          sprintf(buf15, "%f", x0.value_);
          if(sendto(sock, buf15, strlen(buf15), 0,(struct sockaddr*)&pub_adr, sizeof(pub_adr))==-1){
              perror("sendto");
          }          
          cimdata.cimName = 0;
          memcpy(cimdata.data, (char *)acls, sizeof(sku::ACLineSegment));
          
          printf("ACLientSegemnt Mapping Completed : Sent to Publisher...\n");
        }
          
        g40v2_count++;
      } 
      else if(strcmp(raw_data.varName,"g10v2") == 0) {
        sw->set_normalOpen(atoi(raw_data.value));
        if(sw->get_normalOpen() == true) {
          sprintf(buf, "normalOpen");
          if(sendto(sock, buf, strlen(buf), 0,(struct sockaddr*)&pub_adr, sizeof(pub_adr))==-1){
              perror("sendto");
          }
          sprintf(buf, "true");
          if(sendto(sock, buf, strlen(buf), 0,(struct sockaddr*)&pub_adr, sizeof(pub_adr))==-1){
              perror("sendto");
          }
          sprintf(buf, "END");
          if(sendto(sock, buf, strlen(buf), 0,(struct sockaddr*)&pub_adr, sizeof(pub_adr))==-1){
              perror("sendto");
          }
        }
        cimdata.cimName = 1;
        memcpy(cimdata.data, (char *)sw, sizeof(sku::Switch));
        //sendto(sock, &cimdata, sizeof(cimdata), 0,(struct sockaddr*)&pub_adr, sizeof(pub_adr));
        printf("Switch Mapping Completed : Sent to Publisher...\n");
      }


    } else if(raw_data.protocol == 1) {
      //61850 Mapping
      sku::IEC61850_ObjnameParser obj_parser(raw_data.varName);
      sprintf(buf, "61850");
      if(sendto(sock, buf, strlen(buf), 0,(struct sockaddr*)&pub_adr, sizeof(pub_adr))==-1){
          perror("sendto");
      }
      if(obj_parser.get_ln() == "ZLIN1") {
        if(obj_parser.get_do() == "EEName") {
          if(obj_parser.get_da() == "name") {
            acls->set_name(raw_data.value);
            std::string name = acls->get_name();
            const char * name_a = name.c_str();
            sprintf(buf, "ZLIN1_name");
            if(sendto(sock, buf, strlen(buf), 0,(struct sockaddr*)&pub_adr, sizeof(pub_adr))==-1){
                perror("sendto");
            }
            sprintf(buf, name_a);
            if(sendto(sock, buf, strlen(buf), 0,(struct sockaddr*)&pub_adr, sizeof(pub_adr))==-1){
                perror("sendto");
            }
          }
          else if(obj_parser.get_da() == "mRID"){
            acls->set_mRID(raw_data.value);
            std::string mRID = acls->get_mRID();
            const char * mRID_a = mRID.c_str();
            sprintf(buf, "ZLIN1_mRID");
            if(sendto(sock, buf, strlen(buf), 0,(struct sockaddr*)&pub_adr, sizeof(pub_adr))==-1){
                perror("sendto");
            }
            sprintf(buf, mRID_a);
            if(sendto(sock, buf, strlen(buf), 0,(struct sockaddr*)&pub_adr, sizeof(pub_adr))==-1){
                perror("sendto");
            }
          }
        }
        else if (obj_parser.get_do() == "LinLenkm"){
          if(obj_parser.get_da() == "SIUnit"){
            len.unit_ = (sku::UnitSymbol)(atoi(raw_data.value));
            sprintf(buf, "LinLen_unit");
            if(sendto(sock, buf, strlen(buf), 0,(struct sockaddr*)&pub_adr, sizeof(pub_adr))==-1){
                perror("sendto");
            }
            sprintf(buf, "%d", len.unit_);
            if(sendto(sock, buf, strlen(buf), 0,(struct sockaddr*)&pub_adr, sizeof(pub_adr))==-1){
                perror("sendto");
            }
          }
          else if(obj_parser.get_da() == "multiplier"){
            len.multiplier_ = (sku::UnitMultiplier)(atoi(raw_data.value));
            sprintf(buf, "LinLen_multi");
            if(sendto(sock, buf, strlen(buf), 0,(struct sockaddr*)&pub_adr, sizeof(pub_adr))==-1){
                perror("sendto");
            }
            sprintf(buf, "%d", len.multiplier_);
            if(sendto(sock, buf, strlen(buf), 0,(struct sockaddr*)&pub_adr, sizeof(pub_adr))==-1){
                perror("sendto");
            }
          }
          else if(obj_parser.get_da() == "f"){
            len.value_ = atof(raw_data.value);
            sprintf(buf, "LinLen_f");
            if(sendto(sock, buf, strlen(buf), 0,(struct sockaddr*)&pub_adr, sizeof(pub_adr))==-1){
                perror("sendto");
            }
            sprintf(buf, "%f", len.value_);
            if(sendto(sock, buf, strlen(buf), 0,(struct sockaddr*)&pub_adr, sizeof(pub_adr))==-1){
                perror("sendto");
            }
          }
        }
        else if (obj_parser.get_do() == "RPs"){
          if(obj_parser.get_da() == "SIUnit"){
            r.unit_ = (sku::UnitSymbol)(atoi(raw_data.value));
            sprintf(buf, "RPs_unit");
            if(sendto(sock, buf, strlen(buf), 0,(struct sockaddr*)&pub_adr, sizeof(pub_adr))==-1){
                perror("sendto");
            }
            sprintf(buf, "%d", r.unit_);
            if(sendto(sock, buf, strlen(buf), 0,(struct sockaddr*)&pub_adr, sizeof(pub_adr))==-1){
                perror("sendto");
            }
          }
          else if(obj_parser.get_da() == "multiplier") {
            r.multiplier_ = (sku::UnitMultiplier)(atoi(raw_data.value));
            sprintf(buf, "RPs_multi");
            if(sendto(sock, buf, strlen(buf), 0,(struct sockaddr*)&pub_adr, sizeof(pub_adr))==-1){
                perror("sendto");
            }
            sprintf(buf, "%d", r.multiplier_);
            if(sendto(sock, buf, strlen(buf), 0,(struct sockaddr*)&pub_adr, sizeof(pub_adr))==-1){
                perror("sendto");
            }
          }
          else if(obj_parser.get_da() == "f") {
            r.value_ = atof(raw_data.value);
            sprintf(buf, "RPs_f");
            if(sendto(sock, buf, strlen(buf), 0,(struct sockaddr*)&pub_adr, sizeof(pub_adr))==-1){
                perror("sendto");
            }
            sprintf(buf, "%f", r.value_);
            if(sendto(sock, buf, strlen(buf), 0,(struct sockaddr*)&pub_adr, sizeof(pub_adr))==-1){
                perror("sendto");
            }
          }
        }

        else if (obj_parser.get_do() == "RZer"){
          if(obj_parser.get_da() == "SIUnit"){
            r0.unit_ = (sku::UnitSymbol)(atoi(raw_data.value));
            sprintf(buf, "RZer_unit");
            if(sendto(sock, buf, strlen(buf), 0,(struct sockaddr*)&pub_adr, sizeof(pub_adr))==-1){
                perror("sendto");
            }
            sprintf(buf, "%d", r0.unit_);
            if(sendto(sock, buf, strlen(buf), 0,(struct sockaddr*)&pub_adr, sizeof(pub_adr))==-1){
                perror("sendto");
            }
          }
          else if(obj_parser.get_da() == "multiplier"){
            r0.multiplier_ = (sku::UnitMultiplier)(atoi(raw_data.value));
            sprintf(buf, "RZer_multi");
            if(sendto(sock, buf, strlen(buf), 0,(struct sockaddr*)&pub_adr, sizeof(pub_adr))==-1){
                perror("sendto");
            }
            sprintf(buf, "%d", r0.multiplier_);
            if(sendto(sock, buf, strlen(buf), 0,(struct sockaddr*)&pub_adr, sizeof(pub_adr))==-1){
                perror("sendto");
            }
          }
          else if(obj_parser.get_da() == "f"){
            r0.value_ = atof(raw_data.value);
            sprintf(buf, "RZer_f");
            if(sendto(sock, buf, strlen(buf), 0,(struct sockaddr*)&pub_adr, sizeof(pub_adr))==-1){
                perror("sendto");
            }
            sprintf(buf, "%f", r0.value_);
            if(sendto(sock, buf, strlen(buf), 0,(struct sockaddr*)&pub_adr, sizeof(pub_adr))==-1){
                perror("sendto");
            }
          }
        }
        else if (obj_parser.get_do() == "XPs"){
          if(obj_parser.get_da() == "SIUnit"){
            x.unit_ = (sku::UnitSymbol)(atoi(raw_data.value));
            sprintf(buf, "XPs_unit");
            if(sendto(sock, buf, strlen(buf), 0,(struct sockaddr*)&pub_adr, sizeof(pub_adr))==-1){
                perror("sendto");
            }
            sprintf(buf, "%d", x.unit_);
            if(sendto(sock, buf, strlen(buf), 0,(struct sockaddr*)&pub_adr, sizeof(pub_adr))==-1){
                perror("sendto");
            }
          }
          else if(obj_parser.get_da() == "multiplier"){
            x.multiplier_ = (sku::UnitMultiplier)(atoi(raw_data.value));
            sprintf(buf, "XPs_multi");
            if(sendto(sock, buf, strlen(buf), 0,(struct sockaddr*)&pub_adr, sizeof(pub_adr))==-1){
                perror("sendto");
            }
            sprintf(buf, "%d", x.multiplier_);
            if(sendto(sock, buf, strlen(buf), 0,(struct sockaddr*)&pub_adr, sizeof(pub_adr))==-1){
                perror("sendto");
            }
          }
          else if(obj_parser.get_da() == "f"){
            x.value_ = atof(raw_data.value);
            sprintf(buf, "XPs_f");
            if(sendto(sock, buf, strlen(buf), 0,(struct sockaddr*)&pub_adr, sizeof(pub_adr))==-1){
                perror("sendto");
            }
            sprintf(buf, "%f", x.value_);
            if(sendto(sock, buf, strlen(buf), 0,(struct sockaddr*)&pub_adr, sizeof(pub_adr))==-1){
                perror("sendto");
            }
          }
        }

        else if (obj_parser.get_do() == "XZer"){
          if(obj_parser.get_da() == "SIUnit"){
            x0.unit_ = (sku::UnitSymbol)(atoi(raw_data.value));
            sprintf(buf, "XZer_unit");
            if(sendto(sock, buf, strlen(buf), 0,(struct sockaddr*)&pub_adr, sizeof(pub_adr))==-1){
                perror("sendto");
            }
            sprintf(buf, "%f", x0.unit_);
            if(sendto(sock, buf, strlen(buf), 0,(struct sockaddr*)&pub_adr, sizeof(pub_adr))==-1){
                perror("sendto");
            }
          }
          else if(obj_parser.get_da() == "multiplier"){
            x0.multiplier_ = (sku::UnitMultiplier)(atoi(raw_data.value));
            sprintf(buf, "XZer_multi");
            if(sendto(sock, buf, strlen(buf), 0,(struct sockaddr*)&pub_adr, sizeof(pub_adr))==-1){
                perror("sendto");
            }
            sprintf(buf, "%f", x0.multiplier_);
            if(sendto(sock, buf, strlen(buf), 0,(struct sockaddr*)&pub_adr, sizeof(pub_adr))==-1){
                perror("sendto");
            }
          }
          else if(obj_parser.get_da() == "f"){
            x0.value_ = atof(raw_data.value);
            sprintf(buf, "XZer_f");
            if(sendto(sock, buf, strlen(buf), 0,(struct sockaddr*)&pub_adr, sizeof(pub_adr))==-1){
                perror("sendto");
            }
            sprintf(buf, "%f", x0.value_);
            if(sendto(sock, buf, strlen(buf), 0,(struct sockaddr*)&pub_adr, sizeof(pub_adr))==-1){
                perror("sendto");
            }
          }
        }



        if(raw_data.final == 1){
          cimdata.cimName = 0;
          acls->set_Length(len);
          acls->set_r(r);
          acls->set_r0(r0);
          acls->set_x(x);
          acls->set_x0(x0);

          memcpy(cimdata.data, acls, sizeof(sku::ACLineSegment));
          //sendto(sock, &cimdata, sizeof(cimdata), 0,(struct sockaddr*)&pub_adr, sizeof(pub_adr));
          printf("ACLientSegemnt Mapping Completed : Sent to Publisher...\n");
        }
      } else if (obj_parser.get_ln()  == "XSWI1") {
        if(obj_parser.get_do() == "EEName") {
          if(obj_parser.get_da() == "name") {
            sw->set_name(raw_data.value);
            std::string name = sw->get_name();
            const char * name_a = name.c_str();
            sprintf(buf, "XSWI1_name");
            if(sendto(sock, buf, strlen(buf), 0,(struct sockaddr*)&pub_adr, sizeof(pub_adr))==-1){
                perror("sendto");
            }
            sprintf(buf, name_a);
            if(sendto(sock, buf, strlen(buf), 0,(struct sockaddr*)&pub_adr, sizeof(pub_adr))==-1){
                perror("sendto");
            }
          }
          else if(obj_parser.get_da() == "mRID"){
            sw->set_mRID(raw_data.value);
            std::string name = sw->get_mRID();
            const char * name_a = name.c_str();
            sprintf(buf, "XSWI1_mRID");
            if(sendto(sock, buf, strlen(buf), 0,(struct sockaddr*)&pub_adr, sizeof(pub_adr))==-1){
                perror("sendto");
            }
            sprintf(buf, name_a);
            if(sendto(sock, buf, strlen(buf), 0,(struct sockaddr*)&pub_adr, sizeof(pub_adr))==-1){
                perror("sendto");
            }
          }
        }
        else if(obj_parser.get_do() == "Pos"){
          if(obj_parser.get_da() == "stVal"){
            sprintf(buf, "Pos_normalOpen");
            if(sendto(sock, buf, strlen(buf), 0,(struct sockaddr*)&pub_adr, sizeof(pub_adr))==-1){
                perror("sendto");
            }
            sw->set_normalOpen(atoi(raw_data.value));
            if(sw->get_normalOpen() == true) {
              sprintf(buf, "true");
              if(sendto(sock, buf, strlen(buf), 0,(struct sockaddr*)&pub_adr, sizeof(pub_adr))==-1){
                  perror("sendto");
              }
              sprintf(buf, "END");
              if(sendto(sock, buf, strlen(buf), 0,(struct sockaddr*)&pub_adr, sizeof(pub_adr))==-1){
                  perror("sendto");
              }
            }
          }
        }

        if(raw_data.final == 1){
          cimdata.cimName = 1;
          
          memcpy(cimdata.data, sw, sizeof(sku::Switch));
          //sendto(sock, &cimdata, sizeof(cimdata), 0,(struct sockaddr*)&pub_adr, sizeof(pub_adr));
          printf("Switch Mapping Completed : Sent to Publisher...\n");
        }
      } else
        printf("invalid LN data\n");
    }
  }
    
  close(serv_sock);
  close(sock);
  
  return 0;
}
