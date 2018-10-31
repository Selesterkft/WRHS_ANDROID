package hu.selester.android.webstockandroid.Adapters;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import hu.selester.android.webstockandroid.Fragments.TableListFragment;

public class TableViewPagerAdapter extends FragmentPagerAdapter {

    private static int NUM_ITEMS = 3;

    public static List<TableListFragment> frgList = new ArrayList<>();

    public TableViewPagerAdapter(FragmentManager fm) {
        super(fm);
        frgList.add(TableListFragment.newInstance(0,"TITLE1"));
        frgList.add(TableListFragment.newInstance(1,"TITLE2"));
        frgList.add(TableListFragment.newInstance(2,"TITLE3"));
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return "Page" + position;
    }

    @Override
    public Fragment getItem(int position) {
        return frgList.get(position);
    }

    @Override
    public int getCount() {
        return NUM_ITEMS;
    }
}
