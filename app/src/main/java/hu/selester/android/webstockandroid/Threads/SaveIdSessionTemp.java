package hu.selester.android.webstockandroid.Threads;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import hu.selester.android.webstockandroid.Database.SelesterDatabase;
import hu.selester.android.webstockandroid.Database.Tables.SessionTemp;
import hu.selester.android.webstockandroid.Helper.HelperClass;
import hu.selester.android.webstockandroid.Objects.AllLinesData;

public class SaveIdSessionTemp extends Thread{

    private Context context;
    private SelesterDatabase db;
    private long id;

    public SaveIdSessionTemp(Context context){
        this.context = context;
        db = SelesterDatabase.getDatabase(context);
    }

    public void setId(long id){
        this.id = id;
    }

    @Override
    public void run() {
        try {
            String[] s = AllLinesData.getParam(String.valueOf(id));
            SessionTemp st = db.sessionTempDao().getData(id);
            if(st != null) {
                db.sessionTempDao().setData(HelperClass.createSessionTempFormat(id, st.getNum(), s));
            }
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(context,"Hiba az ideiglenes adatok mentésénél!",Toast.LENGTH_LONG).show();
        }
    }
}
