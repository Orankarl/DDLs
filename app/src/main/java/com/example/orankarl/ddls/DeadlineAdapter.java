package com.example.orankarl.ddls;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.icu.text.LocaleDisplayNames;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextClock;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import org.litepal.crud.DataSupport;

import java.util.Calendar;
import java.util.List;

import static android.view.View.GONE;

/**
 * Created by orankarl on 2017/12/29.
 */

public class DeadlineAdapter
        extends RecyclerView.Adapter<DeadlineAdapter.ViewHolder> {
    private static final int VIEW_TYPE_TOP = 0;
    private static final int VIEW_TYPE_MIDDLE = 1;
    private static final int VIEW_TYPE_BOTTOM = 2;

    public List<Deadline> values;
    private int background;
    private final TypedValue typedValue = new TypedValue();

    public onRefreshListener refreshListener;

    public interface onRefreshListener {
//        void reloadDeadline();
        void undoDeleteDeadline(Deadline deadline, int position);
        void undoFinishDeadline(Long finishedDeadlineId, int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public final View view;
        public final TextView textView1;
        public final TextView textView2;
        public final TextView textView3;
        public final TextView textView4;
        public final FrameLayout itemLine, frameLayout;
        public final CardView cardView;
        public final LinearLayout rightStrip;
        public final int[] androidColors;
        public final View marker;
        public final DisplayMetrics metrics;

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
            metrics = view.getResources().getDisplayMetrics();
            frameLayout = view.findViewById(R.id.timeline_frame);
        }
    }

    public DeadlineAdapter(Context context, List<Deadline> deadlineList, onRefreshListener listener) {
        context.getTheme().resolveAttribute(R.attr.selectableItemBackground, typedValue, true);
        background = typedValue.resourceId;
        values = deadlineList;
        refreshListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.deadline_item, parent, false);
        view.setBackgroundResource(background);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Deadline deadline = values.get(position);
        final long id = deadline.getId();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(deadline.getCalendarMillis());
        String month = String.valueOf(calendar.get(Calendar.MONTH) + 1);
        if (month.length() == 1) month = "0" + month;
        String date = String.valueOf(calendar.get(Calendar.DATE));
        if (date.length() == 1) date = "0" + date;
        String year = String.valueOf(calendar.get(Calendar.YEAR));
        holder.textView3.setText(month+"."+date);
        holder.textView4.setText(year);
        if (position > 0) {
            Deadline pre_deadline = values.get(position - 1);
            Calendar pre_calendar = Calendar.getInstance();
            pre_calendar.setTimeInMillis(pre_deadline.getCalendarMillis());
            if (pre_calendar.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)
                    && pre_calendar.get(Calendar.MONTH) == calendar.get(Calendar.MONTH)
                    && pre_calendar.get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH)) {
//                Log.d("pre_calendar", String.valueOf(pre_calendar.get(Calendar.YEAR))
//                + String.valueOf(pre_calendar.get(Calendar.MONTH)+1)
//                + String.valueOf(pre_calendar.get(Calendar.DAY_OF_MONTH)));
//                Log.d("calendar", String.valueOf(calendar.get(Calendar.YEAR))
//                        + String.valueOf(calendar.get(Calendar.MONTH)+1)
//                        + String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
                holder.textView3.setVisibility(GONE);
                holder.textView4.setVisibility(GONE);
                int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, holder.metrics);
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) holder.cardView.getLayoutParams();
                params.topMargin = px;
            }
            Calendar present_calendar = Calendar.getInstance();
            if (CalendarComparator.INSTANCE.compare(present_calendar, pre_calendar) == 1
                    && CalendarComparator.INSTANCE.compare(present_calendar, calendar) != 1) {
                Log.d("compare", deadline.getTitle());
//                holder.itemLine.setPadding(0, 240, 0, 0);
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.frameLayout.getLayoutParams();
                params.setMargins(0, 24, 0, 0);
                holder.frameLayout.setLayoutParams(params);
            }
        }
        if (position < getItemCount()-1) {
            Deadline postDeadline = values.get(position+1);
            Calendar postCalendar = Calendar.getInstance();
            postCalendar.setTimeInMillis(postDeadline.getCalendarMillis());
            if (postCalendar.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)
                    && postCalendar.get(Calendar.MONTH) == calendar.get(Calendar.MONTH)
                    && postCalendar.get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH)) {
//                    holder.textView3.setVisibility(GONE);
//                    holder.textView4.setVisibility(GONE);
                int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, holder.metrics);
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) holder.cardView.getLayoutParams();
                params.bottomMargin = px;
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

        if (CalendarComparator.INSTANCE.compare(calendar1, calendar) == 1) {
//                holder.itemLine.setBackgroundResource(R.color.colorAccent);
            GradientDrawable gradientDrawable =  (GradientDrawable) holder.itemLine.getBackground();
            gradientDrawable.setColor(Color.parseColor("#FF5722"));
            GradientDrawable gradientDrawable1 = (GradientDrawable) holder.marker.getBackground();
            gradientDrawable1.setColor(Color.parseColor("#FF5722"));
        } else {
            GradientDrawable gradientDrawable =  (GradientDrawable) holder.itemLine.getBackground();
            gradientDrawable.setColor(Color.parseColor("#29B6FC"));
            GradientDrawable gradientDrawable1 = (GradientDrawable) holder.marker.getBackground();
            gradientDrawable1.setColor(Color.parseColor("#29B6FC"));
        }

        int randomAndroidColor = holder.androidColors[position % holder.androidColors.length];
        GradientDrawable gradientDrawable = (GradientDrawable) holder.rightStrip.getBackground();
        gradientDrawable.setColor(randomAndroidColor);

        final DeadlineAdapter adapter = this;

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                new MaterialDialog.Builder(view.getContext())
                        .title(deadline.getTitle())
                        .content(deadline.getInfo())
                        .positiveText(R.string.info_dialog_finish)
                        .negativeText(R.string.cancel)
                        .neutralText(R.string.info_dialog_delete)
                        .onAny(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                DatabaseManager manager = DatabaseManager.getInstance(view.getContext());
                                switch (which) {
                                    case NEUTRAL:{
                                        final Deadline permanentDeadline = manager.queryById(Deadline.class, id);
                                        manager.delete(permanentDeadline);
//                                        final Deadline permanentDeadline = DeadlineList.Companion.queryDeadline(id);
//                                        DeadlineList.Companion.deleteDeadline(id);
                                        values.remove(position);
                                        adapter.notifyItemRemoved(position);
                                        adapter.notifyItemRangeChanged(position, adapter.getItemCount());
                                        refreshListener.undoDeleteDeadline(permanentDeadline, position);
                                        break;
                                    }
                                    case POSITIVE:{
                                        final Deadline permanentDeadline = manager.queryById(Deadline.class, id);
                                        permanentDeadline.setFinished(true);
                                        Calendar calendar2 = Calendar.getInstance();
                                        permanentDeadline.setFinishedMillis(calendar2.getTimeInMillis());
                                        if (manager != null)
                                            manager.update(permanentDeadline);
                                        values.remove(position);
                                        adapter.notifyItemRemoved(position);
                                        adapter.notifyItemRangeChanged(position, adapter.getItemCount());
                                        refreshListener.undoFinishDeadline(id, position);
//                                        DeadlineList.Companion.deleteDeadline(id);
//                                        FinishedDeadline newFinishedDeadline = new FinishedDeadline(permanentDeadline, Calendar.getInstance().getTimeInMillis());
//                                        values.remove(position);
//                                        adapter.notifyItemRemoved(position);
//                                        adapter.notifyItemRangeChanged(position, adapter.getItemCount());
//                                        refreshListener.reloadDeadline();
//                                        refreshListener.undoFinishDeadline(permanentDeadline, newFinishedDeadline.getId(), position);
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
        if (values == null) return 0;
        return values.size();
    }
}
