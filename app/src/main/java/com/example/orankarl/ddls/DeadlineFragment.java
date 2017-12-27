package com.example.orankarl.ddls;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.SwipeDismissBehavior;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.RatingCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
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

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.math.MathContext;
import java.net.PasswordAuthentication;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static android.view.View.GONE;
import static android.view.View.resolveSize;

/**
 * Created by orankarl on 2017/12/21.
 */

public class DeadlineFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private SwipeRefreshLayout swipeRefreshLayout;
    private View view;
    private RecyclerView recyclerView;

    public DeadlineList deadlineList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_deadline, container, false);
        recyclerView = view.findViewById(R.id.deadline_recyclerview);
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
        recyclerView.setAdapter((new SimpleStringRecyclerViewAdapter(getActivity(), deadlineList)));
        swipeRefreshLayout.setRefreshing(false);
        RecyclerView recyclerView = view.findViewById(R.id.deadline_recyclerview);
        TextView textView = view.findViewById(R.id.deadline_empty_text);
        if (deadlineList.isEmpty()) {
            recyclerView.setVisibility(GONE);
            textView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            textView.setVisibility(GONE);
        }
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.setAdapter((new SimpleStringRecyclerViewAdapter(getActivity(), deadlineList)));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
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
            public final CardView cardView;
            public final LinearLayout rightStrip;
            public final int[] androidColors;
            public final View marker;

            public ViewHolder(View view) {
                super(view);
                this.view = view;
                textView1 = view.findViewById(R.id.card_title);
                textView2 = view.findViewById(R.id.card_info);
                textView3 = view.findViewById(R.id.deadline_item_date);
                textView4 = view.findViewById(R.id.deadline_item_year);
                itemLine = view.findViewById(R.id.item_line);
                cardView = view.findViewById(R.id.deadline_cardview);
                rightStrip = view.findViewById(R.id.deadline_right_strip);
                androidColors  = view.getResources().getIntArray(R.array.android_colors1);
                marker = view.findViewById(R.id.deadline_marker);
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
            final Deadline deadline = values.get(position);
            Calendar calendar = deadline.getCalendar();
            String month = String.valueOf(calendar.get(Calendar.MONTH) + 1);
            if (month.length() == 1) month = "0" + month;
            String date = String.valueOf(calendar.get(Calendar.DATE));
            if (date.length() == 1) date = "0" + date;
            String year = String.valueOf(calendar.get(Calendar.YEAR));
            holder.textView3.setText(month+"."+date);
            holder.textView4.setText(year);
            if (position > 0) {
                Deadline pre_deadline = values.get(position - 1);
                Calendar pre_calendar = pre_deadline.getCalendar();
                if (pre_calendar.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)
                        && pre_calendar.get(Calendar.MONTH) == calendar.get(Calendar.MONTH)
                        && pre_calendar.get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH)) {
                    holder.textView3.setVisibility(GONE);
                    holder.textView4.setVisibility(GONE);
                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) holder.cardView.getLayoutParams();
                    params.topMargin = 4;
                }
                Calendar present_calendar = Calendar.getInstance();
                if (CalendarComparator.INSTANCE.compare(present_calendar, pre_calendar) == 1
                        && CalendarComparator.INSTANCE.compare(pre_calendar, calendar) != 1) {
                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) holder.itemLine.getLayoutParams();
                    params.setMargins(0, 24, 0, 0);
                    holder.itemLine.setLayoutParams(params);
                }
            }
            holder.textView1.setText(deadline.getTitle());
            holder.textView2.setText(deadline.getInfo());
            if (holder.textView2.getText().toString().equals("")) {
                holder.textView2.setVisibility(GONE);
            }

            Calendar calendar1 = Calendar.getInstance();

            switch (getItemViewType(position)) {
                case VIEW_TYPE_TOP:
                    holder.itemLine.setBackgroundResource(R.drawable.line_bg_top);
                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) holder.itemLine.getLayoutParams();
                    params.setMargins(0, 24, 0, 0);
                    holder.itemLine.setLayoutParams(params);
                    break;
                case VIEW_TYPE_BOTTOM:
                    holder.itemLine.setBackgroundResource(R.drawable.line_bg_bottom);
                    FrameLayout.LayoutParams params1 = (FrameLayout.LayoutParams) holder.itemLine.getLayoutParams();
                    params1.setMargins(0, 0, 0, 24);
                    holder.itemLine.setLayoutParams(params1);
                    break;
                case VIEW_TYPE_MIDDLE:
                    holder.itemLine.setBackgroundResource(R.drawable.line_bg_middle);
                    break;
            }

            if (CalendarComparator.INSTANCE.compare(calendar1, deadline.getCalendar()) == 1) {
                Log.d("123", calendar1.toString());
                Log.d("1", deadline.getCalendar().toString());
//                holder.itemLine.setBackgroundResource(R.color.colorAccent);
                GradientDrawable gradientDrawable =  (GradientDrawable) holder.itemLine.getBackground();
                gradientDrawable.setColor(Color.parseColor("#FF5722"));
                GradientDrawable gradientDrawable1 = (GradientDrawable) holder.marker.getBackground();
                gradientDrawable1.setColor(Color.parseColor("#FF5722"));
            } else {
                GradientDrawable gradientDrawable1 = (GradientDrawable) holder.marker.getBackground();
                gradientDrawable1.setColor(Color.parseColor("#29B6FC"));
            }

            int randomAndroidColor = holder.androidColors[position % holder.androidColors.length];
            GradientDrawable gradientDrawable = (GradientDrawable) holder.rightStrip.getBackground();
            gradientDrawable.setColor(randomAndroidColor);

            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new MaterialDialog.Builder(view.getContext())
                            .title(deadline.getTitle())
                            .content(deadline.getInfo())
                            .positiveText(R.string.info_dialog_finish)
                            .negativeText(R.string.cancel)
                            .neutralText(R.string.info_dialog_delete)
                            .onAny(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    switch (which) {
                                        case NEUTRAL:{

                                            break;
                                        }
                                        case POSITIVE:{

                                            break;
                                        }
                                        default:break;
                                    }
                                }
                            })
                            .show();
                }
            });
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
