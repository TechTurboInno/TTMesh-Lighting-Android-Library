package com.techturbo.bluetooth.light.fragments;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.techturbo.bluetooth.light.activity.SelectGroupActivity;
import com.techturbo.bluetooth.light.activity.ZoneDetailActivity;
import com.techturbo.bluetooth.light.model.Light;
import com.techturbo.bluetooth.light.model.Lights;
import com.techturbo.okhttputils.okhttp.model.GroupInfo;
import com.techturbo.okhttputils.okhttp.model.SystemConfig;
import com.techturbo.ttmeslight.R;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ZoneFragment extends Fragment {

    private LayoutInflater minflater;
    private ZoneListAdapter adapter;
    private ListView listView;
    private Activity mContext;
    private boolean bIsEditing = false;
    private List<GroupInfo> groupInfoLists;

    public ZoneFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        minflater = inflater;
        this.mContext = this.getActivity();
        View view = inflater.inflate(R.layout.fragment_zone,
                null);

        groupInfoLists = SystemConfig.shareConfig().readGroupInfos();

        this.adapter = new ZoneListAdapter();
        this.listView = (ListView)view.findViewById(R.id.zoneListView);
        this.listView.setAdapter(this.adapter);

        this.adapter.notifyDataSetChanged();

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           final int position, long id) {

                Intent intent = new Intent(mContext,
                        SelectGroupActivity.class);
                intent.putExtra("index",position);
                startActivity(intent);

                return true;
            }
        });


        this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //我们需要的内容，跳转页面或显示详细信息
                Intent intent = new Intent(mContext,
                        ZoneDetailActivity.class);
                intent.putExtra("index",position);
                startActivity(intent);
            }
        });

        final ImageView addButton = (ImageView)view.findViewById(R.id.img_header_menu_right);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,
                        ZoneDetailActivity.class);
                intent.putExtra("index",-1);
                startActivity(intent);
            }
        });

        return view;

    }

    public void removeItem(GroupInfo group) {
        this.adapter.removeItem(group);
    }

    public GroupInfo getItem(int position) {
        return this.adapter.getItem(position);
    }

    public void refreshData() {
        groupInfoLists = SystemConfig.shareConfig().readGroupInfos();
        this.adapter.notifyDataSetChanged();
    }

    private static class ZoneItemHolder {
        public TextView txtName;
    }

    final class ZoneListAdapter extends BaseAdapter {

        public boolean bEditing = false;

        public ZoneListAdapter() {

        }

        @Override
        public int getCount() {
            return groupInfoLists.size();
        }

        @Override
        public GroupInfo getItem(int position) {
            return groupInfoLists.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            ZoneFragment.ZoneItemHolder holder;

            if (convertView == null) {

                convertView = minflater.inflate(R.layout.zone_item, null);

                TextView txtName = (TextView) convertView
                        .findViewById(R.id.textView3);

                holder = new ZoneFragment.ZoneItemHolder();

                holder.txtName = txtName;

                convertView.setTag(holder);

            } else {
                holder = (ZoneFragment.ZoneItemHolder) convertView.getTag();
            }

            GroupInfo group = this.getItem(position);

            holder.txtName.setText(group.getGroupName());

            return convertView;
        }

        public void removeItem(GroupInfo group) {
            groupInfoLists.remove(group);
        }

        public Light get(int meshAddress) {
            return Lights.getInstance().getByMeshAddress(meshAddress);
        }
    }
}
