package com.techturbo.bluetooth.light.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.techturbo.bluetooth.light.fragments.viewholders.LightSectionAdapter;
import com.techturbo.bluetooth.light.model.Constants;
import com.techturbo.bluetooth.light.model.Light;
import com.techturbo.bluetooth.light.util.ToolKit;
import com.techturbo.okhttputils.okhttp.model.GroupInfo;
import com.techturbo.okhttputils.okhttp.model.MessageEvent;
import com.techturbo.okhttputils.okhttp.model.SystemConfig;
import com.techturbo.ttmeslight.R;
import com.truizlop.sectionedrecyclerview.SectionedSpanSizeLookup;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

public class SelectGroupActivity extends Activity {

    RecyclerView recycler;
    ImageButton backButton;
    Button doneButton;
    private LightSectionAdapter adapter;
    GroupInfo selectGroup = null;
    List<GroupInfo> groupList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_group);

        EventBus.getDefault().register(this);
        groupList = SystemConfig.shareConfig().readGroupInfos();
        int currentIndex = getIntent().getIntExtra("index",-1);
        if (currentIndex > -1) {
            selectGroup = groupList.get(currentIndex);
        }

        backButton = (ImageButton)this.findViewById(R.id.img_header_menu_left);
        doneButton = (Button)this.findViewById(R.id.img_header_menu_right);

        recycler = (RecyclerView)this.findViewById(R.id.recycler);
        this.adapter = new LightSectionAdapter(this, true);
        recycler.setAdapter(this.adapter);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        SectionedSpanSizeLookup lookup = new SectionedSpanSizeLookup(this.adapter, layoutManager);
        layoutManager.setSpanSizeLookup(lookup);
        recycler.setLayoutManager(layoutManager);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SelectGroupActivity.this.finish();
            }
        });

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SelectGroupActivity.this.finish();
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)

    public void onEventMainThread(MessageEvent evnt) {
        String msg = evnt.getMsg();

        if (msg.equals(Constants.Event_ClickItem_Group)) {
            Light e = evnt.light;

            ToolKit.setDeviceToGroup(selectGroup, e);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
