package io.jqn.busymama.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "transaction")
public class TransactionEntry {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String place;
    private float amount;

    @ColumnInfo(name = "place_id")
    private String placeId;

    @ColumnInfo(name = "updated_at")
    private Date updatedAt;

    @Ignore
    public TransactionEntry(String placeId, float amount, String place, Date updatedAt) {
        this.placeId = placeId;
        this.amount = amount;
        this.place = place;
        this.updatedAt = updatedAt;
    }

    public TransactionEntry(int id, String placeId, float amount, String place, Date updatedAt) {
        this.id = id;
        this.placeId = placeId;
        this.amount = amount;
        this.place = place;
        this.updatedAt = updatedAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPlaceId() {return placeId;}

    public void setPlaceId(String placeId) {this.placeId = placeId;}

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

}
