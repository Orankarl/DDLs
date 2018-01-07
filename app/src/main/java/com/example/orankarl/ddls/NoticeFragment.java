package com.example.orankarl.ddls;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import android.widget.Toast;

import org.litepal.crud.DataSupport;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by orankarl on 2017/12/27.
 */

public class NoticeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    public static final int QUERY_SUCCESS = 0, QUERY_ERROR = 1;
    private SwipeRefreshLayout swipeRefreshLayout;
    private View view;
    private RecyclerView recyclerView;
    private List<Notice> noticeList;
    private MyHandler handler = new MyHandler(this);
    DatabaseManager manager;

    NoticeCurrentUserGetter currentUserGetter;

    public interface NoticeCurrentUserGetter {
        User getCurrentUserNotice();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            currentUserGetter = (NoticeCurrentUserGetter) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement getCurrentUserNotice");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_notice, container,false);
        recyclerView = view.findViewById(R.id.notice_recyclerview);
        manager = DatabaseManager.getInstance(this.getContext());
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
        loadNoticeList();
//        Log.d("notice", String.valueOf(noticeList.size()));
        setupRecyclerView(recyclerView);
        return view;
    }

    @Override
    public void onRefresh() {
        queryNotice();
//        loadNoticeList();
//        noticeList.loadNoticeList(currentUserGetter.getCurrentUserNotice());
//        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
//        recyclerView.setAdapter((new SimpleRecyclerViewAdapter(getActivity(), noticeList)));
//        swipeRefreshLayout.setRefreshing(false);
//        recyclerView = view.findViewById(R.id.notice_recyclerview);
//        TextView textView = view.findViewById(R.id.notice_empty_text);
//        if (noticeList.isEmpty()) {
//            recyclerView.setVisibility(View.GONE);
//            textView.setVisibility(View.VISIBLE);
//        } else {
//            recyclerView.setVisibility(View.VISIBLE);
//            textView.setVisibility(View.GONE);
//        }
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setAdapter((new SimpleRecyclerViewAdapter(getActivity(), noticeList)));
    }

    private void queryNotice() {
        if (Net.isLogin) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String ret = Net.queryNotices();
                    if (ret.equals("")) {
                        Message message = new Message();
                        message.what = NoticeFragment.QUERY_SUCCESS;
                        handler.sendMessage(message);
                    } else {
                        Message message = new Message();
                        message.what = NoticeFragment.QUERY_ERROR;
                        message.obj = ret;
                        handler.sendMessage(message);
                    }
                }
            }).start();
        } else {
            updateNoticeAdapter();
        }
    }

    private void loadNoticeList() {
        if (manager != null) {
//            noticeList = manager.queryNotice(currentUserGetter.getCurrentUserNotice().getUsername());
            noticeList = manager.queryAll(Notice.class);
            Collections.sort(noticeList, NoticeComparator.INSTANCE);
            if (noticeList != null) Log.d("notice", String.valueOf(noticeList.size()));
        }
    }

    private void updateNoticeAdapter() {
        loadNoticeList();
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setAdapter((new SimpleRecyclerViewAdapter(getActivity(), noticeList)));
        swipeRefreshLayout.setRefreshing(false);
        recyclerView = view.findViewById(R.id.notice_recyclerview);
        TextView textView = view.findViewById(R.id.notice_empty_text);
        if (noticeList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            textView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            textView.setVisibility(View.GONE);
        }
    }

    public static class SimpleRecyclerViewAdapter extends RecyclerView.Adapter<SimpleRecyclerViewAdapter.ViewHolder> {

        private final TypedValue typedValue = new TypedValue();
        private int background;
        private List<Notice> values;

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

        public SimpleRecyclerViewAdapter(Context context, List<Notice> items) {
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
            holder.textView2.setText(notice.getCreator());
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

    private static class MyHandler extends Handler {
        private final WeakReference<NoticeFragment> fragmentWeakReference;
        MyHandler(NoticeFragment fragment) {
            fragmentWeakReference = new WeakReference<NoticeFragment>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case NoticeFragment.QUERY_SUCCESS:
                    fragmentWeakReference.get().updateNoticeAdapter();
                    break;
                case NoticeFragment.QUERY_ERROR:
                    String ret = (String)msg.obj;
                    Toast.makeText(fragmentWeakReference.get().getContext(), ret, Toast.LENGTH_SHORT).show();
                    fragmentWeakReference.get().updateNoticeAdapter();
                    break;
                default:
                    break;
            }
        }
    }
}
