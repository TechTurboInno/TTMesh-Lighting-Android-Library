package com.techturbo.bluetooth.light.util;

import android.app.Fragment;


import com.techturbo.bluetooth.light.fragments.SystemFragment;
import com.techturbo.bluetooth.light.fragments.ZoneFragment;

import com.techturbo.ttmeslight.R;

public abstract class FragmentFactory {

	public static Fragment createFragment(int id) {

		Fragment fragment = null;

		if (id == R.id.tab_system) {
			fragment = new SystemFragment();
		} else  {
			fragment = new ZoneFragment();
		}

		return fragment;
	}
}
