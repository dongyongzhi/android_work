/********************************************************************
公司名称：江苏怡丰通讯有限公司
创建时间: 2007-11-16   13:58
文件名称: E:\商品信息查询分析系统业务逻辑处理项目\源代码\SPP\ReadConfig.c
文件路径: E:\商品信息查询分析系统业务逻辑处理项目\源代码\SPP
file base:ReadConfig
file ext: c
author:	  刘定文

purpose:	读配置文件SPP.INI
*********************************************************************/
#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include <malloc.h>
//#include "../public/init_viriable.h"
#include "../public/ReadConfig.h"
void initINI(INI *ini)
{
     ini->headSection.next=NULL;
     strcpy(ini->headSection.section,"");
     strcpy(ini->headSection.comment,"");

}
SECTION * addSection(INI *ini,char *name,char *comment)
{
        SECTION *visit,*newSection;
        if(ini==NULL||name==NULL) return NULL;
        visit=ini->headSection.next;
        /*准备插入,找到尾结点,如果发现已存在将直接返回现有地址*/
          while(visit) 
        {
            if(ignoreCaseCompare(visit->section,name)) return visit;
            if(visit->next==NULL) break;
            visit=visit->next;
        }
        /*申请空间并初始化数据*/
        newSection=(SECTION *)malloc(sizeof(SECTION));
        if(newSection==NULL) return NULL;
        
        newSection->next=NULL;
        newSection->headKey.next=NULL;
        strcpy(newSection->section,name);
        if(comment==NULL) strcpy(newSection->comment,"");
        else strcpy(newSection->comment,comment);
        if(visit==NULL) ini->headSection.next=newSection;
        else visit->next=newSection;
        return newSection;        
}
DLLIMPORT SECTION *getSection(INI *ini,char *name)
{
        SECTION *visit;
        if(ini==NULL||name==NULL) return NULL;
        visit=ini->headSection.next;
        /*准备插入,找到尾结点,如果发现已存在将直接返回现有地址*/
        while(visit) 
        {
            if(ignoreCaseCompare(visit->section,name)) return visit;
            visit=visit->next;
        }
        return NULL;
}
SECTION *getNextSection(SECTION *sec)
{
    return sec==NULL?NULL:sec->next;
}
int  removeSection(INI *ini,char *name)
{
     SECTION *ptrPrev,*ptrRemove=NULL,*visit;
     if(ini==NULL||name==NULL) return 0;
     visit=ini->headSection.next;
     ptrPrev=&(ini->headSection);
     while(visit) 
     {
         if(ignoreCaseCompare(visit->section,name)) 
         {
             ptrRemove=visit;
             break;
         }
         ptrPrev=visit;
         visit=visit->next;
    }
    
    if(ptrRemove==NULL) return 0;
    ptrPrev->next=ptrRemove->next;
    freeSecKey(ptrRemove);
    free(ptrRemove);
    return 1;
}

void freeSecKey(SECTION *ptrSEC)
{
     KEY *visit=NULL;
     if(ptrSEC==NULL||ptrSEC->headKey.next==NULL) return;
     visit=ptrSEC->headKey.next;
     while(visit)
     {
        ptrSEC->headKey.next=visit->next;
        free(visit);
        visit=ptrSEC->headKey.next;
     }
}

KEY *addKey(INI *ini,char *secName,char *keyName,char *keyValue,char *comment)
{
    SECTION *keySEC=NULL;
    KEY *visit,*newKey=NULL;
    if(ini==NULL||secName==NULL||keyName==NULL) return NULL;
    keySEC=getSection(ini,secName);
    if(keySEC==NULL) return NULL;
    visit=keySEC->headKey.next;
    while(visit)
    {
         if(ignoreCaseCompare(visit->key,keyName)) return visit;
         if(visit->next==NULL) break;
         visit=visit->next;
    }
        
    newKey=(KEY *)malloc(sizeof(KEY));
    if(newKey==NULL) return NULL;
    newKey->next=NULL;
    strcpy(newKey->key,keyName);
    if(keyValue==NULL) strcpy(newKey->value,"");
    else strcpy(newKey->value,keyValue);    
    if(comment==NULL) strcpy(newKey->comment,"");
    else strcpy(newKey->comment,comment);
    
    if(visit==NULL) keySEC->headKey.next=newKey;
    else visit->next=newKey;
    return newKey;
}

KEY *addSecKey(SEC *keySEC,char *keyName,char *keyValue,char *comment)
{
    KEY *visit=NULL,*newKey=NULL;
    if(keySEC==NULL||keyName==NULL) return NULL;
    visit=keySEC->headKey.next;
    while(visit)
    {
         if(ignoreCaseCompare(visit->key,keyName)) return visit;
         if(visit->next==NULL) break;
         visit=visit->next;
    }
  
    newKey=(KEY *)malloc(sizeof(KEY));
    if(newKey==NULL) return NULL;
    newKey->next=NULL;
    strcpy(newKey->key,keyName);
    if(keyValue==NULL) strcpy(newKey->value,"");
    else strcpy(newKey->value,keyValue);    
    if(comment==NULL) strcpy(newKey->comment,"");
    else strcpy(newKey->comment,comment);   

    if(visit==NULL) keySEC->headKey.next=newKey;
    else visit->next=newKey;
    return newKey;
}

KEY *getKey(INI *ini,char *secName,char *keyName)
{
    SECTION *keySEC=NULL;
    KEY *visit,*newKey=NULL;
    if(ini==NULL||secName==NULL||keyName==NULL) return NULL;
    keySEC=getSection(ini,secName);
    if(keySEC==NULL) return NULL;
    visit=keySEC->headKey.next;
    while(visit)
    {
         if(ignoreCaseCompare(visit->key,keyName)) return visit;
         if(visit->next==NULL) break;
         visit=visit->next;
    }
    return NULL;
}
KEY *getNextKey(KEY *key)
{
    return key==NULL?NULL:key->next;
}

char *getKeyValue(KEY *key)
{
	  return key==NULL?NULL:key->value;
}
int setSectionName(INI *ini,SECTION *sec,char *secName)
{
     SECTION *findSec=NULL;     
     if(sec==NULL||secName==NULL||strlen(secName)==0) return 0;/*无效返回*/
     Trim(secName);   
     findSec=getSection(ini,secName);
     if(findSec==NULL) return 0;/*已重复返回*/
     
     strcpy(sec->section,secName);/*修改*/    
     return 1; 
}
int setSectionComment(INI *ini,SECTION *sec,char *secComment)
{
     if(sec==NULL||secComment==NULL||strlen(secComment)==0) return 0;/*无效返回*/
     Trim(secComment);        
     strcpy(sec->comment,secComment);/*修改*/    
     return 1; 
}
int setKeyName(KEY *setKey,char *keyName)
{          
     if(setKey==NULL||keyName==NULL||strlen(keyName)==0) return 0;/*无效返回*/
     Trim(keyName);
     strcpy(setKey->key,keyName);/*修改*/    
     return 1; 
}

int setKeyValue(KEY *setKey,char *keyValue)
{
     if(setKey==NULL||keyValue==NULL||strlen(keyValue)==0) return 0;/*无效返回*/
     Trim(keyValue);
     strcpy(setKey->value,keyValue);/*修改*/    
     return 1; 
}

int setKeyComment(KEY *setKey,char *keyComment)
{
     if(setKey==NULL||keyComment==NULL||strlen(keyComment)==0) return 0;/*无效返回*/
     Trim(keyComment);
     strcpy(setKey->comment,keyComment);/*修改*/    
     return 1; 
}
KEY *getSecKey(SECTION *keySEC,char *keyName)
{
    KEY *visit,*newKey=NULL;
    if(keySEC==NULL||keyName==NULL) return NULL;
    visit=keySEC->headKey.next;
    while(visit)
    {
         if(ignoreCaseCompare(visit->key,keyName)) return visit;
         if(visit->next==NULL) break;
         visit=visit->next;
    }
    return NULL;
}

int removeKey(INI *ini,char *secName,char *keyName)
{
    KEY *ptrPrev,*ptrRemove=NULL,*visit;
    SECTION *keySEC=NULL;
    if(ini==NULL||secName==NULL||keyName==NULL) return 0;
    keySEC=getSection(ini,secName);
    if(keySEC==NULL) return 0;
    
    visit=keySEC->headKey.next;
    ptrPrev=visit;
    while(visit)
    {
        if(ignoreCaseCompare(visit->key,keyName)) 
        {
           ptrRemove=visit;break;                                         
        }
        ptrPrev=visit;
        visit=visit->next;
    }
    
    if(ptrRemove==NULL) return 0;
    ptrPrev->next=ptrRemove->next;
    free(ptrRemove);
    return 1;    
}

int removeSecKey(SECTION *keySEC,char *keyName)
{
    KEY *ptrPrev,*ptrRemove=NULL,*visit;
    if(keySEC==NULL||keyName==NULL) return 0;
   
    visit=keySEC->headKey.next;
    ptrPrev=visit;
    while(visit)
    {
        if(ignoreCaseCompare(visit->key,keyName)) 
        {
           ptrRemove=visit;break;                                         
        }
        ptrPrev=visit;
        visit=visit->next;
    }
    
    if(ptrRemove==NULL) return 0;
    ptrPrev->next=ptrRemove->next;
    free(ptrRemove);
    return 1;    
}
/********************************************************************
函数名称：freeINI
函数功能: 释放INI
参数定义: 无
返回定义: 无
创建时间: 2007-11-20 10:17
函数作者: 刘定文
注意事项: 无	
*********************************************************************/

DLLIMPORT void freeINI(INI *ini)
{
     SECTION *visit;
     if(ini==NULL) return;
     visit=ini->headSection.next;
     while(visit)
     {
        ini->headSection.next=visit->next;
        freeSecKey(visit);
        free(visit);
        visit=ini->headSection.next;
     }
}
int getSectionCount(INI *ini)
{
     int count=0;
     SECTION *list=NULL;
     if(ini==NULL)
     {
        return 0;
     }
     else
     {
        list=ini->headSection.next;
        while(list)
        {
            count++;
            list=list->next;
        }
     }
     return count;
}


int getKeyCount(SECTION *keySEC)
{
     int count=0;
     KEY *list=NULL;
     if(keySEC==NULL||keySEC->headKey.next==NULL)
     {
        return 0;
     }
     else
     {
        list=keySEC->headKey.next;
        while(list)
        {
            count++;
            list=list->next;
        }
     }
     return count;
}

void printList(INI *ini)
{
     int index=0;
     SECTION *list=NULL;
     KEY *keylist=NULL;
     if(ini==NULL)
     {
        printf("\nNone INI.\n");
     }
     else
     {
        list=ini->headSection.next;
  
        while(list)
        {
            keylist=list->headKey.next;
            printf("\nSection Name:%s\n",list->section);
            printf("\nNo |Key Name\t\t|Key Value\n");
            index=0;
            while(keylist)
            {
               printf("%3d %-20s %s\n",++index,keylist->key,keylist->value);
               keylist=keylist->next;
            }
            list=list->next;
        }
     }
}
void printSectionList(INI *ini)
{
     int index=0;
     SECTION *list=NULL;
     if(ini==NULL)
     {
        printf("\nNone INI.\n");
     }
     else
     {
        list=ini->headSection.next;
        printf("\nNo |Section Name\n");
        while(list)
        {
            printf("%3d:%s\n",++index,list->section);
            list=list->next;
        }
     }
}
/********************************************************************
函数名称：GetKeyList
函数功能: 得到各个具体属性
参数定义: keySEC:链表节点，index:项号
返回定义: 无
创建时间: 2007-11-20 10:09
函数作者: 刘定文
注意事项: 无	
*********************************************************************/

DLLIMPORT char * GetKeyList(SECTION *keySEC,/*BYTE offset,*/BYTE index)
{
	int i;
//	BYTE bModule;
    KEY *list=NULL;
    if(keySEC==NULL)
	{
		printf("\nNone Section.\n");
	}
	else
	{
		if(keySEC->headKey.next==NULL)
		{
			list=NULL;
			
		}
		else 
		{
			list=keySEC->headKey.next;
		}
		for(i=0;i<index;i++)
        {
			if(list!=NULL)
				list=list->next;
			else 
			{
				return NULL;
			}
		}
        if(list)
        {
			return list->value;
//			switch(bType)
//			{
//			case INI_COMM://COMM - NUMBER
//				{
//					if(index==0)
//					{
//						s_basic.bNodeNum=(BYTE)atoi(list->value);
//					}
//					else if(index == 1)
//					{
//						s_basic.wAttri.att.isCdr=(WORD)atoi(list->value);
//					}
//					else if(index == 2)
//					{
//						s_basic.wAttri.att.isCdrToSql=(WORD)atoi(list->value);
//					}
//					
//				}
//				break;
//			case INI_NODE://NODE 
//				{
//					if(index==0)//节点号
//					{
//						g_Node[offset].node=inet_addr(list->value);
//						if(offset==0)
//						{
//							g_dwNode = g_Node[offset].node;
//							g_Public->g_dwNode = g_Node[offset].node;
//						}
//						
//					}
//					else if(index==1)//isSpp
//					{
//						g_Node[offset].isSPP =(BOOL)atoi(list->value);
//						
//					}
//					else if(index==2)//isServer
//					{
//						g_Node[offset].isServer = (BOOL)atoi(list->value);
//					}
//					else if(index==3)//module数
//					{
//						g_Node[offset].bModuleNum = (BYTE)atoi(list->value);
//						
//					}
//					else //具体MODULE号
//					{
//						bModule = (BYTE)atoi(list->value);
//						if(SYSMODULE<=bModule<MODULE_MAX)//确定是否系统模块也要写入其中
//						{
//							g_Module[bModule].mId = bModule;
//							g_Module[bModule].isSysModule = FALSE;
//						}
//					}
//				}
//				break;
//			case INI_SQL:
//				if(index==0)//数据库计算机IP
//				{
//					s_database.ComputerIp=inet_addr(list->value);
//					
//				}
//				else if(index==1)//
//				{
//					//g_Node[offset].isSPP =(BOOL)atoi(list->value);
//					strcpy(s_database.pUserName,list->value);
//					
//				}
//				else if(index==2)//isServer
//				{
//					strcpy(s_database.pPassWd,list->value);
//				}
//				break;
//			case INI_IPP:
//				if(index==0)//第三方接口个数
//				{
//					s_basic.bIppNum=(BYTE)atoi(list->value);
//				}
//				break;
//			case INI_IPPX:
//				{
//					if(index == 0)//编号
//					{
//						g_Ipp[offset].id = atoi(list->value);
//					}
//					else if(index==1)//name
//					{
//						strcpy(g_Ipp[offset].pName,list->value);						
//					}
//					else if(index==2)//is server or custom 
//					{
//						g_Ipp[offset].isServer = (BOOL)atoi(list->value);
//						
//					}
//					else if(index==3)//节点号
//					{
//						g_Ipp[offset].dIp=inet_addr(list->value);
//					}
//					else if(index==4)//isServer
//					{
//						g_Ipp[offset].wPort = (WORD)atoi(list->value);
//					}
//				}
//				break;
//			case INI_VER:
//				if(index == 0)
//				{
//					strcpy(s_basic.version,list->value);
//				}
//				break;
//			}
//			index++;
//            //printf("%3d %-20s %s\n",++index,list->key,list->value);
//            list=list->next;
        }
	}
	return NULL;
}
void printKeyList(SECTION *keySEC)
{
     int index=0;
     KEY *list=NULL;
     if(keySEC==NULL)
     {
        printf("\nNone Section.\n");
     }
     else
     {
        if(keySEC->headKey.next==NULL) list=NULL;
        else list=keySEC->headKey.next;
        printf("\nSECTION Name:%s",keySEC->section);
        printf("\nNo |Key Name\t\t|Key Value\n");
        while(list)
        {
            printf("%3d %-20s %s\n",++index,list->key,list->value);
            list=list->next;
        }
     }
}
/*
void toLowerCase(const char *source)
{
    int index=0;
    if(source==NULL) {strcpy(tmpBUFFER,"");return;}
    else strcpy(tmpBUFFER,source); 
    
    while(tmpBUFFER[index])
    {
       if(tmpBUFFER[index]>='A'&&tmpBUFFER[index]<='Z')
          tmpBUFFER[index]=tmpBUFFER[index]+32;
       index++;
    }
}
*/
int ignoreCaseCompare(char *str1,char *str2)
{
    int index=0;
    if(str1==str2) return 1;
    if(str1==NULL||str2==NULL) return 0;
    while(isIgnoreCaseChar(str1[index],str2[index]) && str1[index] && str2[index])
    {
        index++;
    }
    return isIgnoreCaseChar(str1[index],str2[index]);
}

int isIgnoreCaseChar(char ch1,char ch2)
{
    if(ch1==ch2) return 1;
    if(ch1>='A' && ch1<='Z') ch1+=32;
    if(ch2>='A' && ch2<='Z') ch2+=32;
    return ch1==ch2?1:0;
}

SECTION * allocateSEC(char *name,char *comment)
{
        SECTION *newSection;
        newSection=(SECTION *)malloc(sizeof(SECTION));
        if(newSection==NULL) return NULL;
        newSection->next=NULL;
        newSection->headKey.next=NULL;
        strcpy(newSection->section,name);
        if(comment==NULL) strcpy(newSection->comment,"");
        else strcpy(newSection->comment,comment);
        return newSection;        
}

KEY *allocateKEY(char *name,char *value,char *comment)
{
    KEY *newKey=NULL;         
    newKey=(KEY *)malloc(sizeof(KEY));
    if(newKey==NULL) return NULL;
    newKey->next=NULL;
    strcpy(newKey->key,name);
    if(value==NULL) strcpy(newKey->value,"");
    else strcpy(newKey->value,value);    
    if(comment==NULL) strcpy(newKey->comment,"");
    else strcpy(newKey->comment,comment);   
    return newKey;
}

int insertSECTION(SECTION *newSEC,SECTION *preSEC)
{
    if(preSEC==NULL) return 0;
    newSEC=preSEC->next;
    preSEC->next=newSEC;
	return 0;
}
int insertKEY(KEY *newKEY,KEY *preKEY)
{
    if(preKEY==NULL) return 0;
    newKEY=preKEY->next;
    preKEY->next=newKEY;
	return 0;
}

void saveINI(INI *ini,char *filename)
{
     FILE *dat=NULL;
     SECTION *visitSEC=NULL;
     KEY *visitKEY=NULL;
     int len=0;
     if(ini==NULL) 
     {
        printf("No INI to Export.\n");
        return;
     }
     dat=fopen(filename,"w");
     if(dat==NULL)
     {
        printf("I/O Error:FILE %s\n",filename);
        return;           
     }
     
     visitSEC=ini->headSection.next;
     while(visitSEC)
     {
         if(strlen(visitSEC->comment)==0)
             sprintf(tmpBUFFER,"[%s]\n",visitSEC->section);
         else
             sprintf(tmpBUFFER,"[%s]%c%s\n",visitSEC->section,CommentSeparator,visitSEC->comment);
         if(visitSEC->headKey.next==NULL) visitKEY=NULL;
         else visitKEY=visitSEC->headKey.next;
         fprintf(dat,"%s",tmpBUFFER);
         while(visitKEY)
         {
            sprintf(tmpBUFFER,"%s = %s",visitKEY->key,visitKEY->value);
            if(strlen(visitKEY->comment)>0)
            {
              len=strlen(tmpBUFFER);
              tmpBUFFER[len]=CommentSeparator;  
              tmpBUFFER[len+1]='\0';                           
              strcat(tmpBUFFER,visitKEY->comment);
            }
            strcat(tmpBUFFER,"\n");    
            visitKEY=visitKEY->next;
            fprintf(dat,"%s",tmpBUFFER);
         }      
        
         visitSEC=visitSEC->next;
     }
     fclose(dat);     
}
/********************************************************************
函数名称：loadINI
函数功能: 装载INI文件
参数定义: ini:信息存放的链表，filename:文件名
返回定义: 无
创建时间: 2007-11-20 10:14
函数作者: 刘定文
注意事项: 无	
*********************************************************************/

DLLIMPORT void loadINI(INI *ini,char *filename)
{
     FILE *dat=NULL;
     int isSection=0,isKey=0,isValue=0,isComment=0,isNewLine=1;
     int isLoadSection=0;
     int index=0;
     char ch;
     SECTION *sec=NULL;
     initINI(ini);
     dat=fopen(filename,"r");
     if(dat==NULL)
     {
         printf("I/O Error:File %s\n",filename);
         return;
     }
     while(!feof(dat))
     {
       ch=fgetc(dat);

       if(!(ch=='\r'||ch=='\n'))
       {
          if(isNewLine)
          {
              if(ch!=' ') isNewLine=0;
              if(!(ch=='['||ch==CommentSeparator)) {isKey=1; isLoadSection=0; }   
          }
          
          if(isSection) 
          {
              if(ch!=']') {tmpSection[index++]=ch;tmpSection[index]='\0';}
          }
          else if(isKey) 
          {
               if(ch!='=')
               {
                  tmpKey[index++]=ch;
                  tmpKey[index]='\0';
               }
          }
          else if(isValue) 
          {
               if(ch!=CommentSeparator)
               {
                  tmpValue[index++]=ch;
                  tmpValue[index]='\0';
               }
          }
          else if(isComment) {tmpComment[index++]=ch;tmpComment[index]='\0';}
       }
       
       if(ch=='[')
       {            
           if(isKey==0&&isValue==0&&isComment==0)   {isSection=1;isLoadSection=1;index=0;}
       }
       else if(ch==']') 
       {
           if(isSection==1) 
           {
              isSection=0;
              index=0;
           }
       }
       else if(ch=='=') 
       {
           if(isKey)
           {           
               index=0;
               isKey=0;
               isValue=1;
           } 
       }
       else if(ch==CommentSeparator) 
       {
            if(isValue==1)
            {               
               isValue=0;
            }
            isComment=1;
            index=0;
       }
       else if(ch=='\n'||ch=='\r')
       {
            /*保存Section或Key*/
            if(isNewLine==0)
            {
               Trim(tmpComment);
               
               if(isLoadSection)
               {   
                  Trim(tmpSection);      
                  sec=addSection(ini,tmpSection,tmpComment);
               }
               else
               {
                   //printf("Key:%s Value:%s Comment:%s\n",tmpKey,tmpValue,tmpComment);
                   Trim(tmpKey);
                   Trim(tmpValue);
                   if(sec!=NULL) addSecKey(sec,tmpKey,tmpValue,tmpComment);
               }
            }
            isSection=0;
            isKey=0;
            isValue=0;
            isComment=0;
            isLoadSection=0;
            tmpSection[0]='\0';
            tmpKey[0]='\0';
            tmpValue[0]='\0';
            tmpComment[0]='\0';
            isNewLine=1;
            index=0;
       }
   
     }
     fclose(dat);
}

int isSpecial(char ch) 
{
    return (ch=='['||ch==']'||ch=='='||ch=='\r'||ch=='\n'||ch==CommentSeparator)?1:0;
}

void RightTrim(char *str)
{
  int len=0,index;
  if(str==NULL) return;
  len=strlen(str);
  if(len==0) return ;
  index=len-1;
  while(index>=0)
  {
      if(str[index]==' ') str[index]='\0';
      else break;
      index--;
  }
}

void LeftTrim(char *str)
{
  int len=0,index=0,tmpIndex;
  if(str==NULL) return;
  len=strlen(str);
  if(len==0) return ;
  while(index<len)
  {
      if(str[index]!=' ')  break;
      index++;
  }
  if(index==0) return;
  if(index==len) str[0]='\0';
  else
  {
     for(tmpIndex=0;;tmpIndex++)
     {
          str[tmpIndex]=str[index+tmpIndex];
          if(str[index+tmpIndex]=='\0') break;
     }
  }
}

void Trim(char *str)
{
     LeftTrim(str);
     RightTrim(str);
}