package hu.selester.android.webstockandroid.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import hu.selester.android.webstockandroid.AsyncTask.LoadingParams;
import hu.selester.android.webstockandroid.Helper.HelperClass;
import hu.selester.android.webstockandroid.Objects.AllLinesData;
import hu.selester.android.webstockandroid.Objects.CheckedList;
import hu.selester.android.webstockandroid.Objects.SessionClass;
import hu.selester.android.webstockandroid.R;
import hu.selester.android.webstockandroid.Threads.SaveIdSessionTemp;

public class CollectListAdapter extends BaseAdapter {

    private Context context;
    private List<String> dataList;
    private int qBarcode01, qBarcode02;

    public interface OnDelClick{
        public void onClick(List<String> delId);
    }

    OnDelClick onDelClick;

    public CollectListAdapter(Context context, List<String> dataList) {
        this.onDelClick = onDelClick;
        int tranCode = Integer.parseInt(SessionClass.getParam("tranCode"));
        qBarcode01 = HelperClass.getArrayPosition("Barcode01", SessionClass.getParam(tranCode + "_Line_ListView_SELECT"));
        qBarcode02 = HelperClass.getArrayPosition("Barcode02", SessionClass.getParam(tranCode + "_Line_ListView_SELECT"));

        this.context = context;
        this.dataList = dataList;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public String getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.row_treeview_group, parent, false);
        TextView tv = rootView.findViewById(R.id.row_treeview_group_text);
        TextView count = rootView.findViewById(R.id.row_treeview_group_counter);
        count.setVisibility(View.GONE);
        ImageView delBtn = rootView.findViewById(R.id.row_treeview_group_delBtn);
        ImageView sign = rootView.findViewById(R.id.row_treeview_group_sign);
        sign.setImageDrawable(context.getDrawable(R.drawable.box_inv));
        delBtn.setVisibility(View.VISIBLE);
        delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeItem(position);
            }
        });
        tv.setText( dataList.get(position) );
        return rootView;
    }

    public void removeItem(int position){
        try{
            AllLinesData.setParamsPosition(qBarcode01,qBarcode02,dataList.get(position),"");
            List<String> ids = AllLinesData.getParamsPosition(qBarcode01,0,dataList.get(position));
            List<Long> dataIDSList = new ArrayList<>();
            for(int j = 0 ;j<ids.size(); j++){
                dataIDSList.add(Long.parseLong(ids.get(j)));
                CheckedList.setParamItem(ids.get(j),1);
            }
            SaveIdSessionTemp savePalett = new SaveIdSessionTemp(context);
            savePalett.setId(dataIDSList);
            savePalett.start();
            dataList.remove(position);
            notifyDataSetChanged();
        }catch (Exception e){
            Toast.makeText(context,"Hiba a törlés közben!",Toast.LENGTH_LONG).show();
        }
    }

    public void addItem(String item){
        dataList.add(item);
        notifyDataSetChanged();
    }

    public void updateItems(List<String> items){
        dataList = items;
        notifyDataSetChanged();
    }

    public void clearItems(){
        dataList.clear();
        notifyDataSetChanged();
    }
}
