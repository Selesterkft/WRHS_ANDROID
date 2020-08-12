package hu.selester.android.webstockandroid.Database.Daos;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import hu.selester.android.webstockandroid.Database.Tables.SessionTemp;

@Dao
public interface SessionTempDao {

    @Query("Select * from SessionTemp order by num")
    List<SessionTemp> getAllData();

    @Query("Select count(id) from SessionTemp")
    int getDataSize();

    @Query("Select * from SessionTemp where id=:id")
    SessionTemp getData(long id);

    @Query("Delete from SessionTemp")
    void deleteAllData();

    @Insert
    void setDatas(List<SessionTemp> list);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void setData(SessionTemp sessionTemp);

    @Query("Delete from SessionTemp where id=:id")
    void deleteData(long id);


}
