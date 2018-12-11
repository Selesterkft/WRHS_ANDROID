package hu.selester.android.webstockandroid.Adapters.TreeView;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import hu.selester.android.webstockandroid.Helper.HelperClass;
import hu.selester.android.webstockandroid.Objects.AllLinesData;
import hu.selester.android.webstockandroid.Objects.SessionClass;
import hu.selester.android.webstockandroid.Objects.TreeViewGroup;
import hu.selester.android.webstockandroid.R;


public class TreeViewPalettAdapter extends RecyclerView.Adapter<TreeViewPalettAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView text, count;
        public RecyclerView collector;
        public ImageView sign;
        public LinearLayout header;

        public ViewHolder(View itemView) {
            super(itemView);
            text        = itemView.findViewById(R.id.row_treeview_group_text);
            count       = itemView.findViewById(R.id.row_treeview_group_counter);
            collector   = itemView.findViewById(R.id.row_treeview_group_subList);
            sign        = itemView.findViewById(R.id.row_treeview_group_sign);
            header      = itemView.findViewById(R.id.row_treeview_group_header);
        }

        public void bind(){
            if( collector.getVisibility() == View.VISIBLE ){
                collector.setVisibility(View.GONE);
            }else{
                collector.setVisibility(View.VISIBLE);
            }
        }
    }

    private List<TreeViewGroup> dataList;
    private Context context;

    public TreeViewPalettAdapter(List<TreeViewGroup> dataList, Context context) {
        this.dataList = dataList;
        this.context = context;
    }

    @Override
    public void onBindViewHolder(@NonNull final TreeViewPalettAdapter.ViewHolder holder, int position) {
        holder.text.setText(dataList.get(position).getText());
        holder.count.setVisibility(View.GONE);
        List<TreeViewGroup> list = new ArrayList<>();
        int tranCode = Integer.parseInt(SessionClass.getParam("tranCode"));
        int qBarcode02 = HelperClass.getArrayPosition("Barcode02", SessionClass.getParam(tranCode + "_Line_ListView_SELECT"));
        int qBarcode01 = HelperClass.getArrayPosition("Barcode01", SessionClass.getParam(tranCode + "_Line_ListView_SELECT"));
        List<String> data = AllLinesData.getParamsPosition(qBarcode02, qBarcode01, dataList.get(position).getText());
        for(int i = 0; i < data.size(); i++){
            list.add( new TreeViewGroup(data.get(i),10) );
        }
        TreeViewCollectorAdapter collectorAdapter = new TreeViewCollectorAdapter(list, context);
        LayoutManager manager = new LinearLayoutManager(context);
        holder.sign.setImageDrawable(context.getDrawable(R.drawable.pallett_inv));
        holder.collector.setLayoutManager(manager);
        holder.collector.setAdapter(collectorAdapter);
        holder.text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.bind();
            }
        });
//        holder.count.setText(dataList.get(position).getCount());
    }

    @NonNull
    @Override
    public TreeViewPalettAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.row_treeview_group_card, parent, false);
        return new TreeViewPalettAdapter.ViewHolder(v);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void update(List<TreeViewGroup> list){
        dataList = list;
        notifyDataSetChanged();
    }

}
