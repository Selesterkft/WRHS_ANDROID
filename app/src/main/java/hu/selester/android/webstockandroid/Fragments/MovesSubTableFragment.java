package hu.selester.android.webstockandroid.Fragments;

import android.annotation.SuppressLint;
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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
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

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import hu.selester.android.webstockandroid.Adapters.XD_Dialog_ItemsParametersListAdapter;
import hu.selester.android.webstockandroid.Adapters.XD_ItemsParametersListAdapter;
import hu.selester.android.webstockandroid.Database.SelesterDatabase;
import hu.selester.android.webstockandroid.Database.Tables.LogTable;
import hu.selester.android.webstockandroid.Database.Tables.SessionTemp;
import hu.selester.android.webstockandroid.Divider.SimpleDividerItemDecoration;
import hu.selester.android.webstockandroid.Helper.HelperClass;
import hu.selester.android.webstockandroid.Helper.KeyboardUtils;
import hu.selester.android.webstockandroid.Helper.MySingleton;
import hu.selester.android.webstockandroid.Objects.ActiveFragment;
import hu.selester.android.webstockandroid.Objects.AllLinesData;
import hu.selester.android.webstockandroid.Objects.CheckedList;
import hu.selester.android.webstockandroid.Objects.InsertedList;
import hu.selester.android.webstockandroid.Objects.ListSettings;
import hu.selester.android.webstockandroid.Objects.NotCloseList;
import hu.selester.android.webstockandroid.Objects.SessionClass;
import hu.selester.android.webstockandroid.Objects.XD_Dialog_ItemsParameters;
import hu.selester.android.webstockandroid.Objects.XD_ItemsParameters;
import hu.selester.android.webstockandroid.R;
import hu.selester.android.webstockandroid.TablePanel.TablePanel;
import hu.selester.android.webstockandroid.Threads.ChangeStatusThread;
import hu.selester.android.webstockandroid.Threads.CloseLineThread;
import hu.selester.android.webstockandroid.Threads.SaveCheckedDataThread;
import hu.selester.android.webstockandroid.Threads.SaveDataThread;
import hu.selester.android.webstockandroid.Threads.SaveAllSessionTemp;
import hu.selester.android.webstockandroid.Threads.SaveDataThread_All;
import hu.selester.android.webstockandroid.Threads.SaveDataThread_All_INSERT;
import hu.selester.android.webstockandroid.Threads.UploadFilesThread;
import mobil.selester.wheditbox.WHEditBox;

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
    private int qNeed,qCurrent,qBarcode01,qMissing,qBarcode,qLineID,qEvidNum,qTo_Place,qRampNum, qWeight, qWidth, qHeight, qLength, qRefLineId;
    private ImageButton saveDataBtn, lockBtn, selectBtn, flushBtn, createBtn, paramsBtn;
    private int qBreak, qCollection, qTakePhoto, qMAR;
    private String[] arrayBtnVisibility;
    private Dialog popup = null;
    public ProgressBar uploadpbar;
    private LinearLayout progressLayout;
    private TextView progressPercent, findTvLabel;
    private LinearLayout functLayout;
    private TablePanel tablePanel;
    private int[] headerWidth;
    private String[] headerText;
    private String reload, listFrom;
    private SelesterDatabase db;
    private ProgressDialog pd;
    private boolean closeMovesDialogRun;
    private String resultText;
    private List<String[]> rowTempData = new ArrayList<>();
    private AlertDialog.Builder builderSelect;
    private String dialogSelectItem;
    private List<String[]> dataDialog;
    private AlertDialog dialog;

    public static MovesSubTableFragment getInstance(Bundle args){
        MovesSubTableFragment f = new MovesSubTableFragment();
        f.setArguments(args);
        return f;
    }

    @Override
    public void onResumeFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ActiveFragment.setFragment(this);
        closeMovesDialogRun = false;
        db = SelesterDatabase.getDatabase(getContext());
        tranCode = getArguments().getString("tranCode");
        SessionClass.setParam("tranCode", ""+tranCode);
        WHEditBox.activity = getActivity();
        WHEditBox.suffix = SessionClass.getParam("barcodeSuffix");

        tranID      = getArguments().getString("tranid");
        movenum     = getArguments().getString("movenum");
        reload      = getArguments().getString("reload");

        qWeight     = HelperClass.getArrayPosition("Weight",        SessionClass.getParam(tranCode + "_Line_ListView_SELECT"));
        qWidth      = HelperClass.getArrayPosition("Size_Width",    SessionClass.getParam(tranCode + "_Line_ListView_SELECT"));
        qHeight     = HelperClass.getArrayPosition("Size_Height",   SessionClass.getParam(tranCode + "_Line_ListView_SELECT"));
        qLength     = HelperClass.getArrayPosition("Size_Length",   SessionClass.getParam(tranCode + "_Line_ListView_SELECT"));
        qRefLineId  = HelperClass.getArrayPosition("Ref_Line_ID",   SessionClass.getParam(tranCode + "_Line_ListView_SELECT"));

        qBarcode01  = HelperClass.getArrayPosition("Barcode01", SessionClass.getParam(tranCode + "_Line_ListView_SELECT"));
        qBarcode    = HelperClass.getArrayPosition("Barcode", SessionClass.getParam(tranCode + "_Line_ListView_SELECT"));
        qMissing    = HelperClass.getArrayPosition("Missing_Qty", SessionClass.getParam(tranCode + "_Line_ListView_SELECT"));
        qLineID     = HelperClass.getArrayPosition("Line_ID", SessionClass.getParam(tranCode + "_Line_ListView_SELECT"));
        qEvidNum    = HelperClass.getArrayPosition("EvidNum", SessionClass.getParam(tranCode + "_Line_ListView_SELECT"));
        qRampNum    = HelperClass.getArrayPosition("Loading_Gate", SessionClass.getParam(tranCode + "_Line_ListView_SELECT"));
        if(tranCode.charAt(0)=='4'){
            qTo_Place  = HelperClass.getArrayPosition("From_place", SessionClass.getParam(tranCode + "_Line_ListView_SELECT"));
        }else{
            qTo_Place  = HelperClass.getArrayPosition("To_Place", SessionClass.getParam(tranCode + "_Line_ListView_SELECT"));
        }

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
        findRow         = Integer.parseInt(SessionClass.getParam(tranCode+"_Line_TextBox_Find_Index"));
        rootView        = inflater.inflate(R.layout.frg_movessub, container, false);
        functLayout     = rootView.findViewById(R.id.movessub_tableRoot);
        uploadpbar      = rootView.findViewById(R.id.movessub_progressBar);
        progressLayout  = rootView.findViewById(R.id.movessub_progressLayout);
        progressPercent = rootView.findViewById(R.id.movessub_progresspercent);
        findTvLabel     = rootView.findViewById(R.id.movessub_header_label);
        findTvLabel.setText(SessionClass.getParam(tranCode + "_Line_TextBox_Find_Text"));
        findTvLabel.setLongClickable(true);
        findTvLabel.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v) {
                saveImageDialog();
                return true;
            }
        });
        progressLayout.setVisibility(View.GONE);
        SessionClass.setParam("currentPlace","");
        selectBtn           = rootView.findViewById(R.id.movessub_selectBtn);
        Button delTextBtn   = rootView.findViewById(R.id.movessub_header_delbtn);
        Button findValueBtn = rootView.findViewById(R.id.movessub_header_btn);
        lockBtn             = rootView.findViewById(R.id.movessub_lockBtn);
        flushBtn            = rootView.findViewById(R.id.movessub_flushBtn);
        createBtn           = rootView.findViewById(R.id.movessub_createBtn);
        paramsBtn           = rootView.findViewById(R.id.movessub_paramsBtn);

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

        if (SessionClass.getParam("breakBtn").equals("1")) {
            createBtn.setVisibility(View.VISIBLE);
            createBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    createLineDialog();
                }
            });
        }else{
            createBtn.setVisibility(View.GONE);
        }
        paramsBtn.setVisibility(View.GONE);
        if (tranCode.charAt(0) == '3') {
            paramsBtn.setVisibility(View.VISIBLE);
            paramsBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                        if (tablePanel.getAdapter().getCheckedPosition(0) != null && tablePanel.getAdapter().getCheckedPosition(0).size() > 0) {
                        Fragment f = new XD_ItemParametersFragment();
                        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                        Bundle b = new Bundle();
                        b.putString("tranid", tranID);
                        b.putString("movenum", "");
                        b.putString("tranCode", tranCode);
                        b.putString("evidNum",AllLinesData.getParam(tablePanel.getAdapter().getCheckedPosition(0).get(0))[qEvidNum]);
                        f.setArguments(b);
                        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
                        ft.replace(R.id.fragments, f);
                        ft.addToBackStack("app");
                        ft.commit();
                    }
                }
            });
        }
        LinearLayout palettBtn = rootView.findViewById(R.id.movessub_palettBtn);
        if( SessionClass.getParam("collectionBtn").equals("1") ){
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
                    if( CheckedList.getSizeOfChecked() == 0 ) {
                        boolean error = false;
                        String errorString = "";
                        if( tranCode.charAt(0) == '3' ) {
                            for (Map.Entry<String, String[]> entry : AllLinesData.getAllParam().entrySet()) {
                                if (!InsertedList.isInsert(entry.getKey()) && NotCloseList.getParamItem(entry.getKey()) == 0) {
                                    if (!entry.getValue()[qCurrent].equals("0")) {
                                        error = true;
                                        errorString += entry.getValue()[qEvidNum] + ", ";
                                    }
                                }
                            }
                            if (!errorString.equals("")) errorString = "Nincs minden bevételezett tétel rakhelyre helyezve!\nEvidenciaszám(ok):\n"+errorString.substring(0, errorString.length() - 2);
                        }else if( tranCode.charAt(0) == '4' ) {
                            for (Map.Entry<String, String[]> entry : AllLinesData.getAllParam().entrySet()) {
                                if ( !entry.getValue()[qCurrent].equals( entry.getValue()[qNeed] )  && NotCloseList.getParamItem(entry.getKey()) == 0 ) {
                                    error = true;
                                    errorString += entry.getValue()[qEvidNum] + ", ";
                                }
                            }
                            if (!errorString.equals("")) errorString = "Nincs minden tétel kitárolva!\nEvidenciaszám(ok):\n"+errorString.substring(0, errorString.length() - 2);
                        }
                        if(error){
                            HelperClass.messageBox(getActivity(),"Feladat zárása",errorString,3);
                        }else{
                            closeDialog();
                        }
                    }else{
                        CheckedList.toLogString();
                        notSaveAll();
                    }
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
                if (tranCode.charAt(0) == '3') {
                    rowTempData = AllLinesData.getGroupByParam(qEvidNum, qNeed, qCurrent, qMissing);
                }else{
                    rowTempData = AllLinesData.getAllDataList();
                }
                tablePanel.getAdapter().updateData(rowTempData);
            }else{
                HelperClass.messageBox(getActivity(),"Feladatkezelés","Hálózati hiba!",HelperClass.ERROR);
            }
            }
        });
        selectBtn.setOnClickListener(this);
        findValue = rootView.findViewById(R.id.movessub_header_value);
        findValue.setOnFocusChangeListener(HelperClass.getFocusBorderListener(getContext()));
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
                    HelperClass.messageBox(getActivity(),"Feladatkezelés","Hálózati hiba!",HelperClass.ERROR);
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
                    HelperClass.messageBox(getActivity(),"Feladatkezelés","Hálózati hiba!",HelperClass.ERROR);
                }
                return true;
            }
        });
        String[] temp2 = SessionClass.getParam(tranCode+"_Line_ListView_Widths").split(",",-1);
        headerWidth = new int[temp2.length];
        for(int i=0;i<temp2.length;i++){
            temp2[i].replace(" ","");
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

            if (tranCode.charAt(0) == '3') {
                rowTempData = AllLinesData.getGroupByParam(qEvidNum, qNeed, qCurrent, qMissing);
            }else{
                rowTempData = AllLinesData.getAllDataList();
            }
            tablePanel = new TablePanel(getContext(), rootView, R.id.moves_mainSubLayout, headerText, rowTempData, headerWidth);
            createTablePanel();
        }else {
            if (AllLinesData.getLinesCount() > 0) {
                new SaveCheckedDataThread(getContext()).start();
                if (tranCode.charAt(0) == '3') {
                    rowTempData = AllLinesData.getGroupByParam(qEvidNum, qNeed, qCurrent, qMissing);
                }else{
                    rowTempData = AllLinesData.getAllDataList();
                }
                tablePanel = new TablePanel(getContext(), rootView, R.id.moves_mainSubLayout, headerText, rowTempData, headerWidth);
                createTablePanel();
            } else {
                refreshData();
            }
        }

        if( SessionClass.getParam("currentLineID")!=null && !SessionClass.getParam("currentLineID").isEmpty() && tranCode.charAt(0) != '3' ) {
            try {
                tablePanel.getAdapter().setCheckedArray(AllLinesData.findPosition(SessionClass.getParam("currentLineID")));
                tablePanel.smoothScrollToPosition(AllLinesData.findPosition(SessionClass.getParam("currentLineID")));
            }catch(Exception e){
                db.logDao().addLog(new LogTable(LogTable.LogType_Error,"MovesSubTableFragment",e.getMessage(),"LOGUSER",null,null));
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
            findValue.setText( SessionClass.getParam("scancount_selectBar") + SessionClass.getParam("barcodeSuffix") );
            SessionClass.setParam("scancount_selectBar","");
        }else {

        }
        KeyboardUtils.hideKeyboard(getActivity());
        KeyboardUtils.hideKeyboardFrom(getContext(),rootView);
        InsertedList.toStringLog();
        findValue.requestFocus();
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
                            HelperClass.messageBox(getActivity(),"Feladatkezelés","Nincs kiválasztva feladat sor!",HelperClass.ERROR);
                            //Toast.makeText(getContext(), "Nincs kiválasztva feladat sor!", Toast.LENGTH_LONG).show();
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                        db.logDao().addLog(new LogTable(LogTable.LogType_Error,"MovesSubTableFragment",e.getMessage(),"LOGUSER",null,null));
                        HelperClass.messageBox(getActivity(),"Feladatkezelés","Hiba a működés közben (1001)",HelperClass.ERROR);
                        //Toast.makeText(getContext(),"Hiba a működés közben (1001)", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    private void checkListElements(){
        isBar = HelperClass.isBarcode(findValue.getText().toString());
        if( isBar != null ){
            isBar = HelperClass.getTrimmedText(isBar);
            List<String[]> resourceDataList = new ArrayList<>();
            ArrayList<String> findValueList = AllLinesData.findKeyFromMap(isBar,findRow);
            for (int i=0; i<findValueList.size();i++){
                resourceDataList.add( AllLinesData.getParam(findValueList.get(i)) );
            }
            if(tranCode.charAt(0)=='1') {
                String id = AllLinesData.searchFirstItem(findRow,qNeed,qCurrent,isBar);
                if(id == null){
                    HelperClass.messageBox(getActivity(),"Feladatkezelés","Nem található ilyen elem!",HelperClass.ERROR);
                    //Toast.makeText(getContext(),"Nem található ilyen elem!",Toast.LENGTH_LONG).show();
                }else{
                    placeDialog(id);
                }
            } else {
                findValue.setText(isBar);
                if (findValueList != null && findValueList.size() > 0 ) {
                    loadScanCount(findValueList.get(0));
                }else{
                    findValue.setText("");
                    HelperClass.messageBox(getActivity(),"Feladatkezelés","Nem található ilyen elem!",HelperClass.ERROR);
                    //Toast.makeText(getContext(),"Nem található ilyen elem!",Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void checkListElementsManual(){
        isBar = findValue.getText().toString();
        //findValue.setText(isBar);
        if( isBar != null ){
            List<String[]> resourceDataList = new ArrayList<>();
            ArrayList<String> findValueList = AllLinesData.findKeyFromMap(isBar,findRow);
            for (int i=0; i<findValueList.size();i++){
                resourceDataList.add( AllLinesData.getParam(findValueList.get(i)) );
            }
            if(tranCode.charAt(0)=='1') {
                String id = AllLinesData.searchFirstItem(findRow,qNeed,qCurrent,isBar);
                if(id == null){
                    findValue.setText("");
                    HelperClass.messageBox(getActivity(),"Feladatkezelés","Nem található ilyen elem!",HelperClass.ERROR);
                    //Toast.makeText(getContext(),"Nem található ilyen elem!",Toast.LENGTH_LONG).show();
                }else{
                    placeDialog(id);
                }
            } else {
                if (resourceDataList.size() > 1) {
                    loadScanCount(findValueList.get(0));
                } else {
                    if (findValueList != null && findValueList.size()>0 ) {
                        if (findValueList.size() > 0) {
                            if(tranCode.charAt(0)!='3') {
                                placeDialog(findValueList.get(0));
                            }else{
                                loadScanCount(findValueList.get(0));
                            }
                        } else {
                            loadScanCount(findValueList.get(0));
                        }
                    }else{
                        findValue.setText("");
                        HelperClass.messageBox(getActivity(),"Feladatkezelés","Nem található ilyen elem!",HelperClass.ERROR);
                        //Toast.makeText(getContext(),"Nem található ilyen elem!",Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    }

    private void loadScanCount(String id) {
        if( SessionClass.getParam("collectionBtn").equals("1") ){
            if (AllLinesData.getParam(id)[qBarcode01].equals("")) {
                collectDialog(id);
            }else{
                SessionClass.setParam("currentCollect",AllLinesData.getParam(id)[qBarcode01]);
                loadScanCount_now(id);
            }
        } else {
            checkBarcode(id);
        }
    }

    private void checkBarcode(String id){
        String[] barcodes = AllLinesData.getParam(id);
        List<String[]> data = AllLinesData.findSameUniqueItemsFromMap(barcodes[qBarcode] , qBarcode, qRefLineId);
        if(data.size() > 1){
            if( qTo_Place > 0) showSelectItemDialog(id, data, qTo_Place);
        }if(data.size() == 1){
            loadScanCount_now(data.get(0)[0]);
        }if(data.size() == 0){
            HelperClass.messageBox(getActivity(),"Feladatkezelés","Az adott tétel már rakhelyen van!\nHa változtatni szeretné kérem törölje a rakhelyről!",HelperClass.WARNING);
        }
    }

    private void loadScanCount_now(String id){
        KeyboardUtils.hideKeyboard(getActivity());
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
        String listviewwhere = "ID="+tranID;
        String listviewselect = SessionClass.getParam(tranCode+"_Line_ListView_SELECT");
        String listviewfrom = SessionClass.getParam(tranCode+"_Line_ListView_FROM");
        String listvieworderby = SessionClass.getParam(tranCode+"_Line_ListView_ORDER_BY");
        String url;
        if(tranCode.charAt(0)=='3') {
            url = SessionClass.getParam("WSUrl")+"/WRHS_PDA_XD_getTask/" + terminal + "/" + userid + "/" + tranID + "/" + tranCode + "/" + pdaid + "/" + listviewselect + "/" + listviewfrom + "/nothing/" + listvieworderby;
            resultText = "WRHS_PDA_XD_getTaskResult";
        }else{
            url = SessionClass.getParam("WSUrl")+"/getTaskLines/"+terminal+"/"+userid+"/"+pdaid+"/"+listviewselect+"/"+listviewfrom+"/"+listviewwhere+"/"+listvieworderby+"/"+tranCode;
            resultText = "getTaskLinesResult";
        }
        RequestQueue rq = MySingleton.getInstance(getContext()).getRequestQueue();
        JSONObject jsonObject=null;
        pd = HelperClass.loadingDialogOn(getActivity());
        Log.i("URL",url);
        JsonRequest<JSONObject> jr = new  JsonObjectRequest(Request.Method.GET, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    AllLinesData.delParams();
                    List<String[]> tl = new ArrayList<>();
                    String rootText=response.getString(resultText);
                    JSONArray jsonArray = new JSONArray(rootText);
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject jsonObj = jsonArray.getJSONObject(i);
                        String[] dataText = new String[40];
                        for(int j=0;j<columnName.length;j++){
                            String value = "";
                            value = jsonObj.getString(columnName[j]);
                            if(!value.equals("null")){
                                dataText[j] = value;
                            }else{
                                if( columnName[j].equals("To_Place") || columnName[j].equals("Barcode01") || columnName[j].equals("Barcode02") || columnName[j].equals("Ref_Line_ID") || columnName[j].equals("Missing_Qty") ){
                                    dataText[j] = "";
                                }
                                if( columnName[j].equals("Needed_Qty") || columnName[j].equals("Current_Qty")) {
                                    dataText[j] = "0";
                                }
                            }
                        }
                        if( qRampNum > -1 ){
                            if( SessionClass.getParam("rampNum") != null && !SessionClass.getParam("rampNum").equals("") ){
                                dataText[qRampNum] = SessionClass.getParam("rampNum");
                            }
                        }

                        AllLinesData.setParam(dataText[0],dataText);
                        NotCloseList.setParamItem(dataText[0],0);
                        tl.add(dataText);
                    }
                    AllLinesData.toStringLog();
                    if (tranCode.charAt(0) == '3') {
                        rowTempData = AllLinesData.getGroupByParam(qEvidNum, qNeed, qCurrent, qMissing);
                    }else{
                        rowTempData = AllLinesData.getAllDataList();
                    }
                    tablePanel = new TablePanel(getContext(), rootView, R.id.moves_mainSubLayout, headerText, rowTempData,headerWidth);
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
                    db.logDao().addLog(new LogTable(LogTable.LogType_Error,"MovesSubTableFragment",e.getMessage(),"LOGUSER",null,null));
                    e.printStackTrace();
                }
                if( pd!=null ){
                    pd.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error!=null){
                    error.printStackTrace();
                }
                if( pd!=null ){
                    pd.dismiss();
                }
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
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View v) {
                KeyboardUtils.hideKeyboard(getActivity());
                @SuppressLint("ResourceType") CheckBox cb = v.findViewById(R.dimen.tablepanel_row_checkBox);
                if(v.getId() != R.dimen.tablepanel_row_checkBox) cb.setChecked(!cb.isChecked());
                for(int i = 0; i < tablePanel.getAdapter().getItemCount(); i++){
                    tablePanel.getAdapter().checkedList[i] = false;
                }
                tablePanel.getAdapter().checkedList[(int)cb.getTag()] = cb.isChecked();
                tablePanel.getAdapter().update();
                List<String> chcekedList = tablePanel.getAdapter().getCheckedPosition(0);

                if(chcekedList!=null && chcekedList.size()>0){

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
        List<String[]> rowData;
        if (tranCode.charAt(0) == '3') {
            rowData = AllLinesData.getGroupByParam(qEvidNum, qNeed, qCurrent, qMissing);
        }else{
            rowData = AllLinesData.getAllDataList();
        }
        for(int rowCount=0; rowCount<rowData.size(); rowCount++){
            TablePanel.RowSetting rowSetting = tablePanel.getRowSettingInstance();
            if (!rowData.get(rowCount)[qCurrent].equals("0")){
                try {
                    if (Integer.parseInt(rowData.get(rowCount)[qCurrent]) == Integer.parseInt(rowData.get(rowCount)[qNeed])) { rowSetting.setBackColor(R.color.productRowOKBack); } else
                    if (Integer.parseInt(rowData.get(rowCount)[qCurrent]) < Integer.parseInt(rowData.get(rowCount)[qNeed])) { rowSetting.setBackColor(R.color.productRowPHBack); }
                }catch (Exception e){
                    db.logDao().addLog(new LogTable(LogTable.LogType_Error,"MovesSubTableFragment",e.getMessage(),"LOGUSER",null,null));
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
            if(data.size()>0 && CheckedList.getSizeOfChecked() > 0 ) {
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
            db.logDao().addLog(new LogTable(LogTable.LogType_Error,"MovesSubTableFragment",e.getMessage(),"LOGUSER",null,null));
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
            db.logDao().addLog(new LogTable(LogTable.LogType_Error,"MovesSubTableFragment",e.getMessage(),"LOGUSER",null,null));
            e.printStackTrace();
        }
    }

    public void saveDataAllInsert(){
        KeyboardUtils.hideKeyboard(getActivity());
        try{
            List<String[]> data = AllLinesData.getAllDataList();
            if(data.size()>0) {
                    SaveDataThread_All_INSERT sds = new SaveDataThread_All_INSERT(getContext(), data, tranCode);
                    sds.run();
            }
        }catch(Exception e){
            db.logDao().addLog(new LogTable(LogTable.LogType_Error,"MovesSubTableFragment",e.getMessage(),"LOGUSER",null,null));
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

    private void closeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Biztos, hogy lezárja a feladatot?");
        builder.setMessage("Lezárás után a feladat eltűnik a feladatlistából és áttöltődik a rendszerbe!");
        builder.setNegativeButton("mégsem", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                KeyboardUtils.hideKeyboard(getActivity());
                dialog.cancel();
            }
        });
        builder.setPositiveButton("rendben", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                KeyboardUtils.hideKeyboard(getActivity());
                if( tranCode.charAt(0)=='3' ){
                    closeLine();
                }else {
                    if (NotCloseList.getSizeOfChecked() > 0) {
                        notCloseDialog();
                    } else {
                        closeLine();
                    }
                }
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void notCloseDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Biztos, hogy lezárja a feladatot?");
        builder.setMessage("Vannak tételek amik nem lesznek lezárva!\n\nErősítse meg a műveletet a műszakvezető kódjával:");
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_close_checker, null);
        final WHEditBox whEditBox = rootView.findViewById(R.id.dialog_close_checker);
        final TextView succText = rootView.findViewById(R.id.dialog_close_text);
        succText.setVisibility(View.GONE);

        whEditBox.setOnDetectBarcodeListener(new WHEditBox.OnDetectBarcodeListener() {
            @Override
            public void OnDetectBarcode(String value) {
                if( whEditBox.EDText.getText().toString().equals("muszakvezeto") ) {
                    whEditBox.EDText.setText("");
                    whEditBox.setVisibility(View.GONE);
                    succText.setVisibility(View.VISIBLE);
                    dialog.setMessage("");
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                }else {
                    Toast.makeText(getContext(),"Hibás engedélyező kód!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void OnDetectError(String errorResult, String value) {

            }

            @Override
            public void OnFocusOutListener(String value) {

            }

            @Override
            public void OnFocusInListener(String value) {

            }
        });
        builder.setView(rootView);
        builder.setNegativeButton("mégsem", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                KeyboardUtils.hideKeyboard(getActivity());
                dialog.cancel();
            }
        });
        builder.setPositiveButton("lezárom", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                KeyboardUtils.hideKeyboard(getActivity());
                closeLine();
                dialog.cancel();
            }
        });

        dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
    }

    private void notSaveAll(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Lezárás nem lehetséges!");
        builder.setMessage("Vannak még módosított, de le nem mentett sorok a rendszerben!");
        builder.setNegativeButton("rendben", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                KeyboardUtils.hideKeyboard(getActivity());
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void closeLine(){
        pd = HelperClass.loadingDialogOn(getActivity());
        new UploadFilesThread(getContext(),tranCode,tranID,this).start();
        SessionClass.setParam("currentLineID","");
        KeyboardUtils.hideKeyboard(getActivity());
    }

    private void closeWithoutSave(){
        closeMovesDialog(this);
    }

    public void closeFragment(){
        KeyboardUtils.hideKeyboard(getActivity());
        db.sessionTempDao().deleteAllData();
        if( pd!=null ){
            pd.dismiss();
        }
        if(SessionClass.getParam("collectionBtn").equals("1") ){
            ((MovesSubViewPager)getParentFragment()).closeFragment();
        }else{
            getFragmentManager().popBackStack();
        }
    }
    public void errorClose() {
        KeyboardUtils.hideKeyboard(getActivity());
        if( pd!=null ){
            pd.dismiss();
        }
    }

    void placeDialog(final String id) {
        if ( AllLinesData.getParam(id)[qTo_Place] != null && ( AllLinesData.getParam(id)[qTo_Place].equals("") || AllLinesData.getParam(id)[qTo_Place].isEmpty()) ) {
            KeyboardUtils.showKeyboard(getActivity());
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            View v = getLayoutInflater().inflate(R.layout.dialog_place_number, null);
            final EditText ed1 = v.findViewById(R.id.dialog_place_item);
            builder.setView(v);
            ed1.setOnKeyListener(new View.OnKeyListener() {
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (event.getAction() == KeyEvent.ACTION_DOWN) {
                        switch (keyCode) {
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
            SessionClass.setParam("currentPlace", AllLinesData.getParam(id)[qTo_Place]);
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
                if (tranCode.charAt(0) == '3') mode = "CrossDock";
                if (tranCode.charAt(0) == '4') mode = "CrossDock";
                if (tranCode.charAt(0) == '5') mode = "Szedés";
                db.sessionTempDao().deleteAllData();
                SessionClass.setParam("currentLineID", "");
                KeyboardUtils.hideKeyboard(getActivity());
                if( tranCode.charAt(0) == '3' || tranCode.charAt(0) == '4' ) {
                    getActivity().getSupportFragmentManager().popBackStack();
                    new ChangeStatusThread(getContext(), "XD_PDA", tranCode, tranID, frg).start();
                }else {
                    new ChangeStatusThread(getContext(), "PDA", tranCode, tranID, frg).start();
                }
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
                        KeyboardUtils.hideKeyboardFrom(getContext(),rootView);
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

    void createLineDialog_old() {
        try {
            SessionClass.setParam("currentLineID", tablePanel.getAdapter().getCheckedPosition(0).get(0));
        }catch (Exception e){
            SessionClass.setParam("currentLineID", "");
        }
        if( !SessionClass.getParam("currentLineID").equals("") ) {
            if ( !InsertedList.isInsert(SessionClass.getParam("currentLineID")) ) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Tételbontás");
                builder.setMessage("Kérem adja meg a bontandó darabszámot:");
                View dialogRootView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_numberpicker, null, false);
                final EditText num = dialogRootView.findViewById(R.id.numberpicker_number);
                Button add = dialogRootView.findViewById(R.id.numberpicker_number_add);
                Button minus = dialogRootView.findViewById(R.id.numberpicker_number_minus);
                num.setText("0");
                add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            if( (Integer.valueOf( AllLinesData.getParam(SessionClass.getParam("currentLineID"))[qNeed]) - 1 ) > Integer.valueOf(num.getText().toString()) ) {
                                num.setText("" + (Integer.valueOf(num.getText().toString()) + 1));
                            }
                        } catch (Exception e) {
                            num.setText("0");
                        }
                    }
                });
                minus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            if( 0 < Integer.valueOf(num.getText().toString()) ) {
                                num.setText("" + (Integer.valueOf(num.getText().toString()) - 1));
                            }
                        } catch (Exception e) {
                            num.setText("0");
                        }
                    }
                });
                builder.setView(dialogRootView);
                builder.setPositiveButton("Felbontom", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //----------------------------------------------------------- Normal worehouse break function ------------------------------------------------------------------

                        if( !num.getText().toString().equals("0") ) {
                            if ((Integer.valueOf(AllLinesData.getParam(SessionClass.getParam("currentLineID"))[qNeed]) - 1) >= Integer.valueOf(num.getText().toString())) {
                                int qTo_Place = HelperClass.getArrayPosition("To_Place", SessionClass.getParam(tranCode + "_Line_ListView_SELECT"));


                                String[] oldRow = AllLinesData.getParam(SessionClass.getParam("currentLineID"));
                                int pos = AllLinesData.getRowCount(SessionClass.getParam("currentLineID"));
                                String[] newRow = new String[oldRow.length];

                                for (int i = 0; i < oldRow.length; i++) {
                                    newRow[i] = oldRow[i];
                                }
                                String newId = "";
                                if (newId.equals("")) {
                                    Random r = new Random();
                                    newId = String.valueOf(-1 * Long.parseLong(oldRow[0]) + 1000000000 + (r.nextInt(900000 - 100000) + 100000));
                                }

                                newRow[0] = newId;
                                newRow[qNeed] = num.getText().toString();
                                newRow[qCurrent] = "0";
                                newRow[qMissing] = "0";
                                if (qBarcode > -1) newRow[qBarcode] = "";
                                //if (qTo_Place > -1) newRow[qTo_Place] = "";
                                newRow[qRefLineId] = oldRow[qLineID];
                                oldRow[qNeed] = String.valueOf(Integer.valueOf(oldRow[qNeed]) - Integer.valueOf(num.getText().toString()));
                                if (Integer.valueOf(oldRow[qNeed]) < Integer.valueOf(oldRow[qCurrent])) {
                                    oldRow[qCurrent] = oldRow[qNeed];
                                }
                                oldRow[qMissing] = String.valueOf(Integer.valueOf(oldRow[qNeed]) - Integer.valueOf(oldRow[qCurrent]));
                                List<SessionTemp> st = db.sessionTempDao().getAllData();
                                AllLinesData.insertParam(AllLinesData.getRowCount(oldRow[0]) + 1, newRow[0], newRow);
                                if (tranCode.charAt(0) == '3') {
                                    rowTempData = AllLinesData.getGroupByParam(qEvidNum, qNeed, qCurrent, qMissing);
                                } else {
                                    rowTempData = AllLinesData.getAllDataList();
                                }

                                tablePanel = new TablePanel(getContext(), rootView, R.id.moves_mainSubLayout, headerText, rowTempData, headerWidth);
                                createTablePanel();
                                if (tranCode.charAt(0) != '3') {
                                    tablePanel.getAdapter().setCheckedArray(AllLinesData.findPosition(SessionClass.getParam("currentLineID")));
                                    tablePanel.smoothScrollToPosition(pos);
                                }
                                InsertedList.setInsertElement(newId, "0");
                                CheckedList.setParamItem(oldRow[0], 1);
                                NotCloseList.setParamItem(newId, 0);
                                SaveAllSessionTemp sst = new SaveAllSessionTemp(getContext());
                                sst.start();
                            } else {
                                Toast.makeText(getContext(), "Túl nagy a bontási mennyiség!", Toast.LENGTH_LONG).show();
                            }
                        }else{
                            Toast.makeText(getContext(), "Bontási mennyiség nem lehet nulla!", Toast.LENGTH_LONG).show();
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

                builder.setCancelable(false);
                builder.show();
            } else {
                HelperClass.messageBox(getActivity(),"Feladatkezelés","Bontott tételt tovább nem bonthat!",HelperClass.ERROR);
                //Toast.makeText(getContext(), "Bontott tételt tovább nem bonthat!", Toast.LENGTH_LONG).show();
            }
        }else{
            HelperClass.messageBox(getActivity(),"Feladatkezelés","Nincs kiválasztva feladat sor!",HelperClass.ERROR);
            //Toast.makeText(getContext(), "Nincs kiválasztva feladat sor!", Toast.LENGTH_LONG).show();
        }
    }

    private void showSelectItemDialog(final String barcode, final List<String[]> data, int placePos){
        dialogSelectItem = "";
        builderSelect = new AlertDialog.Builder(getActivity());
        builderSelect.setTitle("Tétel kiválasztás");
        builderSelect.setMessage("Ez a vonalkód több különböző tételsort is meghatároz, kérem válassza ki a megfelelőt!");
        View dialogRootView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_select_items, null, false);

        RecyclerView.LayoutManager lm = new LinearLayoutManager(getContext());
        List<XD_Dialog_ItemsParameters> itemsList = new ArrayList<>();
        List<String[]> dataList = data;
        if( dataList != null && dataList.size() > 0 ){
            for(int i=0; i < dataList.size(); i++){
                try {
                    itemsList.add(new XD_Dialog_ItemsParameters(Long.parseLong(dataList.get(i)[0]), Integer.parseInt(dataList.get(i)[qNeed]), Integer.parseInt(dataList.get(i)[qCurrent]), Integer.parseInt(dataList.get(i)[qWeight]), HelperClass.convertStringToFloat(dataList.get(i)[qLength]), HelperClass.convertStringToFloat(dataList.get(i)[qWidth]), HelperClass.convertStringToFloat(dataList.get(i)[qHeight]), dataList.get(i)[placePos]));
                }catch (Exception e){
                    e.printStackTrace();
                    HelperClass.messageBox(getActivity(),"Adatmegadás","Hibás érték formátum " , HelperClass.ERROR);
                }
            }
        }

        XD_Dialog_ItemsParametersListAdapter XD_listAdapter = new XD_Dialog_ItemsParametersListAdapter(getContext(),itemsList,3);
        XD_listAdapter.setEvidNum( "" );
        XD_listAdapter.setActivity(getActivity());
        XD_listAdapter.setOnSelectMoreItems(new XD_ItemsParametersListAdapter.OnSelectMoreItems() {
            @Override
            public void onContainerClick(Long id) {
                dialogSelectItem = String.valueOf(id);
            }
        });
        RecyclerView itemsListContainer = dialogRootView.findViewById(R.id.dialog_rv_items_list);
        itemsListContainer.setLayoutManager(lm);
        itemsListContainer.setAdapter(XD_listAdapter);
        SimpleDividerItemDecoration itemDecor = new SimpleDividerItemDecoration(getContext());
        itemsListContainer.addItemDecoration(itemDecor);


        builderSelect.setView(dialogRootView);
        builderSelect.setPositiveButton("Kiválaszt", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                loadScanCount_now(SessionClass.getParam("XD_CHECKED3"));
            }
        });
        builderSelect.setNegativeButton("Mégsem", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                KeyboardUtils.hideKeyboard(getActivity());
                dialog.cancel();
            }
        });

        builderSelect.setCancelable(false);
        builderSelect.show();
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

    void createLineDialog() {
        try {
            SessionClass.setParam("currentLineID", tablePanel.getAdapter().getCheckedPosition(0).get(0));
        }catch (Exception e){
            SessionClass.setParam("currentLineID", "");
        }
        if( !SessionClass.getParam("currentLineID").equals("") ) {
            if ( !InsertedList.isInsert(SessionClass.getParam("currentLineID")) ) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Tételbontás");
                builder.setMessage("Kérem adja meg a bontandó darabszámot:");
                View dialogRootView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_numberpicker, null, false);
                final EditText num = dialogRootView.findViewById(R.id.numberpicker_number);
                Button add = dialogRootView.findViewById(R.id.numberpicker_number_add);
                Button minus = dialogRootView.findViewById(R.id.numberpicker_number_minus);
                num.setText("0");
                add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            if( (Integer.valueOf( AllLinesData.getParam(SessionClass.getParam("currentLineID"))[qNeed]) - 1 ) > Integer.valueOf(num.getText().toString()) ) {
                                num.setText("" + (Integer.valueOf(num.getText().toString()) + 1));
                            }
                        } catch (Exception e) {
                            num.setText("0");
                        }
                    }
                });
                minus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            if( 0 < Integer.valueOf(num.getText().toString()) ) {
                                num.setText("" + (Integer.valueOf(num.getText().toString()) - 1));
                            }
                        } catch (Exception e) {
                            num.setText("0");
                        }
                    }
                });
                builder.setView(dialogRootView);
                builder.setPositiveButton("Felbontom", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        RequestQueue rq = MySingleton.getInstance(getContext()).getRequestQueue();
                        String url = SessionClass.getParam("WSUrl") + "/WRHS_PDA_XD_CD_SPLIT";
                        HashMap<String,String> map = new HashMap<>();
                        map.put("Terminal",SessionClass.getParam("terminal"));
                        map.put("WRHS_L_ID", SessionClass.getParam("currentLineID"));
                        map.put("PCS",num.getText().toString());
                        HelperClass.printHashMap(map);
                        Log.i("URL",url);
                        JsonRequest<JSONObject> jr = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(map), new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    String rootText = response.getString("WRHS_PDA_XD_CD_SPLITResult");
                                    Log.i("TAG",rootText);
                                    JSONObject jsonObject = new JSONObject(rootText);
                                    String rtext = jsonObject.getString("ERROR_CODE");
                                    if (!rtext.isEmpty()) {
                                        if (rtext.equals("0")) {
                                            String newID = jsonObject.getString("NEWID");
                                            String newLineID = jsonObject.getString("NEWLINEID");
                                            int pos = AllLinesData.getRowCount(SessionClass.getParam("currentLineID"));
                                            String[] baseLine = AllLinesData.getParam( SessionClass.getParam("currentLineID") );
                                            String[] newLine = new String[baseLine.length];
                                            for(int i = 0; i < baseLine.length; i++){
                                                newLine[i] = baseLine[i];
                                            }
                                            newLine[0] = newID;
                                            newLine[qLineID] = newLineID;
                                            newLine[qNeed] = num.getText().toString();
                                            //if( !baseLine[qCurrent].equals("0")){ newLine[qCurrent] = num.getText().toString(); } else { newLine[qCurrent] = "0"; }
                                            //newLine[qBarcode] = newID;
                                            newLine[qCurrent] = "0";
                                            Float f = Float.parseFloat( newLine[qNeed] ) / ( Float.parseFloat(baseLine[qNeed]) + Float.parseFloat(baseLine[qMissing]) );
                                            if( Integer.parseInt(baseLine[qCurrent]) > Integer.parseInt(baseLine[qNeed]) ){
                                                baseLine[qCurrent] = baseLine[qNeed];
                                            }
                                            baseLine[qNeed] = String.valueOf( Integer.parseInt(baseLine[qNeed]) - Integer.parseInt(num.getText().toString()) );
                                            baseLine[qWeight] = String.valueOf( Math.round( Float.parseFloat(baseLine[qWeight]) * (1-f) ));
                                            newLine[qWeight] = String.valueOf( Math.round( Float.parseFloat(newLine[qWeight]) * f) );
                                            AllLinesData.setParam(newID, newLine);
                                            if (tranCode.charAt(0) == '3') {
                                                rowTempData = AllLinesData.getGroupByParam(qEvidNum, qNeed, qCurrent, qMissing);
                                            } else {
                                                rowTempData = AllLinesData.getAllDataList();
                                            }
                                            baseLine[qMissing] = String.valueOf( Integer.parseInt(baseLine[qCurrent]) - Integer.parseInt(baseLine[qNeed]) );
                                            newLine[qMissing] = String.valueOf( Integer.parseInt(newLine[qCurrent]) - Integer.parseInt(newLine[qNeed]) );
                                            tablePanel = new TablePanel(getContext(), rootView, R.id.moves_mainSubLayout, headerText, rowTempData, headerWidth);
                                            createTablePanel();
                                            if (tranCode.charAt(0) != '3') {
                                                tablePanel.getAdapter().setCheckedArray(AllLinesData.findPosition(SessionClass.getParam("currentLineID")));
                                                tablePanel.smoothScrollToPosition(pos);
                                            }

                                            new SaveAllSessionTemp(getContext()).start();
                                        }else{
                                            Toast.makeText(getContext(),jsonObject.getString("ERROR_TEXT"),Toast.LENGTH_LONG).show();
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                if(error!=null){
                                    Toast.makeText(getContext(),"Adatok áttöltése sikertelen, hálózati hiba!",Toast.LENGTH_LONG).show();
                                    error.printStackTrace();
                                }
                            }
                        });
                        jr.setRetryPolicy(new DefaultRetryPolicy(50000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                        rq.add(jr);
                    }
                });
                builder.setNegativeButton("Mégsem", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        KeyboardUtils.hideKeyboard(getActivity());
                        dialog.cancel();
                    }
                });

                builder.setCancelable(false);
                builder.show();
            } else {
                HelperClass.messageBox(getActivity(),"Feladatkezelés","Bontott tételt tovább nem bonthat!",HelperClass.ERROR);
                //Toast.makeText(getContext(), "Bontott tételt tovább nem bonthat!", Toast.LENGTH_LONG).show();
            }
        }else{
            HelperClass.messageBox(getActivity(),"Feladatkezelés","Nincs kiválasztva feladat sor!",HelperClass.ERROR);
            //Toast.makeText(getContext(), "Nincs kiválasztva feladat sor!", Toast.LENGTH_LONG).show();
        }

    }

    private void saveImageDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Állapot mentés");
        TextView tv1 = new TextView(getContext());
        tv1.setPadding(HelperClass.dpToPx(getContext(),20),HelperClass.dpToPx(getContext(),20),0,0);
        tv1.setText("Szeretné az adott állapotot feltölteni a szerverre hibakeresés céljából?");
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
                saveImageData();
            }
        });
        builder.show();
    }

    private void saveImageData(){

        JSONObject jsonObject= new JSONObject();

        pd = HelperClass.loadingDialogOn(getActivity());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault());
        String currentDateandTime = sdf.format(new Date());
        Map<String, String> headMap = new HashMap<>();
        headMap.put("tran_code",""+tranCode);
        headMap.put("move_num",""+movenum);
        headMap.put("action_date",currentDateandTime);
        headMap.put("terminal",SessionClass.getParam("terminal"));
        headMap.put("userid",SessionClass.getParam("userid"));
        JSONObject jsonRoot = new JSONObject(headMap);

        List<SessionTemp> tempList = db.sessionTempDao().getAllData();
        JSONArray jsonArray = new JSONArray();
        for(int i = 0; i < tempList.size(); i++){
            try {
                JSONObject jobj = new JSONObject();
                jobj.put("id", tempList.get(i).getId());
                jobj.put("num", tempList.get(i).getNum());
                jobj.put("param0", tempList.get(i).getParam0()   == null ? JSONObject.NULL : tempList.get(i).getParam0());
                jobj.put("param1", tempList.get(i).getParam1()   == null ? JSONObject.NULL : tempList.get(i).getParam1());
                jobj.put("param2", tempList.get(i).getParam2()   == null ? JSONObject.NULL : tempList.get(i).getParam2());
                jobj.put("param3", tempList.get(i).getParam3()   == null ? JSONObject.NULL : tempList.get(i).getParam3());
                jobj.put("param4", tempList.get(i).getParam4()   == null ? JSONObject.NULL : tempList.get(i).getParam4());
                jobj.put("param5", tempList.get(i).getParam5()   == null ? JSONObject.NULL : tempList.get(i).getParam5());
                jobj.put("param6", tempList.get(i).getParam6()   == null ? JSONObject.NULL : tempList.get(i).getParam6());
                jobj.put("param7", tempList.get(i).getParam7()   == null ? JSONObject.NULL : tempList.get(i).getParam7());
                jobj.put("param8", tempList.get(i).getParam8()   == null ? JSONObject.NULL : tempList.get(i).getParam8());
                jobj.put("param9", tempList.get(i).getParam9()   == null ? JSONObject.NULL : tempList.get(i).getParam9());
                jobj.put("param10", tempList.get(i).getParam10() == null ? JSONObject.NULL : tempList.get(i).getParam10());
                jobj.put("param11", tempList.get(i).getParam11() == null ? JSONObject.NULL : tempList.get(i).getParam11());
                jobj.put("param12", tempList.get(i).getParam12() == null ? JSONObject.NULL : tempList.get(i).getParam12());
                jobj.put("param13", tempList.get(i).getParam13() == null ? JSONObject.NULL : tempList.get(i).getParam13());
                jobj.put("param14", tempList.get(i).getParam14() == null ? JSONObject.NULL : tempList.get(i).getParam14());
                jobj.put("param15", tempList.get(i).getParam15() == null ? JSONObject.NULL : tempList.get(i).getParam15());
                jobj.put("param16", tempList.get(i).getParam16() == null ? JSONObject.NULL : tempList.get(i).getParam16());
                jobj.put("param17", tempList.get(i).getParam17() == null ? JSONObject.NULL : tempList.get(i).getParam17());
                jobj.put("param18", tempList.get(i).getParam18() == null ? JSONObject.NULL : tempList.get(i).getParam18());
                jobj.put("param19", tempList.get(i).getParam19() == null ? JSONObject.NULL : tempList.get(i).getParam19());
                jobj.put("param20", tempList.get(i).getParam20() == null ? JSONObject.NULL : tempList.get(i).getParam20());
                jobj.put("param21", tempList.get(i).getParam21() == null ? JSONObject.NULL : tempList.get(i).getParam21());
                jobj.put("param22", tempList.get(i).getParam22() == null ? JSONObject.NULL : tempList.get(i).getParam22());
                jobj.put("param23", tempList.get(i).getParam23() == null ? JSONObject.NULL : tempList.get(i).getParam23());
                jobj.put("param24", tempList.get(i).getParam24() == null ? JSONObject.NULL : tempList.get(i).getParam24());
                jobj.put("param25", tempList.get(i).getParam25() == null ? JSONObject.NULL : tempList.get(i).getParam25());
                jobj.put("param26", tempList.get(i).getParam26() == null ? JSONObject.NULL : tempList.get(i).getParam26());
                jobj.put("param27", tempList.get(i).getParam27() == null ? JSONObject.NULL : tempList.get(i).getParam27());
                jobj.put("param28", tempList.get(i).getParam28() == null ? JSONObject.NULL : tempList.get(i).getParam28());
                jobj.put("param29", tempList.get(i).getParam29() == null ? JSONObject.NULL : tempList.get(i).getParam29());
                jobj.put("param30", tempList.get(i).getParam30() == null ? JSONObject.NULL : tempList.get(i).getParam30());
                jobj.put("param31", tempList.get(i).getParam31() == null ? JSONObject.NULL : tempList.get(i).getParam31());
                jobj.put("param32", tempList.get(i).getParam32() == null ? JSONObject.NULL : tempList.get(i).getParam32());
                jobj.put("param33", tempList.get(i).getParam33() == null ? JSONObject.NULL : tempList.get(i).getParam33());
                jobj.put("param34", tempList.get(i).getParam34() == null ? JSONObject.NULL : tempList.get(i).getParam34());
                jobj.put("param35", tempList.get(i).getParam35() == null ? JSONObject.NULL : tempList.get(i).getParam35());
                jobj.put("param36", tempList.get(i).getParam36() == null ? JSONObject.NULL : tempList.get(i).getParam36());
                jobj.put("param37", tempList.get(i).getParam37() == null ? JSONObject.NULL : tempList.get(i).getParam37());
                jobj.put("param38", tempList.get(i).getParam38() == null ? JSONObject.NULL : tempList.get(i).getParam38());
                jobj.put("param39", tempList.get(i).getParam39() == null ? JSONObject.NULL : tempList.get(i).getParam39());
                jobj.put("STATUS", tempList.get(i).getStatus());
                jobj.put("insertRow", tempList.get(i).isInsertRow());
                jobj.put("notClose", tempList.get(i).getNotClose());
                jsonArray.put(jobj);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        if(jsonArray != null){
            try {
                jsonRoot.put("data", jsonArray);
            }catch (Exception e){
                e.printStackTrace();
            }

        }
        Log.i("TAG",jsonRoot.toString());

        HashMap<String,String> map = new HashMap<>();
        String text = jsonRoot.toString();
        String base64 = "";
        try {
            byte[] data = text.getBytes("UTF-8");
            base64 = Base64.encodeToString(data, Base64.DEFAULT);
        }catch (Exception e ){
            e.printStackTrace();
        }
        map.put("Data", base64 );
        // 0 - create new table
        // 1 - insert row in table
        map.put("NEW_DATA", "1" );

        if(HelperClass.isOnline(getContext())) {
            RequestQueue rq = MySingleton.getInstance(getContext()).getRequestQueue();
            String url = SessionClass.getParam("WSUrl") + "/WRHS_PDA_DATA_LOG_Upload";
            //url = "https://web-restapi-services-01.conveyor.cloud/service1.svc/WRHS_PDA_DATA_LOG_Upload";
            Log.i("URL", url);
            Log.i("TAG", new JSONObject(map).toString() );
            JsonRequest<JSONObject> jr = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(map), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.i("TAG",response.toString());
                    pd.dismiss();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (error != null) {
                        Toast.makeText(getContext(), "Adatok áttöltése sikertelen, hálózati hiba!", Toast.LENGTH_LONG).show();
                        error.printStackTrace();
                        pd.dismiss();
                    }
                }
            });
            jr.setRetryPolicy(new DefaultRetryPolicy(50000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            rq.add(jr);
        }
    }
}