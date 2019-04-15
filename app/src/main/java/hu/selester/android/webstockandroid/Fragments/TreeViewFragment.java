package hu.selester.android.webstockandroid.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import hu.selester.android.webstockandroid.Adapters.TreeView.TreeViewPalettAdapter;
import hu.selester.android.webstockandroid.Helper.HelperClass;
import hu.selester.android.webstockandroid.Helper.KeyboardUtils;
import hu.selester.android.webstockandroid.Objects.AllLinesData;
import hu.selester.android.webstockandroid.Objects.SessionClass;
import hu.selester.android.webstockandroid.Objects.TreeViewGroup;
import hu.selester.android.webstockandroid.R;

public class TreeViewFragment extends Fragment implements MovesSubViewPager.FragmentLifecycle {

    private List<TreeViewGroup> dataList;
    private TreeViewPalettAdapter tvPalett;
    private EditText searchET;
    private RecyclerView rv;
    private View rootView;

    @Override
    public void onResumeFragment() {
        refreshData();
    }

    public static TreeViewFragment getInstance(){
        return new TreeViewFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.frg_movessub_treeview, container, false);
        ImageView searchIcon = rootView.findViewById(R.id.movessub_treeview_searchicon);

        ImageView delBtn = rootView.findViewById(R.id.movessub_treeview_delBtn);
        delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchET.setText("");
            }
        });
        searchET = rootView.findViewById(R.id.movessub_treeview_search);
        searchET.addTextChangedListener(searchTextWatcher);
        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchClick();
            }
        });
        dataList = new ArrayList<>();
        tvPalett = new TreeViewPalettAdapter(dataList, getContext());
        rv = rootView.findViewById(R.id.movessub_treeview_rv);
        LayoutManager lm = new LinearLayoutManager(getContext());
        rv.setLayoutManager(lm);
        rv.setAdapter(tvPalett);
        refreshData();
        return rootView;
    }

    public void refreshData(){
        try {
            dataList.clear();
            int tranCode = Integer.parseInt(SessionClass.getParam("tranCode"));
            int qBarcode02 = HelperClass.getArrayPosition("Barcode02", SessionClass.getParam(tranCode + "_Line_ListView_SELECT"));
            List<String> data = AllLinesData.getParamsMainPosition(qBarcode02);
            for (int i = 0; i < data.size(); i++) {
                dataList.add(new TreeViewGroup(data.get(i), 10));
            }
            ((TreeViewPalettAdapter) rv.getAdapter()).update(dataList);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    TextWatcher searchTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.length() > 3) {
                if (s.toString().substring(s.length() - SessionClass.getParam("barcodeSuffix").length(), s.length()).equals(SessionClass.getParam("barcodeSuffix"))) {
                    KeyboardUtils.hideKeyboard(getActivity());
                    searchET.removeTextChangedListener(this);
                    String bar = s.toString().substring(0, s.length() - SessionClass.getParam("barcodeSuffix").length());
                    searchET.setText(bar);
                    searchET.addTextChangedListener(this);
                    searchClick();
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    public void searchClick(){
        SessionClass.setParam("TreeViewSearchText",searchET.getText().toString());
        ((TreeViewPalettAdapter)rv.getAdapter()).update(dataList);
        KeyboardUtils.hideKeyboard(getActivity());
    }
}
