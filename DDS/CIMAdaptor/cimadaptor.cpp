#include <iostream>
#include <cstring>
#include <vector> 
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <unistd.h>
#include <sstream>
//#include "cim.h"
#include "data_control.h"

#define PORT_NUM 9999
#define MAXLEN 256

using namespace std;
namespace sku
{

    enum class UnitSymbol : int
    {
        UnitSymbol_none = 0,
        UnitSymbol_m = 2,
        UnitSymbol_s = 4,
        UnitSymbol_A = 5,
        UnitSymbol_deg = 9,
        UnitSymbol_rad = 10,
        UnitSymbol_degC = 23,
        UnitSymbol_F = 25,
        UnitSymbol_C = 26,
        UnitSymbol_H = 28,
        UnitSymbol_V = 29,
        UnitSymbol_ohm = 30,
        UnitSymbol_Hz = 33,
        UnitSymbol_W = 38,
        UnitSymbol_VA = 61,
        UnitSymbol_VAr = 63,
        UnitSymbol_cosPhi = 65,
        UnitSymbol_Vs = 66,
        UnitSymbol_V2 = 67,
        UnitSymbol_As = 68,
        UnitSymbol_A2 = 69,
        UnitSymbol_A2s = 70,
        UnitSymbol_VAh = 71,
        UnitSymbol_Wh = 72,
        UnitSymbol_VArh = 73,
        UnitSymbol_h = 84,
        UnitSymbol_min = 85,
        UnitSymbol_Q = 100,
        UnitSymbol_Qh = 101,
        UnitSymbol_V2h = 104,
        UnitSymbol_A2h = 105,
        UnitSymbol_Ah = 106,
        UnitSymbol_count = 111,
        UnitSymbol_sPers = 149,
        UnitSymbol_HzPerHz = 150,
        UnitSymbol_VPerV = 151,
        UnitSymbol_APerA = 152,
        UnitSymbol_VPerVA = 153
    };
    enum class UnitMultiplier : int
    {
        UnitMultiplier_y = -24,
        UnitMultiplier_z = -21,
        UnitMultiplier_a = -18,
        UnitMultiplier_f = -15,
        UnitMultiplier_p = -12,
        UnitMultiplier_n = -9,
        UnitMultiplier_micro = -6,
        UnitMultiplier_m = -3,
        UnitMultiplier_c = -2,
        UnitMultiplier_d = -1,
        UnitMultiplier_none = 0,
        UnitMultiplier_da = 1,
        UnitMultiplier_h = 2,
        UnitMultiplier_k = 3,
        UnitMultiplier_M = 6,
        UnitMultiplier_G = 9,
        UnitMultiplier_T = 12,
        UnitMultiplier_P = 15,
        UnitMultiplier_E = 18,
        UnitMultiplier_Z = 21,
        UnitMultiplier_Y = 24
    };
};
int Common_input_cnt = 0;
class Common_D {
public:
    string name;
    string mrid;
    int unitSymbol;
    int unitMultiplier;
    int value;
    float f_value;
    bool dnp_normalOpen_ = false;
    bool iec_normalOpen_ = false;
    void set_properties(int data) {
        if (data == (int)sku::UnitSymbol::UnitSymbol_ohm) {
            this->unitSymbol = data;
            Common_input_cnt++;
        }
        else if (data == (int)sku::UnitMultiplier::UnitMultiplier_z) {
            this->unitMultiplier = data;
            Common_input_cnt++;
        }
        else if (data == (int)sku::UnitMultiplier::UnitMultiplier_M) {
            this->unitMultiplier = data;
            Common_input_cnt++;
        }
        else if (data == (int)sku::UnitMultiplier::UnitMultiplier_m) {
            this->unitMultiplier = data;
            Common_input_cnt++;
        }
        else if (data == (int)sku::UnitMultiplier::UnitMultiplier_micro) {
            this->unitMultiplier = data;
            Common_input_cnt++;
        }
        else if (data == (int)sku::UnitSymbol::UnitSymbol_m) {
            this->unitSymbol = data;
            Common_input_cnt++;
        }
        else if (data == (int)sku::UnitMultiplier::UnitMultiplier_none) {
            this->unitMultiplier = data;
            Common_input_cnt++;
        }
        else {
            this->value = data;
            Common_input_cnt++;
        }
    };
    void dnp_set_normalOpen(int data) {
        if(data) {
            this->dnp_normalOpen_ = true;
        }
    };
    void iec_set_normalOpen(int data) {
        if(data) {
            this->iec_normalOpen_ = true;
        }
    };
};
class ACLineSegment : public Common_D{

};
class Length : public ACLineSegment {

};

class R : public ACLineSegment {

};

class X : public ACLineSegment {

};

class R0 : public ACLineSegment {

};

class X0 : public ACLineSegment {

};

class Switch : public Common_D{

};
class C_XSWI : public Switch {

};
class C_ZLIN : public Switch {

};
class IEC_Length : public Switch {

};

class IEC_R : public Switch {

};

class IEC_X : public Switch {

};

class IEC_R0 : public Switch {

};

class IEC_X0 : public Switch {

};
vector<string> split_func(string str, char Delimiter) {
    istringstream iss(str);             // istringstream에 str을 담는다.
    string buffer;                      // 구분자를 기준으로 절삭된 문자열이 담겨지는 버퍼
 
    vector<string> result;
 
    // istringstream은 istream을 상속받으므로 getline을 사용할 수 있다.
    while (getline(iss, buffer, Delimiter)) {
        result.push_back(buffer);               // 절삭된 문자열을 vector에 저장
    }
 
    return result;
}

int main()
{   
    char dnp_correct[255] = "DNP";
    char correct_61850[255] = "IEC";
    char bye_correct[255] = "BYE";
    int sockfd;
    string ans = "SUCCESS"; // Using string for string handling
    socklen_t addrlen;
    struct sockaddr_in addr, cliaddr;
    struct FEP fep;
    

    
    // DNP
    tArr g40v2 = {};
    tArr g10v2 = {};

    // Bye
    // tArr bye_buffer = {};
    // InitArr(&bye_buffer);

    // DNP
    InitArr(&g40v2);
    InitArr(&g10v2);

    // 61850
    vector<string> ZLIN_Name;
    vector<string> ZLIN_Data_LineLenkm;
    vector<string> ZLIN_Data_RPs;
    vector<string> ZLIN_Data_RZer;
    vector<string> ZLIN_Data_Xps;
    vector<string> ZLIN_Data_XZer;
    vector<string> XSWI_Name;
    vector<string> XSWI_Data_Pos;

    sockfd = socket(AF_INET, SOCK_DGRAM, 0);
    if (sockfd == -1)
    {
        return 1;
    }

    memset(&addr, 0, sizeof(addr));
    addr.sin_family = AF_INET;
    addr.sin_addr.s_addr = htonl(INADDR_ANY);
    addr.sin_port = htons(PORT_NUM);

    addrlen = sizeof(addr);
    if (bind(sockfd, (struct sockaddr *)&addr, addrlen) == -1)
    {
        return 1;
    }
    
    int i = 0;
    int j = 0;
    int k = 1;
    int l = 1;
    bool dnp_ok = false;
    bool iec_ok = false;
    bool isOK = true;
    while (isOK)
    {   
        if (iec_ok && dnp_ok)
            break;
        addrlen = sizeof(cliaddr);
        if ((recvfrom(sockfd, &fep, sizeof(fep), 0, (struct sockaddr *)&cliaddr, &addrlen) < 0) )
        {
            printf("error!");
            exit(1);
        }
        // cout << "protocol : " << fep.protocol << endl;
        if (strcmp(fep.protocol, dnp_correct) == 0)
        {
            if (i == 0 && fep.obj == 40 && fep.var == 2)
            {
                PushBack(&g40v2, fep.obj);
                PushBack(&g40v2, fep.var);
                i++;
            }
            else if (i >= 1 && fep.obj == 40 && fep.var == 2)
            {
                PushBack(&g40v2, fep.data);
            }
            else if (j == 0 && fep.obj == 10 && fep.var == 2)
            {
                PushBack(&g10v2, fep.obj);
                PushBack(&g10v2, fep.var);
                j++;
            }
            else if (j >= 1 && fep.obj == 10 && fep.var == 2)
            {
                PushBack(&g10v2, fep.data);
            }
        }
        else if (strcmp(fep.protocol, correct_61850) == 0)
        {
            iec_ok = true;   
            if(((string(fep.varName).compare(EENAME[0])) == 0) || ((string(fep.varName).compare(EENAME[1])) == 0)) {
                ZLIN_Name.push_back(string(fep.varName));
                ZLIN_Name.push_back(string(fep.value));
            }
            else if(((string(fep.varName).compare(EENAME[2])) == 0) || ((string(fep.varName).compare(EENAME[3])) == 0)) {
                XSWI_Name.push_back(string(fep.varName));
                XSWI_Name.push_back(string(fep.value));
            }
            else if(((string(fep.varName).compare(ZLIN[0][1])) == 0) || ((string(fep.varName).compare(ZLIN[0][2])) == 0) || ((string(fep.varName).compare(ZLIN[0][3])) == 0)){
                ZLIN_Data_LineLenkm.push_back(string(fep.varName));
                ZLIN_Data_LineLenkm.push_back(string(fep.value));               
            }
            else if(((string(fep.varName).compare(ZLIN[1][1])) == 0) || ((string(fep.varName).compare(ZLIN[1][2])) == 0) || ((string(fep.varName).compare(ZLIN[1][3])) == 0)){
                ZLIN_Data_RPs.push_back(string(fep.varName));
                ZLIN_Data_RPs.push_back(string(fep.value));
            }
            else if(((string(fep.varName).compare(ZLIN[2][1])) == 0) || ((string(fep.varName).compare(ZLIN[2][2])) == 0) || ((string(fep.varName).compare(ZLIN[2][3])) == 0)) {
                ZLIN_Data_Xps.push_back(string(fep.varName));
                ZLIN_Data_Xps.push_back(string(fep.value));                    
            }
            else if(((string(fep.varName).compare(ZLIN[3][1])) == 0) || ((string(fep.varName).compare(ZLIN[3][2])) == 0) || ((string(fep.varName).compare(ZLIN[3][3])) == 0)) {
                ZLIN_Data_RZer.push_back(string(fep.varName));
                ZLIN_Data_RZer.push_back(string(fep.value));
            }
            else if(((string(fep.varName).compare(ZLIN[4][1])) == 0) || ((string(fep.varName).compare(ZLIN[4][2])) == 0) || ((string(fep.varName).compare(ZLIN[4][3])) == 0)) {
                ZLIN_Data_XZer.push_back(string(fep.varName));
                ZLIN_Data_XZer.push_back(string(fep.value));
            }
            else if((string(fep.varName).compare("XSWI1$ST$Pos$stVal")) == 0) {
                XSWI_Data_Pos.push_back(fep.varName);
                XSWI_Data_Pos.push_back(fep.value);
            }
        }
        else if (strcmp(fep.protocol, "BYE_DNP") == 0){
            dnp_ok = true;
        }
        else if (strcmp(fep.protocol, "BYE_IEC") == 0){
            iec_ok = true;
        }
    }
    /*
    cout << "==================DNP==================" << endl;
    for (int a = 0; a < g40v2.iCount; a++)
    {
        cout << g40v2.pInt[a] << " ";
    }
    cout << endl << endl;
    for (int b = 0; b < g10v2.iCount; b++)
    {
        cout << g10v2.pInt[b] << " ";
    }
    cout << endl << endl;

    cout << "=================61850=================" << endl;
    cout << "===========[ZLIN]-EENAME===============" << endl;

    cout << ZLIN_Name[0] << " : " << ZLIN_Name[1] << endl;
    cout << ZLIN_Name[2] << " : " << ZLIN_Name[3];

    cout << endl << endl;
    cout << "===========[XSWI]-EENAME===============" << endl;

    cout << XSWI_Name[0] << " : " << XSWI_Name[1] << endl;
    cout << XSWI_Name[2] << " : " << XSWI_Name[3] << endl;

    cout << endl << endl;
    cout << "============[ZLIN]-Data================" << endl;
    cout << "----------- LinLenkm -----------" << endl;

    cout << ZLIN_Data_LineLenkm[0] << ":" << ZLIN_Data_LineLenkm[1] << endl;
    cout << ZLIN_Data_LineLenkm[2] << ":" << ZLIN_Data_LineLenkm[3] << endl;
    cout << ZLIN_Data_LineLenkm[4] << ":" << ZLIN_Data_LineLenkm[5] << endl;

    cout << endl << endl;
    cout << "----------- RPS -----------" << endl;

    cout << ZLIN_Data_RPs[0] << ":" << ZLIN_Data_RPs[1] << endl;
    cout << ZLIN_Data_RPs[2] << ":" << ZLIN_Data_RPs[3] << endl;
    cout << ZLIN_Data_RPs[4] << ":" << ZLIN_Data_RPs[5] << endl;

    cout << endl << endl;
    cout << "----------- XPs -----------" << endl;

    cout << ZLIN_Data_Xps[0] << ":" << ZLIN_Data_Xps[1] << endl;
    cout << ZLIN_Data_Xps[2] << ":" << ZLIN_Data_Xps[3] << endl;
    cout << ZLIN_Data_Xps[4] << ":" << ZLIN_Data_Xps[5] << endl;

    cout << endl << endl;
    cout << "----------- RZer -----------" << endl;

    cout << ZLIN_Data_RZer[0] << ":" << ZLIN_Data_RZer[1] << endl;
    cout << ZLIN_Data_RZer[2] << ":" << ZLIN_Data_RZer[3] << endl;
    cout << ZLIN_Data_RZer[4] << ":" << ZLIN_Data_RZer[5] << endl;

    cout << endl << endl;
    cout << "----------- XZer -----------" << endl;

    cout << ZLIN_Data_XZer[0] << ":" << ZLIN_Data_XZer[1] << endl;
    cout << ZLIN_Data_XZer[2] << ":" << ZLIN_Data_XZer[3] << endl;
    cout << ZLIN_Data_XZer[4] << ":" << ZLIN_Data_XZer[5] << endl;

    cout << endl << endl;
    cout << "============[XSWI]-Pos================" << endl;
    
    cout << XSWI_Data_Pos[0] << ":" << XSWI_Data_Pos[1] << endl;
    */
    Common_D common_d;
    Switch swi;
    ACLineSegment acl;
    Length lengthInstance;
    IEC_Length iec_lengthInstance;
    R rInstance;
    IEC_R iec_rInstance;
    X xInstance;
    IEC_X iec_xInstance;
    R0 r0Instance;
    IEC_R0 iec_r0Instance;
    X0 x0Instance;
    IEC_X0 iec_x0Instance;

    C_XSWI c_xswi;
    C_ZLIN c_zlin;

    cout << endl << endl;
    for (int i = 2; i < g40v2.iCount; i++) {
        lengthInstance.set_properties(g40v2.pInt[i]);
        rInstance.set_properties(g40v2.pInt[i]);
        xInstance.set_properties(g40v2.pInt[i]);
        r0Instance.set_properties(g40v2.pInt[i]);
        x0Instance.set_properties(g40v2.pInt[i]);
        if(Common_input_cnt == 15)
            break;
    }
    for (int i = 2; i < g10v2.iCount; i++ ) {
        acl.dnp_set_normalOpen(g10v2.pInt[i]);
        if(common_d.dnp_normalOpen_ == true)
            break;
    }
    vector<string> split_ZLIN1_EEName;
    vector<string> split_ZLIN1_LineLenkm;
    vector<string> split_ZLIN1_RPs;
    vector<string> split_ZLIN1_XPs;
    vector<string> split_ZLIN1_RZer;
    vector<string> split_ZLIN1_XZer;
    vector<string> split_XSWI_Name;
    vector<string> split_XSWI_Pos;
    
    if(split_ZLIN1_EEName.size() <= 0) {
        for (int i=0; i<ZLIN_Name.size()-1; i+=2) {
            cout << i << endl;
            vector<string> tokens = split_func(ZLIN_Name[i], '$');
            for (int j = 0; j < tokens.size(); j++) {
                cout << j << endl;
                split_ZLIN1_EEName.push_back(tokens[j]);
            }
        }
    }
    if(split_ZLIN1_LineLenkm.size() <= 0) {
        for (int i=0; i<ZLIN_Data_LineLenkm.size(); i+=2) {
            vector<string> tokens = split_func(ZLIN_Data_LineLenkm[i], '$');
            for (int j = 0; j < tokens.size(); j++) {
                split_ZLIN1_LineLenkm.push_back(tokens[j]);
            }
        }
    }
    if(split_ZLIN1_RPs.size() <= 0) {
        for (int i=0; i<ZLIN_Data_RPs.size(); i+=2) {
            vector<string> tokens = split_func(ZLIN_Data_RPs[i], '$');
            for (int j = 0; j < tokens.size(); j++) {
                split_ZLIN1_RPs.push_back(tokens[j]);
            }
        }
    }
    if(split_ZLIN1_XPs.size() <= 0) {
        for (int i=0; i<ZLIN_Data_Xps.size(); i+=2) {
            vector<string> tokens = split_func(ZLIN_Data_Xps[i], '$');
            for (int j = 0; j < tokens.size(); j++) {
                split_ZLIN1_XPs.push_back(tokens[j]);
            }
        }
    }
    if(split_ZLIN1_RZer.size() <= 0) {
        for (int i=0; i<ZLIN_Data_RZer.size(); i+=2) {
            vector<string> tokens = split_func(ZLIN_Data_RZer[i], '$');
            for (int j = 0; j < tokens.size(); j++) {
                split_ZLIN1_RZer.push_back(tokens[j]);
            }
        }
    }
    if(split_ZLIN1_XZer.size() <= 0) {
        for (int i=0; i<ZLIN_Data_XZer.size(); i+=2) {
            vector<string> tokens = split_func(ZLIN_Data_XZer[i], '$');
            for (int j = 0; j < tokens.size(); j++) {
                split_ZLIN1_XZer.push_back(tokens[j]);
            }
        }
    }
    if(split_XSWI_Name.size() <= 0) {
        for (int i=0; i<XSWI_Name.size(); i+=2) {
            vector<string> tokens = split_func(XSWI_Name[i], '$');
            for (int j = 0; j < tokens.size(); j++) {
                split_XSWI_Name.push_back(tokens[j]);
            }
        }
    }
    if(split_XSWI_Pos.size() <= 0) {
        for (int i=0; i<XSWI_Data_Pos.size(); i+=2) {
            vector<string> tokens = split_func(XSWI_Data_Pos[i], '$');
            for (int j = 0; j < tokens.size(); j++) {
                split_XSWI_Pos.push_back(tokens[j]);
            }
        }
    }
    if(((split_XSWI_Name[0].compare("XSWI1")) == 0) && ((split_XSWI_Pos[0].compare("XSWI1")) == 0)) {
        if ((split_XSWI_Name[2].compare("EEName")) == 0) {
            if((split_XSWI_Name[3].compare("name")) == 0) {
                c_xswi.name = ((string)XSWI_Name[1]);
            }
            if((split_XSWI_Name[7].compare("mRID")) == 0) {
                c_xswi.mrid = ((string)XSWI_Name[3]);
            }
        }
        if((split_XSWI_Pos[2].compare("Pos")) == 0) 
            swi.iec_set_normalOpen(stoi(XSWI_Data_Pos[1]));
    }
    if(((split_ZLIN1_EEName[0].compare("ZLIN1")) == 0) && ((split_ZLIN1_LineLenkm[0].compare("ZLIN1")) == 0) && ((split_ZLIN1_RPs[0].compare("ZLIN1")) == 0) && ((split_ZLIN1_XPs[0].compare("ZLIN1")) == 0) && ((split_ZLIN1_RZer[0].compare("ZLIN1")) == 0) && ((split_ZLIN1_XZer[0].compare("ZLIN1")) == 0)) {
        if ((split_ZLIN1_EEName[2].compare("EEName")) == 0) {
            if((split_ZLIN1_EEName[3].compare("name")) == 0) {
                c_zlin.name=((string)ZLIN_Name[1]);
            }
            if((split_ZLIN1_EEName[7].compare("mRID")) == 0) {
                c_zlin.mrid=((string)ZLIN_Name[3]);
            }
        }
        if((split_ZLIN1_LineLenkm[2].compare("LinLenkm")) == 0) {
            for (int i = 1; i<ZLIN_Data_LineLenkm.size()-2; i+=2) {
                iec_lengthInstance.set_properties(stoi(ZLIN_Data_LineLenkm[i]));
                stringstream ssDouble(ZLIN_Data_LineLenkm[5]);
                ssDouble >> iec_lengthInstance.f_value;
            }

        }
        if((split_ZLIN1_RPs[2].compare("RPs")) == 0) {
            for (int i = 1; i<ZLIN_Data_RPs.size()-2; i+=2) {
                iec_rInstance.set_properties(stoi(ZLIN_Data_RPs[i]));
                stringstream ssDouble(ZLIN_Data_RPs[5]);
                ssDouble >> iec_rInstance.f_value;

            }
        }
        if((split_ZLIN1_XPs[2].compare("XPs")) == 0) {
            for (int i = 1; i<ZLIN_Data_Xps.size()-2; i+=2) {
                iec_xInstance.set_properties(stoi(ZLIN_Data_Xps[i]));
                stringstream ssDouble(ZLIN_Data_Xps[5]);
                ssDouble >> iec_xInstance.f_value;
            }
        }
        if((split_ZLIN1_RZer[2].compare("RZer")) == 0) {
            for (int i = 1; i<ZLIN_Data_RZer.size()-2; i+=2) {
                iec_r0Instance.set_properties(stoi(ZLIN_Data_RZer[i]));
                stringstream ssDouble(ZLIN_Data_RZer[5]);
                ssDouble >> iec_r0Instance.f_value;
            }
        }
        if((split_ZLIN1_XZer[2].compare("XZer")) == 0) {
            for (int i = 1; i<ZLIN_Data_XZer.size()-2; i+=2) {
                iec_x0Instance.set_properties(stoi(ZLIN_Data_XZer[i]));
                stringstream ssDouble(ZLIN_Data_XZer[5]);
                ssDouble >> iec_x0Instance.f_value;
            }
        }
    }
    
    // Send to DDS
    int sock1 = 0, sock2=0;
    struct sockaddr_in serv_addr_open;
    struct sockaddr_in serv_addr_rti;
    if ((sock1 = socket(AF_INET, SOCK_STREAM, 0)) < 0) {
        cerr << "Socket creation error" << endl;
        return -1;
    }
    if ((sock2 = socket(AF_INET, SOCK_STREAM, 0)) < 0) {
        cerr << "Socket creation error" << endl;
        return -1;
    }

    serv_addr_open.sin_family=AF_INET;
    serv_addr_rti.sin_family=AF_INET;
    
    serv_addr_open.sin_port=htons(1234);
    serv_addr_rti.sin_port=htons(5678);

    if(inet_pton(AF_INET, "127.0.0.1", &serv_addr_open.sin_addr)<=0) {
        cerr << "Invalid address/ Address not supported" << endl;
        return -1;
    }
    if(inet_pton(AF_INET, "127.0.0.1", &serv_addr_rti.sin_addr)<=0) {
        cerr << "Invalid address/ Address not supported" << endl;
        return -1;
    }

    // Connect to the server
    if (connect(sock1, (struct sockaddr *)&serv_addr_open, sizeof(serv_addr_open)) < 0) {
        cerr << "Connection Failed" << endl;
        return -1;
    }
    if (connect(sock2, (struct sockaddr *)&serv_addr_rti, sizeof(serv_addr_rti)) < 0) {
        cerr << "Connection Failed" << endl;
        return -1;
    }
    
    //uint32_t dataLength = c_xswi.name.size();

    // Send message to the server
    cout << c_zlin.name << endl;
    cout << c_xswi.name << endl;
    c_zlin.name += "\n";
    c_zlin.mrid += "\n";
    
    send(sock1, c_zlin.name.c_str(), c_zlin.name.size(), 0);
    send(sock1, c_zlin.mrid.c_str(), c_zlin.mrid.size(), 0);
    send(sock2, c_zlin.name.c_str(), c_zlin.name.size(), 0);
    send(sock2, c_zlin.mrid.c_str(), c_zlin.mrid.size(), 0);

    string iec_length_us = to_string(iec_lengthInstance.unitSymbol);
    iec_length_us += "\n";
    send(sock1, iec_length_us.c_str(), iec_length_us.size(), 0);
    send(sock2, iec_length_us.c_str(), iec_length_us.size(), 0);

    string iec_length_um = to_string(iec_lengthInstance.unitMultiplier);
    iec_length_um += "\n";
    send(sock1, iec_length_um.c_str(), iec_length_um.size(), 0);
    send(sock2, iec_length_um.c_str(), iec_length_um.size(), 0);

    string iec_length_value = to_string(iec_lengthInstance.f_value);
    iec_length_value += "\n";
    send(sock1, iec_length_value.c_str(), iec_length_value.size(), 0);
    send(sock2, iec_length_value.c_str(), iec_length_value.size(), 0);

    string bye_to_dds = "Bye\n";
    send(sock1, bye_to_dds.c_str(), bye_to_dds.size(), 0);
    send(sock2, bye_to_dds.c_str(), bye_to_dds.size(), 0);

    // cout << "Hello message sent" << endl;

    // serv_addr.sin_family = AF_INET;
    // serv_addr.sin_port = htons(1234);

    /*
    cout << "======================= DNP =========================" << endl << endl;
       
    cout << "length Unitsymbol : " << lengthInstance.unitSymbol << endl;
    cout << "length UnitMulitplier : " << lengthInstance.unitMultiplier << endl;
    cout << "length value : " << lengthInstance.value << endl << endl;

    cout << "r Unitsymbol : " << rInstance.unitSymbol << endl;
    cout << "r UnitMulitplier : " << rInstance.unitMultiplier << endl ;
    cout << "r value : " << rInstance.value << endl << endl;

    cout << "x Unitsymbol : " << xInstance.unitSymbol << endl;
    cout << "x UnitMulitplier : " << xInstance.unitMultiplier << endl;
    cout << "x value : " << xInstance.value << endl << endl;

    cout << "r0 Unitsymbol : " << r0Instance.unitSymbol << endl;
    cout << "r0 UnitMulitplier : " << r0Instance.unitMultiplier << endl;
    cout << "r0 value : " << r0Instance.value << endl << endl;

    cout << "x0 Unitsymbol : " << x0Instance.unitSymbol << endl;
    cout << "x0 UnitMulitplier : " << x0Instance.unitMultiplier << endl;
    cout << "x0 value : " << x0Instance.value << endl << endl;

    cout << "normalOpen : " << boolalpha << acl.dnp_normalOpen_ << endl << endl;

    cout << "======================= IEC =========================" << endl << endl;
       
    cout << "length Unitsymbol : " << iec_lengthInstance.unitSymbol << endl;
    cout << "length UnitMulitplier : " << iec_lengthInstance.unitMultiplier << endl;
    cout << "length value : " << iec_lengthInstance.f_value << endl << endl;

    cout << "r Unitsymbol : " << iec_rInstance.unitSymbol << endl;
    cout << "r UnitMulitplier : " << iec_rInstance.unitMultiplier << endl ;
    cout << "r value : " << iec_rInstance.f_value << endl << endl;

    cout << "x Unitsymbol : " << iec_xInstance.unitSymbol << endl;
    cout << "x UnitMulitplier : " << iec_xInstance.unitMultiplier << endl;
    cout << "x value : " << iec_xInstance.f_value << endl << endl;

    cout << "r0 Unitsymbol : " << iec_r0Instance.unitSymbol << endl;
    cout << "r0 UnitMulitplier : " << iec_r0Instance.unitMultiplier << endl;
    cout << "r0 value : " << iec_r0Instance.f_value << endl << endl;

    cout << "x0 Unitsymbol : " << iec_x0Instance.unitSymbol << endl;
    cout << "x0 UnitMulitplier : " << iec_x0Instance.unitMultiplier << endl;
    cout << "x0 value : " << iec_x0Instance.f_value << endl << endl;

    cout << "normalOpen : " << boolalpha << swi.iec_normalOpen_ << endl << endl;
    */
    ReleaseArr(&g40v2);
    ReleaseArr(&g10v2);
    return 0;
}