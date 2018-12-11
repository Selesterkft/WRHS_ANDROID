package hu.selester.android.webstockandroid.Adapters.TreeView;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import hu.selester.android.webstockandroid.Fragments.MovesSubTableFragment;
import hu.selester.android.webstockandroid.Fragments.TreeViewFragment;

public class TreeViewVPAdapter extends FragmentPagerAdapter {

    private Context context;
    private Bundle args;
    private List<Fragment> frgs;

    public TreeViewVPAdapter(FragmentManager fm, Context context, Bundle args) {
        super(fm);
        this.context = context;
        this.args = args;
        frgs = new ArrayList<>();
    }

    @Override
    public Fragment getItem(int position) {
        frgs.add(MovesSubTableFragment.getInstance(args));
        frgs.add(TreeViewFragment.getInstance());
        return frgs.get(position);
    }

    @Override
    public int getCount() {
        return 2;
    }
}
