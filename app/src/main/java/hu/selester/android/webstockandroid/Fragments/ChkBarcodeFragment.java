package hu.selester.android.webstockandroid.Fragments;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.print.PrinterId;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.PrivilegedAction;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;


import hu.selester.android.webstockandroid.Database.SelesterDatabase;
import hu.selester.android.webstockandroid.Database.Tables.EansTable;
import hu.selester.android.webstockandroid.Database.Tables.SystemTable;
import hu.selester.android.webstockandroid.Database.Tables.UsersTable;
import hu.selester.android.webstockandroid.Helper.HelperClass;
import hu.selester.android.webstockandroid.Helper.KeyboardUtils;
import hu.selester.android.webstockandroid.Helper.MySingleton;
import hu.selester.android.webstockandroid.Objects.SessionClass;
import hu.selester.android.webstockandroid.R;
import android.app.AlertDialog.Builder;

import static hu.selester.android.webstockandroid.R.color.secondColor;

public class ChkBarcodeFragment extends Fragment {


    private TextView errorTv;
    private EditText barcodeEt;
    private Button bar1Btn;
    private Button bar2Btn;
    private ImageButton editable1;
    private ImageButton lock;
    private Button endChk;
    private ImageView resultImage;
    private TextView resultText;
    private int activeButton = 1;
    private int counter = 1;
    private SelesterDatabase db;
    private final String LOG_TAG = "Selester / " + this.getClass().getCanonicalName();
    private String tranid,movenum;
    private SwitchCompat trimFunction1, trimFunction2;
    private Button trimET11, trimET21, trimET12, trimET22;
    private boolean trimBarcode1,trimBarcode2;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        trimBarcode1 = false;trimBarcode2 = false;
        View rootView = inflater.inflate(R.layout.frg_chkbarcode, container, false);
        tranid = getArguments().getString("tranid");
        movenum = getArguments().getString("movenum");
        db = SelesterDatabase.getDatabase(getActivity());
        db.eansDao().deleteAll();
        barcodeEt = rootView.findViewById(R.id.barText);
        resultImage = rootView.findViewById(R.id.barcode_resultImage);
        trimET11 = rootView.findViewById(R.id.barcode_trim11);
        trimET21 = rootView.findViewById(R.id.barcode_trim21);
        trimET12 = rootView.findViewById(R.id.barcode_trim12);
        trimET22 = rootView.findViewById(R.id.barcode_trim22);
        trimET11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buildDialogTrimBtn(1);
            }
        });
        trimET21.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buildDialogTrimBtn(2);
            }
        });
        trimET12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buildDialogTrimBtn(3);
            }
        });
        trimET22.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buildDialogTrimBtn(4);
            }
        });

        resultText = rootView.findViewById(R.id.barcode_resultText);
        resultText.setText(null);
        resultImage.setImageDrawable(null);
        trimFunction1 = rootView.findViewById(R.id.barcode_enableTrim1);
        trimFunction1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    trimET11.setEnabled(true);
                    trimET21.setEnabled(true);
                    trimBarcode1 = true;
                }else{
                    trimET11.setEnabled(false);
                    trimET21.setEnabled(false);
                    trimBarcode1 = false;
                }
            }
        });
        trimFunction2 = rootView.findViewById(R.id.barcode_enableTrim2);
        trimFunction2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    trimET12.setEnabled(true);
                    trimET22.setEnabled(true);
                    trimBarcode2 = true;
                }else{
                    trimET12.setEnabled(false);
                    trimET22.setEnabled(false);
                    trimBarcode2 = false;
                }
            }
        });
        editable1 = rootView.findViewById(R.id.barcode_editable1);
        lock = rootView.findViewById(R.id.barcode_lock);
        endChk = rootView.findViewById(R.id.barcode_endChk);
        editable1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buildDialog();
            }
        });




        if(tranid.equals("") || tranid.isEmpty() ){
            endChk.setText("Kilépés");
            endChk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getFragmentManager().popBackStack();
                }
            });
        }else{
            endChk.setText("Ellenőrzéssel végeztem");
            endChk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String a = db.eansDao().getAllData().toString();
                    sendEans(a);
                    Toast.makeText(getActivity(), "Várjon!", Toast.LENGTH_SHORT).show();
                }
            });
        }



        lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if (bar1Btn.getText().toString().equals(getResources().getString(R.string.buttonLabel))) {
                Toast.makeText(getActivity(), "Nincs kiválasztva az eredeti vonalkód!", Toast.LENGTH_LONG).show();
            } else {
                if (bar1Btn.isEnabled()) {
                    lock.setImageDrawable(getResources().getDrawable(R.drawable.lock_on));
                    bar1Btn.setEnabled(false);
                    bar2Btn.performClick();
                    resultImage.setImageResource(0);
                    resultText.setText("");

                } else {
                    lock.setImageDrawable(getResources().getDrawable(R.drawable.lock_off));
                    bar1Btn.setEnabled(true);
                    bar1Btn.performClick();
                    bar1Btn.setText(getResources().getString(R.string.buttonLabel));
                    bar2Btn.setText(getResources().getString(R.string.buttonLabel));
                }
            }
            }
        });
        bar1Btn = rootView.findViewById(R.id.barcode1);
        bar1Btn.setText(getResources().getString(R.string.buttonLabel));
        bar2Btn = rootView.findViewById(R.id.barcode2);
        bar2Btn.setText(getResources().getString(R.string.buttonLabel));

        barcodeEt.requestFocus();
        barcodeEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!barcodeEt.getText().toString().equals("")) {
                    barcodeEt.requestFocus();
                    String isBar = HelperClass.isBarcode(s.toString());
                    if (activeButton == 2) {
                        if(trimBarcode2){
                            try {
                                int x = Integer.parseInt(trimET22.getText().toString());
                                if(x>isBar.length()){ x=isBar.length(); }
                                Toast.makeText(getContext(),isBar,Toast.LENGTH_LONG).show();
                                isBar = isBar.substring(Integer.parseInt(trimET12.getText().toString())-1, x);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                        bar2Btn.setText(isBar);
                        checkBarcodes();
                        bar1Btn.performClick();
                    } else {
                        if(trimBarcode1){
                            try {
                                int x = Integer.parseInt(trimET21.getText().toString());
                                if(x>isBar.length()){ x=isBar.length(); }
                                Toast.makeText(getContext(),isBar,Toast.LENGTH_LONG).show();
                                isBar = isBar.substring(Integer.parseInt(trimET11.getText().toString())-1, x);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                        bar1Btn.setText(isBar);
                        bar2Btn.setText(getResources().getString(R.string.buttonLabel));
                        bar2Btn.performClick();
                        resultImage.setImageResource(0);
                        resultText.setText("");
                    }
                    barcodeEt.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        bar1Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bar1Btn.isEnabled()) {
                    activeButton = 1;
                    bar1Btn.setBackgroundColor(Color.YELLOW);
                    bar1Btn.setTextColor(Color.BLACK);
                    bar2Btn.setBackgroundColor(getResources().getColor(R.color.secondColor));
                    bar2Btn.setTextColor(Color.WHITE);

                }
            }
        });
        bar2Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bar1Btn.getText().toString().equals(getResources().getString(R.string.buttonLabel))) {
                    Toast.makeText(getActivity(), "Nincs kiválasztva az eredeti vonalkód!", Toast.LENGTH_LONG).show();
                } else {
                    activeButton = 2;
                    bar2Btn.setBackgroundColor(Color.YELLOW);
                    bar2Btn.setTextColor(Color.BLACK);
                    bar1Btn.setBackgroundColor(getResources().getColor(R.color.secondColor));
                    bar1Btn.setTextColor(Color.WHITE);
                }
            }
        });
        bar1Btn.performClick();
        return rootView;
    }


    void buildDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Kérem gépelje be a vonalkódot");
        final EditText input = new EditText(getActivity());
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                KeyboardUtils.hideKeyboard(getActivity());
                CharSequence edtext = input.getText();
                if (activeButton == 1) {
                    if(trimBarcode1){
                        try {
                            int x = Integer.parseInt(trimET21.getText().toString());
                            if(x>edtext.length()){ x=edtext.length(); }
                            edtext = edtext.subSequence(Integer.parseInt(trimET11.getText().toString())-1, x);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    bar1Btn.setText(edtext);
                    bar2Btn.performClick();

                    bar2Btn.setText(getResources().getString(R.string.buttonLabel));
                    resultImage.setImageResource(0);
                    resultText.setText("");

                } else {
                    if(trimBarcode2){
                        try {
                            int x = Integer.parseInt(trimET22.getText().toString());
                            if(x>edtext.length()){ x=edtext.length(); }
                            edtext = edtext.subSequence(Integer.parseInt(trimET12.getText().toString())-1, x);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    bar2Btn.setText(edtext);
                    checkBarcodes();
                    bar1Btn.performClick();
                }
            }
        });
        builder.setNegativeButton("Mégsem", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                KeyboardUtils.hideKeyboard(getActivity());
                dialog.cancel();
            }
        });

        builder.show();
    }

    void buildDialogTrimBtn(final int tag) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Kérem válassza ki a vágás értékét!");
        //final EditText input = new EditText(getActivity());
        final NumberPicker np = new NumberPicker(getActivity());

        np.setMinValue(1);
        np.setMaxValue(20);
        try{
            if(tag == 1){
                np.setValue(Integer.parseInt(trimET11.getText().toString()));
            }else if(tag == 2){
                np.setValue(Integer.parseInt(trimET21.getText().toString()));
            }else if(tag == 3){
                np.setValue(Integer.parseInt(trimET12.getText().toString()));
            }else if(tag == 4){
                np.setValue(Integer.parseInt(trimET22.getText().toString()));
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        builder.setView(np);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                KeyboardUtils.hideKeyboard(getActivity());
                CharSequence edtext = String.valueOf(np.getValue());
                if(tag==1){
                    trimET11.setText(edtext);
                }else if(tag==2){
                    trimET21.setText(edtext);
                }else if(tag==3){
                    trimET12.setText(edtext);
                }else if(tag==4){
                    trimET22.setText(edtext);
                }

            }
        });
        builder.setNegativeButton("Mégsem", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                KeyboardUtils.hideKeyboard(getActivity());
                dialog.cancel();
            }
        });

        builder.show();
    }

    void checkBarcodes() {
        KeyboardUtils.hideKeyboard(getActivity());
        String prodid = db.productDataDAO().getBarcodeProd(bar1Btn.getText().toString());
        String[] barCodes = db.productDataDAO().getProdBarcode(prodid);
        if (bar1Btn.getText().toString().equals(bar2Btn.getText().toString())) {

            // DateFormat df = new SimpleDateFormat("MMddyyyyHHmmss");
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss");
            Date currentTime = Calendar.getInstance().getTime();
            String reportDate = df.format(currentTime);
            db.eansDao().setEansData(new EansTable(counter, bar1Btn.getText().toString(), bar2Btn.getText().toString(), "'" + reportDate + "'"));
            counter = counter + 1;
            resultImage.setImageResource(R.drawable.accept);
            resultText.setText("Vonalkód rendben!");
        } else {
            if (Arrays.asList(barCodes).contains(bar2Btn.getText().toString())) {
                resultImage.setImageResource(R.drawable.accept);
                resultText.setText("Vonalkód rendben!");
            } else {
                resultImage.setImageResource(R.drawable.warningicon);
                HelperClass.errorSound(getActivity());
                resultText.setText("Vonalkód nem egyezik!");
            }
        }

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


    private void sendEans(String eans){
        RequestQueue rq = MySingleton.getInstance(getContext()).getRequestQueue();
        String userid = SessionClass.getParam("userid");
        String url = SessionClass.getParam("WSUrl") + "/WRHS_PDA_POST_SCANNED_EANS";
        HashMap<String,String> map = new HashMap<>();
        map.put("EANS",eans);
        map.put("movenr",movenum);
        map.put("Tranid",tranid);
        map.put("userid",SessionClass.getParam("userid"));

        JsonRequest<JSONObject> jr = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(map), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String rootText=response.getString("WRHS_PDA_POST_SCANNED_EANSResult");
                    JSONObject jsonObject = new JSONObject(rootText);
                    String rtext = jsonObject.getString("message");
                    if(!rtext.isEmpty()){
                        if(rtext.equals("TESZT OK!")){
                            Toast.makeText(getContext(),"Adatok áttöltése sikeres!!",Toast.LENGTH_LONG).show();
                            getFragmentManager().popBackStack();
                        }else{

                            Toast.makeText(getContext(),"Adatok áttöltése sikertelen, kérem jelezze a Selesternek!",Toast.LENGTH_LONG).show();
                        }

                    }else{
                        Toast.makeText(getContext(),"Adatok áttöltése sikertelen, kérem jelezze a Selesternek!",Toast.LENGTH_LONG).show();
                    }


                } catch (JSONException e) {
                    Toast.makeText(getContext(),"Adatok áttöltése sikertelen, kérem jelezze a Selesternek!",Toast.LENGTH_LONG).show();
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
                barcodeEt.setText(result.getContents()+SessionClass.getParam("barcodeSuffix"));
            }
        }else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}