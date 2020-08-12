package hu.selester.android.webstockandroid.Fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import hu.selester.android.webstockandroid.Adapters.XD_Dialog_ItemsParametersListAdapter;
import hu.selester.android.webstockandroid.Adapters.XD_ItemsParametersListAdapter;
import hu.selester.android.webstockandroid.Database.SelesterDatabase;
import hu.selester.android.webstockandroid.Database.Tables.LogTable;

import hu.selester.android.webstockandroid.Dialogs.MessageDialog;
import hu.selester.android.webstockandroid.Divider.SimpleDividerItemDecoration;
import hu.selester.android.webstockandroid.Helper.HelperClass;
import hu.selester.android.webstockandroid.Helper.KeyboardUtils;
import hu.selester.android.webstockandroid.Objects.ActiveFragment;
import hu.selester.android.webstockandroid.Objects.AllLinesData;
import hu.selester.android.webstockandroid.Objects.CheckedList;
import hu.selester.android.webstockandroid.Objects.CustomTextWatcher;

import hu.selester.android.webstockandroid.Objects.NotCloseList;
import hu.selester.android.webstockandroid.Objects.SessionClass;
import hu.selester.android.webstockandroid.Objects.XD_Dialog_ItemsParameters;
import hu.selester.android.webstockandroid.R;
import hu.selester.android.webstockandroid.Threads.SaveCheckedDataThread;
import hu.selester.android.webstockandroid.Threads.SaveIdSessionTemp;
import mobil.selester.wheditbox.WHEditBox;

public class ScanCountFragment extends Fragment implements View.OnClickListener{

    private EditText counter, findValue;
    private SelesterDatabase db;
    private String isBar;
    private int findRow;
    private String lineID;
    private Dialog popup = null;
    private EditText textDataValue1;
    private TextView textDataValue2,textDataValue3,getTextDataValue3;
    private LinearLayout collectorBtn, collectorLabel;
    private MessageDialog message;
    private SwitchCompat notCloseSwitch;
    private Bundle si;
    private View rootView;
    private ArrayList<String> inBar;
    private String tranCode, Head_ID;
    private int selectedComboVal;
    private int qNeed,qCurrent,qMissing,qBarcode01,qBarcode02,qTo_Place, qWeight, qWidth, qHeight, qLength, qToPlace, qRefLineId, qBarcode;
    private EditText textDataValue;
    private String[] arrayTempInt, arrayTempNames, arrayTempType, arrayTextBoxLabels, arrayTextBoxEnableds, arrayTextBoxIndexes, arrayTextBoxSelect, arrayLabelTextBox, arrayLabelIndex, arrayLabelSelect, arrayTextBoxTypes, arrayComboSelect;;
    private TextView headerText, findTvLabel;
    private LinearLayout subHeadText1, subHeadText2, subHeadText3;
    private boolean newOpenWindow;
    private boolean isnew;
    private int def;
    private boolean dialogActive = false;
    private int dataCounter;
    private WHEditBox[] whEditBoxes;
    private boolean trimmed;
    private CustomTextWatcher[] customTextWatcherArray = new CustomTextWatcher[8];
    private AlertDialog.Builder builderSelect;
    private String dialogSelectItem;
    private List<String[]> dataDialog;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        trimmed = true;
        ActiveFragment.setFragment(this);
        lineID = getArguments().getString("lineID");
        tranCode = getArguments().getString("tranCode");
        WHEditBox.activity = getActivity();
        WHEditBox.suffix = SessionClass.getParam("barcodeSuffix");
        selectedComboVal = 0;
        qWeight     = HelperClass.getArrayPosition("Weight",        SessionClass.getParam(tranCode + "_Line_ListView_SELECT"));
        qWidth      = HelperClass.getArrayPosition("Size_Width",    SessionClass.getParam(tranCode + "_Line_ListView_SELECT"));
        qHeight     = HelperClass.getArrayPosition("Size_Height",   SessionClass.getParam(tranCode + "_Line_ListView_SELECT"));
        qLength     = HelperClass.getArrayPosition("Size_Length",   SessionClass.getParam(tranCode + "_Line_ListView_SELECT"));
        qToPlace    = HelperClass.getArrayPosition("To_Place",      SessionClass.getParam(tranCode + "_Line_ListView_SELECT"));
        qRefLineId  = HelperClass.getArrayPosition("Ref_Line_ID",   SessionClass.getParam(tranCode + "_Line_ListView_SELECT"));

        qBarcode01 = HelperClass.getArrayPosition("Barcode01", SessionClass.getParam(tranCode + "_Line_ListView_SELECT"));
        qBarcode02 = HelperClass.getArrayPosition("Barcode02", SessionClass.getParam(tranCode + "_Line_ListView_SELECT"));
        qBarcode    = HelperClass.getArrayPosition("Barcode", SessionClass.getParam(tranCode + "_Line_ListView_SELECT"));
        qTo_Place  = HelperClass.getArrayPosition("To_Place", SessionClass.getParam(tranCode + "_Line_ListView_SELECT"));
        CheckedList.setParamItem(lineID,1);
        dataCounter = 0;
        si = savedInstanceState;
        rootView = inflater.inflate(R.layout.frg_scancount,container,false);

        arrayTextBoxLabels      = SessionClass.getParam(tranCode + "_Detail_TextBox_Names").split(",");
        arrayTextBoxEnableds    = SessionClass.getParam(tranCode + "_Detail_TextBox_Enabled").split(",");
        arrayTextBoxIndexes     = SessionClass.getParam(tranCode + "_Detail_TextBox_Index").split(",");
        arrayTextBoxSelect      = SessionClass.getParam(tranCode + "_Detail_TextBox_SELECT").split(",");
        arrayTextBoxTypes       = SessionClass.getParam(tranCode + "_Detail_TextBox_DataType").split(",");

        arrayLabelTextBox   = SessionClass.getParam(tranCode + "_Detail_Label_Info_Names").split(",");
        arrayLabelIndex     = SessionClass.getParam(tranCode + "_Detail_Label_Info_Index").split(",");
        arrayLabelSelect    = SessionClass.getParam(tranCode + "_Detail_Label_Info_SELECT").split(",");
        if( SessionClass.getParam(tranCode + "_Detail_Combo_Values") != null ) {arrayComboSelect = SessionClass.getParam(tranCode + "_Detail_Combo_Values").split("\\|"); }else{ arrayComboSelect = new String[]{"1","1"}; }

        newOpenWindow = true;
        try {
            counter = rootView.findViewById(R.id.scancount_count);
            counter.setOnFocusChangeListener(HelperClass.getFocusBorderListener(getContext()));
            counter.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.toString().isEmpty() || s.toString().charAt(0) == '0') {
//                        counter.setText("1");
                    }
                }
            });
            db = SelesterDatabase.getDatabase(getContext());
            isnew = false;
            KeyboardUtils.hideKeyboard(getActivity());
            try {
                if (SessionClass.getParam(tranCode + "_Detail_TextBox_Needed_Qty_Index").equals("")) {
                    qNeed = 0;
                } else {
                    qNeed = Integer.parseInt(SessionClass.getParam(tranCode + "_Detail_TextBox_Needed_Qty_Index"));
                }
            } catch (Exception e) {
                qNeed = 0;
            }
            try {
                if (SessionClass.getParam(tranCode + "_Detail_TextBox_Current_Qty_Index").equals("")) {
                    qCurrent = 0;
                } else {
                    qCurrent = Integer.parseInt(SessionClass.getParam(tranCode + "_Detail_TextBox_Current_Qty_Index"));
                }
            } catch (Exception e) {
                qCurrent = 0;
            }
            collectorBtn = rootView.findViewById(R.id.scancount_collectorBtn);
            collectorLabel = rootView.findViewById(R.id.scancount_collectorLabel);
            collectorBtn.setVisibility(View.GONE);
            collectorLabel.setVisibility(View.GONE);
            headerText = rootView.findViewById(R.id.scancount_headertext);
            headerText.setBackgroundResource(R.color.headerColor);
            subHeadText1 = rootView.findViewById(R.id.scancount_subHeadText1);
            subHeadText1.setBackgroundResource(R.color.headerColor);
            subHeadText2 = rootView.findViewById(R.id.scancount_subHeadText2);
            subHeadText2.setBackgroundResource(R.color.headerColor);
            subHeadText3 = rootView.findViewById(R.id.scancount_subHeadText3);
            subHeadText3.setBackgroundResource(R.color.headerColor);
            findTvLabel = rootView.findViewById(R.id.scancount_header_label);
            findTvLabel.setText(SessionClass.getParam(tranCode+"_Detail_TextBox_Find_Text") + ":" );
            String[] temp = SessionClass.getParam(tranCode + "_Detail_TextBox_Index").split(",", -1);
            if (SessionClass.getParam(tranCode + "_Detail_TextBox_Index").equals("")) {
                qMissing = 0;
            } else {
                if (temp[0].equals("")) {
                    qMissing = 0;
                } else {
                    qMissing = Integer.parseInt(temp[0]);
                }
            }
            notCloseSwitch = rootView.findViewById(R.id.scancount_notclose_switch);
            notCloseSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        Log.i("NCL",lineID + "0");
                        NotCloseList.setParamItem(lineID,0);
                    }else{
                        Log.i("NCL",lineID + "1");
                        NotCloseList.setParamItem(lineID,1);
                    }
                }
            });
            findRow = Integer.parseInt(SessionClass.getParam(tranCode + "_Detail_TextBox_Find_Index"));
            findValue = rootView.findViewById(R.id.scancount_header_value);
            arrayTempNames = SessionClass.getParam(tranCode + "_Detail_TextBox_Names").split(",");
            arrayTempType = SessionClass.getParam(tranCode + "_Detail_TextBox_DataType").split(",");
            arrayTempInt = SessionClass.getParam(tranCode + "_Detail_TextBox_Index").split(",");
            collectorBtn.setVisibility(View.GONE);
            findValue.setOnFocusChangeListener(HelperClass.getFocusBorderListener(getContext()));
            /*
            if (tranCode.charAt(0) == '1') {
                TextView textDataLabel = rootView.findViewById(R.id.scancount_data_label);
                textDataValue = rootView.findViewById(R.id.scancount_data_value);
                if (AllLinesData.getParam(lineID)[Integer.parseInt(arrayTempInt[0])].equals("")) {
                    textDataValue.setText(SessionClass.getParam("currentPlace"));
                    AllLinesData.setItemParams(lineID, Integer.parseInt(arrayTempInt[0]), SessionClass.getParam("currentPlace"));
                } else {
                    textDataValue.setText(AllLinesData.getParam(lineID)[Integer.parseInt(arrayTempInt[0])]);
                }
                if (arrayTempType[0].equals("int") || arrayTempType[0].equals("Int")) {
                    textDataValue.setInputType(InputType.TYPE_CLASS_NUMBER);
                } else {
                    textDataValue.setInputType(InputType.TYPE_CLASS_TEXT);
                }
                textDataLabel.setText(arrayTempNames[0]);
                textDataValue.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        int suffixLen = SessionClass.getParam("barcodeSuffix").length();
                        AllLinesData.setItemParams(lineID, Integer.parseInt(arrayTempInt[0]), s.toString());
                        if (s.length() > 3) {
                            if (s.toString().substring(s.length() - suffixLen, s.length()).equals(SessionClass.getParam("barcodeSuffix"))) {
                                KeyboardUtils.hideKeyboard(getActivity());
                                SessionClass.setParam("currentPlace", s.toString().substring(0, s.toString().length() - suffixLen));
                                textDataValue.removeTextChangedListener(textWatcher);
                                textDataValue.setText(s.toString().substring(0, s.toString().length() - suffixLen));
                                textDataValue.addTextChangedListener(textWatcher);
                                AllLinesData.setItemParams(lineID, Integer.parseInt(arrayTempInt[0]), s.toString().substring(0, s.toString().length() - suffixLen));
                                findValue.requestFocus();
                            }
                        }
                        refreshPlaceCounter();
                    }
                }):
            }*/
            findValue.requestFocus();
            TextView textLabel1 = rootView.findViewById(R.id.scancount_textLabel1);
            TextView textLabel2 = rootView.findViewById(R.id.scancount_textLabel2);
            TextView textLabel3 = rootView.findViewById(R.id.scancount_textLabel3);
            ImageView takePhotoBtn = rootView.findViewById(R.id.scancount_photo);
            LinearLayout marBtn = rootView.findViewById(R.id.scancount_mar);

            if( SessionClass.getParam("takePhotoBtn").equals("1") ){
                takePhotoBtn.setVisibility(View.VISIBLE);
                takePhotoBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SessionClass.setParam("selectLineID",lineID);
                        getActivity().getSupportFragmentManager().beginTransaction().add(R.id.fragments, new PhotoFragment()).addToBackStack("app").commit();
                    }
                });
            } else { takePhotoBtn.setVisibility(View.GONE); }
            if( SessionClass.getParam("marBtn").equals("1") ){ marBtn.setVisibility(View.VISIBLE); } else { marBtn.setVisibility(View.GONE); }


            getTextDataValue3 = rootView.findViewById(R.id.scancount_data3_value);
            String[] labelArray = (SessionClass.getParam(tranCode + "_Detail_Label_Info_Names")).split(",", -1);
            textLabel1.setText(labelArray[0]);
            textLabel2.setText(labelArray[1]);
            textLabel3.setText(labelArray[2]);
            refreshData(lineID);
            Button barcodeBtn = rootView.findViewById(R.id.scancount_header_btn);
            barcodeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkListElementsManual();
                }
            });
            Button removeBtn = rootView.findViewById(R.id.scancount_removeBtn);
            removeBtn.setOnClickListener(this);
            Button addBtn = rootView.findViewById(R.id.scancount_addBtn);
            addBtn.setOnClickListener(this);

            findValue.addTextChangedListener(textWatcher);
            ImageView goBackBtn = rootView.findViewById(R.id.scancount_exit);
            goBackBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(textDataValue1.getText().toString().equals("0")){
                        AllLinesData.setItemParams(lineID, qMissing, "0");
                        if(tranCode.charAt(0)=='1'){
                            AllLinesData.setItemParams(lineID, Integer.parseInt(arrayTempInt[0]), "");
                        }
                    } else {
                        AllLinesData.setItemParams(lineID, qMissing, textDataValue3.getText().toString());
                    }
                    saveDBDatas();
                    CheckedList.setParamItem(lineID,1);
                    KeyboardUtils.hideKeyboard(getActivity());
                    getFragmentManager().popBackStack();
                }
            });
            newOpenWindow = false;
        }catch (Exception e){
            db.logDao().addLog(new LogTable(LogTable.LogType_Error,"ScanCountFragment",e.getMessage(),"LOGUSER",null,null));
            e.printStackTrace();
            HelperClass.messageBox(getActivity(),"Részletek","Ismeretlen hiba (001)!",HelperClass.ERROR);
            //Toast.makeText(getContext(),"ERROR SCANCOUNT 001",Toast.LENGTH_LONG).show();
        }
        KeyboardUtils.hideKeyboard(getActivity());
        //HelperClass.loadTempSession(getContext());
        return rootView;
    }

    private void collectorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View v = getLayoutInflater().inflate(R.layout.dialog_collector,null);
        final EditText eanET = v.findViewById(R.id.dialog_collect_ean);
        eanET.setText("");
        builder.setView(v);
        eanET.setOnKeyListener(new View.OnKeyListener()
        {
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            KeyboardUtils.hideKeyboard(getActivity());
                            getTextDataValue3.setText(eanET.getText());
                            SessionClass.setParam("currentCollect",eanET.getText().toString());
                            AllLinesData.setItemParams(lineID, qBarcode01, eanET.getText().toString());
                            if( eanET.getText().toString().equals("") ) AllLinesData.setItemParams(lineID, qBarcode02, "");
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
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                CheckedList.setParamItem(lineID,1);
                if (s.length() > 3) {
                    if (s.toString().substring(s.length() - SessionClass.getParam("barcodeSuffix").length(), s.length()).equals(SessionClass.getParam("barcodeSuffix"))) {
                        KeyboardUtils.hideKeyboard(getActivity());
                        getTextDataValue3.setText(s.toString().substring(0, s.toString().length() - SessionClass.getParam("barcodeSuffix").length()));
                        SessionClass.setParam("currentCollect",s.toString().substring(0, s.toString().length() - SessionClass.getParam("barcodeSuffix").length()));
                        AllLinesData.setItemParams(lineID, qBarcode01, s.toString().substring(0, s.toString().length() - SessionClass.getParam("barcodeSuffix").length()) );
                        popup.dismiss();
                    }
                }
            }
        });
        builder.setPositiveButton("IGEN", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getTextDataValue3.setText(eanET.getText());
                SessionClass.setParam("currentCollect",eanET.getText().toString());
                AllLinesData.setItemParams(lineID, qBarcode01, eanET.getText().toString());
                if( eanET.getText().toString().equals("") ) AllLinesData.setItemParams(lineID, qBarcode02, "");
                KeyboardUtils.hideKeyboard(getActivity());
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("NEM", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                KeyboardUtils.hideKeyboard(getActivity());
                dialog.dismiss();
            }
        });
        popup = builder.create();
        popup.show();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        try {
            int now = 0;
            int x1; int x2;
            switch (v.getId()) {
                case R.id.scancount_addBtn:
                    if (counter.getText().toString().isEmpty()) {
                        counter.setText("1");
                    }
                    if( counter.getText().charAt(0)!='0') {
                        now = Integer.parseInt(textDataValue1.getText().toString());
                        textDataValue1.setText("" + (now + Integer.parseInt(counter.getText().toString())));
                        x1 = Integer.parseInt(textDataValue1.getText().toString());
                        x2 = Integer.parseInt(textDataValue2.getText().toString());
                        if (x1 > x2) {
                            textDataValue1.setText("" + x2);
                            AllLinesData.setItemParams(lineID, qCurrent, "" + x2);
                            AllLinesData.setItemParams(lineID, qMissing, "" + 0);
                            textDataValue3.setText("" + 0);

                        } else {
                            textDataValue3.setText("" + (x1 - x2));
                            if (qCurrent != 0) {
                                AllLinesData.setItemParams(lineID, qCurrent, "" + x1);
                            }
                            if (qMissing != 0) {
                                AllLinesData.setItemParams(lineID, qMissing, "" + (x1 - x2));
                            }
                        }
                    }
                    break;
                case R.id.scancount_removeBtn:
                    if (!counter.getText().toString().isEmpty()) {
                        if (counter.getText().charAt(0) != '0') {
                            now = Integer.parseInt(textDataValue1.getText().toString());
                            x2 = Integer.parseInt(textDataValue2.getText().toString());

                            textDataValue1.setText("" + (now - Integer.parseInt(counter.getText().toString())));
                            x1 = Integer.parseInt(textDataValue1.getText().toString());

                            if (x1 <= 0) {
                                textDataValue1.setText("0");
                                AllLinesData.setItemParams(lineID, qCurrent, "" + 0);
                                AllLinesData.setItemParams(lineID, qMissing, "" + x1);
                                textDataValue3.setText("0");
                            } else {
                                textDataValue3.setText("" + (x1 - x2));
                                if (qCurrent != 0) {
                                    AllLinesData.setItemParams(lineID, qCurrent, "" + x1);
                                    if (qMissing != 0) {
                                        AllLinesData.setItemParams(lineID, qMissing, "" + (x1 - x2));
                                    }
                                } else {
                                    AllLinesData.setItemParams(lineID, qMissing, "0");
                                }
                            }
                        }
                    }
                    break;
                case R.id.scancount_header_btn:
                    break;
            }
            refreshPlaceCounter();
        }catch (Exception e){
            db.logDao().addLog(new LogTable(LogTable.LogType_Error,"ScanCountFragment",e.getMessage(),"LOGUSER",null,null));
            e.printStackTrace();
            HelperClass.messageBox(getActivity(),"Részletek","Ismeretlen hiba (002)!",HelperClass.ERROR);
            //Toast.makeText(getContext(),"ERROR SCANCOUNT 002",Toast.LENGTH_LONG).show();
        }
    }

    private void checkListElementsManual(){
        try {
            trimmed = false;
            isBar = findValue.getText().toString();
            if (!isBar.isEmpty()) findValue.setText(isBar + SessionClass.getParam("barcodeSuffix"));
            refreshPlaceCounter();
        }catch (Exception e){
            db.logDao().addLog(new LogTable(LogTable.LogType_Error,"ScanCountFragment",e.getMessage(),"LOGUSER",null,null));
            e.printStackTrace();
            HelperClass.messageBox(getActivity(),"Részletek","Ismeretlen hiba (003)!",HelperClass.ERROR);
            //Toast.makeText(getContext(),"ERROR SCANCOUNT 003",Toast.LENGTH_LONG).show();
        }
    }

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(!dialogActive) {
                try {
                    isBar = HelperClass.isBarcode(findValue.getText().toString());
                    if (isBar != null) {
                        if(trimmed) isBar = HelperClass.getTrimmedText(isBar);
                        trimmed = true;
                        List<String[]> resourceDataList = new ArrayList<>();
                        ArrayList<String> findValueList = AllLinesData.findKeyFromMap(isBar, findRow);
                        if (findValueList != null) {
                            for (int i = 0; i < findValueList.size(); i++) {
                                resourceDataList.add(AllLinesData.getParam(findValueList.get(i)));
                            }
                            if (inBar.contains(isBar)) {
                                int itemCount = Integer.parseInt(AllLinesData.getParam(lineID)[qCurrent]);
                                int maxItemCount = Integer.parseInt(AllLinesData.getParam(lineID)[qNeed]);
                                if ((itemCount + Integer.parseInt(counter.getText().toString())) > maxItemCount) {
                                    String id = null;
                                    try {
                                        id = AllLinesData.findKeyFromMap(isBar, findRow).get(0);
                                    }catch(Exception e ){ }
                                    if (id != null) {
                                        String[] barcodes = AllLinesData.getParam(id);
                                        List<String[]> data = AllLinesData.findSameUniqueItemsFromMap(barcodes[qBarcode] , qBarcode, qRefLineId);
                                        if(data.size() > 1){
                                            showSelectItemDialog("", data);
                                        }else if(data.size() == 1){
                                            buildFullDialog(id);
                                        }else if(data.size() == 0){
                                            HelperClass.messageBox(getActivity(),"Feladatkezelés","Az adott tétel már rakhelyen van!\nHa változtatni szeretné kérem törölje a rakhelyről!",HelperClass.WARNING);
                                        }
                                    } else {
                                        HelperClass.messageBox(getActivity(),"Részletek","Nem található több ebből a cikkből!",HelperClass.WARNING);
                                        //buildFullDialog();
                                    }
                                } else {
                                    AllLinesData.setItemParams(lineID, qCurrent, "" + (itemCount + Integer.parseInt(counter.getText().toString())));
                                    if (qMissing != 0) {
                                        AllLinesData.setItemParams(lineID, qMissing, "" + (Integer.parseInt(AllLinesData.getParam(lineID)[qCurrent]) - Integer.parseInt(AllLinesData.getParam(lineID)[qNeed])));
                                    }
                                    textDataValue1.setText(AllLinesData.getParam(lineID)[qCurrent]);
                                    textDataValue2.setText(AllLinesData.getParam(lineID)[qNeed]);
                                    def = Integer.parseInt(AllLinesData.getParam(lineID)[qCurrent]) - Integer.parseInt(AllLinesData.getParam(lineID)[qNeed]);

                                    if(AllLinesData.getParam(lineID)[qCurrent].equals("0")){def = 0;}
                                    textDataValue3.setText("" + def);
                                }
                            } else {
                                String id = AllLinesData.searchFirstItem(findRow, qNeed, qCurrent, isBar);
                                if (id != null) {
                                    String[] barcodes = AllLinesData.getParam(id);
                                    List<String[]> data = AllLinesData.findSameUniqueItemsFromMap(barcodes[qBarcode] , qBarcode, qRefLineId);
                                    if(data.size() > 1) {
                                        showSelectItemDialog("", data);
                                    }else if(data.size() == 1){
                                        refreshData(id);
                                    }else if(data.size() == 0){
                                        HelperClass.messageBox(getActivity(),"Feladatkezelés","Az adott tétel már rakhelyen van!\nHa változtatni szeretné kérem törölje a rakhelyről!",HelperClass.WARNING);
                                    }

                                    if (isnew) {
                                        headerText.setBackgroundResource(R.color.headerColor);
                                        subHeadText1.setBackgroundResource(R.color.headerColor);
                                        subHeadText2.setBackgroundResource(R.color.headerColor);
                                        subHeadText3.setBackgroundResource(R.color.headerColor);
                                    } else {
                                        headerText.setBackgroundResource(R.color.headerColor2);
                                        subHeadText1.setBackgroundResource(R.color.headerColor2);
                                        subHeadText2.setBackgroundResource(R.color.headerColor2);
                                        subHeadText3.setBackgroundResource(R.color.headerColor2);
                                    }
                                    isnew = !isnew;
                                } else {
                                    if( AllLinesData.isValidateValue(findRow, isBar) ) {
                                        HelperClass.errorSound(getActivity());
                                        HelperClass.messageBox(getActivity(),"Részletek","Nem található több ebből a cikkből!",HelperClass.WARNING);
                                    }else{
                                        //HelperClass.messageBox(getActivity(), "Részletek", "Nem létező vonalkód (1011)!", HelperClass.ERROR);
                                        HelperClass.messageBox(getActivity(), "Részletek", "Nem létező vonalkód!\nEAN kód: "+isBar, HelperClass.ERROR);
                                    }
                                }
                            }
                        } else {
                            if( AllLinesData.isValidateValue(findRow, isBar) ) {
                                HelperClass.errorSound(getActivity());
                                HelperClass.messageBox(getActivity(),"Részletek","Nem található több ebből a cikkből!",HelperClass.WARNING);
                            }else{
                                //HelperClass.messageBox(getActivity(), "Részletek", "Nem létező vonalkód (1012)!", HelperClass.ERROR);
                                HelperClass.messageBox(getActivity(), "Részletek", "Nem létező vonalkód!\nEAN kód: "+isBar, HelperClass.ERROR);
                            }
                            //Toast.makeText(getContext(), "Nem létező vonalkód!", Toast.LENGTH_LONG).show();
                        }
                        findValue.removeTextChangedListener(textWatcher);
                        findValue.setText("");
                        findValue.addTextChangedListener(textWatcher);
                        refreshPlaceCounter();
                    }
                } catch (Exception e) {
                    db.logDao().addLog(new LogTable(LogTable.LogType_Error,"ScanCountFragment",e.getMessage(),"LOGUSER",null,null));
                    e.printStackTrace();
                    HelperClass.messageBox(getActivity(),"Részletek","Ismeretlen hiba (004)!",HelperClass.ERROR);
                    //Toast.makeText(getContext(), "ERROR SCANCOUNT 004", Toast.LENGTH_LONG).show();
                }
            }else{
                findValue.removeTextChangedListener(textWatcher);
                findValue.setText("");
                findValue.addTextChangedListener(textWatcher);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            //s.append("OK");
        }
    };

    void buildDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Új termékkel akar tovább dolgozni?");
        View v = getLayoutInflater().inflate(R.layout.dialog_new_item,null);
        TextView tv1 = v.findViewById(R.id.dialog_new_item_kod);
        tv1.setText(isBar);
        builder.setView(v);
        builder.setPositiveButton("IGEN", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                List<String[]> resourceDataList = new ArrayList<>();
                ArrayList<String> findValueList = AllLinesData.findKeyFromMap(isBar,findRow);
                if( findValueList.size() > 0 ) {
                    for (int i = 0; i < findValueList.size(); i++) {
                        resourceDataList.add(AllLinesData.getParam(findValueList.get(i)));
                    }
                    if (resourceDataList != null) {
                        if (resourceDataList.size() > 1) {
                            SessionClass.setParam("scancount_selectBar", isBar);
                            getFragmentManager().popBackStack();
                            KeyboardUtils.hideKeyboard(getActivity());
                        } else {
                            refreshData(resourceDataList.get(0)[0]);
                        }
                    }
                }else{
                    HelperClass.errorSound(getActivity());
                    HelperClass.messageBox(getActivity(), "Részletek", "Nem létező vonalkód!\nEAN kód: "+isBar, HelperClass.ERROR);
                }
            }
        });
        builder.setNegativeButton("NEM", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                KeyboardUtils.hideKeyboard(getActivity());
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void refreshData(String id) {
        CheckedList.setParamItem(id,1);
        try {
            lineID = id;
            SessionClass.setParam("currentLineID", lineID);
            SharedPreferences sharedPref = getActivity().getPreferences(getContext().MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("whrs_selexped_currentLineID", lineID);
            if( NotCloseList.getParamItem(lineID) == 0 ){
                notCloseSwitch.setChecked(true);
            }else{
                notCloseSwitch.setChecked(false);
            }
            editor.commit();
            KeyboardUtils.hideKeyboard(getActivity());
            String findItems = AllLinesData.getParam(lineID)[findRow];
            if (findItems != null && findItems.length() > 1) {
                if ((findItems.substring(findItems.length() - 1, findItems.length())).equals("|")) {
                    if (findItems.charAt(0) == '0') {
                        findItems = findItems.substring(1);
                    }
                    findItems = findItems.replace("|0", "|");
                    findItems = findItems.substring(0, findItems.length() - 1);
                    inBar = new ArrayList<>(Arrays.asList(findItems.split("\\|")));
                }else{
                    inBar = new ArrayList<>(Arrays.asList(findItems));
                }

            } else {

            }
            if (!newOpenWindow) {
                int itemCount = Integer.parseInt(AllLinesData.getParam(lineID)[qCurrent]);
                int maxItemCount = Integer.parseInt(AllLinesData.getParam(lineID)[qNeed]);
                if ((itemCount + 1) > maxItemCount) {
                    if (tranCode.charAt(0) == '2') {
                        HelperClass.errorSound(getActivity());
                        HelperClass.messageBox(getActivity(),"Részletek","Nem tárolható ki több tétel!",HelperClass.ERROR);
                    }
                } else {
                    if (qCurrent != 0) {
                        AllLinesData.setItemParams(lineID, qCurrent, "" + (itemCount + 1));
                    }
                    if (qMissing != 0) {
                        AllLinesData.setItemParams(lineID, qMissing, "" + ((itemCount + 1) - Integer.parseInt(AllLinesData.getParam(lineID)[qNeed])));
                    }
                }
            }
            counter.setText("1");

            String[] labelIndex = SessionClass.getParam(tranCode + "_Detail_Label_Info_Index").split(",", -1);

            TextView textValue1 = rootView.findViewById(R.id.scancount_textValue1);
            textValue1.setText(AllLinesData.getParam(lineID)[Integer.parseInt(labelIndex[0])]);

            TextView textValue2 = rootView.findViewById(R.id.scancount_textValue2);
            if (labelIndex.length > 1) {
                textValue2.setText(AllLinesData.getParam(lineID)[Integer.parseInt(labelIndex[1])]);
            }else{
                textValue2.setVisibility(View.GONE);
            }

            TextView textValue3 = rootView.findViewById(R.id.scancount_textValue3);
            if (labelIndex.length > 2) {
                textValue3.setText(AllLinesData.getParam(lineID)[Integer.parseInt(labelIndex[2])]);
            } else {
                textValue3.setVisibility(View.GONE);
            }

            textDataValue1 = rootView.findViewById(R.id.scancount_data2_value1);
            textDataValue1.setText(AllLinesData.getParam(lineID)[qCurrent]);
            textDataValue2 = rootView.findViewById(R.id.scancount_data2_value2);
            textDataValue2.setText(AllLinesData.getParam(lineID)[qNeed]);
            textDataValue3 = rootView.findViewById(R.id.scancount_data1_value);

            textDataValue1.setOnFocusChangeListener(HelperClass.getFocusBorderListener(getContext()));
            textDataValue2.setOnFocusChangeListener(HelperClass.getFocusBorderListener(getContext()));
            textDataValue3.setOnFocusChangeListener(HelperClass.getFocusBorderListener(getContext()));

            def = Integer.parseInt(AllLinesData.getParam(lineID)[qCurrent]) - Integer.parseInt(AllLinesData.getParam(lineID)[qNeed]);
            if(AllLinesData.getParam(lineID)[qCurrent].equals("0")){def = 0;}
            textDataValue3.setText("" + def);

            /*
            if (tranCode.charAt(0) == '1') {
                TextView textDataLabel = rootView.findViewById(R.id.scancount_data_label);
                EditText textDataValue = rootView.findViewById(R.id.scancount_data_value);
                if (AllLinesData.getParam(lineID)[Integer.parseInt(arrayTempInt[0])].equals("")) {
                    textDataValue.setText(SessionClass.getParam("currentPlace"));
                    AllLinesData.setItemParams(lineID, Integer.parseInt(arrayTempInt[0]), SessionClass.getParam("currentPlace"));
                } else {
                    textDataValue.setText(AllLinesData.getParam(lineID)[Integer.parseInt(arrayTempInt[0])]);
                }
                if (arrayTempType[0].equals("int") || arrayTempType[0].equals("Int")) {
                    textDataValue.setInputType(InputType.TYPE_CLASS_NUMBER);
                } else {
                    textDataValue.setInputType(InputType.TYPE_CLASS_TEXT);
                }
                textDataLabel.setText(arrayTempNames[0]);
                textDataValue.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        AllLinesData.setItemParams(lineID, Integer.parseInt(arrayTempInt[0]), s.toString());
                    }
                });
            }*/

            if( SessionClass.getParam("collectionBtn").equals("1") ){
                textDataValue1.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        try{
                            int x = Integer.parseInt(s.toString());
                            if(x > 0){
                                if( !getTextDataValue3.getText().toString().equals("")) {
                                    AllLinesData.setItemParams(lineID, qBarcode01, getTextDataValue3.getText().toString());
                                    String palett = AllLinesData.getParamPosition(qBarcode01, qBarcode02, getTextDataValue3.getText().toString());
                                    if (palett != null) {
                                        AllLinesData.setItemParams(lineID, qBarcode02, palett);
                                    }
                                }
                            }else{
                                AllLinesData.setItemParams(lineID, qBarcode02, "");
                                AllLinesData.setItemParams(lineID, qBarcode01, "");
                            }
                        }catch (NumberFormatException e){
                            Toast.makeText(getContext(),"Hibás szám formátum!", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                collectorBtn.setVisibility(View.VISIBLE);
                collectorLabel.setVisibility(View.VISIBLE);
                collectorBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        collectorDialog();
                    }
                });
                getTextDataValue3.setText(SessionClass.getParam("currentCollect"));
            }

            try {
                int toplace = -1;
                if (tranCode.charAt(0) == '1') {
                     toplace = Arrays.asList(arrayTextBoxSelect).indexOf("To_Place");

                }
                for (int i = 0; i < arrayLabelIndex.length; i++) {
                    int resIDLayer = getActivity().getResources().getIdentifier("scancount_subHeadText" + (i+1) , "id", getActivity().getPackageName());
                    int resIDLabel = getActivity().getResources().getIdentifier("scancount_textLabel" + (i+1) , "id", getActivity().getPackageName());
                    LinearLayout layer = rootView.findViewById(resIDLayer);
                    TextView label = rootView.findViewById(resIDLabel);
                    layer.setVisibility(View.VISIBLE);
                    if( arrayLabelTextBox.length > i && arrayLabelTextBox[i] != null){
                        label.setText(arrayLabelTextBox[i]);
                    }else{
                        label.setText("");
                    }
                }
                whEditBoxes = new WHEditBox[arrayTextBoxIndexes.length - 1];
                CustomTextWatcher[] customTextWatcherArray = new CustomTextWatcher[arrayTextBoxIndexes.length - 1];
                for (int i = 0; i < arrayTextBoxIndexes.length; i++) {
                    int resIDLayer = getActivity().getResources().getIdentifier("scancount_param" + (i + 1) + "_layer", "id", getActivity().getPackageName());
                    int resIDLabel = getActivity().getResources().getIdentifier("scancount_param" + (i + 1) + "_label", "id", getActivity().getPackageName());
                    LinearLayout layer = rootView.findViewById(resIDLayer);
                    TextView label = rootView.findViewById(resIDLabel);
                    if (arrayTextBoxEnableds[i].equals("1")) {
                        layer.setVisibility(View.VISIBLE);
                    } else {
                        layer.setVisibility(View.GONE);
                    }
                    if(i > 0) {
                        int resIDValue = getActivity().getResources().getIdentifier("scancount_param" + (i + 1) + "_value", "id", getActivity().getPackageName());
                        //WHEditBox value = rootView.findViewById(resIDValue);
                        whEditBoxes[i-1] = rootView.findViewById(resIDValue);
                        //whEditBoxes[i-1].setSelectBackgroundFunc(R.drawable.et_shape_select);
                        if(arrayTextBoxTypes[i] != null && arrayTextBoxTypes[i].equals("NUM")){
                            whEditBoxes[i-1].EDText.setInputType(InputType.TYPE_CLASS_NUMBER);
                        }
                        whEditBoxes[i-1].setOnDetectBarcodeListener(new WHEditBox.OnDetectBarcodeListener() {
                            @Override
                            public void OnDetectBarcode(String value) {

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
                        label.setText(arrayTextBoxLabels[i]);

                        if( customTextWatcherArray[i-1] == null ){
                            customTextWatcherArray[i-1] = new CustomTextWatcher();
                            whEditBoxes[i-1].EDText.addTextChangedListener(customTextWatcherArray[i-1]);
                        }
                        customTextWatcherArray[i-1].changeSetting(getActivity(), Integer.parseInt(arrayTextBoxIndexes[i]), lineID, whEditBoxes[i-1].EDText);


                        if(i == toplace) {
                            whEditBoxes[i-1].EDText.setText(SessionClass.getParam("currentPlace"));
                        } else {
                            whEditBoxes[i-1].EDText.setText(AllLinesData.getParam(lineID)[Integer.parseInt(arrayTextBoxIndexes[i])]);
                        }
                        /*
                        if( arrayTextBoxTypes[i] != null){
                            if( arrayTextBoxTypes[i].equals("COMBO")) {
                                whEditBoxes[i - 1].EDText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                    @Override
                                    public void onFocusChange(View v, boolean hasFocus) {
                                        if (hasFocus) show((EditText) v);
                                    }
                                });
                                whEditBoxes[i - 1].EDText.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        show((EditText) v);
                                    }
                                });
                            }
                        }*/
                    }
                }
                for (int i = 0; i < arrayTextBoxIndexes.length-2; i++) {
                    whEditBoxes[i].setNextFocus(whEditBoxes[i+1].EDText);
                }
            }catch (Exception e){
                db.logDao().addLog(new LogTable(LogTable.LogType_Error,"ScanCountFragment",e.getMessage(),"LOGUSER",null,null));
                e.printStackTrace();
                Toast.makeText(getContext(),"Hiba a művelet közben!",Toast.LENGTH_LONG).show();
            }
            refreshPlaceCounter();
        }catch (Exception e){
            db.logDao().addLog(new LogTable(LogTable.LogType_Error,"ScanCountFragment",e.getMessage(),"LOGUSER",null,null));
            e.printStackTrace();
            HelperClass.messageBox(getActivity(),"Részletek","Ismeretlen hiba (005)!",HelperClass.ERROR);
            //Toast.makeText(getContext(),"ERROR SCANCOUNT 005",Toast.LENGTH_LONG).show();
        }
    }

    public void loadQR() {
        try {
            IntentIntegrator integrator = IntentIntegrator.forSupportFragment(this);
            integrator
                    .setOrientationLocked(false)
                    .setBeepEnabled(true)
                    .setCameraId(0)
                    .setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)
                    .setBarcodeImageEnabled(true)
                    .initiateScan();
        }catch (Exception e){
            db.logDao().addLog(new LogTable(LogTable.LogType_Error,"ScanCountFragment",e.getMessage(),"LOGUSER",null,null));
            e.printStackTrace();
            HelperClass.messageBox(getActivity(),"Részletek","Ismeretlen hiba (006)!",HelperClass.ERROR);
            //Toast.makeText(getContext(),"ERROR SCANCOUNT 006",Toast.LENGTH_LONG).show();
        }
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


    private void refreshPlaceCounter(){
        CheckedList.setParamItem(lineID,1);
        try {
            if (tranCode.charAt(0) == '1') {
                headerText.setText("Ellenőrzés / " + AllLinesData.getPlaceCount(qTo_Place, qCurrent, SessionClass.getParam("currentPlace")));
            } else if (tranCode.charAt(0) == '5') {
                headerText.setText("Ellenőrzés / " + AllLinesData.getAllCurrentCount(qCurrent));
            }else if (tranCode.charAt(0) == '2') {
                if( SessionClass.getParam("collectionBtn").equals("1") ){
                    headerText.setText("Ellenőrzés / " + AllLinesData.getPlaceCount(qBarcode01, qCurrent, getTextDataValue3.getText().toString() ));
                }
            }
            saveDBDatas();
        }catch (Exception e){
            db.logDao().addLog(new LogTable(LogTable.LogType_Error,"ScanCountFragment",e.getMessage(),"LOGUSER",null,null));
            e.printStackTrace();
            HelperClass.messageBox(getActivity(),"Részletek","Ismeretlen hiba (007)!",HelperClass.ERROR);
            //Toast.makeText(getContext(),"ERROR SCANCOUNT 007",Toast.LENGTH_LONG).show();
        }
        if(dataCounter == 10){
            dataCounter = 0;
            new SaveCheckedDataThread(getContext()).start();
        }
        dataCounter++;
    }

    private void saveDBDatas(){
        SaveIdSessionTemp sit = new SaveIdSessionTemp(getContext());
        List<Long> ids = new ArrayList<>();
        ids.add(Long.parseLong(lineID));
        sit.setId( ids );
        sit.start();
    }

    void buildFullDialog(final String id) {
        dialogActive = true;
        KeyboardUtils.hideKeyboard(getActivity());
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Figyelem több tétel jött be mint a rendszerben rögzített!");
        builder.setPositiveButton("Rendben", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int itemCount = Integer.parseInt(AllLinesData.getParam(lineID)[qCurrent]);
                AllLinesData.setItemParams(lineID, qCurrent, "" + (itemCount + Integer.parseInt(counter.getText().toString())));
                if (qMissing != 0) {
                    AllLinesData.setItemParams(lineID, qMissing, "" + (Integer.parseInt(AllLinesData.getParam(lineID)[qCurrent]) - Integer.parseInt(AllLinesData.getParam(lineID)[qNeed])));
                }
                textDataValue1.setText(AllLinesData.getParam(lineID)[qCurrent]);
                textDataValue2.setText(AllLinesData.getParam(lineID)[qNeed]);
                def = Integer.parseInt(AllLinesData.getParam(lineID)[qCurrent]) - Integer.parseInt(AllLinesData.getParam(lineID)[qNeed]);

                if(AllLinesData.getParam(lineID)[qCurrent].equals("0")){def = 0;}
                textDataValue3.setText("" + def);
                refreshData(id);

                dialogActive = false;
                dialog.cancel();
            }
        });
        builder.setNegativeButton("Mégsem", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                refreshData(id);
                dialogActive = false;
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void showSelectItemDialog(final String barcode, final List<String[]> data){
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
                    if( Integer.parseInt(dataList.get(i)[qNeed]) > 0 ) {
                        itemsList.add(new XD_Dialog_ItemsParameters(Long.parseLong(dataList.get(i)[0]), Integer.parseInt(dataList.get(i)[qNeed]), Integer.parseInt(dataList.get(i)[qCurrent]), Integer.parseInt(dataList.get(i)[qWeight]), HelperClass.convertStringToFloat(dataList.get(i)[qLength]), HelperClass.convertStringToFloat(dataList.get(i)[qWidth]), HelperClass.convertStringToFloat(dataList.get(i)[qHeight]), dataList.get(i)[qToPlace]));
                    }
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
                refreshData(SessionClass.getParam("XD_CHECKED3"));
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

    public void show(final EditText et) {
        final Dialog d = new Dialog(getContext());
        d.setTitle("NumberPicker");
        d.setContentView(R.layout.dialog_combo_picker);
        ImageView b1 = d.findViewById(R.id.button1);
        ImageView b2 = d.findViewById(R.id.button2);

        final NumberPicker np = d.findViewById(R.id.numberPicker1);
        np.setMaxValue(arrayComboSelect.length-1);
        np.setMinValue(0);
        np.setWrapSelectorWheel(false);
        np.setDisplayedValues(arrayComboSelect);
        np.setValue(selectedComboVal);
        b1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                et.setText( arrayComboSelect[np.getValue()] );
                selectedComboVal = np.getValue();
                d.dismiss();
            }
        });
        b2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });
        d.show();
    }
}