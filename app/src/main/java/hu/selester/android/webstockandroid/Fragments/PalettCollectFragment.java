package hu.selester.android.webstockandroid.Fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.LongSparseArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import hu.selester.android.webstockandroid.Adapters.CollectListAdapter;
import hu.selester.android.webstockandroid.Helper.HelperClass;
import hu.selester.android.webstockandroid.Helper.KeyboardUtils;
import hu.selester.android.webstockandroid.Objects.AllLinesData;
import hu.selester.android.webstockandroid.Objects.SessionClass;
import hu.selester.android.webstockandroid.R;
import hu.selester.android.webstockandroid.Threads.SaveIdSessionTemp;

public class PalettCollectFragment extends Fragment {

    private boolean locked = false;
    private ImageView lockBtn;
    private AppCompatEditText barcodeET1, barcodeET2;
    private ListView palettList;
    private List<String> dataList = new ArrayList<>();
    private int qBarcode01, qBarcode02, tranCode;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frg_palett, container,false);
        palettList = rootView.findViewById(R.id.palett_list);
        lockBtn = rootView.findViewById(R.id.palett_lock);
        tranCode = Integer.parseInt(SessionClass.getParam("tranCode"));
        qBarcode01 = HelperClass.getArrayPosition("Barcode01", SessionClass.getParam(tranCode + "_Line_ListView_SELECT"));
        qBarcode02 = HelperClass.getArrayPosition("Barcode02", SessionClass.getParam(tranCode + "_Line_ListView_SELECT"));
        barcodeET1 = rootView.findViewById(R.id.palett_barcode1);
        barcodeET2 = rootView.findViewById(R.id.palett_barcode2);
        barcodeET2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 3) {
                    if (s.toString().substring(s.length() - SessionClass.getParam("barcodeSuffix").length(), s.length()).equals(SessionClass.getParam("barcodeSuffix"))) {
                        String bar = s.toString().substring(0, s.length() - SessionClass.getParam("barcodeSuffix").length() );
                        KeyboardUtils.hideKeyboard(getActivity());
                        barcodeET2.removeTextChangedListener(this);
                        barcodeET2.setText(bar);
                        barcodeET2.addTextChangedListener(this);
                        barcodeET1.requestFocus();
                        dataList = AllLinesData.getParamsPosition(qBarcode02, qBarcode01, barcodeET2.getText().toString());
                        if( dataList.size() > 0 ) ((CollectListAdapter) palettList.getAdapter()).updateItems(dataList);
                    }
                }
            }
        });

        barcodeET2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus){
                    KeyboardUtils.hideKeyboard(getActivity());
                    barcodeET1.requestFocus();
                    dataList = AllLinesData.getParamsPosition(qBarcode02, qBarcode01, barcodeET2.getText().toString());
                    Log.i("TAG", "SIZE "+dataList.size());
                    ((CollectListAdapter) palettList.getAdapter()).updateItems(dataList);
                }
            }
        });

        barcodeET2.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if (event.getAction() == KeyEvent.ACTION_DOWN)
                {
                    switch (keyCode)
                    {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            KeyboardUtils.hideKeyboard(getActivity());
                            barcodeET1.requestFocus();
                            dataList = AllLinesData.getParamsPosition(qBarcode02, qBarcode01, barcodeET2.getText().toString());
                            if( dataList.size() > 0 ) ((CollectListAdapter) palettList.getAdapter()).updateItems(dataList);
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });

        barcodeET1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 3) {
                    if (s.toString().substring(s.length() - SessionClass.getParam("barcodeSuffix").length(), s.length()).equals(SessionClass.getParam("barcodeSuffix"))) {
                        String bar = s.toString().substring(0, s.length() - SessionClass.getParam("barcodeSuffix").length() );
                        if( AllLinesData.isValidateValue(qBarcode01, bar) ) {
                            barcodeET1.removeTextChangedListener(this);
                            try {
                                barcodeET1.setText(s.toString().substring(0, s.length() - SessionClass.getParam("barcodeSuffix").length()));
                                List<String> l = AllLinesData.getParamsPosition(qBarcode01, qBarcode02, barcodeET1.getText().toString());
                                String chkItem = "";
                                if (l != null && l.size() > 0) chkItem = l.get(0);
                                if (!chkItem.equals("") && !chkItem.equals(barcodeET1.getText().toString())) {
                                    findThisCollectDialog();
                                } else {
                                    KeyboardUtils.hideKeyboard(getActivity());
                                    addItem();
                                    if (!locked) {
                                        barcodeET2.requestFocus();
                                    }
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            barcodeET1.addTextChangedListener(this);
                        }else{
                            Toast.makeText(getContext(), "Nincs ilyen gyűjtő létrehozva!", Toast.LENGTH_LONG).show();
                            barcodeET1.setText( "" );
                        }
                    }
                }
            }
        });
        barcodeET1.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if (event.getAction() == KeyEvent.ACTION_DOWN)
                {
                    switch (keyCode)
                    {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            String bar = barcodeET1.getText().toString();
                            if( AllLinesData.isValidateValue(qBarcode01, bar) ) {
                                try {
                                    List<String> l = AllLinesData.getParamsPosition(qBarcode01, qBarcode02, barcodeET1.getText().toString());
                                    String chkItem = "";
                                    if (l != null && l.size() > 0) chkItem = l.get(0);
                                    if (!chkItem.equals("") && !chkItem.equals(barcodeET1.getText().toString())) {
                                        findThisCollectDialog();
                                    } else {
                                        KeyboardUtils.hideKeyboard(getActivity());
                                        addItem();
                                        if (!locked) {
                                            barcodeET2.requestFocus();
                                        }
                                    }
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }else{
                                Toast.makeText(getContext(), "Nincs ilyen gyűjtő létrehozva!", Toast.LENGTH_LONG).show();
                                barcodeET1.setText( "" );
                            }
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });

        ImageView exitBtn = rootView.findViewById(R.id.palett_exit);
        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        lockBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(locked){
                    lockBtn.setImageDrawable(getResources().getDrawable(R.drawable.lock_off, null));
                    locked = false;
                    barcodeET2.setEnabled(true);
                    barcodeET2.requestFocus();
                }else{
                    if( !barcodeET2.getText().toString().equals("") ) {
                        lockBtn.setImageDrawable(getResources().getDrawable(R.drawable.lock_on, null));
                        locked = true;
                        barcodeET2.setEnabled(false);
                        barcodeET1.requestFocus();
                    }else{
                        Toast.makeText(getContext(),"Rakhely kód nincs meghatározva!", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        CollectListAdapter collectListAdapter = new CollectListAdapter(getContext(),dataList);
        palettList.setAdapter(collectListAdapter);
        return rootView;
    }

    private void addItem(){
        if( !barcodeET1.getText().toString().equals("") ) {
            AllLinesData.setParamsPosition(qBarcode01, qBarcode02, barcodeET1.getText().toString(), barcodeET2.getText().toString());
            dataList = AllLinesData.getParamsPosition(qBarcode02, qBarcode01, barcodeET2.getText().toString());
            List<String> dataIDSStringList = AllLinesData.getParamsPosition(qBarcode01, 0, barcodeET1.getText().toString());
            List<Long> dataIDSList = new ArrayList<>();
            for(int j = 0 ;j<dataIDSStringList.size(); j++){
                dataIDSList.add(Long.parseLong(dataIDSStringList.get(j)));
            }
            SaveIdSessionTemp sit = new SaveIdSessionTemp(getContext());
            sit.setId( dataIDSList );
            sit.start();

            ((CollectListAdapter) palettList.getAdapter()).updateItems(dataList);
            barcodeET1.setText( "" );
        }
    }

    void findThisCollectDialog() {
        KeyboardUtils.hideKeyboard(getActivity());
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Hiba!");
        builder.setMessage("Ehhez a gyűjtőhöz már van raklap rendelve, kérem először törölje!");
        builder.setNegativeButton("Rendben", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                barcodeET1.setText("");
                dialog.cancel();
            }
        });
        builder.show();

    }

}