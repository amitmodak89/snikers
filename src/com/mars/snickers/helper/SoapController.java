package com.mars.snickers.helper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.mars.snickers.R;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.Calendar;


/**
 * Created by malpani on 9/11/14.
 */
public class SoapController {


    private static final String TAG = "Soap";

    public static enum SoapConstant {
        NAMESPACE("http://tempuri.org/"),
        URL("http://clients.resultrix-apps.com/SnickersWS/Service.asmx"),
        METHOD_AuthenticateFacebookUser("AuthenticateFacebookUser"),
        METHOD_AuthenticateNormalUser("AuthenticateNormalUser"),
        METHOD_GetAllFotoDetails("GetAllFotoDetails"),
        METHOD_GetAllUserDetails("GetAllUserDetails"),
        METHOD_GetFotoDetails("GetFotoDetails"),
        METHOD_GetUserDetails("GetUserDetails"),
        METHOD_RegisterNormalUser("RegisterNormalUser"),
        METHOD_UploadPicture("UploadPicture");

        String value;

        SoapConstant(String s) {
            this.value = s;
        }

        public String getValue() {
            return value;
        }

    }


    public static PropertyInfo generatePropertyInfo(String key, Object value, Object classType) {
        //Property which holds input parameters
        PropertyInfo info = new PropertyInfo();
        info.setName(key);
        info.setValue(value==null?"":value);
        info.setType(classType);
        Log.d("PropertyInfo", "Key: "+key+" Value: "+value);
        return info;
    }

    public static String makeRequest(SoapConstant methodName, ArrayList<PropertyInfo> inputDataArray) {

        String result = null;



            Log.i(TAG, "SOAP_NAMESPACE: " + SoapConstant.NAMESPACE.getValue());
            //Create request
            SoapObject request = new SoapObject(SoapConstant.NAMESPACE.getValue(), methodName.getValue());

            //Add the property to request object
            for (PropertyInfo data : inputDataArray)
                request.addProperty(data);

            //Create envelope
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;

            //Set output SOAP object
            envelope.setOutputSoapObject(request);

            //Create HTTP call object
            HttpTransportSE androidHttpTransport = new HttpTransportSE(SoapConstant.URL.getValue());

            try {

                Log.i(TAG, "SOAP_ACTION: " + SoapConstant.NAMESPACE.getValue() + methodName.getValue());
                //Invole web service
                androidHttpTransport.call(SoapConstant.NAMESPACE.getValue() + methodName.getValue(), envelope);

                //Get the response
                SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
                result = response.toString();
                Log.i(TAG, "SOAP_RESULT: " + result);

            } catch (Exception e) {
                e.printStackTrace();
            }


        return result;
    }

    public static boolean isNetworkAvailable(Context context) {
        boolean result = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        result =  activeNetworkInfo != null && activeNetworkInfo.isConnected();
        if(!result)
            Toast.makeText(context, context.getString(R.string.noInternetFound), Toast.LENGTH_SHORT).show();
        return result;
    }




}
