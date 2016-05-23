package model;

import android.content.Context;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.login.LoginListener;
import com.uy.App;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by Shine on 2016/5/19.
 */
@JsonObject
@DatabaseTable
public class User implements Serializable {
    private static User user;
    @JsonField
    @DatabaseField(id = true)
    public String userid;
    @JsonField
    @DatabaseField
    public String password;
    @JsonField
    @DatabaseField
    public String nickname;
    @JsonField
    @DatabaseField
    public int age;
    @DatabaseField
    public String gender;
    @DatabaseField
    public String iconUrl;
    @DatabaseField
    public int feedCount;
    @DatabaseField
    public int fansCount;
    @DatabaseField
    public int followCount;
    @DatabaseField
    public boolean isFollowed;

    public static User valueOf(CommUser user) {
        if (user == null)
            return null;
        User newUser = new User();
        newUser.nickname = user.name;
        newUser.userid = user.id;
        newUser.iconUrl = user.iconUrl;
        newUser.age = user.age;
        newUser.fansCount = user.fansCount;
        newUser.feedCount = user.feedCount;
        newUser.followCount = user.followCount;
        newUser.gender = user.gender.toString();
        newUser.isFollowed = user.isFollowed;
        return newUser;
    }

    public static CommUser toValue(User user) {
        if (user == null)
            return null;
        CommUser newUser = new CommUser();
        newUser.name = user.nickname;
        newUser.id = user.userid;
        newUser.iconUrl = user.iconUrl;
        newUser.age = user.age;
        newUser.fansCount = user.fansCount;
        newUser.feedCount = user.feedCount;
        newUser.followCount = user.followCount;
        newUser.gender = CommUser.Gender.convertToEnum(user.gender);
        newUser.isFollowed = user.isFollowed;
        return newUser;
    }

    public static User getCurrentUser(Context context) {
        if (user != null)
            return user;
        try {
            Dao<User, String
                    > userDao = DatabaseHelper.getHelper(context).getUserDao();
            List<User> users = userDao.queryForAll();
            user = users.get(0);
        } catch (Exception e) {
        }
        return user;
    }

    public static void updateUser(Context context, User newUser) {
        Dao<User, String> userDao = DatabaseHelper.getHelper(context).getUserDao();
        try {
            userDao.deleteBuilder().delete();
            userDao.create(newUser);
            user = newUser;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void logout(Context context) {
        Dao<User, String> userDao = DatabaseHelper.getHelper(context).getUserDao();
        try {
            userDao.deleteBuilder().delete();
            user = null;
            App.getCommunitySDK().logout(context, new LoginListener() {
                @Override
                public void onStart() {

                }

                @Override
                public void onComplete(int i, CommUser commUser) {

                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @JsonObject
    public static class Pojo implements Serializable {
        @JsonField(name = "returnData")
        public User user;
        @JsonField
        public int code = -1;
        @JsonField
        public String reason;
    }
}
