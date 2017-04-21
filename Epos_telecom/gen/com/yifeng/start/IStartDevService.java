/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: E:\\JAVA_PRO\\Epos_telecom\\src\\com\\yifeng\\start\\IStartDevService.aidl
 */
package com.yifeng.start;
public interface IStartDevService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.yifeng.start.IStartDevService
{
private static final java.lang.String DESCRIPTOR = "com.yifeng.start.IStartDevService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.yifeng.start.IStartDevService interface,
 * generating a proxy if needed.
 */
public static com.yifeng.start.IStartDevService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.yifeng.start.IStartDevService))) {
return ((com.yifeng.start.IStartDevService)iin);
}
return new com.yifeng.start.IStartDevService.Stub.Proxy(obj);
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
case TRANSACTION_startDevPackData:
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
int _result = this.startDevPackData(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(_result);
reply.writeByteArray(_arg1);
return true;
}
case TRANSACTION_startDevUnpackData:
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
int _result = this.startDevUnpackData(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(_result);
reply.writeByteArray(_arg1);
return true;
}
case TRANSACTION_startDevPrint:
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
int _result = this.startDevPrint(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(_result);
reply.writeByteArray(_arg1);
return true;
}
case TRANSACTION_startDevSetTimeOut:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _result = this.startDevSetTimeOut(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_startDevInitIdle:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.startDevInitIdle();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_release:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.release();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_close:
{
data.enforceInterface(DESCRIPTOR);
this.close();
reply.writeNoException();
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
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.yifeng.start.IStartDevService
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
@Override public int startDevPackData(byte[] inBuf, byte[] outBuf) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeByteArray(inBuf);
if ((outBuf==null)) {
_data.writeInt(-1);
}
else {
_data.writeInt(outBuf.length);
}
mRemote.transact(Stub.TRANSACTION_startDevPackData, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
_reply.readByteArray(outBuf);
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public int startDevUnpackData(byte[] inBuf, byte[] outBuf) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeByteArray(inBuf);
if ((outBuf==null)) {
_data.writeInt(-1);
}
else {
_data.writeInt(outBuf.length);
}
mRemote.transact(Stub.TRANSACTION_startDevUnpackData, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
_reply.readByteArray(outBuf);
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public int startDevPrint(byte[] buf, byte[] outBuf) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeByteArray(buf);
if ((outBuf==null)) {
_data.writeInt(-1);
}
else {
_data.writeInt(outBuf.length);
}
mRemote.transact(Stub.TRANSACTION_startDevPrint, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
_reply.readByteArray(outBuf);
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public int startDevSetTimeOut(int timeout) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(timeout);
mRemote.transact(Stub.TRANSACTION_startDevSetTimeOut, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public int startDevInitIdle() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_startDevInitIdle, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public boolean release() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_release, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
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
}
static final int TRANSACTION_init = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_initForMAC = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_initForName = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_startDevPackData = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_startDevUnpackData = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_startDevPrint = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
static final int TRANSACTION_startDevSetTimeOut = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
static final int TRANSACTION_startDevInitIdle = (android.os.IBinder.FIRST_CALL_TRANSACTION + 7);
static final int TRANSACTION_release = (android.os.IBinder.FIRST_CALL_TRANSACTION + 8);
static final int TRANSACTION_close = (android.os.IBinder.FIRST_CALL_TRANSACTION + 9);
static final int TRANSACTION_getPOSAddress = (android.os.IBinder.FIRST_CALL_TRANSACTION + 10);
}
/**
    * 初始化设备
    */
public int init() throws android.os.RemoteException;
public int initForMAC(java.lang.String mac) throws android.os.RemoteException;
public int initForName(java.lang.String name) throws android.os.RemoteException;
public int startDevPackData(byte[] inBuf, byte[] outBuf) throws android.os.RemoteException;
public int startDevUnpackData(byte[] inBuf, byte[] outBuf) throws android.os.RemoteException;
public int startDevPrint(byte[] buf, byte[] outBuf) throws android.os.RemoteException;
public int startDevSetTimeOut(int timeout) throws android.os.RemoteException;
public int startDevInitIdle() throws android.os.RemoteException;
public boolean release() throws android.os.RemoteException;
public void close() throws android.os.RemoteException;
/**
     * 获取pos地址
     */
public java.lang.String getPOSAddress() throws android.os.RemoteException;
}
