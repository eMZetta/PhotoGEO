package ch.uifz725.photogeo;


import android.arch.persistence.room.Database;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {Picture.class}, version = 1, exportSchema = false)
public abstract class CreateDatabase extends RoomDatabase {

    public abstract PictureDao pictureDao();

}
