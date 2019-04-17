package hu.selester.android.webstockandroid.Database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import hu.selester.android.webstockandroid.Database.Daos.EansDao;
import hu.selester.android.webstockandroid.Database.Daos.LogDao;
import hu.selester.android.webstockandroid.Database.Daos.PalettDao;
import hu.selester.android.webstockandroid.Database.Daos.PhotosDao;
import hu.selester.android.webstockandroid.Database.Daos.PlacesDao;
import hu.selester.android.webstockandroid.Database.Daos.ProductDataDao;
import hu.selester.android.webstockandroid.Database.Daos.SessionTempDao;
import hu.selester.android.webstockandroid.Database.Daos.SystemDao;
import hu.selester.android.webstockandroid.Database.Daos.UsersDao;
import hu.selester.android.webstockandroid.Database.Tables.EansTable;
import hu.selester.android.webstockandroid.Database.Tables.LogTable;
import hu.selester.android.webstockandroid.Database.Tables.PhotosTable;
import hu.selester.android.webstockandroid.Database.Tables.PlacesTable;
import hu.selester.android.webstockandroid.Database.Tables.ProductData;
import hu.selester.android.webstockandroid.Database.Tables.SessionTemp;
import hu.selester.android.webstockandroid.Database.Tables.SystemTable;
import hu.selester.android.webstockandroid.Database.Tables.UsersTable;

@Database(entities = {ProductData.class, SystemTable.class, UsersTable.class, EansTable.class,SessionTemp.class, LogTable.class, PhotosTable.class, PlacesTable.class}, version = 28)
public abstract class SelesterDatabase extends RoomDatabase{
    private static SelesterDatabase INSTANCE;

    public static SelesterDatabase getDatabase(Context context){
        if(INSTANCE==null){
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),SelesterDatabase.class,"selexped.db").allowMainThreadQueries().fallbackToDestructiveMigration().build();
        }
        return INSTANCE;
    }

    public abstract ProductDataDao productDataDAO();
    public abstract SystemDao systemDao();
    public abstract UsersDao usersDao();
    public abstract EansDao eansDao();
    public abstract SessionTempDao sessionTempDao();
    public abstract LogDao logDao();
    public abstract PhotosDao photosDao();
    public abstract PlacesDao placesDao();

}
