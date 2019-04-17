package hu.selester.android.webstockandroid.Database.Tables;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class PlacesTable {

    @PrimaryKey
    private long id;
    private String placeName;
    private long transactid;

    public PlacesTable(long id, String placeName, long transactid) {
        this.id = id;
        this.placeName = placeName;
        this.transactid = transactid;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public long getTransactid() {
        return transactid;
    }

    public void setTransactid(long transactid) {
        this.transactid = transactid;
    }

    @Override
    public String toString() {
        return "PlacesTable{" +
                "id=" + id +
                ", placeName='" + placeName + '\'' +
                ", transactid=" + transactid +
                '}';
    }
}
