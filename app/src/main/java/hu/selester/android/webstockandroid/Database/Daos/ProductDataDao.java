package hu.selester.android.webstockandroid.Database.Daos;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import hu.selester.android.webstockandroid.Database.Tables.ProductData;

@Dao
public interface ProductDataDao {

    @Query("Select transactId from ProductData LIMIT 1")
    long getLastTransactId();

    @Query("Select prodid from ProductData where bar1=:barcode1 or bar1 like '0'+:barcode1 or bar1 like '00'+:barcode1")
    String getBarcodeProd(String barcode1);

    @Query("Select bar1 from ProductData where prodid=:prodid")
    String[] getProdBarcode(String prodid);

    @Query("Select * from ProductData")
    List<ProductData> getAllProductData();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void setProductData(List<ProductData> list);

    @Query("Delete from ProductData")
    void deleteAll();
}

