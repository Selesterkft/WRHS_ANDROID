package hu.selester.android.webstockandroid.Fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


import hu.selester.android.webstockandroid.Database.SelesterDatabase;
import hu.selester.android.webstockandroid.Helper.HelperClass;
import hu.selester.android.webstockandroid.Helper.MySingleton;
import hu.selester.android.webstockandroid.Objects.AllLinesData;
import hu.selester.android.webstockandroid.Objects.CheckedList;
import hu.selester.android.webstockandroid.Objects.InsertedList;
import hu.selester.android.webstockandroid.Objects.ListSettings;
import hu.selester.android.webstockandroid.Objects.NotCloseList;
import hu.selester.android.webstockandroid.Objects.SessionClass;
import hu.selester.android.webstockandroid.R;
import hu.selester.android.webstockandroid.TablePanel.TablePanel;
import hu.selester.android.webstockandroid.TableView.TableView;

public class
MovesTableFragment extends Fragment implements View.OnClickListener {

    private View rootView;
    private ListSettings ls;
    //private TableView table;
    private TablePanel tablePanel;
    private int tranCode;
    private String[] headerText;
    private ProgressDialog pd;
    private int qBreak, qCollection, qTakePhoto, qMAR;
    private String[] arrayBtnVisibility;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        CheckedList.clearAllData();
        SessionClass.setParam("TreeViewSearchText","");
        pd = HelperClass.loadingDialogOn(getActivity());
        rootView = inflater.inflate(R.layout.frg_moves, container, false);
        tranCode = getArguments().getInt("tranCode");
        SessionClass.setParam("tranCode",String.valueOf(tranCode));
        ImageButton selectBtn = rootView.findViewById(R.id.moves_selectBtn);
        selectBtn.setOnClickListener(this);
        ImageButton selectBtn1 = rootView.findViewById(R.id.moves_selectBtn1);
        selectBtn1.setOnClickListener(this);
        ImageButton backBtn = rootView.findViewById(R.id.moves_backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });
        AllLinesData.delParams();
        InsertedList.clearAll();
        CheckedList.clearAllData();
        if(SessionClass.getParam(tranCode + "_Detail_Button_IsVisible") != null) {

            arrayBtnVisibility  = SessionClass.getParam(tranCode + "_Detail_Button_IsVisible").split(",");
            qBreak              = HelperClass.getArrayPosition("break", SessionClass.getParam(tranCode + "_Detail_Button_Names"));
            qCollection         = HelperClass.getArrayPosition("collection", SessionClass.getParam(tranCode + "_Detail_Button_Names"));
            qTakePhoto          = HelperClass.getArrayPosition("photo", SessionClass.getParam(tranCode + "_Detail_Button_Names"));
            qMAR                = HelperClass.getArrayPosition("ManualAddRemove", SessionClass.getParam(tranCode + "_Detail_Button_Names"));
            if( qBreak > -1 )       { SessionClass.setParam("breakBtn", arrayBtnVisibility[qBreak]); } else { SessionClass.setParam("breakBtn", "0" ); }
            if( qCollection > -1 )  { SessionClass.setParam("collectionBtn", arrayBtnVisibility[qCollection]); } else { SessionClass.setParam("collectionBtn", "0" ); }
            if( qTakePhoto > -1 )   { SessionClass.setParam("takePhotoBtn", arrayBtnVisibility[qTakePhoto]); } else { SessionClass.setParam("takePhotoBtn", "0" ); }
            if( qMAR > -1 )         { SessionClass.setParam("marBtn", arrayBtnVisibility[qMAR]); } else { SessionClass.setParam("marBtn", "0" ); }
        }else{
            SessionClass.setParam("breakBtn", "0");
            SessionClass.setParam("collectionBtn", "0");
            SessionClass.setParam("takePhotoBtn", "0");
            SessionClass.setParam("marBtn", "0");
        }
//        Log.i("TAG","BREAK: "+SessionClass.getParam("breakBtn")+" - CollectionBtn: "+SessionClass.getParam("collectionBtn"));

        if(!SessionClass.getParam(tranCode+"_Head_ListView_SELECT").isEmpty()) SessionClass.setParam(tranCode+"_Head_ListView_SELECT",SessionClass.getParam(tranCode+"_Head_ListView_SELECT").replace(" ",""));

        // ---------------------------- Header --------------------------------

        String[] temp = SessionClass.getParam(tranCode+"_Head_ListView_Widths").split(",");
        final int[] headerWidth = new int[temp.length];
        for(int i=0;i<temp.length;i++){
            if(Integer.parseInt(temp[i])>0){
                headerWidth[i] = tablePanel.WRAP_MAX_COLUMN;
            }else{
                headerWidth[i] = 0;
            }
        }
        headerWidth[0] = 0;
        headerText = SessionClass.getParam(tranCode+"_Head_ListView_Names").split(",");
        final String[] columnName = SessionClass.getParam(tranCode+"_Head_ListView_SELECT").split(",");
        //final String[] columnName = {"Tran_Order", "Tran_ID", "Tran_Code", "Tran_No","Tran_Date", "Supplier_Short_Name","Rentner_Short_Name"};
        ls = new ListSettings(headerText, headerWidth, columnName,true);

        //table.setHeaderData(R.id.headerTextLayout, ls);

        // ---------------------------- Content -------------------------------


        String terminal = SessionClass.getParam("terminal");
        String pdaid = SessionClass.getParam("pdaid");
        String userid = SessionClass.getParam("userid");
        String listSelect = SessionClass.getParam(tranCode+"_Head_ListView_SELECT");
        String listFrom = SessionClass.getParam(tranCode+"_Head_ListView_FROM");
        String listOrder = SessionClass.getParam(tranCode+"_Head_ListView_ORDER_BY");
        String url = SessionClass.getParam("WSUrl")+"/get_task_head/"+terminal+"/"+userid+"/"+pdaid+"/"+listSelect+"/"+listFrom+"/"+listOrder+"/"+tranCode;
        url = url.replace(" ","");
        RequestQueue rq = MySingleton.getInstance(getContext()).getRequestQueue();
        Log.i("URL",url);
        JsonRequest<JSONObject> jr = new  JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    List<String[]> tl = new ArrayList<>();
                    String rootText=response.getString("getTaskHead_Result");
                    JSONArray jsonArray = new JSONArray(rootText);
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject jsonObj = jsonArray.getJSONObject(i);
                        String[] dataText = new String[10];
                        for(int j=0;j<columnName.length;j++){
                            dataText[j] = jsonObj.getString(columnName[j]);
                        }
                        tl.add(dataText);
                    }
                    tablePanel = new TablePanel(getContext(), rootView, R.id.moves_mainLayout, headerText, tl,headerWidth);
                    createTablePanel();

                } catch (JSONException e) {
                    e.printStackTrace();
                    pd.dismiss();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if( error != null ){
                    error.printStackTrace();
                }
                pd.dismiss();
            }
        });
        jr.setRetryPolicy(new DefaultRetryPolicy(50000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rq.add(jr);

        //table.setContentData(R.id.dataListView,tl,ls);

        return rootView;
    }

    private void createTablePanel(){
        TablePanel.TablePanelSetting tps = tablePanel.getTablePanelSettingInstance();
        tps.setFontSize(14f);
        tps.setCellLeftRightPadding(HelperClass.dpToPx(getContext(),6));
        tps.setCellTopBottomPadding(HelperClass.dpToPx(getContext(),6));
        tps.setCheckable(true);
        tps.setOnRowClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View v) {
                // Itt volt a HIBA
                CheckBox cb = v.findViewById(R.dimen.tablepanel_row_checkBox);
                if(v.getId() != R.dimen.tablepanel_row_checkBox) cb.setChecked(!cb.isChecked());
                for(int i = 0; i < tablePanel.getAdapter().getItemCount(); i++){
                    tablePanel.getAdapter().checkedList[i] = false;
                }
                tablePanel.getAdapter().checkedList[(int)cb.getTag()] = cb.isChecked();
                tablePanel.getAdapter().update();
                List<String> checkedList = tablePanel.getAdapter().getCheckedPosition(0);

                if(checkedList!=null && checkedList.size()>0){
                    //Toast.makeText(getContext(),"Kiválasztva: "+chcekedList.size()+" elem",Toast.LENGTH_LONG).show();
                }else{
                    //Toast.makeText(getContext(),"NINCS KIVÁLASZTVA SEMMI",Toast.LENGTH_LONG).show();
                }

            }
        });
        tps.setOnHeaderClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TablePanel.SortResultObject sro = tablePanel.sortDataGrid((int)v.getTag());
                tablePanel.setRowSetting(sro.rowSettingData);
                tablePanel.getAdapter().updateData( sro.data, sro.checkData, sro.rowSettingData );
                tablePanel.showSortSign();
            }
        });
        tablePanel.setTablePanelSetting(tps);
        tablePanel.createTablePanel();
        pd.dismiss();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        Fragment f;
        FragmentTransaction ft;
        Bundle b;
        switch (v.getId()){
            case R.id.moves_selectBtn:
                if(tablePanel.getAdapter().getCheckedPosition(1) != null && tablePanel.getAdapter().getCheckedPosition(1).size()>0){
                    InsertedList.clearAll();
                    CheckedList.clearAllData();
                    NotCloseList.clearAllData();
                    f = new ChkBarcodeFragment();
                    ft = getActivity().getSupportFragmentManager().beginTransaction();
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
                    b = new Bundle();
                    b.putString("tranid", tablePanel.getAdapter().getCheckedPosition(1).get(0));
                    b.putString("movenum", tablePanel.getAdapter().getCheckedPosition(3).get(0));
                    f.setArguments(b);
                    ft.replace(R.id.fragments,f);
                    ft.addToBackStack("app");
                    ft.commit();
                } else {
                    Toast.makeText(getContext(),"Nincs kiválasztva feladat!",Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.moves_selectBtn1:
                if(tablePanel.getAdapter().getCheckedPosition(1) != null && tablePanel.getAdapter().getCheckedPosition(1).size()>0){
                    InsertedList.clearAll();
                    CheckedList.clearAllData();
                    NotCloseList.clearAllData();
                    if( SessionClass.getParam("collectionBtn").equals("1") ){
                        f = new MovesSubViewPager();
                    }else{
                        f = new MovesSubTableFragment();
                    }

                    ft = getActivity().getSupportFragmentManager().beginTransaction();
                    b = new Bundle();
                    b.putString("tranid", tablePanel.getAdapter().getCheckedPosition(1).get(0));
                    b.putString("movenum", tablePanel.getAdapter().getCheckedPosition(3).get(0));
                    b.putString("tranCode", "" + getArguments().getInt("tranCode"));
                    b.putString("reload", "0");
                    f.setArguments(b);
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
                    ft.replace(R.id.fragments, f);
                    ft.addToBackStack("app");
                    ft.commit();
                } else {
                    Toast.makeText(getContext(),"Nincs kiválasztva feladat!",Toast.LENGTH_LONG).show();
                }
                break;
        }
    }


}