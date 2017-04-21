package com.yifengcom.yfpos.callback;

import android.os.RemoteException;
import com.yifengcom.yfpos.service.CardModel;
import com.yifengcom.yfpos.service.DeviceModel;
import com.yifengcom.yfpos.service.ICallBack;

public class CallBackListener extends ICallBack.Stub{

	@Override
	public void onError(int errorCode, String errorMessage)
			throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGetDeviceInfo(String customerNo, String termNo,
			String batchNo, boolean existsMainKey, String sn, String version)
			throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTimeout() throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onResultSuccess(int ntype) throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTradeCancel() throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCalculateMacSuccess(byte[] mac) throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onReadSuccess(byte[] body) throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGetDateTimeSuccess(String dateTime) throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSwiperSuccess(CardModel cardModel) throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDetectIc() throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onInputPin() throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onShowPinPad(byte[] pad) throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onClosePinPad() throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGetPsamAndSt720Info(String psam, String st720)
			throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onReadPsamNo(String num) throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onIDCardResultData(byte[] body) throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDownloadProgress(long current, long total)
			throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGetDeviceInfoSuccess(DeviceModel deviceModel)
			throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onEMVTestOK(byte[] body) throws RemoteException {
		// TODO Auto-generated method stub
		
	}

}
