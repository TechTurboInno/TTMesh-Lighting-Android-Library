package com.techturbo.bluetooth.light.fragments;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.techturbo.bluetooth.light.activity.MeshInfoActivity;
import com.techturbo.bluetooth.light.fragments.viewholders.LightSectionAdapter;
import com.techturbo.bluetooth.light.model.Constants;
import com.techturbo.bluetooth.light.model.Light;
import com.techturbo.okhttputils.okhttp.model.MeshInfo;
import com.techturbo.okhttputils.okhttp.model.MessageEvent;
import com.techturbo.ttmeslight.R;
import com.truizlop.sectionedrecyclerview.SectionedSpanSizeLookup;

import org.greenrobot.eventbus.EventBus;

/**
 * A simple {@link Fragment} subclass.
 */
public class SystemFragment extends Fragment {

    RecyclerView recycler;
    TextView allOnTV;
    TextView allOffTV;
    TextView allColorTV;
    TextView titleTV;
    ImageView dropButton;

    private LightSectionAdapter adapter;
    private Activity mContext;

    public SystemFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.mContext = this.getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_system,
                null);

        titleTV = (TextView)view.findViewById(R.id.txt_header_title);
        dropButton = (ImageView) view.findViewById(R.id.drop);
        allOnTV = (TextView)view.findViewById(R.id.textView2);
        allOffTV = (TextView)view.findViewById(R.id.textView5);
        allColorTV = (TextView)view.findViewById(R.id.textView4);

        recycler = (RecyclerView)view.findViewById(R.id.recycler);
        this.adapter = new LightSectionAdapter(this.getActivity(), false);
        recycler.setAdapter(this.adapter);

        GridLayoutManager layoutManager = new GridLayoutManager(this.getActivity(), 3);
        SectionedSpanSizeLookup lookup = new SectionedSpanSizeLookup(this.adapter, layoutManager);
        layoutManager.setSpanSizeLookup(lookup);
        recycler.setLayoutManager(layoutManager);

        final ImageView addButton = (ImageView)view.findViewById(R.id.img_header_menu_right);

        titleTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new MessageEvent(Constants.Event_Show_Picker));
            }
        });

        dropButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new MessageEvent(Constants.Event_Show_Picker));
            }
        });

        allOnTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new MessageEvent(Constants.Event_All_On));
            }
        });

        allOffTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new MessageEvent(Constants.Event_All_Off));
            }
        });

        allColorTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new MessageEvent(Constants.Event_All_Color));
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new MessageEvent(Constants.Event_Add_Device));
            }
        });

        final Button editButton = (Button)view.findViewById(R.id.img_header_menu_left);


        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, MeshInfoActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    public void changeTitle(MeshInfo info) {
        titleTV.setText(info.getMeshName());
    }

    public void notifyDataSetChanged() {
        if (this.adapter != null)
            this.adapter.notifyDataSetChanged();
    }

    public Light getDevice(int meshAddress) {
        return this.adapter.get(meshAddress);
    }

    public void addDevice(Light light) {
        this.adapter.add(light);
    }
}
