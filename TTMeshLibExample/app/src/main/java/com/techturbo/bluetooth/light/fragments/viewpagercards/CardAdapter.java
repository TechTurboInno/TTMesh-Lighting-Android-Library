package com.techturbo.bluetooth.light.fragments.viewpagercards;

/**
 * Created by zhubin on 2018/6/20.
 */

import android.support.v7.widget.CardView;

public interface CardAdapter {
    int MAX_ELEVATION_FACTOR = 8;

    float getBaseElevation();

    CardView getCardViewAt(int position);

    int getCount();
}
