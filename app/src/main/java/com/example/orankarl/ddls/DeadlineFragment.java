package com.example.orankarl.ddls;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.SwipeDismissBehavior;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.net.PasswordAuthentication;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by orankarl on 2017/12/21.
 */

public class DeadlineFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private SwipeRefreshLayout swipeRefreshLayout;

    public DeadlineList deadlineList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_deadline, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.deadline_recyclerview);
        swipeRefreshLayout = view.findViewById(R.id.deadline_swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setProgressViewOffset(true, 0, 50);
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                onRefresh();
            }
        });
        deadlineList = new DeadlineList();
        deadlineList.loadDeadlineList();
        setupRecyclerView(recyclerView);
        return view;
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(false);
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.setAdapter((new SimpleStringRecyclerViewAdapter(getActivity(), deadlineList)));
    }

    private DeadlineList getDeadlineList() {
        DeadlineList deadlineList = new DeadlineList();
        deadlineList.loadDeadlineList();
        return deadlineList;
    }

    public static class SimpleStringRecyclerViewAdapter extends RecyclerView.Adapter<SimpleStringRecyclerViewAdapter.ViewHolder> {
        private static final int VIEW_TYPE_TOP = 0;
        private static final int VIEW_TYPE_MIDDLE = 1;
        private static final int VIEW_TYPE_BOTTOM = 2;

        private DeadlineList values;
        private int background;
        private final TypedValue typedValue = new TypedValue();

        public static class ViewHolder extends RecyclerView.ViewHolder {

            public final View view;
            public final TextView textView1;
            public final TextView textView2;
            public final TextView textView3;
            public final TextView textView4;
            public final FrameLayout itemLine;

            public ViewHolder(View view) {
                super(view);
                this.view = view;
                textView1 = view.findViewById(R.id.card_title);
                textView2 = view.findViewById(R.id.card_info);
                textView3 = view.findViewById(R.id.deadline_item_date);
                textView4 = view.findViewById(R.id.deadline_item_year);
                itemLine = view.findViewById(R.id.item_line);
            }
        }

        public SimpleStringRecyclerViewAdapter(Context context, DeadlineList deadlineList) {
            context.getTheme().resolveAttribute(R.attr.selectableItemBackground, typedValue, true);
            background = typedValue.resourceId;
            values = deadlineList;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.deadline_item, parent, false);
            view.setBackgroundResource(background);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Deadline deadline = values.get(position);
            String month = String.valueOf(deadline.getCalendar().get(Calendar.MONTH) + 1);
            if (month.length() == 1) month = "0" + month;
            String date = String.valueOf(deadline.getCalendar().get(Calendar.DATE));
            if (date.length() == 1) date = "0" + date;
            String year = String.valueOf(deadline.getCalendar().get(Calendar.YEAR));
            holder.textView3.setText(month+"."+date);
            holder.textView4.setText(year);
            holder.textView1.setText(deadline.getTitle());
            holder.textView2.setText(deadline.getInfo());

            Calendar calendar1 = Calendar.getInstance();

            switch (getItemViewType(position)) {
                case VIEW_TYPE_TOP:
                    holder.itemLine.setBackgroundResource(R.drawable.line_bg_top);
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.itemLine.getLayoutParams();
                    params.setMargins(0, 24, 0, 0);
                    holder.itemLine.setLayoutParams(params);
                    break;
                case VIEW_TYPE_BOTTOM:
                    holder.itemLine.setBackgroundResource(R.drawable.line_bg_bottom);
                    LinearLayout.LayoutParams params1 = (LinearLayout.LayoutParams) holder.itemLine.getLayoutParams();
                    params1.setMargins(0, 0, 0, 24);
                    holder.itemLine.setLayoutParams(params1);
                    break;
                case VIEW_TYPE_MIDDLE:
                    holder.itemLine.setBackgroundResource(R.drawable.line_bg_middle);
                    break;
            }

            if (calendar1.after(deadline.getCalendar())) {
                GradientDrawable gradientDrawable =  (GradientDrawable) holder.itemLine.getBackground();
                gradientDrawable.setColor(Color.GRAY);
            }

//            holder.view.setOnClickListener();
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0) {
                return VIEW_TYPE_TOP;
            } else if (position == getItemCount() - 1) {
                return VIEW_TYPE_BOTTOM;
            } else {
                return VIEW_TYPE_MIDDLE;
            }
        }

        @Override
        public int getItemCount() {
            return values.size();
        }
    }
}
