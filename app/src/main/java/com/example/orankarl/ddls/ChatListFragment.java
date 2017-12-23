package com.example.orankarl.ddls;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
    private static final int NAME_MAX_LEN = 8;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_list, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.chat_list_recyclerview);
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
        return view;
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(false);
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.setAdapter((new SimpleStringRecyclerViewAdapter(getActivity(), getChatList())));
    }

    private ChatList getChatList() {
        ChatList chatList = new ChatList();
        chatList.loadChatList();
        return chatList;
    }

    public static class SimpleStringRecyclerViewAdapter extends RecyclerView.Adapter<SimpleStringRecyclerViewAdapter.ViewHolder> {

        private final TypedValue mTypedValue = new TypedValue();
        private int mBackground;
        private ChatList mValues;

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public String mBoundString; //for intent

            public final View mView;
            public final ImageView imageView;
            public final TextView textView1;
            public final TextView textView2;

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

        public SimpleStringRecyclerViewAdapter(Context context, ChatList items) {
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
            ChatItem item = mValues.get(position);
            holder.mBoundString = item.getTitle();
            holder.textView1.setText(item.getTitle());
            String name = item.getLatestSender();
            if (name.length() > NAME_MAX_LEN) name = name.substring(0, NAME_MAX_LEN-2) + "..";
            String message = item.getLatestMessage();
            holder.textView2.setText(name + ": " + message);

//            holder.mView.setOnClickListerner

            Glide.with(holder.imageView.getContext())
                    .load(R.drawable.user)
                    .fitCenter()
                    .into(holder.imageView);
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }
    }
}
