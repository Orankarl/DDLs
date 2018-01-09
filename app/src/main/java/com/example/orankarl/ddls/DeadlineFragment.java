package com.example.orankarl.ddls;

import android.accessibilityservice.AccessibilityService;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;
import android.nfc.tech.IsoDep;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.TokenWatcher;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.SwipeDismissBehavior;
import android.support.design.widget.TabLayout;
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
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.load.resource.NullDecoder;

import org.litepal.crud.DataSupport;

import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.math.MathContext;
import java.net.PasswordAuthentication;
import java.util.BitSet;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.internal.http.RetryAndFollowUpInterceptor;

import static android.view.View.GONE;
import static android.view.View.LAYER_TYPE_SOFTWARE;
import static android.view.View.SCROLLBAR_POSITION_DEFAULT;
import static android.view.View.resolveSize;

/**
 * Created by orankarl on 2017/12/21.
 */

public class DeadlineFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, DeadlineAdapter.onRefreshListener {
    public static final int QUERY_FINISHED = 0, QUERY_ERROR = 1, ADD_SUCCESS = 2, ADD_ERROR = 3,
            FINISH_SUCCESS = 4, FINISH_ERROR = 5, DELETE_SUCCESS = 6, DELETE_ERROR = 7;

//    private Activity activity;

    private SwipeRefreshLayout swipeRefreshLayout;
    private View view;
    private RecyclerView recyclerView;
    public DeadlineAdapter adapter;
    public List<Deadline> deadlineList;
    private DatabaseManager manager;
    private MyHandler handler = new MyHandler(this);

//    DeadlineCurrentUserGetter currentUserListener;
//
//    public interface DeadlineCurrentUserGetter{
//        User getCurrentUserDeadline();
//    }
//
//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        try {
//            currentUserListener = (DeadlineCurrentUserGetter) context;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(context.toString() + "must implement getCurrentUserDeadline");
//        }
//    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        activity = (Activity) context;
    }

    public void queryDeadline() {
        if (Net.isLogin) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String ret = Net.queryDeadlineList();
                    if (ret.equals("")) {
                        Message message = new Message();
                        message.what = DeadlineFragment.QUERY_FINISHED;
                        handler.sendMessage(message);
                    } else{
                        Message message = new Message();
                        message.what = DeadlineFragment.QUERY_ERROR;
                        message.obj = ret;
                        handler.sendMessage(message);
                    }
                }
            }).start();
        }
    }

    @Override
    public void deleteDeadline(final int id, final int position) {
        if (Net.isLogin) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String ret = Net.deleteDeadline(id);
                    if (ret.equals("")) {
                        Message message = new Message();
                        message.what = DeadlineFragment.DELETE_SUCCESS;
                        message.arg1 = id;
                        message.arg2 = position;
                        handler.sendMessage(message);
                    } else{
                        Message message = new Message();
                        message.what = DeadlineFragment.DELETE_ERROR;
                        message.obj = ret;
                        handler.sendMessage(message);
                    }
                }
            }).start();
        }
        else {
            afterDeleteDeadline(id, position);
        }
    }

    public void afterDeleteDeadline(int id, int position) {
        if (manager != null) {
            Deadline deadline = manager.queryById(Deadline.class, id);
            manager.delete(deadline);
            adapter.values.remove(position);
            adapter.notifyItemRemoved(position);
            Toast.makeText(getContext(), "Deadline deleted", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void finishDeadline(final int finishedDeadlineId, final int position) {
        if (Net.isLogin) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String ret = Net.finishDeadline(finishedDeadlineId, true);
                    if (ret.equals("")) {
                        Message message = new Message();
                        message.what = DeadlineFragment.FINISH_SUCCESS;
                        message.arg1 = finishedDeadlineId;
                        message.arg2 = position;
                        handler.sendMessage(message);
                    } else{
                        Message message = new Message();
                        message.what = DeadlineFragment.FINISH_ERROR;
                        message.obj = ret;
                        handler.sendMessage(message);
                    }
                }
            }).start();
        } else {
            afterFinishDeadline(finishedDeadlineId, position);
        }

    }

    public void afterFinishDeadline(int id, int position) {
        if (manager != null) {
            Deadline finishedDeadline = manager.queryById(Deadline.class, id);
            finishedDeadline.setFinished(true);
            manager.update(finishedDeadline);
            adapter.values.remove(position);
            adapter.notifyItemRemoved(position);
            Toast.makeText(getContext(), "Congratulations!", Toast.LENGTH_SHORT).show();
        }
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
        manager = DatabaseManager.getInstance(this.getContext());
        setupRecyclerView(recyclerView);
        return view;
    }

    @Override
    public void onRefresh() {
        Log.d("refresh", "yes");
        if (Net.isLogin) {
            Log.d("isLogin", "Yes");
            queryDeadline();
        } else {
            Log.d("isLogin", "No");
            updateDeadlineAdapter();
        }
//        loadDeadlineList();
//        deadlineList.loadDeadlineList(currentUserListener.getCurrentUserDeadline());
//        adapter = new DeadlineAdapter(getActivity(), deadlineList, this);
//        recyclerView.setAdapter(adapter);

    }

    public void updateDeadlineAdapter() {
        loadDeadlineList();
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

    public void addNewDeadline(final Deadline deadline) {
        Log.d("isLogin", String.valueOf(Net.isLogin));
        if (Net.isLogin) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String ret = Net.addDeadline(deadline);
                    Log.d("isDigit", String.valueOf(Character.isDigit(ret.charAt(0))));
                    Log.d("FirstDigit", String.valueOf(ret.charAt(0)));
                    if (Character.isDigit(ret.charAt(0))) {
                        long id = Long.parseLong(ret);
                        Message msg = new Message();
                        msg.what = ADD_SUCCESS;
                        msg.arg1 = (int) id;
                        msg.obj = deadline;
                        handler.sendMessage(msg);
                    } else {
                        Message msg = new Message();
                        msg.what = ADD_ERROR;
                        msg.obj = ret;
                        handler.sendMessage(msg);
                    }
                }
            }).start();
        } else {
            Toast.makeText(getContext(), "Please login at first", Toast.LENGTH_SHORT).show();
        }
    }

    public void afterAddDeadline(Deadline deadline) {
        if (manager != null) {
            manager.insert(deadline);
            Toast.makeText(getContext(), "New deadline added successfully", Toast.LENGTH_SHORT).show();
            updateDeadlineAdapter();
        }
    }


    private void setupRecyclerView(RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.setAdapter((new DeadlineAdapter(getActivity(), deadlineList, this)));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private void loadDeadlineList() {
        if (manager != null) {
            if (deadlineList != null) {
                deadlineList.clear();
            }
            if (Net.isLogin) {
                deadlineList = manager.queryDeadline(Net.username, false);
                Log.d("listSize", String.valueOf(deadlineList.size()));
            } else {
                deadlineList = manager.queryDeadline("local", false);
            }

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

    private static class MyHandler extends Handler {
        private final WeakReference<DeadlineFragment> fragmentWeakReference;
        MyHandler(DeadlineFragment fragment) {
            fragmentWeakReference = new WeakReference<DeadlineFragment>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case DeadlineFragment.QUERY_FINISHED:
                    fragmentWeakReference.get().updateDeadlineAdapter();
                    break;
                case DeadlineFragment.QUERY_ERROR:
                    String ret = (String)msg.obj;
                    Toast.makeText(fragmentWeakReference.get().getContext(), ret, Toast.LENGTH_SHORT).show();
                    fragmentWeakReference.get().updateDeadlineAdapter();
                    break;
                case  DeadlineFragment.ADD_SUCCESS:
                    Deadline deadline = (Deadline) msg.obj;
                    int id = msg.arg1;
                    deadline.setId(id);
                    Log.d("Add Success", "true");
                    fragmentWeakReference.get().afterAddDeadline(deadline);
                    break;
                case DeadlineFragment.ADD_ERROR:
                    String reason = (String) msg.obj;
                    Toast.makeText(fragmentWeakReference.get().getContext(), reason, Toast.LENGTH_SHORT).show();
                    break;
                case DeadlineFragment.FINISH_SUCCESS:
                    fragmentWeakReference.get().afterFinishDeadline(msg.arg1, msg.arg2);
                    break;
                case DeadlineFragment.FINISH_ERROR:
                    String finish_reason = (String) msg.obj;
                    Toast.makeText(fragmentWeakReference.get().getContext(), finish_reason, Toast.LENGTH_SHORT).show();
                case DeadlineFragment.DELETE_SUCCESS:
                    fragmentWeakReference.get().afterDeleteDeadline(msg.arg1, msg.arg2);
                    break;
                case DeadlineFragment.DELETE_ERROR:
                    String delete_reason = (String) msg.obj;
                    Toast.makeText(fragmentWeakReference.get().getContext(), delete_reason, Toast.LENGTH_SHORT).show();
                default:
                    break;
            }
        }
    }

}
