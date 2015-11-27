package lab.u2xd.socialspace.supporter;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by ysb on 2015-11-26.
 */
public class DataWriter {

    public static boolean write(String filename, String content) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

            String sSDdir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Context/";
            File dir = new File(sSDdir);
            if(dir.mkdir()) {
                Log.e("Data Writer", "I made an directory");
            }

            File file = new File(sSDdir, filename);
            try {
                if(file.exists()) {
                    file.delete();
                }
                file.createNewFile();
                String path = file.getAbsolutePath();
                FileWriter writer = new FileWriter(file, true);
                writer.append(content);
                writer.flush();
                writer.close();
                Log.e("Data Writer", "Writing Complete... " + path);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        } else {
            Log.e("Data Writer", "There is no storage");
            return false;
        }
    }
}
