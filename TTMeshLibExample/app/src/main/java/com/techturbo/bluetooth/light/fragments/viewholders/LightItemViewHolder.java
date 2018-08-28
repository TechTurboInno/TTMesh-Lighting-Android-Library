package com.techturbo.bluetooth.light.fragments.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.techturbo.bluetooth.light.model.Constants;
import com.techturbo.bluetooth.light.model.Light;
import com.techturbo.okhttputils.okhttp.model.MessageEvent;
import com.techturbo.ttmeslight.R;
import com.telink.bluetooth.light.ConnectionStatus;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by zhubin on 2018/6/5.
 */

public class LightItemViewHolder extends RecyclerView.ViewHolder {
    TextView textView;
    ImageView imageView;
    ImageView offlineIcon;
    ImageButton button;
    View containerView;
    Light mlight;

    public LightItemViewHolder(View itemView) {
        super(itemView);
        containerView = itemView.findViewById(R.id.container);
        textView = itemView.findViewById(R.id.title);
        imageView = itemView.findViewById(R.id.imageView);
        offlineIcon = itemView.findViewById(R.id.offlineIcon);
        button = itemView.findViewById(R.id.deleteButton);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MessageEvent event = new MessageEvent(Constants.Event_Delete_Device);
                event.light = mlight;
                EventBus.getDefault().post(event);
            }
        });

        containerView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                MessageEvent event = new MessageEvent(Constants.Event_Long_Click);
                event.light = mlight;
                EventBus.getDefault().post(event);

                return false;
            }
        });
    }

    public void render(Light light, boolean isGroup){
        mlight = light;
        String des = light.getShowName();
        textView.setText(des);
        imageView.setImageResource(light.icon);

        if (light.status == ConnectionStatus.OFFLINE)
        {
            offlineIcon.setVisibility(View.VISIBLE);
        }
        else {
            offlineIcon.setVisibility(View.INVISIBLE);
        }

        button.setVisibility(View.INVISIBLE);
    }
}
