package com.randomsegment.apn.ismshuttle;

import android.content.Context;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;

/**
 * Created by Rishabh on 01-10-2015.
 */
public class ToastAdListener extends AdListener {

    private Context mContext;
    private String mErrorReason;

    public ToastAdListener(Context context){this.mContext = context;}

    public void onAdLoader(){
        Toast.makeText(mContext, "onAdLoader()", Toast.LENGTH_SHORT).show();
    }


    public void onAdOpened(){
        Toast.makeText(mContext, "onAdOpened()", Toast.LENGTH_SHORT).show();
    }


    public void onAdClosed(){
        Toast.makeText(mContext, "onAdClosed()", Toast.LENGTH_SHORT).show();
    }


    public void onAdLeftApplication(){
        Toast.makeText(mContext, "onAdLeftApplication()", Toast.LENGTH_SHORT).show();
    }


    public void onAdFailedToLoad(int errorCode){
        mErrorReason ="";
        switch (errorCode)
        {
            case AdRequest.ERROR_CODE_INTERNAL_ERROR:
                mErrorReason = "Internal Error";
                break;
            case AdRequest.ERROR_CODE_INVALID_REQUEST:
                mErrorReason = "Invalid Request";
                break;
            case AdRequest.ERROR_CODE_NETWORK_ERROR:
                mErrorReason = "Network Error";
                break;
        }
        Toast.makeText(mContext, String.format("onAdFailedToLoad(%s)", mErrorReason), Toast.LENGTH_SHORT).show();
    }

    public String getErrorReason(){return mErrorReason == null ? "": mErrorReason; }
}
