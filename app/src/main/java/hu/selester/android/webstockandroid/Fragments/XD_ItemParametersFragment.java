package hu.selester.android.webstockandroid.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import hu.selester.android.webstockandroid.Adapters.XD_PlacenumberPagerAdapter;
import hu.selester.android.webstockandroid.Adapters.XD_ItemsParametersListAdapter;
import hu.selester.android.webstockandroid.Database.SelesterDatabase;
import hu.selester.android.webstockandroid.Divider.SimpleDividerItemDecoration;
import hu.selester.android.webstockandroid.Helper.HelperClass;
import hu.selester.android.webstockandroid.Helper.KeyboardUtils;
import hu.selester.android.webstockandroid.Helper.SlidingTabLayout;
import hu.selester.android.webstockandroid.Objects.AllLinesData;
import hu.selester.android.webstockandroid.Objects.CheckedList;
import hu.selester.android.webstockandroid.Objects.DefaultTextWatcher;
import hu.selester.android.webstockandroid.Objects.InsertedList;
import hu.selester.android.webstockandroid.Objects.SessionClass;
import hu.selester.android.webstockandroid.Objects.XD_ItemsParameters;
import hu.selester.android.webstockandroid.R;
import hu.selester.android.webstockandroid.Threads.SaveAllSessionTemp;
import mobil.selester.wheditbox.WHEditBox;

public class XD_ItemParametersFragment extends Fragment {

    private View rootView;
    private TextView txt;
    private RecyclerView itemsListContainer;
    private ViewPager vp;
    private SlidingTabLayout spl;
    private TextInputEditText placeNumberTV;
    private int qEvidNum, qNeed, qCurrent, qWeight, qWidth, qHeight, qLength, qBarcode, qToPlace, qRefLineId;
    private String tranCode;
    private String tranID, evidNum;
    private WHEditBox edit;
    private XD_ItemParametersFragment myFrag;
    private Button chkBtn;
    private SelesterDatabase db;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myFrag = this;
        rootView = inflater.inflate(R.layout.frg_itemparameters, container, false);
        db = SelesterDatabase.getDatabase(getContext());
        tranCode = SessionClass.getParam("tranCode");
        qBarcode = HelperClass.getArrayPosition("Barcode", SessionClass.getParam(tranCode + "_Line_ListView_SELECT"));
        qEvidNum = HelperClass.getArrayPosition("EvidNum", SessionClass.getParam(tranCode + "_Line_ListView_SELECT"));
        qNeed = HelperClass.getArrayPosition("Needed_Qty", SessionClass.getParam(tranCode + "_Line_ListView_SELECT"));
        qCurrent = HelperClass.getArrayPosition("Current_Qty", SessionClass.getParam(tranCode + "_Line_ListView_SELECT"));
        qWeight  = HelperClass.getArrayPosition("Weight", SessionClass.getParam(tranCode + "_Line_ListView_SELECT"));
        qWidth   = HelperClass.getArrayPosition("Size_Width", SessionClass.getParam(tranCode + "_Line_ListView_SELECT"));
        qHeight  = HelperClass.getArrayPosition("Size_Height", SessionClass.getParam(tranCode + "_Line_ListView_SELECT"));
        qLength  = HelperClass.getArrayPosition("Size_Length", SessionClass.getParam(tranCode + "_Line_ListView_SELECT"));
        qToPlace  = HelperClass.getArrayPosition("To_Place", SessionClass.getParam(tranCode + "_Line_ListView_SELECT"));
        qRefLineId = HelperClass.getArrayPosition("Ref_Line_ID", SessionClass.getParam(tranCode + "_Line_ListView_SELECT"));
        evidNum = getArguments().getString("evidNum");
        List<XD_PlacenumberFragment> frgList = new ArrayList<>();
        List<String> pageTitleList = new ArrayList<>();

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
                chkBarcode();
            }
        });
        edit = rootView.findViewById(R.id.ps_header_value);
        edit.activity = getActivity();
        edit.setDialogTitle("Barcode");
        edit.setOnDetectBarcodeListener(new WHEditBox.OnDetectBarcodeListener() {
            @Override
            public void OnDetectBarcode() {
                chkBarcode();
            }
        });
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
                        Log.i("TAG", vp.getAdapter().getPageTitle(x) + " - " + placeNumberTV.getText());
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
            //Toast.makeText(getContext(),"Nincs rakhely meghatározva!",Toast.LENGTH_LONG).show();
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
                    itemsList.add(new XD_ItemsParameters(Long.parseLong(dataList.get(i)[0]),Integer.parseInt(dataList.get(i)[qNeed]), Integer.parseInt(dataList.get(i)[qCurrent]), Float.parseFloat(dataList.get(i)[qWeight]), Float.parseFloat(dataList.get(i)[qLength]), Float.parseFloat(dataList.get(i)[qWidth]), Float.parseFloat(dataList.get(i)[qHeight])));
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
/*        if( vp.getAdapter().getCount() > 0) {
            (((XD_PlacenumberPagerAdapter) vp.getAdapter()).getItem(vp.getCurrentItem())).update();
            ((XD_ItemsParametersListAdapter) itemsListContainer.getAdapter()).update("");
        }*/
    }

    private void chkBarcode(){
        if( vp.getAdapter().getCount() > 0 ) {
            boolean itOK = false;
            String barcode = edit.EDText.getText().toString();
            String vpTitle = vp.getAdapter().getPageTitle(vp.getCurrentItem()).toString();
            List<String[]> data = AllLinesData.findItemsFromMap(barcode, qBarcode);
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
                                        if (dataList.get(j)[qBarcode].equals(barcode) && dataList.get(j)[qToPlace].equals(vpTitle)) {
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
                                //Toast.makeText(getContext(), "Nincs rakhely kiválasztva!", Toast.LENGTH_LONG).show();
                            }
                            itOK = true;
                            break;
                        }
                    }
                }
                if (!itOK) {
                    if( AllLinesData.isValidateValue(qBarcode, barcode) ){
                        List<String[]> list = AllLinesData.findItemsFromMap(evidNum, qEvidNum);
                        boolean hereItIs = false;
                        for (int x = 0; x < list.size(); x++ ){
                            if( list.get(x)[qBarcode].equals(barcode) ) hereItIs = true;
                        }
                        if( hereItIs ){
                            HelperClass.messageBox(getActivity(),"CrossDock","Nincs több ebből a termékből!", HelperClass.WARNING);
                        }else{
                            HelperClass.messageBox(getActivity(),"CrossDock","Ez a termék nem ehhez az evidenciához tartozik!", HelperClass.ERROR);
                        }
                    } else {
                        HelperClass.messageBox(getActivity(),"CrossDock","Nem létező termékkód!", HelperClass.ERROR);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            HelperClass.messageBox(getActivity(),"CrossDock","Nincs rakhely definiálva!",HelperClass.ERROR);
            //Toast.makeText(getContext(), "Nincs rakhely definiálva!", Toast.LENGTH_LONG).show();
        }
        edit.EDText.setText("");
    }

    public void updateTopItems(){
        ((XD_ItemsParametersListAdapter)itemsListContainer.getAdapter()).update("");
        refreshPlace();
        Toast.makeText(getContext(),"UpdateTopItems",Toast.LENGTH_LONG).show();
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

}