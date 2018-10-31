package hu.selester.android.webstockandroid.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import hu.selester.android.webstockandroid.Database.Tables.LogTable;
import hu.selester.android.webstockandroid.R;

public class LogListAdapter extends RecyclerView.Adapter<LogListAdapter.ViewHolder> {

    private Context context;
    private List<LogTable> dataList;

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView type;
        public TextView message;
        public TextView userDateTime;

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    public LogListAdapter(Context context, List<LogTable> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_log,parent,false);
        LogListAdapter.ViewHolder vh = new LogListAdapter.ViewHolder(rootView);
        vh.message = rootView.findViewById(R.id.row_log_msg);
        vh.userDateTime = rootView.findViewById(R.id.row_log_user_datetime);
        vh.type = rootView.findViewById(R.id.row_log_type);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.type.setText(dataList.get(position).getType());
        holder.message.setText(dataList.get(position).getMessage());
        holder.userDateTime.setText(dataList.get(position).getUser()+" - "+dataList.get(position).getDate()+" "+dataList.get(position).getTime());
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void updateData(List<LogTable> newDataList){
        this.dataList = newDataList;
        notifyDataSetChanged();
    }

}
