package com.techturbo.okhttputils.okhttp.model;

import com.techturbo.bluetooth.light.model.Constants;
import com.techturbo.bluetooth.light.model.DBDevice;
import com.techturbo.bluetooth.light.model.DBTiming;
import com.techturbo.bluetooth.light.model.DBZone;
import com.techturbo.bluetooth.light.model.Light;
import com.techturbo.bluetooth.light.model.Mesh;
import com.techturbo.bluetooth.light.model.SortLight;
import com.techturbo.okhttputils.okhttp.util.RealmHelper;
import com.telink.bluetooth.light.ConnectionStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class SystemConfig{
    //构造方法私有化，这样外界就不能访问了
    private SystemConfig(){

        mRealmHelper=new RealmHelper();
        deviceList = new ArrayList<DBDevice>();
        zoneList = new ArrayList<DBZone>();
        sortDeviceList = new ArrayList<SortLight>();
        timingList = new ArrayList<DBTiming>();
        bleDeviceList = new ArrayList<Light>();

        gateway = null;
    };

    //当类被初始化的时候，就直接new出来
    private static SystemConfig instance = new SystemConfig();
    //提供一个方法，给他人调用
    public static SystemConfig shareConfig(){
        return instance;
    }

    private RealmHelper mRealmHelper;

    public User user;
    public ArrayList<DBDevice> deviceList;
    public ArrayList<DBZone> zoneList;
    public ArrayList<DBTiming> timingList;
    public DBDevice gateway;
    public boolean gatewayIsOnline;
    public ArrayList<SortLight> sortDeviceList;
    public ArrayList<Light> bleDeviceList;


    public String getUseableMeshID() {
        for (int i = 1; i <= 255; i++) {
            boolean bUsed = true;

            for (int j = 0; j < this.deviceList.size(); j++) {
                DBDevice device = this.deviceList.get(j);
                int netid = Integer.valueOf(device.netid);

                if (netid == i) {
                    bUsed = false;
                    break;
                }
            }

            if (bUsed) {
                return String.valueOf(i);
            }
        }

        return "-1";
    }

    public String getUseableGroupID() {
        List<GroupInfo> list = readGroupInfos();

        for (int i = 1; i <= 16; i++) {
            boolean bUsed = true;

            for (int j = 0; j < list.size(); j++) {
                GroupInfo info = list.get(j);
                int netid = Integer.valueOf(info.getGroupId());

                if (netid == i) {
                    bUsed = false;
                    break;
                }
            }

            if (bUsed) {
                return String.valueOf(i);
            }
        }

        return "-1";
    }

    public DBZone getZoneByGroupID(String groupID) {
        for (int j = 0; j < this.zoneList.size(); j++) {
            DBZone zone = this.zoneList.get(j);
            if (zone.groupid.equals(groupID)) {
                return zone;
            }
        }

        return null;
    }

    public Light getLightByDeviceID(String deviceID) {
        for (int j = 0; j < this.sortDeviceList.size(); j++) {
            SortLight sLight = this.sortDeviceList.get(j);
            ArrayList<Light> list = sLight.deviceList;

            for (int i = 0; i < list.size(); i++) {
                Light e = list.get(i);

                if (e.deviceID.equals(deviceID)) {
                    return e;
                }
            }
        }

        return null;
    }

    public void deleteLight(String meshAddress) {
        for (int j = this.bleDeviceList.size() - 1; j >=0 ;j--) {
            Light e = this.bleDeviceList.get(j);

            if (e.meshAddress == Integer.valueOf(meshAddress)) {
                this.bleDeviceList.remove(j);
            }
        }

        for (int j = this.deviceList.size() - 1; j >=0 ;j--) {
            DBDevice e = this.deviceList.get(j);

            if (Integer.valueOf(e.netid) == Integer.valueOf(meshAddress)) {
                this.deviceList.remove(j);
            }
        }
    }

    public DBDevice getDBDeviceByMeshAddress(String meshAddress) {
        for (int j = 0; j < this.deviceList.size(); j++) {
            DBDevice dbDevice = this.deviceList.get(j);

            if (dbDevice.netid.equals(meshAddress)) {
                return dbDevice;
            }

        }

        return null;
    }

    public Light getLightByMeshAddress(String meshAddress) {
        for (int j = 0; j < this.bleDeviceList.size(); j++) {
            Light e = this.bleDeviceList.get(j);

            if (e.meshAddress == Integer.valueOf(meshAddress)) {
                return e;
            }

        }

        return null;
    }

    public void addBleDeviceModel(Light e) {
        boolean bExists = false;

        for (int j = 0; j < this.bleDeviceList.size(); j++) {
            Light light = this.bleDeviceList.get(j);
            if (light.meshAddress == e.meshAddress) {
                light.status = e.status;
                light.updateIcon();
                bExists = true;
                break;
            }
        }

        if (!bExists) {
            this.bleDeviceList.add(e);
        }
    }

    public void updateOfflineState(){
        for (int j = 0; j < this.sortDeviceList.size(); j++) {
            SortLight sLight = this.sortDeviceList.get(j);
            ArrayList<Light> list = sLight.deviceList;

            for (int i = 0; i < list.size(); i++) {
                Light e = list.get(i);

                e.status = ConnectionStatus.OFFLINE;
                e.updateIcon();
            }
        }

        for (int j = 0; j < this.bleDeviceList.size(); j++) {
            Light light = this.bleDeviceList.get(j);

            light.status = ConnectionStatus.OFFLINE;
            light.updateIcon();
        }
    }

    public List<GroupInfo> readGroupInfos() {
        List<GroupInfo> list = mRealmHelper.queryAllGroups();

        if (list == null || list.size() == 0) {
            list = new ArrayList<GroupInfo>();

            GroupInfo info = new GroupInfo();
            info.setGroupId("1");
            info.setGroupName("Group 1");
            info.setGroupTimeFrom("360");
            info.setGroupTimeTo("1080");
            info.setGroupCT("50");
            info.setGroupColor("50");
            info.setGroupBrightness("50");

            list.add(info);
        }

        return list;
    }

    public List<MeshInfo> readMeshInfos() {
        List<MeshInfo> list = mRealmHelper.queryAllMeshInfo();

        if (list == null || list.size() == 0) {
            list = new ArrayList<MeshInfo>();

            MeshInfo info = new MeshInfo();
            info.setId("1");
            info.setMeshName("TTMesh");
            info.setMeshPassword("123");

            list.add(info);
        }

        return list;
    }

    public MeshInfo readDefaultMeshInfo() {
        MeshInfo info = new MeshInfo();
        info.setMeshName(user.getDefaultMeshName());
        info.setMeshPassword(user.getDefaultMeshPwd());

        return info;
    }

    public void saveDefaultMeshInfo(MeshInfo info) {
        user.setDefaultMeshName(info.getMeshName());
        user.setDefaultMeshPwd(info.getMeshPassword());

        mRealmHelper.saveDefaultUser(user);
    }

    public void saveMeshInfo(List<MeshInfo> list)
    {
        for (int i = 0; i < list.size(); i++) {
            MeshInfo info = list.get(i);

            mRealmHelper.saveMeshInfo(info);
        }
    }

    public void deleteMeshInfo(MeshInfo info) {
        mRealmHelper.deleteMeshInfo(info);
    }

    public void saveGroupInfo(GroupInfo groupInfo)
    {
        mRealmHelper.saveGroupInfo(groupInfo);
    }

    public void deleteGroupInfo(GroupInfo groupInfo)
    {
        mRealmHelper.deleteGroupInfo(groupInfo);
    }

    public void save() {
        mRealmHelper.saveDefaultUser(user);
    }

    public void read() {
        user = mRealmHelper.getDefaultUser();

        if (user == null) {
            user = new User();
        }
    }
}
