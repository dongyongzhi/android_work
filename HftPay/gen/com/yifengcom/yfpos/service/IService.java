/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: E:\\eclipse\\����POS\\HftPay\\src\\com\\yifengcom\\yfpos\\service\\IService.aidl
 */
package com.yifengcom.yfpos.service;
public interface IService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.yifengcom.yfpos.service.IService
{
private static final java.lang.String DESCRIPTOR = "com.yifengcom.yfpos.service.IService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.yifengcom.yfpos.service.IService interface,
 * generating a proxy if needed.
 */
public static com.yifengcom.yfpos.service.IService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.yifengcom.yfpos.service.IService))) {
return ((com.yifengcom.yfpos.service.IService)iin);
}
return new com.yifengcom.yfpos.service.IService.Stub.Proxy(obj);
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
case TRANSACTION_registerICallback:
{
data.enforceInterface(DESCRIPTOR);
com.yifengcom.yfpos.service.ICallBack _arg0;
_arg0 = com.yifengcom.yfpos.service.ICallBack.Stub.asInterface(data.readStrongBinder());
this.registerICallback(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_unregisterICallback:
{
data.enforceInterface(DESCRIPTOR);
com.yifengcom.yfpos.service.ICallBack _arg0;
_arg0 = com.yifengcom.yfpos.service.ICallBack.Stub.asInterface(data.readStrongBinder());
this.unregisterICallback(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_onGetDeviceInfo:
{
data.enforceInterface(DESCRIPTOR);
com.yifengcom.yfpos.service.ICallBack _arg0;
_arg0 = com.yifengcom.yfpos.service.ICallBack.Stub.asInterface(data.readStrongBinder());
this.onGetDeviceInfo(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_setDeviceData:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
java.lang.String _arg2;
_arg2 = data.readString();
java.lang.String _arg3;
_arg3 = data.readString();
com.yifengcom.yfpos.service.ICallBack _arg4;
_arg4 = com.yifengcom.yfpos.service.ICallBack.Stub.asInterface(data.readStrongBinder());
this.setDeviceData(_arg0, _arg1, _arg2, _arg3, _arg4);
reply.writeNoException();
return true;
}
case TRANSACTION_onPrint:
{
data.enforceInterface(DESCRIPTOR);
byte[] _arg0;
_arg0 = data.createByteArray();
com.yifengcom.yfpos.service.ICallBack _arg1;
_arg1 = com.yifengcom.yfpos.service.ICallBack.Stub.asInterface(data.readStrongBinder());
this.onPrint(_arg0, _arg1);
reply.writeNoException();
reply.writeByteArray(_arg0);
return true;
}
case TRANSACTION_calculateMac:
{
data.enforceInterface(DESCRIPTOR);
byte[] _arg0;
_arg0 = data.createByteArray();
com.yifengcom.yfpos.service.ICallBack _arg1;
_arg1 = com.yifengcom.yfpos.service.ICallBack.Stub.asInterface(data.readStrongBinder());
this.calculateMac(_arg0, _arg1);
reply.writeNoException();
reply.writeByteArray(_arg0);
return true;
}
case TRANSACTION_writeMainKey:
{
data.enforceInterface(DESCRIPTOR);
byte[] _arg0;
_arg0 = data.createByteArray();
com.yifengcom.yfpos.service.ICallBack _arg1;
_arg1 = com.yifengcom.yfpos.service.ICallBack.Stub.asInterface(data.readStrongBinder());
this.writeMainKey(_arg0, _arg1);
reply.writeNoException();
reply.writeByteArray(_arg0);
return true;
}
case TRANSACTION_writeWorkKey:
{
data.enforceInterface(DESCRIPTOR);
com.yifengcom.yfpos.service.WorkKey _arg0;
if ((0!=data.readInt())) {
_arg0 = com.yifengcom.yfpos.service.WorkKey.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
com.yifengcom.yfpos.service.ICallBack _arg1;
_arg1 = com.yifengcom.yfpos.service.ICallBack.Stub.asInterface(data.readStrongBinder());
this.writeWorkKey(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_readRFCard:
{
data.enforceInterface(DESCRIPTOR);
com.yifengcom.yfpos.service.ICallBack _arg0;
_arg0 = com.yifengcom.yfpos.service.ICallBack.Stub.asInterface(data.readStrongBinder());
this.readRFCard(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_readMoney:
{
data.enforceInterface(DESCRIPTOR);
com.yifengcom.yfpos.service.ICallBack _arg0;
_arg0 = com.yifengcom.yfpos.service.ICallBack.Stub.asInterface(data.readStrongBinder());
this.readMoney(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_startSwiper:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
long _arg1;
_arg1 = data.readLong();
int _arg2;
_arg2 = data.readInt();
byte _arg3;
_arg3 = data.readByte();
com.yifengcom.yfpos.service.ICallBack _arg4;
_arg4 = com.yifengcom.yfpos.service.ICallBack.Stub.asInterface(data.readStrongBinder());
this.startSwiper(_arg0, _arg1, _arg2, _arg3, _arg4);
reply.writeNoException();
return true;
}
case TRANSACTION_cancel:
{
data.enforceInterface(DESCRIPTOR);
this.cancel();
reply.writeNoException();
return true;
}
case TRANSACTION_setDateTime:
{
data.enforceInterface(DESCRIPTOR);
long _arg0;
_arg0 = data.readLong();
com.yifengcom.yfpos.service.ICallBack _arg1;
_arg1 = com.yifengcom.yfpos.service.ICallBack.Stub.asInterface(data.readStrongBinder());
this.setDateTime(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_getDateTime:
{
data.enforceInterface(DESCRIPTOR);
com.yifengcom.yfpos.service.ICallBack _arg0;
_arg0 = com.yifengcom.yfpos.service.ICallBack.Stub.asInterface(data.readStrongBinder());
this.getDateTime(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_getPsamInfo:
{
data.enforceInterface(DESCRIPTOR);
com.yifengcom.yfpos.service.ICallBack _arg0;
_arg0 = com.yifengcom.yfpos.service.ICallBack.Stub.asInterface(data.readStrongBinder());
this.getPsamInfo(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_getPsamAndSt720Info:
{
data.enforceInterface(DESCRIPTOR);
com.yifengcom.yfpos.service.ICallBack _arg0;
_arg0 = com.yifengcom.yfpos.service.ICallBack.Stub.asInterface(data.readStrongBinder());
this.getPsamAndSt720Info(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_sendIDCardData:
{
data.enforceInterface(DESCRIPTOR);
byte[] _arg0;
_arg0 = data.createByteArray();
com.yifengcom.yfpos.service.ICallBack _arg1;
_arg1 = com.yifengcom.yfpos.service.ICallBack.Stub.asInterface(data.readStrongBinder());
this.sendIDCardData(_arg0, _arg1);
reply.writeNoException();
reply.writeByteArray(_arg0);
return true;
}
case TRANSACTION_openADB:
{
data.enforceInterface(DESCRIPTOR);
com.yifengcom.yfpos.service.ICallBack _arg0;
_arg0 = com.yifengcom.yfpos.service.ICallBack.Stub.asInterface(data.readStrongBinder());
this.openADB(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_closeADB:
{
data.enforceInterface(DESCRIPTOR);
com.yifengcom.yfpos.service.ICallBack _arg0;
_arg0 = com.yifengcom.yfpos.service.ICallBack.Stub.asInterface(data.readStrongBinder());
this.closeADB(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_clearSecurity:
{
data.enforceInterface(DESCRIPTOR);
com.yifengcom.yfpos.service.ICallBack _arg0;
_arg0 = com.yifengcom.yfpos.service.ICallBack.Stub.asInterface(data.readStrongBinder());
this.clearSecurity(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_clearKey:
{
data.enforceInterface(DESCRIPTOR);
com.yifengcom.yfpos.service.ICallBack _arg0;
_arg0 = com.yifengcom.yfpos.service.ICallBack.Stub.asInterface(data.readStrongBinder());
this.clearKey(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_updateFirmware:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
com.yifengcom.yfpos.service.ICallBack _arg1;
_arg1 = com.yifengcom.yfpos.service.ICallBack.Stub.asInterface(data.readStrongBinder());
this.updateFirmware(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_openRFID:
{
data.enforceInterface(DESCRIPTOR);
com.yifengcom.yfpos.service.ICallBack _arg0;
_arg0 = com.yifengcom.yfpos.service.ICallBack.Stub.asInterface(data.readStrongBinder());
this.openRFID(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_sendRFIDCmd:
{
data.enforceInterface(DESCRIPTOR);
byte[] _arg0;
_arg0 = data.createByteArray();
com.yifengcom.yfpos.service.ICallBack _arg1;
_arg1 = com.yifengcom.yfpos.service.ICallBack.Stub.asInterface(data.readStrongBinder());
this.sendRFIDCmd(_arg0, _arg1);
reply.writeNoException();
reply.writeByteArray(_arg0);
return true;
}
case TRANSACTION_closeRFID:
{
data.enforceInterface(DESCRIPTOR);
com.yifengcom.yfpos.service.ICallBack _arg0;
_arg0 = com.yifengcom.yfpos.service.ICallBack.Stub.asInterface(data.readStrongBinder());
this.closeRFID(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_openICFind:
{
data.enforceInterface(DESCRIPTOR);
com.yifengcom.yfpos.service.ICallBack _arg0;
_arg0 = com.yifengcom.yfpos.service.ICallBack.Stub.asInterface(data.readStrongBinder());
this.openICFind(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_sendICCmd:
{
data.enforceInterface(DESCRIPTOR);
byte[] _arg0;
_arg0 = data.createByteArray();
com.yifengcom.yfpos.service.ICallBack _arg1;
_arg1 = com.yifengcom.yfpos.service.ICallBack.Stub.asInterface(data.readStrongBinder());
this.sendICCmd(_arg0, _arg1);
reply.writeNoException();
reply.writeByteArray(_arg0);
return true;
}
case TRANSACTION_powerDownIC:
{
data.enforceInterface(DESCRIPTOR);
com.yifengcom.yfpos.service.ICallBack _arg0;
_arg0 = com.yifengcom.yfpos.service.ICallBack.Stub.asInterface(data.readStrongBinder());
this.powerDownIC(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_resetPSAM:
{
data.enforceInterface(DESCRIPTOR);
byte[] _arg0;
_arg0 = data.createByteArray();
com.yifengcom.yfpos.service.ICallBack _arg1;
_arg1 = com.yifengcom.yfpos.service.ICallBack.Stub.asInterface(data.readStrongBinder());
this.resetPSAM(_arg0, _arg1);
reply.writeNoException();
reply.writeByteArray(_arg0);
return true;
}
case TRANSACTION_readPSAM:
{
data.enforceInterface(DESCRIPTOR);
byte[] _arg0;
_arg0 = data.createByteArray();
com.yifengcom.yfpos.service.ICallBack _arg1;
_arg1 = com.yifengcom.yfpos.service.ICallBack.Stub.asInterface(data.readStrongBinder());
this.readPSAM(_arg0, _arg1);
reply.writeNoException();
reply.writeByteArray(_arg0);
return true;
}
case TRANSACTION_writePSAM:
{
data.enforceInterface(DESCRIPTOR);
byte[] _arg0;
_arg0 = data.createByteArray();
com.yifengcom.yfpos.service.ICallBack _arg1;
_arg1 = com.yifengcom.yfpos.service.ICallBack.Stub.asInterface(data.readStrongBinder());
this.writePSAM(_arg0, _arg1);
reply.writeNoException();
reply.writeByteArray(_arg0);
return true;
}
case TRANSACTION_testDestroy:
{
data.enforceInterface(DESCRIPTOR);
com.yifengcom.yfpos.service.ICallBack _arg0;
_arg0 = com.yifengcom.yfpos.service.ICallBack.Stub.asInterface(data.readStrongBinder());
this.testDestroy(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_requestSN:
{
data.enforceInterface(DESCRIPTOR);
com.yifengcom.yfpos.service.ICallBack _arg0;
_arg0 = com.yifengcom.yfpos.service.ICallBack.Stub.asInterface(data.readStrongBinder());
this.requestSN(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_writeSN:
{
data.enforceInterface(DESCRIPTOR);
byte[] _arg0;
_arg0 = data.createByteArray();
com.yifengcom.yfpos.service.ICallBack _arg1;
_arg1 = com.yifengcom.yfpos.service.ICallBack.Stub.asInterface(data.readStrongBinder());
this.writeSN(_arg0, _arg1);
reply.writeNoException();
reply.writeByteArray(_arg0);
return true;
}
case TRANSACTION_EMVTest:
{
data.enforceInterface(DESCRIPTOR);
byte[] _arg0;
_arg0 = data.createByteArray();
com.yifengcom.yfpos.service.ICallBack _arg1;
_arg1 = com.yifengcom.yfpos.service.ICallBack.Stub.asInterface(data.readStrongBinder());
this.EMVTest(_arg0, _arg1);
reply.writeNoException();
reply.writeByteArray(_arg0);
return true;
}
case TRANSACTION_readSLECardData:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
com.yifengcom.yfpos.service.ICallBack _arg2;
_arg2 = com.yifengcom.yfpos.service.ICallBack.Stub.asInterface(data.readStrongBinder());
this.readSLECardData(_arg0, _arg1, _arg2);
reply.writeNoException();
return true;
}
case TRANSACTION_writeSLECardData:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
int _arg1;
_arg1 = data.readInt();
byte[] _arg2;
_arg2 = data.createByteArray();
com.yifengcom.yfpos.service.ICallBack _arg3;
_arg3 = com.yifengcom.yfpos.service.ICallBack.Stub.asInterface(data.readStrongBinder());
this.writeSLECardData(_arg0, _arg1, _arg2, _arg3);
reply.writeNoException();
reply.writeByteArray(_arg2);
return true;
}
case TRANSACTION_updateSLEpwd:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
com.yifengcom.yfpos.service.ICallBack _arg2;
_arg2 = com.yifengcom.yfpos.service.ICallBack.Stub.asInterface(data.readStrongBinder());
this.updateSLEpwd(_arg0, _arg1, _arg2);
reply.writeNoException();
return true;
}
case TRANSACTION_uploadSignBitmap:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
com.yifengcom.yfpos.service.ICallBack _arg1;
_arg1 = com.yifengcom.yfpos.service.ICallBack.Stub.asInterface(data.readStrongBinder());
this.uploadSignBitmap(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_psamResetEx:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
byte[] _result = this.psamResetEx(_arg0, _arg1);
reply.writeNoException();
reply.writeByteArray(_result);
return true;
}
case TRANSACTION_psamApduEx:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
byte[] _arg1;
_arg1 = data.createByteArray();
int _arg2;
_arg2 = data.readInt();
byte[] _result = this.psamApduEx(_arg0, _arg1, _arg2);
reply.writeNoException();
reply.writeByteArray(_result);
return true;
}
case TRANSACTION_rfidOpenEx:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
byte[] _result = this.rfidOpenEx(_arg0);
reply.writeNoException();
reply.writeByteArray(_result);
return true;
}
case TRANSACTION_rfidApduEx:
{
data.enforceInterface(DESCRIPTOR);
byte[] _arg0;
_arg0 = data.createByteArray();
int _arg1;
_arg1 = data.readInt();
byte[] _result = this.rfidApduEx(_arg0, _arg1);
reply.writeNoException();
reply.writeByteArray(_result);
reply.writeByteArray(_arg0);
return true;
}
case TRANSACTION_rfidCloseEx:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.rfidCloseEx();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.yifengcom.yfpos.service.IService
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
@Override public void registerICallback(com.yifengcom.yfpos.service.ICallBack cb) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((cb!=null))?(cb.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_registerICallback, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void unregisterICallback(com.yifengcom.yfpos.service.ICallBack cb) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((cb!=null))?(cb.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_unregisterICallback, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
// 获取设备版本

@Override public void onGetDeviceInfo(com.yifengcom.yfpos.service.ICallBack icallback) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((icallback!=null))?(icallback.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_onGetDeviceInfo, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
// 设置数据

@Override public void setDeviceData(java.lang.String customerNo, java.lang.String termNo, java.lang.String serialNo, java.lang.String batchNo, com.yifengcom.yfpos.service.ICallBack icallback) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(customerNo);
_data.writeString(termNo);
_data.writeString(serialNo);
_data.writeString(batchNo);
_data.writeStrongBinder((((icallback!=null))?(icallback.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_setDeviceData, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
// 打印

@Override public void onPrint(byte[] body, com.yifengcom.yfpos.service.ICallBack icallback) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeByteArray(body);
_data.writeStrongBinder((((icallback!=null))?(icallback.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_onPrint, _data, _reply, 0);
_reply.readException();
_reply.readByteArray(body);
}
finally {
_reply.recycle();
_data.recycle();
}
}
// 验证MAC

@Override public void calculateMac(byte[] data, com.yifengcom.yfpos.service.ICallBack icallback) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeByteArray(data);
_data.writeStrongBinder((((icallback!=null))?(icallback.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_calculateMac, _data, _reply, 0);
_reply.readException();
_reply.readByteArray(data);
}
finally {
_reply.recycle();
_data.recycle();
}
}
// 更新主密钥

@Override public void writeMainKey(byte[] key, com.yifengcom.yfpos.service.ICallBack icallback) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeByteArray(key);
_data.writeStrongBinder((((icallback!=null))?(icallback.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_writeMainKey, _data, _reply, 0);
_reply.readException();
_reply.readByteArray(key);
}
finally {
_reply.recycle();
_data.recycle();
}
}
// 更新工作密钥

@Override public void writeWorkKey(com.yifengcom.yfpos.service.WorkKey key, com.yifengcom.yfpos.service.ICallBack icallback) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((key!=null)) {
_data.writeInt(1);
key.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
_data.writeStrongBinder((((icallback!=null))?(icallback.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_writeWorkKey, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
// 射频卡

@Override public void readRFCard(com.yifengcom.yfpos.service.ICallBack icallback) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((icallback!=null))?(icallback.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_readRFCard, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
// 读取电子现金余额

@Override public void readMoney(com.yifengcom.yfpos.service.ICallBack icallback) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((icallback!=null))?(icallback.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_readMoney, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
// 刷卡

@Override public void startSwiper(int timeout, long nAmount, int brushCard, byte type, com.yifengcom.yfpos.service.ICallBack icallback) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(timeout);
_data.writeLong(nAmount);
_data.writeInt(brushCard);
_data.writeByte(type);
_data.writeStrongBinder((((icallback!=null))?(icallback.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_startSwiper, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
// 取消指令

@Override public void cancel() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_cancel, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
// 设置时间

@Override public void setDateTime(long time, com.yifengcom.yfpos.service.ICallBack icallback) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeLong(time);
_data.writeStrongBinder((((icallback!=null))?(icallback.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_setDateTime, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
// 获取时间

@Override public void getDateTime(com.yifengcom.yfpos.service.ICallBack icallback) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((icallback!=null))?(icallback.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_getDateTime, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
// 获取PSAM卡号

@Override public void getPsamInfo(com.yifengcom.yfpos.service.ICallBack icallback) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((icallback!=null))?(icallback.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_getPsamInfo, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
// 获取PSAM、ST720 ATR内容

@Override public void getPsamAndSt720Info(com.yifengcom.yfpos.service.ICallBack icallback) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((icallback!=null))?(icallback.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_getPsamAndSt720Info, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
// 发送身份证数据

@Override public void sendIDCardData(byte[] body, com.yifengcom.yfpos.service.ICallBack icallback) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeByteArray(body);
_data.writeStrongBinder((((icallback!=null))?(icallback.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_sendIDCardData, _data, _reply, 0);
_reply.readException();
_reply.readByteArray(body);
}
finally {
_reply.recycle();
_data.recycle();
}
}
// 打开ADB

@Override public void openADB(com.yifengcom.yfpos.service.ICallBack icallback) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((icallback!=null))?(icallback.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_openADB, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
// 关闭ADB

@Override public void closeADB(com.yifengcom.yfpos.service.ICallBack icallback) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((icallback!=null))?(icallback.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_closeADB, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
// 清除安全触发

@Override public void clearSecurity(com.yifengcom.yfpos.service.ICallBack icallback) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((icallback!=null))?(icallback.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_clearSecurity, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
// 清除密钥证书

@Override public void clearKey(com.yifengcom.yfpos.service.ICallBack icallback) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((icallback!=null))?(icallback.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_clearKey, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
// 更新固件

@Override public void updateFirmware(java.lang.String path, com.yifengcom.yfpos.service.ICallBack icallback) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(path);
_data.writeStrongBinder((((icallback!=null))?(icallback.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_updateFirmware, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
// 打开射频

@Override public void openRFID(com.yifengcom.yfpos.service.ICallBack icallback) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((icallback!=null))?(icallback.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_openRFID, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
// 发送射频命令

@Override public void sendRFIDCmd(byte[] data, com.yifengcom.yfpos.service.ICallBack icallback) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeByteArray(data);
_data.writeStrongBinder((((icallback!=null))?(icallback.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_sendRFIDCmd, _data, _reply, 0);
_reply.readException();
_reply.readByteArray(data);
}
finally {
_reply.recycle();
_data.recycle();
}
}
// 关闭射频

@Override public void closeRFID(com.yifengcom.yfpos.service.ICallBack icallback) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((icallback!=null))?(icallback.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_closeRFID, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
// 打开IC卡寻卡

@Override public void openICFind(com.yifengcom.yfpos.service.ICallBack icallback) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((icallback!=null))?(icallback.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_openICFind, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
// IC卡数据交互

@Override public void sendICCmd(byte[] data, com.yifengcom.yfpos.service.ICallBack icallback) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeByteArray(data);
_data.writeStrongBinder((((icallback!=null))?(icallback.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_sendICCmd, _data, _reply, 0);
_reply.readException();
_reply.readByteArray(data);
}
finally {
_reply.recycle();
_data.recycle();
}
}
// IC卡下电

@Override public void powerDownIC(com.yifengcom.yfpos.service.ICallBack icallback) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((icallback!=null))?(icallback.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_powerDownIC, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
// PSAM卡复位

@Override public void resetPSAM(byte[] data, com.yifengcom.yfpos.service.ICallBack icallback) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeByteArray(data);
_data.writeStrongBinder((((icallback!=null))?(icallback.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_resetPSAM, _data, _reply, 0);
_reply.readException();
_reply.readByteArray(data);
}
finally {
_reply.recycle();
_data.recycle();
}
}
// 读PSAM卡

@Override public void readPSAM(byte[] data, com.yifengcom.yfpos.service.ICallBack icallback) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeByteArray(data);
_data.writeStrongBinder((((icallback!=null))?(icallback.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_readPSAM, _data, _reply, 0);
_reply.readException();
_reply.readByteArray(data);
}
finally {
_reply.recycle();
_data.recycle();
}
}
// 写PSAM卡

@Override public void writePSAM(byte[] data, com.yifengcom.yfpos.service.ICallBack icallback) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeByteArray(data);
_data.writeStrongBinder((((icallback!=null))?(icallback.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_writePSAM, _data, _reply, 0);
_reply.readException();
_reply.readByteArray(data);
}
finally {
_reply.recycle();
_data.recycle();
}
}
// 自毁测试

@Override public void testDestroy(com.yifengcom.yfpos.service.ICallBack icallback) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((icallback!=null))?(icallback.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_testDestroy, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
// 请求录入SN号

@Override public void requestSN(com.yifengcom.yfpos.service.ICallBack icallback) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((icallback!=null))?(icallback.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_requestSN, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
// SN号下发

@Override public void writeSN(byte[] data, com.yifengcom.yfpos.service.ICallBack icallback) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeByteArray(data);
_data.writeStrongBinder((((icallback!=null))?(icallback.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_writeSN, _data, _reply, 0);
_reply.readException();
_reply.readByteArray(data);
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void EMVTest(byte[] data, com.yifengcom.yfpos.service.ICallBack icallback) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeByteArray(data);
_data.writeStrongBinder((((icallback!=null))?(icallback.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_EMVTest, _data, _reply, 0);
_reply.readException();
_reply.readByteArray(data);
}
finally {
_reply.recycle();
_data.recycle();
}
}
// 读取SLE4442数据

@Override public void readSLECardData(int offset, int len, com.yifengcom.yfpos.service.ICallBack icallback) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(offset);
_data.writeInt(len);
_data.writeStrongBinder((((icallback!=null))?(icallback.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_readSLECardData, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
// 写入SLE4442数据

@Override public void writeSLECardData(java.lang.String pwd, int offset, byte[] data, com.yifengcom.yfpos.service.ICallBack icallback) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(pwd);
_data.writeInt(offset);
_data.writeByteArray(data);
_data.writeStrongBinder((((icallback!=null))?(icallback.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_writeSLECardData, _data, _reply, 0);
_reply.readException();
_reply.readByteArray(data);
}
finally {
_reply.recycle();
_data.recycle();
}
}
// 修改密码

@Override public void updateSLEpwd(java.lang.String pwd, java.lang.String newPwd, com.yifengcom.yfpos.service.ICallBack icallback) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(pwd);
_data.writeString(newPwd);
_data.writeStrongBinder((((icallback!=null))?(icallback.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_updateSLEpwd, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
// 上传电子签名

@Override public void uploadSignBitmap(java.lang.String path, com.yifengcom.yfpos.service.ICallBack icallback) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(path);
_data.writeStrongBinder((((icallback!=null))?(icallback.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_uploadSignBitmap, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
//PSAM卡复位

@Override public byte[] psamResetEx(int SiteNo, int timeout) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
byte[] _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(SiteNo);
_data.writeInt(timeout);
mRemote.transact(Stub.TRANSACTION_psamResetEx, _data, _reply, 0);
_reply.readException();
_result = _reply.createByteArray();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
//PSAM卡APDU指令透传

@Override public byte[] psamApduEx(int SiteNo, byte[] send, int timeout) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
byte[] _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(SiteNo);
_data.writeByteArray(send);
_data.writeInt(timeout);
mRemote.transact(Stub.TRANSACTION_psamApduEx, _data, _reply, 0);
_reply.readException();
_result = _reply.createByteArray();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
// 打开射频, 单位：秒,最大255秒

@Override public byte[] rfidOpenEx(int timeout) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
byte[] _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(timeout);
mRemote.transact(Stub.TRANSACTION_rfidOpenEx, _data, _reply, 0);
_reply.readException();
_result = _reply.createByteArray();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
// 发送射频命令

@Override public byte[] rfidApduEx(byte[] data, int timeout) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
byte[] _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeByteArray(data);
_data.writeInt(timeout);
mRemote.transact(Stub.TRANSACTION_rfidApduEx, _data, _reply, 0);
_reply.readException();
_result = _reply.createByteArray();
_reply.readByteArray(data);
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
// 关闭射频

@Override public int rfidCloseEx() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_rfidCloseEx, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
}
static final int TRANSACTION_registerICallback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_unregisterICallback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_onGetDeviceInfo = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_setDeviceData = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_onPrint = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_calculateMac = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
static final int TRANSACTION_writeMainKey = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
static final int TRANSACTION_writeWorkKey = (android.os.IBinder.FIRST_CALL_TRANSACTION + 7);
static final int TRANSACTION_readRFCard = (android.os.IBinder.FIRST_CALL_TRANSACTION + 8);
static final int TRANSACTION_readMoney = (android.os.IBinder.FIRST_CALL_TRANSACTION + 9);
static final int TRANSACTION_startSwiper = (android.os.IBinder.FIRST_CALL_TRANSACTION + 10);
static final int TRANSACTION_cancel = (android.os.IBinder.FIRST_CALL_TRANSACTION + 11);
static final int TRANSACTION_setDateTime = (android.os.IBinder.FIRST_CALL_TRANSACTION + 12);
static final int TRANSACTION_getDateTime = (android.os.IBinder.FIRST_CALL_TRANSACTION + 13);
static final int TRANSACTION_getPsamInfo = (android.os.IBinder.FIRST_CALL_TRANSACTION + 14);
static final int TRANSACTION_getPsamAndSt720Info = (android.os.IBinder.FIRST_CALL_TRANSACTION + 15);
static final int TRANSACTION_sendIDCardData = (android.os.IBinder.FIRST_CALL_TRANSACTION + 16);
static final int TRANSACTION_openADB = (android.os.IBinder.FIRST_CALL_TRANSACTION + 17);
static final int TRANSACTION_closeADB = (android.os.IBinder.FIRST_CALL_TRANSACTION + 18);
static final int TRANSACTION_clearSecurity = (android.os.IBinder.FIRST_CALL_TRANSACTION + 19);
static final int TRANSACTION_clearKey = (android.os.IBinder.FIRST_CALL_TRANSACTION + 20);
static final int TRANSACTION_updateFirmware = (android.os.IBinder.FIRST_CALL_TRANSACTION + 21);
static final int TRANSACTION_openRFID = (android.os.IBinder.FIRST_CALL_TRANSACTION + 22);
static final int TRANSACTION_sendRFIDCmd = (android.os.IBinder.FIRST_CALL_TRANSACTION + 23);
static final int TRANSACTION_closeRFID = (android.os.IBinder.FIRST_CALL_TRANSACTION + 24);
static final int TRANSACTION_openICFind = (android.os.IBinder.FIRST_CALL_TRANSACTION + 25);
static final int TRANSACTION_sendICCmd = (android.os.IBinder.FIRST_CALL_TRANSACTION + 26);
static final int TRANSACTION_powerDownIC = (android.os.IBinder.FIRST_CALL_TRANSACTION + 27);
static final int TRANSACTION_resetPSAM = (android.os.IBinder.FIRST_CALL_TRANSACTION + 28);
static final int TRANSACTION_readPSAM = (android.os.IBinder.FIRST_CALL_TRANSACTION + 29);
static final int TRANSACTION_writePSAM = (android.os.IBinder.FIRST_CALL_TRANSACTION + 30);
static final int TRANSACTION_testDestroy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 31);
static final int TRANSACTION_requestSN = (android.os.IBinder.FIRST_CALL_TRANSACTION + 32);
static final int TRANSACTION_writeSN = (android.os.IBinder.FIRST_CALL_TRANSACTION + 33);
static final int TRANSACTION_EMVTest = (android.os.IBinder.FIRST_CALL_TRANSACTION + 34);
static final int TRANSACTION_readSLECardData = (android.os.IBinder.FIRST_CALL_TRANSACTION + 35);
static final int TRANSACTION_writeSLECardData = (android.os.IBinder.FIRST_CALL_TRANSACTION + 36);
static final int TRANSACTION_updateSLEpwd = (android.os.IBinder.FIRST_CALL_TRANSACTION + 37);
static final int TRANSACTION_uploadSignBitmap = (android.os.IBinder.FIRST_CALL_TRANSACTION + 38);
static final int TRANSACTION_psamResetEx = (android.os.IBinder.FIRST_CALL_TRANSACTION + 39);
static final int TRANSACTION_psamApduEx = (android.os.IBinder.FIRST_CALL_TRANSACTION + 40);
static final int TRANSACTION_rfidOpenEx = (android.os.IBinder.FIRST_CALL_TRANSACTION + 41);
static final int TRANSACTION_rfidApduEx = (android.os.IBinder.FIRST_CALL_TRANSACTION + 42);
static final int TRANSACTION_rfidCloseEx = (android.os.IBinder.FIRST_CALL_TRANSACTION + 43);
}
public void registerICallback(com.yifengcom.yfpos.service.ICallBack cb) throws android.os.RemoteException;
public void unregisterICallback(com.yifengcom.yfpos.service.ICallBack cb) throws android.os.RemoteException;
// 获取设备版本

public void onGetDeviceInfo(com.yifengcom.yfpos.service.ICallBack icallback) throws android.os.RemoteException;
// 设置数据

public void setDeviceData(java.lang.String customerNo, java.lang.String termNo, java.lang.String serialNo, java.lang.String batchNo, com.yifengcom.yfpos.service.ICallBack icallback) throws android.os.RemoteException;
// 打印

public void onPrint(byte[] body, com.yifengcom.yfpos.service.ICallBack icallback) throws android.os.RemoteException;
// 验证MAC

public void calculateMac(byte[] data, com.yifengcom.yfpos.service.ICallBack icallback) throws android.os.RemoteException;
// 更新主密钥

public void writeMainKey(byte[] key, com.yifengcom.yfpos.service.ICallBack icallback) throws android.os.RemoteException;
// 更新工作密钥

public void writeWorkKey(com.yifengcom.yfpos.service.WorkKey key, com.yifengcom.yfpos.service.ICallBack icallback) throws android.os.RemoteException;
// 射频卡

public void readRFCard(com.yifengcom.yfpos.service.ICallBack icallback) throws android.os.RemoteException;
// 读取电子现金余额

public void readMoney(com.yifengcom.yfpos.service.ICallBack icallback) throws android.os.RemoteException;
// 刷卡

public void startSwiper(int timeout, long nAmount, int brushCard, byte type, com.yifengcom.yfpos.service.ICallBack icallback) throws android.os.RemoteException;
// 取消指令

public void cancel() throws android.os.RemoteException;
// 设置时间

public void setDateTime(long time, com.yifengcom.yfpos.service.ICallBack icallback) throws android.os.RemoteException;
// 获取时间

public void getDateTime(com.yifengcom.yfpos.service.ICallBack icallback) throws android.os.RemoteException;
// 获取PSAM卡号

public void getPsamInfo(com.yifengcom.yfpos.service.ICallBack icallback) throws android.os.RemoteException;
// 获取PSAM、ST720 ATR内容

public void getPsamAndSt720Info(com.yifengcom.yfpos.service.ICallBack icallback) throws android.os.RemoteException;
// 发送身份证数据

public void sendIDCardData(byte[] body, com.yifengcom.yfpos.service.ICallBack icallback) throws android.os.RemoteException;
// 打开ADB

public void openADB(com.yifengcom.yfpos.service.ICallBack icallback) throws android.os.RemoteException;
// 关闭ADB

public void closeADB(com.yifengcom.yfpos.service.ICallBack icallback) throws android.os.RemoteException;
// 清除安全触发

public void clearSecurity(com.yifengcom.yfpos.service.ICallBack icallback) throws android.os.RemoteException;
// 清除密钥证书

public void clearKey(com.yifengcom.yfpos.service.ICallBack icallback) throws android.os.RemoteException;
// 更新固件

public void updateFirmware(java.lang.String path, com.yifengcom.yfpos.service.ICallBack icallback) throws android.os.RemoteException;
// 打开射频

public void openRFID(com.yifengcom.yfpos.service.ICallBack icallback) throws android.os.RemoteException;
// 发送射频命令

public void sendRFIDCmd(byte[] data, com.yifengcom.yfpos.service.ICallBack icallback) throws android.os.RemoteException;
// 关闭射频

public void closeRFID(com.yifengcom.yfpos.service.ICallBack icallback) throws android.os.RemoteException;
// 打开IC卡寻卡

public void openICFind(com.yifengcom.yfpos.service.ICallBack icallback) throws android.os.RemoteException;
// IC卡数据交互

public void sendICCmd(byte[] data, com.yifengcom.yfpos.service.ICallBack icallback) throws android.os.RemoteException;
// IC卡下电

public void powerDownIC(com.yifengcom.yfpos.service.ICallBack icallback) throws android.os.RemoteException;
// PSAM卡复位

public void resetPSAM(byte[] data, com.yifengcom.yfpos.service.ICallBack icallback) throws android.os.RemoteException;
// 读PSAM卡

public void readPSAM(byte[] data, com.yifengcom.yfpos.service.ICallBack icallback) throws android.os.RemoteException;
// 写PSAM卡

public void writePSAM(byte[] data, com.yifengcom.yfpos.service.ICallBack icallback) throws android.os.RemoteException;
// 自毁测试

public void testDestroy(com.yifengcom.yfpos.service.ICallBack icallback) throws android.os.RemoteException;
// 请求录入SN号

public void requestSN(com.yifengcom.yfpos.service.ICallBack icallback) throws android.os.RemoteException;
// SN号下发

public void writeSN(byte[] data, com.yifengcom.yfpos.service.ICallBack icallback) throws android.os.RemoteException;
public void EMVTest(byte[] data, com.yifengcom.yfpos.service.ICallBack icallback) throws android.os.RemoteException;
// 读取SLE4442数据

public void readSLECardData(int offset, int len, com.yifengcom.yfpos.service.ICallBack icallback) throws android.os.RemoteException;
// 写入SLE4442数据

public void writeSLECardData(java.lang.String pwd, int offset, byte[] data, com.yifengcom.yfpos.service.ICallBack icallback) throws android.os.RemoteException;
// 修改密码

public void updateSLEpwd(java.lang.String pwd, java.lang.String newPwd, com.yifengcom.yfpos.service.ICallBack icallback) throws android.os.RemoteException;
// 上传电子签名

public void uploadSignBitmap(java.lang.String path, com.yifengcom.yfpos.service.ICallBack icallback) throws android.os.RemoteException;
//PSAM卡复位

public byte[] psamResetEx(int SiteNo, int timeout) throws android.os.RemoteException;
//PSAM卡APDU指令透传

public byte[] psamApduEx(int SiteNo, byte[] send, int timeout) throws android.os.RemoteException;
// 打开射频, 单位：秒,最大255秒

public byte[] rfidOpenEx(int timeout) throws android.os.RemoteException;
// 发送射频命令

public byte[] rfidApduEx(byte[] data, int timeout) throws android.os.RemoteException;
// 关闭射频

public int rfidCloseEx() throws android.os.RemoteException;
}
