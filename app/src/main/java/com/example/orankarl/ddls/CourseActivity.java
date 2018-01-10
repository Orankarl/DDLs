package com.example.orankarl.ddls;

import android.content.Intent;
import android.media.midi.MidiManager;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.List;

public class CourseActivity extends AppCompatActivity {

    public static final int QUERY_SUCCESS = 0, QUERY_ERROR = 1;

    private RecyclerView recyclerView;
    private List<Course> courseList;
    private String currentUsername;
    private CourseAdapter adapter;
    private DatabaseManager manager;
    private MyHandler handler = new MyHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);
        recyclerView = findViewById(R.id.course_recyclerview);
        manager = DatabaseManager.getInstance(this);

        Intent intent = getIntent();
        currentUsername = intent.getStringExtra("CurrentUserName");

        Toolbar toolbar = findViewById(R.id.course_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_back_arrow);
        toolbar.setTitle("Courses");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

        queryCourses();
    }

    private void setRecyclerView(RecyclerView recyclerView) {
        loadCourseList();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CourseAdapter(this, courseList);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void loadCourseList() {
        if (manager != null) {
//            courseList = manager.queryAll(Course.class);
            courseList = manager.queryCourse(Net.username);
        }
    }

    public void queryCourses() {
        if (Net.isLogin) {
            if (manager != null) manager.deleteAll(Course.class);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String ret = Net.queryCourse();
                    if (ret.equals("")) {
                        handler.sendEmptyMessage(CourseActivity.QUERY_SUCCESS);
                    } else {
                        Message message = new Message();
                        message.what = CourseActivity.QUERY_ERROR;
                        message.obj = ret;
                        handler.sendMessage(message);
                    }
                }
            }).start();
        } else {
            setRecyclerView(recyclerView);
        }

    }

    private static class MyHandler extends Handler {
        private final WeakReference<CourseActivity> fragmentWeakReference;
        MyHandler(CourseActivity course) {
            fragmentWeakReference = new WeakReference<CourseActivity>(course);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case CourseActivity.QUERY_SUCCESS:
                    fragmentWeakReference.get().setRecyclerView(fragmentWeakReference.get().recyclerView);
                    break;
                case DeadlineFragment.QUERY_ERROR:
                    String ret = (String)msg.obj;
                    Toast.makeText(fragmentWeakReference.get(), ret, Toast.LENGTH_SHORT).show();
                    fragmentWeakReference.get().setRecyclerView(fragmentWeakReference.get().recyclerView);
                    break;
                default:
                    break;
            }
        }
    }
}
