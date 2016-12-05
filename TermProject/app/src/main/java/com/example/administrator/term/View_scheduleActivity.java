package com.example.administrator.term;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

public class View_scheduleActivity extends AppCompatActivity implements OnItemClickListener,
        OnClickListener{
    MyDBHelper mDBHelper;
    String year;
    String month;
    String day;
    String today,today2, time;
    Cursor cursor;
    SimpleCursorAdapter adapter;
    SimpleCursorAdapter adapter2;
    ListView list;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_schedule);

        Intent intent = getIntent();
        year = intent.getStringExtra("year");
        month = intent.getStringExtra("month");
        day = intent.getStringExtra("day");
        time = intent.getStringExtra("time");

        mDBHelper = new MyDBHelper(this, "Today.db", null, 1);
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        today = year + "/" + month + "/" + day;
        if(time != null){
            today2 = year + "/" + month + "/" + day +"/"+ time +"시";
            TextView text = (TextView) findViewById(R.id.texttoday);
            text.setText(today2);
            cursor = db.rawQuery(
                    "SELECT * FROM today WHERE date = '" + today + "' AND stime= '"+time+"' ", null);
        }else {
            TextView text = (TextView) findViewById(R.id.texttoday);
            text.setText(today);

            cursor = db.rawQuery(
                    "SELECT * FROM today WHERE date = '" + today + "'", null);
        }
        adapter = new SimpleCursorAdapter(this,
                R.layout.schedule_list_item, cursor, new String[] {
                "title","stime","etime" }, new int[] { R.id.tv1,
                R.id.tv2, R.id.tv3});

        ListView list = (ListView) findViewById(R.id.list1);
        list.setAdapter(adapter);
        list.setOnItemClickListener(this);

        mDBHelper.close();

        Button btn = (Button) findViewById(R.id.btnadd);
        btn.setOnClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        // TODO Auto-generated method stub
        Intent intent = new Intent(this, DetailActivity.class);
        cursor.moveToPosition(position);
        intent.putExtra("ParamID", cursor.getInt(0));
        intent.putExtra("time", time);
        startActivityForResult(intent, 0);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        Intent intent = new Intent(this, Add_scheduleActivity.class);
        intent.putExtra("ParamDate", today);
        intent.putExtra("year",year);
        intent.putExtra("month",month);
        intent.putExtra("day",day);
        startActivityForResult(intent, 1);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        // super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:
            case 1:
                if (resultCode == RESULT_OK) {
                    // adapter.notifyDataSetChanged();
                    SQLiteDatabase db = mDBHelper.getWritableDatabase();
                        cursor = db.rawQuery("SELECT * FROM today WHERE date = '"
                                + today + "'", null);
                    adapter.changeCursor(cursor);
                    mDBHelper.close();
                }
                break;
        }
    }
}
