package hu.selester.android.webstockandroid.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;
import hu.selester.android.webstockandroid.Objects.WHItem;
import hu.selester.android.webstockandroid.R;

public class CheckPlaceAdapter extends BaseAdapter {

    private Context context;
    private List<WHItem> dataList;
    private View rootView;

    public CheckPlaceAdapter(Context context, List<WHItem> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public WHItem getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return dataList.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        rootView = layoutInflater.inflate(R.layout.row_whlist, null);
        TextView tv  = rootView.findViewById(R.id.row_whlist_whname);
        tv.setText(dataList.get(position).getName());
        return rootView;
    }
}
