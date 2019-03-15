package ch.uifz725.photogeo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

/**
 * Created by eMZetta March 2019.
 */

    EditText edtName;
    TextView tvLocation;
    FloatingActionButton btnList;
    FloatingActionButton btnTakePic;
    Button btnAdd;
    ImageView pictureView;
    MyLocListener loc;

    private String longitude;
    private String latitude;
    private String location;

    Bitmap thumbnail;


    private static final int SELECT_PHOTO = 1;
    private static final int CAPTURE_PHOTO = 2;

    public static SQLiteHelper sqLiteHelper;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtName = (EditText) findViewById(R.id.edtName);
        tvLocation = (TextView) findViewById(R.id.tvLocation);
        btnTakePic = (FloatingActionButton) findViewById(R.id.btnTakePic);
        btnAdd = (Button) findViewById(R.id.btnAdd);
        btnList = (FloatingActionButton) findViewById(R.id.btnList);
        pictureView = (ImageView) findViewById(R.id.pictureView);

        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);



        sqLiteHelper = new SQLiteHelper(this, "PictureDB.sqlite", null, 1);

        sqLiteHelper.queryData();

        btnTakePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, CAPTURE_PHOTO);
            }
        });



        LocationManager myManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        loc = new MyLocListener();
        myManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, loc);

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            pictureView.setEnabled(false);
            ActivityCompat.requestPermissions(MainActivity.this, new String[] { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
        } else {
            pictureView.setEnabled(true);
        }


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

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    sqLiteHelper.insertData(
                            edtName.getText().toString().trim(),
                            tvLocation.getText().toString().trim(),
                            imageViewToByte(pictureView)
                    );
                    Toast.makeText(getApplicationContext(), "Bild wurde zur Datenbank hinzugefügt!", Toast.LENGTH_SHORT).show();
                    edtName.setText("");
                    tvLocation.setText("");
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        btnList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, PictureListActivity.class);
                startActivity(intent);
            }
        });
    }

    public static byte[] imageViewToByte(ImageView image) {
        Bitmap bitmap = ((BitmapDrawable)image.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                pictureView.setEnabled(true);
            }
        }
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);



        if(requestCode == SELECT_PHOTO){
            if(resultCode == RESULT_OK) {
                try {
                    final Uri imageUri = data.getData();
                    final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    //set Progress Bar
                    //setProgressBar();
                    //set profile picture form gallery
                    pictureView.setImageBitmap(selectedImage);


                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }

        }else if(requestCode == CAPTURE_PHOTO){
            if(resultCode == RESULT_OK) {
                onCaptureImageResult(data);
            }
        }

    }


    private void onCaptureImageResult(Intent data) {

        longitude = "";
        latitude = "";
        thumbnail = (Bitmap) data.getExtras().get("data");

        //set Progress Bar
        //setProgressBar();
        //set profile picture form camera
        pictureView.setMaxWidth(200);
        pictureView.setImageBitmap(thumbnail);

        longitude =  longitude +loc.getLongitude();
        latitude = latitude +loc.getLangitude();

        location = "Breitengrad: " + latitude + "\n" +
                "Längengrad: " + longitude;
        tvLocation.setText(location);

    }

}
