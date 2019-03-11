package hu.selester.android.webstockandroid.Fragments;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import hu.selester.android.webstockandroid.Adapters.LogListAdapter;
import hu.selester.android.webstockandroid.Database.SelesterDatabase;
import hu.selester.android.webstockandroid.Divider.SimpleDividerItemDecoration;
import hu.selester.android.webstockandroid.Helper.KeyboardUtils;
import hu.selester.android.webstockandroid.Objects.SessionClass;
import hu.selester.android.webstockandroid.R;
import hu.selester.android.webstockandroid.Threads.ChangeStatusThread;

public class LogFragment extends Fragment{

    private RecyclerView logList;
    private LinearLayoutManager mLayountManager;
    private SelesterDatabase db;
    private String queryChar = "";
    private Button allBtn,mBtn,wBtn,eBtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frg_log,container,false);
        db = SelesterDatabase.getDatabase(getContext());
        logList = rootView.findViewById(R.id.log_listview);
        ImageButton exitBtn = rootView.findViewById(R.id.log_exit);
        allBtn = rootView.findViewById(R.id.log_query_all);
        mBtn = rootView.findViewById(R.id.log_query_m);
        wBtn = rootView.findViewById(R.id.log_query_w);
        eBtn = rootView.findViewById(R.id.log_query_e);
        allBtn.setBackgroundResource(android.R.drawable.btn_default);
        mBtn.setBackgroundResource(android.R.drawable.btn_default);
        wBtn.setBackgroundResource(android.R.drawable.btn_default);
        eBtn.setBackgroundResource(android.R.drawable.btn_default);
        btnColor();
        Button delBtn = rootView.findViewById(R.id.log_query_delall);
        allBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                queryChar = "";
                updateData();
            }
        });
        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                queryChar = "M";
                updateData();
            }
        });
        wBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                queryChar = "W";
                updateData();
            }
        });
        eBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                queryChar = "E";
                updateData();
            }
        });
        delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.logDao().deleteAll();
                updateData();
            }
        });
        mLayountManager = new LinearLayoutManager(getContext());
        LogListAdapter lla = new LogListAdapter(getContext(),db.logDao().getAllLog());
        logList.setLayoutManager(mLayountManager);
        logList.addItemDecoration(new SimpleDividerItemDecoration(getContext()));
        logList.setAdapter(lla);
        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    void updateData(){
        switch (queryChar){
            case "": ((LogListAdapter)logList.getAdapter()).updateData(db.logDao().getAllLog());btnColor(); break;
            case "M":((LogListAdapter)logList.getAdapter()).updateData(db.logDao().getMessgaeLog());btnColor();break;
            case "W":((LogListAdapter)logList.getAdapter()).updateData(db.logDao().getWarningLog());btnColor();break;
            case "E":((LogListAdapter)logList.getAdapter()).updateData(db.logDao().getErrorLog());btnColor();break;
        }
    }

    void btnColor(){
        allBtn.setTextColor(Color.GRAY);
        mBtn.setTextColor(Color.GRAY);
        wBtn.setTextColor(Color.GRAY);
        eBtn.setTextColor(Color.GRAY);
        switch (queryChar) {
            case "":
                allBtn.setTextColor(Color.BLACK);
                break;
            case "M":
                mBtn.setTextColor(Color.BLACK);
                break;
            case "W":
                wBtn.setTextColor(Color.BLACK);
                break;
            case "E":
                eBtn.setTextColor(Color.BLACK);
                break;
        }
    }

}
