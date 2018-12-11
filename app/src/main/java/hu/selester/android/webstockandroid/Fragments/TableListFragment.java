package hu.selester.android.webstockandroid.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import hu.selester.android.webstockandroid.Objects.AllLinesData;
import hu.selester.android.webstockandroid.Objects.ListSettings;
import hu.selester.android.webstockandroid.R;
import hu.selester.android.webstockandroid.TablePanel.TablePanel;

public class TableListFragment extends Fragment {

    private String[] columnName, headerText;
    private int[] headerWidth;
    private String title;
    private int page;
    private View rootView;
    private ListSettings ls;
    private TablePanel tablePanel;

    public static TableListFragment newInstance(int page, String title){
        TableListFragment fragment = new TableListFragment();
        Bundle b = new Bundle();
        b.putString("title", title);
        b.putInt("page", page);
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDefaultData();
        title = getArguments().getString("title");
        page = getArguments().getInt("page",0);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.vp_table,container, false);
        createNewTable();
        return rootView;
    }

    public void createNewTable(){
        tablePanel = new TablePanel(getContext(), rootView, R.id.vp_table_mainlayout, headerText, createDataList(),headerWidth);
        tablePanel.createTablePanel();
    }

    public void refreshDataList() {
        if(tablePanel.getAdapter() != null) {
            tablePanel.getAdapter().updateData(createDataList());
        }
    }

    private void setDefaultData(){
        columnName = new String[6];
        columnName[0] = "ItemNo";
        columnName[1] = "StockValue";
        columnName[2] = "ReservedStockValue";
        columnName[3] = "ek";
        columnName[4] = "t";
        columnName[5] = "ItemDescription";

        headerWidth = new int[6];
        headerWidth[0] = tablePanel.WRAP_MAX_COLUMN;
        headerWidth[1] = tablePanel.WRAP_MAX_COLUMN;
        headerWidth[2] = tablePanel.WRAP_MAX_COLUMN;
        headerWidth[3] = tablePanel.WRAP_MAX_COLUMN;
        headerWidth[4] = tablePanel.WRAP_MAX_COLUMN;
        headerWidth[5] = tablePanel.WRAP_MAX_COLUMN;
        headerText = new String[6];
        headerText[0] = "Cikk";
        headerText[1] = "K";
        headerText[2] = "FK";
        headerText[3] = "EK";
        headerText[4] = "T";
        headerText[5] = "Megnevezés";
    }

    private List<String[]> createDataList(){
        List<String[]> dtList = new ArrayList<>();
        for(int i = 0; i < AllLinesData.getAllDataList().size(); i++){
            String[] elements = new String[6];
            elements[0] = AllLinesData.getAllDataList().get(i)[0];
            elements[1] = AllLinesData.getAllDataList().get(i)[1];
            elements[2] = AllLinesData.getAllDataList().get(i)[2];
            elements[3] = AllLinesData.getAllDataList().get(i)[3];
            elements[4] = AllLinesData.getAllDataList().get(i)[4];
            elements[5] = AllLinesData.getAllDataList().get(i)[5];
            dtList.add(elements);
        }
        List<String[]> tempDataList = new ArrayList<>();
        if(page == 0) {
            for (int i = 0; i < dtList.size() ; i++) {
                if( !dtList.get(i)[3].isEmpty() ) {
                    try {
                        int ek = Integer.parseInt(dtList.get(i)[3]);
                        int t = Integer.parseInt(dtList.get(i)[4]);
                        if (ek > t) {
                            tempDataList.add(dtList.get(i));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }else if(page == 1) {
            for (int i = 0; i < dtList.size() ; i++) {
                if( !dtList.get(i)[3].isEmpty() ) {
                    try {
                        int ek = Integer.parseInt(dtList.get(i)[3]);
                        int t = Integer.parseInt(dtList.get(i)[4]);
                        if (ek == t || ek < t) {
                            if( ek < t ){
                                dtList.get(i)[4] = dtList.get(i)[3];
                            }
                            tempDataList.add(dtList.get(i));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }else if(page == 2) {
            for (int i = 0; i < dtList.size() ; i++) {
                if( dtList.get(i)[3].isEmpty() ){
                    tempDataList.add(dtList.get(i));
                }else {
                    try {
                        int ek = Integer.parseInt(dtList.get(i)[3]);
                        int t = Integer.parseInt(dtList.get(i)[4]);
                        if (ek < t) {
                            dtList.get(i)[4] = String.valueOf(t - ek);

                            tempDataList.add(dtList.get(i));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return tempDataList;
    }
}
