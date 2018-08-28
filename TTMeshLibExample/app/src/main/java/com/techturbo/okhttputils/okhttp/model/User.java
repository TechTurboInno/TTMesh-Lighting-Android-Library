package com.techturbo.okhttputils.okhttp.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;


/**
 * Created by zhubin on 2018/4/19.
 */


public class User  extends RealmObject{

    public User() {
        isDefault = "1";
        defaultMeshName = "TTMesh";
        defaultMeshPwd = "123";
        oldMeshName = "";
        oldMeshPwd = "";
    }

    @PrimaryKey
    public String isDefault;
    public String defaultMeshName;
    public String defaultMeshPwd;
    public String oldMeshName;
    public String oldMeshPwd;

    public String getOldMeshName() {
        return oldMeshName;
    }

    public void setOldMeshName(String oldMeshName) {
        this.oldMeshName = oldMeshName;
    }

    public String getOldMeshPwd() {
        return oldMeshPwd;
    }

    public void setOldMeshPwd(String oldMeshPwd) {
        this.oldMeshPwd = oldMeshPwd;
    }

    public String getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(String isDefault) {
        this.isDefault = isDefault;
    }

    public String getDefaultMeshName() {
        return defaultMeshName;
    }

    public void setDefaultMeshName(String defaultMeshName) {
        this.defaultMeshName = defaultMeshName;
    }

    public String getDefaultMeshPwd() {
        return defaultMeshPwd;
    }

    public void setDefaultMeshPwd(String getDefaultMeshPwd) {
        this.defaultMeshPwd = getDefaultMeshPwd;
    }
}
