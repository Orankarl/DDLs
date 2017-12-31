package com.example.orankarl.ddls;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.Calendar;
import java.util.List;


/**
 * Created by orankarl on 2017/12/29.
 */

public class FinishedDeadlineAdapter extends RecyclerView.Adapter<FinishedDeadlineAdapter.ViewHolder> {

    private final TypedValue typedValue = new TypedValue();
    private int background;
    public List<Deadline> values;
    private DialogListener dialogListener;

    public interface DialogListener {
        void unfinishItem(final long id, final int position);
        void deleteItem(final long id, final int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final TextView title, info, date, year;
        public long id;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            title = view.findViewById(R.id.finished_deadline_title);
            info = view.findViewById(R.id.finished_deadline_info);
            date = view.findViewById(R.id.finished_deadline_date);
            year = view.findViewById(R.id.finished_deadline_year);
        }
    }

    public FinishedDeadlineAdapter(Context context, List<Deadline> finishedDeadlineList, DialogListener listener) {
        context.getTheme().resolveAttribute(R.attr.selectableItemBackground, typedValue, true);
        background = typedValue.resourceId;
        values = finishedDeadlineList;
        dialogListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.finished_deadline_item, parent, false);
        view.setBackgroundResource(background);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Deadline finishedDeadline = values.get(position);
        holder.id = finishedDeadline.getId();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(finishedDeadline.getCalendarMillis());
        Calendar finishedCalendar = Calendar.getInstance();
        finishedCalendar.setTimeInMillis(finishedDeadline.getFinishedMillis());
        String month = String.valueOf(calendar.get(Calendar.MONTH) + 1);
        if (month.length() == 1) month = "0" + month;
        String date = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
        if (date.length() == 1) date = "0" + date;
        String year = String.valueOf(calendar.get(Calendar.YEAR));
        holder.year.setText(year);
        holder.date.setText(month+"."+date);
        holder.title.setText(finishedDeadline.getTitle());
        holder.info.setText(finishedDeadline.getInfo());

        final String finishedDate = String.valueOf(finishedCalendar.get(Calendar.YEAR)) + "年"
                + String.valueOf(finishedCalendar.get(Calendar.MONTH) + 1) + "月"
                + String.valueOf(finishedCalendar.get(Calendar.DAY_OF_MONTH)) + "日\n";

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialDialog.Builder builder = new MaterialDialog.Builder(view.getContext())
                        .title(finishedDeadline.getTitle())
                        .customView(R.layout.finished_deadline_dialog, true)
                        .positiveText(R.string.finished_dialog_unfinished)
                        .negativeText(R.string.cancel)
                        .neutralText(R.string.info_dialog_delete)
                        .onAny(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                switch (which) {
                                    case NEUTRAL:
                                        break;
                                    case POSITIVE:
                                        break;
                                    default:break;
                                }
                            }
                        });
                MaterialDialog dialog = builder.build();
                View view1 = dialog.getCustomView();
                if (view1 != null) {
                    TextView textView1 = view1.findViewById(R.id.finished_dialog_date);
                    TextView textView2 = view1.findViewById(R.id.finished_dialog_info);
                    textView1.setText(finishedDate);
                    textView2.setText(finishedDeadline.getInfo());
                }
                dialog.show();

            }
        });
    }

    @Override
    public int getItemCount() {
        return values.size();
    }
}
