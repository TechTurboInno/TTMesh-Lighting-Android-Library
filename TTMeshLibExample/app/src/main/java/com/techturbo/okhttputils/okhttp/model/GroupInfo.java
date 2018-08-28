package com.techturbo.okhttputils.okhttp.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by zhubin on 2018/8/22.
 */

public class GroupInfo extends RealmObject {

    @PrimaryKey
    private String groupId;
    private String groupName;
    private String groupTimeFrom;
    private String groupTimeTo;
    private String groupBrightness;
    private String groupCT;
    private String groupColor;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupTimeFrom() {
        return groupTimeFrom;
    }

    public void setGroupTimeFrom(String groupTimeFrom) {
        this.groupTimeFrom = groupTimeFrom;
    }

    public String getGroupTimeTo() {
        return groupTimeTo;
    }

    public void setGroupTimeTo(String groupTimeTo) {
        this.groupTimeTo = groupTimeTo;
    }

    public String getGroupBrightness() {
        return groupBrightness;
    }

    public void setGroupBrightness(String groupBrightness) {
        this.groupBrightness = groupBrightness;
    }

    public String getGroupCT() {
        return groupCT;
    }

    public void setGroupCT(String groupCT) {
        this.groupCT = groupCT;
    }

    public String getGroupColor() {
        return groupColor;
    }

    public void setGroupColor(String groupColor) {
        this.groupColor = groupColor;
    }
}
