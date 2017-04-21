/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: F:\\android_work\\YFPosClient_new1\\app\\src\\main\\aidl\\com\\yifengcom\\yfpos\\service\\ICallBack.aidl
 */
package com.yifengcom.yfpos.service;
public interface ICallBack extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.yifengcom.yfpos.service.ICallBack
{
private static final java.lang.String DESCRIPTOR = "com.yifengcom.yfpos.service.ICallBack";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.yifengcom.yfpos.service.ICallBack interface,
 * generating a proxy if needed.
 */
public static com.yifengcom.yfpos.service.ICallBack asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.yifengcom.yfpos.service.ICallBack))) {
return ((com.yifengcom.yfpos.service.ICallBack)iin);
}
return new com.yifengcom.yfpos.service.ICallBack.Stub.Proxy(obj);
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
case TRANSACTION_onError:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
java.lang.String _arg1;
_arg1 = data.readString();
this.onError(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_onGetDeviceInfo:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
java.lang.String _arg2;
_arg2 = data.readString();
boolean _arg3;
_arg3 = (0!=data.readInt());
java.lang.String _arg4;
_arg4 = data.readString();
java.lang.String _arg5;
_arg5 = data.readString();
this.onGetDeviceInfo(_arg0, _arg1, _arg2, _arg3, _arg4, _arg5);
reply.writeNoException();
return true;
}
case TRANSACTION_onTimeout:
{
data.enforceInterface(DESCRIPTOR);
this.onTimeout();
reply.writeNoException();
return true;
}
case TRANSACTION_onResultSuccess:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.onResultSuccess(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_onCalculateMacSuccess:
{
data.enforceInterface(DESCRIPTOR);
byte[] _arg0;
_arg0 = data.createByteArray();
this.onCalculateMacSuccess(_arg0);
reply.writeNoException();
reply.writeByteArray(_arg0);
return true;
}
case TRANSACTION_onReadSuccess:
{
data.enforceInterface(DESCRIPTOR);
byte[] _arg0;
_arg0 = data.createByteArray();
this.onReadSuccess(_arg0);
reply.writeNoException();
reply.writeByteArray(_arg0);
return true;
}
case TRANSACTION_onGetDateTimeSuccess:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.onGetDateTimeSuccess(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_onSwiperSuccess:
{
data.enforceInterface(DESCRIPTOR);
com.yifengcom.yfpos.service.CardModel _arg0;
if ((0!=data.readInt())) {
_arg0 = com.yifengcom.yfpos.service.CardModel.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
this.onSwiperSuccess(_arg0);
reply.writeNoException();
if ((_arg0!=null)) {
reply.writeInt(1);
_arg0.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_onDetectIc:
{
data.enforceInterface(DESCRIPTOR);
this.onDetectIc();
reply.writeNoException();
return true;
}
case TRANSACTION_onInputPin:
{
data.enforceInterface(DESCRIPTOR);
this.onInputPin();
reply.writeNoException();
return true;
}
case TRANSACTION_onTradeCancel:
{
data.enforceInterface(DESCRIPTOR);
this.onTradeCancel();
reply.writeNoException();
return true;
}
case TRANSACTION_onShowPinPad:
{
data.enforceInterface(DESCRIPTOR);
byte[] _arg0;
_arg0 = data.createByteArray();
this.onShowPinPad(_arg0);
reply.writeNoException();
reply.writeByteArray(_arg0);
return true;
}
case TRANSACTION_onClosePinPad:
{
data.enforceInterface(DESCRIPTOR);
this.onClosePinPad();
reply.writeNoException();
return true;
}
case TRANSACTION_onGetPsamAndSt720Info:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
this.onGetPsamAndSt720Info(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_onReadPsamNo:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.onReadPsamNo(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_onIDCardResultData:
{
data.enforceInterface(DESCRIPTOR);
byte[] _arg0;
_arg0 = data.createByteArray();
this.onIDCardResultData(_arg0);
reply.writeNoException();
reply.writeByteArray(_arg0);
return true;
}
case TRANSACTION_onDownloadProgress:
{
data.enforceInterface(DESCRIPTOR);
long _arg0;
_arg0 = data.readLong();
long _arg1;
_arg1 = data.readLong();
this.onDownloadProgress(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_onGetDeviceInfoSuccess:
{
data.enforceInterface(DESCRIPTOR);
com.yifengcom.yfpos.service.DeviceModel _arg0;
if ((0!=data.readInt())) {
_arg0 = com.yifengcom.yfpos.service.DeviceModel.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
this.onGetDeviceInfoSuccess(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_onEMVTestOK:
{
data.enforceInterface(DESCRIPTOR);
byte[] _arg0;
_arg0 = data.createByteArray();
this.onEMVTestOK(_arg0);
reply.writeNoException();
reply.writeByteArray(_arg0);
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.yifengcom.yfpos.service.ICallBack
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
@Override public void onError(int errorCode, java.lang.String errorMessage) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(errorCode);
_data.writeString(errorMessage);
mRemote.transact(Stub.TRANSACTION_onError, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void onGetDeviceInfo(java.lang.String customerNo, java.lang.String termNo, java.lang.String batchNo, boolean existsMainKey, java.lang.String sn, java.lang.String version) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(customerNo);
_data.writeString(termNo);
_data.writeString(batchNo);
_data.writeInt(((existsMainKey)?(1):(0)));
_data.writeString(sn);
_data.writeString(version);
mRemote.transact(Stub.TRANSACTION_onGetDeviceInfo, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void onTimeout() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_onTimeout, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void onResultSuccess(int ntype) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(ntype);
mRemote.transact(Stub.TRANSACTION_onResultSuccess, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void onCalculateMacSuccess(byte[] mac) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeByteArray(mac);
mRemote.transact(Stub.TRANSACTION_onCalculateMacSuccess, _data, _reply, 0);
_reply.readException();
_reply.readByteArray(mac);
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void onReadSuccess(byte[] body) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeByteArray(body);
mRemote.transact(Stub.TRANSACTION_onReadSuccess, _data, _reply, 0);
_reply.readException();
_reply.readByteArray(body);
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void onGetDateTimeSuccess(java.lang.String dateTime) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(dateTime);
mRemote.transact(Stub.TRANSACTION_onGetDateTimeSuccess, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void onSwiperSuccess(com.yifengcom.yfpos.service.CardModel cardModel) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((cardModel!=null)) {
_data.writeInt(1);
cardModel.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_onSwiperSuccess, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
cardModel.readFromParcel(_reply);
}
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void onDetectIc() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_onDetectIc, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void onInputPin() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_onInputPin, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void onTradeCancel() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_onTradeCancel, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void onShowPinPad(byte[] pad) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeByteArray(pad);
mRemote.transact(Stub.TRANSACTION_onShowPinPad, _data, _reply, 0);
_reply.readException();
_reply.readByteArray(pad);
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void onClosePinPad() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_onClosePinPad, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void onGetPsamAndSt720Info(java.lang.String psam, java.lang.String st720) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(psam);
_data.writeString(st720);
mRemote.transact(Stub.TRANSACTION_onGetPsamAndSt720Info, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void onReadPsamNo(java.lang.String num) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(num);
mRemote.transact(Stub.TRANSACTION_onReadPsamNo, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void onIDCardResultData(byte[] body) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeByteArray(body);
mRemote.transact(Stub.TRANSACTION_onIDCardResultData, _data, _reply, 0);
_reply.readException();
_reply.readByteArray(body);
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void onDownloadProgress(long current, long total) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeLong(current);
_data.writeLong(total);
mRemote.transact(Stub.TRANSACTION_onDownloadProgress, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void onGetDeviceInfoSuccess(com.yifengcom.yfpos.service.DeviceModel deviceModel) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((deviceModel!=null)) {
_data.writeInt(1);
deviceModel.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_onGetDeviceInfoSuccess, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void onEMVTestOK(byte[] body) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeByteArray(body);
mRemote.transact(Stub.TRANSACTION_onEMVTestOK, _data, _reply, 0);
_reply.readException();
_reply.readByteArray(body);
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_onError = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_onGetDeviceInfo = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_onTimeout = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_onResultSuccess = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_onCalculateMacSuccess = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_onReadSuccess = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
static final int TRANSACTION_onGetDateTimeSuccess = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
static final int TRANSACTION_onSwiperSuccess = (android.os.IBinder.FIRST_CALL_TRANSACTION + 7);
static final int TRANSACTION_onDetectIc = (android.os.IBinder.FIRST_CALL_TRANSACTION + 8);
static final int TRANSACTION_onInputPin = (android.os.IBinder.FIRST_CALL_TRANSACTION + 9);
static final int TRANSACTION_onTradeCancel = (android.os.IBinder.FIRST_CALL_TRANSACTION + 10);
static final int TRANSACTION_onShowPinPad = (android.os.IBinder.FIRST_CALL_TRANSACTION + 11);
static final int TRANSACTION_onClosePinPad = (android.os.IBinder.FIRST_CALL_TRANSACTION + 12);
static final int TRANSACTION_onGetPsamAndSt720Info = (android.os.IBinder.FIRST_CALL_TRANSACTION + 13);
static final int TRANSACTION_onReadPsamNo = (android.os.IBinder.FIRST_CALL_TRANSACTION + 14);
static final int TRANSACTION_onIDCardResultData = (android.os.IBinder.FIRST_CALL_TRANSACTION + 15);
static final int TRANSACTION_onDownloadProgress = (android.os.IBinder.FIRST_CALL_TRANSACTION + 16);
static final int TRANSACTION_onGetDeviceInfoSuccess = (android.os.IBinder.FIRST_CALL_TRANSACTION + 17);
static final int TRANSACTION_onEMVTestOK = (android.os.IBinder.FIRST_CALL_TRANSACTION + 18);
}
public void onError(int errorCode, java.lang.String errorMessage) throws android.os.RemoteException;
public void onGetDeviceInfo(java.lang.String customerNo, java.lang.String termNo, java.lang.String batchNo, boolean existsMainKey, java.lang.String sn, java.lang.String version) throws android.os.RemoteException;
public void onTimeout() throws android.os.RemoteException;
public void onResultSuccess(int ntype) throws android.os.RemoteException;
public void onCalculateMacSuccess(byte[] mac) throws android.os.RemoteException;
public void onReadSuccess(byte[] body) throws android.os.RemoteException;
public void onGetDateTimeSuccess(java.lang.String dateTime) throws android.os.RemoteException;
public void onSwiperSuccess(com.yifengcom.yfpos.service.CardModel cardModel) throws android.os.RemoteException;
public void onDetectIc() throws android.os.RemoteException;
public void onInputPin() throws android.os.RemoteException;
public void onTradeCancel() throws android.os.RemoteException;
public void onShowPinPad(byte[] pad) throws android.os.RemoteException;
public void onClosePinPad() throws android.os.RemoteException;
public void onGetPsamAndSt720Info(java.lang.String psam, java.lang.String st720) throws android.os.RemoteException;
public void onReadPsamNo(java.lang.String num) throws android.os.RemoteException;
public void onIDCardResultData(byte[] body) throws android.os.RemoteException;
public void onDownloadProgress(long current, long total) throws android.os.RemoteException;
public void onGetDeviceInfoSuccess(com.yifengcom.yfpos.service.DeviceModel deviceModel) throws android.os.RemoteException;
public void onEMVTestOK(byte[] body) throws android.os.RemoteException;
}
