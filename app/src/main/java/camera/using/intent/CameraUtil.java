package camera.using.intent;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import androidx.core.content.FileProvider;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CameraUtil {

    private static final float PREFERRED_WIDTH = 250;
    private static final float PREFERRED_HEIGHT = 250;

    /**
     * Checks whether device has camera or not. This method not necessary if
     * android:required="true" is used in manifest file
     */
    public static boolean isDeviceSupportCamera(Context context)
    {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Refreshes gallery on adding new image/video. Gallery won't be refreshed
     * on older devices until device is rebooted
     */
    public static void refreshGallery(Context context, String filePath)
    {
        MediaScannerConnection.scanFile(context,
                new String[]{filePath}, null,
                new MediaScannerConnection.OnScanCompletedListener()
                {
                    public void onScanCompleted(String path, Uri uri)
                    {

                    }
                });
    }

    /**
     * Downsizing the bitmap to avoid OutOfMemory exceptions
     */
    public static Bitmap optimizeBitmap(int sampleSize, String filePath)
    {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = sampleSize;
        return BitmapFactory.decodeFile(filePath, options);
    }

    private static String bitmapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    private static Bitmap stringToBitmap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    public static Bitmap resizeBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        float scaleWidth = PREFERRED_WIDTH / width;
        float scaleHeight = PREFERRED_HEIGHT / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        Bitmap resizedBitmap = Bitmap.createBitmap(
                bitmap, 0, 0, width, height, matrix, false);

        bitmap.recycle();
        return resizedBitmap;
    }

    /**
     * The directory is private to your app
     * Only access data using app, not public
     * created path : /storage/emulated/0/Android/data/camera.using.intent/files/Pictures/IMG_20200715_1431013137390746518673908.jpg
     *
     * What Can You Read?
     *
     * If you wrote it, you can read it. However, that is it.
     * You cannot read the contents of files or directories that were created by other means, using filesystem APIs.
     */
    public static File createPrivateFile(Activity activity, int type) {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        /*
         * Use this if you want android to automatically save it
         * into the device's but not showing up anywhere! Not in image gallery
         *
         * Note: Files you save in the directories provided by
         * getExternalFilesDir() or getFilesDir()
         * are deleted when the user uninstalls your app.
         */
        File mediaStorageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        assert mediaStorageDir != null;
        if (!mediaStorageDir.exists())
        {
            if (!mediaStorageDir.mkdirs())
            {
                Log.e( CameraConstants.GALLERY_DIRECTORY_NAME, "Oops! Failed create "
                        +  CameraConstants.GALLERY_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        File mediaFile = null;

        if (type ==  CameraConstants.MEDIA_TYPE_IMAGE)
        {
            String imageFileName = "IMG_" + timeStamp;

            try {
                mediaFile = File.createTempFile(
                        imageFileName,  /* prefix */
                        CameraConstants.IMAGE_EXTENSION,  /* suffix */
                        mediaStorageDir     /* directory */
                );
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        else if (type ==  CameraConstants.MEDIA_TYPE_VIDEO)
        {
            String imageFileName = "VID_" + timeStamp;

            try {
                mediaFile = File.createTempFile(
                        imageFileName,  /* prefix */
                        CameraConstants.VIDEO_EXTENSION,  /* suffix */
                        mediaStorageDir     /* directory */
                );
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return mediaFile;
    }

    /**
     * The directory is public to your app
     * Any one can access, not private
     * created path : /storage/emulated/0/Pictures/VID_20200715_1431276234191655446864926.mp4
     * getExternalStoragePublicDirectory deprecated in Android Q (API LEVEL 29 )
     *
     * The documentation states that the next 3 options are the new preferred alternatives :
     * 1). Context#getExternalFilesDir(String)
     * 2). MediaStore
     * 3). Intent#ACTION_OPEN_DOCUMENT
     */
    public static File createPublicFile(int type) {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());

        /**
         * Use this if you want android to automatically save it
         * into the device's image gallery
         */
        File mediaStorageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        /*File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "Camera Example");*/


       /* String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        String dataPath = extStorageDirectory + "/ImageOpenExample/SampleImage.jpg";

        File mediaStorageDir = new File(dataPath);*/


        assert mediaStorageDir != null;
        if (!mediaStorageDir.exists())
        {
            if (!mediaStorageDir.mkdirs())
            {
                Log.e( CameraConstants.GALLERY_DIRECTORY_NAME, "Oops! Failed create " +
                        CameraConstants.GALLERY_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        File mediaFile = null;

        if (type ==  CameraConstants.MEDIA_TYPE_IMAGE)
        {
            String imageFileName = "IMG_" + timeStamp;

            try
            {
                mediaFile = File.createTempFile(
                        imageFileName,  /* prefix */
                        CameraConstants.IMAGE_EXTENSION,  /* suffix */
                        mediaStorageDir     /* directory */
                );
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

        }
        else if (type ==  CameraConstants.MEDIA_TYPE_VIDEO)
        {
            String imageFileName = "VID_" + timeStamp;

            try
            {
                mediaFile = File.createTempFile(
                        imageFileName, /* prefix */
                        CameraConstants.VIDEO_EXTENSION, /* suffix */
                        mediaStorageDir /* directory */
                );
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        return mediaFile;
    }

    public static Uri getFileUri(Context context, File file)
    {
        /**
         * Note: We are using getUriForFile(Context, String, File) which returns a content:// URI.
         * For more recent apps targeting Android 7.0 (API level 24) and higher, passing a file:// URI
         * across a package boundary causes a FileUriExposedException.
         * Therefore, we now present a more generic way of storing images using a FileProvider.
         */
        return FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileProvider", file);
    }

    public static void deleteUriFile(String path)
    {
        File file = new File(path);
        if(file.exists())
        {
            if (file.delete())
            {
                System.out.println("file Deleted :" + path);
            }
            else
            {
                System.out.println("file not Deleted :" + path);
            }
        }
    }
}
