package model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by Shine on 2016/5/21.
 */
@DatabaseTable
public class Topic implements Serializable {
    @DatabaseField(id = true)
    public String id;
    @DatabaseField
    public String name;
    @DatabaseField
    public String desc;
    @DatabaseField
    public String createTime;
    @DatabaseField
    public boolean isFocused;
    @DatabaseField
    public long feedCount;
    @DatabaseField
    public long fansCount;
    @DatabaseField
    public String icon;

    public static Topic valueOf(com.umeng.comm.core.beans.Topic topic) {
        if (topic == null)
            return null;
        Topic newTopic = new Topic();
        newTopic.desc = topic.desc;
        newTopic.createTime = topic.createTime;
        newTopic.fansCount = topic.fansCount;
        newTopic.feedCount = topic.fansCount;
        newTopic.icon = topic.icon;
        newTopic.name = topic.name;
        newTopic.id = topic.id;
        newTopic.isFocused = topic.isFocused;
        return newTopic;
    }

    public static com.umeng.comm.core.beans.Topic toValue(Topic topic) {
        if (topic == null)
            return null;
        com.umeng.comm.core.beans.Topic newTopic = new com.umeng.comm.core.beans.Topic();
        newTopic.desc = topic.desc;
        newTopic.createTime = topic.createTime;
        newTopic.fansCount = topic.fansCount;
        newTopic.feedCount = topic.fansCount;
        newTopic.icon = topic.icon;
        newTopic.name = topic.name;
        newTopic.id = topic.id;
        newTopic.isFocused = topic.isFocused;
        return newTopic;
    }
}
