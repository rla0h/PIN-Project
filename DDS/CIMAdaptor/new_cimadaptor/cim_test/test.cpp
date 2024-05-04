#include<stdio.h>
#include<stdlib.h>
#include<string.h>
#include<unistd.h>
#include<arpa/inet.h>
#include<sys/socket.h>

#include <cstdlib>
#include <iostream>

#include "cim.h"



struct CIM {
    uint8_t cimName; ///0 : ACLineSegment, 1 : Switch
    char data[512];
};

int main() {  
    char *argv[] = {"12345"};

    /////test
    struct sockaddr_in serv_adr, clnt_adr;
    int serv_sock;
    int str_len;
    socklen_t clnt_adr_sz;
    clnt_adr_sz = sizeof(clnt_adr);


    struct CIM cimdata;
    sku::ACLineSegment acls;
    sku::Switch sw;


    auto len = acls.get_Length();
    auto r = acls.get_r();
    auto r0 = acls.get_r0();
    auto x = acls.get_x();
    auto x0 = acls.get_x0();
    auto normal_open = sw.get_normalOpen();




    //test UDP server
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
        printf("bind() error\n");
        exit(1);
    }

    while (1)
    {
        str_len = recvfrom(serv_sock, &cimdata, sizeof(cimdata), 0,(struct sockaddr*)&clnt_adr, &clnt_adr_sz);
        
        if(cimdata.cimName == 0) {
            memcpy(&acls, cimdata.data, sizeof(cimdata.data));
            len = acls.get_Length();
            r = acls.get_r();
            r0 = acls.get_r0();
            x = acls.get_x();
            x0 = acls.get_x0();

            printf("==========ACLinet Segment==========\n");
            printf("len : %d, %d, %.2f\n" , (int)len.unit_, (int)len.multiplier_, len.value_);
            printf("  r : %d, %d, %.2f\n" , (int)r.unit_, (int)r.multiplier_, r.value_);
            printf(" r0 : %d, %d, %.2f\n" , r0.unit_, r0.multiplier_, r0.value_);
            printf("  x : %d, %d, %.2f\n" , x.unit_, x.multiplier_, x.value_);
            printf(" x0 : %d, %d, %.2f\n\n" , x0.unit_, x0.multiplier_, x0.value_);


        } else if(cimdata.cimName == 1) {
            memcpy(&sw, cimdata.data, sizeof(sw));
            normal_open = sw.get_normalOpen();

            printf("========Switch========\n");
            printf("normalOpen : %d\n\n", normal_open);
        }

    }  
    
    
  close(serv_sock);
  
  return 0;
}
