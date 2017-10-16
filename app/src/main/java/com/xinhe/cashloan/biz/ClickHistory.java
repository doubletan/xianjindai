package com.xinhe.cashloan.biz;

import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import com.xinhe.cashloan.myapp.MyApplication;
import com.xinhe.cashloan.util.Constants;
import com.xinhe.cashloan.util.DeviceUtil;
import com.xinhe.cashloan.util.ExceptionUtil;

import android.content.Context;
import android.text.TextUtils;

public class ClickHistory {

	private static JSONObject json;

	// 保存点击记录
	public static void saveClickHistory(String a, Context context) {

		if (DeviceUtil.IsNetWork(context) == false) {
			return;
		}

		String uid = "";
		if (TextUtils.isEmpty(MyApplication.userId)) {
			uid = "0";
		} else {
			uid = MyApplication.userId;
		}
		try {
			JSONObject json1 = new JSONObject();
			json1.put("xjdId", uid);
			json1.put("prdId", a);
			json1.put("channel", "andriod");
			json = new JSONObject();
			json.put("Record", json1);
		} catch (JSONException e1) {
			ExceptionUtil.handleException(e1);
		}
		new Thread(new Runnable() {

			@Override
			public void run() {
				String nameSpace = Constants.nameSpace;
				String methodName = "SetRecordAPP";
				String URL = Constants.URL;
				String SOAP_ACTION = nameSpace + methodName;
				SoapObject rpc = new SoapObject(nameSpace, methodName);
				rpc.addProperty("strJson", json.toString());
				HttpTransportSE transport = new HttpTransportSE(URL);
				transport.debug = true;
				SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
				envelope.bodyOut = rpc;
				envelope.dotNet = true;
				envelope.setOutputSoapObject(rpc);
				try {
					transport.call(SOAP_ACTION, envelope);
				} catch (Exception e) {
					ExceptionUtil.handleException(e);
				}
				SoapObject object = (SoapObject) envelope.bodyIn;
				String str = object.getProperty("SetRecordAPPResult").toString();
			}
		}).start();

	}

}
