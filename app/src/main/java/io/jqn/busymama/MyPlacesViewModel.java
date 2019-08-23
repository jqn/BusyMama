package io.jqn.busymama;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import io.jqn.busymama.database.BusyMamaDatabase;
import io.jqn.busymama.database.MyPlacesEntry;

public class MyPlacesViewModel extends AndroidViewModel {
    private LiveData<List<MyPlacesEntry>> myPlaces;

    public MyPlacesViewModel(Application application) {
        super(application);
        BusyMamaDatabase database = BusyMamaDatabase.getInstance(this.getApplication());
        myPlaces = database.myPlacesDao().loadAllPlaces();
    }

    public LiveData<List<MyPlacesEntry>> getMyPlaces() {return myPlaces;}

}
