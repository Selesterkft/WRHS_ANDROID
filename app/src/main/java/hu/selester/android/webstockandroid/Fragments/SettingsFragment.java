package hu.selester.android.webstockandroid.Fragments;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import hu.selester.android.webstockandroid.Adapters.ImageListAdapter;
import hu.selester.android.webstockandroid.Database.SelesterDatabase;
import hu.selester.android.webstockandroid.Database.Tables.ProductData;
import hu.selester.android.webstockandroid.Database.Tables.SessionTemp;
import hu.selester.android.webstockandroid.Database.Tables.SystemTable;
import hu.selester.android.webstockandroid.Divider.SimpleDividerItemDecoration;
import hu.selester.android.webstockandroid.Helper.HelperClass;
import hu.selester.android.webstockandroid.Helper.KeyboardUtils;
import hu.selester.android.webstockandroid.Helper.MySingleton;
import hu.selester.android.webstockandroid.Objects.ActiveFragment;
import hu.selester.android.webstockandroid.Objects.ImageObject;
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
    private ImageView landscapeBtn, portraitBtn;
    public boolean scanListenerActive;
    private FragmentTransaction dialogTransaction;
    private Fragment dialogFragment;
    public int scanBtnCode;
    private EditText barCodeSuffix;
    private int orientation;
    private List<ImageObject> imageHeadlist = new ArrayList<>();
    private LinearLayoutManager mLayountManager;


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
        landscapeBtn = rootView.findViewById(R.id.setting_landscape);
        portraitBtn = rootView.findViewById(R.id.setting_portait);
        Button loadimageBtn = rootView.findViewById(R.id.setting_imageloadBtn);
        loadimageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadSessionTempHead();
            }
        });

        if( db.systemDao().getValue("orientation") != null && !db.systemDao().getValue("orientation").equals("") ) {
            orientation = Integer.parseInt(db.systemDao().getValue("orientation"));
        }else{
            orientation = 0;
        }
        if( orientation == 0 ){
            portraitBtn.setBackgroundColor(getResources().getColor(R.color.backOpacityColor));
            landscapeBtn.setBackgroundColor(Color.TRANSPARENT);
        }else{
            landscapeBtn.setBackgroundColor(getResources().getColor(R.color.backOpacityColor));
            portraitBtn.setBackgroundColor(Color.TRANSPARENT);
        }
        portraitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                portraitBtn.setBackgroundColor(getResources().getColor(R.color.backOpacityColor));
                landscapeBtn.setBackgroundColor(Color.TRANSPARENT);
                orientation = 0;
            }
        });

        landscapeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                landscapeBtn.setBackgroundColor(getResources().getColor(R.color.backOpacityColor));
                portraitBtn.setBackgroundColor(Color.TRANSPARENT);
                orientation = 1;
            }
        });

        if( db.systemDao().getValue("barcodeSuffix") == null || db.systemDao().getValue("barcodeSuffix").equals("")) {
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
                .setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
                .setBarcodeImageEnabled(true)
                .initiateScan();

    }

    private void saveSetting() {
        String error = "";
        if(urlText.getText().toString().equals("")){error+="Nincs megadva a szerver host!\n\r";}
        if(error.equals("")) {
            RequestQueue rq = MySingleton.getInstance(getContext()).getRequestQueue();
            String url = urlText.getText() + "/teszt";
            JSONObject jsonObject = null;
            Log.i("URL",url);
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
        db.systemDao().setValue(new SystemTable("orientation", "" + orientation));
        SessionClass.setParam("WSUrl",urlText.getText().toString());
        SessionClass.setParam("terminal",terminalText.getText().toString());
        db.systemDao().setValue(new SystemTable("terminal",terminalText.getText().toString()));
        Toast.makeText(getActivity(),"Mentés sikeresen megtörtént!",Toast.LENGTH_LONG).show();
        KeyboardUtils.hideKeyboard(getActivity());
        if( orientation == 1 ){
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        }else{
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        }

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
        RequestQueue rq = MySingleton.getInstance(getContext()).getRequestQueue();
        String url = urlText.getText()+"/teszt";
        JSONObject jsonObject=null;
        Log.i("URL",url);
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
        RequestQueue rq = MySingleton.getInstance(getContext()).getRequestQueue();
        JSONObject jsonObject=null;
        String url = urlText.getText()+"/get_freeTerminal";
        Log.i("URL",url);
        JsonRequest<JSONObject> jr = new JsonObjectRequest(Request.Method.GET, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String rootText= null;
                try {
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
        RequestQueue rq = MySingleton.getInstance(getContext()).getRequestQueue();
        String url = SessionClass.getParam("WSUrl") + "/SEL_SYS_INSTALLED_TERMINALS_CHECK";
        HashMap<String,String> map = new HashMap<>();
        map.put("Terminal",terminalText.getText().toString());
        map.put("Computername", HelperClass.getAndroidID(getContext()));
        map.put("StartupPath","/data/data/" + getContext().getPackageName());
        Log.i("URL",url);
        JsonRequest<JSONObject> jr = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(map), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
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
        switch (requestCode) {
            case ACTIVITY_CHOOSE_FILE1: {
                if (resultCode == Activity.RESULT_OK) {
                    importCSV(data);
                }
            }
        }
        IntentResult result=IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if(result!=null){
            if(result.getContents()==null){
                errorText.setText("");
            }else{
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
        eanET.selectAll();
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

    private void loadImageListDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Állapot betöltés");
        View v = LayoutInflater.from(getContext()).inflate(R.layout.dialog_image, null);
        RecyclerView rv = v.findViewById(R.id.dialog_image_rv);

        if(imageHeadlist.size() > 0 ){
            mLayountManager = new LinearLayoutManager(getContext());
            ImageListAdapter lla = new ImageListAdapter(getContext(),imageHeadlist);
            lla.setActivity(getActivity());
            rv.setLayoutManager(mLayountManager);
            rv.addItemDecoration(new SimpleDividerItemDecoration(getContext()));
            rv.setAdapter(lla);
        }

        builder.setView(v);
        builder.setNegativeButton("Mégsem", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                KeyboardUtils.hideKeyboard(getActivity());
                dialog.cancel();
            }
        });
        builder.setPositiveButton("Betöltés", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if( !SessionClass.getParam("Select_ImageID").equals("0") ){
                    loadSessionTempdata();
                }else{
                    Toast.makeText(getContext(),"Nincs kiválasztva a betöltendő állomány!", Toast.LENGTH_LONG).show();
                }
            }
        });

        builder.show();
    }

    private void loadSessionTempdata() {
        String url = SessionClass.getParam("WSUrl") + "/WRHS_PDA_LOG_GET_DATA/"+SessionClass.getParam("Select_ImageID");
        Log.i("URL",url);
        JSONObject jsonObject=null;
        RequestQueue rq = MySingleton.getInstance(getContext()).getRequestQueue();
        JsonRequest<JSONObject> jr = new JsonObjectRequest(Request.Method.GET, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String rootText=response.getString("WRHS_PDA_LOG_GET_DATAResult");
                    Log.i("TAG", rootText);
                    if(!rootText.isEmpty()){
                        JSONArray root=new JSONArray(rootText);
                        getSessionTempHead(root);
                    }else{
                        Toast.makeText(getContext(),"Hiba a kapcsolódáskor", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(getContext(),"Hiba a kapcsolódáskor", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error!=null){
                    Toast.makeText(getContext(),"Hiba a kapcsolódáskor", Toast.LENGTH_LONG).show();
                    error.printStackTrace();
                }
            }
        });
        jr.setRetryPolicy(new DefaultRetryPolicy(50000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rq.add(jr);
    }

    private void getSessionTempHead(JSONArray root) {
        List<SessionTemp> list = new ArrayList<>();
        try {
            for (int i = 0; i < root.length(); i++) {

                int status =0;
                if( !root.getJSONObject(i).getString("STATUS").equals("null") && !root.getJSONObject(i).getString("STATUS").equals("") ){
                    status = root.getJSONObject(i).getInt("STATUS");
                }
                SessionTemp st = new SessionTemp(
                        root.getJSONObject(i).getInt("id"),
                        root.getJSONObject(i).getInt("num"),
                        root.getJSONObject(i).getString("param0"),
                        root.getJSONObject(i).getString("param1"),
                        root.getJSONObject(i).getString("param2"),
                        root.getJSONObject(i).getString("param3"),
                        root.getJSONObject(i).getString("param4"),
                        root.getJSONObject(i).getString("param5"),
                        root.getJSONObject(i).getString("param6"),
                        root.getJSONObject(i).getString("param7"),
                        root.getJSONObject(i).getString("param8"),
                        root.getJSONObject(i).getString("param9"),
                        root.getJSONObject(i).getString("param10"),
                        root.getJSONObject(i).getString("param11"),
                        root.getJSONObject(i).getString("param12"),
                        root.getJSONObject(i).getString("param13"),
                        root.getJSONObject(i).getString("param14"),
                        root.getJSONObject(i).getString("param15"),
                        root.getJSONObject(i).getString("param16"),
                        root.getJSONObject(i).getString("param17"),
                        root.getJSONObject(i).getString("param18"),
                        root.getJSONObject(i).getString("param19"),
                        root.getJSONObject(i).getString("param20"),
                        root.getJSONObject(i).getString("param21"),
                        root.getJSONObject(i).getString("param22"),
                        root.getJSONObject(i).getString("param23"),
                        root.getJSONObject(i).getString("param24"),
                        root.getJSONObject(i).getString("param25"),
                        root.getJSONObject(i).getString("param26"),
                        root.getJSONObject(i).getString("param27"),
                        root.getJSONObject(i).getString("param28"),
                        root.getJSONObject(i).getString("param29"),
                        root.getJSONObject(i).getString("param30"),
                        root.getJSONObject(i).getString("param31"),
                        root.getJSONObject(i).getString("param32"),
                        root.getJSONObject(i).getString("param33"),
                        root.getJSONObject(i).getString("param34"),
                        root.getJSONObject(i).getString("param35"),
                        root.getJSONObject(i).getString("param36"),
                        root.getJSONObject(i).getString("param37"),
                        root.getJSONObject(i).getString("param38"),
                        root.getJSONObject(i).getString("param39"),
                        status,
                        root.getJSONObject(i).getBoolean("insertRow"),
                        root.getJSONObject(i).getInt("notClose")
                );
                list.add( st );
            }
            SelesterDatabase db = SelesterDatabase.getDatabase(getContext());
            db.sessionTempDao().deleteAllData();
            db.sessionTempDao().setDatas(list);
            Toast.makeText(getContext(),"Betöltés megtörtént! Lépjen be a programba", Toast.LENGTH_LONG).show();
        }catch (JSONException e){
            e.printStackTrace();
            Toast.makeText(getContext(),"Hiba a mentés közben!", Toast.LENGTH_LONG).show();
        }
    }

    private void loadSessionTempHead(){
        SessionClass.setParam("Select_ImageID","0");
        String url = SessionClass.getParam("WSUrl") + "/WRHS_PDA_LOG_GET_HEADS";
        Log.i("URL",url);
        JSONObject jsonObject=null;
        RequestQueue rq = MySingleton.getInstance(getContext()).getRequestQueue();
        JsonRequest<JSONObject> jr = new JsonObjectRequest(Request.Method.GET, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String rootText=response.getString("WRHS_PDA_LOG_GET_HEADSResult");
                    if(!rootText.isEmpty()){
                        JSONArray root=new JSONArray(rootText);
                        getSessionTempHeadData(root);
                    }else{
                        Toast.makeText(getContext(),"Hiba a kapcsolódáskor", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(getContext(),"Hiba a kapcsolódáskor", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error!=null){
                    Toast.makeText(getContext(),"Hiba a kapcsolódáskor", Toast.LENGTH_LONG).show();
                    error.printStackTrace();
                }
            }
        });
        jr.setRetryPolicy(new DefaultRetryPolicy(50000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rq.add(jr);
    }

    private void getSessionTempHeadData(JSONArray jsonArray) throws JSONException{
        imageHeadlist.clear();
        for(int i=0; i < jsonArray.length(); i++){
            imageHeadlist.add(
                    new ImageObject(
                            jsonArray.getJSONObject(i).getLong("ID"),
                            jsonArray.getJSONObject(i).getString("ACTION_DATE"),
                            jsonArray.getJSONObject(i).getInt("MOVE_NUM"),
                            jsonArray.getJSONObject(i).getInt("TRAN_CODE"),
                            jsonArray.getJSONObject(i).getInt("USERID"),
                            jsonArray.getJSONObject(i).getString("TERMINAL")
                    )
            );
        }
        loadImageListDialog();
    }
}
