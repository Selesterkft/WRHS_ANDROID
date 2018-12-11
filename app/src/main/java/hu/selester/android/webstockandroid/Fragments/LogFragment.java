package hu.selester.android.webstockandroid.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import hu.selester.android.webstockandroid.Adapters.LogListAdapter;
import hu.selester.android.webstockandroid.Database.SelesterDatabase;
import hu.selester.android.webstockandroid.Divider.SimpleDividerItemDecoration;
import hu.selester.android.webstockandroid.R;

public class LogFragment extends Fragment {

    private RecyclerView logList;
    private LinearLayoutManager mLayountManager;
    private SelesterDatabase db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frg_log,container,false);
        db = SelesterDatabase.getDatabase(getContext());
        logList = rootView.findViewById(R.id.log_listview);
        ImageButton exitBtn = rootView.findViewById(R.id.log_exit);

        Button allBtn = rootView.findViewById(R.id.log_query_all);
        Button mBtn = rootView.findViewById(R.id.log_query_m);
        Button wBtn = rootView.findViewById(R.id.log_query_w);
        Button eBtn = rootView.findViewById(R.id.log_query_e);
        allBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((LogListAdapter)logList.getAdapter()).updateData(db.logDao().getAllLog());
            }
        });
        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((LogListAdapter)logList.getAdapter()).updateData(db.logDao().getMessgaeLog());
            }
        });
        wBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((LogListAdapter)logList.getAdapter()).updateData(db.logDao().getWarningLog());
            }
        });
        eBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((LogListAdapter)logList.getAdapter()).updateData(db.logDao().getErrorLog());
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
}
