package hu.selester.android.webstockandroid.Fragments;

import android.content.DialogInterface;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import hu.selester.android.webstockandroid.Adapters.XD_Dialog_ItemsParametersListAdapter;
import hu.selester.android.webstockandroid.Adapters.XD_PlacenumberPagerAdapter;
import hu.selester.android.webstockandroid.Adapters.XD_ItemsParametersListAdapter;
import hu.selester.android.webstockandroid.Database.SelesterDatabase;
import hu.selester.android.webstockandroid.Database.Tables.SessionTemp;
import hu.selester.android.webstockandroid.Divider.SimpleDividerItemDecoration;
import hu.selester.android.webstockandroid.Helper.HelperClass;
import hu.selester.android.webstockandroid.Helper.KeyboardUtils;
import hu.selester.android.webstockandroid.Helper.MySingleton;
import hu.selester.android.webstockandroid.Helper.SlidingTabLayout;
import hu.selester.android.webstockandroid.Objects.AllLinesData;
import hu.selester.android.webstockandroid.Objects.CheckedList;
import hu.selester.android.webstockandroid.Objects.DefaultTextWatcher;
import hu.selester.android.webstockandroid.Objects.InsertedList;
import hu.selester.android.webstockandroid.Objects.SessionClass;
import hu.selester.android.webstockandroid.Objects.XD_Dialog_ItemsParameters;
import hu.selester.android.webstockandroid.Objects.XD_ItemsParameters;
import hu.selester.android.webstockandroid.R;
import hu.selester.android.webstockandroid.TablePanel.TablePanel;
import hu.selester.android.webstockandroid.Threads.SaveAllSessionTemp;
import hu.selester.android.webstockandroid.Threads.SaveCheckedDataThread;
import hu.selester.android.webstockandroid.Threads.SaveIdSessionTemp;
import mobil.selester.wheditbox.WHEditBox;

public class XD_ItemParametersFragment extends Fragment {

    private View rootView;
    private TextView txt;
    private RecyclerView itemsListContainer;
    private ViewPager vp;
    private SlidingTabLayout spl;
    private TextInputEditText placeNumberTV;
    private int qEvidNum, qNeed, qCurrent, qWeight, qWidth, qHeight, qLength, qBarcode, qToPlace, qRefLineId, qSelID, qMissing;
    private String tranCode;
    private String tranID, evidNum;
    private WHEditBox edit;
    private XD_ItemParametersFragment myFrag;
    private Button chkBtn;
    private List<String[]> dataDialog;
    private SelesterDatabase db;
    private boolean trimmed;
    private AlertDialog.Builder builderSelect;
    private String dialogSelectItem;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myFrag = this;
        trimmed = true;
        rootView = inflater.inflate(R.layout.frg_itemparameters, container, false);
        db = SelesterDatabase.getDatabase(getContext());
        tranCode    = SessionClass.getParam("tranCode");
        qSelID      = 0;
        qBarcode    = HelperClass.getArrayPosition("Barcode",       SessionClass.getParam(tranCode + "_Line_ListView_SELECT"));
        qEvidNum    = HelperClass.getArrayPosition("EvidNum",       SessionClass.getParam(tranCode + "_Line_ListView_SELECT"));
        qNeed       = HelperClass.getArrayPosition("Needed_Qty",    SessionClass.getParam(tranCode + "_Line_ListView_SELECT"));
        qCurrent    = HelperClass.getArrayPosition("Current_Qty",   SessionClass.getParam(tranCode + "_Line_ListView_SELECT"));
        qMissing    = HelperClass.getArrayPosition("Missing_Qty",   SessionClass.getParam(tranCode + "_Line_ListView_SELECT"));
        qWeight     = HelperClass.getArrayPosition("Weight",        SessionClass.getParam(tranCode + "_Line_ListView_SELECT"));
        qWidth      = HelperClass.getArrayPosition("Size_Width",    SessionClass.getParam(tranCode + "_Line_ListView_SELECT"));
        qHeight     = HelperClass.getArrayPosition("Size_Height",   SessionClass.getParam(tranCode + "_Line_ListView_SELECT"));
        qLength     = HelperClass.getArrayPosition("Size_Length",   SessionClass.getParam(tranCode + "_Line_ListView_SELECT"));
        qToPlace    = HelperClass.getArrayPosition("To_Place",      SessionClass.getParam(tranCode + "_Line_ListView_SELECT"));
        qRefLineId  = HelperClass.getArrayPosition("Ref_Line_ID",   SessionClass.getParam(tranCode + "_Line_ListView_SELECT"));

        evidNum     = getArguments().getString("evidNum");
        dataDialog = new ArrayList<>();
        dialogSelectItem = "";
        List<XD_PlacenumberFragment> frgList = new ArrayList<>();
        List<String> pageTitleList = new ArrayList<>();
        SessionClass.setParam("XD_CHECKED","0");
        SessionClass.setParam("XD_CHECKED2","0");
        vp = rootView.findViewById(R.id.ps_pager);
        XD_PlacenumberPagerAdapter ppa = new XD_PlacenumberPagerAdapter(getFragmentManager(), frgList, pageTitleList);
        vp.setAdapter( ppa );
        vp.setOffscreenPageLimit(1);
        spl = rootView.findViewById(R.id.ps_sliding_panel);
        spl.setViewPager(vp);
        placeNumberTV = rootView.findViewById(R.id.ps_placenumber);
        placeNumberTV.addTextChangedListener(new DefaultTextWatcher(placeNumberTV, new DefaultTextWatcher.TextChangedEvent() {
            @Override
            public void Changed() {
                addNewPlacenumPanel();
            }
        }));

        ImageView plusBtn = rootView.findViewById(R.id.ps_plus);
        ImageView minusBtn = rootView.findViewById(R.id.ps_minus);
        ImageView explodeBtn = rootView.findViewById(R.id.ps_explode);

        plusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( SessionClass.getParam("XD_CHECKED") != null && !SessionClass.getParam("XD_CHECKED" ).equals("0") && !SessionClass.getParam("XD_CHECKED" ).isEmpty() ) {
                    /*List<String[]> result = AllLinesData.findItemsFromMap(SessionClass.getParam("XD_CHECKED"), 0);
                    if (result.size() > 0) {
                        chkBarcode_add(String.valueOf(result.get(0)[qBarcode]));
                    } else {
                        Toast.makeText(getContext(), "Nincs tétel kiválasztva!", Toast.LENGTH_LONG).show();
                    }*/
                    chkBarcode_add();
                }
            }
        });

        minusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                if( SessionClass.getParam("XD_CHECKED") != null && !SessionClass.getParam("XD_CHECKED" ).equals("0") && !SessionClass.getParam("XD_CHECKED" ).isEmpty() ) {
                    List<String[]> result = AllLinesData.findItemsFromMap(SessionClass.getParam("XD_CHECKED"), 0);
                    if (result.size() > 0) {

                    } else {
                        Toast.makeText(getContext(), "Nincs tétel kiválasztva!", Toast.LENGTH_LONG).show();
                    }
                }*/
                chkBarcode_minus();
            }
        });

        explodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( SessionClass.getParam("XD_CHECKED2") != null && !SessionClass.getParam("XD_CHECKED2" ).equals("0") && !SessionClass.getParam("XD_CHECKED2" ).isEmpty() ) {
                    createLineDialog();
                }
            }
        });

        final TextInputLayout TIL_placeNumber = rootView.findViewById(R.id.textInputLayout4);
        placeNumberTV.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    TIL_placeNumber.setBackground( ContextCompat.getDrawable(getContext(), R.drawable.et_shape_select) );
                } else {
                    TIL_placeNumber.setBackground( ContextCompat.getDrawable(getContext(), R.drawable.et_shape) );
                }
            }
        });

        ImageView addPlaceNumberBtn = rootView.findViewById(R.id.ps_addplacenum_btn);
        addPlaceNumberBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewPlacenumPanel();
            }
        });
        ImageView exitBtn = rootView.findViewById(R.id.ps_exit);
        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        chkBtn = rootView.findViewById(R.id.ps_header_btn);
        chkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                trimmed = false; chkBarcode();
            }
        });
        edit = rootView.  findViewById(R.id.ps_header_value);
        edit.activity = getActivity();
        WHEditBox.suffix="#&";
        edit.setDialogTitle("Barcode");
        edit.setOnDetectBarcodeListener(new WHEditBox.OnDetectBarcodeListener() {
            @Override
            public void OnDetectBarcode(String value) {
                chkBarcode();
                KeyboardUtils.hideKeyboard(getActivity());
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
        edit.requestFocus();
        setViewPagerElement();
        getItems();
        return rootView;
    }

    private void addNewPlacenumPanel() {
        if( !placeNumberTV.getText().toString().equals("") ){
            if( db.placesDao().getTransactID() > 0 ) {
                if (db.placesDao().getPlacesData(placeNumberTV.getText().toString()) != null) {
                    boolean hereItIs = false;
                    for (int x = 0; x < vp.getAdapter().getCount(); x++) {
                        if (vp.getAdapter().getPageTitle(x).toString().equals(placeNumberTV.getText().toString())) {
                            hereItIs = true;
                        }
                    }
                    if (!hereItIs) {
                        XD_PlacenumberFragment newFrg = XD_PlacenumberFragment.newInstance(placeNumberTV.getText().toString());
                        newFrg.setParentFragment(myFrag);
                        ((XD_PlacenumberPagerAdapter) vp.getAdapter()).update(newFrg, placeNumberTV.getText().toString());
                        spl.setViewPager(vp);
                        placeNumberTV.setText("");
                        vp.setCurrentItem(vp.getAdapter().getCount(), true);
                        edit.EDText.requestFocus();
                    } else {
                        HelperClass.messageBox(getActivity(), "CrossDock", "Már van ilyen nevű rakhely definiálva ennél az evidenciánál!", HelperClass.WARNING);
                        placeNumberTV.setText("");
                    }
                } else {
                    HelperClass.messageBox(getActivity(), "CrossDock", "Nincs ilyen nevű rakhely ebben a raktárban!", HelperClass.ERROR);
                    placeNumberTV.setText("");
                }
            }else{
                HelperClass.messageBox(getActivity(), "CrossDock", "Nem érhető el a rakhely törzs, kérem ellenőrizze a raktári beállításokat!", HelperClass.ERROR);
                placeNumberTV.setText("");
            }
        }else{
            HelperClass.messageBox(getActivity(),"CrossDock","Nincs rakhely meghatározva!",HelperClass.ERROR);
        }
        KeyboardUtils.hideKeyboard(getActivity());
    }

    void getItems(){
        RecyclerView.LayoutManager lm = new LinearLayoutManager(getContext());
        List<XD_ItemsParameters> itemsList = new ArrayList<>();
        List<String[]> dataList = AllLinesData.findItemsFromMap(evidNum, qEvidNum);
        if( dataList != null && dataList.size() > 0 ){
            for(int i=0; i < dataList.size(); i++){
                if( dataList.get(i)[qToPlace].equals("") ) {
                    try {
                        itemsList.add(new XD_ItemsParameters(Long.parseLong(dataList.get(i)[0]), Integer.parseInt(dataList.get(i)[qNeed]), Integer.parseInt(dataList.get(i)[qCurrent]), Float.parseFloat(dataList.get(i)[qWeight]), HelperClass.convertStringToFloat(dataList.get(i)[qLength]), HelperClass.convertStringToFloat(dataList.get(i)[qWidth]), HelperClass.convertStringToFloat(dataList.get(i)[qHeight])));
                    }catch (Exception e){
                        e.printStackTrace();
                        HelperClass.messageBox(getActivity(),"Adatmegadás","Hibás érték formátum " , HelperClass.ERROR);
                    }
                }
            }
        }
        XD_ItemsParametersListAdapter XD_listAdapter = new XD_ItemsParametersListAdapter(getContext(),itemsList,0);
        XD_listAdapter.setEvidNum( evidNum );
        XD_listAdapter.setActivity(getActivity());
        XD_listAdapter.setOnEventUpdate(new XD_ItemsParametersListAdapter.OnEventUpdate() {
            @Override
            public void onUpdatePanel() {
                refreshPlace();
            }
        });
        itemsListContainer = rootView.findViewById(R.id.ps_rv_items_list);
        itemsListContainer.setLayoutManager(lm);
        itemsListContainer.setAdapter(XD_listAdapter);
        SimpleDividerItemDecoration itemDecor = new SimpleDividerItemDecoration(getContext());
        itemsListContainer.addItemDecoration(itemDecor);
    }

    private void refreshPlace() {
        ((XD_ItemsParametersListAdapter) itemsListContainer.getAdapter()).update("");
        if( vp.getAdapter() != null && vp.getAdapter().getCount() > 0 ) {
            for (int c = 0; c < vp.getAdapter().getCount(); c++) {
                if( ((XD_PlacenumberPagerAdapter) vp.getAdapter()).getItem(c) != null ){
                    XD_PlacenumberFragment page = ((XD_PlacenumberPagerAdapter) vp.getAdapter()).getItem(c);
                    page.update();
                }
            }
        }
    }

    private void chkBarcode(){
        if( vp.getAdapter().getCount() > 0 ) {
            String barcode = edit.EDText.getText().toString();
            trimmed = true;
            if (trimmed) barcode = HelperClass.getTrimmedText(barcode);
            dataDialog.clear();
            List<String[]> data = AllLinesData.findItemsFromMap(barcode, qBarcode);
            Log.i("TAG", barcode + " data: " + dataDialog.size() );
            for (int i = 0; i < data.size(); i++) {
                if ( data.get(i)[qEvidNum].equals(evidNum) && data.get(i)[qToPlace].equals("") ) {
                    if (Integer.parseInt(data.get(i)[qCurrent]) > 0) {
                        dataDialog.add(data.get(i));
                    }
                }
            }
            if( dataDialog != null ) {
                if (dataDialog.size() > 1) {
                    showSelectItemDialog(barcode, dataDialog);
                } if (dataDialog.size() == 1) {
                    moveSelectedItems(dataDialog.get(0)[0],barcode, dataDialog);
                }
            }

        }else{
            HelperClass.messageBox(getActivity(),"CrossDock","Nincs rakhely definiálva!",HelperClass.ERROR);
            //Toast.makeText(getContext(), "Nincs rakhely definiálva!", Toast.LENGTH_LONG).show();
        }
        edit.EDText.setText("");
    }

    private void moveSelectedItems(String id, String barcode, List<String[]> data) {
        for(int i=0 ; i< data.size();i++){
            Log.i("TAG", Arrays.toString( data.get(i) ) );
        }
        Log.i("TAG",""+id);

        boolean itOK = false;
        String vpTitle = vp.getAdapter().getPageTitle(vp.getCurrentItem()).toString();
        String newId = "";
        try {
            for (int i = 0; i < data.size(); i++) {
                if( data.get(i)[0].equals(id) ){
                    if ( data.get(i)[qEvidNum].equals(evidNum) && data.get(i)[qToPlace].equals("") ) {
                        if (Integer.parseInt(data.get(i)[qCurrent]) > 0) {
                            if (vp.getAdapter().getPageTitle(vp.getCurrentItem()) != null) {
                                data.get(i)[qCurrent] = String.valueOf(Integer.parseInt(data.get(i)[qCurrent]) - 1);
                                List<String[]> dataList = AllLinesData.findItemsFromMap(evidNum, qEvidNum);
                                if (dataList != null && dataList.size() > 0) {
                                    for (int j = 0; j < dataList.size(); j++) {
                                        if (dataList.get(j)[qRefLineId].equals(id) && dataList.get(j)[qToPlace].equals(vpTitle)) {
                                            newId = dataList.get(j)[0];
                                        }
                                    }
                                }
                                if (newId.equals("")) {
                                    Random r = new Random();
                                    newId = String.valueOf(-1 * Long.parseLong(data.get(i)[0]) + 1000000000 + (r.nextInt(900000 - 100000) + 100000));
                                }
                                if (AllLinesData.getParam(newId) == null) {
                                    String[] newRow = new String[data.get(i).length];
                                    for (int x = 0; x < data.get(i).length; x++) {
                                        newRow[x] = data.get(i)[x];
                                    }
                                    newRow[qToPlace] = vpTitle;
                                    newRow[qCurrent] = "1";
                                    newRow[qNeed] = "0";
                                    newRow[0] = newId;
                                    if (qWeight != -1)
                                        newRow[qWeight] = String.valueOf( Math.round(Float.parseFloat(data.get(i)[qWeight]) / (Float.parseFloat(data.get(i)[qNeed]) + Float.parseFloat(data.get(i)[qMissing] ))) );
                                    newRow[qRefLineId] = data.get(i)[0];
                                    AllLinesData.setParam(newId, newRow);
                                } else {
                                    String[] selectData = AllLinesData.getParam(newId);
                                    String[] newRow = new String[selectData.length];
                                    for (int x = 0; x < selectData.length; x++) {
                                        newRow[x] = selectData[x];
                                    }
                                    newRow[qCurrent] = String.valueOf(Integer.parseInt(newRow[qCurrent]) + 1);
                                    newRow[qToPlace] = vp.getAdapter().getPageTitle(vp.getCurrentItem()).toString();
                                    newRow[qNeed] = "0";
                                    if (qWeight != -1)
                                        newRow[qWeight] = String.valueOf( Math.round(Float.parseFloat(data.get(i)[qWeight]) / (Float.parseFloat(data.get(i)[qNeed]) + Float.parseFloat(data.get(i)[qMissing])) * Float.parseFloat(newRow[qCurrent])));
                                    AllLinesData.setParam(newId, newRow);
                                }
                                if (InsertedList.getInsertElement(newId) == null) {
                                    InsertedList.setInsertElement(newId, "0");
                                }
                                CheckedList.setParamItem(data.get(i)[0], 1);
                                AllLinesData.toStringLog();
                                refreshPlace();
                                SaveAllSessionTemp sst = new SaveAllSessionTemp(getContext());
                                sst.start();
                            } else {
                                HelperClass.messageBox(getActivity(), "CrossDock", "Nincs rakhely kiválasztva!", HelperClass.ERROR);
                            }
                            itOK = true;
                            break;
                        }
                    }
                }
            }

            if (!itOK) {
                if( !barcode.isEmpty() ) {
                    if (AllLinesData.isValidateValue(qBarcode, barcode)) {
                        List<String[]> list = AllLinesData.findItemsFromMap(evidNum, qEvidNum);
                        boolean hereItIs = false;
                        for (int x = 0; x < list.size(); x++) {
                            if (list.get(x)[qBarcode].equals(barcode)) hereItIs = true;
                        }
                        if (hereItIs) {
                            HelperClass.messageBox(getActivity(), "CrossDock", "Nincs több ebből a termékből!", HelperClass.WARNING);
                        } else {
                            HelperClass.messageBox(getActivity(), "CrossDock", "Ez a termék nem ehhez az evidenciához tartozik!", HelperClass.ERROR);
                        }
                    } else {
                        HelperClass.messageBox(getActivity(), "CrossDock", "Nem létező termékkód!", HelperClass.ERROR);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void chkBarcode_add(){
        String SelID = SessionClass.getParam("XD_CHECKED");
        String[] sData = AllLinesData.getParam(SelID);
        if( vp.getAdapter().getCount() > 0 ) {
            boolean itOK = false;
            trimmed = false;
            String vpTitle = vp.getAdapter().getPageTitle(vp.getCurrentItem()).toString();
            List<String[]> data = AllLinesData.findItemsFromMap(sData[qRefLineId], qSelID);
            String newId = "";
            try {
                for (int i = 0; i < data.size(); i++) {
                    if ( data.get(i)[qEvidNum].equals(evidNum) && data.get(i)[qToPlace].equals("") ) {
                        if (Integer.parseInt(data.get(i)[qCurrent]) > 0) {
                            if (vp.getAdapter().getPageTitle(vp.getCurrentItem()) != null) {
                                data.get(i)[qCurrent] = String.valueOf(Integer.parseInt(data.get(i)[qCurrent]) - 1);
                                List<String[]> dataList = AllLinesData.findItemsFromMap(evidNum, qEvidNum);
                                if (dataList != null && dataList.size() > 0) {
                                    for (int j = 0; j < dataList.size(); j++) {
                                        if (dataList.get(j)[qSelID].equals(SelID) && dataList.get(j)[qToPlace].equals(vpTitle)) {
                                            newId = dataList.get(j)[0];
                                        }
                                    }
                                }
                                if (newId.equals("")) {
                                    Random r = new Random();
                                    newId = String.valueOf(-1 * Long.parseLong(data.get(i)[0]) + 1000000000 + (r.nextInt(900000 - 100000) + 100000));
                                }
                                if (AllLinesData.getParam(newId) == null) {
                                    String[] newRow = new String[data.get(i).length];
                                    for (int x = 0; x < data.get(i).length; x++) {
                                        newRow[x] = data.get(i)[x];
                                    }
                                    newRow[qToPlace] = vpTitle;
                                    newRow[qCurrent] = "1";
                                    newRow[qNeed] = "0";
                                    newRow[0] = newId;
                                    if(qWeight != -1) newRow[qWeight] = String.valueOf( Math.round( Float.parseFloat(data.get(i)[qWeight]) / (Float.parseFloat(data.get(i)[qNeed]) + Float.parseFloat(data.get(i)[qMissing]) )));
                                    newRow[qRefLineId] = data.get(i)[0];
                                    AllLinesData.setParam(newId, newRow);
                                } else {
                                    String[] selectData = AllLinesData.getParam(newId);
                                    String[] newRow = new String[selectData.length];
                                    for (int x = 0; x < selectData.length; x++) {
                                        newRow[x] = selectData[x];
                                    }
                                    newRow[qCurrent] = String.valueOf(Integer.parseInt(newRow[qCurrent]) + 1);
                                    newRow[qToPlace] = vp.getAdapter().getPageTitle(vp.getCurrentItem()).toString();
                                    newRow[qNeed] = "0";
                                    if(qWeight != -1) newRow[qWeight] = String.valueOf( Math.round( Float.parseFloat(data.get(i)[qWeight]) / (Float.parseFloat(data.get(i)[qNeed]) + Float.parseFloat(data.get(i)[qMissing]) ) * Float.parseFloat(newRow[qCurrent])));
                                    AllLinesData.setParam(newId, newRow);
                                }
                                if( InsertedList.getInsertElement(newId) == null ) {
                                    InsertedList.setInsertElement(newId, "0");
                                }
                                CheckedList.setParamItem( data.get(i)[0],1);
                                AllLinesData.toStringLog();
                                refreshPlace();
                                SaveAllSessionTemp sst = new SaveAllSessionTemp(getContext());
                                sst.start();
                            } else {
                                HelperClass.messageBox(getActivity(),"CrossDock","Nincs rakhely kiválasztva!", HelperClass.ERROR);
                            }
                            itOK = true;
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            HelperClass.messageBox(getActivity(),"CrossDock","Nincs rakhely definiálva!",HelperClass.ERROR);
            //Toast.makeText(getContext(), "Nincs rakhely definiálva!", Toast.LENGTH_LONG).show();
        }
    }

    private void chkBarcode_minus(){
        if( SessionClass.getParam("XD_CHECKED") != null && !SessionClass.getParam("XD_CHECKED").equals("") && !SessionClass.getParam("XD_CHECKED").equals("0")) {
            String SelID = SessionClass.getParam("XD_CHECKED");
            Log.i("TAG","SELID: "+SelID);
            String[] sData = AllLinesData.getParam(SelID);
            if (vp.getAdapter().getCount() > 0) {
                boolean itOK = false;
                trimmed = false;
                String vpTitle = vp.getAdapter().getPageTitle(vp.getCurrentItem()).toString();
                List<String[]> data = AllLinesData.findItemsFromMap(sData[qRefLineId], qSelID);
                String newId = "";
                try {
                    for (int i = 0; i < data.size(); i++) {
                        if (data.get(i)[qEvidNum].equals(evidNum) && data.get(i)[qToPlace].equals("")) {
                            if (vp.getAdapter().getPageTitle(vp.getCurrentItem()) != null) {
                                data.get(i)[qCurrent] = String.valueOf(Integer.parseInt(data.get(i)[qCurrent]) + 1);
                                List<String[]> dataList = AllLinesData.findItemsFromMap(evidNum, qEvidNum);
                                if (dataList != null && dataList.size() > 0) {
                                    for (int j = 0; j < dataList.size(); j++) {
                                        if (dataList.get(j)[qSelID].equals(SelID) && dataList.get(j)[qToPlace].equals(vpTitle)) {
                                            newId = dataList.get(j)[0];
                                        }
                                    }
                                }
                                if (newId.equals("")) {
                                    Random r = new Random();
                                    newId = String.valueOf(-1 * Long.parseLong(data.get(i)[0]) + 1000000000 + (r.nextInt(900000 - 100000) + 100000));
                                }
                                String[] selectData = AllLinesData.getParam(newId);
                                String[] newRow = new String[selectData.length];
                                for (int x = 0; x < selectData.length; x++) {
                                    newRow[x] = selectData[x];
                                }
                                newRow[qCurrent] = String.valueOf(Integer.parseInt(newRow[qCurrent]) - 1);
                                newRow[qToPlace] = vp.getAdapter().getPageTitle(vp.getCurrentItem()).toString();
                                newRow[qNeed] = "0";
                                if (qWeight != -1)
                                    newRow[qWeight] = String.valueOf(Math.round(Float.parseFloat(data.get(i)[qWeight]) / (Float.parseFloat(data.get(i)[qNeed]) + Float.parseFloat(data.get(i)[qMissing])) * Float.parseFloat(newRow[qCurrent])));
                                if (newRow[qCurrent].equals("0")) {
                                    data.get(i)[qCurrent] = String.valueOf(Integer.parseInt(data.get(i)[qCurrent]) - 1);
                                    break;
                                }
                                AllLinesData.setParam(newId, newRow);
                                if (InsertedList.getInsertElement(newId) == null) {
                                    InsertedList.setInsertElement(newId, "0");
                                }
                                CheckedList.setParamItem(data.get(i)[0], 1);
                                AllLinesData.toStringLog();
                                refreshPlace();
                                SaveAllSessionTemp sst = new SaveAllSessionTemp(getContext());
                                sst.start();
                            } else {
                                HelperClass.messageBox(getActivity(), "CrossDock", "Nincs rakhely kiválasztva!", HelperClass.ERROR);
                            }
                            itOK = true;
                            break;

                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                HelperClass.messageBox(getActivity(), "CrossDock", "Nincs rakhely definiálva!", HelperClass.ERROR);
                //Toast.makeText(getContext(), "Nincs rakhely definiálva!", Toast.LENGTH_LONG).show();
            }
        }
    }
    public void updateTopItems(){
        ((XD_ItemsParametersListAdapter)itemsListContainer.getAdapter()).update("");
        refreshPlace();
        //Toast.makeText(getContext(),"UpdateTopItems",Toast.LENGTH_LONG).show();
    }

    public void setViewPagerElement(){
        Map<String,String> placeNums = AllLinesData.getItemsCol(qToPlace, qEvidNum, evidNum);
        for( Map.Entry<String,String> entry : placeNums.entrySet() ) {
            XD_PlacenumberFragment newFrg = XD_PlacenumberFragment.newInstance(entry.getKey());
            newFrg.setParentFragment(myFrag);
            ((XD_PlacenumberPagerAdapter) vp.getAdapter()).update(newFrg, entry.getKey());
            spl.setViewPager(vp);
        }
    }

    void createLineDialog() {
        if( !SessionClass.getParam("XD_CHECKED2").equals("") ) {
            if ( !InsertedList.isInsert(SessionClass.getParam("XD_CHECKED2")) ) {
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
                            if( (Integer.valueOf( AllLinesData.getParam(SessionClass.getParam("XD_CHECKED2"))[qNeed]) - 1 ) > Integer.valueOf(num.getText().toString()) ) {
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
                        map.put("WRHS_L_ID", SessionClass.getParam("XD_CHECKED2"));
                        map.put("PCS",num.getText().toString());
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
                                            String[] baseLine = AllLinesData.getParam( SessionClass.getParam("XD_CHECKED2") );
                                            String[] newLine = new String[baseLine.length];
                                            for(int i = 0; i < baseLine.length; i++){
                                                newLine[i] = baseLine[i];
                                            }
                                            newLine[0] = newID;
                                            newLine[qNeed] = num.getText().toString();
                                            if( !baseLine[qCurrent].equals("0")){ newLine[qCurrent] = num.getText().toString(); } else { newLine[qCurrent] = "0"; }
                                            //newLine[qBarcode] = newID;
                                            Float f = Float.parseFloat( newLine[qNeed] ) / ( Float.parseFloat(baseLine[qNeed]) + Float.parseFloat(baseLine[qMissing]) );
                                            if( !baseLine[qCurrent].equals("0")){ baseLine[qCurrent] = String.valueOf( Integer.parseInt(baseLine[qCurrent]) - Integer.parseInt(num.getText().toString()) ); }
                                            baseLine[qNeed] = String.valueOf( Integer.parseInt(baseLine[qNeed]) - Integer.parseInt(num.getText().toString()) );
                                            baseLine[qWeight] = String.valueOf( Math.round( Float.parseFloat(baseLine[qWeight]) * (1-f) ));
                                            newLine[qWeight] = String.valueOf( Math.round( Float.parseFloat(newLine[qWeight]) * f) );
                                            AllLinesData.setParam(newID, newLine);
                                            ((XD_ItemsParametersListAdapter) itemsListContainer.getAdapter()).update("");
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
                moveSelectedItems(SessionClass.getParam("XD_CHECKED3"), barcode, data);
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

}