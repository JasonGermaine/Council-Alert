package com.jgermaine.fyp.android_client.util;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by jason on 20/01/15.
 */
public final class FileUtil {

    private static final String TAG = "FileUtil";
    private static final String JPEG = ".jpg";
    private static final String DATE_FORMAT = "yyyy-MM-dd_HH:mm:ss";
    private static final String DEFAULT_DIR = Environment
            .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/CouncilAlert/";

    /**
     * Creates a new file for the image
     *
     * @return uri of file created
     */
    public static File createImageFile() {
        String path = generateImageFilename();
        return createFile(path);
    }

    private static String generateImageFilename() {
        return generateFilename(DEFAULT_DIR, JPEG);
    }

    /**
     * Generates a file path and name
     * @param dir
     * @param postfix
     * @return filename
     */
    private static String generateFilename(String dir, String postfix) {
        createImageDirIfNotExist(dir);
        String timestamp = new SimpleDateFormat(DATE_FORMAT).format(new Date());
        return dir + timestamp + postfix;
    }

    /**
     * Creates a file from a given file path
     * @param path
     * @return new file
     */
    private static File createFile(String path) {
        File file = null;
        try {
            file = new File(path);
            file.createNewFile();
        } catch(IOException ie) {
            Log.e(TAG, ie.getMessage(), ie);
        }
        return file;
    }


    /**
     * Default hook point for {@link #createImageDirIfNotExist(String)}
     * using {@link #DEFAULT_DIR}
     */
    public static void createImageDirIfNotExist() {
        createImageDirIfNotExist(DEFAULT_DIR);
    }

    /**
     * Creates directory on device if it does not exist
     * @param dir
     */
    private static void createImageDirIfNotExist(String dir) {
        File imageDir = new File(dir);
        if (!imageDir.exists() || !imageDir.isDirectory()) {
            // Create directory
            imageDir.mkdirs();
        }
    }

    /**
     * Returns the path from a given Uri
     * @param uri
     * @param context
     * @return file path
     */
    public static String getPathFromURI(Uri uri, Context context) {
        String result;
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        if (cursor == null) {
            result = uri.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.MediaColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }
}
