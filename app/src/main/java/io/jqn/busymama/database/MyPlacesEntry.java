package io.jqn.busymama.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "myplaces")
public class MyPlacesEntry {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String  placeId;
    private String placeName;
    private String placeAddress;

    @ColumnInfo(name = "updated_at")
    private Date updatedAt;

    @Ignore
    public MyPlacesEntry(String placeId, String placeName, String placeAddress, Date updatedAt) {
        this.placeId = placeId;
        this.placeName = placeName;
        this.placeAddress = placeAddress;
        this.updatedAt = updatedAt;
    }

    public MyPlacesEntry(int id, String placeId, String placeName, String placeAddress, Date updatedAt) {
        this.id = id;
        this.placeId = placeId;
        this.placeName = placeName;
        this.placeAddress = placeAddress;
        this.updatedAt = updatedAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String place) {
        this.placeName = placeName;
    }

    public String getPlaceAddress() {
        return placeAddress;
    }

    public void setPlaceAddress(String place) {
        this.placeAddress = placeAddress;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

}
