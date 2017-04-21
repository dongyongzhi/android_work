#include <termios.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <string.h>
#include <jni.h>

#include "SerialPort.h"

#include "android/log.h"
static const char *TAG="yfdownload";
#define LOGI(fmt, args...) __android_log_print(ANDROID_LOG_INFO,  TAG, fmt, ##args)
#define LOGD(fmt, args...) __android_log_print(ANDROID_LOG_DEBUG, TAG, fmt, ##args)
#define LOGE(fmt, args...) __android_log_print(ANDROID_LOG_ERROR, TAG, fmt, ##args)

speed_t getBaudrate(jint baudrate)
{
        switch(baudrate) {
        case 0: return B0;
        case 50: return B50;
        case 75: return B75;
        case 110: return B110;
        case 134: return B134;
        case 150: return B150;
        case 200: return B200;
        case 300: return B300;
        case 600: return B600;
        case 1200: return B1200;
        case 1800: return B1800;
        case 2400: return B2400;
        case 4800: return B4800;
        case 9600: return B9600;
        case 19200: return B19200;
        case 38400: return B38400;
        case 57600: return B57600;
        case 115200: return B115200;
        case 230400: return B230400;
        case 460800: return B460800;
        case 500000: return B500000;
        case 576000: return B576000;
        case 921600: return B921600;
        case 1000000: return B1000000;
        case 1152000: return B1152000;
        case 1500000: return B1500000;
        case 2000000: return B2000000;
        case 2500000: return B2500000;
        case 3000000: return B3000000;
        case 3500000: return B3500000;
        case 4000000: return B4000000;
        default: return -1;
        }
}
/*
 * Class:     android_serialport_SerialPort
 * Method:    open
 * Signature: (Ljava/lang/String;II)Ljava/io/FileDescriptor;
 */
JNIEXPORT jint JNICALL Java_com_yfcomm_pos_SerialPort_open
  (JNIEnv *env, jclass thiz,jstring path,jint baudrate)
{
	    int fd;
        speed_t speed;
        // jobject mFileDescriptor;
        jboolean iscopy;
        const char *path_utf = (env)->GetStringUTFChars(path, &iscopy);

        fd = open( path_utf, O_RDWR|O_NOCTTY|O_NDELAY);
        (env)->ReleaseStringUTFChars(path, path_utf);
        if (fd == -1)
        {
           LOGE("Cannot open port");
           return 0;
        }
        if(fcntl(fd, F_SETFL, 0) < 0) {
               return 0;
        }

        return fd;
}
JNIEXPORT jint JNICALL Java_com_yfcomm_pos_SerialPort_set
  (JNIEnv *env, jclass thiz,jint fd, jint speed,jint flow_ctrl,jint databits,jint stopbits,jint parity){

	    int   i;
	    int   status;
	    speed_t speed_b;

	    struct termios options;

	    if ( tcgetattr( fd,&options)  !=  0)
	     {
	    	  LOGE("tcgetattr set failed");
	          return 0;
	     }

	    //设置串口输入波特率和输出波特率

	    speed_b = getBaudrate(speed);
	     if (speed_b == -1) {
	         LOGE("Invalid baudrate");
	         return 0;
	      }
	     cfsetispeed(&options,speed_b);
		 cfsetospeed(&options,speed_b);

	    //设置数据流控制
	    switch(flow_ctrl)
	    {

	       case 0 ://不使用流控制
	              options.c_cflag &= ~CRTSCTS;
	              break;

	       case 1 ://使用硬件流控制
	              options.c_cflag |= CRTSCTS;
	              break;
	       case 2 ://使用软件流控制
	              options.c_cflag |= IXON | IXOFF | IXANY;
	              break;
	    }

	    options.c_cflag &= ~CSIZE;
	    switch (databits)
	    {
	       case 5:
	            options.c_cflag |= CS5;
	            break;

	       case 6:

	           options.c_cflag |= CS6;
	           break;

	       case 7:
	            options.c_cflag |= CS7;
	            break;

	       case 8:
	           options.c_cflag |= CS8;
	           break;

	       default:
	                 return 0;
	    }
	    //设置校验位
	    switch (parity)
	    {
	       case 'n':
	       case 'N': //无奇偶校验位。
	                 options.c_cflag &= ~PARENB;
	                 options.c_iflag &= ~INPCK;
	                 break;
	       case 'o':
	       case 'O'://设置为奇校验
	                 options.c_cflag |= (PARODD | PARENB);
	                 options.c_iflag |= INPCK;
	                 break;
	       case 'e':
	       case 'E'://设置为偶校验
	                 options.c_cflag |= PARENB;
	                 options.c_cflag &= ~PARODD;
	                 options.c_iflag |= INPCK;
	                 break;
	       case 's':
	       case 'S': //设置为空格
	                 options.c_cflag &= ~PARENB;
	                 options.c_cflag &= ~CSTOPB;
	                 break;
	        default:
	                 return 0;
	    }
	    // 设置停止位
	    switch (stopbits)
	    {
	       case 1:
	                 options.c_cflag &= ~CSTOPB; break;
	       case 2:
	                 options.c_cflag |= CSTOPB; break;
	       default:
	          return 0;
	    }

	   options.c_iflag &= ~(BRKINT | ICRNL | INPCK | ISTRIP | IXON);
	   options.c_oflag &= ~OPOST;
	   options.c_cflag |= CLOCAL | CREAD;
	   options.c_lflag &= ~(ICANON | ECHO | ECHOE | ISIG);

	    //设置等待时间和最小接收字符
	    options.c_cc[VTIME] = 0; /* 读取一个字符等待1*(1/10)s */
	    options.c_cc[VMIN] = 0; /* 读取字符的最少个数为1 */

	    //激活配置 (将修改后的termios数据设置到串口中）
	    if (tcsetattr(fd,TCSANOW,&options) != 0)
	    {
	         return 0;
        }
	    return 1;
}


JNIEXPORT jint JNICALL Java_com_yfcomm_pos_SerialPort_send
  (JNIEnv *env, jclass thiz,jint fd, jbyteArray sendbuf, jint data_len){

    jbyte* buffer = (jbyte*) (env)->GetByteArrayElements(sendbuf, 0);
    int len = 0;
    len= write(fd,buffer,data_len);
    (env)->ReleaseByteArrayElements(sendbuf, buffer, 0);

    if (len == data_len){
    	return len;
    }
    else
    {
    	LOGE("send failed");
    }
    return 0;
}



unsigned int caldelaytime(jint speed,jint rcvMAxlen)
{
	unsigned int neadtime;

    neadtime= (rcvMAxlen*10000)/(speed/8); //微秒
	return neadtime;
}


JNIEXPORT jint JNICALL Java_com_yfcomm_pos_SerialPort_sel
(JNIEnv *env, jclass thiz,jint fd)
{
    int fs_sel;
	fd_set fs_read;

	struct timeval time;

	FD_ZERO(&fs_read);
	FD_SET(fd,&fs_read);

	time.tv_sec = 5;
	time.tv_usec = 0;

	fs_sel = select(fd+1,&fs_read,NULL,NULL,&time);      //使用select实现串口的多路通信

	return fs_sel;
}

JNIEXPORT jint JNICALL Java_com_yfcomm_pos_SerialPort_rcv
(JNIEnv *env, jclass thiz,jint fd, jbyteArray rcvbuf,jint data_len,jint speed)
{
	jbyte buffer[1024];
	int  len,maxlen;

	maxlen= data_len>1024 ? 1024:data_len;
    unsigned int  timer=caldelaytime(speed,data_len);
   // LOGE("Receive delay time:%u Microsecond",timer);
    usleep(timer);
    len = read(fd,buffer,maxlen);
    if(len<=0) return 0;
    env->SetByteArrayRegion(rcvbuf, 0, len, buffer);
    return len;
}


/*
 * Class:     cedric_serial_SerialPort
 * Method:    close
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_yfcomm_pos_SerialPort_close
  (JNIEnv *env, jobject thiz,jint  fd)
{
	 close(fd);
}
