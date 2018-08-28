package com.techturbo.bluetooth.light.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

import com.baoyz.actionsheet.ActionSheet;
import com.bigkoo.svprogresshud.SVProgressHUD;
import com.jzxiang.pickerview.TimePickerDialog;
import com.jzxiang.pickerview.listener.OnDateSetListener;
import com.techturbo.bluetooth.light.TelinkLightApplication;
import com.techturbo.bluetooth.light.TelinkLightService;
import com.techturbo.bluetooth.light.fragments.SystemFragment;
import com.techturbo.bluetooth.light.fragments.ZoneFragment;
import com.techturbo.bluetooth.light.model.Constants;
import com.techturbo.bluetooth.light.model.DBDevice;
import com.techturbo.bluetooth.light.model.DBZone;
import com.techturbo.bluetooth.light.model.Light;
import com.techturbo.bluetooth.light.model.SortLight;
import com.techturbo.bluetooth.light.widget.ProgressAlertDialog;
import com.techturbo.okhttputils.okhttp.model.MeshInfo;
import com.techturbo.okhttputils.okhttp.model.MessageEvent;
import com.techturbo.okhttputils.okhttp.model.SystemConfig;
import com.techturbo.ttmeslight.R;
import com.telink.bluetooth.LeBluetooth;
import com.telink.bluetooth.TelinkLog;
import com.telink.bluetooth.event.DeviceEvent;
import com.telink.bluetooth.event.LeScanEvent;
import com.telink.bluetooth.event.MeshEvent;
import com.telink.bluetooth.event.NotificationEvent;
import com.telink.bluetooth.event.ServiceEvent;
import com.telink.bluetooth.light.ConnectionStatus;
import com.telink.bluetooth.light.DeviceInfo;
import com.telink.bluetooth.light.LeAutoConnectParameters;
import com.telink.bluetooth.light.LeRefreshNotifyParameters;
import com.telink.bluetooth.light.LeScanParameters;
import com.telink.bluetooth.light.LeUpdateParameters;
import com.telink.bluetooth.light.LightAdapter;
import com.telink.bluetooth.light.OnlineStatusNotificationParser;
import com.telink.bluetooth.light.Parameters;

import com.techturbo.bluetooth.light.TelinkFragmentActivity;
import com.techturbo.bluetooth.light.model.Lights;
import com.techturbo.bluetooth.light.model.Mesh;
import com.techturbo.bluetooth.light.util.FragmentFactory;
import com.telink.util.BuildUtils;
import com.telink.util.Event;
import com.telink.util.EventListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public final class MainActivity extends TelinkFragmentActivity implements EventListener<String>, ActionSheet.ActionSheetListener, OnDateSetListener {

    private final static String TAG = MainActivity.class.getSimpleName();

    private static final int UPDATE_LIST = 0;
    private FragmentManager fragmentManager;
    private ZoneFragment zoneFragment;
    private SystemFragment systemFragment;
    private ProgressAlertDialog progress;
    SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private SVProgressHUD mSVProgressHUD;
    private static final String GPS_LOCATION_NAME = android.location.LocationManager.GPS_PROVIDER;
    private static final int REQUEST_PRESSMION_CODE = 10000;
    private final static String[] MULTI_PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION};

    private boolean bAddMode;
    private Fragment mContent;
    private MessageEvent currentMsg;
    private RadioGroup tabs;
    private String currentAddDeviceAddress = "-1";
    private TelinkLightApplication mApplication;

    private OnCheckedChangeListener checkedChangeListener = new OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {

            if (checkedId == R.id.tab_system) {
                switchContent(mContent, systemFragment);
            } else {
                switchContent(mContent, zoneFragment);
            }
        }
    };

    private int connectMeshAddress;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_LIST:
                    systemFragment.notifyDataSetChanged();
                    break;
            }
        }
    };

    private Handler mDelayHandler = new Handler();
    private Runnable mDelayTask = new SendCommandTask();
    private int delay = 200;

    private void TestData() {
//        SystemConfig.shareConfig().user.userid = "testzenggong";
//        SystemConfig.shareConfig().user.meshName = "aa1";
//        SystemConfig.shareConfig().user.meshPassword = "a1";
//        SystemConfig.shareConfig().user.factoryMeshName = "P1_Mesh";
//        SystemConfig.shareConfig().user.factoryMeshPassword = "123";
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate");
        //TelinkLog.ENABLE = false;
//        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.activity_main);

        EventBus.getDefault().register(this);

        this.mApplication = (TelinkLightApplication) this.getApplication();
        progress = new ProgressAlertDialog(this);
        mSVProgressHUD = new SVProgressHUD(this);

        this.fragmentManager = this.getFragmentManager();

        this.systemFragment = (SystemFragment) FragmentFactory
                .createFragment(R.id.tab_system);
        this.zoneFragment = (ZoneFragment) FragmentFactory
                .createFragment(R.id.tab_zone);

        this.tabs = (RadioGroup) this.findViewById(R.id.tabs);
        this.tabs.setOnCheckedChangeListener(this.checkedChangeListener);

        if (savedInstanceState == null) {

            FragmentTransaction transaction = this.fragmentManager
                    .beginTransaction();
            transaction.add(R.id.content, this.systemFragment).commit();

            this.mContent = this.systemFragment;
        }


    }

    @Override
    protected void onStart() {

        super.onStart();

        TestData();

        Log.d(TAG, "onStart");

        int result = BuildUtils.assetSdkVersion("4.4");
        Log.d(TAG, " Version : " + result);
        this.mApplication.addEventListener(DeviceEvent.STATUS_CHANGED, this);
        this.mApplication.addEventListener(NotificationEvent.ONLINE_STATUS, this);
        this.mApplication.addEventListener(ServiceEvent.SERVICE_CONNECTED, this);
        this.mApplication.addEventListener(MeshEvent.OFFLINE, this);
        this.mApplication.addEventListener(MeshEvent.ERROR, this);

        this.mApplication.addEventListener(LeScanEvent.LE_SCAN, this);
        this.mApplication.addEventListener(LeScanEvent.LE_SCAN_TIMEOUT, this);
//        this.mApplication.addEventListener(DeviceEvent.STATUS_CHANGED, this);
        this.mApplication.addEventListener(MeshEvent.UPDATE_COMPLETED, this);
        this.autoConnect();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!LeBluetooth.getInstance().isSupport(this)) {
            this.finish();
        }

        if (!LeBluetooth.getInstance().isEnabled()) {
            new AlertDialog.Builder(this).setMessage("Please turn on the bluetooth and try again!").show();
        }

        DeviceInfo deviceInfo = this.mApplication.getConnectDevice();

        if (deviceInfo != null) {
            this.connectMeshAddress = this.mApplication.getConnectDevice().meshAddress & 0xFF;
        }

        Log.d(TAG, "onResume");

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
        this.mApplication.removeEventListener(this);
        TelinkLightService.Instance().disableAutoRefreshNotify();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        this.mApplication.doDestroy();
        this.mDelayHandler.removeCallbacksAndMessages(null);
        EventBus.getDefault().unregister(this);
        Lights.getInstance().clear();
    }

    private void autoConnect() {

        if (TelinkLightService.Instance() != null) {

            if (TelinkLightService.Instance().getMode() != LightAdapter.MODE_AUTO_CONNECT_MESH) {

                Lights.getInstance().clear();
                this.systemFragment.notifyDataSetChanged();


                progress.show();

                bAddMode = false;
                MeshInfo mesh = SystemConfig.shareConfig().readDefaultMeshInfo();
                this.systemFragment.changeTitle(mesh);

                LeAutoConnectParameters connectParams = Parameters.createAutoConnectParameters();
                connectParams.setMeshName(mesh.getMeshName());
                connectParams.setPassword(mesh.getMeshPassword());
                connectParams.autoEnableNotification(false);
                connectParams.setConnectType("normal");

                TelinkLightService.Instance().autoConnect(connectParams);
            }

           /* LeRefreshNotifyParameters refreshNotifyParams = Parameters.createRefreshNotifyParameters();
            refreshNotifyParams.setRefreshRepeatCount(2);
            refreshNotifyParams.setRefreshInterval(2000);
            *//*refreshNotifyParams.set(Parameters.PARAM_AUTO_REFRESH_NOTIFICATION_REPEAT, 5);
            refreshNotifyParams.set(Parameters.PARAM_AUTO_REFRESH_NOTIFICATION_DELAY, 2000);*//*
            TelinkLightService.Instance().autoRefreshNotify(true, refreshNotifyParams);*/
        }
    }

    private void switchContent(Fragment from, Fragment to) {

        if (this.mContent != to) {

            this.mContent = to;

            FragmentTransaction transaction = this.fragmentManager
                    .beginTransaction();

            if (!to.isAdded()) {
                transaction.hide(from).add(R.id.content, to);
            } else {
                transaction.hide(from).show(to);
            }

            transaction.commit();
        }
    }

    private String formatMacAddress(String macAddress) {
        String[] sourceStrArray = macAddress.split(":");
        String result = "TTL";

        if (sourceStrArray.length > 1) {
            for (int i = sourceStrArray.length - 1; i >= 2; i--) {
                result += sourceStrArray[i];
            }
        }
        else
        {
            result = macAddress;
        }

        return result;
    }

    private void onAddDeviceStatusChanged(DeviceEvent event) {

        DeviceInfo deviceInfo = event.getArgs();

        switch (deviceInfo.status) {
            case LightAdapter.STATUS_UPDATE_MESH_COMPLETED:

                progress.dismiss();

                DBDevice deviceMap = new DBDevice();
                deviceMap.sn = formatMacAddress(deviceInfo.macAddress);
                deviceMap.netid = String.valueOf(deviceInfo.meshAddress);
                deviceMap.dtype = "";
                deviceMap.nickname = "   ";
                deviceMap.groupid = "1";
                currentAddDeviceAddress = deviceMap.netid;

                SystemConfig.shareConfig().deviceList.add(deviceMap);


                this.mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        autoConnect();
                    }
                }, 500);


                break;
            case LightAdapter.STATUS_UPDATE_MESH_FAILURE:
                this.startAddNewLightScan(1000);
                break;
        }
    }

    private void onDeviceStatusChanged(DeviceEvent event) {

        DeviceInfo deviceInfo = event.getArgs();

        switch (deviceInfo.status) {
            case LightAdapter.STATUS_LOGIN:
                this.connectMeshAddress = this.mApplication.getConnectDevice().meshAddress;
                //TelinkLightService.Instance().sendCommand((byte) 0xE4, 0xFFFF, new byte[0]);
                TelinkLightService.Instance().enableNotification();
                LeRefreshNotifyParameters refreshNotifyParams = Parameters.createRefreshNotifyParameters();
                refreshNotifyParams.setRefreshRepeatCount(3);
                refreshNotifyParams.setRefreshInterval(2000);
                /*refreshNotifyParams.set(Parameters.PARAM_AUTO_REFRESH_NOTIFICATION_REPEAT, 5);
                refreshNotifyParams.set(Parameters.PARAM_AUTO_REFRESH_NOTIFICATION_DELAY, 2000);*/
                //TelinkLightService.Instance().autoRefreshNotify(true, refreshNotifyParams);
                TelinkLightService.Instance().updateNotification();
                TelinkLightService.Instance().updateNotification(new byte[]{0x01, 0x22, 0x11});
                //this.mDelayHandler.postDelayed(this.mDelayTask, delay);
                this.show("login success");
                progress.dismiss();
                break;
            case LightAdapter.STATUS_CONNECTING:
                this.show("login");
                break;
            case LightAdapter.STATUS_LOGOUT:
                this.show("disconnect");
                progress.dismiss();
                break;
            default:
                break;
        }
    }

    private void onOnlineStatusNotify(NotificationEvent event) {


        TelinkLog.d("Thread ID : " + Thread.currentThread().getId());
        List<OnlineStatusNotificationParser.DeviceNotificationInfo> notificationInfoList;
        //noinspection unchecked
        notificationInfoList = (List<OnlineStatusNotificationParser.DeviceNotificationInfo>) event.parse();

        if (notificationInfoList == null || notificationInfoList.size() <= 0)
            return;

        for (OnlineStatusNotificationParser.DeviceNotificationInfo notificationInfo : notificationInfoList) {

            int meshAddress = notificationInfo.meshAddress;
            int brightness = notificationInfo.brightness;


            Light light = this.systemFragment.getDevice(meshAddress);

            if (light == null) {
                light = new Light();
                this.systemFragment.addDevice(light);
            }

            light.meshAddress = meshAddress;
            light.brightness = brightness;
            light.status = notificationInfo.connectStatus;

            if (light.meshAddress == this.connectMeshAddress) {
                light.textColor = this.getResources().getColorStateList(
                        R.color.theme_positive_color);
            } else {
                light.textColor = this.getResources().getColorStateList(
                        R.color.black);
            }

            light.updateIcon();
        }

        mHandler.obtainMessage(UPDATE_LIST).sendToTarget();
    }

    private void onServiceConnected(ServiceEvent event) {
        this.autoConnect();
    }

    private void onServiceDisconnected(ServiceEvent event) {

    }

    private void onMeshOffline(MeshEvent event) {

        if (currentAddDeviceAddress.equals("-1")) {
            progress.dismiss();
            SystemConfig.shareConfig().updateOfflineState();
            this.systemFragment.notifyDataSetChanged();
        }
    }

    private void onMeshError(MeshEvent event) {
        progress.dismiss();
        new AlertDialog.Builder(this).setMessage("The bluetooth has some problems, it will reconnect later").show();
    }

    @Override
    public void performed(Event<String> event) {
        switch (event.getType()) {
            case NotificationEvent.ONLINE_STATUS:
                this.onOnlineStatusNotify((NotificationEvent) event);
                break;
            case DeviceEvent.STATUS_CHANGED:
                if (bAddMode) {
                    this.onAddDeviceStatusChanged((DeviceEvent) event);
                }
                else {
                    this.onDeviceStatusChanged((DeviceEvent) event);
                }

                break;
            case MeshEvent.OFFLINE:
                this.onMeshOffline((MeshEvent) event);
                break;
            case MeshEvent.ERROR:
                this.onMeshError((MeshEvent) event);
                break;
            case ServiceEvent.SERVICE_CONNECTED:
                this.onServiceConnected((ServiceEvent) event);
                break;
            case ServiceEvent.SERVICE_DISCONNECTED:
                this.onServiceDisconnected((ServiceEvent) event);
                break;
            case LeScanEvent.LE_SCAN:
                if (bAddMode) {
                    this.onLeScan((LeScanEvent) event);
                }
                break;
            case LeScanEvent.LE_SCAN_TIMEOUT:
                if (bAddMode) {
                    progress.dismiss();
                    this.onLeScanTimeout((LeScanEvent) event);
                }
                break;
        }
    }

    private void onLeScan(LeScanEvent event) {

        Mesh mesh = this.mApplication.getMesh();
        String meshID = SystemConfig.shareConfig().getUseableMeshID();

        if (Integer.valueOf(meshID) >= 255) {
            this.show("mesh network amount overflow");
            return;
        }

        this.show("Adding Light");
        LeUpdateParameters params = Parameters.createUpdateParameters();
        params.setOldMeshName(SystemConfig.shareConfig().user.defaultMeshName);
        params.setOldPassword(SystemConfig.shareConfig().user.defaultMeshPwd);
        params.setNewMeshName(SystemConfig.shareConfig().user.defaultMeshName);
        params.setNewPassword(SystemConfig.shareConfig().user.defaultMeshPwd);

        DeviceInfo deviceInfo = event.getArgs();
        deviceInfo.meshAddress = Integer.valueOf(meshID);
        // FF:FF:AF:C0:BB:61
//        DeviceInfo list[] = new DeviceInfo[1];
//        list[0] = deviceInfo;
//        params.setUpdateDeviceList(list);

        params.set(Parameters.PARAM_DEVICE_LIST, deviceInfo);
        TelinkLightService.Instance().idleMode(true);
        TelinkLightService.Instance().updateMesh(params);
//        TelinkLightService.Instance().
    }

    private void onLeScanTimeout(LeScanEvent event) {
        SystemConfig.shareConfig().updateOfflineState();
        Toast.makeText(this, "onLeScanTimeout", Toast.LENGTH_LONG).show();

        this.mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                autoConnect();
            }
        }, 1000);
    }

    private class SendCommandTask implements Runnable {
        @Override
        public void run() {
            TelinkLightService.Instance().sendCommandNoResponse((byte) 0xE2, 0xFFFF, new byte[]{0x04, 0x00, 0x00, 0x00});
            mDelayHandler.postDelayed(this, delay);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)

    public void onEventMainThread(MessageEvent evnt)
    {
        String msg = evnt.getMsg();
        if (msg.equals(Constants.Event_Add_Device))
        {
            inputTitleDialog();

        }else if (msg.equals(Constants.Event_Re_Scan)) {
            TelinkLightService.Instance().idleMode(true);

            this.mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    autoConnect();
                }
            }, 500);

        }else if (msg.equals(Constants.Event_Save_ZoneDetail)) {
            zoneFragment.refreshData();
        }else if (msg.equals(Constants.Event_Show_Picker)) {
            List<MeshInfo> meshInfoLists = SystemConfig.shareConfig().readMeshInfos();
            String name[] = new String[meshInfoLists.size()];

            for (int i = 0; i < meshInfoLists.size(); i++) {
                name[i] = meshInfoLists.get(i).getMeshName();
            }

            ActionSheet.createBuilder(this, getSupportFragmentManager())
                    .setCancelButtonTitle("Cancel")
                    .setOtherButtonTitles(name)
                    .setCancelableOnTouchOutside(true)
                    .setListener(this).show();
        }
        else if (msg.equals(Constants.Event_ClickItem_Device)) {
            Light e = evnt.light;

            turnLightPower(e);
        }else if (msg.equals(Constants.Event_All_On)) {
            byte opcode = (byte) 0xD0;
            int address = 0xFFFF;
            byte[] params = new byte[]{0x01, 0x00, 0x00};
            TelinkLightService.Instance().sendCommandNoResponse(opcode, address,
                    params);
        }else if (msg.equals(Constants.Event_All_Off)) {
            byte opcode = (byte) 0xD0;
            int address = 0xFFFF;
            byte[] params = new byte[]{0x00, 0x00, 0x00};
            TelinkLightService.Instance().sendCommandNoResponse(opcode, address,
                    params);
        }else if (msg.equals(Constants.Event_Long_Click)) {
            Intent intent = new Intent(this, LightSettingActivity.class);
            Light light = evnt.light;

            intent.putExtra("meshAddress", light.meshAddress);
            startActivity(intent);
        }else if (msg.equals(Constants.Event_All_Color)) {
            Intent intent = new Intent(this, LightSettingActivity.class);

            intent.putExtra("all",true);
            startActivity(intent);
        }
    }

    private void inputTitleDialog() {

        LayoutInflater factory = LayoutInflater.from(this);
        // 把布局文件中的控件定义在View中
        final View textEntryView = factory.inflate(R.layout.inputdialog, null);
        final EditText nameET = (EditText)textEntryView.findViewById(R.id.editText);
        final EditText pwdET = (EditText)textEntryView.findViewById(R.id.editText2);

        nameET.setText(SystemConfig.shareConfig().user.getOldMeshName());
        pwdET.setText(SystemConfig.shareConfig().user.getOldMeshPwd());

        final EditText inputServer = new EditText(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Fill the old mesh info").setView(textEntryView)
                .setNegativeButton("Cancel", null);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                SystemConfig.shareConfig().user.setOldMeshName(nameET.getText().toString());
                SystemConfig.shareConfig().user.setOldMeshPwd(pwdET.getText().toString());

                SystemConfig.shareConfig().save();

                progress.show();

                bAddMode = true;
                startAddNewLightScan(100);

            }
        });
        builder.show();

    }

    private void showDeleteDialog(final Light e){

        String msg = "Delete Repeator?";
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(MainActivity.this);
        normalDialog.setMessage(msg);
        normalDialog.setPositiveButton("Detele",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                        byte opcode = (byte) 0xE3;
                        TelinkLightService.Instance().sendCommand(opcode, e.meshAddress, null);

                    }
                });
        normalDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                    }
                });
        // 显示
        normalDialog.show();
    }

    @Override
    public void onDateSet(TimePickerDialog timePickerDialog, long millseconds) {
        int times = getDateToString(millseconds);
//        timeFragment.setData(currentMsg,String.valueOf(times));
    }

    public int getDateToString(long time) {
        Date d = new Date(time);

        Calendar calendar=Calendar.getInstance();

        calendar.setTime(d);
        int hour=calendar.get(Calendar.HOUR);
        int minute=calendar.get(Calendar.MINUTE);

        return hour * 60 + minute;
    }

    @Override
    public void onOtherButtonClick(ActionSheet actionSheet, int index) {
        List<MeshInfo> meshInfoLists = SystemConfig.shareConfig().readMeshInfos();
        MeshInfo info = meshInfoLists.get(index);

        SystemConfig.shareConfig().saveDefaultMeshInfo(info);

        EventBus.getDefault().post(new MessageEvent(Constants.Event_Re_Scan));
    }

    @Override
    public void onDismiss(ActionSheet actionSheet, boolean isCancle) {
//        Toast.makeText(getApplicationContext(), "dismissed isCancle = " + isCancle, Toast.LENGTH_SHORT).show();
    }

    private void startAddNewLightScan(int delay) {

        this.mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {

                Mesh mesh = new Mesh();
                mesh.factoryName = SystemConfig.shareConfig().user.getOldMeshName();

                LeScanParameters params = Parameters.createScanParameters();
                params.setMeshName(mesh.factoryName);
                params.setOutOfMeshName("kick");
                params.setTimeoutSeconds(10);
                params.setScanMode(true);
                params.setConnectType("normal");

                TelinkLightService.Instance().startScan(params);
            }
        }, delay);

    }

    public void reloadGridViewWhenDelete(Light e) {

        Lights.getInstance().remove(e);

        SystemConfig.shareConfig().deleteLight(String.valueOf(e.meshAddress));

        for (int i = SystemConfig.shareConfig().sortDeviceList.size() - 1; i >= 0; i--) {
            SortLight sLight = SystemConfig.shareConfig().sortDeviceList.get(i);
            ArrayList<Light> list = sLight.deviceList;
            boolean deleteSuccess = false;

            for (int j = list.size() - 1; j >= 0; j--){
                Light light = list.get(j);

                if (light.deviceID.equals(e.deviceID)) {
                    list.remove(light);

                    if (list.size() == 0) {
                        SystemConfig.shareConfig().sortDeviceList.remove(sLight);
                    }

                    deleteSuccess = true;
                    break;
                }
            }

            if (deleteSuccess) {
                break;
            }
        }

        systemFragment.notifyDataSetChanged();
    }

    private void turnLightPower(Light light) {

        if (light.status == ConnectionStatus.OFFLINE)
            return;

        int dstAddr = light.meshAddress;

        Log.d(TAG, " on off " + dstAddr);

        byte opcode = (byte) 0xD0;

        if (light.status == ConnectionStatus.OFF) {
            if (TelinkLightService.Instance().isLogin()) {
                light.status = ConnectionStatus.ON;
                light.icon = R.drawable.bulb_on;
                systemFragment.notifyDataSetChanged();
                TelinkLightService.Instance().sendCommandNoResponse(opcode, dstAddr,
                        new byte[]{0x01, 0x00, 0x00});
            }
            else {
                Toast.makeText(this, "You can not operation because you are not in the mesh network", Toast.LENGTH_LONG).show();
            }


        } else if (light.status == ConnectionStatus.ON) {

            if (TelinkLightService.Instance().isLogin()) {
                light.status = ConnectionStatus.OFF;
                light.icon = R.drawable.bulb_off;
                systemFragment.notifyDataSetChanged();
                TelinkLightService.Instance().sendCommandNoResponse(opcode, dstAddr,
                        new byte[]{0x00, 0x00, 0x00});
            }
            else {
                Toast.makeText(this, "You can not operation because you are not in the mesh network", Toast.LENGTH_LONG).show();
            }

        }
    }

    private void showDeleteDialog(final DBZone zone){

        String msg = String.format("Delete %s?", zone.name);
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(MainActivity.this);
        normalDialog.setMessage(msg);
        normalDialog.setPositiveButton("Detele",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do

                    }
                });
        normalDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                    }
                });
        // 显示
        normalDialog.show();
    }

}
