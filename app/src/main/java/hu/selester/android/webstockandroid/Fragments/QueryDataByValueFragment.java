package hu.selester.android.webstockandroid.Fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import hu.selester.android.webstockandroid.Helper.HelperClass;
import hu.selester.android.webstockandroid.Helper.MySingleton;
import hu.selester.android.webstockandroid.Objects.SessionClass;
import hu.selester.android.webstockandroid.R;
import hu.selester.android.webstockandroid.TablePanel.TablePanel;
import mobil.selester.wheditbox.WHEditBox;

public class QueryDataByValueFragment extends Fragment {

    private WHEditBox findValue;
    private TablePanel tablePanel;
    private ProgressDialog pd;
    private View rootView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frg_querydata_by_value, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.i("TAG","onViewCreated");
        super.onViewCreated(view, savedInstanceState);
        rootView = view;
        WHEditBox.suffix = SessionClass.getParam("barcodeSuffix");
        WHEditBox.activity = getActivity();
        ImageView closeBtn = view.findViewById(R.id.checkstore_close);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().popBackStack();
            }
        });
        TextView findLabel = view.findViewById(R.id.checkstore_finderlabel);
        findLabel.setText(getArguments().getString("queryLabel")+":");
        findValue = view.findViewById(R.id.checkstore_header_value);
        findValue.setSelected(true);
        Log.i("TAG",""+getArguments().getInt("queryValueTrimFrom") +" - "+getArguments().getInt("queryValueTrimTo"));
        if( getArguments().getInt("queryValueTrimFrom") != 0 ) findValue.setTrimFrom(getArguments().getInt("queryValueTrimFrom"));
        if( getArguments().getInt("queryValueTrimTo") != 0 ) findValue.setTrimTo(getArguments().getInt("queryValueTrimTo"));
        findValue.setOnDetectBarcodeListener(new WHEditBox.OnDetectBarcodeListener() {
            @Override
            public void OnDetectBarcode(String value) {
                Log.i("TAG","VALUE: " + value);
                getDataOnServer(value);
                findValue.EDText.selectAll();
            }

            @Override
            public void OnDetectError(String errorResult, String value) {

            }

            @Override
            public void OnFocusOutListener(String value) {

            }

            @Override
            public void OnFocusInListener(String value) {

            }
        });
    }

    private void createTablePanel(){
        TablePanel.TablePanelSetting tps = tablePanel.getTablePanelSettingInstance();
        tps.setFontSize(14f);
        tps.setCheckable(false);
        tps.setCellLeftRightPadding(HelperClass.dpToPx(getContext(),6));
        tps.setCellTopBottomPadding(HelperClass.dpToPx(getContext(),6));
        tps.setOnHeaderClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TablePanel.SortResultObject sro = tablePanel.sortDataGrid((int)v.getTag());
                tablePanel.setRowSetting(sro.rowSettingData);
                tablePanel.getAdapter().updateData( sro.data, sro.checkData, sro.rowSettingData );
                tablePanel.showSortSign();
            }
        });
        tablePanel.setTablePanelSetting(tps);
        tablePanel.createTablePanel();
    }

    private void getDataOnServer(String queryValue){
        pd = HelperClass.loadingDialogOn(getActivity());

        String url = SessionClass.getParam("WSUrl") + "/" + getArguments().getString("queryEndURL") + "/" + SessionClass.getParam("terminal") + "/" + queryValue;
        //String url = "https://web-restapi-services-01.conveyor.cloud/Service1.svc/" + getArguments().getString("queryEndURL") + "/" + SessionClass.getParam("terminal") + "/" + queryValue;
        RequestQueue rq = MySingleton.getInstance(getContext()).getRequestQueue();
        Log.i("URL",url);

        JsonRequest<JSONObject> jr = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String rootJsonText = response.getString(getArguments().getString("queryEndURL") + "Result");
                    JSONArray jsonRoot = new JSONArray(rootJsonText);
                    if(jsonRoot.length() > 0){
                        List<String[]> tl = new ArrayList<>();
                        List<String> headerTextList = new ArrayList<>();




                        for(int i=0; i < jsonRoot.length(); i++){
                            Map<String, Object> map = HelperClass.jsonToMap( jsonRoot.getJSONObject(i) );
                            List<String> row = new ArrayList<>();
                            for (Map.Entry<String, Object> entry : map.entrySet()) {
                                if( i == 0 ){
                                    headerTextList.add(entry.getKey());
                                }
                                row.add(entry.getValue().toString());

                            }
                            Log.i("TAG", Arrays.toString( row.toArray(new String[0])) );
                            tl.add(row.toArray(new String[0]));
                        }
                        int[] headerWidthList = new int[headerTextList.size()];
                        for(int i=0; i < headerTextList.size(); i++){
                            headerWidthList[i] = tablePanel.WRAP_MAX_COLUMN;
                        }
                        tablePanel = new TablePanel(getContext(), rootView, R.id.checkstore_mainLayout, headerTextList.toArray(new String[0]), tl, headerWidthList );
                        createTablePanel();

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                pd.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if( error != null ){
                    error.printStackTrace();
                }
                pd.dismiss();
            }
        });
        jr.setRetryPolicy(new DefaultRetryPolicy(50000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rq.add(jr);

    }

}
