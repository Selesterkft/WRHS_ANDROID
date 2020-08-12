package hu.selester.android.webstockandroid.Adapters.TreeView;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import hu.selester.android.webstockandroid.Helper.HelperClass;
import hu.selester.android.webstockandroid.Objects.AllLinesData;
import hu.selester.android.webstockandroid.Objects.SessionClass;
import hu.selester.android.webstockandroid.Objects.TreeViewGroup;
import hu.selester.android.webstockandroid.R;

public class TreeViewCollectorAdapter extends RecyclerView.Adapter<TreeViewPalettAdapter.ViewHolder> {

    private List<TreeViewGroup> dataList;
    private Context context;

    public TreeViewCollectorAdapter(List<TreeViewGroup> dataList, Context context) {
        this.dataList = dataList;
        this.context = context;
    }

    @NonNull
    @Override
    public TreeViewPalettAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.row_treeview_group_card, parent, false);
        return new TreeViewPalettAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final TreeViewPalettAdapter.ViewHolder holder, int position) {
        holder.text.setText(dataList.get(position).getText());
        holder.count.setText(""+dataList.get(position).getCount());
        holder.count.setVisibility(View.GONE);
        if( dataList.get(position).getText().equals(SessionClass.getParam("TreeViewSearchText")) ){
            holder.header.setBackgroundColor(Color.YELLOW);
        }

        List<TreeViewGroup> list = new ArrayList<>();
        int tranCode = Integer.parseInt(SessionClass.getParam("tranCode"));
        int qBarcode01 = HelperClass.getArrayPosition("Barcode01", SessionClass.getParam(tranCode + "_Line_ListView_SELECT"));
        int qItemNum = HelperClass.getArrayPosition("ItemNum", SessionClass.getParam(tranCode + "_Line_ListView_SELECT"));
        int qEANBarcode = HelperClass.getArrayPosition("EANCode", SessionClass.getParam(tranCode + "_Line_ListView_SELECT"));

        Map<String,Integer> data = AllLinesData.getParamsPositionMapSecond(qBarcode01, qItemNum, qEANBarcode ,dataList.get(position).getText());
        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            String[] tag = entry.getKey().split("#");
            if(tag.length == 1)  list.add( new TreeViewGroup( tag[0],entry.getValue() ));
            if(tag.length == 2)  list.add( new TreeViewGroup( tag[0],entry.getValue(), tag[1] ));
        }

        TreeViewElementAdapter collectorAdapter = new TreeViewElementAdapter(list, context);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(context);
        holder.sign.setImageDrawable(context.getDrawable(R.drawable.box_inv));
        holder.collector.setLayoutManager(manager);
        holder.collector.setAdapter(collectorAdapter);
        holder.text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.bind();
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}
