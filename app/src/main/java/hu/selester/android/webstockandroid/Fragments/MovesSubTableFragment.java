package hu.selester.android.webstockandroid.Fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import hu.selester.android.webstockandroid.Database.SelesterDatabase;
import hu.selester.android.webstockandroid.Database.Tables.LogTable;
import hu.selester.android.webstockandroid.Helper.HelperClass;
import hu.selester.android.webstockandroid.Helper.KeyboardUtils;
import hu.selester.android.webstockandroid.Helper.MySingleton;
import hu.selester.android.webstockandroid.Objects.ActiveFragment;
import hu.selester.android.webstockandroid.Objects.AllLinesData;
import hu.selester.android.webstockandroid.Objects.CheckedList;
import hu.selester.android.webstockandroid.Objects.ListSettings;
import hu.selester.android.webstockandroid.Objects.SessionClass;
import hu.selester.android.webstockandroid.R;
import hu.selester.android.webstockandroid.TablePanel.TablePanel;
import hu.selester.android.webstockandroid.Threads.ChangeStatusThread;
import hu.selester.android.webstockandroid.Threads.CloseLineThread;
import hu.selester.android.webstockandroid.Threads.SaveCheckedDataThread;
import hu.selester.android.webstockandroid.Threads.SaveDataThread;
import hu.selester.android.webstockandroid.Threads.SaveAllSessionTemp;
import hu.selester.android.webstockandroid.Threads.SaveDataThread_All;

public class MovesSubTableFragment extends Fragment implements View.OnClickListener, MovesSubViewPager.FragmentLifecycle {

    private View rootView;
    private ListSettings ls;
    private EditText findValue;
    private String isBar;
    private int findRow;
    private String[] columnName;
    private String tranCode;
    private String tranID;
    private String movenum;
    private int qNeed,qCurrent,qBarcode01;
    private ImageButton saveDataBtn, lockBtn, selectBtn, flushBtn;
    private Dialog popup = null;
    public ProgressBar uploadpbar;
    private LinearLayout progressLayout;
    private TextView progressPercent;
    private LinearLayout functLayout;
    private TablePanel tablePanel;
    private int[] headerWidth;
    private String[] headerText;
    private String reload;
    private SelesterDatabase db;
    private ProgressDialog pd;
    private boolean closeMovesDialogRun;

    public static MovesSubTableFragment getInstance(Bundle args){
        MovesSubTableFragment f = new MovesSubTableFragment();
        f.setArguments(args);
        return f;
    }

    @Override
    public void onResumeFragment() {
        Log.i("TAG","TABLE RESUME");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ActiveFragment.setFragment(this);
        closeMovesDialogRun = false;
        db = SelesterDatabase.getDatabase(getContext());
        tranCode = getArguments().getString("tranCode");
        SessionClass.setParam("tranCode", ""+tranCode);
        tranID = getArguments().getString("tranid");
        movenum = getArguments().getString("movenum");
        reload = getArguments().getString("reload");
        qBarcode01 = HelperClass.getArrayPosition("Barcode01", SessionClass.getParam(tranCode + "_Line_ListView_SELECT"));
        if(SessionClass.getParam(tranCode+"_Detail_TextBox_Needed_Qty_Index").equals("")){
            qNeed = 0;
        }else{
            qNeed = Integer.parseInt(SessionClass.getParam(tranCode+"_Detail_TextBox_Needed_Qty_Index"));
        }

        if(SessionClass.getParam(tranCode+"_Detail_TextBox_Current_Qty_Index").equals("")){
            qCurrent = 0;
        }else{
            qCurrent = Integer.parseInt(SessionClass.getParam(tranCode+"_Detail_TextBox_Current_Qty_Index"));
        }
        findRow = Integer.parseInt(SessionClass.getParam(tranCode+"_Line_TextBox_Find_Index"));
        rootView = inflater.inflate(R.layout.frg_movessub, container, false);
        functLayout = rootView.findViewById(R.id.movessub_tableRoot);
        uploadpbar = rootView.findViewById(R.id.movessub_progressBar);
        progressLayout = rootView.findViewById(R.id.movessub_progressLayout);
        progressPercent = rootView.findViewById(R.id.movessub_progresspercent);
        progressLayout.setVisibility(View.GONE);
        SessionClass.setParam("currentPlace","");
        selectBtn = rootView.findViewById(R.id.movessub_selectBtn);
        Button delTextBtn = rootView.findViewById(R.id.movessub_header_delbtn);
        Button findValueBtn = rootView.findViewById(R.id.movessub_header_btn);
        lockBtn = rootView.findViewById(R.id.movessub_lockBtn);
        flushBtn = rootView.findViewById(R.id.movessub_flushBtn);
        LinearLayout palettBtn = rootView.findViewById(R.id.movessub_palettBtn);
        if( SessionClass.getParam(tranCode + "_Detail_Button_IsVisible").equals("1") ){
            palettBtn.setVisibility(View.VISIBLE);
            palettBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PalettCollectFragment f = new PalettCollectFragment();
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragments, f).addToBackStack("app").commit();
                }
            });
        }else{
            palettBtn.setVisibility(View.GONE);
        }

        flushBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.isClickable()) {
                    closeWithoutSave();
                }
            }
        });
        lockBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(HelperClass.isOnline(getContext())) {
                    if (v.isClickable()) {
                        closeLine();
                    }
                }else{
                    Toast.makeText(getContext(),"Hálózati hiba!",Toast.LENGTH_LONG).show();
                }
            }
        });
        findValueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkListElementsManual();
            }
        });
        delTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(HelperClass.isOnline(getContext())) {
                    KeyboardUtils.hideKeyboard(getActivity());
                    findValue.setText("");
                    tablePanel.getAdapter().updateData(AllLinesData.getAllDataList());
                }else{
                    Toast.makeText(getContext(),"Hálózati hiba!",Toast.LENGTH_LONG).show();
                }
            }
        });
        selectBtn.setOnClickListener(this);
        findValue = rootView.findViewById(R.id.movessub_header_value);
        findValue.requestFocus();
        saveDataBtn = rootView.findViewById(R.id.movessub_saveBtn);
        saveDataBtn.setEnabled(true);
        saveDataBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(HelperClass.isOnline(getContext())){
                    if(v.isClickable()) {
                        saveData();
                    }
                }else{
                    Toast.makeText(getContext(),"Hálózati hiba!",Toast.LENGTH_LONG).show();
                }
            }
        });
        saveDataBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(HelperClass.isOnline(getContext())){
                    if(v.isClickable()) {
                        saveDataAll();
                    }
                }else{
                    Toast.makeText(getContext(),"Hálózati hiba!",Toast.LENGTH_LONG).show();
                }
                return true;
            }
        });
        String[] temp2 = SessionClass.getParam(tranCode+"_Line_ListView_Widths").split(",",-1);
        headerWidth = new int[temp2.length];
        for(int i=0;i<temp2.length;i++){
            if(Integer.parseInt(temp2[i])>0){
                headerWidth[i] = tablePanel.WRAP_MAX_COLUMN;
            }else{
                headerWidth[i] = 0;
            }
        }
        headerWidth[0]=0;
        headerText = SessionClass.getParam(tranCode+"_Line_ListView_Names").split(",",-1);
        columnName = SessionClass.getParam(tranCode+"_Line_ListView_SELECT").split(",",-1);

        ls = new ListSettings(headerText, headerWidth, columnName,true);

        // ---------------------------- Content -------------------------------

        findRow = Integer.parseInt(SessionClass.getParam(tranCode+"_Line_TextBox_Find_Index"));
        int size = db.sessionTempDao().getDataSize();
        if(reload != null && reload.equals("1") && size > 0){
            HelperClass.reloadTempSession(getContext(), columnName.length);
            Bundle b = new Bundle();
            b.putString("tranCode",tranCode);
            b.putString("tranid",tranID);
            b.putString("movenum",movenum);
            b.putString("reload","0");
            setArguments(b);
            SharedPreferences sharedPref = getActivity().getPreferences(getContext().MODE_PRIVATE);
            String line = sharedPref.getString("whrs_selexped_currentLineID","");
            SessionClass.setParam("currentLineID",line);
            tablePanel = new TablePanel(getContext(), rootView, R.id.moves_mainSubLayout, headerText, AllLinesData.getAllDataList(), headerWidth);
            createTablePanel();
        }else {
            if (AllLinesData.getLinesCount() > 0) {
                new SaveCheckedDataThread(getContext()).start();
                //CheckedList.toLogString();
                tablePanel = new TablePanel(getContext(), rootView, R.id.moves_mainSubLayout, headerText, AllLinesData.getAllDataList(), headerWidth);
                createTablePanel();
            } else {

                refreshData();
            }
        }

        if( SessionClass.getParam("currentLineID")!=null && !SessionClass.getParam("currentLineID").isEmpty() ) {
            try {
                tablePanel.getAdapter().setCheckedArray(AllLinesData.findPosition(SessionClass.getParam("currentLineID")));
                tablePanel.smoothScrollToPosition(AllLinesData.findPosition(SessionClass.getParam("currentLineID")));
            }catch(Exception e){
                e.printStackTrace();
            }
        }

        findValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkListElements();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        if( !SessionClass.getParam("scancount_selectBar").isEmpty() ){
            findValue.setText( SessionClass.getParam("scancount_selectBar")+SessionClass.getParam("barcodeSuffix") );
            SessionClass.setParam("scancount_selectBar","");
        }else {

        }
        return rootView;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.movessub_selectBtn:
                if(v.isClickable()) {
                    try {
                        if (tablePanel.getAdapter().getCheckedPosition(0) != null && tablePanel.getAdapter().getCheckedPosition(0).size() > 0) {
                            if (tranCode.charAt(0) == '1') {
                                placeDialog(tablePanel.getAdapter().getCheckedPosition(0).get(0));
                            } else {
                                loadScanCount(tablePanel.getAdapter().getCheckedPosition(0).get(0));
                            }
                        } else {
                            Toast.makeText(getContext(), "Nincs kiválasztva feladat sor!", Toast.LENGTH_LONG).show();
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                        Toast.makeText(getContext(),"Hiba a működés közben (1001)", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    private void checkListElements(){
        isBar = HelperClass.isBarcode(findValue.getText().toString());
        if( isBar != null ){
            List<String[]> resourceDataList = new ArrayList<>();
            ArrayList<String> findValueList = AllLinesData.findKeyFromMap(isBar,findRow);
            for (int i=0; i<findValueList.size();i++){
                resourceDataList.add( AllLinesData.getParam(findValueList.get(i)) );
            }
            if(tranCode.charAt(0)=='1') {
                String id = AllLinesData.searchFirstItem(findRow,qNeed,qCurrent,isBar);
                if(id == null){
                    Toast.makeText(getContext(),"Nem található ilyen elem!",Toast.LENGTH_LONG).show();
                }else{
                    placeDialog(id);
                }
            } else {
                findValue.setText(isBar);
                if (findValueList != null) {
                    if (findValueList.size() > 0) {
                        loadScanCount(findValueList.get(0));
                    }
                }
            }
        }
    }

    private void checkListElementsManual(){
        isBar = findValue.getText().toString();
        if( isBar != null ){
            List<String[]> resourceDataList = new ArrayList<>();
            ArrayList<String> findValueList = AllLinesData.findKeyFromMap(isBar,findRow);
            for (int i=0; i<findValueList.size();i++){
                resourceDataList.add( AllLinesData.getParam(findValueList.get(i)) );
            }
            if(tranCode.charAt(0)=='1') {
                String id = AllLinesData.searchFirstItem(findRow,qNeed,qCurrent,isBar);
                if(id == null){
                    Toast.makeText(getContext(),"Nem található ilyen elem!",Toast.LENGTH_LONG).show();
                }else{
                    placeDialog(id);
                }
            } else {
                if (resourceDataList.size() > 1) {
                    loadScanCount(findValueList.get(0));
                } else {
                    if (findValueList != null && findValueList.size()>0 ) {
                        if (findValueList.size() > 0) {
                            placeDialog(findValueList.get(0));
                        } else {
                            loadScanCount(findValueList.get(0));
                        }
                    }
                }
            }
        }
    }

    private void loadScanCount(String id) {
        if( SessionClass.getParam(tranCode + "_Detail_Button_IsVisible").equals("1") ){
            if (AllLinesData.getParam(id)[qBarcode01].equals("")) {
                collectDialog(id);
            }else{
                SessionClass.setParam("currentCollect",AllLinesData.getParam(id)[qBarcode01]);
                loadScanCount_now(id);
            }
        } else {
            loadScanCount_now(id);
        }
    }

    private void loadScanCount_now(String id){
        Fragment f = new ScanCountFragment();
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        Bundle b = new Bundle();
        b.putString("lineID",id);
        b.putString("isBar",findValue.getText().toString());
        b.putString("tranid", tranID);
        b.putString("tranCode",tranCode);
        f.setArguments(b);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
        ft.replace(R.id.fragments,f);
        ft.addToBackStack("app");
        ft.commit();
        findValue.setText("");
    }

    public void loadQR() {
        IntentIntegrator integrator= IntentIntegrator.forSupportFragment(this);
        integrator
                .setOrientationLocked(false)
                .setBeepEnabled(true)
                .setCameraId(0)
                .setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)
                .setBarcodeImageEnabled(true)
                .initiateScan();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result= IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if(result!=null){
            if(result.getContents()==null){
            }else{
                findValue.setText(result.getContents()+SessionClass.getParam("barcodeSuffix"));
            }
        }else{
            super.onActivityResult(requestCode, resultCode, data);
        }

    }

    private void refreshData(){
        new ChangeStatusThread(getContext(),"PDA_CHECKING", tranCode, tranID,this).start();
        String terminal = SessionClass.getParam("terminal");
        String pdaid = SessionClass.getParam("pdaid");
        String userid = SessionClass.getParam("userid");
        final String tranid = tranID;
        String listviewwhere = "ID="+tranID;
        String listviewselect = SessionClass.getParam(tranCode+"_Line_ListView_SELECT");
        String listviewfrom = SessionClass.getParam(tranCode+"_Line_ListView_FROM");
        String listvieworderby = SessionClass.getParam(tranCode+"_Line_ListView_ORDER_BY");;
        String url = SessionClass.getParam("WSUrl")+"/getTaskLines/"+terminal+"/"+userid+"/"+pdaid+"/"+listviewselect+"/"+listviewfrom+"/"+listviewwhere+"/"+listvieworderby+"/"+tranCode;
        Log.i("URL",url);
        RequestQueue rq = MySingleton.getInstance(getContext()).getRequestQueue();
        JSONObject jsonObject=null;
        pd = HelperClass.loadingDialogOn(getActivity());
        JsonRequest<JSONObject> jr = new  JsonObjectRequest(Request.Method.GET, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    AllLinesData.delParams();
                    List<String[]> tl = new ArrayList<>();
                    String rootText=response.getString("getTaskLinesResult");
                    JSONArray jsonArray = new JSONArray(rootText);
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject jsonObj = jsonArray.getJSONObject(i);
                        String[] dataText = new String[20];
                        for(int j=0;j<columnName.length;j++){
                            String value = "";
                            value = jsonObj.getString(columnName[j]);
                            if(!value.equals("null")){
                                dataText[j] = value;
                            }else{
                                if( columnName[j].equals("To_Place") || columnName[j].equals("Barcode01") || columnName[j].equals("Barcode02")) {
                                    dataText[j] = "";
                                }
                            }
                        }
                        AllLinesData.setParam(dataText[0],dataText);
                        tl.add(dataText);
                    }
                    tablePanel = new TablePanel(getContext(), rootView, R.id.moves_mainSubLayout, headerText, tl,headerWidth);
                    SharedPreferences sharedPref = getActivity().getPreferences(getContext().MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("whrs_selexped_tranID", tranID);
                    editor.putString("whrs_selexped_tranCode", tranCode);
                    editor.putString("whrs_selexped_movenum", movenum);
                    editor.commit();

                    SaveAllSessionTemp sst = new SaveAllSessionTemp(getContext());
                    sst.start();
                    createTablePanel();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                pd.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error!=null){
                    error.printStackTrace();
                }
                pd.dismiss();
            }
        });
        jr.setRetryPolicy(new DefaultRetryPolicy(50000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rq.add(jr);

    }

    private void createTablePanel() {
        TablePanel.TablePanelSetting tps = tablePanel.getTablePanelSettingInstance();
        tps.setCheckable(true);
        tps.setFontSize(14f);
        tps.setCellLeftRightPadding(HelperClass.dpToPx(getContext(),6));
        tps.setCellTopBottomPadding(HelperClass.dpToPx(getContext(),6));
        tps.setOnRowClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KeyboardUtils.hideKeyboard(getActivity());
                CheckBox cb = v.findViewById(R.dimen.tablepanel_row_checkBox);
                if(v.getId() != R.dimen.tablepanel_row_checkBox) cb.setChecked(!cb.isChecked());
                for(int i = 0; i < tablePanel.getAdapter().getItemCount(); i++){
                    tablePanel.getAdapter().checkedList[i] = false;
                }
                tablePanel.getAdapter().checkedList[(int)cb.getTag()] = cb.isChecked();
                tablePanel.getAdapter().update();
                List<String> chcekedList = tablePanel.getAdapter().getCheckedPosition(0);

                if(chcekedList!=null && chcekedList.size()>0){
                    //Toast.makeText(getContext(),"Kiválasztva: "+chcekedList.size()+" elem",Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getContext(),"NINCS KIVÁLASZTVA SEMMI",Toast.LENGTH_LONG).show();
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
        List<TablePanel.RowSetting> rowSettingsList = new ArrayList<>();

        List<String[]> rowData = AllLinesData.getAllDataList();
        for(int rowCount=0; rowCount<rowData.size(); rowCount++){
            TablePanel.RowSetting rowSetting = tablePanel.getRowSettingInstance();
            if (!rowData.get(rowCount)[qCurrent].equals("0")){
                try {
                    if (Integer.parseInt(rowData.get(rowCount)[qCurrent]) == Integer.parseInt(rowData.get(rowCount)[qNeed])) { rowSetting.setBackColor(R.color.productRowOKBack); } else
                    if (Integer.parseInt(rowData.get(rowCount)[qCurrent]) < Integer.parseInt(rowData.get(rowCount)[qNeed])) { rowSetting.setBackColor(R.color.productRowPHBack); }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }else{
            }
            rowSettingsList.add(rowSetting);
        }
        tablePanel.setRowSetting(rowSettingsList);
        tablePanel.setTablePanelSetting(tps);
        tablePanel.createTablePanel();
    }

    public void saveData(){
        //CheckedList.toLogString();
        KeyboardUtils.hideKeyboard(getActivity());
        try{
            List<String[]> data = AllLinesData.getAllDataList();
            if(data.size()>0) {
                progressPercent.setText("0 %");
                uploadpbar.setProgress(0);
                progressLayout.setVisibility(View.VISIBLE);
                int fromNum, toNum;
                int db = 100;
                int count = (int) Math.ceil(data.size() / db);
                uploadpbar.setMax(count);
                saveDataBtn.setClickable(false);
                lockBtn.setClickable(false);
                selectBtn.setClickable(false);
                flushBtn.setClickable(false);
                for (int i = 0; i <= count; i++) {
                    fromNum = i * db;
                    if (i == count) {
                        toNum = data.size();
                    } else {
                        toNum = db * (i + 1);
                    }
                    SaveDataThread sds = new SaveDataThread(getContext(), data, this, fromNum, toNum);
                    sds.run();

                }
                lockBtn.setEnabled(true);
            }else{
                Toast.makeText(getContext(),"Nincs feltöltendő adat!",Toast.LENGTH_LONG).show();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void saveDataAll(){
        KeyboardUtils.hideKeyboard(getActivity());
        try{
            List<String[]> data = AllLinesData.getAllDataList();
            if(data.size()>0) {
                progressPercent.setText("0 %");
                uploadpbar.setProgress(0);
                progressLayout.setVisibility(View.VISIBLE);
                int fromNum, toNum;
                int db = 100;
                int count = (int) Math.ceil(data.size() / db);
                uploadpbar.setMax(count);
                saveDataBtn.setClickable(false);
                lockBtn.setClickable(false);
                selectBtn.setClickable(false);
                flushBtn.setClickable(false);
                for (int i = 0; i <= count; i++) {
                    fromNum = i * db;
                    if (i == count) {
                        toNum = data.size();
                    } else {
                        toNum = db * (i + 1);
                    }
                    SaveDataThread_All sds = new SaveDataThread_All(getContext(), data, this, fromNum, toNum);
                    sds.run();

                }
                lockBtn.setEnabled(true);
            }else{
                Toast.makeText(getContext(),"Nincs feltöltendő adat!",Toast.LENGTH_LONG).show();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    private void buildDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Kilépés a sorokól!");
        TextView tv1 = new TextView(getContext());
        tv1.setPadding(HelperClass.dpToPx(getContext(),20),HelperClass.dpToPx(getContext(),20),0,0);
        tv1.setText("Kilépés előtt menteni szeretné az eddigi munkáját?");
        builder.setView(tv1);
        builder.setNegativeButton("Nem", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                KeyboardUtils.hideKeyboard(getActivity());
                dialog.cancel();
            }
        });
        builder.setPositiveButton("Igen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveData();
            }
        });
        builder.show();
    }

    private void closeLine(){
        db.sessionTempDao().deleteAllData();
        SessionClass.setParam("currentLineID","");
        KeyboardUtils.hideKeyboard(getActivity());
        CloseLineThread cl = new CloseLineThread(getContext(),tranCode, tranID, this);
        cl.start();
    }

    private void closeWithoutSave(){
        closeMovesDialog(this);
    }

    public void closeFragment(){
        KeyboardUtils.hideKeyboard(getActivity());

        if(SessionClass.getParam( tranCode+"_Detail_Button_IsVisible").equals("1") ){
            ((MovesSubViewPager)getParentFragment()).closeFragment();
        }else{
            getFragmentManager().popBackStack();
        }

    }


    void placeDialog(final String id) {
        AllLinesData.toStringLog();
        if (AllLinesData.getParam(id)[10].equals("") || AllLinesData.getParam(id)[10].isEmpty()) {
            KeyboardUtils.showKeyboard(getActivity());
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            View v = getLayoutInflater().inflate(R.layout.dialog_place_number, null);
            final EditText ed1 = v.findViewById(R.id.dialog_place_item);
            builder.setView(v);
            ed1.setOnKeyListener(new View.OnKeyListener()
            {
                public boolean onKey(View v, int keyCode, KeyEvent event)
                {
                    if (event.getAction() == KeyEvent.ACTION_DOWN)
                    {
                        switch (keyCode)
                        {
                            case KeyEvent.KEYCODE_DPAD_CENTER:
                            case KeyEvent.KEYCODE_ENTER:
                                KeyboardUtils.hideKeyboard(getActivity());
                                SessionClass.setParam("currentPlace", ed1.getText().toString());
                                loadScanCount(id);
                                popup.dismiss();
                                return true;
                            default:
                                break;
                        }
                    }
                    return false;
                }
            });

            ed1.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() > 3) {
                        if (s.toString().substring(s.length() - SessionClass.getParam("barcodeSuffix").length(), s.length()).equals(SessionClass.getParam("barcodeSuffix"))) {
                            KeyboardUtils.hideKeyboard(getActivity());
                            SessionClass.setParam("currentPlace", s.toString().substring(0, s.toString().length() - SessionClass.getParam("barcodeSuffix").length()));
                            loadScanCount(id);
                            popup.dismiss();
                        }
                    }
                }
            });
            builder.setPositiveButton("Megadás", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (ed1.getText().toString().equals("") || ed1.getText().toString().equals(" ")) {
                        Toast.makeText(getContext(), "Nem adott meg rakhelyet!", Toast.LENGTH_LONG).show();
                    } else {
                        SessionClass.setParam("currentPlace", ed1.getText().toString());
                        loadScanCount(id);
                        KeyboardUtils.hideKeyboard(getActivity());
                        dialog.dismiss();
                    }
                }
            });
            builder.setNegativeButton("Mégsem", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    KeyboardUtils.hideKeyboard(getActivity());
                    dialog.cancel();
                }
            });
            popup = builder.create();
            popup.setTitle("Rakhely megadása");
            popup.show();
        } else {
            SessionClass.setParam("currentPlace", AllLinesData.getParam(id)[10]);
            KeyboardUtils.hideKeyboard(getActivity());
            loadScanCount(id);
        }
    }

    private List<Integer> checkLineColor(){
        List<Integer> textColor = new ArrayList<>();
        List<String[]> allData = AllLinesData.getAllDataList();
        for(int i=0; i<allData.size() ;i++){
            int curr = 0;
            int need = 0;
            int color = 0;
            try{ curr = Integer.parseInt(allData.get(i)[qCurrent]); }catch (Exception e){}
            try{ need = Integer.parseInt(allData.get(i)[qNeed]); }catch (Exception e){}
            if( (curr < need) && curr!=0 ){ color = R.color.productRowPHBack; } else
            if(curr == need){ color = R.color.productRowOKBack; } else {
                color = R.color.productRowNOTBack;
            }
            textColor.add(color);
        }
        return textColor;
    }

    public void stopProgress(){
        uploadpbar.setProgress(uploadpbar.getProgress()+1);
        progressPercent.setText( (int)(((float)uploadpbar.getProgress()/uploadpbar.getMax())*100)+" %");

        if( uploadpbar.getMax() == uploadpbar.getProgress() ){
            progressLayout.setVisibility(View.GONE);
            saveDataBtn.setClickable(true);
            lockBtn.setClickable(true);
            selectBtn.setClickable(true);
            flushBtn.setClickable(true);
        }

    }

    void closeMovesDialog( final MovesSubTableFragment frg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Kilépés a feladatból");
        builder.setMessage("Biztos, hogy kilép a feladatból, ha igen minden eddig mentett adat törlődik!");
        builder.setPositiveButton("Igen, megértettem", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String mode = "";
                if (tranCode.charAt(0) == '1') mode = "Áruátvétel";
                if (tranCode.charAt(0) == '2') mode = "Árukiadás";
                if (tranCode.charAt(0) == '5') mode = "Szedés";
                db.logDao().addLog(new LogTable("M", "Feladat eldobása ( " + mode + ": " + movenum + " )", SessionClass.getParam("account"), HelperClass.getCurrentDate(), HelperClass.getCurrentTime()));
                db.sessionTempDao().deleteAllData();
                SessionClass.setParam("currentLineID", "");
                KeyboardUtils.hideKeyboard(getActivity());
                new ChangeStatusThread(getContext(), "PDA", tranCode, tranID, frg).start();
            }
        });
        builder.setNegativeButton("Nem lépek ki", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                KeyboardUtils.hideKeyboard(getActivity());
                dialog.cancel();
            }
        });

        builder.setCancelable(false);
        builder.show();
    }

    private void collectDialog(final String id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View v = getLayoutInflater().inflate(R.layout.dialog_collector,null);
        final EditText eanET = v.findViewById(R.id.dialog_collect_ean);
        eanET.setText("");
        eanET.setOnKeyListener(new View.OnKeyListener()
        {
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if (event.getAction() == KeyEvent.ACTION_DOWN)
                {
                    switch (keyCode)
                    {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            KeyboardUtils.hideKeyboard(getActivity());
                            SessionClass.setParam("currentCollect",eanET.getText().toString());
                            loadScanCount_now(id);
                            popup.dismiss();
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });
        eanET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 3) {
                    if (s.toString().substring(s.length() - SessionClass.getParam("barcodeSuffix").length(), s.length()).equals(SessionClass.getParam("barcodeSuffix"))) {
                        KeyboardUtils.hideKeyboard(getActivity());
                        SessionClass.setParam("currentCollect", s.toString().substring(0, s.toString().length() - SessionClass.getParam("barcodeSuffix").length()));
                        loadScanCount_now(id);
                        popup.dismiss();
                    }
                }
            }
        });
        builder.setView(v);
        builder.setPositiveButton("IGEN", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                KeyboardUtils.hideKeyboard(getActivity());
                SessionClass.setParam("currentCollect",eanET.getText().toString());
                loadScanCount_now(id);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("NEM", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                KeyboardUtils.hideKeyboard(getActivity());
                dialog.cancel();
            }
        });
        popup = builder.create();
        popup.show();
    }


/*
    private void palettDialog() {
        final boolean locked = false;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View v = getLayoutInflater().inflate(R.layout.frg_palett,null);
        final EditText bar1 = v.findViewById(R.id.dialog_palett_barcode1);
        ImageView lockBtn = v.findViewById(R.id.dialog_palett_lock);
        lockBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(locked){
                    ((ImageView)v).setImageDrawable(getResources().getDrawable(R.drawable.lock_off));
                }else{
                    ((ImageView)v).setImageDrawable(getResources().getDrawable(R.drawable.lock_on));
                }
            }
        });
        bar1.setText("");
        bar1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 3) {
                    if (s.toString().substring(s.length() - 2, s.length()).equals(SessionClass.getParam("barcodeSuffix"))) {
                        KeyboardUtils.hideKeyboard(getActivity());
                        popup.dismiss();
                    }
                }
            }
        });
        final EditText bar2 = v.findViewById(R.id.dialog_palett_barcode2);
        bar2.setText("");
        bar2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 3) {
                    if (s.toString().substring(s.length() - 2, s.length()).equals(SessionClass.getParam("barcodeSuffix"))) {
                        KeyboardUtils.hideKeyboard(getActivity());
                        popup.dismiss();
                    }
                }
            }
        });

        builder.setView(v);
        builder.setPositiveButton("IGEN", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                KeyboardUtils.hideKeyboard(getActivity());
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("NEM", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                KeyboardUtils.hideKeyboard(getActivity());
                dialog.cancel();
            }
        });
        popup = builder.create();
        popup.show();
    }*/
}