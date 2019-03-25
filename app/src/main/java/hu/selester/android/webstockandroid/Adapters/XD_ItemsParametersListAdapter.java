package hu.selester.android.webstockandroid.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.List;

import hu.selester.android.webstockandroid.Adapters.TreeView.TreeViewPalettAdapter;
import hu.selester.android.webstockandroid.Helper.HelperClass;
import hu.selester.android.webstockandroid.Objects.XD_ItemsParameters;
import hu.selester.android.webstockandroid.R;

public class XD_ItemsParametersListAdapter extends RecyclerView.Adapter<XD_ItemsParametersListAdapter.ViewHolder> {

    private List<XD_ItemsParameters> dataList;
    private Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView item_pcs,item_weight,item_length,item_width,item_height;
        public LinearLayout container;

        public ViewHolder(View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.row_line_container);
            item_pcs = itemView.findViewById(R.id.row_line_pcs);
            item_weight = itemView.findViewById(R.id.row_line_weight);
            item_length = itemView.findViewById(R.id.row_line_length);
            item_width = itemView.findViewById(R.id.row_line_width);
            item_height = itemView.findViewById(R.id.row_line_height);
        }
    }

    public XD_ItemsParametersListAdapter(@NonNull Context context, List<XD_ItemsParameters> dataList) {
        this.dataList = dataList;
        this.context = context;
    }

    @Override
    public void onBindViewHolder(@NonNull XD_ItemsParametersListAdapter.ViewHolder holder, int position) {
        Log.i("TAG","BIND: " + position);
        holder.container.setTag("container_" + position);
        holder.container.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                HelperClass.setDrag(view);
                return false;
            }
        });
        holder.item_pcs.setText("1");
    }

    @NonNull
    @Override
    public XD_ItemsParametersListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        Log.i("TAG","CREATE");
        View v = LayoutInflater.from(context).inflate(R.layout.row_xd_items_paramteres, parent, false);
        return new XD_ItemsParametersListAdapter.ViewHolder(v);

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}
