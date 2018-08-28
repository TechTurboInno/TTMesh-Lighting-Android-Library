package com.techturbo.bluetooth.light;

import com.techturbo.okhttputils.okhttp.model.SystemConfig;
import com.techturbo.okhttputils.okhttp.util.RealmHelper;
import com.telink.TelinkApplication;
import com.techturbo.bluetooth.light.model.Mesh;
import com.techturbo.bluetooth.light.util.FileSystem;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public final class TelinkLightApplication extends TelinkApplication {

    private Mesh mesh;

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        RealmConfiguration configuration=new RealmConfiguration.Builder()
                .name(RealmHelper.DB_NAME)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(configuration);
    }

    @Override
    public void doInit() {
        super.doInit();
        //AES.Security = true;
        if (FileSystem.exists("telink.meshs")) {
            this.mesh = (Mesh) FileSystem.readAsObject("telink.meshs");
        }

        SystemConfig.shareConfig().read();
        this.startLightService(TelinkLightService.class);
    }

    @Override
    public void doDestroy() {
        super.doDestroy();
    }

    public Mesh getMesh() {
        return this.mesh;
    }

    public void setMesh(Mesh mesh) {
        this.mesh = mesh;
    }

    public boolean isEmptyMesh() {
        return this.mesh == null;
    }
}
