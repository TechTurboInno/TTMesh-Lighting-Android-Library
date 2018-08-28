package com.techturbo.bluetooth.light.util;

import android.content.Context;
import android.graphics.Color;
import android.graphics.RectF;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;

import com.techturbo.bluetooth.light.TelinkLightService;
import com.techturbo.bluetooth.light.model.DBZone;
import com.techturbo.bluetooth.light.model.Light;
import com.techturbo.okhttputils.okhttp.model.GroupInfo;
import com.techturbo.okhttputils.okhttp.model.SystemConfig;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhubin on 2018/6/15.
 */

public abstract class ToolKit {

    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    public static boolean isFixedString(String s) {
        if (s.equals("Dawn") || s.equals("Dusk") || s.equals("1hr B4 Dusk")) {
            return true;
        }

        return false;
    }

    public static String getTimingDesByMin(String minitues) {

        if (!ToolKit.isNumeric(minitues)) {
            return minitues;
        }

        int hour = Integer.valueOf(minitues) / 60;
        int min = Integer.valueOf(minitues) % 60;

        if (hour < 12) {
            return  String.format("%02d:%02d AM", hour, min);
        }
        else
        {
            return String.format("%02d:%02d PM", hour - 12, min);
        }
    }

    public static String getTimeFromTimingDes(String timeDes) {

        return "";
    }

    public static void SleepTime(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void setDeviceToGroup(GroupInfo groupInfo, Light light) {

        int groupAddress = Integer.valueOf(groupInfo.getGroupId());
        int dstAddress = light.meshAddress;

        byte opcode;
        byte[] params;

        // delete all the groups
        opcode = (byte) 0xD7;
        params = new byte[]{0x00, (byte) (0xFF), (byte) (0xFF)};
        TelinkLightService.Instance().sendCommand(opcode, dstAddress, params);

        ToolKit.SleepTime(50);

        // all new group
        params = new byte[]{0x01, (byte) (groupAddress), (byte) (0x80)};
        TelinkLightService.Instance().sendCommand(opcode, dstAddress, params);

        ToolKit.SleepTime(50);

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH)+1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);

        // set current time
        opcode = (byte) 0xE4;
        params = new byte[]{(byte) (year & 0xFF), (byte) (year >> 8 & 0xFF), (byte) (month), (byte) (day), (byte) (hour), (byte) (minute), (byte) (second)};
        TelinkLightService.Instance().sendCommand(opcode, dstAddress, params);

        ToolKit.SleepTime(50);

        // delete all the alarm
        opcode = (byte) 0xE5;
        params = new byte[]{(byte) (0x01), (byte) (0xFF), (byte) (0x00), (byte) (0x00), (byte) (0x00), (byte) (0x00), (byte) (0x00), (byte) (0x00)};
        TelinkLightService.Instance().sendCommand(opcode, dstAddress, params);

        ToolKit.SleepTime(50);

        // add alarm of turn on
        int fromhour = Integer.valueOf(groupInfo.getGroupTimeFrom()) / 60;
        int frommin = Integer.valueOf(groupInfo.getGroupTimeFrom()) % 60;

        opcode = (byte) 0xE5;
        params = new byte[]{(byte) (0x00), (byte) (0x00), (byte) (0x91), (byte) (0x01), (byte) (0x7f), (byte) (fromhour), (byte) (frommin), (byte) (0x00), (byte) (0x00)};
        TelinkLightService.Instance().sendCommand(opcode, dstAddress, params);

        ToolKit.SleepTime(50);

        // add alarm of turn off
        int tohour = Integer.valueOf(groupInfo.getGroupTimeTo()) / 60;
        int tomin = Integer.valueOf(groupInfo.getGroupTimeTo()) % 60;

        opcode = (byte) 0xE5;
        params = new byte[]{(byte) (0x00), (byte) (0x00), (byte) (0x90), (byte) (0x01), (byte) (0x7f), (byte) (tohour), (byte) (tomin), (byte) (0x00), (byte) (0x00)};
        TelinkLightService.Instance().sendCommand(opcode, dstAddress, params);
    }

    public static void setGroupTiming(GroupInfo groupInfo) {

        int addr = 0x8000 + Integer.valueOf(groupInfo.getGroupId());
        String from = groupInfo.getGroupTimeFrom();
        String to = groupInfo.getGroupTimeTo();

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH)+1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);

        // set current time
        byte opcode = (byte) 0xE4;
        byte[] params = new byte[]{(byte) (year & 0xFF), (byte) (year >> 8 & 0xFF), (byte) (month), (byte) (day), (byte) (hour), (byte) (minute), (byte) (second)};
        TelinkLightService.Instance().sendCommand(opcode, addr, params);

        ToolKit.SleepTime(50);

        // delete all the alarm
        opcode = (byte) 0xE5;
        params = new byte[]{(byte) (0x01), (byte) (0xFF), (byte) (0x00), (byte) (0x00), (byte) (0x00), (byte) (0x00), (byte) (0x00), (byte) (0x00)};
        TelinkLightService.Instance().sendCommand(opcode, addr, params);

        ToolKit.SleepTime(50);

        // add alarm of turn off
        int tohour = Integer.valueOf(to) / 60;
        int tomin = Integer.valueOf(to) % 60;

        opcode = (byte) 0xE5;
        params = new byte[]{(byte) (0x00), (byte) (0x00), (byte) (0x90), (byte) (0x01), (byte) (0x7f), (byte) (tohour), (byte) (tomin), (byte) (0x00), (byte) (0x00)};
        TelinkLightService.Instance().sendCommand(opcode, addr, params);

        ToolKit.SleepTime(50);

        // add alarm of turn on
        int fromhour = Integer.valueOf(from) / 60;
        int frommin = Integer.valueOf(from) % 60;

        opcode = (byte) 0xE5;
        params = new byte[]{(byte) (0x00), (byte) (0x00), (byte) (0x91), (byte) (0x01), (byte) (0x7f), (byte) (fromhour), (byte) (frommin), (byte) (0x00), (byte) (0x00)};
        TelinkLightService.Instance().sendCommand(opcode, addr, params);
    }

    public static String utc2Local(String utcTime) {
        SimpleDateFormat utcFormater = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss+00:00");//UTC时间格式
        utcFormater.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date gpsUTCDate = null;
        try {
            gpsUTCDate = utcFormater.parse(utcTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat localFormater = new SimpleDateFormat("HH");//当地时间格式
        localFormater.setTimeZone(TimeZone.getDefault());
        String hour = localFormater.format(gpsUTCDate.getTime());

        SimpleDateFormat localFormater1 = new SimpleDateFormat("mm");//当地时间格式
        localFormater1.setTimeZone(TimeZone.getDefault());
        String min = localFormater.format(gpsUTCDate.getTime());

        int total = Integer.valueOf(hour) * 60 + Integer.valueOf(min);
        return String.valueOf(total);
    }

    public static ShapeDrawable createRoundCornerShapeDrawable(float radius, float borderLength, int borderColor) {
        float[] outerRadii = new float[8];
        float[] innerRadii = new float[8];
        for (int i = 0; i < 8; i++) {
            outerRadii[i] = radius + borderLength;
            innerRadii[i] = radius;
        }

        ShapeDrawable sd = new ShapeDrawable(new RoundRectShape(outerRadii, new RectF(borderLength, borderLength,
                borderLength, borderLength), innerRadii));
        sd.getPaint().setColor(borderColor);

        return sd;
    }

    public static int dp2px(Context context, float dpValue){
        float scale=context.getResources().getDisplayMetrics().density;
        return (int)(dpValue*scale+0.5f);
    }

    /**
     * px转换成dp
     */
    public static int px2dp(Context context,float pxValue){
        float scale=context.getResources().getDisplayMetrics().density;
        return (int)(pxValue/scale+0.5f);
    }
    /**
     * sp转换成px
     */
    public static int sp2px(Context context,float spValue){
        float fontScale=context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue*fontScale+0.5f);
    }
    /**
     * px转换成sp
     */
    public static int px2sp(Context context,float pxValue){
        float fontScale=context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue/fontScale+0.5f);
    }
}

