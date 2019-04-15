package hu.selester.android.webstockandroid.Fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import hu.selester.android.webstockandroid.Adapters.TaskMenuAdapter;
import hu.selester.android.webstockandroid.Helper.KeyboardUtils;
import hu.selester.android.webstockandroid.Helper.MySingleton;
import hu.selester.android.webstockandroid.Objects.AllLinesData;
import hu.selester.android.webstockandroid.Objects.SessionClass;
import hu.selester.android.webstockandroid.Objects.TaskObject;
import hu.selester.android.webstockandroid.R;

public class TasksFragment extends Fragment {

    View rootView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.frg_tasks,container,false);

        String terminal = SessionClass.getParam("terminal");
        String pdaid = SessionClass.getParam("pdaid");
        String userid = SessionClass.getParam("userid");
        String url = SessionClass.getParam("WSUrl")+"/get_task/"+terminal+"/"+userid+"/"+pdaid;
        Log.i("URL",url);
        ImageButton backBtn = rootView.findViewById(R.id.tasks_back);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });
        RequestQueue rq = MySingleton.getInstance(getContext()).getRequestQueue();
        JSONObject jsonObject=null;
        JsonRequest<JSONObject> jr = new  JsonObjectRequest(Request.Method.GET, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String rootText=response.getString("get_task_Result");
                    JSONArray jsonArray = new JSONArray(rootText);
                    List<TaskObject> list = new ArrayList<>();

                    for (int i=0; i< jsonArray.length();i++){
                        String TranText = jsonArray.getJSONObject(i).getString("Tran_Text").replace("\r","\r\n");
                        //addTaskIcon(jsonArray.getJSONObject(i).getInt("Tran_Code"),TranText);

                        String tranCode = jsonArray.getJSONObject(i).getString("Tran_Code");
                        if( tranCode.equals("null") ){ tranCode = "0"; }
                        int id = getContext().getResources().getIdentifier("task_icon"+tranCode.charAt(0), "drawable", getContext().getPackageName());
                        list.add(new TaskObject(Integer.parseInt(tranCode), id, TranText ) );
                    }
                    GridView gv = rootView.findViewById(R.id.tasks_root);
                    TaskMenuAdapter tma = new TaskMenuAdapter(getContext(),list);
                    gv.setAdapter(tma);
                    gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Fragment f;
                            if(id == 31 || id == 41) {
                                f = new XD_SelectFragment();
                            }else{
                                f = new MovesTableFragment();
                            }
                            FragmentManager fm = getFragmentManager();
                            FragmentTransaction ft = fm.beginTransaction();
                            Bundle b = new Bundle();
                            b.putInt("tranCode", (int)id);
                            f.setArguments(b);
                            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
                            ft.replace(R.id.fragments, f);
                            ft.addToBackStack("app");
                            ft.commit();

                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        rq.add(jr);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void addTaskIcon(int id, String titleText){
        try {
            if (SessionClass.getParam(id + "_Head_TextBox_Find_Source") != null) {
                LayoutInflater layoutInflater;
                layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                LinearLayout cell = (LinearLayout) layoutInflater.inflate(R.layout.cell_task, null);
                TextView text1 = cell.findViewById(R.id.cell_task_text);
                text1.setText(titleText);
                final int index = id;
                GridView taskRoot = rootView.findViewById(R.id.tasks_root);
                cell.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Fragment f = new MovesTableFragment();
                        FragmentManager fm = getFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction();
                        Bundle b = new Bundle();
                        b.putInt("tranCode", index);
                        f.setArguments(b);
                        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
                        ft.replace(R.id.fragments, f);
                        ft.addToBackStack("app");
                        //ft.commit();
                    }
                });

                taskRoot.addView(cell);
            } else {
                buildErrorDialog();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    void buildErrorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Hibakezelés");
        builder.setMessage("Tranzakciós kód nem érvényes!");
        builder.setNegativeButton("Rendben", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                KeyboardUtils.hideKeyboard(getActivity());
                dialog.cancel();
            }
        });
        builder.show();
    }


}
