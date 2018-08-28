package com.techturbo.bluetooth.light.model;

import com.techturbo.bluetooth.light.util.FileSystem;
import com.telink.util.MeshUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Mesh implements Serializable {

    private static final long serialVersionUID = 1L;

    public String name;
    public String password;
    public String factoryName;
    public String factoryPassword;

    //public String otaDevice;

    public List<Integer> allocDeviceAddress;
    public List<DeviceInfo> devices = new ArrayList<>();

    public int getDeviceAddress() {
        int address = MeshUtils.allocDeviceAddress(this.allocDeviceAddress);

        if (address != -1) {
            if (this.allocDeviceAddress == null)
                this.allocDeviceAddress = new ArrayList<>();
            this.allocDeviceAddress.add(address);
        }

        return address;
    }

    public DeviceInfo getDevice(int meshAddress) {
        if (this.devices == null)
            return null;

        for (DeviceInfo info : devices) {
            if (info.meshAddress == meshAddress)
                return info;
        }
        return null;
    }

    public boolean saveOrUpdate() {
        return FileSystem.writeAsObject("telink.meshs", this);
    }
}
