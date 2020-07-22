package camera.using.intent;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class CameraActivity extends AppCompatActivity {

    private Button captureImageButton, captureVideoButton;
    private ImageView imageView;
    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        initializeView();
        initializeEvent();
    }

    private void initializeView()
    {
        captureImageButton  = findViewById(R.id.capture_image_using_intent);
        imageView           = findViewById(R.id.capture_image);

        captureVideoButton  = findViewById(R.id.capture_video_using_intent);
        videoView           = findViewById(R.id.capture_video);
    }

    private void initializeEvent()
    {
        captureImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(CameraUtil.isDeviceSupportCamera(getApplicationContext()))
                {
                    captureImage();
                }
                else
                {
                    Toast.makeText(CameraActivity.this,"Camera Not Available",Toast.LENGTH_SHORT).show();
                }
            }
        });

        captureVideoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(CameraUtil.isDeviceSupportCamera(getApplicationContext()))
                {
                    captureVideo();
                }
                else
                {
                    Toast.makeText(CameraActivity.this,"Camera Not Available",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Capturing Camera Image will launch camera app requested image capture
     */
    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = CameraUtil.createPublicFile(CameraConstants.MEDIA_TYPE_IMAGE);
        if (file != null)
        {
            CameraConstants.imageStoragePath = file.getAbsolutePath();
        }

        Uri fileUri = CameraUtil.getFileUri(getApplicationContext(), file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, CameraConstants.CAPTURE_IMAGE_REQUEST_CODE);
        }
    }

    /**
     * Launching camera app to record video
     */
    private void captureVideo() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        File file = CameraUtil.createPublicFile(CameraConstants.MEDIA_TYPE_VIDEO);
        if (file != null)
        {
            CameraConstants.imageStoragePath = file.getAbsolutePath();
        }

        Uri fileUri = CameraUtil.getFileUri(getApplicationContext(), file);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, CameraConstants.CAPTURE_VIDEO_REQUEST_CODE);
        }
    }

    /**
     * Select image from gallery
     */
    private void openGallery()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), CameraConstants.GET_IMAGE_FROM_GALLERY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CameraConstants.CAPTURE_IMAGE_REQUEST_CODE)
        {
            if (resultCode == RESULT_OK)
            {
                //******** Refreshing the gallery ******
                CameraUtil.refreshGallery(getApplicationContext(), CameraConstants.imageStoragePath);

                Bitmap bitmap = CameraUtil.optimizeBitmap(CameraConstants.BITMAP_SAMPLE_SIZE, CameraConstants.imageStoragePath);
                this.imageView.setImageBitmap(bitmap);

                Toast.makeText(this, ""+CameraConstants.imageStoragePath, Toast.LENGTH_LONG).show();
            }
            else if (resultCode == RESULT_CANCELED)
            {
                Toast.makeText(getApplicationContext(),
                        "User cancelled image capture", Toast.LENGTH_SHORT)
                        .show();
                CameraUtil.deleteUriFile(CameraConstants.imageStoragePath);
            }
            else
            {
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();
                CameraUtil.deleteUriFile(CameraConstants.imageStoragePath);
            }
        }
        else if (requestCode == CameraConstants.CAPTURE_VIDEO_REQUEST_CODE)
        {
            if (resultCode == RESULT_OK)
            {
                //******** Refreshing the gallery ******
                CameraUtil.refreshGallery(getApplicationContext(), CameraConstants.imageStoragePath);

                videoView.setVideoPath(CameraConstants.imageStoragePath);
                videoView.start();

                Toast.makeText(this, ""+CameraConstants.imageStoragePath, Toast.LENGTH_LONG).show();
            }
            else if (resultCode == RESULT_CANCELED)
            {
                Toast.makeText(getApplicationContext(),
                        "User cancelled video recording", Toast.LENGTH_SHORT)
                        .show();
                CameraUtil.deleteUriFile(CameraConstants.imageStoragePath);
            }
            else
            {
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to record video", Toast.LENGTH_SHORT)
                        .show();
                CameraUtil.deleteUriFile(CameraConstants.imageStoragePath);
            }
        }
        else if (requestCode == CameraConstants.GET_IMAGE_FROM_GALLERY_REQUEST_CODE)
        {
            if (resultCode == RESULT_OK)
            {
                Uri selectedImage = data.getData();
                InputStream imageStream = null;
                try {
                    imageStream = getContentResolver().openInputStream(selectedImage);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                imageView.setImageBitmap(BitmapFactory.decodeStream(imageStream));

            }
            else if (resultCode == RESULT_CANCELED)
            {
                Toast.makeText(this, "Action Cancelled.", Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(this, "Action Failed", Toast.LENGTH_LONG).show();
            }
        }
    }
}
