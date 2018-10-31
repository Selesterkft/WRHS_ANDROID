package hu.selester.android.webstockandroid.Fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import hu.selester.android.webstockandroid.Adapters.CheckPlaceAdapter;
import hu.selester.android.webstockandroid.Adapters.TableViewPagerAdapter;
import hu.selester.android.webstockandroid.Database.SelesterDatabase;
import hu.selester.android.webstockandroid.Helper.HelperClass;
import hu.selester.android.webstockandroid.Helper.KeyboardUtils;
import hu.selester.android.webstockandroid.Helper.MySingleton;
import hu.selester.android.webstockandroid.Objects.AllLinesData;
import hu.selester.android.webstockandroid.Objects.SessionClass;
import hu.selester.android.webstockandroid.Objects.WHItem;
import hu.selester.android.webstockandroid.R;
import hu.selester.android.webstockandroid.Threads.CheckPlaceSaveThread;

public class CheckPlaceFragment extends Fragment {

    private EditText findValue;
    private EditText findValueEAN;
    private ProgressDialog pd;
    private String isBar;
    private String selectPlaceID = "";
    private String[] columnName, headerText;
    private int[] headerWidth;
    private View rootView;
    private List<String[]> dataList;
    private List<WHItem> whList;
    private Button whBtn;
    private WHItem selectWHItem = null;
    private LinearLayout queryPanel2, queryPanel3;
    private SelesterDatabase db;
    private FragmentPagerAdapter fragmentPagerAdapter;
    private final int numOfPages = 3;
    private final String[] pageTitle = {"Hiány", "Megfelelő", "Többlet"};
    private ViewPager vp;
    private FragmentManager fm;
    private String taskid;


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fm = getActivity().getSupportFragmentManager();
        rootView = inflater.inflate(R.layout.frg_checkplace,container,false);
        db = SelesterDatabase.getDatabase(getContext());
        AllLinesData.delParams();
        setDefaultData();
        Random r = new Random();
        int i1 = r.nextInt(9999 - 1000) + 1000;
        taskid = SessionClass.getParam("terminal")+i1;
        TextView workNum = rootView.findViewById(R.id.checkplace_worknum);
        workNum.setText("Munkaszám: "+taskid);
        TabLayout tabLayout = rootView.findViewById(R.id.checkplace_tablayout);
        for (int i = 0; i < numOfPages; i++) {
            tabLayout.addTab(tabLayout.newTab().setText(pageTitle[i]));
        }
        TextView titleTV = rootView.findViewById(R.id.checkplace_title);
        titleTV.setText("Tárhely ellenőrzés");
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        vp = rootView.findViewById(R.id.checkplace_viewpager);
        fragmentPagerAdapter = new TableViewPagerAdapter(getFragmentManager());
        vp.setAdapter(fragmentPagerAdapter);
        vp.setCurrentItem(0);
        vp.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(onTabSelectedListener(vp));

        queryPanel2 = rootView.findViewById(R.id.checkplace_query_panel2);
        queryPanel3 = rootView.findViewById(R.id.checkplace_query_panel3);
        queryPanel2.setVisibility(View.GONE);
        queryPanel3.setVisibility(View.GONE);
        findValue = rootView.findViewById(R.id.checkplace_header_value);
        findValue.requestFocus();
        findValue.addTextChangedListener(textWatcher);

        findValueEAN = rootView.findViewById(R.id.checkplace_EAN_header_value);
        findValueEAN.addTextChangedListener(textWatcherEAN);
        Button findValueBtnEAN = rootView.findViewById(R.id.checkplace_EAN_header_btn);
        findValueBtnEAN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItemCount(findValueEAN.getText().toString());
            }
        });

        ImageButton saveBtn = rootView.findViewById(R.id.checkplace_saveBtn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckPlaceSaveThread cpst = new CheckPlaceSaveThread(getContext(),selectWHItem.getId(), selectPlaceID, taskid);
                cpst.start();
            }
        });

        Button delTextBtn = rootView.findViewById(R.id.checkplace_header_delbtn);
        whBtn = rootView.findViewById(R.id.checkplace_wh_header_btn);
        whBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadWHList();
            }
        });
        Button findValueBtn = rootView.findViewById(R.id.checkplace_header_btn);
        findValueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPlaceID = findValue.getText().toString();
                loadData(findValue.getText().toString());
            }
        });
        delTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KeyboardUtils.hideKeyboard(getActivity());
                findValue.setText("");
            }
        });
        ImageButton flushBtn = rootView.findViewById(R.id.checkplace_flushBtn);
        flushBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KeyboardUtils.hideKeyboard(getActivity());

                List<Fragment> fragments = getFragmentManager().getFragments();
                if (fragments != null) {
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    for (Fragment f : fragments) {
                        //You can perform additional check to remove some (not all) fragments:
                        if (f instanceof TableListFragment) {
                            ft.remove(f);
                        }
                    }
                    ft.commitAllowingStateLoss();
                }
                getFragmentManager().popBackStack();
            }
        });

        return rootView;
    }

    private TabLayout.OnTabSelectedListener onTabSelectedListener(final ViewPager pager) {
        return new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                pager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        };
    }

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            isBar = HelperClass.isBarcode(findValue.getText().toString());
            if( isBar != null ){
                Log.i("TAG", isBar);
                loadData(isBar);
                selectPlaceID = isBar;
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    TextWatcher textWatcherEAN = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(!findValueEAN.getText().toString().isEmpty()) {
                isBar = HelperClass.isBarcode(findValueEAN.getText().toString());
                if (isBar != null) {
                    Log.i("TAG", isBar);
                    addItemCount(isBar);
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

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
                Log.i("TAG","ERROR");
            }else{
                if(findValue.isFocused()) {
                    findValue.setText(result.getContents() + SessionClass.getParam("barcodeSuffix"));
                }else if(findValueEAN.isFocused()) {
                    findValueEAN.setText(result.getContents() + SessionClass.getParam("barcodeSuffix"));
                }
            }
        }else{
            super.onActivityResult(requestCode, resultCode, data);
        }

    }

    private void loadData(String locsname){
        if( selectWHItem!=null ) {
            if( !locsname.isEmpty() ) {
                findValue.setText("");
                pd = HelperClass.loadingDialogOn(getActivity());
                AllLinesData.delParams();
                KeyboardUtils.hideKeyboard(getActivity());
                String terminal = SessionClass.getParam("terminal");
                String url = SessionClass.getParam("WSUrl") + "/randominventory/" + locsname + "/" + terminal + "/" + selectWHItem.getId();
                Log.i("URL", url);
                RequestQueue rq = MySingleton.getInstance(getContext()).getRequestQueue();
                JSONObject jsonObject = null;
                JsonRequest<JSONObject> jr = new JsonObjectRequest(Request.Method.GET, url, jsonObject, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String rootText = response.getString("randominventoryResult");
                            JSONArray jsonArray = new JSONArray(rootText);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObj = jsonArray.getJSONObject(i);
                                String[] dataText = new String[columnName.length];
                                for (int j = 0; j < columnName.length; j++) {
                                    try {
                                        if(columnName[j].equals("ek")){
                                            dataText[j] = String.valueOf(Integer.parseInt(dataText[j-2]) - Integer.parseInt(dataText[j-1]) );
                                        } else if(columnName[j].equals("t")){
                                            dataText[j]="0";
                                        } else {
                                            String value = jsonObj.getString(columnName[j]);
                                            if (!value.equals("null")) {
                                                dataText[j] = value;
                                            } else {
                                                dataText[j] = "";
                                            }
                                        }
                                    } catch (Exception e) {
                                        dataText[j] = "";
                                    }

                                }
                                AllLinesData.setParam(dataText[0],dataText);
                            }
                            queryPanel3.setVisibility(View.VISIBLE);
                            findValueEAN.requestFocus();
                            ((TableListFragment)((TableViewPagerAdapter)vp.getAdapter()).getItem(0)).createNewTable();
                            ((TableListFragment)((TableViewPagerAdapter)vp.getAdapter()).getItem(1)).createNewTable();
                            ((TableListFragment)((TableViewPagerAdapter)vp.getAdapter()).getItem(2)).createNewTable();
                            AllLinesData.toStringLog();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        pd.dismiss();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error != null) {
                            error.printStackTrace();
                        }
                        pd.dismiss();
                    }
                });
                rq.add(jr);
            }
        }else{
            Toast.makeText(getContext(),"Nincs kiválasztva a raktár!",Toast.LENGTH_LONG).show();
        }
    }


    private void loadWHList(){
        String url = SessionClass.getParam("WSUrl") + "/warehouses";
        Log.i("URL", url);
        RequestQueue rq = MySingleton.getInstance(getContext()).getRequestQueue();
        JSONObject jsonObject = null;
        whList = new ArrayList<WHItem>();
        JsonRequest<JSONObject> jr = new JsonObjectRequest(Request.Method.GET, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String rootText = response.getString("warehousesResult");
                    JSONArray jsonArray = new JSONArray(rootText);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObj = jsonArray.getJSONObject(i);
                        whList.add(new WHItem(jsonObj.getInt("ID"), jsonObj.getString("CompanyName1")));
                    }
                    whPickerDialog();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error != null) {
                    error.printStackTrace();
                }
            }
        });
        rq.add(jr);
    }

    public void whPickerDialog_new() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(getContext());
        builderSingle.setTitle("Válasszon raktárat");
        View rootView = getLayoutInflater().inflate(R.layout.dialog_searchlist, null, false);
        builderSingle.setView(rootView);
        final CheckPlaceAdapter cpa = new CheckPlaceAdapter(getContext(), whList);
        ListView lv = rootView.findViewById(R.id.dialog_searchlist_listview);
        lv.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                whBtn.setText(cpa.getItem(position).getName());
                selectWHItem = whList.get(position);
                queryPanel2.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        builderSingle.setNegativeButton("bezárás", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
/*
        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });*/
        builderSingle.show();
        KeyboardUtils.hideKeyboard(getActivity());
    }

    public void whPickerDialog() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(getContext());
        builderSingle.setTitle("Válasszon raktárat");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_selectable_list_item);
        for(int i=0; i<whList.size(); i++){
            arrayAdapter.add(whList.get(i).getName());
        }

        builderSingle.setNegativeButton("bezárás", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                whBtn.setText(arrayAdapter.getItem(which));
                selectWHItem = whList.get(which);
                queryPanel2.setVisibility(View.VISIBLE);

                dialog.dismiss();
            }
        });
        builderSingle.show();
    }

    private void addItemCount(String isBar) {
        try {
            KeyboardUtils.hideKeyboard(getActivity());
            findValueEAN.setText("");
            String ean = db.productDataDAO().getBarcodeProd(isBar);
            if (ean != null && !ean.isEmpty()) {
                Log.i("TAG", ean);
                String[] datas = AllLinesData.getParam(ean);
                if(datas!=null){
                    if (datas[4].isEmpty()) {
                        datas[4] = "0";
                    } else {
                        datas[4] = String.valueOf( Integer.parseInt(datas[4])+1 );
                    }
                }else{
                    datas = new String[6];
                    datas[0] = ean;
                    datas[1] = "";
                    datas[2] = "";
                    datas[3] = "";
                    datas[4] = "1";
                    datas[5] = "";
                    AllLinesData.setParam(ean, datas);
                }
                //AllLinesData.setParam(ean,datas);
                ((TableListFragment)((TableViewPagerAdapter)vp.getAdapter()).getItem(0)).refreshDataList();
                ((TableListFragment)((TableViewPagerAdapter)vp.getAdapter()).getItem(1)).refreshDataList();
                ((TableListFragment)((TableViewPagerAdapter)vp.getAdapter()).getItem(2)).refreshDataList();
            } else {
                buildNonEANDialog();
            }
        }catch (Exception e){
            e.printStackTrace();
            //Toast.makeText(getContext(),"Hiba a program működésében!",Toast.LENGTH_LONG).show();
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
    }

    void buildNonEANDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Hibás vonalkód!");
        builder.setMessage("Nincs ilyen vonalkód felvéve a rendszerbe!");

        builder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                db.sessionTempDao().deleteAllData();
            }
        });

        builder.show();

    }

}