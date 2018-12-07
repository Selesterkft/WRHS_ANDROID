package hu.selester.android.webstockandroid.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import hu.selester.android.webstockandroid.Helper.HelperClass;
import hu.selester.android.webstockandroid.Objects.AllLinesData;
import hu.selester.android.webstockandroid.Objects.SessionClass;
import hu.selester.android.webstockandroid.R;

public class CollectListAdapter extends BaseAdapter {

    private Context context;
    private List<String> dataList;
    private int qBarcode01, qBarcode02;

    public CollectListAdapter(Context context, List<String> dataList) {
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
        View rootView = LayoutInflater.from(context).inflate(R.layout.row_palett, parent, false);
        TextView tv = rootView.findViewById(R.id.row_palett_text);
        ImageView delBtn = rootView.findViewById(R.id.row_palett_delBtn);
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
        AllLinesData.setParamsPosition(qBarcode01,qBarcode02,dataList.get(position),"");
        dataList.remove(position);
        notifyDataSetChanged();
    }

    public void addItem(String item){
        dataList.add(item);
        notifyDataSetChanged();
    }

    public void updateItems(List<String> items){
        dataList = items;
        notifyDataSetChanged();
    }
}
