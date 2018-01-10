package com.example.orankarl.ddls;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by orankarl on 2018/1/2.
 */

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.ViewHolder> {
    private final TypedValue typedValue = new TypedValue();
    private int background;
    public List<Course> values;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final TextView title, semester, id;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            title = view.findViewById(R.id.course_title);
            semester = view.findViewById(R.id.course_semester);
            id = view.findViewById(R.id.course_id);
        }
    }

    public CourseAdapter(Context context, List<Course> courseList) {
        context.getTheme().resolveAttribute(R.attr.selectableItemBackground, typedValue, true);
        background = typedValue.resourceId;
        values = courseList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.course_item, parent, false);
        view .setBackgroundResource(background);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Course course = values.get(position);
        holder.id.setText("id:" + String.valueOf(course.getId()));
        holder.title.setText(course.getTitle());
        holder.semester.setText(course.getSemester());

    }

    @Override
    public int getItemCount() {
        return values.size();
    }
}
