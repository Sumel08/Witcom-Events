package com.upiita.witcomevents.eventPager;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.upiita.witcomevents.R;
import com.upiita.witcomevents.SplashScreen;
import com.upiita.witcomevents.database.EventDatabase;

public class EventPagerActivity extends AppCompatActivity {

    private EventFragmentAdapter mAdapter;
    public static ViewPager mPager;
    private String eventCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_pager);

        eventCode = getIntent().getStringExtra("eventCode");

        mAdapter = new EventFragmentAdapter(getSupportFragmentManager());
        mPager = (ViewPager) findViewById(R.id.event_pager);
        mPager.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.pager_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.update) {
            //update();
        } else if(id == R.id.swapEvent) {
            clearEvent();
        }

        return super.onOptionsItemSelected(item);
    }

    private void clearEvent() {
        SQLiteDatabase bd = new EventDatabase(getApplicationContext()).getReadableDatabase();
        bd.execSQL("delete from event");
        bd.close();
        startActivity(new Intent(getApplicationContext(), SplashScreen.class));
        finish();
    }
}
