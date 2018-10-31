package hu.selester.android.webstockandroid.Database.Tables;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;


@Entity
public class LogTable {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String message;
    private String type;
    private String user;
    private String date;
    private String time;

    public LogTable(String type,String message, String user, String date, String time) {
        this.message = message;
        this.type = type;
        this.user = user;
        this.date = date;
        this.time = time;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "LogTable{" +
                "id=" + id +
                ", message='" + message + '\'' +
                ", type=" + type +
                ", user='" + user + '\'' +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
