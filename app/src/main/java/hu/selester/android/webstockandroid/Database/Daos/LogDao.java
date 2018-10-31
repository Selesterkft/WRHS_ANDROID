package hu.selester.android.webstockandroid.Database.Daos;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import hu.selester.android.webstockandroid.Database.Tables.LogTable;

/*

       Log type:
       0 - Message
       1 - Warning
       2 - Error
 */

@Dao
public interface LogDao {

    @Query("Select * from LogTable order by date desc,time desc")
    List<LogTable> getAllLog();

    @Query("Select * from LogTable where type = 'M' order by date desc,time desc")
    List<LogTable> getMessgaeLog();

    @Query("Select * from LogTable where type = 'W' order by date desc,time desc")
    List<LogTable> getWarningLog();

    @Query("Select * from LogTable where type = 'E' order by date desc,time desc")
    List<LogTable> getErrorLog();

    @Insert
    void addLog(LogTable lt);

}
