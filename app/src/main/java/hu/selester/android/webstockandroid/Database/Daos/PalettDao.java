package hu.selester.android.webstockandroid.Database.Daos;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;
import hu.selester.android.webstockandroid.Database.Tables.PalettTable;

@Dao
public interface PalettDao {

    @Query("Select * from PalettTable where palett=:palett")
    PalettTable getPalett(String palett);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void setPalettTable(PalettTable palettTable);


    @Query("Select * from PalettTable")
    List<PalettTable> getAllData();


    @Query("Delete from PalettTable")
    void deleteAll();

}
