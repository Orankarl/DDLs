package com.example.orankarl.ddls;

import android.content.Context;

import com.litesuits.orm.LiteOrm;
import com.litesuits.orm.db.assit.QueryBuilder;

import java.util.List;

/**
 * Created by orankarl on 2017/12/31.
 */

public class DatabaseManager {
    public static LiteOrm liteOrm;
    public static DatabaseManager manager;

    private DatabaseManager(Context context) {
        if (liteOrm == null) {
            liteOrm = LiteOrm.newSingleInstance(context, "database");
        }
    }

    public static DatabaseManager getInstance(Context context) {
        context = context.getApplicationContext();
        if (manager == null) {
            synchronized (DatabaseManager.class) {
                if (manager == null) {
                    manager = new DatabaseManager(context);
                }
            }
        }
        return manager;
    }

    public <T> long insert(T t) {
        return liteOrm.save(t);
    }
    public <T> void insertAll(List<T> list) {
        liteOrm.save(list);
    }

    public <T> void delete(T t) {
        liteOrm.delete(t);
    }

    public <T> void deleteAll(Class<T> tClass) {
        liteOrm.delete(tClass);
    }

    public <T> int update(T t) {
        return liteOrm.update(t);
    }

    public <T> int updateAll(List<T> list) {
        return liteOrm.update(list);
    }

    public <T> T queryById(Class<T> tClass, long id) {
        return liteOrm.queryById(id, tClass);
    }
    public <T> List<T> queryAll(Class<T> tClass) {
        return liteOrm.query(tClass);
    }

    public <T> List<T> queryByWhere(Class<T> tClass, String field, String[] value) {
        if (value == null) {
            return null;
        }
        return liteOrm.query(new QueryBuilder<T>(tClass).where(field + "=?", (Object[]) value));
    }

    public List<Deadline> queryDeadline(String username, Boolean isFinished) {
        return liteOrm.query(new QueryBuilder<Deadline>(Deadline.class)
        .where("username = ?", (Object[])new String[]{username})
        .whereAppendAnd()
        .where("finished = ?", (Object[])new String[]{String.valueOf(isFinished)}));
    }

    public List<Notice> queryNotice(String username) {
        return liteOrm.query(new QueryBuilder<Notice>(Notice.class)
        .where("username = ?", (Object[])new String[]{username}));
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> queryByWhereLength(Class<T> tClass, String field, String[] value, int start, int length) {
        return liteOrm.query(new QueryBuilder<T>(tClass).where(field + "=?", (Object[]) value).limit(start, length));
    }

}
