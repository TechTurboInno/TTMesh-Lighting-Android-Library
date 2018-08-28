package com.techturbo.bluetooth.light.fragments.viewholders;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.techturbo.bluetooth.light.model.Constants;
import com.techturbo.bluetooth.light.model.Light;
import com.techturbo.bluetooth.light.model.Lights;
import com.techturbo.okhttputils.okhttp.model.MessageEvent;
import com.techturbo.ttmeslight.R;

import com.truizlop.sectionedrecyclerview.SectionedRecyclerViewAdapter;

import org.greenrobot.eventbus.EventBus;

public class LightSectionAdapter extends SectionedRecyclerViewAdapter<LightHeaderViewHolder,
        LightItemViewHolder,
        LightFooterViewHolder> {

    protected Context context = null;
    protected boolean isGroup = false;

    public LightSectionAdapter(Context context, boolean isGroup) {
        this.context = context;
        this.isGroup = isGroup;
    }

    @Override
    protected int getItemCountForSection(int section) {

        return Lights.getInstance().size();
    }

    @Override
    protected int getSectionCount() {
        return 1;
    }

    @Override
    protected boolean hasFooterInSection(int section) {
        return false;
    }

    protected LayoutInflater getLayoutInflater(){
        return LayoutInflater.from(context);
    }

    @Override
    protected LightHeaderViewHolder onCreateSectionHeaderViewHolder(ViewGroup parent, int viewType) {
        View view = getLayoutInflater().inflate(R.layout.view_light_header, parent, false);
        return new LightHeaderViewHolder(view);
    }

    @Override
    protected LightFooterViewHolder onCreateSectionFooterViewHolder(ViewGroup parent, int viewType) {
        View view = getLayoutInflater().inflate(R.layout.view_light_footer, parent, false);
        return new LightFooterViewHolder(view);
    }

    @Override
    protected LightItemViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        View view = getLayoutInflater().inflate(R.layout.view_light_item, parent, false);
        return new LightItemViewHolder(view);
    }

    @Override
    protected void onBindSectionHeaderViewHolder(LightHeaderViewHolder holder, int section) {
//        SortLight sortLight = this.sortDeviceList.get(section);
//        holder.render(sortLight.title);
    }

    @Override
    protected void onBindSectionFooterViewHolder(LightFooterViewHolder holder, int section) {
//        holder.render("Footer " + (section + 1));
    }

    protected int[] colors = new int[]{0xfff44336, 0xff2196f3, 0xff009688, 0xff8bc34a, 0xffff9800};
    @Override
    protected void onBindItemViewHolder(LightItemViewHolder holder, int section, final int position) {
        final Light light = Lights.getInstance().get(position);
        light.updateIcon();
        holder.render(light, this.isGroup);

        holder.containerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MessageEvent event;

                if (isGroup) {
                    event = new MessageEvent(Constants.Event_ClickItem_Group);
                }
                else {
                    event = new MessageEvent(Constants.Event_ClickItem_Device);
                }

                event.light = light;
                EventBus.getDefault().post(event);
            }
        });
    }

    public void add(Light light) {
        Lights.getInstance().add(light);
    }

    public Light get(int meshAddress) {
        return Lights.getInstance().getByMeshAddress(meshAddress);
    }
}