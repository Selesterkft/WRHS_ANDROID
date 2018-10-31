package hu.selester.android.webstockandroid.Database.Daos;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import hu.selester.android.webstockandroid.Database.Tables.SystemTable;

@Dao
public interface SystemDao {

    @Query("Select _value from SystemTable where _key=:key limit 1")
    String getValue(String key);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void setValue(SystemTable systemTable);

}
