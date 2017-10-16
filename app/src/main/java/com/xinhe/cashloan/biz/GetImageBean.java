package com.xinhe.cashloan.biz;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.xinhe.cashloan.entity.ImagerBean;
import com.xinhe.cashloan.myapp.MyApplication;
import com.xinhe.cashloan.util.Constants;
import com.xinhe.cashloan.util.ExceptionUtil;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.Collections;

/**
 * Created by apple on 2017/4/12.
 *   bunder
 *
 */

public class GetImageBean {
    private Context mContext;

    public GetImageBean(Context mContext) {
        this.mContext = mContext;
    }

    public  void execute(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String URL = Constants.URL;
                String nameSpace = Constants.nameSpace;
                String method_Name = "Daohang";
                String SOAP_ACTION = nameSpace + method_Name;
                SoapObject rpc = new SoapObject(nameSpace, method_Name);
                HttpTransportSE transport = new HttpTransportSE(URL);
                transport.debug = true;
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.bodyOut = rpc;
                envelope.dotNet = true;
                envelope.setOutputSoapObject(rpc);
                try {
                    transport.call(SOAP_ACTION, envelope);
                    SoapObject object = (SoapObject) envelope.bodyIn;
                    String result = object.getProperty("DaohangResult").toString();
                    if (!TextUtils.isEmpty(result)) {
                        Gson gson = new Gson();
                        ImagerBean imagerBean = gson.fromJson(result, ImagerBean.class);
                        Collections.sort(imagerBean.getDaohang());
                        MyApplication.imagerBean=imagerBean;
                    }
                } catch (Exception e) {
                    ExceptionUtil.handleException(e);
                }
            }
        }).start();
    }
}
