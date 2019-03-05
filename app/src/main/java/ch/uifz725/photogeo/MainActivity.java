package ch.uifz725.photogeo;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    static final int REQUEST_TAKE_PICTURE = 2;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    ImageView pictureView;
    String currentPicturePath;


    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;




    GeoTracker tracker;
    private ImageView imgPreview;
    private Uri fileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        pictureView = (ImageView)findViewById(R.id.pictureView);

        FloatingActionButton listPhotos = findViewById(R.id.btn_listPhotos);
        listPhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        FloatingActionButton newPhoto = findViewById(R.id.btn_newPhoto);
        newPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Prüfen, ob das Device eine Kamera hat
                if(!hasCamera()){
                    Snackbar.make(view, "Ihr Gerät hat keine Kamera", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                takePicture(view);
            }
        });
    }

    /**
     * Methode prüft, ob das Device eine Kamera hat
     * @return hat eine Kamera
     */
    private boolean hasCamera(){
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
    }

    /**
     * Methode startet die Kamera, und speichert das Foto in die URI)
     * @param view
     */
    private void takePicture(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getPackageManager()) !=null){
            File pictureFile = null;
            try{pictureFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (pictureFile != null){
                Uri pictureURI = FileProvider.getUriForFile(this,"ch.uifz725.photogeo",pictureFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, pictureURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PICTURE);
            }


        }
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
            Bundle extras = data.getExtras();
            Bitmap pictureBitmap = (Bitmap) extras.get("data");
            pictureView.setImageBitmap(pictureBitmap);
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String pictureFileName = "JPEG_" +timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File picture = File.createTempFile(pictureFileName,".jpg",storageDir);

        currentPicturePath = "file:" +picture.getAbsolutePath();
        return picture;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }




       /*

        // if the result is capturing Image
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            tracker = new GeoTracker(MainActivity.this);

            if (resultCode == RESULT_OK)
            {
                previewCapturedImage();
                if(tracker.isLocation)
                {
                    double latitude = tracker.getLatitude();
                    double longitude = tracker.getLongitude();

                    // \n is for new line
                    Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
                } else {
                    // Can't get location.
                    // GPS or network is not enabled.
                    // Ask user to enable GPS/network in settings.

                }

            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled Image capture
                Toast.makeText(getApplicationContext(),
                        "Cancelled", Toast.LENGTH_SHORT)
                        .show();
            } else {
                // failed to capture image
                Toast.makeText(getApplicationContext(),
                        "Error!", Toast.LENGTH_SHORT)
                        .show();
            }
        }

    }
    private void previewCapturedImage() {
        try {
            imgPreview.setVisibility(View.VISIBLE);

            // bimatp factory
            BitmapFactory.Options options = new BitmapFactory.Options();

            // downsizing image as it throws OutOfMemory Exception for larger
            // images
            options.inSampleSize = 8;

            final Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(),
                    options);

            imgPreview.setImageBitmap(bitmap);

        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
}
*/
    }