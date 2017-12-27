package com.example.orankarl.ddls;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;
import java.util.Random;

/**
 * Created by orankarl on 2017/12/27.
 */

public class NoticeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    private SwipeRefreshLayout swipeRefreshLayout;
    private View view;
    private RecyclerView recyclerView;
    private NoticeList noticeList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_notice, container,false);
        recyclerView = view.findViewById(R.id.notice_recyclerview);
        swipeRefreshLayout = view.findViewById(R.id.notice_swiperefresh);
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
        noticeList = new NoticeList();
        noticeList.loadNoticeList();
        setupRecyclerView(recyclerView);
        return view;
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(false);
        Log.d("123", String.valueOf(noticeList.size()));
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
//        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.setAdapter((new SimpleRecyclerViewAdapter(getActivity(), noticeList)));
    }

    private NoticeList getNoticeList() {
        NoticeList noticeList = new NoticeList();
        noticeList.loadNoticeList();
        return noticeList;
    }

    public static class SimpleRecyclerViewAdapter extends RecyclerView.Adapter<SimpleRecyclerViewAdapter.ViewHolder> {

        private final TypedValue typedValue = new TypedValue();
        private int background;
        private NoticeList values;

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public final LinearLayout topStrip;
            public final View mView;
            public final TextView textView1;
            public final TextView textView2;
            public final TextView textView3;
            public final int[] androidColors;
            public ViewHolder(View view) {
                super(view);
                mView = view;
                textView1 = view.findViewById(R.id.notice_item_title);
                textView2 = view.findViewById(R.id.notice_item_from);
                textView3 = view.findViewById(R.id.notice_item_info);
                topStrip = view.findViewById(R.id.notice_item_top);
                androidColors  = view.getResources().getIntArray(R.array.android_colors);
            }

            @Override
            public String toString() {
                return super.toString() + "'" + textView1.getText();
            }
        }

        public SimpleRecyclerViewAdapter(Context context, NoticeList items) {
            context.getTheme().resolveAttribute(R.attr.selectableItemBackground, typedValue, true);
            background = typedValue.resourceId;
            values = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notice_item, parent, false);
            view.setBackgroundResource(background);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Notice notice = values.get(position);
            holder.textView1.setText(notice.getTitle());
            holder.textView2.setText(notice.getFrom());
            holder.textView3.setText(notice.getInfo());
            int randomAndroidColor = holder.androidColors[position % holder.androidColors.length];
            GradientDrawable gradientDrawable = (GradientDrawable) holder.topStrip.getBackground();
            gradientDrawable.setColor(randomAndroidColor);
        }

        @Override
        public int getItemCount() {
            return values.size();
        }
    }
}
