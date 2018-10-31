package hu.selester.android.webstockandroid.Database.Daos;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import hu.selester.android.webstockandroid.Database.Tables.EansTable;


@Dao
public interface EansDao {



    @Query("Select * from EansTable where eancode=:eancode")
    EansTable getEansdata(String eancode);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void setEansData(EansTable eansTable);


    @Query("Select * from EansTable")
    List <EansTable> getAllData();


    @Query("Delete from EansTable")
    void deleteAll();
}
