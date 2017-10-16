package com.xinhe.cashloan.biz;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.xinhe.cashloan.entity.Classification;
import com.xinhe.cashloan.entity.Product;
import com.xinhe.cashloan.myapp.MyApplication;
import com.xinhe.cashloan.util.Constants;
import com.xinhe.cashloan.util.ExceptionUtil;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

/**
 * Created by tantan on 2017/9/30.
 */

public class GetClassificationProduct {

    public GetClassificationProduct(Context mContext) {
        this.mContext = mContext;
    }

    private Context mContext;

    public  void execute(){
        new Thread(new Runnable() {
            @Override
            public void run() {


                String URL = Constants.URL;
                String nameSpace = Constants.nameSpace;
                String method_Name = "Get6Type";
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
                    String str = object.getProperty("Get6TypeResult").toString();

                    if (!TextUtils.isEmpty(str) && !str.startsWith("1")&& !str.startsWith("2")) {
                        Gson gson = new Gson();
                        Classification product = gson.fromJson(str, Classification.class);
                        MyApplication.classification=product;
                    }
                } catch (Exception e) {
                    ExceptionUtil.handleException(e);
                }
            }
        }).start();
    }
}
