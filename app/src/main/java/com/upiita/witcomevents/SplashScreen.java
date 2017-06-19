package com.upiita.witcomevents;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.upiita.witcomevents.database.EventDatabase;
import com.upiita.witcomevents.eventList.EventListActivity;
import com.upiita.witcomevents.eventPager.EventPagerActivity;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class SplashScreen extends AppCompatActivity {

    private boolean currentEvent = false;
    public static final ArrayList<String> PAGES_CONTENT = new ArrayList<>();
    public static final HashMap<String, Integer> PAGES_ICONS = new HashMap<>();
    public static final HashMap<String, Integer> PAGES_IMAGES = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);

        PAGES_ICONS.clear();
        PAGES_IMAGES.clear();
        PAGES_CONTENT.clear();

        PAGES_ICONS.put(getString(R.string.streamings),R.drawable.perm_group_streaming);
        PAGES_ICONS.put(getString(R.string.schedule),R.drawable.perm_group_conferences);
        PAGES_ICONS.put(getString(R.string.activities),R.drawable.perm_group_workshops);
        PAGES_ICONS.put(getString(R.string.people),R.drawable.perm_group_speaker);
        PAGES_ICONS.put(getString(R.string.how_to_arrive),R.drawable.perm_group_howtoarrive);
        PAGES_ICONS.put(getString(R.string.sketch),R.drawable.perm_group_sketch);
        PAGES_ICONS.put(getString(R.string.places),R.drawable.perm_group_tourism);
        PAGES_ICONS.put(getString(R.string.sponsors_and_about),R.drawable.perm_group_sponsors);

        PAGES_IMAGES.put(getString(R.string.streamings),R.drawable.streaming);
        PAGES_IMAGES.put(getString(R.string.schedule),R.drawable.conference);
        PAGES_IMAGES.put(getString(R.string.activities),R.drawable.workshop);
        PAGES_IMAGES.put(getString(R.string.people),R.drawable.speaker);
        PAGES_IMAGES.put(getString(R.string.how_to_arrive),R.drawable.map);
        PAGES_IMAGES.put(getString(R.string.sketch),R.drawable.sketch);
        PAGES_IMAGES.put(getString(R.string.places),R.drawable.tourism);
        PAGES_IMAGES.put(getString(R.string.sponsors_and_about),R.drawable.sponsors);

        SQLiteDatabase db = new EventDatabase(getApplicationContext()).getReadableDatabase();
        Cursor fila = db.rawQuery("SELECT * FROM event", null);
        currentEvent = fila.getCount()>0;
        if (fila.moveToFirst()) {

            //Create List of Sections
            if (db.rawQuery("SELECT * FROM streams", null).getCount()>0)
                PAGES_CONTENT.add(getString(R.string.streamings));
            PAGES_CONTENT.add(getString(R.string.schedule));
            PAGES_CONTENT.add(getString(R.string.activities));
            PAGES_CONTENT.add(getString(R.string.people));
            if (!fila.getString(6).equals("null"))
                PAGES_CONTENT.add(getString(R.string.how_to_arrive));
            if (!fila.getString(8).equals("null")) {
                PAGES_CONTENT.add(getString(R.string.sketch));
                Toast.makeText(this, "Sí Hay", Toast.LENGTH_SHORT).show();
            }
            PAGES_CONTENT.add(getString(R.string.places));
            PAGES_CONTENT.add(getString(R.string.sponsors_and_about));


            Toast.makeText(this, fila.getString(8), Toast.LENGTH_SHORT).show();

            do {
                Cursor fila2 = db.rawQuery("SELECT image FROM images where id=" + fila.getString(4), null);
                if(fila2.moveToFirst())
                    ((ImageView)findViewById(R.id.imagelogo)).setImageBitmap(BitmapFactory.decodeStream(new ByteArrayInputStream(fila2.getBlob(0))));
                ((TextView)findViewById(R.id.textlogo)).setText(fila.getString(5));
                fila2.close();
            } while (fila.moveToNext());
        }

        fila.close();
        db.close();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {

                if(currentEvent)
                    startActivity(new Intent(getApplicationContext(), EventPagerActivity.class));
                else
                    startActivity(new Intent(getApplicationContext(), EventListActivity.class));

                //startActivity(new Intent(getApplicationContext(), StreamingActivity.class));
            }
        }, 1680);
    }

    @Override
    public void onPause() {
        super.onPause();
        finish();
    }
}
