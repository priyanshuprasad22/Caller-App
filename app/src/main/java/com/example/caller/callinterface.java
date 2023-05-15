package com.example.caller;

import android.telecom.Call;
import android.webkit.JavascriptInterface;

public  class callinterface {

    CallActivity callActivity;

    public callinterface(CallActivity callActivity)
    {
        this.callActivity =callActivity;
    }

    @JavascriptInterface
    public void onPeerConnected()
    {
        callActivity.onPeerConnected();
    }


}
