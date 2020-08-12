package hu.selester.android.webstockandroid.AsyncTask;

import android.os.AsyncTask;
import android.os.Environment;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadNewVersion extends AsyncTask<String, Integer, Integer> {

    private ProgressBar pb;
    private TextView tv;

    public interface AsyncResponse {
        void processFinish(Integer status);
    }

    public AsyncResponse delegate;

    public DownloadNewVersion(AsyncResponse delegate, ProgressBar pb, TextView tv) {
        this.pb = pb;
        this.tv = tv;
        this.delegate = delegate;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        pb.setProgress(values[0]);
        tv.setText(values[0]+" %");

    }

    @Override
    protected Integer doInBackground(String... strings) {
        try {
            String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
            File folder = new File(extStorageDirectory, "Selester");
            folder.mkdir();
            File file = new File(folder, "newversion."+"apk");
            try {
                file.createNewFile();
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            FileOutputStream f = new FileOutputStream(file);
            URL u = new URL(strings[0]);
            HttpURLConnection c = (HttpURLConnection) u.openConnection();
            c.setRequestMethod("GET");
            //c.setDoOutput(true);
            c.connect();
            int fileLength = c.getContentLength();
            long total = 0;
            InputStream in = c.getInputStream();
            byte[] buffer = new byte[1024];
            int len1 = 0;
            while ((len1 = in.read(buffer)) > 0) {
                f.write(buffer, 0, len1);
                total += len1;
                if (fileLength > 0) // only if total length is known
                    publishProgress((int) (total * 100 / fileLength));
            }
            f.close();

        } catch (Exception e) {

            System.out.println("exception in DownloadFile: --------" + e.toString());
            e.printStackTrace();
            return 1;
        }

        return -1;
    }

    @Override
    protected void onPostExecute(Integer status) {
        delegate.processFinish(status);
    }
}
