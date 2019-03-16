package ch.uifz725.photogeo.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

import ch.uifz725.photogeo.model.Picture;
import ch.uifz725.photogeo.controller.PictureListAdapter;
import ch.uifz725.photogeo.R;

/**
 * Die Aktivität listet die gespeicherten Fotos in einer GridView
 * Created by eMZetta March 2019.
 */

public class PictureListActivity extends AppCompatActivity {

    GridView gridView;
    ArrayList<Picture> list;
    PictureListAdapter adapter = null;
    ImageView imageViewPic;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_list);

        gridView = (GridView) findViewById(R.id.gridView);
        list = new ArrayList<>();
        adapter = new PictureListAdapter(this, R.layout.activity_picture_items, list);
        gridView.setAdapter(adapter);

        // holt alle Daten aus SQLite-Datenbank
        Cursor cursor = MainActivity.sqLiteHelper.getData("SELECT * FROM PICTURES");
        list.clear();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            String price = cursor.getString(2);
            byte[] image = cursor.getBlob(3);

            list.add(new Picture(name, price, image, id));
        }
        adapter.notifyDataSetChanged();

        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, final long id) {


                CharSequence[] items = {"Anzeigen"};
                AlertDialog.Builder dialog = new AlertDialog.Builder(PictureListActivity.this);

                dialog.setTitle("Bitte wählen");
                dialog.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (item == 0) {
                            // Anzeigen
                            Cursor c = MainActivity.sqLiteHelper.getData("SELECT id FROM PICTURES");
                            ArrayList<Integer> arrID = new ArrayList<Integer>();
                            while (c.moveToNext()){
                                arrID.add(c.getInt(0));
                            }
                            // show dialog update at here
                            showDialogAnzeigen(PictureListActivity.this, arrID.get(position));

                        } else {
                            // Löschen, noch nicht vollständig implementiert
                            Cursor c = MainActivity.sqLiteHelper.getData("SELECT id FROM PICTURES");
                            ArrayList<Integer> arrID = new ArrayList<Integer>();
                            while (c.moveToNext()){
                                arrID.add(c.getInt(0));
                            }
                            showDialogLoeschen(arrID.get(position));
                        }
                    }
                });
                dialog.show();
                return true;
            }
        });
    }


    /**
     * Foto wird vergrössert dargestellt
     * @param activity
     * @param position
     */
    private void showDialogAnzeigen(Activity activity, int position){


        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.activity_picture_activity);

        imageViewPic = (ImageView) dialog.findViewById(R.id.imageViewPic);

        // set width for dialog
        int width = (int) (activity.getResources().getDisplayMetrics().widthPixels * 0.95);
        // set height for dialog
        int height = (int) (activity.getResources().getDisplayMetrics().heightPixels * 0.7);

        dialog.getWindow().setLayout(width, height);
        dialog.show();


        Picture picture = list.get(position-1);
        byte[] image = picture.getImage();
        Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
        imageViewPic.setImageBitmap(bitmap);






    }

    /**
     * Nachfragen, ob das Foto wirklich gelöscht werden soll
     * Kann später für die Implementation einer Löschfunktion gebraucht werden.
     * @param idPicture
     */
    private void showDialogLoeschen(final int idPicture){
        final AlertDialog.Builder dialogDelete = new AlertDialog.Builder(PictureListActivity.this);

        dialogDelete.setTitle("Achtung!!");
        dialogDelete.setMessage("Wollen Sie das Bild wirklich löschen?");
        dialogDelete.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    MainActivity.sqLiteHelper.deleteData(idPicture);
                    Toast.makeText(getApplicationContext(), "Bild gelöscht",Toast.LENGTH_SHORT).show();
                } catch (Exception e){
                    Log.e("error", e.getMessage());
                }
                updatePictureList();
            }
        });

        dialogDelete.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialogDelete.show();
    }

    /**
     * Aktualisiert die Liste mit den Fotos (nach dem Löschen)
     * Kann später für die Implementation einer Löschfunktion gebraucht werden.
     */
    private void updatePictureList(){
        // holt alle Daten aus der SQLite-Datenbank
        Cursor cursor = MainActivity.sqLiteHelper.getData("SELECT * FROM PICTURES");
        list.clear();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            String location = cursor.getString(2);
            byte[] image = cursor.getBlob(3);

            list.add(new Picture(name, location, image, id));
        }
        adapter.notifyDataSetChanged();
    }

}