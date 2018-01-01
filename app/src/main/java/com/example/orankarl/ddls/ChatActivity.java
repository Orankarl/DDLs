package com.example.orankarl.ddls;

import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private DatabaseManager manager;
    private String currentUsername;
    private long course_id;
    private MsgAdapter adapter;
    private Toolbar toolbar;
    private List<Msg> msgList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Intent intent = getIntent();
        currentUsername = intent.getStringExtra("current_username");
        course_id = intent.getLongExtra("course_id", -1);
        recyclerView = findViewById(R.id.chat_content);

        manager = DatabaseManager.getInstance(this);

        loadMsgList();
        setRecyclerView(recyclerView);

        List<Course> course = manager.queryCourseById(course_id);

        toolbar = findViewById(R.id.chat_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_back_arrow);
        if (course != null) toolbar.setTitle(course.get(0).getTitle());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        final EditText editText = findViewById(R.id.input_text);

        Button button = findViewById(R.id.send_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Msg newMsg = new Msg(Calendar.getInstance().getTimeInMillis(), course_id, currentUsername, currentUsername, editText.getText().toString(), Msg.RIGHT);
                if (manager != null && adapter != null && adapter.mValues != null) {
                    manager.insert(newMsg);
                    adapter.mValues.add(newMsg);
                    adapter.notifyItemInserted(adapter.getItemCount()-1);
                    recyclerView.scrollToPosition(adapter.getItemCount()-1);
                    editText.setText("", TextView.BufferType.EDITABLE);
                }
            }
        });


        Window window = this.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimaryDark));
    }

    private void setRecyclerView(RecyclerView recyclerView) {
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setStackFromEnd(true);
        recyclerView.setLayoutManager(manager);
        adapter = new MsgAdapter(this, msgList);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private void loadMsgList() {
        if (manager != null) {
            msgList = manager.queryMsg(currentUsername, course_id);
            Collections.sort(msgList, MsgComparator.INSTANCE);
            Log.d("MsgCount", String.valueOf(msgList.size()));
        }
    }

    public static class MsgAdapter extends RecyclerView.Adapter<MsgAdapter.ViewHolder> {

        private TypedValue mTypedValue = new TypedValue();
        private int mBackground;
        private List<Msg> mValues;

        public class ViewHolder extends RecyclerView.ViewHolder {
            public View mView;
            public ImageView leftIcon, rightIcon;
            public TextView leftSender, rightSender;
            public TextView leftMsg, rightMsg;
            public LinearLayout leftLayout, rightLayout;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                leftLayout = view.findViewById(R.id.left_layout);
                rightLayout = view.findViewById(R.id.right_layout);
                leftIcon = view.findViewById(R.id.left_icon);
                rightIcon = view.findViewById(R.id.right_icon);
                leftMsg = view.findViewById(R.id.left_msg);
                rightMsg = view.findViewById(R.id.right_msg);
                leftSender = view.findViewById(R.id.left_sender);
                rightSender = view.findViewById(R.id.right_sender);
            }
        }

        public MsgAdapter(Context context, List<Msg> msgList) {
            context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
            mBackground = mTypedValue.resourceId;
            mValues = msgList;
            if (mValues != null) Log.d("list size", String.valueOf(msgList.size()));
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.msg_item, parent, false);
            view.setBackgroundResource(mBackground);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Msg item = mValues.get(position);
            Log.d("test if", "Ok");
            if (item.getType() == Msg.LEFT) {
                Log.d("msgs", item.getContent());
                holder.leftMsg.setText(item.getContent());
                holder.leftSender.setText(item.getSender());
                holder.leftLayout.setVisibility(View.VISIBLE);
                holder.rightLayout.setVisibility(View.GONE);
            } else if (item.getType() == Msg.RIGHT) {
                holder.rightMsg.setText(item.getContent());
                holder.rightSender.setText(item.getSender());
                holder.leftLayout.setVisibility(View.GONE);
                holder.rightLayout.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public int getItemCount() {
            if (mValues != null) return mValues.size();
            return 0;
        }
    }
}
