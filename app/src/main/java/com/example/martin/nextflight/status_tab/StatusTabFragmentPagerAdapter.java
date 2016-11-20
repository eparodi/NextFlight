package com.example.martin.nextflight.status_tab;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.martin.nextflight.elements.Status;

public class StatusTabFragmentPagerAdapter extends FragmentPagerAdapter {
    final private int PAGE_COUNT = 3;
    private String tabTitles[] = new String[] { "Detalles", "Opiniones", "Mapa" };
    private Context context;
    private String json_status;

    final private int DETAILS = 0;
    private Fragment details_fragment = null;
    final private int OPINIONS = 1;
    private Fragment opinions_fragment = null;
    final private int MAP = 2;
    private Fragment map_fragment = null;

    public StatusTabFragmentPagerAdapter(FragmentManager fm, Context context,
                                         String json_status) {
        super(fm);
        this.context = context;
        this.json_status = json_status;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == DETAILS){
            if (details_fragment == null){
                details_fragment = StatusFragment.newInstance(json_status);
            }
            return details_fragment;
        }else if (position == OPINIONS){
            if (opinions_fragment == null){
                opinions_fragment = StatusFragment.newInstance(json_status);
            }
            return opinions_fragment;
        }else{
            if (map_fragment == null){
                map_fragment = MapFlightFragment.newInstance(json_status);
            }
            return map_fragment;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }
}
