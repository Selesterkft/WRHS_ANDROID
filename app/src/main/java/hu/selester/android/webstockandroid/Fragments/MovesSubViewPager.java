package hu.selester.android.webstockandroid.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;

import hu.selester.android.webstockandroid.Adapters.TreeView.TreeViewVPAdapter;
import hu.selester.android.webstockandroid.Objects.SessionClass;
import hu.selester.android.webstockandroid.R;

public class MovesSubViewPager extends Fragment {

    public interface FragmentLifecycle {
        public void onResumeFragment();
    }

    private TreeViewVPAdapter vpAdapter;
    private ViewPager vp;
    private TabLayout tl;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frg_movessub_vp, container, false);
        vp = rootView.findViewById(R.id.movessub_vp_viewpager);
        tl = rootView.findViewById(R.id.movessub_vp_tab);
        tl.addTab(tl.newTab().setIcon(R.drawable.table));
        tl.addTab(tl.newTab().setIcon(R.drawable.tree));
        vpAdapter = new TreeViewVPAdapter(getChildFragmentManager(), getContext(), getArguments());
        vp.setAdapter(vpAdapter);
        vp.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tl));

        tl.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                vp.setCurrentItem(tl.getSelectedTabPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {


            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                FragmentLifecycle fragmentToShow = (FragmentLifecycle)vpAdapter.getItem(position);
                fragmentToShow.onResumeFragment();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        return rootView;
    }

    public void closeFragment(){
        getFragmentManager().popBackStack();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
