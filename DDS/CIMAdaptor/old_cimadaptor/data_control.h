/*
data_control.h

Header file containing IEC 61850 data definitions and 
the necessary functions to insert incoming data into a dynamic array.
*/

#include <stdio.h>
#include <iostream>
#include <sys/types.h>
#include <cstring>
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

// ACLineSegment
class ACLineSegment : public Common_D;

class Length : public ACLineSegment;

class R : public ACLineSegment;

class X : public ACLineSegment;

class R0 : public ACLineSegment;

class X0 : public ACLineSegment;

// Switch
class Switch : public Common_D;

class C_XSWI : public Switch;

class C_ZLIN : public Switch;
class IEC_Length : public Switch;
class IEC_R : public Switch;

class IEC_X : public Switch;

class IEC_R0 : public Switch;

class IEC_X0 : public Switch;


struct FEP
{
    char protocol[255];
    char varName[255];
    char value[255];
    uint8_t obj;
    uint8_t var;
    short data;
    //char bye_string[255];
};

typedef struct _tabArr
{
    // 가변데이터을 가지는 구조체
    // 해당 주소를 가르키는 포인터
    char **pStr;
    int *pInt;
    // 얼마나 넣었는지 알려주는 변수
    int iCount;
    // 마지막 변수
    int iMaxCount;

} tArr;

// Init Function
void InitArr(tArr *_pArr)
{
    _pArr->pInt = (int *)malloc(sizeof(int) * 2);
    _pArr->iCount = 0;
    _pArr->iMaxCount = 2;
}

void Reallocate(tArr *_pArr)
{
    // 1. 2배 더 큰 공간을 동적할당한다.
    int *pNew = (int *)malloc(_pArr->iMaxCount * 2 * sizeof(int));
    // 2. 기존 공간에 있던 데이터들을 새로 할당한 공간으로 복사한다.
    for (int i = 0; i < _pArr->iCount; ++i)
    {
        pNew[i] = _pArr->pInt[i];
    }

    // 3. 기존 공간은 메모리 해제
    free(_pArr->pInt);
    // 4. 배열이 새로 할당된 공간을 가리키게 한다.
    _pArr->pInt = pNew;
    // 5. MaxCount  변경점 적용
    _pArr->iMaxCount *= 2;
}


// Data add function
void PushBack(tArr *_pArr, int _iData)
{
    // Heap Data full..
    if (_pArr->iMaxCount <= _pArr->iCount)
    {
        Reallocate(_pArr);
    }
    // Data add
    _pArr->pInt[_pArr->iCount++] = _iData;
}


// Array memory unrelease
void ReleaseArr(tArr *_pArr)
{
    free(_pArr->pInt);
    _pArr->iCount = 0;
    _pArr->iMaxCount = 0;
}

string EENAME[4] = {"ZLIN1$DC$EEName$name", "ZLIN1$DC$EEName$mRID","XSWI1$DC$EEName$name", "XSWI1$DC$EEName$mRID"};
string ZLIN[5][4] = {{"LinLenkm", "ZLIN1$CF$LinLenkm$units$SIUnit", "ZLIN1$CF$LinLenkm$units$multiplier", "ZLIN1$SP$LinLenkm$setMag$f"},
                          {"RPs", "ZLIN1$CF$RPs$units$SIUnit", "ZLIN1$CF$RPs$units$multiplier", "ZLIN1$SP$RPs$setMag$f"},
                          {"XPs", "ZLIN1$CF$XPs$units$SIUnit", "ZLIN1$CF$XPs$units$multiplier", "ZLIN1$SP$XPs$setMag$f"},
                          {"RZer", "ZLIN1$CF$RZer$units$SIUnit", "ZLIN1$CF$RZer$units$multiplier", "ZLIN1$SP$RZer$setMag$f"},
                          {"XZer", "ZLIN1$CF$XZer$units$SIUnit", "ZLIN1$CF$XZer$units$multiplier", "ZLIN1$SP$XZer$setMag$f"},};