package com.techturbo.bluetooth.light.model;

import android.content.res.ColorStateList;

import com.techturbo.ttmeslight.R;
import com.telink.bluetooth.light.ConnectionStatus;
import com.telink.bluetooth.light.DeviceInfo;

public final class Light {

    public String name;
    public String macAddress;
    public String deviceID;
    public String type;
    public int meshAddress;
    public int brightness;
    public int color;
    public int temperature;
    public ConnectionStatus status;
    public DeviceInfo raw;
    public boolean selected;
    public ColorStateList textColor;
    public int icon = R.drawable.icon_light_on;
    public String nickName;
    public String groupid;

    public String getLabel() {
        return Integer.toString(this.meshAddress, 16) + ":" + this.brightness;
    }

    public String getLabel1() {
        return this.name + " : " + Integer.toString(this.meshAddress, 16);
    }

    public String getLabel2() {
        return Integer.toString(this.meshAddress, 16);
    }

    public String getShowName() {

        return String.format("bulb %d",meshAddress);
    }

    public void updateIcon() {

        if (this.status == ConnectionStatus.OFFLINE) {
            this.icon = R.drawable.bulb_off;

        } else if (this.status == ConnectionStatus.OFF) {
            this.icon = R.drawable.bulb_off;

        } else if (this.status == ConnectionStatus.ON) {
            this.icon = R.drawable.bulb_on;
        }
    }
}
