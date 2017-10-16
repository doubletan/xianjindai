package com.xinhe.cashloan.biz;

import com.xinhe.cashloan.myapp.MyApplication;
import com.xinhe.cashloan.util.Constants;
import com.xinhe.cashloan.util.ExceptionUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

/**
 * Created by tantan on 2017/7/14.
 */

public class BrowsingHistory {

    public  void execute(final String prdId, final String page) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject js1=new JSONObject();
                final JSONObject js2=new JSONObject();
                try {
                    js1.put("userId", MyApplication.userId);
                    js1.put("prdId",prdId);
                    js1.put("channel",Constants.channel);
                    js1.put("channel2",Constants.channel1);
                    js1.put("page",page);
                    js2.put("Record",js1);
                } catch (JSONException e) {
                    ExceptionUtil.handleException(e);
                }
                String URL = Constants.URL;
                String nameSpace = Constants.nameSpace;
                String method_Name = "SetRecord";
                String SOAP_ACTION = nameSpace + method_Name;
                SoapObject rpc = new SoapObject(nameSpace, method_Name);
                rpc.addProperty("strJson", js2.toString());
                HttpTransportSE transport = new HttpTransportSE(URL);
                transport.debug = true;
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.bodyOut = rpc;
                envelope.dotNet = true;
                envelope.setOutputSoapObject(rpc);
                try {
                    transport.call(SOAP_ACTION, envelope);
                    SoapObject object = (SoapObject) envelope.bodyIn;
                    String str = object.getProperty("SetRecordResult").toString();
                } catch (Exception e) {
                    ExceptionUtil.handleException(e);
                }
            }
        }).start();
    }
}
