package com.yfcomm.pos.utils;

import java.lang.reflect.Method;

import android.bluetooth.BluetoothDevice;
 
 /**
 * 蓝牙 操作工具
 * 
 * @author qin
 * 
 */
 
public class BlueUtils {
	
	/**
	 * 与设备配对
	 * @param btDevice
	 * @return
	 */
	public static boolean createBond(BluetoothDevice btDevice) {
		
		Method createBondMethod;
		try {
			createBondMethod = BluetoothDevice.class.getMethod("createBond");
			Boolean returnValue = (Boolean) createBondMethod.invoke(btDevice);
			return returnValue.booleanValue();
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * 与设备解除配对
	 * @param btDevice
	 * @return
	 */
	public static boolean removeBond(BluetoothDevice btDevice){
		Method removeBondMethod;
		try {
			removeBondMethod = BluetoothDevice.class.getMethod("removeBond");
			Boolean returnValue = (Boolean) removeBondMethod.invoke(btDevice);
			return returnValue.booleanValue();
		} catch (Exception e) {
			return false;
		}  
	}
	/**
	 * 设置 pin 码
	 * @param btDevice 蓝牙设备
	 * @param str      pin 码
	 * @return
	 */
	public static boolean setPin(BluetoothDevice btDevice,String pin){
		try {
			byte[] pinBytes =  convertPinToBytes(btDevice,pin);
			Method setPinMethod = btDevice.getClass().getDeclaredMethod("setPin", new Class[]{byte[].class});
			Boolean returnValue = (Boolean) setPinMethod.invoke(btDevice,new Object[]{pinBytes});
			return returnValue.booleanValue();
		}catch (Exception e) {
			return false;
		}  
	}
	
	/**
	 *  取消用户输入
	 * @param btDevice  蓝牙设备
	 * @return
	 */
	public static boolean cancelPairingUserInput(BluetoothDevice btDevice){
		try {
			Method cancelPairingUserInputMethod = BluetoothDevice.class.getMethod("cancelPairingUserInput");
			return ((Boolean)cancelPairingUserInputMethod.invoke(btDevice)).booleanValue();
		} catch (Exception e) {
			return false;
		} 
	}
	
	/**
	 * 取消用户配对
	 * @param btDevice
	 * @return
	 */
	public static boolean cancelBondProcess(BluetoothDevice btDevice){
		try {
			Method cancelBondProcessMethod = BluetoothDevice.class.getMethod("cancelBondProcess");
			return ((Boolean)cancelBondProcessMethod.invoke(btDevice)).booleanValue();
		} catch (Exception e) {
			return false;
		} 
	}
	
	/**
	 * 获取pin编码 
	 * @param btDevice
	 * @return
	 */
	private static byte[] convertPinToBytes(BluetoothDevice btDevice,String pin){
		try {
			Method cancelBondProcessMethod = BluetoothDevice.class.getMethod("convertPinToBytes",String.class);
			return (byte[])cancelBondProcessMethod.invoke(btDevice,pin);
		} catch (Exception e) {
			return null;
		} 
	}
	
}
