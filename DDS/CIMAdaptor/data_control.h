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