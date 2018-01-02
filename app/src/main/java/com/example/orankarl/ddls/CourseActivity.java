package com.example.orankarl.ddls;

import android.content.Intent;
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

import java.util.List;

public class CourseActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<Course> courseList;
    private String currentUsername;
    private CourseAdapter adapter;
    private DatabaseManager manager;

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

        loadCourseList();
        setRecyclerView(recyclerView);
    }

    private void setRecyclerView(RecyclerView recyclerView) {
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
            courseList = manager.queryCourse(currentUsername);
        }
    }
}
