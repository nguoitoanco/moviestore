package com.fsoft.sonic_larue.khanhnv10.moviestore.service.util;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by KhanhNV10 on 2015/12/08.
 */
public class ImageUtil {

    public static int RATE_TWO = 2;
    public static int RATE_THREE = 3;
    public static int RATE_FOUR = 4;
    public static String saveBitmap(Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");
        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
            return destination.getAbsolutePath();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Bitmap getBitmapFromPath(String path) {


        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        final int REQUIRED_SIZE = 200;
        int scale = 1;
        while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                && options.outHeight / scale / 2 >= REQUIRED_SIZE)
            scale *= 2;
        options.inSampleSize = scale;
        options.inJustDecodeBounds = false;
        Bitmap bm = BitmapFactory.decodeFile(path, options);

        return bm;
    }

    public static String getImagePathFromUri( Uri uri, Context context) {
        String[] projection = { MediaStore.MediaColumns.DATA };
        CursorLoader cursorLoader = new CursorLoader(context,uri, projection, null, null,
                null);
        Cursor cursor =cursorLoader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();

        String selectedImagePath = cursor.getString(column_index);
        return selectedImagePath;
    }

    public static void setAutoSizeOfCircleImageView(CircleImageView imageView, Context context, int rate) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float mWidth = displayMetrics.widthPixels;
        imageView.getLayoutParams().width = (int)(mWidth / rate);
        imageView.getLayoutParams().height = (int)(mWidth / rate);
    }

    public static void setAutoSizeOfImageView(ImageView imageView, Context context, int rate) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float mWidth = displayMetrics.widthPixels;
        imageView.getLayoutParams().width = (int)(mWidth / rate);
        imageView.getLayoutParams().height = (int)(mWidth / rate);
    }
}
