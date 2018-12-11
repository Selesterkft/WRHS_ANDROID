package hu.selester.android.webstockandroid.Fragments;



import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import hu.selester.android.webstockandroid.Database.SelesterDatabase;
import hu.selester.android.webstockandroid.Helper.KeyboardUtils;
import hu.selester.android.webstockandroid.Helper.MySingleton;
import hu.selester.android.webstockandroid.Objects.SessionClass;
import hu.selester.android.webstockandroid.R;
import hu.selester.android.webstockandroid.Threads.LoadEANDatasThread;

public class MainMenuFragment extends Fragment {

    private SelesterDatabase db;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frg_mainmenu,container,false);
        db = SelesterDatabase.getDatabase(getContext());
        int size = db.sessionTempDao().getDataSize();
        if(size > 0){
            buildReloadDialog();
        }
        if( db.systemDao().getValue("barcodeSuffix")!=null) {
            if( !db.systemDao().getValue("barcodeSuffix").equals("") ) {
                SessionClass.setParam("barcodeSuffix", db.systemDao().getValue("barcodeSuffix"));
            }
        }
        Button tasksBtn = rootView.findViewById(R.id.menu_tasksBtn);
        tasksBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Fragment f = new TreeViewFragment();
                Fragment f = new TasksFragment();
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
                ft.replace(R.id.fragments,f);
                ft.addToBackStack("app");
                ft.commit();
            }
        });
        Button barcodeChkBtn = rootView.findViewById(R.id.menu_barcodeChkBtn);
        barcodeChkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment f = new ChkBarcodeFragment();
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
                Bundle b = new Bundle();
                b.putString("tranid","");
                b.putString("moveid","");
                f.setArguments(b);
                ft.replace(R.id.fragments,f);
                ft.addToBackStack("app");
                ft.commit();
            }
        });
        Button settingsBtn = rootView.findViewById(R.id.menu_settingsBtn);
        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment f = new SettingsFragment();
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
                ft.replace(R.id.fragments,f);
                ft.addToBackStack("app");
                ft.commit();
            }
        });

        Button inventoryBtn = rootView.findViewById(R.id.menu_inventoryBtn);
        inventoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment f = new InventoryFragment();
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
                ft.replace(R.id.fragments,f);
                ft.addToBackStack("app");
                ft.commit();
            }
        });

        Button checkPlaceBtn = rootView.findViewById(R.id.menu_checkplaceBtn);
        checkPlaceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment f = new CheckPlaceFragment();
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
                ft.replace(R.id.fragments,f);
                ft.addToBackStack("app");
                ft.commit();
            }
        });

        Button logBtn = rootView.findViewById(R.id.menu_log);
        logBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment f = new LogFragment();
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
                ft.replace(R.id.fragments,f);
                ft.addToBackStack("app");
                ft.commit();
            }
        });

        Button logoutBtn = rootView.findViewById(R.id.menu_logoutBtn);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
        loadBarcodeData();
        downloadSetting();
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    void loadBarcodeData(){
        new LoadEANDatasThread(getContext()).start();
    }

    public void logout(){
        String account = SessionClass.getParam("account");
        String psw = SessionClass.getParam("password");
        String terminal = SessionClass.getParam("terminal");
        String pda = "123";
        String url=SessionClass.getParam("WSUrl")+"/log_out/"+account+"/"+psw+"/"+terminal+"/"+pda;
        RequestQueue rq = MySingleton.getInstance(getContext()).getRequestQueue();
        JSONObject jsonObject = null;
        JsonRequest<JSONObject> jr = new  JsonObjectRequest(Request.Method.GET, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String rootText=response.getString("log_out_Result");
                    Fragment f = new LoginFragment();
                    FragmentManager fm = getFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
                    ft.replace(R.id.fragments,f);
                    ft.commit();
                } catch (JSONException e) {
                    Toast.makeText(getContext(),"Hiba kijelentkezéskor!",Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error!=null){
                    Toast.makeText(getContext(),"Hiba kijelentkezéskor!",Toast.LENGTH_LONG).show();
                    error.printStackTrace();
                }
            }
        });
        jr.setRetryPolicy(new DefaultRetryPolicy(50000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rq.add(jr);
    }

    private void downloadSetting(){
        RequestQueue rq = MySingleton.getInstance(getContext()).getRequestQueue();
        String userid = SessionClass.getParam("userid");
        String pdaid = SessionClass.getParam("pdaid");
        String terminal = SessionClass.getParam("terminal");
        String url = SessionClass.getParam("WSUrl")+"/getFormSettings/"+terminal+"/"+userid+"/"+pdaid;
        JsonObjectRequest jro = new JsonObjectRequest(Request.Method.GET, url, new JSONObject(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String jsonText = response.getString("getFormSettings_Result");
                    if( !jsonText.equals("")) {
                        JSONArray jsonArray = new JSONArray(jsonText);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String tranCode = jsonArray.getJSONObject(i).getString("Tran_Code");
                            Iterator<String> iter = jsonObject.keys();
                            while (iter.hasNext()) {
                                String key = iter.next();
                                try {
                                    String value = jsonObject.getString(key);
                                    if (key.equals("ID")) {
                                        key = "Tran_ID";
                                    }
                                    key = tranCode + "_" + key;
                                    SessionClass.setParam(key, value);
                                } catch (JSONException e) {
                                    // Something went wrong!
                                }
                            }
                        }
                    }else{
                        buildErrorDialog();
                    }
                } catch (JSONException e) {
                    buildErrorDialog();
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error != null){
                    buildErrorDialog();
                    error.printStackTrace();
                }
            }
        });
        jro.setRetryPolicy(new DefaultRetryPolicy(50000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rq.add(jro);
    }

    void buildErrorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Hibakezelés");
        builder.setMessage("Tranzakciós beállítások nem érvényesek!");
        builder.setNegativeButton("Rendben", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                KeyboardUtils.hideKeyboard(getActivity());
                dialog.cancel();
            }
        });

        builder.show();
    }

    void buildReloadDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Félbehagyott feladat");
        builder.setMessage("Törlés esetén az eddig felvett adatok törlődnek!");

        builder.setPositiveButton("Betölt", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences sharedPref = getActivity().getPreferences(getContext().MODE_PRIVATE);
                String tranID = sharedPref.getString("whrs_selexped_tranID","");
                String tranCode = sharedPref.getString("whrs_selexped_tranCode","");
                String movenum = sharedPref.getString("whrs_selexped_movenum","");
                Fragment f;
                FragmentTransaction ft;
                Bundle b;
                f = new MovesSubViewPager();
                ft = getActivity().getSupportFragmentManager().beginTransaction();
                b = new Bundle();
                b.putString("tranid", tranID);
                b.putString("movenum", movenum);
                b.putString("tranCode", tranCode);
                b.putString("reload", "1");
                f.setArguments(b);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
                ft.replace(R.id.fragments, f);
                ft.addToBackStack("app");
                ft.commit();
                KeyboardUtils.hideKeyboard(getActivity());
                dialog.cancel();
            }
        });
        builder.setNegativeButton("Töröl", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                db.sessionTempDao().deleteAllData();
            }
        });

        builder.show();

    }
}
