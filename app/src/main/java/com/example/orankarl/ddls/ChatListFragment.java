package com.example.orankarl.ddls;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.DrmInitData;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by orankarl on 2017/12/20.
 */

public class ChatListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    private SwipeRefreshLayout swipeRefreshLayout;
    private View view;
    private DatabaseManager manager;
    private static final int NAME_MAX_LEN = 8;
    ChatCurrentUserListener chatCurrentUserListener;
    RecyclerView recyclerView;

    public interface ChatCurrentUserListener {
        User getCurrentUserChat();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            chatCurrentUserListener = (ChatCurrentUserListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement getCurrentUserChat");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_chat_list, container, false);
        recyclerView = view.findViewById(R.id.chat_list_recyclerview);
        swipeRefreshLayout = view.findViewById(R.id.chat_list_swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshLayout.setProgressViewOffset(true, 0, 50);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                onRefresh();
            }
        });
        setupRecyclerView(recyclerView);
        manager = DatabaseManager.getInstance(this.getContext());
        return view;
    }

    @Override
    public void onRefresh() {
        recyclerView.setAdapter((new SimpleStringRecyclerViewAdapter(getActivity(), getChatList())));
        swipeRefreshLayout.setRefreshing(false);
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.setAdapter((new SimpleStringRecyclerViewAdapter(getActivity(), getChatList())));
        recyclerView.addItemDecoration(new DividerItemDecoration(view.getContext(), DividerItemDecoration.VERTICAL));
    }

    private List<Course> getChatList() {
        if (manager != null) {
            List<Course> chatList = manager.queryCourse(chatCurrentUserListener.getCurrentUserChat().getUsername());
            return chatList;
        }
        return null;
    }

    public static class SimpleStringRecyclerViewAdapter extends RecyclerView.Adapter<SimpleStringRecyclerViewAdapter.ViewHolder> {

        private final TypedValue mTypedValue = new TypedValue();
        private int mBackground;
        private List<Course> mValues;

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public String mBoundString; //for intent

            public final View mView;
            public final ImageView imageView;
            public final TextView textView1;
            public final TextView textView2;
            public long course_id;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                imageView = view.findViewById(R.id.avatar);
                textView1 = view.findViewById(R.id.text1);
                textView2 = view.findViewById(R.id.text2);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + textView1.getText();
            }
        }

        public SimpleStringRecyclerViewAdapter(Context context, List<Course> items) {
            context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
            mBackground = mTypedValue.resourceId;
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_list_item, parent, false);
            view.setBackgroundResource(mBackground);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final Course item = mValues.get(position);
            holder.mBoundString = item.getTitle();
            holder.textView1.setText(item.getTitle());
            String name = item.getLatestName();
            if (name != null && name.length() > NAME_MAX_LEN) name = name.substring(0, NAME_MAX_LEN-2) + "..";
            String message = item.getLatestMsg();
            holder.textView2.setText(name + ": " + message);

            holder.course_id = item.getCourse_id();

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Activity activity = (Activity)view.getContext();
                    Intent intent = new Intent(activity, ChatActivity.class);
                    intent.putExtra("course_id", item.getCourse_id());
                    intent.putExtra("current_username", item.getUsername());
                    activity.startActivity(intent);
                }
            });

            Glide.with(holder.imageView.getContext())
                    .load(R.drawable.user)
                    .fitCenter()
                    .into(holder.imageView);
        }

        @Override
        public int getItemCount() {
            if (mValues != null)
                return mValues.size();
            return 0;
        }



    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
            onRefresh();
    }

    @Override
    public void onResume() {
        super.onResume();
        onRefresh();
    }
}
