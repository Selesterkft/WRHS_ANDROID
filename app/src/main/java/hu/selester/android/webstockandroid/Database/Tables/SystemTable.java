package hu.selester.android.webstockandroid.Database.Tables;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class SystemTable {

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "_key")
    private String key;
    @ColumnInfo(name = "_value")
    private String value;

    public SystemTable(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "SystemTable{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
