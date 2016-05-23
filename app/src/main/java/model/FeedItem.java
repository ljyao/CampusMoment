package model;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.beans.ImageItem;

import java.io.Serializable;
import java.util.ArrayList;

@DatabaseTable
public class FeedItem implements Serializable {
    @DatabaseField(id = true)
    public String id;
    @DatabaseField(dataType = DataType.SERIALIZABLE)
    public ArrayList<Topic> topics;
    @DatabaseField(dataType = DataType.SERIALIZABLE)
    public User creator;
    @DatabaseField
    public String text = "";
    @DatabaseField(dataType = DataType.SERIALIZABLE)
    public ArrayList<String> imageUrls;
    @DatabaseField(dataType = DataType.SERIALIZABLE)
    public ArrayList<User> atFriends;
    @DatabaseField
    public String publishTime;
    @DatabaseField
    public String locationAddr = "";
    @DatabaseField(dataType = DataType.SERIALIZABLE)
    public FeedItem sourceFeed;
    @DatabaseField
    public int likeCount;
    @DatabaseField
    public int forwardCount;
    @DatabaseField
    public int commentCount;
    @DatabaseField
    public boolean isLiked = false;
    @DatabaseField
    public int feedType;
    @DatabaseField
    public String addTime;

    public static FeedItem valueOf(com.umeng.comm.core.beans.FeedItem feedItem, int feedType) {
        if (feedItem == null)
            return null;
        FeedItem newFeedItem = new model.FeedItem();
        newFeedItem.id = feedItem.id;
        newFeedItem.text = feedItem.text;
        newFeedItem.sourceFeed = FeedItem.valueOf(feedItem.sourceFeed, -1);
        newFeedItem.addTime = feedItem.addTime;
        newFeedItem.atFriends = new ArrayList<>();
        for (CommUser atUser : feedItem.atFriends) {
            newFeedItem.atFriends.add(User.valueOf(atUser));
        }
        newFeedItem.creator = User.valueOf(feedItem.creator);
        newFeedItem.imageUrls = new ArrayList<>();
        for (ImageItem imageUrl : feedItem.imageUrls) {
            newFeedItem.imageUrls.add(imageUrl.originImageUrl);
        }
        newFeedItem.topics = new ArrayList<>();
        for (com.umeng.comm.core.beans.Topic newTopic : feedItem.topics) {
            newFeedItem.topics.add(Topic.valueOf(newTopic));
        }
        newFeedItem.commentCount = feedItem.commentCount;
        newFeedItem.likeCount = feedItem.likeCount;
        newFeedItem.forwardCount = feedItem.forwardCount;
        newFeedItem.locationAddr = feedItem.locationAddr;
        newFeedItem.feedType = feedType;
        newFeedItem.publishTime = feedItem.publishTime;
        return newFeedItem;
    }

    public static com.umeng.comm.core.beans.FeedItem toValueOf(FeedItem feedItem) {
        if (feedItem == null)
            return null;
        com.umeng.comm.core.beans.FeedItem newFeedItem = new com.umeng.comm.core.beans.FeedItem();
        newFeedItem.id = feedItem.id;
        newFeedItem.text = feedItem.text;
        newFeedItem.sourceFeed = FeedItem.toValueOf(feedItem.sourceFeed);
        newFeedItem.addTime = feedItem.addTime;
        newFeedItem.atFriends = new ArrayList<>();
        for (User atUser : feedItem.atFriends) {
            newFeedItem.atFriends.add(User.toValue(atUser));
        }
        newFeedItem.creator = User.toValue(feedItem.creator);
        newFeedItem.imageUrls = new ArrayList<>();
        for (String imageUrl : feedItem.imageUrls) {
            newFeedItem.imageUrls.add(new ImageItem("", imageUrl, imageUrl));
        }
        newFeedItem.topics = new ArrayList<>();
        for (Topic newTopic : feedItem.topics) {
            newFeedItem.topics.add(Topic.toValue(newTopic));
        }
        newFeedItem.commentCount = feedItem.commentCount;
        newFeedItem.likeCount = feedItem.likeCount;
        newFeedItem.forwardCount = feedItem.forwardCount;
        newFeedItem.locationAddr = feedItem.locationAddr;
        newFeedItem.publishTime = feedItem.publishTime;
        return newFeedItem;
    }
}
