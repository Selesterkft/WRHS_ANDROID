package hu.selester.android.webstockandroid.Fragments;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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

import org.apache.commons.collections.map.HashedMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import hu.selester.android.webstockandroid.Database.Tables.SessionTemp;
import hu.selester.android.webstockandroid.Helper.HelperClass;
import hu.selester.android.webstockandroid.Helper.KeyboardUtils;
import hu.selester.android.webstockandroid.Helper.MySingleton;
import hu.selester.android.webstockandroid.Objects.CheckedList;
import hu.selester.android.webstockandroid.Objects.SessionClass;
import hu.selester.android.webstockandroid.Objects.WHItem;
import hu.selester.android.webstockandroid.R;

public class InventoryFragment extends Fragment {

    private Map<Integer, String> dataList;
    private TextView whIDTV, renterTV, virtwhTV, itemNameTV;
    private List<WHItem> whList;
    private String[] WHTexts, virtWHTexts, renterTexts;
    private Integer[] WHIds, virtWHIds, renterIds;
    private int selectWHID, selectvirtWHID, selectRenterID;
    private EditText placeIdET, palET, itemIdET, countET, lotET, expET;
    private TextView counterTV;
    private int itemCounter;
    private boolean WHListLoaded = false;
    private String isBar;
    private Button nextBtn1, nextBtn2;
    private LinearLayout group1, group2;
    private String[] params;
    private View rootView;
    private String Locks_ID, WrhsRenters_ID, OUT_WrhsGoods_ID, OUT_WrhsGoods_ItemNum, OUT_WrhsGoods_Name, FieldParams_TextBox_MHD, expDate ;
    private int mYear, mMonth, mDay;

    // Store temp data split pipline
    private String Vector_Paletta_ParamStr, Vector_EinlagererID_ParamStr, Vector_ArtikelID_ParamStr, Vector_Anz_ParamStr, Vector_ProdFeld1_ParamStr, Vector_MHD_ParamStr, Vector_VirtualLagerID_ParamStr;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.frg_inventory, container, false);
        FieldParams_TextBox_MHD = "";
        OUT_WrhsGoods_ID = "";
        Locks_ID = "";
        expDate = "";
        loadWHData();
        loadGetParams();
        loadRenterData();
        params = new String[5];

        counterTV = rootView.findViewById(R.id.inventory_counter);
        whIDTV = rootView.findViewById(R.id.inventory_text1);
        placeIdET = rootView.findViewById(R.id.inventory_text2);
        palET = rootView.findViewById(R.id.inventory_text3);
        renterTV = rootView.findViewById(R.id.inventory_text4);
        itemIdET = rootView.findViewById(R.id.inventory_text5);
        countET = rootView.findViewById(R.id.inventory_text6);
        lotET = rootView.findViewById(R.id.inventory_text7);
        expET = rootView.findViewById(R.id.inventory_text8);
        virtwhTV = rootView.findViewById(R.id.inventory_text9);
        nextBtn1 = rootView.findViewById(R.id.inventory_text1_btn);
        nextBtn2 = rootView.findViewById(R.id.inventory_text2_btn);
        nextBtn2 = rootView.findViewById(R.id.inventory_text2_btn);
        itemNameTV = rootView.findViewById(R.id.inventory_itemname);
        group1 = rootView.findViewById(R.id.inventory_group1);
        group2 = rootView.findViewById(R.id.inventory_group2);

        expET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker();
            }
        });

        whIDTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nextBtn1.getVisibility() == View.VISIBLE) loadWHList();
            }
        });

        ImageButton clearBtn = rootView.findViewById(R.id.inventory_clearBtn);
        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearData();
            }
        });

        ImageButton addBtn = rootView.findViewById(R.id.inventory_addBtn);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTempDatas();
                clearData();
            }
        });

        ImageButton flushBtn = rootView.findViewById(R.id.inventory_flushBtn);
        flushBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KeyboardUtils.hideKeyboard(getActivity());
                getFragmentManager().popBackStack();
            }
        });

        ImageButton saveBtn = rootView.findViewById(R.id.inventory_saveBtn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
            }
        });

        placeIdET.addTextChangedListener(placeIdTW);
        itemIdET.addTextChangedListener(itemIdTW);

        virtwhTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadVirtWHList();
            }
        });

        renterTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nextBtn2.getVisibility() == View.VISIBLE) loadRenterList();
            }
        });

        nextBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadData1();
            }
        });
        nextBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadData2();
            }
        });
        refreshData();
        return rootView;
    }

    private void addTempDatas() {
        Vector_Paletta_ParamStr         += palET.getText().toString()+"|";
        Vector_EinlagererID_ParamStr    += selectRenterID + "|";
        Vector_ArtikelID_ParamStr       += OUT_WrhsGoods_ID + "|";
        Vector_Anz_ParamStr             += countET.getText().toString() + "|";
        Vector_ProdFeld1_ParamStr       += lotET.getText().toString() + "|";
        Vector_MHD_ParamStr             += expDate + "|";
        Vector_VirtualLagerID_ParamStr  += selectvirtWHID+"|";
        itemCounter++;
        counterTV.setText("Tételek: "+itemCounter);

    }

    private void clearData() {
        selectRenterID = 0;
        selectvirtWHID = 0;
        FieldParams_TextBox_MHD = "";
        OUT_WrhsGoods_ID = "";
        expDate = "";

        group1.setVisibility(View.VISIBLE);
        group2.setVisibility(View.GONE);
        nextBtn1.setVisibility(View.VISIBLE);
        nextBtn2.setVisibility(View.VISIBLE);
        itemIdET.setEnabled(true);
        palET.setText("");
        renterTV.setText("Válasszon bérlőt...");
        itemIdET.setText("");
        countET.setText("");
        lotET.setText("");
        expET.setText("");
        virtwhTV.setText("Válasszon vir. raktárat...");
        itemNameTV.setText("");
        itemIdET.requestFocus();
    }


    private void datePicker() {
        final Calendar c = Calendar.getInstance();
        if (expET.getText().toString().isEmpty()) {
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);
        } else {
            String[] dateStr = expET.getText().toString().split(".");
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String dm, dd;
                        if( dayOfMonth < 10 ) dd = "0" + dayOfMonth; else dd = ""+dayOfMonth;
                        if( (monthOfYear + 1) < 10 ) dm = "0" + (monthOfYear + 1); else dm = ""+(monthOfYear + 1);
                        expDate = year + dm + dd;
                        expET.setText( year + "." + dm + "." + dd );
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    private void refreshData() {
        selectWHID = 0;
        selectRenterID = 0;
        selectvirtWHID = 0;
        Vector_Paletta_ParamStr = "";
        Vector_EinlagererID_ParamStr = "";
        Vector_ArtikelID_ParamStr = "";
        Vector_Anz_ParamStr = "";
        Vector_ProdFeld1_ParamStr  = "";
        Vector_MHD_ParamStr = "";
        Vector_VirtualLagerID_ParamStr = "";
        itemCounter = 1;
        counterTV.setText("Tételek: "+itemCounter);

        group1.setVisibility(View.GONE);
        group2.setVisibility(View.GONE);
        nextBtn1.setVisibility(View.VISIBLE);
        nextBtn2.setVisibility(View.VISIBLE);
        itemIdET.setEnabled(true);
        whIDTV.setText("Válasszon raktárat...");
        placeIdET.setText("");
        palET.setText("");
        renterTV.setText("Válasszon bérlőt...");
        itemIdET.setText("");
        countET.setText("");
        lotET.setText("");
        expET.setText("");
        virtwhTV.setText("Válasszon vir. raktárat...");
        itemNameTV.setText("");

    }

    TextWatcher placeIdTW = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            try {
                isBar = HelperClass.isBarcode(placeIdET.getText().toString());
                if (isBar != null) {
                    placeIdET.removeTextChangedListener(placeIdTW);
                    placeIdET.setText(isBar);
                    placeIdET.addTextChangedListener(placeIdTW);
                    loadData1();
                }
            } catch (Exception e) { }
        }

        @Override
        public void afterTextChanged(Editable s) { }
    };

    TextWatcher itemIdTW = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            try {
                isBar = HelperClass.isBarcode(itemIdET.getText().toString());
                if (isBar != null) {
                    itemIdET.removeTextChangedListener(itemIdTW);
                    itemIdET.setText(isBar);
                    itemIdET.addTextChangedListener(itemIdTW);
                    loadData2();
                }
            } catch (Exception e) { }
        }

        @Override
        public void afterTextChanged(Editable s) { }
    };

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void createSelectItemDialog(String title, String[] datas) {
        AlertDialog.Builder b = new AlertDialog.Builder(getContext());
        b.setTitle(title);
        b.setItems(datas, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectWHID = WHIds[which];
                whIDTV.setText(WHTexts[which]);
                dialog.dismiss();
            }
        });
        b.show();
    }

    private void createVirtSelectItemDialog(String title, String[] datas) {
        AlertDialog.Builder b = new AlertDialog.Builder(getContext());
        b.setTitle(title);
        b.setItems(datas, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectvirtWHID = virtWHIds[which];
                virtwhTV.setText(virtWHTexts[which]);
                dialog.dismiss();
            }
        });
        b.show();
    }

    private void createRenterSelectItemDialog(String title, String[] datas) {
        AlertDialog.Builder b = new AlertDialog.Builder(getContext());
        b.setTitle(title);
        b.setItems(datas, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectRenterID = renterIds[which];
                renterTV.setText(renterTexts[which]);
                dialog.dismiss();
            }
        });
        b.show();
    }

    private void loadWHList() {
        if (WHListLoaded) {
            createSelectItemDialog("Válasszon raktárat", WHTexts);
        } else {
            Toast.makeText(getContext(), "A raktár törzs még nem töltődött le, kérem várjon pár másodpercet!", Toast.LENGTH_LONG).show();
        }
    }

    private void loadVirtWHList() {
        if (WHListLoaded) {
            if (virtWHTexts != null && virtWHTexts.length > 0) {
                createVirtSelectItemDialog("Válasszon virt. raktárat", virtWHTexts);
            } else {
                Toast.makeText(getContext(), "A virtuális raktár törzs üres!", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getContext(), "A virtuális raktár törzs még nem töltődött le, kérem várjon pár másodpercet!", Toast.LENGTH_LONG).show();
        }
    }

    private void loadRenterList() {
        if (renterTexts != null && renterTexts.length > 0) {
            createRenterSelectItemDialog("Válasszon bérlőt", renterTexts);
        } else {
            Toast.makeText(getContext(), "A bérlő törzs üres!", Toast.LENGTH_LONG).show();
        }
    }

    private void loadWHData() {
        if (HelperClass.isOnline(getContext())) {
            WHListLoaded = false;
            String url = SessionClass.getParam("WSUrl") + "/whs/0";
            Log.i("URL", url);
            RequestQueue rq = MySingleton.getInstance(getContext()).getRequestQueue();
            JSONObject jsonObject = null;
            JsonRequest<JSONObject> jr = new JsonObjectRequest(Request.Method.GET, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        String rootText = response.getString("WHSResult");
                        JSONArray jsonArray = new JSONArray(rootText);
                        int maxCount = jsonArray.length();
                        if (jsonArray.getJSONObject(0).getString("KundeName1").isEmpty()) {
                            maxCount = jsonArray.length() - 1;
                        }
                        WHTexts = new String[maxCount];
                        WHIds = new Integer[maxCount];
                        int c = 0;
                        for (int i = 0; i < jsonArray.length(); i++) {
                            if (!jsonArray.getJSONObject(i).getString("KundeName1").isEmpty()) {
                                JSONObject jsonObj = jsonArray.getJSONObject(i);
                                WHIds[c] = jsonObj.getInt("ID");
                                WHTexts[c] = jsonObj.getString("KundeName1");
                                c++;
                            }
                        }
                        WHListLoaded = true;
                        loadVirtWHData();
                    } catch (Exception e) {
                        if(getContext() != null) {
                            e.printStackTrace();
                            HelperClass.messageBox(getActivity(),"Vak leltár","Hiba a raktár törzs betöltésekor!",HelperClass.ERROR);
                            //Toast.makeText(getContext(), "Hiba a raktár törzs betöltésekor!", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (error != null) {
                        error.printStackTrace();
                    }
                    HelperClass.messageBox(getActivity(),"Vak leltár","Hiba a raktár törzs betöltésekor!",HelperClass.ERROR);
                    //Toast.makeText(getContext(), "Hiba a raktár törzs betöltésekor!", Toast.LENGTH_LONG).show();
                }
            });
            rq.add(jr);
        } else {
            HelperClass.messageBox(getActivity(),"Vak leltár","Nincs hálózat az eszközön!",HelperClass.ERROR);
            //Toast.makeText(getContext(), "Nincs hálózat az eszközön!", Toast.LENGTH_LONG).show();
        }
    }

    private void loadVirtWHData() {
        if (HelperClass.isOnline(getContext())) {
            String url = SessionClass.getParam("WSUrl") + "/whs/1";
            Log.i("URL", url);
            RequestQueue rq = MySingleton.getInstance(getContext()).getRequestQueue();
            JSONObject jsonObject = null;
            JsonRequest<JSONObject> jr = new JsonObjectRequest(Request.Method.GET, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        String rootText = response.getString("WHSResult");
                        JSONArray jsonArray = new JSONArray(rootText);
                        int maxCount = jsonArray.length();
                        if (jsonArray.getJSONObject(0).getString("KundeName1").isEmpty()) {
                            maxCount = jsonArray.length() - 1;
                        }
                        virtWHTexts = new String[maxCount];
                        virtWHIds = new Integer[maxCount];
                        int c = 0;
                        for (int i = 0; i < jsonArray.length(); i++) {
                            if (!jsonArray.getJSONObject(i).getString("KundeName1").isEmpty()) {
                                JSONObject jsonObj = jsonArray.getJSONObject(i);
                                virtWHIds[c] = jsonObj.getInt("ID");
                                virtWHTexts[c] = jsonObj.getString("KundeName1");
                                c++;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        HelperClass.messageBox(getActivity(),"Vak leltár","Hiba a virtuális raktár törzs betöltésekor!",HelperClass.ERROR);
                        //Toast.makeText(getContext(), "Hiba a virtuális raktár törzs betöltésekor!", Toast.LENGTH_LONG).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (error != null) {
                        error.printStackTrace();
                    }
                    HelperClass.messageBox(getActivity(),"Vak leltár","Hiba a virtuális raktár törzs betöltésekor!",HelperClass.ERROR);
                    //Toast.makeText(getContext(), "Hiba a virtuális raktár törzs betöltésekor!", Toast.LENGTH_LONG).show();
                }
            });
            rq.add(jr);
        } else {
            HelperClass.messageBox(getActivity(),"Vak leltár","Nincs hálózat az eszközön!",HelperClass.ERROR);
            //Toast.makeText(getContext(), "Nincs hálózat az eszközön!", Toast.LENGTH_LONG).show();
        }
    }

    private void loadRenterData() {
        if (HelperClass.isOnline(getContext())) {
            String url = SessionClass.getParam("WSUrl") + "/WRHS_RENTER";
            Log.i("URL", url);
            RequestQueue rq = MySingleton.getInstance(getContext()).getRequestQueue();
            JSONObject jsonObject = null;
            JsonRequest<JSONObject> jr = new JsonObjectRequest(Request.Method.GET, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        String rootText = response.getString("WRHS_RENTERResult");
                        JSONArray jsonArray = new JSONArray(rootText);
                        int maxCount = jsonArray.length();
                        if (jsonArray.getJSONObject(0).getString("CompanyName1").isEmpty()) {
                            maxCount = jsonArray.length() - 1;
                        }
                        renterTexts = new String[maxCount];
                        renterIds = new Integer[maxCount];
                        int c = 0;
                        for (int i = 0; i < jsonArray.length(); i++) {
                            if (!jsonArray.getJSONObject(i).getString("CompanyName1").isEmpty()) {
                                JSONObject jsonObj = jsonArray.getJSONObject(i);
                                renterIds[c] = jsonObj.getInt("ID");
                                renterTexts[c] = jsonObj.getString("CompanyName1");
                                c++;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        HelperClass.messageBox(getActivity(),"Vak leltár","Hiba a bérlő törzs betöltésekor!",HelperClass.ERROR);
                        //Toast.makeText(getContext(), "Hiba a bérlő törzs betöltésekor!", Toast.LENGTH_LONG).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (error != null) {
                        error.printStackTrace();
                    }
                    HelperClass.messageBox(getActivity(),"Vak leltár","Hiba a bérlő törzs betöltésekor!",HelperClass.ERROR);
                    //Toast.makeText(getContext(), "Hiba a bérlő törzs betöltésekor!", Toast.LENGTH_LONG).show();
                }
            });
            rq.add(jr);
        } else {
            HelperClass.messageBox(getActivity(),"Vak leltár","Nincs hálózat az eszközön!",HelperClass.ERROR);
            //Toast.makeText(getContext(), "Nincs hálózat az eszközön!", Toast.LENGTH_LONG).show();
        }
    }

    private void loadGoodFromBarcode() {
        if (HelperClass.isOnline(getContext())) {
            String url = SessionClass.getParam("WSUrl") + "/WRHS_PDA_getGoodFromBarcode/" + itemIdET.getText().toString() + "/" + selectRenterID + "/0/0/0";
            Log.i("URL", url);
            RequestQueue rq = MySingleton.getInstance(getContext()).getRequestQueue();
            JSONObject jsonObject = null;
            JsonRequest<JSONObject> jr = new JsonObjectRequest(Request.Method.GET, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        String rootText = response.getString("WRHS_PDA_getGoodFromBarcodeResult");
                        JSONObject json = new JSONObject(rootText);
                        if(json.getInt("ERROR_CODE") == 0) {
                            WrhsRenters_ID = json.getString("WrhsRenters_ID");
                            OUT_WrhsGoods_ID = json.getString("OUT_WrhsGoods_ID");
                            OUT_WrhsGoods_ItemNum = json.getString("OUT_WrhsGoods_ItemNum");
                            OUT_WrhsGoods_Name = json.getString("OUT_WrhsGoods_Name");
                            FieldParams_TextBox_MHD = json.getString("FieldParams_TextBox_MHD");
                            itemNameTV.setText(OUT_WrhsGoods_Name);
                            itemIdET.setText(OUT_WrhsGoods_ItemNum);
                            itemIdET.setEnabled(false);
                            selectRenterID = Integer.parseInt(WrhsRenters_ID);
                            int x = Arrays.asList(renterIds).indexOf(Integer.parseInt(WrhsRenters_ID));
                            if (x > 0) renterTV.setText(renterTexts[x]);
                            else renterTV.setText("Válasszon bérlőt...");
                            nextBtn2.setVisibility(View.GONE);
                            group2.setVisibility(View.VISIBLE);
                        }else{
                            itemErrorDialog(json.getString("ERROR_TEXT"));
                            //Toast.makeText(getContext(), json.getString("ERROR_TEXT"), Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        HelperClass.messageBox(getActivity(),"Vak leltár","Hiba a bérlő törzs betöltésekor!",HelperClass.ERROR);
                        //Toast.makeText(getContext(), "Hiba a bérlő törzs betöltésekor!", Toast.LENGTH_LONG).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (error != null) {
                        error.printStackTrace();
                    }
                    HelperClass.messageBox(getActivity(),"Vak leltár","Hiba a bérlő törzs betöltésekor!",HelperClass.ERROR);
                    //Toast.makeText(getContext(), "Hiba a bérlő törzs betöltésekor!", Toast.LENGTH_LONG).show();
                }
            });
            rq.add(jr);
        } else {
            HelperClass.messageBox(getActivity(),"Vak leltár","Hiba a bérlő törzs betöltésekor!",HelperClass.ERROR);
            //Toast.makeText(getContext(), "Nincs hálózat az eszközön!", Toast.LENGTH_LONG).show();
        }
    }

    private void loadData1() {
        if(selectWHID > 0) {
            if( !placeIdET.getText().toString().isEmpty() ) {
                clearData();
                group1.setVisibility(View.GONE);
                group2.setVisibility(View.GONE);
                loadLocsControl();
                KeyboardUtils.hideKeyboard(getActivity());
            }else{
                HelperClass.messageBox(getActivity(),"Vak leltár","Nincs meghatározva a rakhely!",HelperClass.ERROR);
                //Toast.makeText(getContext(), "Nincs meghatározva a rakhely!", Toast.LENGTH_LONG).show();
            }
        }else{
            HelperClass.messageBox(getActivity(),"Vak leltár","Nincs kiválasztva a raktár!",HelperClass.ERROR);
            //Toast.makeText(getContext(), "Nincs kiválasztva a raktár!", Toast.LENGTH_LONG).show();
        }
    }

    private void loadData2() {
        if( !itemIdET.getText().toString().isEmpty() ) {
            loadGoodFromBarcode();
            KeyboardUtils.hideKeyboard(getActivity());
        }else{
            HelperClass.messageBox(getActivity(),"Vak leltár","Nincs kiválasztva a cikk!",HelperClass.ERROR);
            //Toast.makeText(getContext(), "Nincs kiválasztva a cikk!", Toast.LENGTH_LONG).show();
        }
    }

    private void loadGetParams() {
        if (HelperClass.isOnline(getContext())) {
            WHListLoaded = false;
            String terminal = SessionClass.getParam("terminal");
            String url = SessionClass.getParam("WSUrl") + "/WRHS_PDA_getParams/" + terminal;
            Log.i("URL", url);
            RequestQueue rq = MySingleton.getInstance(getContext()).getRequestQueue();
            JSONObject jsonObject = null;
            JsonRequest<JSONObject> jr = new JsonObjectRequest(Request.Method.GET, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        String rootText = response.getString("WRHS_PDA_getParamsResult");
                        JSONObject jsonObject = new JSONObject(rootText);
                        if (jsonObject.getInt("ERROR_CODE") == 0) {
                            params = jsonObject.getString("PARAMS").split(",");
                            if (params[0].equals("0")) {
                                rootView.findViewById(R.id.inventory_panel3).setVisibility(View.GONE);
                            }
                            if (params[1].equals("0")) {
                                rootView.findViewById(R.id.inventory_panel4).setVisibility(View.GONE);
                            }
                            if (params[2].equals("0")) {
                                rootView.findViewById(R.id.inventory_panel3).setVisibility(View.GONE);
                            }
                            if (params[3].equals("0")) {
                                rootView.findViewById(R.id.inventory_panel3).setVisibility(View.GONE);
                            }
                            if (params[4].equals("0")) {
                                rootView.findViewById(R.id.inventory_panel9).setVisibility(View.GONE);
                            }
                        } else {
                            Toast.makeText(getContext(), jsonObject.getString("ERROR_TEXT"), Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        HelperClass.messageBox(getActivity(),"Vak leltár","Hiba a paraméterek betöltésekor!",HelperClass.ERROR);
                        //Toast.makeText(getContext(), "Hiba a paraméterek betöltésekor!", Toast.LENGTH_LONG).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (error != null) {
                        error.printStackTrace();
                    }
                    HelperClass.messageBox(getActivity(),"Vak leltár","Hiba a paraméterek betöltésekor!",HelperClass.ERROR);
                    //Toast.makeText(getContext(), "Hiba a paraméterek betöltésekor!", Toast.LENGTH_LONG).show();
                }
            });
            rq.add(jr);
        } else {
            HelperClass.messageBox(getActivity(),"Vak leltár","Nincs hálózat az eszközön!",HelperClass.ERROR);
            //Toast.makeText(getContext(), "Nincs hálózat az eszközön!", Toast.LENGTH_LONG).show();
        }
    }

    private void loadLocsControl() {
        String terminal = SessionClass.getParam("terminal");
        String url = SessionClass.getParam("WSUrl") + "/Locs_Control_LocName/" + placeIdET.getText().toString() + "/" + selectWHID;
        Log.i("URL", url);
        RequestQueue rq = MySingleton.getInstance(getContext()).getRequestQueue();
        JSONObject jsonObject = null;
        JsonRequest<JSONObject> jr = new JsonObjectRequest(Request.Method.GET, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String rootText = response.getString("Locs_Control_LocNameResult");
                    JSONObject jsonObject = new JSONObject(rootText);
                    if (jsonObject.getInt("ERROR_CODE") == 0) {
                        Locks_ID = jsonObject.getString("OUT_WrhsLocks_ID");
                        group1.setVisibility(View.VISIBLE);
                        group2.setVisibility(View.GONE);
                    } if (jsonObject.getInt("ERROR_CODE") == 7001) {
                        continueDialog(jsonObject.getString("OUT_WrhsLocks_ID"));
                    } else {
                        Toast.makeText(getContext(), jsonObject.getString("ERROR_TEXT"), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    HelperClass.messageBox(getActivity(),"Vak leltár","Hiba a betöltésekor!",HelperClass.ERROR);
                    //Toast.makeText(getContext(), "Hiba a betöltésekor!", Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error != null) {
                    error.printStackTrace();
                }
                HelperClass.messageBox(getActivity(),"Vak leltár","Hiba a paraméterek betöltésekor!",HelperClass.ERROR);
                //Toast.makeText(getContext(), "Hiba a paraméterek betöltésekor!", Toast.LENGTH_LONG).show();
            }
        });
        rq.add(jr);
    }

    private void saveData() {
        RequestQueue rq = MySingleton.getInstance(getContext()).getRequestQueue();
        String url = SessionClass.getParam("WSUrl") + "/WRHS_PDA_Func_BlindInventory_SET";
        addTempDatas();
        HashMap<String, String> map = new HashMap<>();
        map.put("Terminal", SessionClass.getParam("terminal"));
        map.put("WrhsDecl_ID", ""+selectWHID);
        map.put("WrhsLocs_ID", Locks_ID);
        map.put("Vector_Paletta", Vector_Paletta_ParamStr);
        map.put("Vector_EinlagererID", Vector_EinlagererID_ParamStr);
        map.put("Vector_ArtikelID", Vector_ArtikelID_ParamStr);
        map.put("Vector_Anz", Vector_Anz_ParamStr);
        map.put("Vector_ProdFeld1", Vector_ProdFeld1_ParamStr);
        map.put("Vector_MHD", Vector_MHD_ParamStr );
        map.put("Vector_VirtualLagerID", Vector_VirtualLagerID_ParamStr );

        if (HelperClass.isOnline(getContext())) {
            JsonRequest<JSONObject> jr = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(map), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        String rootText = response.getString("WRHS_PDA_Func_BlindInventory_SETResult");
                        JSONObject jsonObject = new JSONObject(rootText);
                        String rtext = jsonObject.getString("ERROR_CODE");
                        if (!rtext.isEmpty()) {
                            if (rtext.equals("0")) {
                                Toast.makeText(getContext(), "Adatok mentése megtörtént!", Toast.LENGTH_LONG).show();
                                refreshData();
                            } else {
                                HelperClass.messageBox(getActivity(),"Vak leltár","Adatok mentése sikertelen, kérem jelezze a Selesternek!",HelperClass.ERROR);
                                //Toast.makeText(getContext(), "Adatok mentése sikertelen, kérem jelezze a Selesternek!", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            HelperClass.messageBox(getActivity(),"Vak leltár","Adatok mentése sikertelen, kérem jelezze a Selesternek!",HelperClass.ERROR);
                            //Toast.makeText(getContext(), "Adatok mentése sikertelen, kérem jelezze a Selesternek!", Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        HelperClass.messageBox(getActivity(),"Vak leltár","Adatok mentése sikertelen, kérem jelezze a Selesternek!",HelperClass.ERROR);
                        //Toast.makeText(getContext(), "Adatok mentése sikertelen, kérem jelezze a Selesternek!", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (error != null) {
                        HelperClass.messageBox(getActivity(),"Vak leltár","Adatok mentése sikertelen, kérem jelezze a Selesternek!",HelperClass.ERROR);
                        //Toast.makeText(getContext(), "Adatok mentése sikertelen, hálózati hiba!", Toast.LENGTH_LONG).show();
                        error.printStackTrace();
                    }
                }
            });
            jr.setRetryPolicy(new DefaultRetryPolicy(50000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            rq.add(jr);
        }
    }

    private void continueDialog(final String id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Tárhely foglaltság");
        TextView tv1 = new TextView(getContext());
        tv1.setPadding(HelperClass.dpToPx(getContext(),20),HelperClass.dpToPx(getContext(),20),0,0);
        tv1.setText("Ezen a tárhelyen már van leltár adat. Biztosan folytatja?");
        builder.setView(tv1);
        builder.setNegativeButton("Nem", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                KeyboardUtils.hideKeyboard(getActivity());
                Locks_ID = "";
                dialog.cancel();
            }
        });
        builder.setPositiveButton("Igen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Locks_ID = id;
                group1.setVisibility(View.VISIBLE);
                group2.setVisibility(View.GONE);
            }
        });
        builder.show();
    }

    private void itemErrorDialog(String errorText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Cikkszám meghatározás");
        TextView tv1 = new TextView(getContext());
        tv1.setPadding(HelperClass.dpToPx(getContext(),20),HelperClass.dpToPx(getContext(),20),0,0);
        tv1.setText(errorText);
        builder.setView(tv1);
        builder.setNegativeButton("Rendben", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                KeyboardUtils.hideKeyboard(getActivity());
                dialog.cancel();
            }
        });
        builder.show();
    }

}