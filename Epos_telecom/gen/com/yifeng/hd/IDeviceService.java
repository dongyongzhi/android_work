/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: E:\\JAVA_PRO\\Epos_telecom\\src\\com\\yifeng\\hd\\IDeviceService.aidl
 */
package com.yifeng.hd;
public interface IDeviceService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.yifeng.hd.IDeviceService
{
private static final java.lang.String DESCRIPTOR = "com.yifeng.hd.IDeviceService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.yifeng.hd.IDeviceService interface,
 * generating a proxy if needed.
 */
public static com.yifeng.hd.IDeviceService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.yifeng.hd.IDeviceService))) {
return ((com.yifeng.hd.IDeviceService)iin);
}
return new com.yifeng.hd.IDeviceService.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_init:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.init();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_initForMAC:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
int _result = this.initForMAC(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_initForName:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
int _result = this.initForName(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getVersion:
{
data.enforceInterface(DESCRIPTOR);
byte[] _arg0;
int _arg0_length = data.readInt();
if ((_arg0_length<0)) {
_arg0 = null;
}
else {
_arg0 = new byte[_arg0_length];
}
int _result = this.getVersion(_arg0);
reply.writeNoException();
reply.writeInt(_result);
reply.writeByteArray(_arg0);
return true;
}
case TRANSACTION_getPOSAddress:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _result = this.getPOSAddress();
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_clearScreen:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.clearScreen();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_clearScreenLine:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _result = this.clearScreenLine(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_screenDisplay:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
java.lang.String _arg2;
_arg2 = data.readString();
int _result = this.screenDisplay(_arg0, _arg1, _arg2);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_printASCII:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
java.util.List<com.yifeng.hd.FontStyle> _arg2;
_arg2 = data.createTypedArrayList(com.yifeng.hd.FontStyle.CREATOR);
int _result = this.printASCII(_arg0, _arg1, _arg2);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_printBitmap:
{
data.enforceInterface(DESCRIPTOR);
byte[] _arg0;
_arg0 = data.createByteArray();
int _result = this.printBitmap(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_printBarcode:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
boolean _arg2;
_arg2 = (0!=data.readInt());
byte[] _arg3;
_arg3 = data.createByteArray();
int _result = this.printBarcode(_arg0, _arg1, _arg2, _arg3);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_movePaper:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _result = this.movePaper(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_readIC:
{
data.enforceInterface(DESCRIPTOR);
byte[] _arg0;
int _arg0_length = data.readInt();
if ((_arg0_length<0)) {
_arg0 = null;
}
else {
_arg0 = new byte[_arg0_length];
}
int _result = this.readIC(_arg0);
reply.writeNoException();
reply.writeInt(_result);
reply.writeByteArray(_arg0);
return true;
}
case TRANSACTION_readRFID:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
int _arg2;
_arg2 = data.readInt();
byte[] _arg3;
int _arg3_length = data.readInt();
if ((_arg3_length<0)) {
_arg3 = null;
}
else {
_arg3 = new byte[_arg3_length];
}
int _result = this.readRFID(_arg0, _arg1, _arg2, _arg3);
reply.writeNoException();
reply.writeInt(_result);
reply.writeByteArray(_arg3);
return true;
}
case TRANSACTION_readBankCard:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
byte[] _arg1;
int _arg1_length = data.readInt();
if ((_arg1_length<0)) {
_arg1 = null;
}
else {
_arg1 = new byte[_arg1_length];
}
int _result = this.readBankCard(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(_result);
reply.writeByteArray(_arg1);
return true;
}
case TRANSACTION_setWaitTimeout:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _result = this.setWaitTimeout(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getMoney:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
int _arg2;
_arg2 = data.readInt();
int _arg3;
_arg3 = data.readInt();
byte[] _arg4;
int _arg4_length = data.readInt();
if ((_arg4_length<0)) {
_arg4 = null;
}
else {
_arg4 = new byte[_arg4_length];
}
int _result = this.getMoney(_arg0, _arg1, _arg2, _arg3, _arg4);
reply.writeNoException();
reply.writeInt(_result);
reply.writeByteArray(_arg4);
return true;
}
case TRANSACTION_displayASCII:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
byte[] _arg2;
_arg2 = data.createByteArray();
int _result = this.displayASCII(_arg0, _arg1, _arg2);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_displayDotMatrix:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
int _arg2;
_arg2 = data.readInt();
int _arg3;
_arg3 = data.readInt();
byte[] _arg4;
_arg4 = data.createByteArray();
int _result = this.displayDotMatrix(_arg0, _arg1, _arg2, _arg3, _arg4);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_downLoadLogo:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
byte[] _arg1;
_arg1 = data.createByteArray();
int _result = this.downLoadLogo(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_transRequest:
{
data.enforceInterface(DESCRIPTOR);
com.yifeng.hd.TransRequest _arg0;
if ((0!=data.readInt())) {
_arg0 = com.yifeng.hd.TransRequest.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
byte[] _arg1;
int _arg1_length = data.readInt();
if ((_arg1_length<0)) {
_arg1 = null;
}
else {
_arg1 = new byte[_arg1_length];
}
int _result = this.transRequest(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(_result);
reply.writeByteArray(_arg1);
return true;
}
case TRANSACTION_transResponse:
{
data.enforceInterface(DESCRIPTOR);
byte[] _arg0;
_arg0 = data.createByteArray();
byte[] _arg1;
int _arg1_length = data.readInt();
if ((_arg1_length<0)) {
_arg1 = null;
}
else {
_arg1 = new byte[_arg1_length];
}
int _result = this.transResponse(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(_result);
reply.writeByteArray(_arg1);
return true;
}
case TRANSACTION_transParams:
{
data.enforceInterface(DESCRIPTOR);
byte[] _arg0;
_arg0 = data.createByteArray();
byte[] _arg1;
int _arg1_length = data.readInt();
if ((_arg1_length<0)) {
_arg1 = null;
}
else {
_arg1 = new byte[_arg1_length];
}
int _result = this.transParams(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(_result);
reply.writeByteArray(_arg1);
return true;
}
case TRANSACTION_printLastTrans:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.printLastTrans();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_close:
{
data.enforceInterface(DESCRIPTOR);
this.close();
reply.writeNoException();
return true;
}
case TRANSACTION_endCmd:
{
data.enforceInterface(DESCRIPTOR);
this.endCmd();
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.yifeng.hd.IDeviceService
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
/**
    * 初始化设备
    */
@Override public int init() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_init, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
    * 用mac初始化设备
    */
@Override public int initForMAC(java.lang.String mac) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(mac);
mRemote.transact(Stub.TRANSACTION_initForMAC, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public int initForName(java.lang.String name) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(name);
mRemote.transact(Stub.TRANSACTION_initForName, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
     * 读取终端版本号
     * @param version  版本内容
     */
@Override public int getVersion(byte[] version) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((version==null)) {
_data.writeInt(-1);
}
else {
_data.writeInt(version.length);
}
mRemote.transact(Stub.TRANSACTION_getVersion, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
_reply.readByteArray(version);
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
     * 获取pos地址
     */
@Override public java.lang.String getPOSAddress() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getPOSAddress, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
	 * 清屏
	 */
@Override public int clearScreen() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_clearScreen, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
	 * 清除指定行
	 *@param line      第几行
	 */
@Override public int clearScreenLine(int line) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(line);
mRemote.transact(Stub.TRANSACTION_clearScreenLine, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
	 * 在终端屏幕上显示字符串
	 * @param line   行号（0 – 3） （每行占16像素高）
	 * @param col    列号（0 – 127）（单位：像素）
	 * @param str    需显示的字符串 
	 * 
	 */
@Override public int screenDisplay(int line, int col, java.lang.String str) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(line);
_data.writeInt(col);
_data.writeString(str);
mRemote.transact(Stub.TRANSACTION_screenDisplay, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
	 *  文字打印
	 * <li>编码为GB2312</li>
	 *
	 * @param offset   打印内容在打印纸上的偏移位置
	 * @param size     字体大小     2种大小（0：16*16,1:24*24）
	 * @param info     要打印的内容，每个元素为一行
	 * @return   返回应答码
	 */
@Override public int printASCII(int offset, int size, java.util.List<com.yifeng.hd.FontStyle> info) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(offset);
_data.writeInt(size);
_data.writeTypedList(info);
mRemote.transact(Stub.TRANSACTION_printASCII, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
     * 图片打印
     * @param str   位图数据
     */
@Override public int printBitmap(byte[] bitmap) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeByteArray(bitmap);
mRemote.transact(Stub.TRANSACTION_printBitmap, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
     * 打印条形码 
     * <li>目前支持打印商品码EAN-13、code 128码（A集中的特殊ASCLL不支持） 、code 39码</li>
     * @param offset   打印条形码在打印纸上的偏移位置  (0 -- 0x01 0x80 )
     * @param type     打印类型 0: 128码 ,1: 39码 , 2: 商品码  
     * @param check    打印内容包括检验
     * @param barcode  打印内容   GB2312 编码
     */
@Override public int printBarcode(int offset, int type, boolean check, byte[] barcode) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(offset);
_data.writeInt(type);
_data.writeInt(((check)?(1):(0)));
_data.writeByteArray(barcode);
mRemote.transact(Stub.TRANSACTION_printBarcode, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
	 * 走纸
	 * @param step  步进数
	 */
@Override public int movePaper(int step) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(step);
mRemote.transact(Stub.TRANSACTION_movePaper, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
	 * IC卡读取
	 * @param  ic  ic卡内容
	 * @return 
	 */
@Override public int readIC(byte[] ic) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((ic==null)) {
_data.writeInt(-1);
}
else {
_data.writeInt(ic.length);
}
mRemote.transact(Stub.TRANSACTION_readIC, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
_reply.readByteArray(ic);
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
	 * 读取 RFID 数据
	 * @param row   显示键值起始行(0-4)
	 * @param col   显示键值起始列(1-0x80)
	 * @param type  0 读取卡号  1  读取二、三磁道所有数据  
	 * @param data   返回码 和内容
	 */
@Override public int readRFID(int row, int col, int type, byte[] data) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(row);
_data.writeInt(col);
_data.writeInt(type);
if ((data==null)) {
_data.writeInt(-1);
}
else {
_data.writeInt(data.length);
}
mRemote.transact(Stub.TRANSACTION_readRFID, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
_reply.readByteArray(data);
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
	 * 刷卡和输密码
	 * @param type  0 读取卡号  1  读取二、三磁道所有数据  
	 * @param data   返回码 和内容
	 */
@Override public int readBankCard(int type, byte[] data) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(type);
if ((data==null)) {
_data.writeInt(-1);
}
else {
_data.writeInt(data.length);
}
mRemote.transact(Stub.TRANSACTION_readBankCard, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
_reply.readByteArray(data);
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
	 * 设置超时时长   
	 * @param time  单位秒
	 * @return
	 */
@Override public int setWaitTimeout(int time) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(time);
mRemote.transact(Stub.TRANSACTION_setWaitTimeout, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
	 * 获取金额  
	 * @param row    显示键值起始行 (1-128)
     * @param col    显示键值起始列 (0-4)
     * @param min    最小长度
     * @param max    最大长度 (<=12)
	 * @param data    输入金额 单位为分
	 */
@Override public int getMoney(int row, int col, int min, int max, byte[] data) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(row);
_data.writeInt(col);
_data.writeInt(min);
_data.writeInt(max);
if ((data==null)) {
_data.writeInt(-1);
}
else {
_data.writeInt(data.length);
}
mRemote.transact(Stub.TRANSACTION_getMoney, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
_reply.readByteArray(data);
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
	 * 显示字符串
	 * @param line    显示起始行（0--4）
     * @param col     显示起始列（1--0x80）
	 * @param data    显示内容 GB2312
	 */
@Override public int displayASCII(int line, int col, byte[] data) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(line);
_data.writeInt(col);
_data.writeByteArray(data);
mRemote.transact(Stub.TRANSACTION_displayASCII, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
	 * 显示点阵
	 * @param bline    显示起始行（0--4）
     * @param bcol     显示起始列（1--0x80）
     * @param eline    显示结束行（1--0x80）
     * @param ecol     显示结束列（1--0x80）
	 * @param data     显示数据
	 */
@Override public int displayDotMatrix(int bline, int bcol, int eline, int ecol, byte[] data) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(bline);
_data.writeInt(bcol);
_data.writeInt(eline);
_data.writeInt(ecol);
_data.writeByteArray(data);
mRemote.transact(Stub.TRANSACTION_displayDotMatrix, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
	 * 下载logo至设备
	 * @param len     数据长度
	 * @param data    logo内容
	 */
@Override public int downLoadLogo(int len, byte[] data) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(len);
_data.writeByteArray(data);
mRemote.transact(Stub.TRANSACTION_downLoadLogo, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
	 * 交易请求接口
	 * @param messageType  交易类型
	 * @param inData       输入数据
	 * @param outData      输出数据
	 */
@Override public int transRequest(com.yifeng.hd.TransRequest req, byte[] outData) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((req!=null)) {
_data.writeInt(1);
req.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
if ((outData==null)) {
_data.writeInt(-1);
}
else {
_data.writeInt(outData.length);
}
mRemote.transact(Stub.TRANSACTION_transRequest, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
_reply.readByteArray(outData);
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
	 * 交易应答报文解析接口
	 * @param inData     输入数据
	 * @param outData    输出数据
	 */
@Override public int transResponse(byte[] inData, byte[] outData) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeByteArray(inData);
if ((outData==null)) {
_data.writeInt(-1);
}
else {
_data.writeInt(outData.length);
}
mRemote.transact(Stub.TRANSACTION_transResponse, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
_reply.readByteArray(outData);
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
	 * 交易参数配置
	 * @param inData     读取数据
	 */
@Override public int transParams(byte[] inData, byte[] outData) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeByteArray(inData);
if ((outData==null)) {
_data.writeInt(-1);
}
else {
_data.writeInt(outData.length);
}
mRemote.transact(Stub.TRANSACTION_transParams, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
_reply.readByteArray(outData);
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
	 * 打印最后一笔交易
	 */
@Override public int printLastTrans() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_printLastTrans, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public void close() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_close, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/**
	 * 结束 命令
	 */
@Override public void endCmd() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_endCmd, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_init = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_initForMAC = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_initForName = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_getVersion = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_getPOSAddress = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_clearScreen = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
static final int TRANSACTION_clearScreenLine = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
static final int TRANSACTION_screenDisplay = (android.os.IBinder.FIRST_CALL_TRANSACTION + 7);
static final int TRANSACTION_printASCII = (android.os.IBinder.FIRST_CALL_TRANSACTION + 8);
static final int TRANSACTION_printBitmap = (android.os.IBinder.FIRST_CALL_TRANSACTION + 9);
static final int TRANSACTION_printBarcode = (android.os.IBinder.FIRST_CALL_TRANSACTION + 10);
static final int TRANSACTION_movePaper = (android.os.IBinder.FIRST_CALL_TRANSACTION + 11);
static final int TRANSACTION_readIC = (android.os.IBinder.FIRST_CALL_TRANSACTION + 12);
static final int TRANSACTION_readRFID = (android.os.IBinder.FIRST_CALL_TRANSACTION + 13);
static final int TRANSACTION_readBankCard = (android.os.IBinder.FIRST_CALL_TRANSACTION + 14);
static final int TRANSACTION_setWaitTimeout = (android.os.IBinder.FIRST_CALL_TRANSACTION + 15);
static final int TRANSACTION_getMoney = (android.os.IBinder.FIRST_CALL_TRANSACTION + 16);
static final int TRANSACTION_displayASCII = (android.os.IBinder.FIRST_CALL_TRANSACTION + 17);
static final int TRANSACTION_displayDotMatrix = (android.os.IBinder.FIRST_CALL_TRANSACTION + 18);
static final int TRANSACTION_downLoadLogo = (android.os.IBinder.FIRST_CALL_TRANSACTION + 19);
static final int TRANSACTION_transRequest = (android.os.IBinder.FIRST_CALL_TRANSACTION + 20);
static final int TRANSACTION_transResponse = (android.os.IBinder.FIRST_CALL_TRANSACTION + 21);
static final int TRANSACTION_transParams = (android.os.IBinder.FIRST_CALL_TRANSACTION + 22);
static final int TRANSACTION_printLastTrans = (android.os.IBinder.FIRST_CALL_TRANSACTION + 23);
static final int TRANSACTION_close = (android.os.IBinder.FIRST_CALL_TRANSACTION + 24);
static final int TRANSACTION_endCmd = (android.os.IBinder.FIRST_CALL_TRANSACTION + 25);
}
/**
    * 初始化设备
    */
public int init() throws android.os.RemoteException;
/**
    * 用mac初始化设备
    */
public int initForMAC(java.lang.String mac) throws android.os.RemoteException;
public int initForName(java.lang.String name) throws android.os.RemoteException;
/**
     * 读取终端版本号
     * @param version  版本内容
     */
public int getVersion(byte[] version) throws android.os.RemoteException;
/**
     * 获取pos地址
     */
public java.lang.String getPOSAddress() throws android.os.RemoteException;
/**
	 * 清屏
	 */
public int clearScreen() throws android.os.RemoteException;
/**
	 * 清除指定行
	 *@param line      第几行
	 */
public int clearScreenLine(int line) throws android.os.RemoteException;
/**
	 * 在终端屏幕上显示字符串
	 * @param line   行号（0 – 3） （每行占16像素高）
	 * @param col    列号（0 – 127）（单位：像素）
	 * @param str    需显示的字符串 
	 * 
	 */
public int screenDisplay(int line, int col, java.lang.String str) throws android.os.RemoteException;
/**
	 *  文字打印
	 * <li>编码为GB2312</li>
	 *
	 * @param offset   打印内容在打印纸上的偏移位置
	 * @param size     字体大小     2种大小（0：16*16,1:24*24）
	 * @param info     要打印的内容，每个元素为一行
	 * @return   返回应答码
	 */
public int printASCII(int offset, int size, java.util.List<com.yifeng.hd.FontStyle> info) throws android.os.RemoteException;
/**
     * 图片打印
     * @param str   位图数据
     */
public int printBitmap(byte[] bitmap) throws android.os.RemoteException;
/**
     * 打印条形码 
     * <li>目前支持打印商品码EAN-13、code 128码（A集中的特殊ASCLL不支持） 、code 39码</li>
     * @param offset   打印条形码在打印纸上的偏移位置  (0 -- 0x01 0x80 )
     * @param type     打印类型 0: 128码 ,1: 39码 , 2: 商品码  
     * @param check    打印内容包括检验
     * @param barcode  打印内容   GB2312 编码
     */
public int printBarcode(int offset, int type, boolean check, byte[] barcode) throws android.os.RemoteException;
/**
	 * 走纸
	 * @param step  步进数
	 */
public int movePaper(int step) throws android.os.RemoteException;
/**
	 * IC卡读取
	 * @param  ic  ic卡内容
	 * @return 
	 */
public int readIC(byte[] ic) throws android.os.RemoteException;
/**
	 * 读取 RFID 数据
	 * @param row   显示键值起始行(0-4)
	 * @param col   显示键值起始列(1-0x80)
	 * @param type  0 读取卡号  1  读取二、三磁道所有数据  
	 * @param data   返回码 和内容
	 */
public int readRFID(int row, int col, int type, byte[] data) throws android.os.RemoteException;
/**
	 * 刷卡和输密码
	 * @param type  0 读取卡号  1  读取二、三磁道所有数据  
	 * @param data   返回码 和内容
	 */
public int readBankCard(int type, byte[] data) throws android.os.RemoteException;
/**
	 * 设置超时时长   
	 * @param time  单位秒
	 * @return
	 */
public int setWaitTimeout(int time) throws android.os.RemoteException;
/**
	 * 获取金额  
	 * @param row    显示键值起始行 (1-128)
     * @param col    显示键值起始列 (0-4)
     * @param min    最小长度
     * @param max    最大长度 (<=12)
	 * @param data    输入金额 单位为分
	 */
public int getMoney(int row, int col, int min, int max, byte[] data) throws android.os.RemoteException;
/**
	 * 显示字符串
	 * @param line    显示起始行（0--4）
     * @param col     显示起始列（1--0x80）
	 * @param data    显示内容 GB2312
	 */
public int displayASCII(int line, int col, byte[] data) throws android.os.RemoteException;
/**
	 * 显示点阵
	 * @param bline    显示起始行（0--4）
     * @param bcol     显示起始列（1--0x80）
     * @param eline    显示结束行（1--0x80）
     * @param ecol     显示结束列（1--0x80）
	 * @param data     显示数据
	 */
public int displayDotMatrix(int bline, int bcol, int eline, int ecol, byte[] data) throws android.os.RemoteException;
/**
	 * 下载logo至设备
	 * @param len     数据长度
	 * @param data    logo内容
	 */
public int downLoadLogo(int len, byte[] data) throws android.os.RemoteException;
/**
	 * 交易请求接口
	 * @param messageType  交易类型
	 * @param inData       输入数据
	 * @param outData      输出数据
	 */
public int transRequest(com.yifeng.hd.TransRequest req, byte[] outData) throws android.os.RemoteException;
/**
	 * 交易应答报文解析接口
	 * @param inData     输入数据
	 * @param outData    输出数据
	 */
public int transResponse(byte[] inData, byte[] outData) throws android.os.RemoteException;
/**
	 * 交易参数配置
	 * @param inData     读取数据
	 */
public int transParams(byte[] inData, byte[] outData) throws android.os.RemoteException;
/**
	 * 打印最后一笔交易
	 */
public int printLastTrans() throws android.os.RemoteException;
public void close() throws android.os.RemoteException;
/**
	 * 结束 命令
	 */
public void endCmd() throws android.os.RemoteException;
}
