package hu.selester.android.webstockandroid.Adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import hu.selester.android.webstockandroid.Helper.HelperClass;
import hu.selester.android.webstockandroid.Helper.MySingleton;
import hu.selester.android.webstockandroid.Objects.ImageObject;
import hu.selester.android.webstockandroid.Objects.SessionClass;
import hu.selester.android.webstockandroid.R;

public class ImageListAdapter extends RecyclerView.Adapter<ImageListAdapter.ViewHolder> {

    private Context context;
    private List<ImageObject> dataList;
    private Activity activity;

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public View view;
        public TextView tranCode, moveNum, date, terminal, userid;
        public CheckBox chkBox;
        public ImageView delBtn;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
        }

        public void bind(){
            tranCode =  view.findViewById(R.id.row_image_head_tranCode);
            moveNum  =  view.findViewById(R.id.row_image_head_moveNum);
            date     =  view.findViewById(R.id.row_image_head_date);
            terminal =  view.findViewById(R.id.row_image_head_terminal);
            userid   =  view.findViewById(R.id.row_image_head_userid);
            chkBox   =  view.findViewById(R.id.row_image_head_chk);
            delBtn   =  view.findViewById(R.id.row_image_head_del);
        }
    }

    public ImageListAdapter(Context context, List<ImageObject> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_image_head,parent,false);
        ImageListAdapter.ViewHolder vh = new ImageListAdapter.ViewHolder(rootView);
        vh.bind();
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.chkBox.isChecked()){
                    holder.chkBox.setChecked(false);
                }else{
                    holder.chkBox.setChecked(true);
                }
            }
        };
        if(activity != null) {
            holder.delBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HelperClass.messageBox(activity, "Kép állomány kezelés", "Biztos, hogy törli az állományt!", HelperClass.DELETE, new HelperClass.OnMessageEvent() {
                        @Override
                        public void onEventYes() {
                            delImage(dataList.get(position).getId());
                        }

                        @Override
                        public void onEventNo() {

                        }
                    });
                }
            });
        }else{
            Toast.makeText(context,"Hiba a törlés közben!", Toast.LENGTH_LONG).show();
        }
        holder.tranCode.setText(""+dataList.get(position).getTranCode());
        holder.moveNum.setText(""+dataList.get(position).getMoveNum());
        holder.terminal.setText(dataList.get(position).getTerminal());
        holder.date.setText(dataList.get(position).getDate());
        holder.userid.setText(""+dataList.get(position).getUserid());
        holder.tranCode.setOnClickListener(onClickListener);
        holder.moveNum.setOnClickListener(onClickListener);
        holder.terminal.setOnClickListener(onClickListener);
        holder.date.setOnClickListener(onClickListener);
        holder.userid.setOnClickListener(onClickListener);
        holder.chkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    SessionClass.setParam("Select_ImageID",""+dataList.get(position).getId());
                }
            }
        });
    }

    private void delImage(final long id) {
        String url = SessionClass.getParam("WSUrl") + "/WRHS_PDA_DATA_IMAGE_LOG_DELETE/" + id;
        Log.i("URL",url);
        JSONObject jsonObject=null;
        RequestQueue rq = MySingleton.getInstance(context).getRequestQueue();
        JsonRequest<JSONObject> jr = new JsonObjectRequest(Request.Method.GET, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String rootText=response.getString("WRHS_PDA_DATA_IMAGE_LOG_DELETEResult");
                    if(!rootText.isEmpty()){
                        JSONObject rootJson = new JSONObject(rootText);
                        if(rootJson.getInt("ERROR_CODE") == -1 ){
                            delRow(id);
                        }else{
                            Toast.makeText(context,rootJson.getString("ERROR_TEXT"), Toast.LENGTH_LONG).show();
                        }
                    }else{
                        Toast.makeText(context,"Hiba a kapcsolódáskor", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(context,"Hiba a kapcsolódáskor", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error!=null){
                    Toast.makeText(context,"Hiba a kapcsolódáskor", Toast.LENGTH_LONG).show();
                    error.printStackTrace();
                }
            }
        });
        jr.setRetryPolicy(new DefaultRetryPolicy(50000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rq.add(jr);

    }

    private void delRow(long id){
        for( int i = 0; i < dataList.size(); i++ ){
            if( id == dataList.get(i).getId() ){
                dataList.remove(i);
                break;
            }
        }
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return dataList.size();
    }

}
