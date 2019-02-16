package hu.selester.android.webstockandroid.Fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
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

import hu.selester.android.webstockandroid.Helper.HelperClass;
import hu.selester.android.webstockandroid.Helper.KeyboardUtils;
import hu.selester.android.webstockandroid.Helper.MySingleton;
import hu.selester.android.webstockandroid.Objects.DefaultTextWatcher;
import hu.selester.android.webstockandroid.Objects.SessionClass;
import hu.selester.android.webstockandroid.R;

public class CrossDockSelectFragment extends Fragment {

    private View rootView;
    private ProgressDialog pd;
    private TextView licencNum, sumWeight, sumCount;
    private EditText rampNum, orderId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.frg_crossdockselect, container, false);
        licencNum = rootView.findViewById(R.id.cd_licencenum);
        sumCount = rootView.findViewById(R.id.cd_sumcount);
        sumWeight = rootView.findViewById(R.id.cd_sumweight);
        orderId = rootView.findViewById(R.id.cd_orderid);
        orderId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderId.selectAll();
            }
        });
        orderId.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                Log.i("TAG", keyCode + " - " + KeyEvent.KEYCODE_ENTER);
                if (event.getAction()!=KeyEvent.ACTION_DOWN) return true;
                if(keyCode == KeyEvent.KEYCODE_ENTER){
                    getTaskData();
                    KeyboardUtils.hideKeyboard(getActivity());
                    return true;
                }
                return false;
            }
        });
        orderId.addTextChangedListener(new DefaultTextWatcher(orderId, new DefaultTextWatcher.TextChangedEvent() {
            @Override
            public void Changed() {
                getTaskData();
            }
        }));
        rampNum = rootView.findViewById(R.id.cd_rampnum);
        rampNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rampNum.selectAll();
            }
        });
        rampNum.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_ENTER){
                    if (event.getAction()!=KeyEvent.ACTION_DOWN) return true;
                    setRampNum();
                    KeyboardUtils.hideKeyboard(getActivity());
                    return true;
                }
                return false;
            }
        });
        rampNum.addTextChangedListener(new DefaultTextWatcher(rampNum, new DefaultTextWatcher.TextChangedEvent() {
            @Override
            public void Changed() {
                setRampNum();
            }
        }));
        getTaskData();
        return rootView;
    }

    private void setRampNum() {
    }

    void getTaskData(){
        String terminal = SessionClass.getParam("terminal");
        String pdaid = SessionClass.getParam("pdaid");
        String userid = SessionClass.getParam("userid");
        //String ordid = "1116323141";
        String ordid = orderId.getText().toString();
        String url = SessionClass.getParam("WSUrl")+"/WRHS_PDA_XD_getTask/" + terminal + "/" + userid + "/" + ordid + "/" + pdaid + "/License_Num,Weight,Pcs/WRHS_PDA_XD_INSTRUCTIONS/nothing/ord_num";
        Log.i("URL",url);
        RequestQueue rq = MySingleton.getInstance(getContext()).getRequestQueue();
        pd = HelperClass.loadingDialogOn(getActivity());
        JsonRequest<JSONObject> jr = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String rootText=response.getString("WRHS_PDA_XD_getTaskResult");
                    JSONArray jsonArray = new JSONArray(rootText);
                    int colli = 0;
                    int w = 0;
                    for(int i=0; i < jsonArray.length(); i++){
                        w += jsonArray.getJSONObject(i).getInt("Weight");
                        colli += jsonArray.getJSONObject(i).getInt("Pcs");
                    }
                    licencNum.setText(jsonArray.getJSONObject(0).getString("License_Num"));
                    sumCount.setText("" + colli + " colli");
                    sumWeight.setText("" + w + " kg");
                } catch (JSONException e) {
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
