package com.xinhe.cashloan.biz;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.xinhe.cashloan.entity.Product;
import com.xinhe.cashloan.myapp.MyApplication;
import com.xinhe.cashloan.util.Constants;
import com.xinhe.cashloan.util.ExceptionUtil;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

/**
 * Created by tantan on 2017/7/12.
 */

public class GetNewProduct {
    public GetNewProduct(Context mContext) {
        this.mContext = mContext;
    }

    private Context mContext;

    public  void execute(){

        new Thread(new Runnable() {
            @Override
            public void run() {


                String URL = Constants.URL;
                String nameSpace = Constants.nameSpace;
                String method_Name = "NewProduct";
                String SOAP_ACTION = nameSpace + method_Name;
                SoapObject rpc = new SoapObject(nameSpace, method_Name);
//                rpc.addProperty("sAppName", Constants.appName);
//                rpc.addProperty("sPage", "3");
                rpc.addProperty("channel", "3");
                HttpTransportSE transport = new HttpTransportSE(URL);
                transport.debug = true;
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.bodyOut = rpc;
                envelope.dotNet = true;
                envelope.setOutputSoapObject(rpc);
                try {
                    transport.call(SOAP_ACTION, envelope);
                    SoapObject object = (SoapObject) envelope.bodyIn;
                    String str = object.getProperty("NewProductResult").toString();

                    if (!TextUtils.isEmpty(str) && !str.startsWith("1")&& !str.startsWith("2")) {
                        Gson gson = new Gson();
                        Product newProduct = gson.fromJson(str, Product.class);
                        MyApplication.newProduct=newProduct;
                    }
                } catch (Exception e) {
                    ExceptionUtil.handleException(e);
                }
            }
        }).start();
    }
}
