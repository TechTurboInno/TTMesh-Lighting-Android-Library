package com.techturbo.bluetooth.light.fragments.viewpagercards;

/**
 * Created by zhubin on 2018/6/20.
 */

public class CardItem {
    private String zoneName;
    private String zoneDes;

    public CardItem(String name, String des) {
        zoneName = name;
        zoneDes = des;
    }

    public String getName() {
        return zoneName;
    }

    public String getDes() {
        return zoneDes;
    }
}
