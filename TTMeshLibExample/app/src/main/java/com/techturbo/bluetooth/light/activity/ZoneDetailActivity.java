package com.techturbo.bluetooth.light.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.bigkoo.pickerview.TimePickerView;
import com.bigkoo.svprogresshud.SVProgressHUD;
import com.jzxiang.pickerview.TimePickerDialog;
import com.jzxiang.pickerview.listener.OnDateSetListener;
import com.techturbo.bluetooth.light.TelinkLightService;
import com.techturbo.bluetooth.light.model.Constants;
import com.techturbo.bluetooth.light.model.DBTiming;
import com.techturbo.bluetooth.light.util.ToolKit;
import com.techturbo.bluetooth.light.widget.SpinerAdapter;
import com.techturbo.bluetooth.light.widget.SpinerPopWindow;
import com.techturbo.okhttputils.okhttp.model.GroupInfo;
import com.techturbo.okhttputils.okhttp.model.MessageEvent;
import com.techturbo.okhttputils.okhttp.model.SystemConfig;
import com.techturbo.ttmeslight.R;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ZoneDetailActivity extends Activity implements OnDateSetListener {

    private final static String TAG = ZoneDetailActivity.class.getSimpleName();
    private ArrayList<String> mListType = new ArrayList<String>();  //类型列表
    private ArrayList<DBTiming> mShowTimingList = new ArrayList<DBTiming>();  //类型列表
    private ArrayList<String> mColorType = new ArrayList<String>();  //类型列表
    private ArrayList<String> mPatternList = new ArrayList<String>();  //类型列表
    private ArrayList<Integer> mIconList = new ArrayList<Integer>();
    private ArrayList<Integer> mSelectedIconList = new ArrayList<Integer>();
    private ArrayList<ImageView> mImageList = new ArrayList<ImageView>();
    List<GroupInfo> groupList;

    EditText nameET;
    EditText timeET;
    EditText timeET2;

    ImageButton backButton;
    Button saveButton;
    Button deteleButton;
    SeekBar sbBar;
    SeekBar ctBar;
    SeekBar colorBar;

    ImageView colorBkImageView;

    GroupInfo selectGroup = null;
    String selectTimingString;
    private SpinerAdapter mTimeAdapter;
    private SpinerPopWindow mTimeSpinerPopWindow;
    private SpinerAdapter mColorAdapter;
    private SpinerPopWindow mColorSpinerPopWindow;
    private SVProgressHUD mSVProgressHUD;
    int patternIndex;
    int lastIndex;
    String colorR;
    String colorG;
    String colorB;
    String timeFrom = "";
    String timeTo = "";

    private ImageView patternImageView1;
    private ImageView patternImageView2;
    private ImageView patternImageView3;
    private ImageView patternImageView4;
    private ImageView patternImageView5;
    private ImageView patternImageView6;
    private ImageView patternImageView7;
    private ImageView patternImageView8;
    private ImageView patternImageView9;
    private ImageView patternImageView10;

    private View.OnClickListener clickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            nameET.clearFocus();

            if (v == patternImageView1) {
                patternIndex = 0;
            }else if (v == patternImageView2){
                patternIndex = 1;
            }else if (v == patternImageView3){
                patternIndex = 2;
            }else if (v == patternImageView4){
                patternIndex = 3;
            }else if (v == patternImageView5){
                patternIndex = 4;
            }else if (v == patternImageView6){
                patternIndex = 5;
            }else if (v == patternImageView7){
                patternIndex = 6;
            }else if (v == patternImageView8){
                patternIndex = 7;
            }else if (v == patternImageView9){
                patternIndex = 8;
            }else if (v == patternImageView10){
                patternIndex = 9;
            }

            if (selectGroup == null) {
                return;
            }

            String value = mPatternList.get(patternIndex);
            int color = Color.parseColor(value);
            int redValue = Color.red(color);
            int blueValue = Color.blue(color);
            int greenValue = Color.green(color);

            int addr = 0x8000 + Integer.valueOf(selectGroup.getGroupId());
            byte opcode;
            byte[] params;

            opcode = (byte) 0xE2;
            params = new byte[]{0x04, (byte)redValue, (byte)greenValue, (byte)blueValue};

            TelinkLightService.Instance().sendCommand(opcode, addr, params);

        }
    };

    private SeekBar.OnSeekBarChangeListener barChangeListener = new SeekBar.OnSeekBarChangeListener() {

        private long preTime;
        private int delayTime = 100;

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            this.onValueChange(seekBar, seekBar.getProgress());
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
//            this.preTime = System.currentTimeMillis();
//            this.onValueChange(seekBar, seekBar.getProgress());
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {

        }

        private void onValueChange(View view, int progress) {

            nameET.clearFocus();

            if (selectGroup == null) {
                return;
            }

            int addr = 0x8000 + Integer.valueOf(selectGroup.getGroupId());
            byte opcode;
            byte[] params;

            if (view == sbBar) {
                opcode = (byte) 0xD2;
                params = new byte[]{(byte) progress};

                TelinkLightService.Instance().sendCommand(opcode, addr, params);

            } else if (view == ctBar) {

                int coldValue = (int)((100 - progress) * 255 / 100);
                int warmValue = (int)(progress * 255 / 100) ;

                opcode = (byte) 0xE2;
                params = new byte[]{0x06, (byte) sbBar.getProgress(), (byte) coldValue, (byte) warmValue};

                TelinkLightService.Instance().sendCommand(opcode, addr, params);
            } else if (view == colorBar) {


                //根据坐标获取
                Bitmap bitmap = ((BitmapDrawable)colorBkImageView.getDrawable()).getBitmap();

                int width = bitmap.getWidth();
                int x = width * progress / 100;

                if (x >= width) {
                    x = width - 4;
                }

                int pixel = bitmap.getPixel(x, 5);

                int redValue = Color.red(pixel);
                int blueValue = Color.blue(pixel);
                int greenValue = Color.green(pixel);

                opcode = (byte) 0xE2;
                params = new byte[]{0x04, (byte)redValue, (byte)greenValue, (byte)blueValue};

                TelinkLightService.Instance().sendCommand(opcode, addr, params);

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zone_detail);

        groupList = SystemConfig.shareConfig().readGroupInfos();

        int currentIndex = getIntent().getIntExtra("index",-1);
        if (currentIndex > -1) {
            selectGroup = groupList.get(currentIndex);
        }
        mSVProgressHUD = new SVProgressHUD(this);
        backButton = (ImageButton)findViewById(R.id.img_header_menu_left);
        nameET = (EditText)findViewById(R.id.nameET);
        timeET = (EditText)findViewById(R.id.timeET);
        timeET2 = (EditText)findViewById(R.id.timeET2);
        saveButton = (Button)findViewById(R.id.saveButton);
        deteleButton = (Button)findViewById(R.id.deleteButton);
        sbBar = findViewById(R.id.sb_brightness);
        sbBar.setOnSeekBarChangeListener(this.barChangeListener);

        ctBar = findViewById(R.id.sb_brightness4);
        ctBar.setOnSeekBarChangeListener(this.barChangeListener);

        colorBar = findViewById(R.id.sb_brightness5);
        colorBar.setOnSeekBarChangeListener(this.barChangeListener);

        colorBkImageView = (ImageView)findViewById(R.id.imageView11);

        patternImageView1 = (ImageView)findViewById(R.id.imageView_1);
        patternImageView2 = (ImageView)findViewById(R.id.imageView_2);
        patternImageView3 = (ImageView)findViewById(R.id.imageView_3);
        patternImageView4 = (ImageView)findViewById(R.id.imageView_4);
        patternImageView5 = (ImageView)findViewById(R.id.imageView_5);
        patternImageView6 = (ImageView)findViewById(R.id.imageView_6);
        patternImageView7 = (ImageView)findViewById(R.id.imageView_7);
        patternImageView8 = (ImageView)findViewById(R.id.imageView_8);
        patternImageView9 = (ImageView)findViewById(R.id.imageView_9);
        patternImageView10 = (ImageView)findViewById(R.id.imageView_10);

        mImageList.add(patternImageView1);
        mImageList.add(patternImageView2);
        mImageList.add(patternImageView3);
        mImageList.add(patternImageView4);
        mImageList.add(patternImageView5);
        mImageList.add(patternImageView6);
        mImageList.add(patternImageView7);
        mImageList.add(patternImageView8);
        mImageList.add(patternImageView9);
        mImageList.add(patternImageView10);

        int radius = ToolKit.dp2px(this, 17);

        String colorString = "#d0021b";
        mPatternList.add(colorString);

        colorString = "#f5a623";
        mPatternList.add(colorString);

        colorString = "#f8e71c";
        mPatternList.add(colorString);

        colorString = "#8b572a";
        mPatternList.add(colorString);

        colorString = "#7ed321";
        mPatternList.add(colorString);

        colorString = "#417505";
        mPatternList.add(colorString);

        colorString = "#bd10e0";
        mPatternList.add(colorString);

        colorString = "#9013fe";
        mPatternList.add(colorString);

        colorString = "#4a90e2";
        mPatternList.add(colorString);

        colorString = "#ffffff";
        mPatternList.add(colorString);

        patternImageView1.setOnClickListener(this.clickListener);
        patternImageView2.setOnClickListener(this.clickListener);
        patternImageView3.setOnClickListener(this.clickListener);
        patternImageView4.setOnClickListener(this.clickListener);
        patternImageView5.setOnClickListener(this.clickListener);
        patternImageView6.setOnClickListener(this.clickListener);
        patternImageView7.setOnClickListener(this.clickListener);
        patternImageView8.setOnClickListener(this.clickListener);
        patternImageView9.setOnClickListener(this.clickListener);
        patternImageView10.setOnClickListener(this.clickListener);

        mIconList.add(R.drawable.c1);
        mIconList.add(R.drawable.c2);
        mIconList.add(R.drawable.c3);
        mIconList.add(R.drawable.c4);
        mIconList.add(R.drawable.c5);
        mIconList.add(R.drawable.c6);
        mIconList.add(R.drawable.c7);
        mIconList.add(R.drawable.c8);
        mIconList.add(R.drawable.c9);
        mIconList.add(R.drawable.c10);

        mSelectedIconList.add(R.drawable.c1_selected);
        mSelectedIconList.add(R.drawable.c2_selected);
        mSelectedIconList.add(R.drawable.c3_selected);
        mSelectedIconList.add(R.drawable.c4_selected);
        mSelectedIconList.add(R.drawable.c5_selected);
        mSelectedIconList.add(R.drawable.c6_selected);
        mSelectedIconList.add(R.drawable.c7_selected);
        mSelectedIconList.add(R.drawable.c8_selected);
        mSelectedIconList.add(R.drawable.c9_selected);
        mSelectedIconList.add(R.drawable.c10_selected);

        for (int i = 0; i < mImageList.size(); i++) {
            ImageView v = mImageList.get(i);
            v.setImageResource(mIconList.get(i));
        }

        timeET.setFocusable(false);
        timeET2.setFocusable(false);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ZoneDetailActivity.this.finish();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean bNew = false;

                if (selectGroup == null) {
                    selectGroup = new GroupInfo();
                    bNew = true;
                    String groupid = SystemConfig.shareConfig().getUseableGroupID();
                    selectGroup.setGroupId(groupid);
                }

                selectGroup.setGroupName(nameET.getText().toString());
                selectGroup.setGroupTimeFrom(timeFrom);
                selectGroup.setGroupTimeTo(timeTo);
                selectGroup.setGroupBrightness(String.valueOf(sbBar.getProgress()));
                selectGroup.setGroupCT(String.valueOf(ctBar.getProgress()));
                selectGroup.setGroupColor(String.valueOf(colorBar.getProgress()));
                SystemConfig.shareConfig().saveGroupInfo(selectGroup);

                if (!bNew) {
                    ToolKit.setGroupTiming(selectGroup);
                }

                MessageEvent msg = new MessageEvent(Constants.Event_Save_ZoneDetail);
                EventBus.getDefault().post(msg);

                showSuccessDialog("Save Success");
            }
        });

        deteleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                deleteGroupInfo(selectGroup);

            }
        });


        nameET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nameET.setFocusable(true);
            }
        });

        timeET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TimePickerView pvTime = new TimePickerView.Builder(ZoneDetailActivity.this, new TimePickerView.OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date,View v) {//选中事件回调
                        Calendar calendar=Calendar.getInstance();

                        calendar.setTime(date);
                        int hour=calendar.get(Calendar.HOUR_OF_DAY);
                        int minute=calendar.get(Calendar.MINUTE);
                        int times = hour * 60 + minute;
                        timeFrom = String.valueOf(times);

                        timeET.setText(ToolKit.getTimingDesByMin(timeFrom));
//                    timeFragment.setData(currentMsg,String.valueOf(times));
                    }
                }).setType(new boolean[]{false, false, false, true, true, false}).setLabel("", "", "", "", "", "").setSubmitText("Done").setCancelText("Cancel").build();
                pvTime.show();
            }
        });

        timeET2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerView pvTime = new TimePickerView.Builder(ZoneDetailActivity.this, new TimePickerView.OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date,View v) {//选中事件回调
                        Calendar calendar=Calendar.getInstance();

                        calendar.setTime(date);
                        int hour=calendar.get(Calendar.HOUR_OF_DAY);
                        int minute=calendar.get(Calendar.MINUTE);
                        int times = hour * 60 + minute;

                        timeTo = String.valueOf(times);
                        timeET2.setText(ToolKit.getTimingDesByMin(timeTo));
//                    timeFragment.setData(currentMsg,String.valueOf(times));
                    }
                }).setType(new boolean[]{false, false, false, true, true, false}).setLabel("", "", "", "", "", "").setSubmitText("Done").setCancelText("Cancel").build();
                pvTime.show();
            }
        });

        colorR = "1";
        colorG = "1";
        colorB = "1";
        patternIndex = -1;

        if (selectGroup != null) {
            nameET.setText(selectGroup.getGroupName());

            sbBar.setProgress(Integer.valueOf(selectGroup.getGroupBrightness()));

            ctBar.setProgress(Integer.valueOf(selectGroup.getGroupCT()));

            colorBar.setProgress(Integer.valueOf(selectGroup.getGroupColor()));

            timeFrom = selectGroup.getGroupTimeFrom();
            timeTo = selectGroup.getGroupTimeTo();

            timeET.setText(ToolKit.getTimingDesByMin(selectGroup.getGroupTimeFrom()));
            timeET2.setText(ToolKit.getTimingDesByMin(selectGroup.getGroupTimeTo()));

            deteleButton.setVisibility(View.VISIBLE);
        }
    }

    public void deleteGroupInfo(final GroupInfo e) {
        String msg = "Delete this group?";
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(ZoneDetailActivity.this);
        normalDialog.setMessage(msg);
        normalDialog.setPositiveButton("Detele",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                        SystemConfig.shareConfig().deleteGroupInfo(e);

                        MessageEvent msg = new MessageEvent(Constants.Event_Save_ZoneDetail);
                        EventBus.getDefault().post(msg);

                        finish();
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

//        timeFragment.setData(currentMsg,String.valueOf(times));
    }

    private void showSuccessDialog(String msg){

        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(this);
        normalDialog.setMessage(msg);
        normalDialog.setPositiveButton(msg,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                        finish();
                    }
                });
        normalDialog.show();
    }

    private void showTipsDialog(String msg){

        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(this);
        normalDialog.setMessage(msg);
        normalDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                    }
                });
        normalDialog.show();
    }

}
