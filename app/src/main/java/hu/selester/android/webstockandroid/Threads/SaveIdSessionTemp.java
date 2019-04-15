package hu.selester.android.webstockandroid.Threads;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

import hu.selester.android.webstockandroid.Database.SelesterDatabase;
import hu.selester.android.webstockandroid.Database.Tables.SessionTemp;
import hu.selester.android.webstockandroid.Helper.HelperClass;
import hu.selester.android.webstockandroid.Objects.AllLinesData;

public class SaveIdSessionTemp extends Thread{

    private Context context;
    private SelesterDatabase db;
    private List<Long> id;

    public SaveIdSessionTemp(Context context){
        this.context = context;
        db = SelesterDatabase.getDatabase(context);
    }

    public void setId(List<Long> id){
        this.id = id;
    }

    @Override
    public void run() {
        try {

            for(int i = 0; i < id.size(); i++ ) {
                String[] s = AllLinesData.getParam(String.valueOf(id.get(i)));
                SessionTemp st = db.sessionTempDao().getData(id.get(i));
                if (st != null) {
                    db.sessionTempDao().setData(HelperClass.createSessionTempFormat(id.get(i), st.getNum(), s));
                }

            }
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(context,"Hiba az ideiglenes adatok mentésénél!",Toast.LENGTH_LONG).show();
        }
    }
}