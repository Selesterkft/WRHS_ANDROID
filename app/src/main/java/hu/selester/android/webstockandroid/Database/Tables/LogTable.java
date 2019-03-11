package hu.selester.android.webstockandroid.Database.Tables;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import hu.selester.android.webstockandroid.Helper.HelperClass;


@Entity
public class LogTable {

    public static String LogType_Message = "M";
    public static String LogType_Warning = "W";
    public static String LogType_Error   = "E";

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String shortmessage;
    private String message;
    private String type;
    private String user;
    private String date;
    private String time;

    public LogTable(String type,String shortmessage, String message, String user, String date, String time) {
        this.message = message;
        this.shortmessage = shortmessage;
        this.type = type;
        this.user = user;
        if(date == null) this.date = HelperClass.getCurrentDate(); else this.date = date;
        if(time == null) this.time = HelperClass.getCurrentTime(); else this.time = time;
    }

    public void addNewMessageLine(String lineText){
        message += "\n" + lineText;
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

    public String getShortmessage() {
        return shortmessage;
    }

    public void setShortmessage(String shortmessage) {
        this.shortmessage = shortmessage;
    }

    @Override
    public String toString() {
        return "LogTable{" +
                "id=" + id +
                ", shortmessage='" + shortmessage + '\'' +
                ", message='" + message + '\'' +
                ", type='" + type + '\'' +
                ", user='" + user + '\'' +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
