package hu.selester.android.webstockandroid.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import hu.selester.android.webstockandroid.Database.SelesterDatabase;
import hu.selester.android.webstockandroid.Database.Tables.SystemTable;
import hu.selester.android.webstockandroid.Objects.ActiveFragment;
import hu.selester.android.webstockandroid.Objects.SessionClass;
import hu.selester.android.webstockandroid.R;

public class DialogFragment extends Fragment {

    private TextView title,message;
    private LinearLayout layoutBtn1,layoutBtn2;
    public boolean scanTesterActive;
    public int scanBtnTempCode;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.i("TAG","CREATE");
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_scanbtn,container,false);
        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("TAG","FRG CLICK");
            }
        });
        scanBtnTempCode=0;
        layoutBtn1 = rootView.findViewById(R.id.dialog_scanBtn_btn1);
        layoutBtn2 = rootView.findViewById(R.id.dialog_scanBtn_btn2);
        layoutBtn1.setVisibility(View.VISIBLE);
        layoutBtn2.setVisibility(View.GONE);
        Button okBtn = rootView.findViewById(R.id.dialog_scanBtn_OK);
        Button nokBtn = rootView.findViewById(R.id.dialog_scanBtn_NOK);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveScanBarcodeBtn();
            }
        });
        nokBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newScanBarcodeBtn();
            }
        });
        title = rootView.findViewById(R.id.dialog_title);
        message = rootView.findViewById(R.id.dialog_message);
        title.setText("Vonalkódolvasó kalibrálás");
        message.setText("Nyomja meg azt a gombot, amivel használni akarja a vonalkódolvasó funkciót!");
        Button cancelBtn = rootView.findViewById(R.id.dialog_scanBtn_cancel);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFragment();
            }
        });
        Log.i("TAG","CREATE VIEW");
        return rootView;
    }

    public void tryButton(){
        scanTesterActive=true;
        title.setText("Vonalkódolvasó tesztelés");
        message.setText("Nyomja meg a kiválasztott gombot, a funkció teszteléséhez!");

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("WS",requestCode+" - "+resultCode);
        title.setText("Sikerült a tesztelés?");
        layoutBtn2.setVisibility(View.VISIBLE);
        layoutBtn1.setVisibility(View.GONE);

        IntentResult result= IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if(result!=null){
            if(result.getContents()==null){
                message.setText("Nem történt beolvasás!");
                Log.i("TAG","ERROR");
            }else{
                message.setText("Az ön által beolvasott kód: "+result.getContents());
                Log.i("TAG",result.getContents());
            }
        }else{
            super.onActivityResult(requestCode, resultCode, data);
        }

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


    private void saveScanBarcodeBtn(){
        Log.i("SCANBTN","YES");
        getActivity().getSupportFragmentManager().getFragments().get(1);
        ((SettingsFragment)getFragmentManager().getFragments().get(0)).scanBtnCode = scanBtnTempCode;
        Log.i("SCANBTN","SAVE CODE:"+scanBtnTempCode);
        closeFragment();
    }

    private void newScanBarcodeBtn(){
        Log.i("SCANBTN","NO");
        scanTesterActive=false;
        scanBtnTempCode=0;
        title.setText("Vonalkódolvasó kalibrálás");
        message.setText("Nyomja meg azt a gombot, amivel használni akarja a vonalkódolvasó funkciót!");
        layoutBtn1.setVisibility(View.VISIBLE);
        layoutBtn2.setVisibility(View.GONE);
    }

    private void closeFragment(){
        scanTesterActive=false;
        ((SettingsFragment) ActiveFragment.getFragment()).scanListenerActive = false;
        Fragment f = getActivity().getSupportFragmentManager().getFragments().get(1);
        getActivity().getSupportFragmentManager().beginTransaction().remove(f).commit();
    }

}
