#ifdef     XVAPL_ODI_123
#define    XVAPL_ODI_123 

#else

#define    R_run                1  

#pragma  pack(1)

typedef struct 
{
 
  char  userNum[32];     // 用户号码
  char  acessNum[32];    // 接入号码
  char  reversed[64];    // 保留发送区
  WORD  bkey;            // 处理该消息的业务键 

}strODIACCESS;

#pragma  pack()

int   Deal_ODI_Msg(WORD  offset);
int   Start_Server_Msg(void);

#endif