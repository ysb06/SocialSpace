package lab.u2xd.socialspace.supporter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Pattern;

/**
 * Created by ysb on 2015-11-26.
 */
public class DataWriter {

    private static final char[] ILLEGAL_NAME = {'<', '>', ':', '"', '/', '\\', '|', '?', '*'};

    public static Bitmap loadBitmap(Context context, String file_name) {
        String name = file_name + ".png";
        name = convertToValidName(name);
        Bitmap bitmap = BitmapFactory.decodeFile(context.getFilesDir() + "/" + name);

        return bitmap;
    }

    /** 비트맵을 저장합니다.
     *
     * @param context Context 객체
     * @param file_name 저장할 파일이름 (확장자 제외, 항상 PNG 형식으로 저장)
     * @param bitmap 저장할 비트맵
     * @return 저장된 파일 경로
     */
    public static String saveBitmap(Context context, String file_name, Bitmap bitmap) {
        String name = file_name + ".png";
        name = convertToValidName(name);
        File file = new File(context.getFilesDir(), name);

        try {
            if(!file.exists()) {
                file.createNewFile();                                           //IOException
                FileOutputStream out = new FileOutputStream(file);              //IOException
                if(!bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)) {
                    Log.e("Data Writer", "Bitmap Error");
                }
                out.close();                                                    //IOException
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file.getAbsolutePath();
    }

    public static String convertToValidName(String name) {
        String str = name;
        for(char c : ILLEGAL_NAME) {
            str = str.replace(c, '_');
        }
        return str;
    }

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
