package model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;


/**
 * Created by Shine on 2016/3/19.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String TABLE_NAME = "CampusMonment";
    private static final int version = 1;
    private static DatabaseHelper instance;
    /**
     * userDao
     */
    private Dao<User, String> userDao;
    private Dao<FeedItem, String> feedDao;


    private DatabaseHelper(Context context) {
        super(context, TABLE_NAME, null, version);
    }

    /**
     * 单例获取该Helper
     *
     * @param context
     * @return
     */
    public static synchronized DatabaseHelper getHelper(Context context) {
        if (instance == null) {
            synchronized (DatabaseHelper.class) {
                if (instance == null)
                    instance = new DatabaseHelper(context);
            }
        }

        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, User.class);
            TableUtils.createTable(connectionSource, FeedItem.class);
            TableUtils.createTable(connectionSource, Topic.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, User.class, true);
            onCreate(database, connectionSource);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获得userDao
     *
     * @return
     * @throws SQLException
     */
    public Dao<User, String> getUserDao() {
        if (userDao == null) {
            try {
                userDao = getDao(User.class);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return userDao;
    }

    public Dao<FeedItem, String> getFeedDao() {

        if (feedDao == null) {
            try {
                feedDao = getDao(FeedItem.class);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return feedDao;
    }

    /**
     * 释放资源
     */
    @Override
    public void close() {
        super.close();
        userDao = null;
    }

}
