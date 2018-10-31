package hu.selester.android.webstockandroid.Database.Daos;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import hu.selester.android.webstockandroid.Database.Tables.UsersTable;

@Dao
public interface UsersDao {

    @Query("Select * from UsersTable where account=:account")
    UsersTable getUsersData(String account);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void setUserData(UsersTable usersTable);
}
