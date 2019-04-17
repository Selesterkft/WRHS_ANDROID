package hu.selester.android.webstockandroid.Database.Daos;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import hu.selester.android.webstockandroid.Database.Tables.PlacesTable;


@Dao
public interface PlacesDao {

    @Query("Select * from PlacesTable where placeName=:placeName")
    PlacesTable getPlacesData(String placeName);

    @Query("Select transactid from PlacesTable order by transactid desc limit 1")
    long getTransactID();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void setPlaceData(PlacesTable placeTable);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void setPlacesData(List<PlacesTable> placesTable);


    @Query("Select * from PlacesTable")
    List<PlacesTable> getAllData();


    @Query("Delete from PlacesTable")
    void deleteAll();
}
