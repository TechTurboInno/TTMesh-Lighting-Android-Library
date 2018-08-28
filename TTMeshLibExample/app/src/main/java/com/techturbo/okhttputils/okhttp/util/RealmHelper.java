package com.techturbo.okhttputils.okhttp.util;


import com.techturbo.bluetooth.light.model.Group;
import com.techturbo.okhttputils.okhttp.model.Device;
import com.techturbo.okhttputils.okhttp.model.GroupInfo;
import com.techturbo.okhttputils.okhttp.model.MeshInfo;
import com.techturbo.okhttputils.okhttp.model.User;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by matou0289 on 2016/10/20.
 */

public class RealmHelper {
    public static final String DB_NAME = "myRealm.realm";
    private Realm mRealm;


    public RealmHelper() {

        mRealm = Realm.getDefaultInstance();
    }

    public User getDefaultUser() {
        User user = mRealm.where(User.class).equalTo("isDefault", "1").findFirst();

        if (user != null) {
            return mRealm.copyFromRealm(user);
        }
        else
        {
            return null;
        }
    }

    public void saveDefaultUser(final User user) {
        mRealm.beginTransaction();
        mRealm.copyToRealmOrUpdate(user);
        mRealm.commitTransaction();

    }

    public List<User> queryAllUsers() {
        RealmResults<User> users = mRealm.where(User.class).findAll();
        return mRealm.copyFromRealm(users);
    }

    public void addDevice(final Device device) {
        mRealm.beginTransaction();
        mRealm.copyToRealmOrUpdate(device);
        mRealm.commitTransaction();
    }

    public List<Device> queryAllDevicesByUserid(String uid, String did) {
        RealmResults<Device> devices = mRealm.where(Device.class).equalTo("userid", uid).equalTo("deviceid", did).findAll();
        return mRealm.copyFromRealm(devices);
    }

    public List<Device> queryAllDevices() {
        RealmResults<Device> devices = mRealm.where(Device.class).findAll();
        return mRealm.copyFromRealm(devices);
    }

    public List<MeshInfo> queryAllMeshInfo() {
        RealmResults<MeshInfo> infos = mRealm.where(MeshInfo.class).findAll();
        return mRealm.copyFromRealm(infos);
    }

    public void saveMeshInfo(final MeshInfo meshInfo) {
        mRealm.beginTransaction();
        mRealm.copyToRealmOrUpdate(meshInfo);
        mRealm.commitTransaction();

    }

    public void deleteMeshInfo(final MeshInfo meshInfo) {
        mRealm.beginTransaction();

        RealmResults<MeshInfo> infos = mRealm.where(MeshInfo.class).equalTo("id", meshInfo.getId()).findAll();

        infos.deleteFirstFromRealm();

        mRealm.commitTransaction();

    }

    public List<GroupInfo> queryAllGroups() {
        RealmResults<GroupInfo> infos = mRealm.where(GroupInfo.class).findAll();
        return mRealm.copyFromRealm(infos);
    }

    public void saveGroupInfo(final GroupInfo groupInfo) {
        mRealm.beginTransaction();
        mRealm.copyToRealmOrUpdate(groupInfo);
        mRealm.commitTransaction();

    }

    public void deleteGroupInfo(final GroupInfo groupInfo) {
        mRealm.beginTransaction();

        RealmResults<GroupInfo> infos = mRealm.where(GroupInfo.class).equalTo("groupId", groupInfo.getGroupId()).findAll();

        infos.deleteFirstFromRealm();

        mRealm.commitTransaction();

    }

    public Realm getRealm(){

        return mRealm;
    }

    public void close(){
        if (mRealm!=null){
            mRealm.close();
        }
    }
}
