package com.example.administrator.term;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
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

public class Add_scheduleActivity extends AppCompatActivity implements OnClickListener { // 일정목록 추가하기
    MyDBHelper mDBHelper;
    int mId;
    String today, time;
    EditText editTitle, editMemo, editState;
    TextView textTime, textDate, textTime2;
    int mYear, mMonth, mDay, mHour, nMinute;
    /** Called when the activity is first created. */
    final Context context = this;
    //멀티미디어
    private ListView mListView;
    private int mSelectePoistion;
    private MediaItemAdapter mAdapter;
    private MediaPlayer mMediaPlayer;
    private MediaRecorder mMediaRecorder;
    private String recFileN = null;
    private File mPhotoFile =null;
    private String mPhotoFileName = null;

    private int mPlaybackPosition = 0;

    String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/MyDir/";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_schedule);


        GregorianCalendar calendar = new GregorianCalendar();
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay= calendar.get(Calendar.DAY_OF_MONTH);

        textDate = (TextView) findViewById(R.id.textdate);
        editTitle = (EditText) findViewById(R.id.edittitle);
        editState = (EditText) findViewById(R.id.editstate);
        textTime = (TextView) findViewById(R.id.texttime);
        textTime2 = (TextView) findViewById(R.id.texttime2);
        editMemo = (EditText) findViewById(R.id.editmemo);

        Intent intent = getIntent();
        mId = intent.getIntExtra("ParamID", -1);
        today = intent.getStringExtra("ParamDate");
        time = intent.getStringExtra("time");



        mDBHelper = new MyDBHelper(this, "Today.db", null, 1);

        if (mId == -1) {
            textDate.setText(today);
            textTime.setText(time);
            sdPath +=today+"/"+time+"/";
            File file = new File(sdPath);
            file.mkdirs();
        } else {

            SQLiteDatabase db = mDBHelper.getWritableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM today WHERE _id='" + mId
                    + "'", null);

            if (cursor.moveToNext()) {
                editTitle.setText(cursor.getString(1));
                editState.setText(cursor.getString(2));
                textDate.setText(cursor.getString(3));
                textTime.setText(cursor.getString(4));
                textTime2.setText(cursor.getString(5));
                editMemo.setText(cursor.getString(6));
                sdPath += textDate.getText().toString()+"/"+textTime.getText().toString()+"/";
                File file = new File(sdPath);
                file.mkdirs();
            }
            mDBHelper.close();
        }

        Button btn1 = (Button) findViewById(R.id.btnsave);
        btn1.setOnClickListener(this);
        Button btn2 = (Button) findViewById(R.id.btndel);
        btn2.setOnClickListener(this);
        Button btn3 = (Button) findViewById(R.id.btnc);
        btn3.setOnClickListener(this);
        Button datepicker_btn = (Button) findViewById(R.id.datepicker_btn);
        datepicker_btn.setOnClickListener(this);
        Button timepicker_btn = (Button) findViewById(R.id.timepicker_btn);
        timepicker_btn.setOnClickListener(this);
        Button timepicker_btn2 = (Button) findViewById(R.id.timepicker_btn2);
        timepicker_btn2.setOnClickListener(this);

        Button imageCaptureBtn = (Button) findViewById(R.id.imageCaptureBtn);
        final Button voiceRecBtn = (Button) findViewById(R.id.voiceRecBtn);
        Button videoRecBtn = (Button) findViewById(R.id.videoRecBtn);



        voiceRecBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (mMediaRecorder == null) {
                    startAudioRec();
                    voiceRecBtn.setText("녹음중지");
                } else {
                    stopAudioRec();
                    voiceRecBtn.setText("음성녹음");
                }
            }
        });

        videoRecBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                dispatchTakeVideoIntent();
            }
        });

        imageCaptureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });



        datepicker_btn.setOnClickListener(new View.OnClickListener() { // 한 버튼만 OnClickListener를 연결합니다
            public void onClick(View v) { //버튼을 눌렀을때 어떤 작업을 할지 선언합니다
                new DatePickerDialog(Add_scheduleActivity.this, dateSetListener, mYear, mMonth, mDay).show();
            }});

        timepicker_btn.setOnClickListener(new View.OnClickListener() { // 한 버튼만 OnClickListener를 연결합니다
            public void onClick(View v) { //버튼을 눌렀을때 어떤 작업을 할지 선언합니다
                new TimePickerDialog(Add_scheduleActivity.this, timeSetListener, 0, 0 , false).show();
            }});

        timepicker_btn2.setOnClickListener(new View.OnClickListener() { // 한 버튼만 OnClickListener를 연결합니다
            public void onClick(View v) { //버튼을 눌렀을때 어떤 작업을 할지 선언합니다
                new TimePickerDialog(Add_scheduleActivity.this, timeSetListener2, 0, 0 , false).show();
            }});
        if (mId == -1) {
            btn2.setVisibility(View.GONE);
        }
        initListView();
    }

    @Override
    public void onClick(View v) {
// TODO Auto-generated method stub
        SQLiteDatabase db = mDBHelper.getWritableDatabase();

        switch (v.getId()) {
            case R.id.btnsave:
                if(editTitle.getText().toString().equals("") || textDate.getText().toString().equals("") || textTime.getText().toString().equals("") || textTime2.getText().toString().equals(""))
                {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                            context);
                    // 제목셋팅
                    alertDialogBuilder.setTitle("경고");

                    // AlertDialog 셋팅
                    alertDialogBuilder
                            .setMessage("제목, 날짜, 시간은 필수 항목입니다.")
                            .setCancelable(false)
                            .setPositiveButton("확인",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(
                                                DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();

                    alertDialog.show();
                }else if( Integer.parseInt(textTime.getText().toString()) >= Integer.parseInt( (textTime2.getText().toString() ) ) ){
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                            context);
                    // 제목셋팅
                    alertDialogBuilder.setTitle("경고");

                    // AlertDialog 셋팅
                    alertDialogBuilder
                            .setMessage("시작 시간과 종료시간을 확인해주세요.")
                            .setCancelable(false)
                            .setPositiveButton("확인",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(
                                                DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();

                    alertDialog.show();
                }
                else {
                    if (mId != -1) {
                        db.execSQL("UPDATE today SET title='"
                                + editTitle.getText().toString() + "',state='"
                                + editState.getText().toString() + "',date='"
                                + textDate.getText().toString() + "', stime='"
                                + textTime.getText().toString() + "', etime='"
                                + textTime2.getText().toString() + "', memo='"
                                + editMemo.getText().toString() + "' WHERE _id='" + mId
                                + "';");
                    } else {
                        db.execSQL("INSERT INTO today VALUES(null, '"
                                + editTitle.getText().toString() + "', '"
                                + editState.getText().toString() + "', '"
                                + textDate.getText().toString() + "', '"
                                + textTime.getText().toString() + "', '"
                                + textTime2.getText().toString() + "', '"
                                + editMemo.getText().toString() + "');");
                    }
                    mDBHelper.close();
                    setResult(RESULT_OK);

                    //startActivity(new Intent(getActivity(), View_scheduleActivity.class));
                    Intent intent = new Intent(this, DetailActivity.class);
                    intent.putExtra("ParamID", mId);
                    intent.putExtra("ParamDate", textDate.getText().toString());
                    intent.putExtra("time", textTime.getText().toString());
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                }
                break;
            case R.id.btndel:
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                            context);
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
                                            DeleteDir(sdPath);
                                            File f = new File(sdPath);
                                            if(f.isDirectory()) {
                                                DeleteDir(sdPath);
                                            }
                                            Intent intent = new Intent(Add_scheduleActivity.this, View_scheduleActivity.class);
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
            case R.id.btnc:
                finish();
                break;
        }
    }

    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            mYear=year;
            mMonth=monthOfYear;
            mDay=dayOfMonth;
            updateDate();
        }
    };

    private TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            mHour=hourOfDay;
            nMinute=0;
            updateTime();
        }
    };
    private TimePickerDialog.OnTimeSetListener timeSetListener2 = new TimePickerDialog.OnTimeSetListener() {

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            mHour=hourOfDay;
            nMinute=0;
            updateTime2();
        }
    };
    private void updateDate(){
        String str = mYear +"/"+(mMonth+1)+"/"+mDay;
        textDate.setText(str);
    }
    private void updateTime(){
        String str =""+ mHour;
        textTime.setText(str);
    }
    private void updateTime2(){
        String str =""+mHour ;
        textTime2.setText(str);
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
                            Intent intent = new Intent(Add_scheduleActivity.this, VideoActivity.class);
                            intent.putExtra("video_uri", ((MediaItem) mAdapter.getItem(position)).uri.toString());
                            startActivity(intent);
                            break;
                        case MediaItem.IMAGE:
                            Toast.makeText(getApplicationContext(), "이미지 항목 선.", Toast.LENGTH_SHORT).show();
                            break;
                    }
                    mSelectePoistion = position;
                }
            }


        });
    }

    private ArrayList<MediaItem> prepareDataSource() {

        ArrayList mediaList = new ArrayList<MediaItem>();
/*
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
        */
        return mediaList;
    }

    private void playAudio(Uri uri) throws Exception {
        killMediaPlayer();
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setDataSource(getApplicationContext(), uri);
        mMediaPlayer.prepare();
        mMediaPlayer.start();
    }

    private void startAudioRec()  {
        mMediaRecorder = new MediaRecorder();
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);

        recFileN = "VOICE" + currentDateFormat() + ".mp4";
        mMediaRecorder.setOutputFile(sdPath + recFileN);

        try {
            mMediaRecorder.prepare();
            Toast.makeText(getApplicationContext(), "녹음을 시작하세요.", Toast.LENGTH_SHORT).show();
            mMediaRecorder.start();

            SQLiteDatabase db = mDBHelper.getWritableDatabase();
            db.execSQL("UPDATE today SET audio='"
                    + recFileN + "' WHERE date='" + today
                    + "';");
            mDBHelper.close();

        } catch (Exception ex) {
            Log.e("SampleAudioRecorder", "Exception : ", ex);
        }
    }

    private void stopAudioRec()  {

        mMediaRecorder.stop();
        mMediaRecorder.release();
        mMediaRecorder = null;

        Uri uri = Uri.parse("file://" + sdPath+ recFileN);
        mAdapter.addItem(new MediaItem(MediaItem.SDCARD, recFileN,uri));
        Toast.makeText(getApplicationContext(), "녹음이 중지되었습니다.", Toast.LENGTH_SHORT).show();

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


    static final int REQUEST_IMAGE_CAPTURE = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            mPhotoFileName = "IMG"+currentDateFormat()+".jpg";
            mPhotoFile = new File(sdPath,mPhotoFileName);

            if (mPhotoFile !=null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mPhotoFile));
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            } else
                Toast.makeText(getApplicationContext(), "file null", Toast.LENGTH_SHORT).show();
        }
    }

    static final int REQUEST_VIDEO_CAPTURE = 2;

    private void dispatchTakeVideoIntent() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if (mPhotoFileName != null) {
                mPhotoFile = new File(sdPath, mPhotoFileName);
                mAdapter.addItem(new MediaItem(MediaItem.SDCARD, mPhotoFileName, Uri.fromFile(mPhotoFile), MediaItem.IMAGE));
            } else
                Toast.makeText(getApplicationContext(), "mPhotoFile is null", Toast.LENGTH_SHORT).show();
        } else if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            Uri sourceUri = null;
            if (data != null)
                sourceUri = data.getData();
            if (sourceUri != null) {
                recFileN = "VIDEO"+currentDateFormat()+".mp4";
                File destination = new File(sdPath+recFileN);
                saveFile(sourceUri, destination);
                mAdapter.addItem(new MediaItem(MediaItem.SDCARD, recFileN, Uri.fromFile(destination) ,MediaItem.VIDEO));
            } else
                Toast.makeText(getApplicationContext(), "!!! null video.", Toast.LENGTH_LONG).show();
        }
    }


    private void saveFile(Uri sourceUri, File destination) {
        try {
            InputStream in = getContentResolver().openInputStream(sourceUri);
            OutputStream out = new FileOutputStream(destination);

            BufferedInputStream bis = new BufferedInputStream(in);
            BufferedOutputStream bos = new BufferedOutputStream(out);
            byte[] buf = new byte[1024];
            bis.read(buf);

            do {
                bos.write(buf);
            } while(bis.read(buf) != -1);

            if (bis != null) bis.close();
            if (bos != null) bos.close();

        } catch (IOException ex) {
            ex.printStackTrace();
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