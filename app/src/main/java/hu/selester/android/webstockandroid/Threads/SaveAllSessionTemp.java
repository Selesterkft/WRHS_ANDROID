package hu.selester.android.webstockandroid.Threads;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import hu.selester.android.webstockandroid.Database.SelesterDatabase;
import hu.selester.android.webstockandroid.Database.Tables.SessionTemp;
import hu.selester.android.webstockandroid.Helper.HelperClass;
import hu.selester.android.webstockandroid.Objects.AllLinesData;

public class SaveAllSessionTemp extends Thread{

    private Context context;
    private SelesterDatabase db;

    public SaveAllSessionTemp(Context context){
        this.context = context;
        db = SelesterDatabase.getDatabase(context);
    }

    @Override
    public void run() {
        try {
            db.sessionTempDao().deleteAllData();
            List<SessionTemp> stList = new ArrayList<>();
            int c = 0;
            for (Map.Entry<String, String[]> entry : AllLinesData.getAllParam().entrySet()) {
                String[] s = entry.getValue();
                stList.add(HelperClass.createSessionTempFormat(Long.parseLong(entry.getKey()), c, s));
                c++;
            }
            db.sessionTempDao().setDatas(stList);
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(context,"Hiba az ideiglenes adatok mentésénél!",Toast.LENGTH_LONG).show();
        }
    }
}
