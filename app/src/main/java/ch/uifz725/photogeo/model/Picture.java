package ch.uifz725.photogeo.model;

/**
 * Modelklasse, welche die Bilder repräsentiert
 * Created by eMZetta March 2019.
 */

public class Picture {
    private int id;
    private String name;
    private String location;
    private byte[] image;

    public Picture(String name, String location, byte[] image, int id) {
        this.name = name;
        this.location = location;
        this.image = image;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}
