package com.jgermaine.fyp.android_client.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Location;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.jgermaine.fyp.android_client.R;
import com.jgermaine.fyp.android_client.application.CouncilAlertApplication;
import com.jgermaine.fyp.android_client.fragment.EntryFragment;
import com.jgermaine.fyp.android_client.model.Entry;
import com.jgermaine.fyp.android_client.model.Report;
import com.jgermaine.fyp.android_client.util.DialogUtil;
import com.jgermaine.fyp.android_client.util.FileUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

public class CommentActivity extends Activity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_GALLERY = 2;
    private String mImagePath;
    private byte[] mImageBytes;
    private Bitmap mBitmap;
    private ImageView mImage;
    private EditText mComment;
    private Menu mMenu;
    private boolean isViewable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        mImage = (ImageView) findViewById(R.id.comment_image);
        mComment = (EditText) findViewById(R.id.comment_text);

        Bundle bundle = getIntent().getExtras();
        if(bundleContains(bundle, EntryFragment.VIEW_TAG)) {
            isViewable = bundle.getBoolean(EntryFragment.VIEW_TAG);
        }

        if (bundleContains(bundle, EntryFragment.IMAGE_TAG)) {
            String imageString = bundle.getString(EntryFragment.IMAGE_TAG, null);
            if (imageString != null)
                mImage.setImageBitmap(decodeBitmap(imageString, mImage.getWidth(), mImage.getHeight()));
        }

        if (bundleContains(bundle, EntryFragment.COMMENT_TAG)) {
            mComment.setText(bundle.getString(EntryFragment.COMMENT_TAG, ""));
        }

        if (isViewable) {
            findViewById(R.id.image_selectors).setVisibility(View.GONE);
            mComment.setKeyListener(null);
        }
    }

    /**
     * Created this method as even the contains method threw NullPointerExceptions?
     * @param bundle
     * @param key
     * @return
     */
    public boolean bundleContains(Bundle bundle, String key) {
        try {
            return bundle.containsKey(key);
        } catch (NullPointerException e) {
            return false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (isViewable) {
            getMenuInflater().inflate(R.menu.viewable, menu);
        } else {
            getMenuInflater().inflate(R.menu.comment, menu);
        }
        return true;
    }


    /**
     * OnClick listener for the camera button
     *
     * @param view
     */
    public void capturePhoto(View view) {
        File image = FileUtil.createImageFile();
        mImagePath = image.getAbsolutePath();
        sendCameraIntent(Uri.fromFile(image));
    }

    /**
     * OnClick listener for the gallery button
     *
     * @param view
     */
    public void galleryPhoto(View view) {
        sendGalleryIntent();
    }

    /**
     * Sends an intent to the device camera
     *
     * @param uri
     */
    protected void sendCameraIntent(Uri uri) {
        Intent i = new Intent("android.media.action.IMAGE_CAPTURE");
        i.putExtra("output", uri);
        i.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION,
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        startActivityForResult(i, REQUEST_IMAGE_CAPTURE);
    }

    /**
     * Sends an intent to the device gallery
     */
    protected void sendGalleryIntent() {
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, REQUEST_IMAGE_GALLERY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == REQUEST_IMAGE_GALLERY) {
                mImagePath = readImageFromGallery(data);
            } else if (requestCode == REQUEST_IMAGE_CAPTURE) {
                galleryAddPic(mImagePath);
            }
            mImageBytes = createBitmap(mImagePath);
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Log.d("INTENT", "User cancelled operation");
        }
    }

    /**
     * This method gets the data from the gallery image selected and returns the file path
     *
     * @param data
     * @return path to image
     */
    protected String readImageFromGallery(Intent data) {
        String path = null;
        try {
            InputStream stream = getContentResolver().openInputStream(data.getData());
            stream.close();
            Uri uri = data.getData();
            path = FileUtil.getPathFromURI(uri, this);
        } catch (FileNotFoundException fe) {
            Log.e("TAG", fe.getMessage(), fe);
        } catch (IOException ie) {
            Log.e("TAG", ie.getMessage(), ie);
        }
        return path;
    }


    /**
     * Returns the bytes from a bitmap
     *
     * @param bitmap
     * @return bytes
     */
    public byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        return stream.toByteArray();
    }

    /**
     * Writes the image to the phones storage
     *
     * @param path
     */
    public void galleryAddPic(String path) {
        if (path != null) {
            // Create and execute the intent to write to gallery
            Intent i = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
            i.setData(Uri.fromFile(new File(path)));
            sendBroadcast(i);
        }
    }

    /**
     * Creates a bitmap based on the given file path.
     * Applies formatting to the image.
     *
     * @param path
     * @return bytes
     */
    protected byte[] createBitmap(String path) {
        byte[] bytes = null;
        try {
            File image = new File(path);

            // rotate image if needed
            Matrix mat = new Matrix();
            mat.postRotate(getRotationAngle(image));
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2;

            // decode the file at the given path
            Bitmap bmp = BitmapFactory.decodeStream(new FileInputStream(image), null, options);
            Bitmap bitmap = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), mat, true);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            mBitmap = bitmap;
            mImage.setImageBitmap(mBitmap);
            bytes = getBytesFromBitmap(bitmap);
        } catch (NullPointerException | IOException | OutOfMemoryError e) {
            return null;
        }
        return bytes;
    }

    /**
     * Calculates the angle in which a file needs to be rotated
     *
     * @param image
     * @return
     */
    private int getRotationAngle(File image) {
        int angle = 0;
        try {
            ExifInterface exif = new ExifInterface(image.getPath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                angle = 90;
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
                angle = 180;
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                angle = 270;
            }
        } catch (IOException ie) {
            ie.printStackTrace();
        }
        return angle;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_comment) {

            Entry entry = new Entry();
            entry.setTimestamp(new Date());
            if (mImageBytes != null)
                entry.setImage(mImageBytes);

            if (mComment.getText().toString() != null)
                entry.setComment(mComment.getText().toString());

            entry.setAuthor(((CouncilAlertApplication)getApplication()).getUser().getEmail());

            EntryFragment.getFragmentInstance().addEntry(entry);
            finish();
            return true;
        } else if (id == R.id.action_finish_view) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public Bitmap decodeBitmap(String image, int width, int height) {
        byte[] decodedByte = Base64.decode(image, 0);
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = calculateSize(options, width, height);
        Bitmap bmp = BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length, options);
        return bmp;
    }

    public int calculateSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {

        final int height = options.outHeight;
        final int width = options.outWidth;
        int size = 1;

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                size = Math.round((float) height / (float) reqHeight);
            } else {
                size = Math.round((float) width / (float) reqWidth);
            }
        }
        return size;
    }
}
