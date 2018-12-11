package hu.selester.android.webstockandroid.Adapters.TreeView;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.List;
import hu.selester.android.webstockandroid.Helper.HelperClass;
import hu.selester.android.webstockandroid.Objects.SessionClass;
import hu.selester.android.webstockandroid.Objects.TreeViewGroup;
import hu.selester.android.webstockandroid.R;

public class TreeViewElementAdapter extends RecyclerView.Adapter<TreeViewPalettAdapter.ViewHolder> {

    private List<TreeViewGroup> dataList;
    private Context context;

    public TreeViewElementAdapter(List<TreeViewGroup> dataList, Context context) {
        this.dataList = dataList;
        this.context = context;
    }

    @NonNull
    @Override
    public TreeViewPalettAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.row_treeview_group, parent, false);
        return new TreeViewPalettAdapter.ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull TreeViewPalettAdapter.ViewHolder holder, int position) {
        if( HelperClass.inArrayString(SessionClass.getParam("TreeViewSearchText"),dataList.get(position).getSecondId() ) ){
            holder.header.setBackgroundColor(Color.YELLOW);
        }
        holder.text.setText(dataList.get(position).getText());
        holder.count.setText("/"+dataList.get(position).getCount()+" db");
        holder.sign.setImageDrawable(context.getDrawable(R.drawable.barcode));

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}
