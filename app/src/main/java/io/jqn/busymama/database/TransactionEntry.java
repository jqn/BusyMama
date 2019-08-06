package io.jqn.busymama.database;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "transaction")
public class TransactionEntry {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String amount;
    private int priority;
    private Date updatedAt;

    @Ignore
    public TransactionEntry(String amount, int priority, Date updatedAt) {
        this.amount = amount;
        this.priority = priority;
        this.updatedAt = updatedAt;
    }

    public TransactionEntry(int id, String amount, int priority, Date updatedAt) {
        this.id = id;
        this.amount = amount;
        this.priority = priority;
        this.updatedAt = updatedAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

}
