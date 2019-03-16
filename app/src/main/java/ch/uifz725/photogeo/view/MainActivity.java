package ch.uifz725.photogeo.view;

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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import ch.uifz725.photogeo.controller.MyLocListener;
import ch.uifz725.photogeo.R;
import ch.uifz725.photogeo.controller.SQLiteHelper;

public class MainActivity extends AppCompatActivity {

/**
 * Die Aktivität bildet den Startpunkt der Applikation.
 * Es können Bilder mit der Kamera geschossen werden.
 * Es kann eine Liste aller geschossenen Bilder angezeigt werden.
 * Es kann ein Name für das geschossene Bild vergeben werden.
 * Geschossene Bilder können gespeichert werden.
 * Created by eMZetta March 2019.
 */

    EditText edtName;
    TextView tvLocation;
    FloatingActionButton btnList;
    FloatingActionButton btnTakePic;
    Button btnSave;
    ImageView pictureView;
    MyLocListener loc;

    private String longitude; //Längengrad
    private String latitude; //Breitengrad
    private String location;

    Bitmap thumbnail;


    private static final int SELECT_PHOTO = 1;
    private static final int CAPTURE_PHOTO = 2;

    public static SQLiteHelper sqLiteHelper;

    GregorianCalendar now;
    DateFormat df;

    /**
    Felder werden initialisiert.
    OnClickListener werden auf Buttons gelegt.
    Datenbank wird aufgerufen.
    Die Rechte für das GPS und die Kamera werden gesetzt.
     */
    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        now = new GregorianCalendar();
        df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.LONG);


        edtName = (EditText) findViewById(R.id.edtName);
        tvLocation = (TextView) findViewById(R.id.tvLocation);
        btnTakePic = (FloatingActionButton) findViewById(R.id.btnTakePic);
        btnSave = (Button) findViewById(R.id.btnSave);
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


        // GPS einrichten
        LocationManager myManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        loc = new MyLocListener();
        myManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, loc);

        // Zugriff auf Kamera prüfen
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            pictureView.setEnabled(false);
            ActivityCompat.requestPermissions(MainActivity.this, new String[] { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
        } else {
            pictureView.setEnabled(true);
        }


        // Aktuelle Acitivity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

            } else {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1
                );

            }
        } else {
            // Rechte wurden bereits erteilt
        }

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(pictureView.getDrawable() == null){
                    Toast.makeText(getApplicationContext(), "Bitte mache zuerst ein Foto!", Toast.LENGTH_SHORT).show();
                }
                try{
                    sqLiteHelper.insertData(
                            edtName.getText().toString().trim(),
                            tvLocation.getText().toString().trim(),
                            imageViewToByte(pictureView)
                    );
                    Toast.makeText(getApplicationContext(), "Bild wurde zur DB hinzugefügt!", Toast.LENGTH_SHORT).show();
                    edtName.setText("");
                    tvLocation.setText("");
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                pictureView.setImageBitmap(null);
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

    /**
     * Aus einer ImageView wird ein Bild zu einem byteArray konvertiert.
     * @param image
     * @return
     */
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


    /**
     * Thumbnail wird gesetzt.
     * Längen- und Breitengrad wird geschrieben.
     * Aktuelle Zeit wird geschrieben.
     * @param data
     */
    private void onCaptureImageResult(Intent data) {

        String time = df.format(now.getTime());

        longitude = "";
        latitude = "";
        thumbnail = (Bitmap) data.getExtras().get("data");

        pictureView.setMaxWidth(400);
        pictureView.setImageBitmap(thumbnail);

        longitude =  longitude +loc.getLongitude();
        latitude = latitude +loc.getLangitude();

        location = "Breitengrad: " + latitude + "\n" +
                "Längengrad: " + longitude + "\n" +
                "Zeit: " + time;
        tvLocation.setText(location);

    }

}
