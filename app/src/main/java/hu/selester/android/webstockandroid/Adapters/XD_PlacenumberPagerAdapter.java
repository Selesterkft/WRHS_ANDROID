package hu.selester.android.webstockandroid.Adapters;

import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import java.util.List;

import hu.selester.android.webstockandroid.Fragments.XD_PlacenumberFragment;

public class XD_PlacenumberPagerAdapter extends FragmentStatePagerAdapter {

    private List<XD_PlacenumberFragment> frgList;
    private List<String> pageTitleList;

    public XD_PlacenumberPagerAdapter(FragmentManager fm, List<XD_PlacenumberFragment> frgList, List<String> pageTitleList) {
        super(fm);
        this.frgList = frgList;
        this.pageTitleList = pageTitleList;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return pageTitleList.get(position);
    }

    @Override
    public XD_PlacenumberFragment getItem(int position) {
        return frgList.get(position);
    }

    @Override
    public int getCount() {
        return frgList.size();
    }

    public void update(XD_PlacenumberFragment item, String text){
        frgList.add(item);
        pageTitleList.add(text);
        notifyDataSetChanged();
    }
}
