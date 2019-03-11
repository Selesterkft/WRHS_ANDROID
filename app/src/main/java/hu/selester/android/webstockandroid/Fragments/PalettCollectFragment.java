package hu.selester.android.webstockandroid.Fragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CircularProgressDrawable;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import hu.selester.android.webstockandroid.Adapters.CollectListAdapter;
import hu.selester.android.webstockandroid.AsyncTask.LoadingParams;
import hu.selester.android.webstockandroid.Database.SelesterDatabase;
import hu.selester.android.webstockandroid.Database.Tables.LogTable;
import hu.selester.android.webstockandroid.Database.Tables.PalettTable;
import hu.selester.android.webstockandroid.Helper.HelperClass;
import hu.selester.android.webstockandroid.Helper.KeyboardUtils;
import hu.selester.android.webstockandroid.Objects.AllLinesData;
import hu.selester.android.webstockandroid.Objects.CheckedList;
import hu.selester.android.webstockandroid.Objects.SessionClass;
import hu.selester.android.webstockandroid.R;
import hu.selester.android.webstockandroid.Threads.SaveAllSessionTemp;
import hu.selester.android.webstockandroid.Threads.SaveCheckedDataThread;
import hu.selester.android.webstockandroid.Threads.SaveIdSessionTemp;

public class PalettCollectFragment extends Fragment implements LoadingParams.AsyncResponse{

    private boolean locked = false;
    private ImageView lockBtn;
    private AppCompatEditText barcodeET1, barcodeET2;
    private ListView palettList;
    private List<String> dataList = new ArrayList<>();
    private TextView headerText;
    private int qBarcode01, qBarcode02, tranCode;
    private CollectListAdapter collectListAdapter;
    private SelesterDatabase db;
    private Button nextBtn;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frg_palett, container,false);
        db = SelesterDatabase.getDatabase(getContext());
        palettList = rootView.findViewById(R.id.palett_list);
        lockBtn = rootView.findViewById(R.id.palett_lock);
        tranCode = Integer.parseInt(SessionClass.getParam("tranCode"));
        qBarcode01 = HelperClass.getArrayPosition("Barcode01", SessionClass.getParam(tranCode + "_Line_ListView_SELECT"));
        qBarcode02 = HelperClass.getArrayPosition("Barcode02", SessionClass.getParam(tranCode + "_Line_ListView_SELECT"));
        headerText = rootView.findViewById(R.id.palett_textView);
        barcodeET1 = rootView.findViewById(R.id.palett_barcode1);
        barcodeET2 = rootView.findViewById(R.id.palett_barcode2);
        nextBtn = rootView.findViewById(R.id.movessub_header_btn);
        barcodeET2.requestFocus();
        ImageView del2Btn = rootView.findViewById(R.id.palett_delBtn2);
        del2Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                barcodeET2.setText("");
                barcodeET2.requestFocus();
            }
        });
        ImageView del1Btn = rootView.findViewById(R.id.palett_delBtn1);
        del1Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                barcodeET1.setText("");
                barcodeET1.requestFocus();
            }
        });
        KeyboardUtils.hideKeyboard(getActivity());
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
                        headerText.setText("Összeredelés / " + AllLinesData.getPlaceCount(qBarcode02, 99999, barcodeET2.getText().toString()) );
                    }
                }
            }
        });
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String bar = barcodeET1.getText().toString();
                addData(bar);
            }
        });
        barcodeET2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus){
                    KeyboardUtils.hideKeyboard(getActivity());
                    barcodeET1.requestFocus();
                    if( !barcodeET2.getText().toString().equals("") ) {
                        dataList = AllLinesData.getParamsPosition(qBarcode02, qBarcode01, barcodeET2.getText().toString());
                        ((CollectListAdapter) palettList.getAdapter()).updateItems(dataList);
                    }else{
                        ((CollectListAdapter) palettList.getAdapter()).clearItems();
                    }
                    headerText.setText("Összeredelés / " + AllLinesData.getPlaceCount(qBarcode02, 99999, barcodeET2.getText().toString()) );
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
                            headerText.setText("Összeredelés / " + AllLinesData.getPlaceCount(qBarcode02, 99999, barcodeET2.getText().toString()) );
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
                        barcodeET1.removeTextChangedListener(this);
                        addData(bar);
                        barcodeET1.addTextChangedListener(this);
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
                            addData(bar);
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
        collectListAdapter = new CollectListAdapter(getContext(),dataList);
        palettList.setAdapter(collectListAdapter);
        return rootView;
    }

    private void addData(String bar) {
        barcodeET1.setText(bar);
        if( AllLinesData.isValidateValue(qBarcode01, bar) ) {
            try {
                List<String> l = AllLinesData.getParamsPosition(qBarcode01, qBarcode02, bar);
                String chkItem = "";
                if (l != null && l.size() > 0) chkItem = l.get(0);
                if (!chkItem.equals("") && !chkItem.equals(bar)) {
                    findThisCollectDialog();
                } else {
                    KeyboardUtils.hideKeyboard(getActivity());
                    if( !bar.equals("") ) {
                        AllLinesData.setParamsPosition(qBarcode01, qBarcode02, bar, barcodeET2.getText().toString());
                        ProgressDialog pd = HelperClass.loadingDialogOn(getActivity());
                        LoadingParams lp = new LoadingParams(this, pd, qBarcode02, qBarcode01, barcodeET2.getText().toString() );
                        lp.execute();
                    }
                    if (!locked) {
                        barcodeET2.setText("");
                        barcodeET2.requestFocus();
                        collectListAdapter.clearItems();
                    }else{
                        barcodeET1.setText("");
                        barcodeET1.requestFocus();
                    }
                }
            }catch (Exception e){
                db.logDao().addLog(new LogTable(LogTable.LogType_Error,"PalettCollectFragment",e.getMessage(),"LOGUSER",null,null));
                e.printStackTrace();
            }
        }else{
            Toast.makeText(getContext(), "Nincs ilyen gyűjtő létrehozva!", Toast.LENGTH_LONG).show();
            barcodeET1.setText( "" );
        }
    }

    @Override
    public void processFinish(List<String> dataList) {
        List<String> dataIDSStringList = AllLinesData.getParamsPosition(qBarcode01, 0, barcodeET1.getText().toString());
        List<Long> dataIDSList = new ArrayList<>();
        for(int j = 0 ;j<dataIDSStringList.size(); j++){
            dataIDSList.add(Long.parseLong(dataIDSStringList.get(j)));
            CheckedList.setParamItem(dataIDSStringList.get(j),1);
        }
        if( !barcodeET2.getText().toString().equals("") ) {
            ((CollectListAdapter) palettList.getAdapter()).updateItems(dataList);
        }else{
            ((CollectListAdapter) palettList.getAdapter()).clearItems();
        }
        barcodeET1.setText( "" );
        SaveIdSessionTemp savePalett = new SaveIdSessionTemp(getContext());
        savePalett.setId(dataIDSList);
        savePalett.start();
        headerText.setText("Összeredelés / " + AllLinesData.getPlaceCount(qBarcode02, 99999, barcodeET2.getText().toString()) );
        new SaveCheckedDataThread(getContext()).start();
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