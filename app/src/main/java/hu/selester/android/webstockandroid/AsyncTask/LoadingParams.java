package hu.selester.android.webstockandroid.AsyncTask;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import hu.selester.android.webstockandroid.Objects.AllLinesData;

public class LoadingParams extends AsyncTask<String, Integer, List<String> >{

    private ProgressDialog pb;
    private int checkValuePosition;
    private int getPosition;
    private String checkData;
    public AsyncResponse delegate;

    public LoadingParams(AsyncResponse delegate, ProgressDialog pb, int checkValuePosition, int getPosition, String checkData) {
        this.pb = pb;
        this.checkValuePosition = checkValuePosition;
        this.getPosition = getPosition;
        this.checkData = checkData;
        this.delegate = delegate;
    }

    public interface AsyncResponse {
        void processFinish(List<String> list);
    }



    @Override
    protected List<String> doInBackground(String... strings) {
        List<String> list = null;
        try {
            list = AllLinesData.getParamsPosition(checkValuePosition,getPosition,checkData);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return list;
    }

    @Override
    protected void onPostExecute(List<String> list) {
        delegate.processFinish(list);
        pb.dismiss();
    }
}
