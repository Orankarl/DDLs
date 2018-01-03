package com.example.orankarl.ddls;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.SwipeDismissBehavior;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.RatingCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
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
import com.bumptech.glide.load.resource.NullDecoder;

import org.litepal.crud.DataSupport;

import java.lang.reflect.Array;
import java.math.MathContext;
import java.net.PasswordAuthentication;
import java.util.BitSet;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static android.view.View.GONE;
import static android.view.View.LAYER_TYPE_SOFTWARE;
import static android.view.View.SCROLLBAR_POSITION_DEFAULT;
import static android.view.View.resolveSize;

/**
 * Created by orankarl on 2017/12/21.
 */

public class DeadlineFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, DeadlineAdapter.onRefreshListener {

    private SwipeRefreshLayout swipeRefreshLayout;
    private View view;
    private RecyclerView recyclerView;
    public DeadlineAdapter adapter;
    public List<Deadline> deadlineList;
    private DatabaseManager manager;

    DeadlineCurrentUserGetter currentUserListener;

    public interface DeadlineCurrentUserGetter{
        User getCurrentUserDeadline();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            currentUserListener = (DeadlineCurrentUserGetter) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement getCurrentUserDeadline");
        }
    }

    @Override
    public void reloadDeadline() {
        onRefresh();
    }

    @Override
    public void undoDeleteDeadline(final Deadline deadline, final int position) {
        final Deadline newDeadline = new Deadline(deadline);
        Snackbar.make(view, "Undo the delete action?", Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.undo, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (manager != null) {
                            manager.insert(newDeadline);
                        }
                        adapter.values.add(position, newDeadline);
                        onRefresh();
//                        adapter.notifyDataSetChanged();
//                        adapter.notifyItemRangeChanged(position, adapter.getItemCount());
                    }
                })
                .show();
    }

    @Override
    public void undoFinishDeadline(final Long finishedDeadlineId, final int position) {
        Snackbar.make(view, "Undo the finish action?", Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.undo, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        manager = DatabaseManager.getInstance(getContext());
                        if (manager != null) {
                            Deadline deadline = manager.queryById(Deadline.class, finishedDeadlineId);
                            if (deadline != null) {
                                deadline.setFinished(false);
                            }
                            manager.update(deadline);
                            adapter.values.add(position, deadline);
                            onRefresh();
                        }
//                        newDeadline.save();
//                        FinishedDeadlineList.Companion.deleteFinishedDeadline(finishedDeadlineId);
//                        adapter.values.addItem(position, newDeadline);
//                        onRefresh();
//                        adapter.notifyDataSetChanged();
//                        adapter.notifyItemRangeChanged(0, adapter.getItemCount());
                    }
                })
                .show();
    }

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
        loadDeadlineList();
        setupRecyclerView(recyclerView);

        manager = DatabaseManager.getInstance(this.getContext());
        return view;
    }

    @Override
    public void onRefresh() {
        loadDeadlineList();
//        deadlineList.loadDeadlineList(currentUserListener.getCurrentUserDeadline());
        adapter = new DeadlineAdapter(getActivity(), deadlineList, this);
        recyclerView.setAdapter(adapter);
        swipeRefreshLayout.setRefreshing(false);
        recyclerView = view.findViewById(R.id.deadline_recyclerview);
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
        recyclerView.setAdapter((new DeadlineAdapter(getActivity(), deadlineList, this)));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private void loadDeadlineList() {
//        Net.queryDeadlineList();
        User currentUser = currentUserListener.getCurrentUserDeadline();
        if (manager != null) {
            deadlineList = manager.queryDeadline(currentUser.getUsername(), false);
//            deadlineList = manager.queryByWhere(Deadline.class, "username", args);
            Collections.sort(deadlineList, DeadlineComparator.INSTANCE);
        }
    }

    public Bitmap getScreenshot() {
        int size = recyclerView.getAdapter().getItemCount();
        int iHeight = 0;
        DeadlineAdapter.ViewHolder viewHolder = (DeadlineAdapter.ViewHolder) recyclerView.getAdapter().createViewHolder(recyclerView, 0);
        for (int i = 0; i < size; i++) {
            recyclerView.getAdapter().onBindViewHolder(viewHolder, i);
            viewHolder.itemView.measure(View.MeasureSpec.makeMeasureSpec(recyclerView.getWidth(), View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            iHeight += viewHolder.itemView.getMeasuredHeight();
            viewHolder.itemView.layout(0, 0, viewHolder.itemView.getMeasuredWidth(), viewHolder.itemView.getMeasuredHeight());
        }
        Bitmap bigBitmap = Bitmap.createBitmap(recyclerView.getMeasuredWidth(), iHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bigBitmap);
        canvas.drawColor(Color.WHITE);

        Paint paint = new Paint();
        iHeight = 0;
        for (int i = 0; i < size; i++) {
            recyclerView.getAdapter().onBindViewHolder(viewHolder, i);
            int height = iHeight;
            viewHolder.itemView.measure(
                    View.MeasureSpec.makeMeasureSpec(recyclerView.getWidth(), View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            );
            viewHolder.itemView.setLayerType(LAYER_TYPE_SOFTWARE, null);
            viewHolder.itemView.setDrawingCacheEnabled(true);
            viewHolder.itemView.buildDrawingCache();
            canvas.drawBitmap(viewHolder.itemView.getDrawingCache(), 0f, iHeight, paint);

            viewHolder.textView3.setDrawingCacheEnabled(true);
            viewHolder.textView3.buildDrawingCache();
            canvas.drawBitmap(viewHolder.textView3.getDrawingCache(), 3f, iHeight, paint);
            viewHolder.textView3.setDrawingCacheEnabled(false);
            viewHolder.textView3.destroyDrawingCache();
            viewHolder.textView4.setDrawingCacheEnabled(true);
            viewHolder.textView4.buildDrawingCache();
            canvas.drawBitmap(viewHolder.textView4.getDrawingCache(), 3f, iHeight+viewHolder.textView3.getMeasuredHeight(), paint);
            viewHolder.textView4.setDrawingCacheEnabled(false);
            viewHolder.textView4.destroyDrawingCache();

            View cardView = viewHolder.cardView;
//            viewHolder.cardView.setDrawingCacheEnabled(true);
//            viewHolder.cardView.buildDrawingCache();
//            canvas.drawBitmap(viewHolder.cardView.getDrawingCache(), viewHolder.textView1.getMeasuredWidth()+viewHolder.itemLine.getMeasuredWidth(), iHeight, paint);
//            viewHolder.cardView.setDrawingCacheEnabled(false);
//            viewHolder.cardView.destroyDrawingCache();

            iHeight += viewHolder.itemView.getMeasuredHeight();
            viewHolder.itemView.setDrawingCacheEnabled(false);
            viewHolder.itemView.destroyDrawingCache();
        }
        return bigBitmap;
    }


}
