package hu.selester.android.webstockandroid;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import hu.selester.android.webstockandroid.Database.SelesterDatabase;
import hu.selester.android.webstockandroid.Fragments.CheckPlaceFragment;
import hu.selester.android.webstockandroid.Fragments.ChkBarcodeFragment;
import hu.selester.android.webstockandroid.Fragments.DialogFragment;
import hu.selester.android.webstockandroid.Fragments.LoginFragment;
import hu.selester.android.webstockandroid.Fragments.MovesSubTableFragment;
import hu.selester.android.webstockandroid.Fragments.ScanCountFragment;
import hu.selester.android.webstockandroid.Fragments.SettingsFragment;
import hu.selester.android.webstockandroid.Helper.HelperClass;
import hu.selester.android.webstockandroid.Objects.ActiveFragment;

public class MainActivity extends FragmentActivity {

    private SelesterDatabase db;

    private final int pressedTesztCode = 300;
    private static final int REQUEST = 112;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Fragment f=new LoginFragment();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.fragments,f).addToBackStack("app").commit();
        db = SelesterDatabase.getDatabase(this);


        ImageView helpBtn = findViewById(R.id.help_btn);
        helpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HelperClass.tooltipBuild(getSupportFragmentManager().getFragments().get(0).getContext(), v,"Tartsa lenyomva az adott gombon az újad és megjelenik a segítség!", R.style.ToolTipLayoutCustomStyle);
            }
        });
    }

    private static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                getFragmentManager().popBackStack();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Fragment frg = ActiveFragment.getFragment();

        if(frg instanceof SettingsFragment){
            if( ((SettingsFragment) frg).scanListenerActive){
                if( ((DialogFragment)getSupportFragmentManager().getFragments().get(1)).scanTesterActive ){
                    try {
                        int scanButtonInt = ((DialogFragment) getSupportFragmentManager().getFragments().get(1)).scanBtnTempCode;
                        if(keyCode == scanButtonInt){
                            ((DialogFragment)getSupportFragmentManager().getFragments().get(1)).loadQR();
                        }
                    }catch(NumberFormatException e){

                    }

                } else {
                    ((DialogFragment) getSupportFragmentManager().getFragments().get(1)).scanBtnTempCode = keyCode;
                    ((DialogFragment) getSupportFragmentManager().getFragments().get(1)).tryButton();
                }
            }
        }else
        if(frg instanceof ChkBarcodeFragment) {
            try {
                if(keyCode == Integer.parseInt(db.systemDao().getValue("scanButtonCode"))){
                    ((ChkBarcodeFragment) frg).loadQR();
                }
            } catch (Exception e) { }
        }else
        if(frg instanceof MovesSubTableFragment) {
            try {
                if(keyCode == Integer.parseInt(db.systemDao().getValue("scanButtonCode"))){
                    ((MovesSubTableFragment) frg).loadQR();
                }
            } catch (Exception e) { }
        }
        if(frg instanceof CheckPlaceFragment) {
            try {
                if(keyCode == Integer.parseInt(db.systemDao().getValue("scanButtonCode"))){
                    ((CheckPlaceFragment) frg).loadQR();
                }
            } catch (Exception e) { }
        }
        if(frg instanceof ScanCountFragment) {
            try {
                if(keyCode == Integer.parseInt(db.systemDao().getValue("scanButtonCode"))){
                    ((ScanCountFragment) frg).loadQR();
                }
            } catch (Exception e) { }
        }
        //return super.onKeyDown(keyCode, event);
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //do here
                } else {
                    Toast.makeText(this, "The app was not allowed to read your store.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        //Toast.makeText(this,"DESTROY",Toast.LENGTH_LONG).show();
        super.onDestroy();
    }
}