package hu.selester.android.webstockandroid.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import hu.selester.android.webstockandroid.Adapters.XD_PlacenumberPagerAdapter;
import hu.selester.android.webstockandroid.Adapters.XD_ItemsParametersListAdapter;
import hu.selester.android.webstockandroid.Helper.HelperClass;
import hu.selester.android.webstockandroid.Helper.KeyboardUtils;
import hu.selester.android.webstockandroid.Helper.SlidingTabLayout;
import hu.selester.android.webstockandroid.Objects.XD_ItemsParameters;
import hu.selester.android.webstockandroid.R;

public class XD_ItemParametersFragment extends Fragment {

    private View rootView;
    private TextView txt;
    private RecyclerView itemsListContainer;
    private ViewPager vp;
    private SlidingTabLayout spl;
    private TextView placeNumberTV;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.frg_itemparameters, container, false);
        List<XD_PlacenumberFragment> frgList = new ArrayList<>();
        List<String> pageTitleList = new ArrayList<>();

        vp = rootView.findViewById(R.id.ps_pager);
        XD_PlacenumberPagerAdapter ppa = new XD_PlacenumberPagerAdapter(getFragmentManager(), frgList, pageTitleList);
        vp.setAdapter( ppa );
        vp.setOffscreenPageLimit(0);

        spl = rootView.findViewById(R.id.ps_sliding_panel);
        spl.setViewPager(vp);

        placeNumberTV = rootView.findViewById(R.id.ps_placenumber);
        ImageView addPlaceNumberBtn = rootView.findViewById(R.id.ps_addplacenum_btn);
        addPlaceNumberBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if( !placeNumberTV.getText().toString().equals("") ){
                    ((XD_PlacenumberPagerAdapter)vp.getAdapter()).update(XD_PlacenumberFragment.newInstance(placeNumberTV.getText().toString()),placeNumberTV.getText().toString());
                    spl.setViewPager(vp);
                    placeNumberTV.setText("");
                }else{
                    Toast.makeText(getContext(),"Nincs rakhely meghatározva!",Toast.LENGTH_LONG).show();
                }
                KeyboardUtils.hideKeyboard(getActivity());
            }
        });

        RecyclerView.LayoutManager lm = new LinearLayoutManager(getContext());
        List<XD_ItemsParameters> itemsList = new ArrayList<>();
        itemsList.add(new XD_ItemsParameters(1,1,1,1,1));
        XD_ItemsParametersListAdapter XD_listAdapter = new XD_ItemsParametersListAdapter(getContext(),itemsList);
        itemsListContainer = rootView.findViewById(R.id.ps_rv_items_list);
        itemsListContainer.setLayoutManager(lm);
        itemsListContainer.setAdapter(XD_listAdapter);

        return rootView;
    }

}