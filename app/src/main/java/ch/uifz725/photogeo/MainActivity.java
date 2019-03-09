/*
Author: Michael Zihlmann

Die Activity stellt den Startbildschirm dar und hat ein Hintergrundbild und
zwei Buttons. Ein Button wird zum Anzeigen der bisherigen Fotos benötigt.
Der Andere Button wird zum Machen von Fotos und Speichern benötigt.

Quellen: https://developer.android.com/training/camera/photobasics

 */

package ch.uifz725.photogeo;

import android.Manifest;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    static final int REQUEST_TAKE_PICTURE = 2;
    static final int REQUEST_PICTURE_CAPTURE = 1;
    CreateDatabase db;
    ImageView pictureView;
    String currentPicturePath;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        db = Room.databaseBuilder(getApplicationContext(),
                CreateDatabase.class, "InTimeDB").allowMainThreadQueries().build();

        pictureView = (ImageView) findViewById(R.id.pictureView);

        FloatingActionButton listPhotos = findViewById(R.id.btn_listPhotos);
        listPhotos.setOnClickListener(this);

        FloatingActionButton newPhoto = findViewById(R.id.btn_newPhoto);
        newPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Prüfen, ob das Device eine Kamera hat
                if (!hasCamera()) {
                    Snackbar.make(view, "Ihr Gerät hat keine Kamera", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                takePictureIntent(view);
            }
        });

        final Double myLongitude;

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1
                );

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
        }


        // Acquire a reference to the system Location Manager
        final LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);


        // Define a listener that responds to location updates
        final LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                //makeUseOfNewLocation(location);

                //TextView longitudeTxt = findViewById(R.id.longitudeTxt);

                //longitudeTxt.setText(Double.toString(location.getLatitude()));


                Log.d("Location", Double.toString(location.getLatitude()));
                System.out.println(location.getLatitude());

            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };

        // Register the listener with the Location Manager to receive location updates

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);




        /*
        Database test
         */
// Database insert Tester
        Picture p = new Picture();
        p.setDescription("Test");
        p.setPictureName("Blume");
        db.pictureDao().insertPicture(p);

        // Database tester Querry
        Button locationBtn = findViewById(R.id.locationBtn);
        locationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Picture> allPictures = db.pictureDao().getAllPictures();

                String tester = allPictures.get(0).getDescription();

               Log.d("Success", tester);


                System.out.println(tester);


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
     * Methode startet die Kamera, und speichert das Foto in die URI
     * @param view
     */
    private void takePictureIntent(View view) {
       Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getPackageManager()) !=null){
            File pictureFile = null;
            try{
                pictureFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (pictureFile != null){
                Uri pictureURI = FileProvider.getUriForFile(this,"ch.uifz725.photogeo",pictureFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, pictureURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PICTURE);
            }

        }

    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String pictureFileName = "JPEG_" +timeStamp + "_";
    File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
    File picture = File.createTempFile(pictureFileName,".jpg",storageDir);

    currentPicturePath = "file:" +picture.getAbsolutePath();
        return picture;}


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_PICTURE_CAPTURE && resultCode == RESULT_OK){
            Bundle extras = data.getExtras(); //null
            Bitmap pictureBitmap = (Bitmap) extras.get("data");

            pictureView.setImageBitmap(pictureBitmap);
        }

        if(requestCode == REQUEST_TAKE_PICTURE && resultCode == RESULT_OK) {

        }
    }

    /*private void setPicture() {
        // Get the dimensions of the View
        int targetW = pictureView.getWidth();
        int targetH = pictureView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(currentPicturePath.replace("file:", ""), bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        Bitmap bitmap = BitmapFactory.decodeFile(currentPicturePath.replace("file:",""), bmOptions);
        pictureView.setImageBitmap(bitmap);
    }
    */


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

    @Override
    public void onClick(View v) {
        startActivity(new Intent(this, ListActivity.class));
    }


    }