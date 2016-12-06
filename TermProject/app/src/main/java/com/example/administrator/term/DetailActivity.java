package com.example.administrator.term;


import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DetailActivity extends AppCompatActivity implements OnClickListener { // 일정목록 추가하기
    MyDBHelper mDBHelper;
    int mId;
    String today, time, day, month, year;
    TextView textTime, textDate, textTitle, textState, textMemo, textTime2;

    /** Called when the activity is first created. */

    //멀티미디어
    private ListView mListView;
    private int mSelectePoistion;
    private MediaItemAdapter mAdapter;
    private MediaPlayer mMediaPlayer;
    ImageView image;
    private int mPlaybackPosition = 0;

    String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/MyDir/";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);


        textDate = (TextView) findViewById(R.id.dateText);
        textTime = (TextView) findViewById(R.id.timeText);
        textTime2 = (TextView) findViewById(R.id.timeText2);
        textTitle = (TextView) findViewById(R.id.titleText);
        textState = (TextView) findViewById(R.id.stateText);
        textMemo = (TextView) findViewById(R.id.memoText);

        Intent intent = getIntent();
        mId = intent.getIntExtra("ParamID", -1);
        today = intent.getStringExtra("ParamDate");
        time= intent.getStringExtra("time");
        mDBHelper = new MyDBHelper(this, "Today.db", null, 1);
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        if(mId==-1){
            Cursor cursor = db.rawQuery("SELECT * FROM today WHERE date='" + today
                    + "'  AND stime='"+ time +"' " , null);
            if (cursor.moveToNext()) {
                textTitle.setText(cursor.getString(1));
                textState.setText(cursor.getString(2));
                textDate.setText(cursor.getString(3));
                textTime.setText(cursor.getString(4));
                textTime2.setText(cursor.getString(5));
                textMemo.setText(cursor.getString(6));

            }
        }else {
            Cursor cursor = db.rawQuery("SELECT * FROM today WHERE _id='" + mId
                    + "'", null);
            if (cursor.moveToNext()) {
                textTitle.setText(cursor.getString(1));
                textState.setText(cursor.getString(2));
                textDate.setText(cursor.getString(3));
                textTime.setText(cursor.getString(4));
                textTime2.setText(cursor.getString(5));
                textMemo.setText(cursor.getString(6));

            }
        }
        mDBHelper.close();
        sdPath += textDate.getText().toString()+"/"+textTime.getText().toString()+"/";
        Button btn1 = (Button) findViewById(R.id.btn1);
        btn1.setOnClickListener(this);
        Button btn2 = (Button) findViewById(R.id.btn2);
        btn2.setOnClickListener(this);
        Button btn3 = (Button) findViewById(R.id.btn3);
        btn3.setOnClickListener(this);
        image =(ImageView)this.findViewById(R.id.imageView);
        image.setVisibility(View.INVISIBLE);
        initListView();
    }

    @Override
    public void onClick(View v) {
// TODO Auto-generated method stub
        SQLiteDatabase db = mDBHelper.getWritableDatabase();

        switch (v.getId()) {
            case R.id.btn1:
                Intent intent = new Intent(this, Add_scheduleActivity.class);
                intent.putExtra("ParamID", mId);

                startActivityForResult(intent, 0);
                break;
            case R.id.btn2:
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        this);

                // 제목셋팅
                alertDialogBuilder.setTitle("일정 삭제");

                // AlertDialog 셋팅
                alertDialogBuilder
                        .setMessage("일정을 삭제하시겠습니까?")
                        .setCancelable(false)
                        .setPositiveButton("삭제",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(
                                            DialogInterface dialog, int id) {
                                        // 프로그램을 종료한다
                                        if (mId != -1) {
                                            SQLiteDatabase db = mDBHelper.getWritableDatabase();
                                            db.execSQL("DELETE FROM today WHERE _id='" + mId + "';");
                                            mDBHelper.close();
                                        }
                                        File f = new File(sdPath);
                                        if(f.isDirectory()) {
                                            DeleteDir(sdPath);
                                        }
                                        Intent intent = new Intent(DetailActivity.this, View_scheduleActivity.class);
                                        intent.putExtra("ParamDate", textDate.getText().toString());
                                        //intent2.putExtra("time", textTime.getText().toString());
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                        startActivity(intent);
                                    }
                                })
                        .setNegativeButton("취소",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(
                                            DialogInterface dialog, int id) {
                                        // 다이얼로그를 취소한다
                                        dialog.cancel();
                                    }
                                });

                // 다이얼로그 생성
                AlertDialog alertDialog = alertDialogBuilder.create();

                // 다이얼로그 보여주기
                alertDialog.show();
                setResult(RESULT_OK);
                break;
            case R.id.btn3:
                setResult(RESULT_CANCELED);

                Intent intent2 = new Intent(this, View_scheduleActivity.class);
                intent2.putExtra("ParamDate", textDate.getText().toString());
                //intent2.putExtra("time", textTime.getText().toString());
                intent2.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent2);
                break;
        }

    }

    private void initListView() {
        mListView = (ListView) findViewById(R.id.listView);
        ArrayList<MediaItem> mediaList = prepareDataSource();
        mAdapter = new MediaItemAdapter(this, R.layout.item, mediaList);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (mMediaPlayer != null && mSelectePoistion == position) {
                    if (mMediaPlayer.isPlaying()) { // 현재 재생 중인 미디어를 선택한 경우
                        mPlaybackPosition = mMediaPlayer.getCurrentPosition();
                        mMediaPlayer.pause();
                        Toast.makeText(getApplicationContext(), "음악 파일 재생 중지됨.", Toast.LENGTH_SHORT).show();
                    } else {
                        mMediaPlayer.start();
                        mMediaPlayer.seekTo(mPlaybackPosition);
                        Toast.makeText(getApplicationContext(), "음악 파일 재생 재시작됨.", Toast.LENGTH_SHORT).show();
                    }
                } else {     // 현재 재생중인 미디어가 없거나, 다른 미디어를 선택한 경우
                    switch (((MediaItem) mAdapter.getItem(position)).type) {
                        case MediaItem.AUDIO:
                            try {
                                playAudio(((MediaItem) mAdapter.getItem(position)).uri);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            Toast.makeText(getApplicationContext(), "음악 파일 재생 시작됨.", Toast.LENGTH_SHORT).show();
                            break;
                        case MediaItem.VIDEO:
                            Intent intent = new Intent(DetailActivity.this, VideoActivity.class);
                            intent.putExtra("video_uri", ((MediaItem) mAdapter.getItem(position)).uri.toString());
                            startActivity(intent);
                            break;
                        case MediaItem.IMAGE:
                            Toast.makeText(getApplicationContext(), "이미지 항목 선택.", Toast.LENGTH_SHORT).show();
                            image.setImageURI(Uri.parse(((MediaItem) mAdapter.getItem(position)).uri.toString()));
                            image.setVisibility(View.VISIBLE);
                            break;
                    }
                    mSelectePoistion = position;
                }
            }


        });
    }

    private ArrayList<MediaItem> prepareDataSource() {

        ArrayList mediaList = new ArrayList<MediaItem>();

        // sdcard/Pictures 데이터 추가
        File file = new File(sdPath);
        File[] files = file.listFiles();
        if (files != null) {
            for (File f : files) {
                Uri uri = Uri.parse("file://" + sdPath + f.getName());
                if (f.getName().contains(".jpg")) {
                    MediaItem item = new MediaItem(MediaItem.SDCARD, f.getName(), uri, MediaItem.IMAGE);
                    mediaList.add(item);

                }
            }
        }

        // sdcard/Movies 데이터 추가
        file = new File(sdPath);
        files = file.listFiles();
        if (files != null) {
            for (File f : files) {
                Log.i("TAG", "File name=" + f.getName());
                Uri uri = Uri.parse("file://" + sdPath + f.getName());
                MediaItem item = new MediaItem(MediaItem.SDCARD, f.getName(), uri, MediaItem.VIDEO);
                mediaList.add(item);

            }
        }
        return mediaList;
    }

    private void playAudio(Uri uri) throws Exception {
        killMediaPlayer();
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setDataSource(getApplicationContext(), uri);
        mMediaPlayer.prepare();
        mMediaPlayer.start();
    }

    private String currentDateFormat(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HH_mm_ss");
        String  currentTimeStamp = dateFormat.format(new Date());
        return currentTimeStamp;
    }

    private void killMediaPlayer() {
        if (mMediaPlayer != null) {
            try {
                mMediaPlayer.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



    protected void onStop() {
        super.onStop();
        killMediaPlayer();
    }

    protected void onDestroy() {
        super.onDestroy();
        killMediaPlayer();
    }

    void DeleteDir(String path)
    {
        File file = new File(path);
        File[] childFileList = file.listFiles();
        for(File childFile : childFileList)
        {
            if(childFile.isDirectory()) {
                DeleteDir(childFile.getAbsolutePath());     //하위 디렉토리 루프
            }
            else {
                childFile.delete();    //하위 파일삭제
            }
        }
        file.delete();    //root 삭제
    }

}