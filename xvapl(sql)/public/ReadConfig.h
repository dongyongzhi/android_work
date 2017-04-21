#ifndef _READ_CONFIG_H_
#define _READ_CONFIG_H_
#include <windows.h>
#include "comm.h"
#include "Commdll.h"
//#define LENGTH_KEY     32   /*关键字长度*/
//#define LENGTH_SECTION 64   /*节长度*/
//#define LENGTH_VALUE   128  /*配置数据长度*/
//#define LENGTH_COMMENT 128  /*注释长度*/
//#define LENGTH_BUFFER  256  /*字符缓冲大小*/
///*关键词存储结构*/
//typedef struct Key
//{
//    char key[LENGTH_KEY],value[LENGTH_VALUE],comment[LENGTH_COMMENT];/*关键词\数据\注释*/
//    struct Key *next;/*下一结点*/
//}KEY,K;
///*节存储结构*/
//typedef struct Section
//{
//   char section[LENGTH_SECTION],comment[LENGTH_COMMENT];/*节名及注释*/
//   struct Key headKey;/*对应节关键词首结点,首结点不含数据*/
//   struct Section *next;/*下一结点*/
//}SECTION,SEC,S;
///*配置文件结构*/
//typedef struct Ini
//{
//   struct Section headSection;/*节首结点,首结点不含数据*/
//}INI,I;
/*临时变量用于导入数据*/
char tmpBUFFER[LENGTH_BUFFER];
char tmpSection[LENGTH_SECTION];
char tmpKey[LENGTH_KEY];
char tmpValue[LENGTH_VALUE];
char tmpComment[LENGTH_COMMENT];
static char CommentSeparator=';';
/*函数声明*/
void initINI(INI *ini);
/*释放*/
//void freeINI(INI *ini);

//void loadINI(INI *ini,char *filename);/* 导入 */
void saveINI(INI *ini,char *filename);/* 保存 */

SECTION * addSection(INI *ini,char *name,char *comment);
/*增加节,参数为配置文件,名称及注释,空为NULL,成功返回地址,
失败返回NULL,如已存在节将自动返回存在地址相当于搜索,不区分大小写*/
int insertSECTION(SECTION *newSEC,SECTION *preSEC);/*在preSEC后插入newSEC*/

//SECTION *getSection(INI *ini,char *name);/*根据名称获取指定节*/
SECTION *getNextSection(SECTION *sec);/*获取指定地址下一元素*/
/*根据名称返回节地址用于操作,失败返回NULL,不区分大小写*/
int  removeSection(INI *ini,char *name);
/*根据名称删除指定节,失败返回0成功返回1,不区分大小写*/
void freeSecKey(SECTION *ptrSEC);/*释放指定节下所有关键字*/

KEY *addKey(INI *ini,char *secName,char *keyName,char *keyValue,char *comment);
/*对指定节名增加关键词名称及其数据与注释,成功返回地址,失败返回NULL*/
int insertKEY(KEY *newKEY,KEY *preKEY);/*在preKEY后插入newKEY*/
KEY *addSecKey(SEC *keySEC,char *keyName,char *keyValue,char *comment);
/*对指定节地址增加关键词名称及其数据与注释,成功返回地址,失败返回NULL*/
KEY *getKey(INI *ini,char *secName,char *keyName);
KEY *getNextKey(KEY *key);/*指定key的下一个元素*/ 
char *getKeyValue(KEY *key);/*获取指定关键字的数据*/
/*对指定节获取匹配到的关键词*/
KEY *getSecKey(SECTION *keySEC,char *keyName);
/*对指定节地址获取匹配到的关键词*/
int removeKey(INI *ini,char *secName,char *keyName);
int removeSecKey(SECTION *keySEC,char *keyName);
/*统计函数*/
int getSectionCount(INI *ini);
int getKeyCount(SECTION *keySEC);
/*修改节名称、注释，关键字名称、注释*/
int setSectionName(INI *ini,SECTION *sec,char *secName);
int setSectionComment(INI *ini,SECTION *sec,char *secComment);
int setKeyName(KEY *setKey,char *keyName);
int setKeyValue(KEY *setKey,char *keyValue);
int setKeyComment(KEY *setKey,char *keyComment);
/*打印输出*/
void printSectionList(INI *ini);
void printKeyList(SECTION *keySEC);
///*void*/char * GetKeyList(SECTION *keySEC,BYTE offset,BYTE index);
void printList(INI *ini);

/*分配空间*/
SECTION * allocateSEC(char *name,char *comment);
KEY *allocateKEY(char *name,char *value,char *comment);
/*字符处理函数*/
int isIgnoreCaseChar(char ch1,char ch2);
int ignoreCaseCompare(char *str1,char *str2);
int isSpecial(char ch);
void RightTrim(char *str);
void LeftTrim(char *str);
void Trim(char *str);
#endif