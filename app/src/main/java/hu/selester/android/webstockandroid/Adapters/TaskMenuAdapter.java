package hu.selester.android.webstockandroid.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import hu.selester.android.webstockandroid.Fragments.MovesTableFragment;
import hu.selester.android.webstockandroid.Objects.TaskObject;
import hu.selester.android.webstockandroid.R;

public class TaskMenuAdapter extends BaseAdapter {

    private Context context;
    private List<TaskObject> list;

    public TaskMenuAdapter(Context context, List<TaskObject> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public TaskObject getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return list.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.cell_task, null);
        }
        TextView tv = convertView.findViewById(R.id.cell_task_text);
        ImageView imgView = convertView.findViewById(R.id.cell_task_img);
        tv.setText(list.get(position).getText());
        imgView.setImageResource(list.get(position).getImageResInt());
        return convertView;
    }
}
