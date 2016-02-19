package community.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.umeng.comm.core.beans.ImageItem;

import java.util.List;


/**
 * Created by shine on 16-2-19.
 */
public class Topic implements Parcelable {
    public static final Creator<Topic> CREATOR = new Creator<Topic>() {
        @Override
        public Topic createFromParcel(Parcel in) {
            return new Topic(in);
        }

        @Override
        public Topic[] newArray(int size) {
            return new Topic[size];
        }
    };
    public String name;
    public String desc;
    public String createTime;
    public boolean isFocused;
    public String nextPage;
    public long feedCount;
    public long fansCount;
    public String icon;
    public List<ImageItem> imageItems;
    public String id;


    public Topic() {
    }

    protected Topic(Parcel in) {
        name = in.readString();
        desc = in.readString();
        createTime = in.readString();
        isFocused = in.readByte() != 0;
        nextPage = in.readString();
        feedCount = in.readLong();
        fansCount = in.readLong();
        icon = in.readString();
        imageItems = in.createTypedArrayList(ImageItem.CREATOR);
        id = in.readString();
    }

    public static Topic valueOf(com.umeng.comm.core.beans.Topic topic) {
        Topic newTopic = new Topic();
        newTopic.createTime = topic.createTime;
        newTopic.desc = topic.desc;
        newTopic.fansCount = topic.fansCount;
        newTopic.feedCount = topic.feedCount;
        newTopic.icon = topic.icon;
        newTopic.imageItems = topic.imageItems;
        newTopic.isFocused = topic.isFocused;
        newTopic.name = topic.name;
        newTopic.id = topic.id;
        newTopic.nextPage = topic.nextPage;
        return newTopic;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(desc);
        dest.writeString(createTime);
        dest.writeByte((byte) (isFocused ? 1 : 0));
        dest.writeString(nextPage);
        dest.writeLong(feedCount);
        dest.writeLong(fansCount);
        dest.writeString(icon);
        dest.writeTypedList(imageItems);
        dest.writeString(id);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
