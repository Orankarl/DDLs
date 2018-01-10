package com.example.orankarl.ddls;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.icu.text.UnicodeSetSpanner;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyboardShortcutGroup;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import org.litepal.crud.DataSupport;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.List;

public class FinishedDeadlineActivity extends AppCompatActivity implements FinishedDeadlineAdapter.DialogListener {

    private RecyclerView recyclerView;
    private List<Deadline> deadlineList;
    private String currentUsername;
    private FinishedDeadlineAdapter adapter;
    private DatabaseManager manager;
    private MyHandler handler = new MyHandler(this);
    public static final int UNFINISH_SUCCESS = 0, UNFINISH_ERROR = 1, DELETE_SUCCESS = 2, DELETE_ERROR = 3;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finished_deadline);
        recyclerView = findViewById(R.id.finished_deadline_recyclerview);
        manager = DatabaseManager.getInstance(this);
        Intent intent = getIntent();
        currentUsername = intent.getStringExtra("CurrentUserName");
//        deadlineList = new FinishedDeadlineList();
//        deadlineList.loadFinishedDeadlineList(username);
        loadDeadlineList();
        setRecyclerView(recyclerView);
        Toolbar toolbar = findViewById(R.id.finished_deadline_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_back_arrow);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Window window = this.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimaryDark));
    }

    private void setRecyclerView(RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FinishedDeadlineAdapter(this, deadlineList, this);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition(); //swiped position
                adapter.values.remove(position);
                adapter.notifyItemRemoved(position);
                int id = -1;
                if (viewHolder instanceof FinishedDeadlineAdapter.ViewHolder) {
                    id = ((FinishedDeadlineAdapter.ViewHolder) viewHolder).id;
                }

                if (direction == ItemTouchHelper.LEFT) { //swipe left
                    deleteItem(id, position);
//                    yourarraylist.remove(position);
//                    youradapter.notifyItemRemoved(position);
//                    Toast.makeText(getApplicationContext(),"Swipped to left",Toast.LENGTH_SHORT).show();

                }else if(direction == ItemTouchHelper.RIGHT){//swipe right
                    unfinishItem(id, position);

//                    yourarraylist.remove(position);
//                    youradapter.notifyItemRemoved(position);

//                    Toast.makeText(getApplicationContext(),"Swipped to right",Toast.LENGTH_SHORT).show();

                }
            }

            public static final float ALPHA_FULL = 1.0f;

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

                    View itemView = viewHolder.itemView;

                    Paint p = new Paint();
                    Bitmap icon;

//                    Log.d("dX", String.valueOf(dX));
//                    Log.d("width", String.valueOf(itemView.getWidth()));
                    int itemWidth = itemView.getWidth();
                    if (dX > 0) {
                        //color : left side (swiping towards right)
                        int colorAccent = getResources().getColor(R.color.colorAccent);
                        p.setColor(colorAccent);
                        double alpha = 2.0*dX/itemWidth * 256;
                        if (alpha > 255) alpha = 255;
                        p.setAlpha((int) alpha);
                        c.drawRect((float) itemView.getLeft(), (float) itemView.getTop(), dX,
                                (float) itemView.getBottom(), p);
                        // icon : left side (swiping towards right)
                        icon = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.ic_action_unfinish);
                        c.drawBitmap(icon,
                                (float) itemView.getLeft() + convertDpToPx(16),
                                (float) itemView.getTop() + ((float) itemView.getBottom() - (float) itemView.getTop() - icon.getHeight())/2,
                                p);
                        Paint paint = new Paint();
                        paint.setTextAlign(Paint.Align.LEFT);
                        int textColor = getResources().getColor(R.color.md_blue_grey_50);
                        paint.setColor(textColor);
                        paint.setTextSize(convertDpToPx(24));
                        c.drawText("Unfinish",
                                (float) itemView.getLeft() + convertDpToPx(24) + icon.getWidth(),
                                (float) itemView.getTop() + ((float) itemView.getBottom() - (float) itemView.getTop() - paint.descent() + paint.ascent())/2 - paint.ascent(),
                                paint);
                    } else {
                        //color : right side (swiping towards left)
                        int color = getResources().getColor(R.color.md_red_500);
                        p.setColor(color);
                        double alpha = 2.0*(-dX)/itemWidth * 256;
                        if (alpha > 255) alpha = 255;
                        p.setAlpha((int) alpha);
                        c.drawRect((float) itemView.getRight() + dX, (float) itemView.getTop(),
                                (float) itemView.getRight(), (float) itemView.getBottom(), p);
                        //icon : left side (swiping towards right)
                        icon = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.ic_action_delete);
                        c.drawBitmap(icon,
                                (float) itemView.getRight() - convertDpToPx(16) - icon.getWidth(),
                                (float) itemView.getTop() + ((float) itemView.getBottom() - (float) itemView.getTop() - icon.getHeight())/2,
                                p);
                        Paint paint = new Paint();
                        paint.setTextAlign(Paint.Align.RIGHT);
                        int textColor = getResources().getColor(R.color.md_blue_grey_50);
                        paint.setColor(textColor);
                        paint.setTextSize(convertDpToPx(24));
                        c.drawText("Delete",
                                (float) itemView.getRight() - convertDpToPx(24) - icon.getWidth(),
                                (float) itemView.getTop() + ((float) itemView.getBottom() - (float) itemView.getTop() - paint.descent() + paint.ascent())/2 - paint.ascent(),
                                paint);

                    }

                    // Fade out the view when it is swiped out of the parent
                    final float alpha = ALPHA_FULL - Math.abs(dX) / (float) viewHolder.itemView.getWidth();
                    viewHolder.itemView.setAlpha(alpha);
                    viewHolder.itemView.setTranslationX(dX);

                } else {
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }
            }

            private int convertDpToPx(int dp){
                return Math.round(dp * (getResources().getDisplayMetrics().xdpi / DisplayMetrics.DENSITY_DEFAULT));
            }
        };

        ItemTouchHelper helper = new ItemTouchHelper(simpleCallback);
        helper.attachToRecyclerView(recyclerView);

    }

    public void unfinishItem(final int id, final int position) {
        if (Net.isLogin) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String ret = Net.finishDeadline(id, false);
                    if (ret.equals("")) {
                        Message message = new Message();
                        message.what = UNFINISH_SUCCESS;
                        message.arg1 = id;
                        message.arg2 = position;
                        handler.sendMessage(message);
                    } else {
                        Message message = new Message();
                        message.what = UNFINISH_ERROR;
                        handler.sendMessage(message);
                    }
                }
            }).start();
        }
        else {
            afterUnfinishItem(id, position);
        }
    }

    public void afterUnfinishItem(int id, int position) {
        if (manager != null) {
            Deadline deadline = manager.queryById(Deadline.class, id);
            deadline.setFinished(false);
            manager.update(deadline);
//            adapter.values.remove(position);
//            adapter.notifyItemRemoved(position);
            Toast.makeText(this, "Unfinish Successfully", Toast.LENGTH_SHORT).show();
        }
    }

    public void deleteItem(final int id, final int position) {
        if (Net.isLogin) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String ret = Net.deleteDeadline(id);
                    if (ret.equals("")) {
                        Message message = new Message();
                        message.what = DELETE_SUCCESS;
                        message.arg1 = id;
                        message.arg2 = position;
                        handler.sendMessage(message);
                    } else {
                        Message message = new Message();
                        message.what = DELETE_ERROR;
                        handler.sendMessage(message);
                    }
                }
            }).start();
        }
        else {
            afterDeleteItem(id, position);
        }
    }

    public void afterDeleteItem(int id, int position) {
        if (manager != null) {
            Deadline deadline = manager.queryById(Deadline.class, id);
            manager.delete(deadline);
//            adapter.values.remove(position);
//            adapter.notifyItemRemoved(position);
            Toast.makeText(this, "Delete Successfully", Toast.LENGTH_SHORT);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void loadDeadlineList() {
        if (manager != null)
        deadlineList = manager.queryDeadline(Net.username, true);
        Collections.sort(deadlineList, FinishedDeadlineComparator.INSTANCE);
    }

    public static class MyHandler extends Handler {
        private final WeakReference<FinishedDeadlineActivity> weakReference;
        MyHandler(FinishedDeadlineActivity activity) {
            weakReference = new WeakReference<FinishedDeadlineActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case FinishedDeadlineActivity.UNFINISH_SUCCESS:
                    weakReference.get().afterUnfinishItem(msg.arg1, msg.arg2);
                    break;
                case FinishedDeadlineActivity.UNFINISH_ERROR:
                    Toast.makeText(weakReference.get(), (String)msg.obj, Toast.LENGTH_SHORT).show();
                    break;
                case FinishedDeadlineActivity.DELETE_SUCCESS:
                    weakReference.get().afterDeleteItem(msg.arg1, msg.arg2);
                    break;
                case FinishedDeadlineActivity.DELETE_ERROR:
                    Toast.makeText(weakReference.get(), (String)msg.obj, Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    }
}
