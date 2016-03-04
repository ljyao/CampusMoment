/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2015 Umeng, Inc
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.umeng.comm.impl;

import android.text.TextUtils;

import com.activeandroid.Model;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.beans.Comment;
import com.umeng.comm.core.beans.FeedItem;
import com.umeng.comm.core.beans.ImageItem;
import com.umeng.comm.core.beans.Like;
import com.umeng.comm.core.beans.Topic;
import com.umeng.comm.core.beans.relation.DBRelationOP;
import com.umeng.comm.core.beans.relation.EntityRelationFactory;
import com.umeng.comm.core.beans.relation.Friendfeed;
import com.umeng.comm.core.beans.relation.NormalFeed;
import com.umeng.comm.core.beans.relation.RecommendFeed;
import com.umeng.comm.core.beans.relation.TopItemn;
import com.umeng.comm.core.constants.Constants;
import com.umeng.comm.core.db.ctrl.FeedDBAPI;
import com.umeng.comm.core.listeners.Listeners.SimpleFetchListener;
import com.umeng.comm.core.utils.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * 对feed在本地进行add、delete、query 操作。
 */
class FeedDBAPIImpl extends AbsDbAPI<List<FeedItem>> implements FeedDBAPI {
    private int mOffset = 0;
    private int mFeedCount = 0;

    @Override
    public void loadFeedsFromDB(final SimpleFetchListener<List<FeedItem>>
            listener) {

        submit(new DbCommand() {

            @Override
            protected void execute() {
                initFeedsCount();
                Log.d("database","loading feeds from DB");
                // 分页加载
                List<FeedItem> items = new
                        Select().from(FeedItem.class).where("isfriends=1")
                                .limit(Constants.COUNT).offset(mOffset).orderBy("publishTime DESC")
                                .execute();
                fillItems(items);
                deliverResult(listener, items);
                updateOffset();
            }
        });

    }
    @Override
    public void loadFeedsFromDBWithTopItem(final SimpleFetchListener<List<FeedItem>>
                                        listener) {

        submit(new DbCommand() {

            @Override
            protected void execute() {
                initFeedsCount();
                Log.d("database","loading feeds from DB");
                // 分页加载
                List<FeedItem> items = new
                        Select().from(FeedItem.class).innerJoin(NormalFeed.class)
                        .on("feeditem._id=normalitem.feed_id")
                        .orderBy("publishTime DESC")
                        .execute();
                for (FeedItem top:items){
                    top.isTop = 0;
                }
                List<FeedItem> topitems = new
                        Select().from(FeedItem.class).innerJoin(TopItemn.class)
                        .on("feeditem._id=topitem.feed_id")
                        .execute();
                for (FeedItem top:topitems){
                    top.isTop = 1;
                }
                items.removeAll(topitems);
                items.addAll(0,topitems);
                fillItems(items);
                deliverResult(listener, items);
                updateOffset();
            }
        });

    }
    /**
     * currently no use weibo version
     * @param days
     */
    @Override
    public void loadHottestFeeds(int days, SimpleFetchListener<List<FeedItem>> listener) {

    }

    @Override
    public void loadFeedsFromDB(final String uid, final SimpleFetchListener<List<FeedItem>> listener) {

        submit(new DbCommand() {

            @Override
            protected void execute() {
                List<FeedItem> items = new Select().from(FeedItem.class).execute();
                List<FeedItem> targetItems = filterFeedItems(items, uid);
                fillItems(targetItems);
                deliverResult(listener, targetItems);
            }
        });
    }

    @Override
    public void loadTopFeedsFromDB( final SimpleFetchListener<List<FeedItem>> listener) {
        submit(new DbCommand() {

            @Override
            protected void execute() {
                initFeedsCount();
                Log.d("database","loading feeds from DB");
                // 分页加载
                List<FeedItem> items = new
                        Select().from(FeedItem.class).innerJoin(TopItemn.class)
                        .on("feeditem._id=topitem.feed_id")
                        .execute();
                fillItems(items);
                deliverResult(listener, items);
                updateOffset();
            }
        });
    }

    private void initFeedsCount() {
        if (mFeedCount == 0) {
            mFeedCount = new Select().from(FeedItem.class).count();
        }
    }

    private void updateOffset() {
        if (mOffset + Constants.COUNT <= mFeedCount) {
            mOffset += Constants.COUNT;
        } else {
            mOffset = mFeedCount;
        }

    }

    private List<FeedItem> filterFeedItems(List<FeedItem> response, String uid) {
        List<FeedItem> targetItems = new ArrayList<FeedItem>();
        for (FeedItem feedItem : response) {
            if (feedItem.creator.id.equals(uid)) {
                targetItems.add(feedItem);
            }
        }
        return targetItems;
    }

    @Override
    public void saveFeedsToDB(List<FeedItem> feedItems) {
        if (feedItems == null || feedItems.size() == 0) {
            return;
        }

        final List<FeedItem> itertorItems = new ArrayList<FeedItem>(feedItems);
        // 保存到数据库
        submit(new DbCommand() {

            @Override
            protected void execute() {
                for (FeedItem feedItem : itertorItems) {
                    if (feedItem.sourceFeed != null) {
                        saveFeedToDB(feedItem.sourceFeed);// 保存被转发的feed
                    }
                    // 保存feed
                    saveFeedToDB(feedItem);
                }
            }
        });

    }

    @Override
    public void deleteFeedFromDB(final String feedId) {
        if (TextUtils.isEmpty(feedId)) {
            return;
        }

        submit(new DbCommand() {

            @Override
            protected void execute() {
                // 删除feed本身
                new Delete().from(FeedItem.class).where("_id=?", feedId).execute();
                // 删除跟feed相关的关系表
                DBRelationOP<?> feedCreator = EntityRelationFactory.createFeedCreator();
                feedCreator.deleteById(feedId);
                DBRelationOP<?> feedFriends = EntityRelationFactory.createFeedFriends();
                feedFriends.deleteById(feedId);
                // 删除图片
                deleteImagesForFeed(feedId);
                DBRelationOP<?> feedTopic = EntityRelationFactory.createFeedTopic();
                feedTopic.deleteById(feedId);
                DBRelationOP<?> feedLike = EntityRelationFactory.createFeedLike();
                feedLike.deleteById(feedId);
                DBRelationOP<?> feedComment = EntityRelationFactory.createFeedComment();
                feedComment.deleteById(feedId);
            }
        });

    }

    private void deleteImagesForFeed(String feedId) {
        new Delete().from(ImageItem.class).where("feedId=?", feedId).execute();
    }

    @Override
    public void deleteAllFeedsFromDB() {
        submit(new DeleteAllFeedItemCmd());
    }

    @Override
    public void queryFeedCount(final String uid, final SimpleFetchListener<Integer> listener) {
        submit(new DbCommand() {

            @Override
            protected void execute() {
                DBRelationOP<?> relationOP = EntityRelationFactory.createFeedCreator();
                deliverResultForCount(listener, relationOP.queryCountById(uid));
            }
        });
    }

    /**
     * 填充feed的相关数据</br>
     * 
     * @param items
     */
    private void fillItems(List<FeedItem> items) {
        for (final FeedItem item : items) {
            if (!TextUtils.isEmpty(item.sourceFeedId)) {
                FeedItem feedItem = new Select().from(FeedItem.class)
                        .where("_id=?",
                                item.sourceFeedId)
                        .executeSingle();
                fillOneItem(feedItem);// 填充源feed的数据
                item.sourceFeed = feedItem;
            }
            fillOneItem(item);
        }
    }
    private void fillTopItems(List<FeedItem> items) {
        for (final FeedItem item : items) {
           item.isTop = 0;
        }
    }
    /**
     * 填充feed每项数据</br>
     * 
     * @param item
     */
    private void fillOneItem(FeedItem item) {
        if (item == null) {
            return;
        }
        DBRelationOP<CommUser> feedCreator = EntityRelationFactory.createFeedCreator();
        DBRelationOP<List<CommUser>> feedFriends = EntityRelationFactory.createFeedFriends();
        DBRelationOP<List<Topic>> feedTopic = EntityRelationFactory.createFeedTopic();
         DBRelationOP<List<Like>> feedLike =
         EntityRelationFactory.createFeedLike();
        // DBRelationOP<List<Comment>> feedComment =
        // EntityRelationFactory.createFeedComment();

        item.creator = feedCreator.queryById(item.id);
        item.atFriends = feedFriends.queryById(item.id);
        item.imageUrls = selectImagesForFeed(item.id);
        item.topics = feedTopic.queryById(item.id);
        item.likes = feedLike.queryById(item.id);
        Log.d("database","size: "+item.likes.size());
        // item.comments = feedComment.queryById(item.id);
    }

    private List<ImageItem> selectImagesForFeed(String feedId) {
        return new Select().from(ImageItem.class).where("feedId=?", feedId).execute();
    }

    @Override
    public void saveFeedToDB(final FeedItem feedItem) {
        submit(new DbCommand() {
            @Override
            protected void execute() {
                // 存储feed本身的信息
                feedItem.saveEntity();
                // 存储一些关联信息
                saveRelationship(feedItem);
            }
        });
    }

    @Override
    public void saveTopFeedToDB(List<TopItemn> topItemns) {
        if (topItemns == null || topItemns.size() == 0) {
            return;
        }

        final List<TopItemn> itertorItems = new ArrayList<TopItemn>(topItemns);
        // 保存到数据库
        submit(new DbCommand() {

            @Override
            protected void execute() {
                for (final TopItemn feedItem : itertorItems) {

                    // 保存feed
                    submit(new DbCommand() {
                        @Override
                        protected void execute() {
                            // 存储feed本身的信息
                            feedItem.saveEntity();
                            // 存储一些关联信息

                        }
                    });
                }
            }
        });
    }

    @Override
    public void saveFriendFeedToDB(List<Friendfeed> items) {
        if (items == null || items.size() == 0) {
            return;
        }

        final List<Friendfeed> itertorItems = new ArrayList<Friendfeed>(items);
        // 保存到数据库
        submit(new DbCommand() {

            @Override
            protected void execute() {
                for (final Friendfeed feedItem : itertorItems) {

                    // 保存feed
                    submit(new DbCommand() {
                        @Override
                        protected void execute() {
                            // 存储feed本身的信息
                            feedItem.saveEntity();
                            // 存储一些关联信息

                        }
                    });
                }
            }
        });
    }

    @Override
    public void saveNormalFeedToDB(List<NormalFeed> items) {
        if (items == null || items.size() == 0) {
            return;
        }
        final List<NormalFeed> itertorItems = new ArrayList<NormalFeed>(items);
        // 保存到数据库
        submit(new DbCommand() {

            @Override
            protected void execute() {
                for (final NormalFeed feedItem : itertorItems) {

                    // 保存feed
                    submit(new DbCommand() {
                        @Override
                        protected void execute() {
                            // 存储feed本身的信息
                            feedItem.saveEntity();
                            // 存储一些关联信息

                        }
                    });
                }
            }
        });
    }

    @Override
    public void saveRecommendFeedToDB(List<RecommendFeed> items) {
        if (items == null || items.size() == 0) {
            return;
        }

        final List<RecommendFeed> itertorItems = new ArrayList<RecommendFeed>(items);
        // 保存到数据库
        submit(new DbCommand() {

            @Override
            protected void execute() {
                for (final RecommendFeed feedItem : itertorItems) {

                    // 保存feed
                    submit(new DbCommand() {
                        @Override
                        protected void execute() {
                            // 存储feed本身的信息
                            feedItem.saveEntity();
                            // 存储一些关联信息

                        }
                    });
                }
            }
        });
    }

    /**
     * 存储feed的关系表,例如评论、@的好友、like等
     * 
     * @param feedItem
     */
    private void saveRelationship(FeedItem feedItem) {
        DBRelationOP<CommUser> feedCreator = EntityRelationFactory.createFeedCreator(feedItem,
                feedItem.creator);
        feedCreator.saveEntity();
        // 存储@的好友
        List<CommUser> friends = new ArrayList<CommUser>(feedItem.atFriends);
        for (CommUser friend : friends) {
            DBRelationOP<?> feedFriends = EntityRelationFactory.createFeedFriends(feedItem, friend);
            feedFriends.saveEntity();
        }

        // 存储feed所属的话题
        List<Topic> topics = new ArrayList<Topic>(feedItem.topics);
        for (Topic topic : topics) {
            DBRelationOP<?> feedTopic = EntityRelationFactory.createFeedTopic(feedItem, topic);
            feedTopic.saveEntity();
        }

        // 存储feed的赞
        List<Like> likes = new ArrayList<Like>(feedItem.likes);
        for (Like like : likes) {
            DBRelationOP<?> feedLike = EntityRelationFactory.createFeedLike(feedItem, like);
            feedLike.saveEntity();
        }

        // 存储feed的评论
        List<Comment> comments = new ArrayList<Comment>(feedItem.comments);
        for (Comment comment : comments) {
            DBRelationOP<?> feedComment = EntityRelationFactory
                    .createFeedComment(feedItem, comment);
            feedComment.saveEntity();
        }
    }

    @Override
    public void resetOffset() {
        mOffset = 0;
    }

    @Override
    public void loadRecommendFeedsFromDB(final SimpleFetchListener<List<FeedItem>> listener) {
//        List<FeedItem> items = new Select().from(FeedItem.class)
//                .where("recommended=1").execute();
//        fillItems(items);
//        fillTopItems(items);
//        Log.e("xxxxx", "loadRecommendFeedsFromDB=" + items);
//        deliverResult(listener, items);
        submit(new DbCommand() {

            @Override
            protected void execute() {
                initFeedsCount();
                Log.d("database","loading feeds from DB");
                // 分页加载
                List<FeedItem> items = new
                        Select().from(FeedItem.class).innerJoin(RecommendFeed.class)
                        .on("feeditem._id=recommends.feed_id")
                        .orderBy("publishTime DESC")
                        .execute();
                fillItems(items);
                deliverResult(listener, items);
                updateOffset();
            }
        });
    }

    @Override
    public void loadFriendsFeedsFromDB(final SimpleFetchListener<List<FeedItem>> listener) {
//        List<FeedItem> items = new Select().from(FeedItem.class)
//                .where("isfriends=1").execute();
//        fillItems(items);
//        fillTopItems(items);
//        deliverResult(listener, items);
        submit(new DbCommand() {

            @Override
            protected void execute() {
                initFeedsCount();
                Log.d("database","loading feeds from DB");
                // 分页加载
                List<FeedItem> items = new
                        Select().from(FeedItem.class).innerJoin(Friendfeed.class)
                        .on("feeditem._id=frienditem.feed_id")
                        .orderBy("publishTime DESC")
                        .execute();
                fillItems(items);
                deliverResult(listener, items);
                updateOffset();
            }
        });
    }

    @Override
    public void deleteAllRecommendFeed() {
        new Delete().from(RecommendFeed.class).execute();
    }
    @Override
    public void deleteTopFeed() {

        new Delete().from(TopItemn.class).execute();
    }
    @Override
    public void deleteFriendFeed(String uid) {
//        if (TextUtils.isEmpty(uid)) {
//            return;
//        }
//        List<FeedItem> items = new Select().from(FeedItem.class)
//                .where("isfriends=1").execute();
//        fillItems(items);
//        for (FeedItem item : items) {
//            if (item.creator.id.equals(uid)) {
//                deleteFeedFromDB(item.id);
//            }
//        }
        new Delete().from(Friendfeed.class).execute();
    }

    @Override
    public void deleteNearbyFeed() {
        new Delete().from(FeedItem.class).where("isnearby=1").execute();
    }

    @Override
    public void loadNearbyFeed(SimpleFetchListener<List<FeedItem>> listener) {
        List<FeedItem> items = new Select().from(FeedItem.class)
                .where("isnearby=1").execute();
        fillItems(items);
        fillTopItems(items);
        deliverResult(listener, items);
    }

    @Override
    public void loadFavoritesFeed(SimpleFetchListener<List<FeedItem>> listener) {
        List<FeedItem> items = new Select().from(FeedItem.class)
                .where("iscollected=1").execute();
        fillItems(items);
        fillTopItems(items);
        deliverResult(listener, items);
    }

    @Override
    public void deleteFavoritesFeed(String feedId) {
        new Delete().from(FeedItem.class).where("isnearby=1'").and("feedId=?", feedId)
                .execute();
    }

    /**
     * 删除Feed相关的Like、评论、feed-topic、feed-friend等关系记录,评论、赞本书的数据也会被删除
     */
    class DeleteAllFeedItemCmd extends DbCommand {

        @Override
        protected void execute() {
            try {
                removeFeedRelativeItems();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            // 删除Feed本身的数据
            new Delete().from(FeedItem.class).where("isfriends=1").execute();
        }

        @SuppressWarnings("unchecked")
        private Class<? extends Model> refectModelClz(String className)
                throws ClassNotFoundException {
            return (Class<? extends Model>) Class.forName(className);
        }

        private void removeFeedRelativeItems() throws ClassNotFoundException {
            // 获取所有缓存的Feed
            List<FeedItem> cacheFeedItems = new Select().from(FeedItem.class)
                    .where("isfriends=1").execute();

            Class<? extends Model> feedLikeClass = refectModelClz("com.umeng.comm.core.beans.relation.FeedLike");
            Class<? extends Model> feedCommentClass = refectModelClz("com.umeng.comm.core.beans.relation.FeedComment");
            Class<? extends Model> feedTopicClass = refectModelClz("com.umeng.comm.core.beans.relation.FeedTopic");
            Class<? extends Model> feedCreatorClass = refectModelClz("com.umeng.comm.core.beans.relation.FeedCreator");
            Class<? extends Model> feedFriendClass = refectModelClz("com.umeng.comm.core.beans.relation.FeedFriends");

            for (FeedItem feedItem : cacheFeedItems) {
                // 移除like相关
                removeRelativeLike(feedItem.id, feedLikeClass);
                // 移除Feed相关的Comment
                removeRelativeComment(feedItem.id, feedCommentClass);
                new Delete().from(feedTopicClass).where("feed_id=?", feedItem.id)
                        .execute();
                new Delete().from(feedCreatorClass).where("feed_id=?", feedItem.id)
                        .execute();
                new Delete().from(feedFriendClass).where("feed_id=?", feedItem.id)
                        .execute();
            }
        }

        private void removeRelativeLike(String feedId, Class<? extends Model> feedLikeClass)
                throws ClassNotFoundException {
            List<Like> likes = new Select().from(Like.class).innerJoin(feedLikeClass)
                    .on("like._id=feed_like.like_id").where("feed_like.feed_id=?", feedId)
                    .execute();
            // 删除feed_like表中的记录

            new Delete().from(feedLikeClass).where("feed_id=?", feedId);
            Class<? extends Model> likeCreatorClz = feedLikeClass = refectModelClz("com.umeng.comm.core.beans.relation.LikeCreator");
            for (Like like : likes) {
                new Delete().from(likeCreatorClz).where("like_id=?", like.id).execute();
                new Delete().from(Like.class).where("_id=?", like.id).execute();
            }
        }

        private void removeRelativeComment(String feedId,
                Class<? extends Model> feedCommentClass)
                throws ClassNotFoundException {
            List<Comment> comments = new Select().from(Comment.class)
                    .innerJoin(feedCommentClass)
                    .on("comment._id=feed_comment.comment_id")
                    .where("feed_comment.feed_id=?", feedId)
                    .execute();
            for (Comment comment : comments) {
                new Delete().from(feedCommentClass).where("comment_id=?", comment.id).execute();
                new Delete().from(Comment.class).where("_id=?", comment.id).execute();
            }
        }
    }
}
