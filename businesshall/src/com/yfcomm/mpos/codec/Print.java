package com.yfcomm.mpos.codec;



public class Print {

////////////////打印排版常量定义/////////////////////
//------------------打印字体定义-----------------
	public final static byte PNT_16X16 = 0x00;	
	public final static byte PNT_16X32 = 0x02; 
	public final static byte PNT_32X16 = 0x01;
	public final static byte PNT_32X32 = 0x03;
	public final static byte PNT_24X24 = 0x10;  // 正常使用
	public final static byte PNT_24X48 = 0x12;
	public final static byte PNT_48X24 = 0x11;
	public final static byte PNT_48X48 = 0x13;
	
//------------------打印错误码的定义-----------------
	public final static byte PNT_ERROR_NONE = 0;
	public final static byte PNT_ERROR_NO_PAPER=1;
	public final static byte PNT_ERROR_NO_DATA=2;
	public final static byte PNT_ERROR_NO_BLACK=3;
	public final static byte PNT_ERROR_FAIL=4;
	public final static byte PNT_ERROR_TEMP_HIGH=5;
	public final static byte PNT_ERROR_VOLTAGE_LOW=6;
	public final static byte PNT_ERROR_PAPER=7;
	public final static byte PNT_ERROR_FORMAT=8;

//------------------打印命令-----------------	
	public final static byte COM_PIC_INDEX=0x21;	// 01 PC 			图片序号
	public final static byte COM_ASCC=0x22; 	// 00 ASCC			ASCC文字信息	   注意ASCC可以不使用协议，通过超时或下一个的命令判断
	public final static byte COM_QR_CODE=0x23;	// 03 二维码直接打印
	public final static byte COM_STEPS=0x24;	  	// 02 steps			步进
	
//------------------打印函数的其它常量----------------	
	private final static byte ERORR_GROUP =0x31;		// 组数溢出 最大数组	
	private final static byte PRINT_GROUP_MAX = 20;			// 最大打印组数
	private final static short PRINT_BUF_MAX_LEN = 1024;		// 存储数buf长度
	
//////////////打印排版变量定义/////////////////////	
//----------------打印内容临时存放--------------------	
	static byte g_printBuf[]= new byte[PRINT_BUF_MAX_LEN];		// 存储数buf

//----------------打印结构的包数-------------------------
	static byte pack_number;							// 信息包组数
	
	class PrintInfor{
	//----------------打印数组组包的结构--------------------
		byte printMode;						// 打印格式
		byte printOffset;						// offset
		byte CFontsize;				// 打印字体
		short printLen;						// len
		short printPtr;						// ptr
		public int setPrintPtr;
		public byte getPrintMode() {
			return printMode;
		}
		public void setPrintMode(byte printMode) {
			this.printMode = printMode;
		}
		public byte getPrintOffset() {
			return printOffset;
		}
		public void setPrintOffset(byte printOffset) {
			this.printOffset = printOffset;
		}
		public byte getCFontsize() {
			return CFontsize;
		}
		public void setCFontsize(byte cFontsize) {
			CFontsize = cFontsize;
		}
		public short getPrintLen() {
			return printLen;
		}
		public void setPrintLen(short printLen) {
			this.printLen = printLen;
		}
		public short getPrintPtr() {
			return printPtr;
		}
		public void setPrintPtr(short printPtr) {
			this.printPtr = printPtr;
		}
		public PrintInfor(){
			
		}
	}

	PrintInfor[] g_printInfor;
	public Print() {
		g_printInfor = new PrintInfor[PRINT_GROUP_MAX];
		for (int i = 0; i < g_printInfor.length; i++) {
			g_printInfor[i] = new PrintInfor();
		}
	}

	//-------清除打印信息------------
	public void PRINT_clear()
	{
		pack_number = 0;
		short i;
		for (i=0;i<PRINT_GROUP_MAX;i++)
		{
			g_printInfor[i].setPrintPtr = 0;
		}
//		memset(g_printBuf,0,sizeof(g_printBuf));
		for (i=0;i<g_printBuf.length;i++)
			g_printBuf[i] = 0;
		
//		g_printBuf[0] = 0;
	}
	
	//--------增加图片序号--------
	public byte PRINT_Add_picture(byte offset,byte pictureNumber)
	{
		if (pack_number < PRINT_GROUP_MAX)
		{
			g_printInfor[pack_number].setPrintMode(COM_PIC_INDEX);
			g_printInfor[pack_number].setPrintOffset(offset);
			g_printInfor[pack_number].setCFontsize((byte)0);
			if (pack_number == 0)
			{
				g_printInfor[pack_number].setPrintPtr((byte)0);
				g_printBuf[0] = pictureNumber;
				g_printInfor[pack_number].setPrintLen((byte)1);
			}
			else
			{
				g_printInfor[pack_number].setPrintPtr((short)(g_printInfor[pack_number-1].getPrintPtr()+g_printInfor[pack_number-1].getPrintLen()));
				g_printBuf[g_printInfor[pack_number].getPrintPtr()] = pictureNumber;
				g_printInfor[pack_number].setPrintLen((short)1);
			}
			pack_number++;
		}
		else
			return ERORR_GROUP;// 组数溢出
		return 1;
	}
	
	//---------增加走纸步进--------
	public byte PRINT_Add_setp(short setp)
	{
		if (pack_number < PRINT_GROUP_MAX)
		{
			g_printInfor[pack_number].setPrintMode(COM_STEPS);
			g_printInfor[pack_number].setPrintOffset((byte)0);
			g_printInfor[pack_number].setCFontsize((byte)0);
			if (pack_number == 0)
			{
				g_printInfor[pack_number].setPrintPtr((byte)0);
				g_printBuf[0] = (byte)((setp>>8)&0xff);
				g_printBuf[1] = (byte)(setp&0xff);
				g_printInfor[pack_number].setPrintLen((short)2);
			}
			else
			{
				g_printInfor[pack_number].setPrintPtr((short)(g_printInfor[pack_number-1].getPrintPtr() + g_printInfor[pack_number-1].getPrintLen()));
				g_printBuf[g_printInfor[pack_number].getPrintPtr()] = (byte)((setp>>8)&0xff);
				g_printBuf[g_printInfor[pack_number].getPrintPtr() + 1] = (byte)(setp&0xff);
				g_printInfor[pack_number].setPrintLen((short)2);
			}
			pack_number++;
		}
		else
			return ERORR_GROUP;// 组数溢出
		return 1;
	}	
	
	

	//----------增加字符信息-------
	public byte PRINT_Add_character(byte offset,byte CFontsize,byte[] data,short len)
	{
		if (pack_number < PRINT_GROUP_MAX)
		{

			if (pack_number == 0)
			{
				g_printInfor[pack_number].setPrintPtr((short)0);
				for (short i=0;i<len;i++)
					g_printBuf[i] = data[i];
				g_printInfor[pack_number].setPrintLen(len);

				g_printInfor[pack_number].setPrintMode(COM_ASCC);
				g_printInfor[pack_number].setPrintOffset(offset);
				g_printInfor[pack_number].setCFontsize(CFontsize);
				pack_number++;
			}
			else
			{
				if ((g_printInfor[pack_number-1].getPrintMode() == COM_ASCC)&&(g_printInfor[pack_number-1].getCFontsize() == CFontsize)&&offset==0)
				{
					short offsetptr=0;

					offsetptr = (short)(g_printInfor[pack_number-1].getPrintPtr() + g_printInfor[pack_number-1].getPrintLen());
//					memcpy(&g_printBuf[offsetptr],data,len);
					for (short i=0;i<len;i++)
						g_printBuf[offsetptr+i] = data[i];

					g_printBuf[offsetptr+len] = (byte)13; // 0x0d  换行
					len += 1;
					
					g_printInfor[pack_number-1].setPrintLen((short)(g_printInfor[pack_number-1].getPrintLen() + len));
				}
				else
				{
					g_printInfor[pack_number].setPrintPtr((short)(g_printInfor[pack_number-1].getPrintPtr() + g_printInfor[pack_number-1].getPrintLen()));
				//	memcpy(&g_printBuf[g_printInfor[pack_number].ptr],data,len);
					for (short i=0;i<len;i++)
						g_printBuf[g_printInfor[pack_number].getPrintPtr() + i] = data[i];
					
					g_printBuf[g_printInfor[pack_number].getPrintPtr() + len] = (byte)13; // 0x0d  换行
					len += 1;
					
					g_printInfor[pack_number].setPrintLen(len);//len = len;
					g_printInfor[pack_number].setPrintMode(COM_ASCC);//mode = COM_ASCC;
					g_printInfor[pack_number].setPrintOffset(offset);//offset = offset;
					g_printInfor[pack_number].setCFontsize(CFontsize);//CFontsize = CFontsize;
					pack_number++;
				}
			}
		}
		else{
			System.out.println("组数溢出");
			return ERORR_GROUP;// 组数溢出
		}
		return 1;
	}
	
	//----------增加二维码信息--------
	public byte PRINT_Add_2Dpicture(byte offset,byte[] data,byte len)
	{
		if (pack_number < PRINT_GROUP_MAX)
		{
			g_printInfor[pack_number].setPrintMode(COM_QR_CODE);//mode = COM_QR_CODE;
			g_printInfor[pack_number].setPrintOffset(offset);//offset = offset;
			g_printInfor[pack_number].setCFontsize((byte)0);//CFontsize = 0;
			if (pack_number == 0)
			{
				g_printInfor[pack_number].setPrintPtr((short)(0));//ptr = 0;
//				memcpy(g_printBuf,data,len);
				for (short i=0;i<len;i++)
					g_printBuf[i] = data[i];
				g_printInfor[pack_number].setPrintLen(len);//len = len;
			}
			else
			{
				g_printInfor[pack_number].setPrintPtr((short)(g_printInfor[pack_number-1].getPrintPtr() + g_printInfor[pack_number-1].getPrintLen()));
		//		memcpy(&g_printBuf[g_printInfor[pack_number].ptr],data,len);
				for (short i=0;i<len;i++)
					g_printBuf[g_printInfor[pack_number].getPrintPtr() + i] = data[i];
				g_printInfor[pack_number].setPrintLen(len);//len = len;
			}
			pack_number++;
		}
		else
			return ERORR_GROUP;// 组数溢出
		return 1;
	}	
	
	/*
	CMD + Offset + Fontsize + Len  +  Ptr
	每条指令Data由offset(1字节)+fontsize(1字节)+len(2字节)+ptr组成
	offset	fontsize	len	ptr

	Offset：  偏移量，1字节
	Fontsize：字体大小，1字节
	Le   数据长度，2字节
	Ptr：    数据内容
	*/

	//--------组包打印数据体--------------
	public short PRINT_packages(byte[] dst)
	{
		short i;
		short ptr=0;

		for (i=0;i<pack_number;i++)
		{
			switch(g_printInfor[i].getPrintMode())
			{
				case COM_ASCC:// = 0, 	// 00 ASCC			ASCC文字信息	   注意ASCC可以不使用协议，通过超时或下一个的命令判断
				dst[ptr++] = g_printInfor[i].getPrintMode();//mode;	// CMD
				dst[ptr++] = g_printInfor[i].getPrintOffset();//offset;	// offset
				dst[ptr++] = g_printInfor[i].getCFontsize();//CFontsize;	// fontsize
				dst[ptr++] = (byte)((g_printInfor[i].getPrintLen()>>8)&0xff);	// len
				dst[ptr++] = (byte)(g_printInfor[i].getPrintLen()&0xff);	// len
		//		memcpy(&dst[ptr],&g_printBuf[g_printInfor[i].ptr],g_printInfor[i].len);
				for (short j=0;j<g_printInfor[i].getPrintLen();j++)
					dst[ptr++] = g_printBuf[g_printInfor[i].getPrintPtr() + j];
			//	ptr += g_printInfor[i].len;
				break;
				//////////////////////////////////
				case COM_PIC_INDEX://,	// 01 PC 			图片序号				dst[ptr++] = g_printInfor[i].mode;	// CMD
				dst[ptr++] = g_printInfor[i].getPrintMode();//mode;	// CMD
				dst[ptr++] = g_printInfor[i].getPrintOffset();//offset;	// offset
				dst[ptr++] = g_printInfor[i].getCFontsize();//CFontsize;	// fontsize
				dst[ptr++] = (byte)((g_printInfor[i].getPrintLen()>>8)&0xff);	// len
				dst[ptr++] = (byte)(g_printInfor[i].getPrintLen()&0xff);	// len
		//		memcpy(&dst[ptr],&g_printBuf[g_printInfor[i].ptr],g_printInfor[i].len);
				for (short j=0;j<g_printInfor[i].getPrintLen();j++)
					dst[ptr++] = g_printBuf[g_printInfor[i].getPrintPtr() + j];
			//	ptr += g_printInfor[i].len;
				break;
				///////////////////////////////////////
				case COM_STEPS://,	  	// 02 steps			步进					dst[ptr++] = g_printInfor[i].mode;	// CMD
				dst[ptr++] = g_printInfor[i].getPrintMode();//mode;	// CMD
				dst[ptr++] = g_printInfor[i].getPrintOffset();//offset;	// offset
				dst[ptr++] = g_printInfor[i].getCFontsize();//CFontsize;	// fontsize
				dst[ptr++] = (byte)((g_printInfor[i].getPrintLen()>>8)&0xff);	// len
				dst[ptr++] = (byte)(g_printInfor[i].getPrintLen()&0xff);	// len
		//		memcpy(&dst[ptr],&g_printBuf[g_printInfor[i].ptr],g_printInfor[i].len);
				for (short j=0;j<g_printInfor[i].getPrintLen();j++)
					dst[ptr++] = g_printBuf[g_printInfor[i].getPrintPtr() + j];
			//	ptr += g_printInfor[i].len;
				break;
				case COM_QR_CODE://,	// 03 二维码直接打印
				dst[ptr++] = g_printInfor[i].getPrintMode();//mode;	// CMD
				dst[ptr++] = g_printInfor[i].getPrintOffset();//offset;	// offset
				dst[ptr++] = g_printInfor[i].getCFontsize();//CFontsize;	// fontsize
				dst[ptr++] = (byte)((g_printInfor[i].getPrintLen()>>8)&0xff);	// len
				dst[ptr++] = (byte)(g_printInfor[i].getPrintLen()&0xff);	// len
		//		memcpy(&dst[ptr],&g_printBuf[g_printInfor[i].ptr],g_printInfor[i].len);
				for (short j=0;j<g_printInfor[i].getPrintLen();j++)
					dst[ptr++] = g_printBuf[g_printInfor[i].getPrintPtr() + j];
			//	ptr += g_printInfor[i].len;
				break;
				//////////////////////////
				default :
				break;
			}
		}
		return ptr;
	}
	
}
