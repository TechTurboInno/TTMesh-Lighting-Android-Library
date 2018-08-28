package com.techturbo.okhttputils.okhttp.model;

import com.techturbo.bluetooth.light.model.Light;
import com.telink.bluetooth.light.DeviceInfo;
import com.telink.bluetooth.light.LightPeripheral;

import java.util.HashMap;

/**
 * Created by zhubin on 2018/4/20.
 */

public class MessageEvent {
    public String msg="";
    public Light light;
    public DeviceInfo deviceInfo;

    public HashMap<String,String> params;

    public MessageEvent(String msg)
    {
        this.msg = msg ;
    }


    public String getMsg() {
        return msg;
    }


    public void setMsg(String msg) {
        this.msg = msg;
    }

}
