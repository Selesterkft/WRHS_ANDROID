package hu.selester.android.webstockandroid.Fragments;

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
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import hu.selester.android.webstockandroid.Database.SelesterDatabase;
import hu.selester.android.webstockandroid.Dialogs.MessageDialog;
import hu.selester.android.webstockandroid.Helper.HelperClass;
import hu.selester.android.webstockandroid.Helper.KeyboardUtils;
import hu.selester.android.webstockandroid.Objects.ActiveFragment;
import hu.selester.android.webstockandroid.Objects.AllLinesData;
import hu.selester.android.webstockandroid.Objects.CheckedList;
import hu.selester.android.webstockandroid.Objects.MessageBoxSettingsObject;
import hu.selester.android.webstockandroid.Objects.SessionClass;
import hu.selester.android.webstockandroid.R;
import hu.selester.android.webstockandroid.Threads.SaveCheckedDataThread;
import hu.selester.android.webstockandroid.Threads.SaveIdSessionTemp;

public class ScanCountFragment extends Fragment implements View.OnClickListener{

    private EditText counter, findValue;
    private SelesterDatabase db;
    private String isBar;
    private int findRow;
    private String lineID;
    private EditText textDataValue1;
    private TextView textDataValue2,textDataValue3;
    private MessageDialog message;
    private Bundle si;
    private View rootView;
    private ArrayList<String> inBar;
    private String tranCode, Head_ID;
    private int qNeed,qCurrent,qMissing;
    private EditText textDataValue;
    private String[] arrayTempInt, arrayTempNames, arrayTempType;
    private TextView headerText;
    private LinearLayout subHeadText1, subHeadText2, subHeadText3;
    private boolean newOpenWindow;
    private boolean isnew;
    private int def;
    private boolean dialogActive = false;
    private int dataCounter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        ActiveFragment.setFragment(this);
        lineID = getArguments().getString("lineID");
        CheckedList.setParamItem(lineID,1);
        dataCounter = 0;
        si = savedInstanceState;
        rootView = inflater.inflate(R.layout.frg_scancount,container,false);
        newOpenWindow = true;
        try {
            counter = rootView.findViewById(R.id.scancount_count);
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
                        counter.setText("1");
                    }
                }
            });
            db = SelesterDatabase.getDatabase(getContext());

            tranCode = getArguments().getString("tranCode");
            isnew = false;
            Log.i("SQL", tranCode);
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
            headerText = rootView.findViewById(R.id.scancount_headertext);
            headerText.setBackgroundResource(R.color.headerColor);
            subHeadText1 = rootView.findViewById(R.id.scancount_subHeadText1);
            subHeadText1.setBackgroundResource(R.color.headerColor);
            subHeadText2 = rootView.findViewById(R.id.scancount_subHeadText2);
            subHeadText2.setBackgroundResource(R.color.headerColor);
            subHeadText3 = rootView.findViewById(R.id.scancount_subHeadText3);
            subHeadText3.setBackgroundResource(R.color.headerColor);
//        qNeed = Integer.parseInt(SessionClass.getParam(tranCode+"_Line_ListView_Qty_Control"));
            String[] temp = SessionClass.getParam(tranCode + "_Line_ListView_Qty_Part").split(",", -1);
            if (SessionClass.getParam(tranCode + "_Line_ListView_Qty_Part").equals("")) {
                qMissing = 0;
            } else {
                if (temp[1].equals("")) {
                    qMissing = 0;
                } else {
                    qMissing = Integer.parseInt(temp[1]);
                }
            }
            findRow = Integer.parseInt(SessionClass.getParam(tranCode + "_Line_TextBox_Find_Index"));
            findValue = rootView.findViewById(R.id.scancount_header_value);
            arrayTempNames = SessionClass.getParam(tranCode + "_Detail_TextBox_Names").split(",");
            arrayTempType = SessionClass.getParam(tranCode + "_Detail_TextBox_DataType").split(",");
            arrayTempInt = SessionClass.getParam(tranCode + "_Detail_TextBox_Index").split(",");
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
                });
            }
            findValue.requestFocus();

            TextView textLabel1 = rootView.findViewById(R.id.scancount_textLabel1);
            TextView textLabel2 = rootView.findViewById(R.id.scancount_textLabel2);
            TextView textLabel3 = rootView.findViewById(R.id.scancount_textLabel3);
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
            Button goBackBtn = rootView.findViewById(R.id.scancount_goback);
            goBackBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(textDataValue1.getText().toString().equals("0")){
                        AllLinesData.setItemParams(lineID, qMissing, "0");
                        if(tranCode.charAt(0)=='1'){
                            AllLinesData.setItemParams(lineID, Integer.parseInt(arrayTempInt[0]), "");
                        }
                    }else {
                        AllLinesData.setItemParams(lineID, qMissing, textDataValue3.getText().toString());
                    }
                    saveDBDatas();
                    getFragmentManager().popBackStack();
                }
            });
            newOpenWindow = false;
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getContext(),"ERROR SCANCOUNT 001",Toast.LENGTH_LONG).show();
        }
        //HelperClass.loadTempSession(getContext());
        return rootView;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        try {
            int now = Integer.parseInt(textDataValue1.getText().toString());
            int x1;
            int x2 = Integer.parseInt(textDataValue2.getText().toString());
            switch (v.getId()) {
                case R.id.scancount_addBtn:
                    textDataValue1.setText("" + (now + Integer.parseInt(counter.getText().toString())));
                    x1 = Integer.parseInt(textDataValue1.getText().toString());
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
                    break;
                case R.id.scancount_removeBtn:
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
                        }else{
                            AllLinesData.setItemParams(lineID, qMissing, "0");
                        }
                    }
                    break;
                case R.id.scancount_header_btn:
                    break;
            }
            refreshPlaceCounter();
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getContext(),"ERROR SCANCOUNT 002",Toast.LENGTH_LONG).show();
        }
    }

    private void checkListElementsManual(){
        try {
            isBar = findValue.getText().toString();
            if (!isBar.isEmpty()) findValue.setText(isBar + SessionClass.getParam("barcodeSuffix"));
            refreshPlaceCounter();
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getContext(),"ERROR SCANCOUNT 003",Toast.LENGTH_LONG).show();
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
                                    String id = AllLinesData.searchFirstItem(findRow, qNeed, qCurrent, isBar);
                                    if (id != null) {
                                        refreshData(id);
                                    } else {
                                        buildFullDialog();
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
                                //                        if (tranCode.equals("21")) {
                                //                            buildDialog();
                                //                        }else{
                                String id = AllLinesData.searchFirstItem(findRow, qNeed, qCurrent, isBar);
                                if (id != null) {
                                    refreshData(id);
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
                                    Toast.makeText(getContext(), "Nem tárolható ilyen tétel!", Toast.LENGTH_LONG).show();
                                }
                                //}

                            }
                        } else {
                            Toast.makeText(getContext(), "Nem létező vonalkód!", Toast.LENGTH_LONG).show();
                        }
                        findValue.removeTextChangedListener(textWatcher);
                        findValue.setText("");
                        findValue.addTextChangedListener(textWatcher);
                        refreshPlaceCounter();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "ERROR SCANCOUNT 004", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(getContext(), "Nem létező vonalkód!", Toast.LENGTH_LONG).show();
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
            editor.commit();

            KeyboardUtils.hideKeyboard(getActivity());
            String findItems = AllLinesData.getParam(lineID)[findRow];
            if (findItems != null && findItems.length() > 2) {
                if ((findItems.substring(findItems.length() - 1, findItems.length())).equals("|")) {
                    if (findItems.charAt(0) == '0') {
                        findItems = findItems.substring(1);
                    }
                    findItems = findItems.replace("|0", "|");
                    findItems = findItems.substring(0, findItems.length() - 1);
                }
                inBar = new ArrayList<>(Arrays.asList(findItems.split("\\|")));
            } else {

            }
            if (!newOpenWindow) {
                int itemCount = Integer.parseInt(AllLinesData.getParam(lineID)[qCurrent]);
                int maxItemCount = Integer.parseInt(AllLinesData.getParam(lineID)[qNeed]);
                if ((itemCount + 1) > maxItemCount) {
                    if (tranCode.charAt(0) == '2') {
                        Toast.makeText(getContext(), "Nem tárolható ki több tétel!", Toast.LENGTH_LONG).show();
                    }
                } else {
                    if (qCurrent != 0) {
                        AllLinesData.setItemParams(lineID, qCurrent, "" + (itemCount + 1));
                    }
                    if (qMissing != 0) {
                        AllLinesData.setItemParams(lineID, qMissing, "" + ((itemCount + 1) - Integer.parseInt(AllLinesData.getParam(lineID)[qNeed])));
                    }
                }
            } else {
            }
            counter.setText("1");

            String[] labelIndex = SessionClass.getParam(tranCode + "_Detail_Label_Info_Index").split(",", -1);

            TextView textValue1 = rootView.findViewById(R.id.scancount_textValue1);
            textValue1.setText(AllLinesData.getParam(lineID)[Integer.parseInt(labelIndex[0])]);
            TextView textValue2 = rootView.findViewById(R.id.scancount_textValue2);
            textValue2.setText(AllLinesData.getParam(lineID)[Integer.parseInt(labelIndex[1])]);

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
            def = Integer.parseInt(AllLinesData.getParam(lineID)[qCurrent]) - Integer.parseInt(AllLinesData.getParam(lineID)[qNeed]);
            if(AllLinesData.getParam(lineID)[qCurrent].equals("0")){def = 0;}
            textDataValue3.setText("" + def);

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
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        AllLinesData.setItemParams(lineID, Integer.parseInt(arrayTempInt[0]), s.toString());

                    }
                });
            }
            refreshPlaceCounter();
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getContext(),"ERROR SCANCOUNT 005",Toast.LENGTH_LONG).show();
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
            e.printStackTrace();
            Toast.makeText(getContext(),"ERROR SCANCOUNT 006",Toast.LENGTH_LONG).show();
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
        try {
            if (tranCode.charAt(0) == '1') {
                headerText.setText("Ellenőrzés / " + AllLinesData.getPlaceCount(10, qCurrent, SessionClass.getParam("currentPlace")));
            } else if (tranCode.charAt(0) == '5') {
                headerText.setText("Ellenőrzés / " + AllLinesData.getAllCurrentCount(qCurrent));
            }
            saveDBDatas();
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getContext(),"ERROR SCANCOUNT 007",Toast.LENGTH_LONG).show();
        }
        if(dataCounter == 10){
            dataCounter = 0;
            new SaveCheckedDataThread(getContext()).start();
        }
        dataCounter++;


    }

    private void saveDBDatas(){
        SaveIdSessionTemp sit = new SaveIdSessionTemp(getContext());
        sit.setId(Long.parseLong(lineID));
        sit.start();
    }
    void buildFullDialog() {
        dialogActive = true;
        KeyboardUtils.hideKeyboard(getActivity());
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Nem található több ebből a cikkből!");
        //builder.setMessage("Törlés esetén az eddig felvett adatok törlődnek!");

        builder.setNegativeButton("Rendben", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialogActive = false;
                dialog.cancel();
            }
        });

        builder.show();

    }
}
