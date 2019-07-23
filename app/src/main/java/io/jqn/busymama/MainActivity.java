package io.jqn.busymama;


import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import io.jqn.busymama.utilities.NotificationUtils;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void testNotification(View view) {
        NotificationUtils.remindUserNearShop(this);
    }
}
