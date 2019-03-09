package ch.uifz725.photogeo;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface PictureDao {
    @Insert
    void insertPicture(Picture picture);

    @Query("Select * FROM 'pictures' ORDER BY 'id'")
    List<Picture> getAllPictures();

}
