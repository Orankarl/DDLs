package com.example.orankarl.ddls;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
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

import org.litepal.crud.DataSupport;

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

public class DeadlineFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, DeadlineAdapter.onRefreshListener {

    private SwipeRefreshLayout swipeRefreshLayout;
    private View view;
    private RecyclerView recyclerView;

    public DeadlineList deadlineList;

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
    public void undoDeleteDeadline(final Deadline deadline) {
        final Deadline newDeadline = new Deadline(deadline);
        Snackbar.make(view, "Undo the delete action?", Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.undo, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        newDeadline.save();
                        onRefresh();
                    }
                })
                .show();
    }

    @Override
    public void undoFinishDeadline(Deadline deadline, final Long finishedDeadlineId) {
        final Deadline newDeadline = new Deadline(deadline);
        Snackbar.make(view, "Undo the finish action?", Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.undo, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        newDeadline.save();
                        FinishedDeadlineList.Companion.deleteFinishedDeadline(finishedDeadlineId);
                        Log.d("finishedCount", String.valueOf(DataSupport.count(FinishedDeadline.class)));
                        onRefresh();
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
        deadlineList = new DeadlineList();
        deadlineList.loadDeadlineList(currentUserListener.getCurrentUserDeadline());
        setupRecyclerView(recyclerView);
        return view;
    }

    @Override
    public void onRefresh() {
        deadlineList.loadDeadlineList(currentUserListener.getCurrentUserDeadline());
        recyclerView.setAdapter((new DeadlineAdapter(getActivity(), deadlineList, this)));
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

    private DeadlineList getDeadlineList() {
        DeadlineList deadlineList = new DeadlineList();
        deadlineList.loadDeadlineList(currentUserListener.getCurrentUserDeadline());
        return deadlineList;
    }


}
