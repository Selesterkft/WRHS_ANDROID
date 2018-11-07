package hu.selester.android.webstockandroid.Fragments;


// Login ez egy másik

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import hu.selester.android.webstockandroid.AsyncTask.DownloadNewVersion;
import hu.selester.android.webstockandroid.BuildConfig;
import hu.selester.android.webstockandroid.Database.SelesterDatabase;
import hu.selester.android.webstockandroid.Database.Tables.LogTable;
import hu.selester.android.webstockandroid.Database.Tables.UsersTable;
import hu.selester.android.webstockandroid.Helper.HelperClass;
import hu.selester.android.webstockandroid.Helper.KeyboardUtils;
import hu.selester.android.webstockandroid.Helper.MySingleton;
import hu.selester.android.webstockandroid.Objects.SessionClass;
import hu.selester.android.webstockandroid.R;


public class LoginFragment extends Fragment implements DownloadNewVersion.AsyncResponse{

    private TextView errorTv;
    private EditText accountEt;
    private EditText passwordEt;
    private SelesterDatabase db;
    private AlertDialog.Builder builder;
    private Dialog dialog;
    private TextView pbSubText;
    private Button dialogCloseBtn;
    private ProgressBar pb;
    private TextView pbText;
    private TextView ver;
    private Button loginBtn;
    private boolean showPasword;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frg_login,container,false);
        db = SelesterDatabase.getDatabase(getContext());
        //db.systemDao().setValue(new SystemTable("WSUrl","http://ne hagyd benne az urlt-!xxx/Service1.svc"));
        //db.systemDao().setValue(new SystemTable("terminal","mag"));
        //db.systemDao().setValue(new SystemTable("barcodeSuffix","#&"));
        //db.systemDao().setValue(new SystemTable("scanButtonCode", "203"));
        showPasword = false;
        loginBtn = rootView.findViewById(R.id.login_btn);
        if(db.systemDao().getValue("WSUrl")!=null ||  db.systemDao().getValue("terminal")!=null){
            SessionClass.setParam("WSUrl",db.systemDao().getValue("WSUrl"));
            SessionClass.setParam("terminal",db.systemDao().getValue("terminal"));
        }else{
            loadSettingPanel();
        }
        ImageView settingBtn = rootView.findViewById(R.id.login_settingBtn);
        settingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadSettingPanel();
            }
        });
        accountEt = rootView.findViewById(R.id.login_account);
        ImageView loginLogo = rootView.findViewById(R.id.login_logo);
        loginLogo.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                forceRefresh();
                return false;
            }
        });
        SharedPreferences sharedPref = getActivity().getPreferences(getContext().MODE_PRIVATE);
        String sp_account = sharedPref.getString("whrs_selexped_account","");
        if(!sp_account.isEmpty() && !sp_account.equals("")){
            accountEt.setText(sp_account);
        }
        passwordEt = rootView.findViewById(R.id.login_password);
        passwordEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    login(accountEt.getText().toString(),passwordEt.getText().toString());
                }
                return false;
            }
        });
        ImageView showHidePasswordBtn = rootView.findViewById(R.id.show_hide_password);
        showHidePasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(showPasword){
                    passwordEt.setTransformationMethod(new PasswordTransformationMethod());
                }else{
                    passwordEt.setTransformationMethod(null);
                }
                showPasword = !showPasword;
            }
        });
        errorTv = rootView.findViewById(R.id.login_error);
        errorTv.setTextColor(Color.RED);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login(accountEt.getText().toString(),passwordEt.getText().toString());
            }
        });
        loginBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                HelperClass.tooltipBuild(getContext(), v,"Bejelentkezés", R.style.ToolTipLayoutCustomStyle);
                return true;
            }
        });
        ver = rootView.findViewById(R.id.login_version);

        ver.setText("Verzió: "+BuildConfig.VERSION_NAME);

        return rootView;
    }

    private void login(String account, String password){
        KeyboardUtils.hideKeyboard(getActivity());
        RequestQueue rq = MySingleton.getInstance(getContext()).getRequestQueue();
        SessionClass.setParam("scancount_selectBar","");
        String terminal = SessionClass.getParam("terminal");
        String pdaid = SessionClass.getParam("pdaid");
        String url = SessionClass.getParam("WSUrl")+"/log_in/"+account+"/"+password+"/"+terminal+"/"+pdaid;
        Log.i("TAG",url);
        JSONObject jsonObject=null;
        JsonRequest<JSONObject> jr = new JsonObjectRequest(Request.Method.GET, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String rootText=response.getString("Log_in_Result");
                    Log.i("TAG",rootText);
                    if(!rootText.isEmpty()){
                        JSONObject root=new JSONObject(rootText);

                        if(root.has("ERROR_CODE")){
                            switch (root.getInt("ERROR_CODE")){
                                case 1000:
                                    errorTv.setText("Nem megfelelő Android verzió!");
                                    break;
                                case 1003:
                                    errorTv.setText("Hibás felhasználó / jelszó!");
                                    break;
                                case 1004:
                                    errorTv.setText("A rendszer nem engedte be, mert túllépte a megvásárolt felhasználói keretet. Léptessen ki valakit a rendszerből!");
                                    break;
                            }
                        }else{
                            if(root.getString("version")!=null){
                                if(root.getString("version").equals(BuildConfig.VERSION_NAME)){
                                    db.usersDao().setUserData(
                                        new UsersTable(
                                                root.getInt("ID"),
                                                root.getString("Name"),
                                                accountEt.getText().toString(),
                                                passwordEt.getText().toString()
                                        )
                                    );
                                    String deviceId = "0";
                                    try {
                                        TelephonyManager telephonyManager;
                                        telephonyManager = (TelephonyManager) getActivity().getSystemService(Context.
                                                TELEPHONY_SERVICE);
                                        deviceId = telephonyManager.getDeviceId();
                                    }catch (SecurityException e){
                                        e.printStackTrace();
                                    }

                                    SessionClass.setParam("userid",root.getString("ID"));
                                    SessionClass.setParam("name",root.getString("Name"));
                                    SessionClass.setParam("account",accountEt.getText().toString());
                                    SessionClass.setParam("password",passwordEt.getText().toString());
                                    SessionClass.setParam("pdaid",deviceId);
                                    SharedPreferences sharedPref = getActivity().getPreferences(getContext().MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPref.edit();
                                    editor.putString("whrs_selexped_account", accountEt.getText().toString());
                                    db.logDao().addLog(new LogTable("M","Bejelentkezés", SessionClass.getParam("account"), HelperClass.getCurrentDate(), HelperClass.getCurrentTime() ));
                                    editor.commit();
                                    Fragment f = new MainMenuFragment();
                                    FragmentManager fm = getFragmentManager();
                                    FragmentTransaction ft = fm.beginTransaction();
                                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
                                    ft.replace(R.id.fragments,f);
                                    ft.addToBackStack("app");
                                    ft.commit();
                                } else{
                                    errorTv.setText("Nem megfelelő Android verzió!");
                                    if(root.getString("URL")!=null){
                                        if(!root.getString("URL").equals("")) {
                                            newVersionDialog(root.getString("URL"));
                                        }
                                    }
                                }
                            } else {
                                errorTv.setText("Nem megfelelő Android verzió!");
                            }
                        }
                    }else{
                        errorTv.setText("Hiba a kapcsolódáskor!");
                    }
                } catch (JSONException e) {
                    errorTv.setText("Hiba a kapcsolódáskor!");
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error!=null){
                    errorTv.setText("Hiba a kapcsolódáskor!");
                    error.printStackTrace();
                }
            }
        });
        rq.add(jr);
    }

    private void forceRefresh(){
        KeyboardUtils.hideKeyboard(getActivity());
        RequestQueue rq = MySingleton.getInstance(getContext()).getRequestQueue();
        SessionClass.setParam("scancount_selectBar","");
        String terminal = SessionClass.getParam("terminal");
        String pdaid = SessionClass.getParam("pdaid");
        String url = SessionClass.getParam("WSUrl")+"/log_in/a/a/"+terminal+"/"+pdaid;
        Log.i("TAG",url);
        JSONObject jsonObject=null;
        JsonRequest<JSONObject> jr = new JsonObjectRequest(Request.Method.GET, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String rootText=response.getString("Log_in_Result");
                    Log.i("TAG",rootText);
                    if(!rootText.isEmpty()) {
                        JSONObject root = new JSONObject(rootText);
                        if (root.getString("version") != null) {
                            errorTv.setText("Nem megfelelő Android verzió!");
                        }
                    }
                } catch (JSONException e) {
                    errorTv.setText("Hiba a kapcsolódáskor!");
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error!=null){
                    errorTv.setText("Hiba a kapcsolódáskor!");
                    error.printStackTrace();
                }
            }
        });
        rq.add(jr);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().finish();
    }

    public void loadSettingPanel(){
        Fragment f = new SettingsFragment();
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
        ft.replace(R.id.fragments,f);
        ft.addToBackStack("app");
        ft.commit();
    }

    private void newVersionDialog(String url) {
        builder = new AlertDialog.Builder(getActivity());

        View v = getLayoutInflater().inflate(R.layout.dialog_newversion,null);
        pb = v.findViewById(R.id.newversion_progressBar);
        pbText = v.findViewById(R.id.newversion_progressBarPercentText);
        pbSubText = v.findViewById(R.id.newversion_progressBarSubText);
        dialogCloseBtn = v.findViewById(R.id.newversion_closeBtn);
        pb.setVisibility(View.VISIBLE);
        dialogCloseBtn.setVisibility(View.GONE);
        pbText.setVisibility(View.VISIBLE);
        dialogCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        builder.setView(v);
        builder.setTitle("Verzió frissítés");
        dialog = builder.create();
        dialog.show();
        new DownloadNewVersion(this,pb,pbText).execute(url);

    }

    @Override
    public void processFinish(Integer status) {
        if(status==-1) {
            try {
                Uri f;
                Intent intent;
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
                    f = FileProvider.getUriForFile(getActivity().getBaseContext(), getActivity().getApplicationContext().getPackageName() + ".hu.selester.android.webstockandroid.provider", new File(Environment.getExternalStorageDirectory() + "/Selester/" + "newversion.apk"));
                    Log.i("TAG",f.toString());
                    intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
                    intent.setData(f);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }else{
                    f = Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/Selester/" + "newversion.apk"));
                    intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(f, "application/vnd.android.package-archive");
                }
                startActivity(intent);
                dialog.dismiss();

            } catch (Exception e) {
                e.printStackTrace();
                pb.setVisibility(View.GONE);
                dialogCloseBtn.setVisibility(View.VISIBLE);
                pbText.setVisibility(View.GONE);
                pbSubText.setText("Hiba a verzió letöltésekor,\nkérlek jelezd a Selester Kft. felé!");

            }
        }else{
            pb.setVisibility(View.GONE);
            pbText.setVisibility(View.GONE);
            dialogCloseBtn.setVisibility(View.VISIBLE);
            pbSubText.setText("Hiba a verzió letöltésekor,\nkérlek jelezd a Selester Kft. felé!");
        }
    }
}
