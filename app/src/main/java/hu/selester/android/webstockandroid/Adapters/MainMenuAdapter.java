package hu.selester.android.webstockandroid.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import hu.selester.android.webstockandroid.Objects.MainMenuItem;
import hu.selester.android.webstockandroid.R;

public class MainMenuAdapter extends BaseAdapter {

    private Context context;
    private List<MainMenuItem> menuList;

    public MainMenuAdapter(Context context, List<MainMenuItem> menuList) {
        this.context = context;
        this.menuList = menuList;
    }

    @Override
    public int getCount() {
        return menuList.size();
    }

    @Override
    public MainMenuItem getItem(int position) {
        return menuList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return menuList.get(position).getMenuid();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.cell_menu_item_layout, null);
        }
        TextView tv = convertView.findViewById(R.id.cell_menu_item_text);
        tv.setText(menuList.get(position).getLabel());

        return convertView;
    }
}
