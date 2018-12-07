package hu.selester.android.webstockandroid.Fragments;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import hu.selester.android.webstockandroid.Database.SelesterDatabase;
import hu.selester.android.webstockandroid.Database.Tables.ProductData;
import hu.selester.android.webstockandroid.Database.Tables.SystemTable;
import hu.selester.android.webstockandroid.Helper.HelperClass;
import hu.selester.android.webstockandroid.Helper.KeyboardUtils;
import hu.selester.android.webstockandroid.Helper.MySingleton;
import hu.selester.android.webstockandroid.Objects.ActiveFragment;
import hu.selester.android.webstockandroid.Objects.SessionClass;
import hu.selester.android.webstockandroid.R;

public class SettingsFragment extends Fragment implements View.OnClickListener{

    private final String LOG_TAG="Selester / "+this.getClass().getCanonicalName();
    private final int ACTIVITY_CHOOSE_FILE1=10;
    private final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE=20;
    private SelesterDatabase db;
    private TextView urlText,terminalText,errorText;
    private LinearLayout urlPanel;
    private ImageButton qrUrlBtn;
    public boolean scanListenerActive;
    private FragmentTransaction dialogTransaction;
    private Fragment dialogFragment;
    public int scanBtnCode;
    private EditText barCodeSuffix;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
        ActiveFragment.setFragment(this);
        scanBtnCode=0;
        scanListenerActive=false;
        db = SelesterDatabase.getDatabase(getContext());
        View rootView = inflater.inflate(R.layout.frg_setting,container,false);
        ImageButton cancelBtn = rootView.findViewById(R.id.setting_cancelBtn);
        Button scacBtn = rootView.findViewById(R.id.setting_barBtn);
        scacBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingScanButton();
            }
        });
        urlText = rootView.findViewById(R.id.setting_url);
        terminalText = rootView.findViewById(R.id.setting_terminal);
        errorText = rootView.findViewById(R.id.setting_errorText);
        barCodeSuffix = rootView.findViewById(R.id.setting_barcodesuffix);
        if( db.systemDao().getValue("barcodeSuffix")==null || db.systemDao().getValue("barcodeSuffix").equals("")) {
            barCodeSuffix.setText("#&");
        }else{
            barCodeSuffix.setText(db.systemDao().getValue("barcodeSuffix"));
        }

        ImageView refreshTerminal = rootView.findViewById(R.id.setting_refreshterminal);
        refreshTerminal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getTerminalString(1,false);
            }
        });
        if(db.systemDao().getValue("WSUrl")!=null) {
            urlText.setText(db.systemDao().getValue("WSUrl"));
        }else{
            urlText.setText("");
        }
        if(db.systemDao().getValue("terminal")!=null) {
            terminalText.setText(db.systemDao().getValue("terminal"));
        }else{
            terminalText.setText("");
        }
        urlPanel = rootView.findViewById(R.id.setting_url_panel);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KeyboardUtils.hideKeyboard(getActivity());
                getFragmentManager().popBackStack();
            }
        });
        ImageButton saveBtn = rootView.findViewById(R.id.setting_saveBtn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSetting();
            }
        });
        qrUrlBtn = rootView.findViewById(R.id.setting_qrUrlBtn);
        qrUrlBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadQR();
            }
        });
        ImageView infoBtn = rootView.findViewById(R.id.setting_info);
        infoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manualSetDialog();
                /*
                if(urlPanel.getVisibility()==View.GONE){
                    urlPanel.setVisibility(View.VISIBLE);
                }else{
                    urlPanel.setVisibility(View.GONE);
                }*/
            }
        });
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        chkQRConnect();
        return rootView;
    }

    private void loadQR() {
        IntentIntegrator integrator= IntentIntegrator.forSupportFragment(this);
        integrator
                .setOrientationLocked(false)
                .setBeepEnabled(true)
                .setCameraId(0)
                .setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES)
                .setBarcodeImageEnabled(true)
                .initiateScan();

    }

    private void saveSetting() {
        String error = "";
        if(urlText.getText().toString().equals("")){error+="Nincs megadva a szerver host!\n\r";}
        Log.i("TAG",error);
        if(error.equals("")) {

            RequestQueue rq = MySingleton.getInstance(getContext()).getRequestQueue();
            String url = urlText.getText() + "/teszt";
            Log.i("TAG", url);
            JSONObject jsonObject = null;
            JsonRequest<JSONObject> jr = new JsonObjectRequest(Request.Method.GET, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        String rootText = response.getString("testResult");
                        JSONObject jsonObject = new JSONObject(rootText);
                        String rtext = jsonObject.getString("message");
                        if (!rtext.isEmpty()) {
                            if (rtext.equals("TESZT OK!")) {
                                if( terminalText.getText().toString().isEmpty() ){
                                    getTerminalString(1,true);
                                }else{
                                    saveParamters();
                                }

                                qrUrlBtn.setImageDrawable(getResources().getDrawable(R.drawable.wsqr_acc,null));
                            } else {
                                errorText.setText("Kapcsolódás sikertelen, mentés nem lehetséges!");
                                qrUrlBtn.setImageDrawable(getResources().getDrawable(R.drawable.wsqr_dec,null));
                                errorText.setVisibility(View.VISIBLE);
                            }
                        } else {
                            errorText.setText("Kapcsolódás sikertelen, mentés nem lehetséges!");
                            qrUrlBtn.setImageDrawable(getResources().getDrawable(R.drawable.wsqr_dec,null));
                            errorText.setVisibility(View.VISIBLE);
                        }
                    } catch (JSONException e) {
                        errorText.setText("Kapcsolódás sikertelen, mentés nem lehetséges!");
                        qrUrlBtn.setImageDrawable(getResources().getDrawable(R.drawable.wsqr_dec,null));
                        errorText.setVisibility(View.VISIBLE);
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (error != null) {
                        errorText.setText("Kapcsolódás sikertelen, mentés nem lehetséges!");
                        qrUrlBtn.setImageDrawable(getResources().getDrawable(R.drawable.wsqr_dec,null));
                        errorText.setVisibility(View.VISIBLE);
                        error.printStackTrace();
                    }
                }
            });
            rq.add(jr);
        }else{
            errorText.setText(error);
            errorText.setVisibility(View.VISIBLE);
        }
    }

    void saveParamters(){
        errorText.setVisibility(View.GONE);
        db.systemDao().setValue(new SystemTable("WSUrl",urlText.getText().toString()));
        db.systemDao().setValue(new SystemTable("terminal",terminalText.getText().toString()));
        db.systemDao().setValue(new SystemTable("barcodeSuffix",barCodeSuffix.getText().toString()));
        db.systemDao().setValue(new SystemTable("scanButtonCode", "" + scanBtnCode));
        SessionClass.setParam("WSUrl",urlText.getText().toString());
        SessionClass.setParam("terminal",terminalText.getText().toString());
        db.systemDao().setValue(new SystemTable("terminal",terminalText.getText().toString()));
        Toast.makeText(getContext(),"Mentés sikeresen megtörtént!",Toast.LENGTH_LONG).show();
        KeyboardUtils.hideKeyboard(getActivity());
        getFragmentManager().popBackStack();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onClick(View v) {

    }

    private void chkQRConnect(){
        Log.i("TAG","TESZT");
        RequestQueue rq = MySingleton.getInstance(getContext()).getRequestQueue();
        String url = urlText.getText()+"/teszt";
        Log.i("TAG",url);
        JSONObject jsonObject=null;
        JsonRequest<JSONObject> jr = new JsonObjectRequest(Request.Method.GET, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String rootText=response.getString("testResult");
                    JSONObject jsonObject = new JSONObject(rootText);
                    String rtext = jsonObject.getString("message");
                    if(!rtext.isEmpty()){
                        if(rtext.equals("TESZT OK!")){
                            Toast.makeText(getContext(),"Sikeres kapcsolódás!",Toast.LENGTH_LONG).show();
                            qrUrlBtn.setImageDrawable(getResources().getDrawable(R.drawable.wsqr_acc,null));
                            if(terminalText.getText().toString().isEmpty()){
                                getTerminalString(0,true);
                            }
                        }else{
                            errorText.setText("Kapcsolódás sikertelen!");
                            errorText.setVisibility(View.VISIBLE);
                            qrUrlBtn.setImageDrawable(getResources().getDrawable(R.drawable.wsqr_dec,null));
                        }
                    }else{
                        errorText.setText("Kapcsolódás sikertelen!");
                        Toast.makeText(getContext(),"Kapcsolódás sikeretelen!",Toast.LENGTH_LONG).show();
                        qrUrlBtn.setImageDrawable(getResources().getDrawable(R.drawable.wsqr_dec,null));
                    }
                } catch (JSONException e) {
                    errorText.setText("Kapcsolódás sikertelen!");
                    errorText.setVisibility(View.VISIBLE);
                    Toast.makeText(getContext(),"Kapcsolódás sikeretelen!",Toast.LENGTH_LONG).show();
                    qrUrlBtn.setImageDrawable(getResources().getDrawable(R.drawable.wsqr_dec,null));
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error!=null){
                    errorText.setText("Kapcsolódás sikertelen!");
                    errorText.setVisibility(View.VISIBLE);
                    Toast.makeText(getContext(),"Kapcsolódás sikeretelen!",Toast.LENGTH_LONG).show();
                    qrUrlBtn.setImageDrawable(getResources().getDrawable(R.drawable.wsqr_dec,null));
                    error.printStackTrace();
                }
            }
        });
        rq.add(jr);

    }

    private void getTerminalString(final int type,final boolean save) {
        Log.i("TAGO","getTerminal");
        RequestQueue rq = MySingleton.getInstance(getContext()).getRequestQueue();
        JSONObject jsonObject=null;
        String url = urlText.getText()+"/get_freeTerminal";
        Log.i("TAG",url);
        JsonRequest<JSONObject> jr = new JsonObjectRequest(Request.Method.GET, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String rootText= null;
                try {
                    Log.i("TAG",response.toString());
                    rootText = response.getString("get_freeTerminalResult");
                    JSONObject jsonObject = new JSONObject(rootText);
                    String terminal = jsonObject.getString("Free terminal");
                    if( terminal!=null && !terminal.isEmpty() ){
                        terminalText.setText(terminal);
                        if(type == 1){ lockTerminal(save); }
                    }else{
                        errorText.setText("Nem meghatározható terminal azonosító!");
                        errorText.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error!=null){
                    error.printStackTrace();
                }
            }
        });
        rq.add(jr);
    }

    private void lockTerminal(final boolean save) {
        Log.i("TAGO","lockTerminal");
        RequestQueue rq = MySingleton.getInstance(getContext()).getRequestQueue();
        String url = SessionClass.getParam("WSUrl") + "/SEL_SYS_INSTALLED_TERMINALS_CHECK";
        HashMap<String,String> map = new HashMap<>();
        map.put("Terminal",terminalText.getText().toString());
        map.put("Computername", HelperClass.getAndroidID(getContext()));
        map.put("StartupPath","/data/data/" + getContext().getPackageName());
        Log.i("TAG",(new JSONObject(map)).toString());
        JsonRequest<JSONObject> jr = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(map), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.i("TAGO",response.toString());
                    String rootText = response.getString("SEL_SYS_INSTALLED_TERMINALS_CHECKResult");
                    JSONObject jsonObject = new JSONObject(rootText);
                    String rtext = jsonObject.getString("ERROR_CODE");
                    if (!rtext.isEmpty()) {
                        if (rtext.equals("-1")) {
                            if(save) saveParamters();
                            //Toast.makeText(getContext(), "Adatok áttöltése sikeresen eltároltam!", Toast.LENGTH_LONG).show();
                        } else {
                            String etext = jsonObject.getString("ERROR_TEXT");
                            errorText.setText(etext);
                            errorText.setVisibility(View.VISIBLE);
                        }
                    } else {
                        errorText.setText("Hiba a mentés során!");
                        errorText.setVisibility(View.VISIBLE);
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
        rq.add(jr);
    }

    private void loadPics() {
        if (ContextCompat.checkSelfPermission(getActivity().getBaseContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                loadPicsPanel();
            }else{
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            }
        }else{
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            return;
        }
    }

    private void loadPicsPanel(){

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/*");
        startActivityForResult(Intent.createChooser(intent, "Open CSV"), ACTIVITY_CHOOSE_FILE1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            loadPicsPanel();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("WS",requestCode+" - "+resultCode);
        switch (requestCode) {
            case ACTIVITY_CHOOSE_FILE1: {
                if (resultCode == Activity.RESULT_OK) {
                    Log.i("S", "LOAD OK: "+data.getData().getPath());
                    importCSV(data);
                }
            }
        }
        IntentResult result=IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if(result!=null){
            if(result.getContents()==null){
                errorText.setText("");
            }else{
                //Log.i("TAG",result.getContents());
                urlText.setText(result.getContents());
                chkQRConnect();
            }
        }else{
            super.onActivityResult(requestCode, resultCode, data);
        }

    }

    private void importCSV(Intent data){
        Uri uri = data.getData();
        BufferedReader mBufferedReader = null;
        String line;
        List<ProductData> list = new ArrayList<>();
        try
        {
            InputStream inputStream = getActivity().getContentResolver().openInputStream(uri);
            mBufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            while ((line = mBufferedReader.readLine()) != null)
            {
                String[] tags=line.split(";");
                ProductData pda = new ProductData(1,tags[0],"",tags[1],(long)100);
                list.add(pda);
            }
            db.productDataDAO().deleteAll();
            db.productDataDAO().setProductData(list);
            mBufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void settingScanButton(){
        dialogFragment = new DialogFragment();
        dialogTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        dialogTransaction.add(R.id.fragments,dialogFragment).commit();
        scanListenerActive=true;
    }

    @Override
    public void onDestroyView() {
        try {
            scanListenerActive=false;
            Fragment f = getActivity().getSupportFragmentManager().getFragments().get(1);
            getActivity().getSupportFragmentManager().beginTransaction().remove(f).commit();
        }catch(Exception e){

        }
        super.onDestroyView();
    }

    private void manualSetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View v = getLayoutInflater().inflate(R.layout.dialog_manual_setting,null);
        final EditText eanET = v.findViewById(R.id.dialog_manual_set_ed);
        eanET.setText(urlText.getText());
        builder.setView(v);
        builder.setPositiveButton("IGEN", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                KeyboardUtils.hideKeyboard(getActivity());
                urlText.setText(eanET.getText());
                chkQRConnect();
                dialog.cancel();
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


}
