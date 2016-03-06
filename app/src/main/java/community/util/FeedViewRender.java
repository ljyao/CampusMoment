
package community.util;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.widget.TextView;

import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.beans.FeedItem;
import com.umeng.comm.core.beans.Topic;
import com.umeng.comm.core.utils.CommonUtils;
import com.umeng.comm.ui.utils.UrlMatcher;
import com.umeng.comm.ui.utils.textspan.UrlClickSpan;
import com.umeng.comm.ui.utils.textspan.UserClickSpan;
import com.umeng.comm.ui.widgets.TextViewFixTouchConsume;

import java.util.LinkedList;
import java.util.List;

/**
 * 包装显示Feed的View
 */
public final class FeedViewRender {

    /**
     * 渲染话题跟好友</br>
     *
     * @param activity
     * @param contentTextView
     * @param item
     */
    public static void parseTopicsAndFriends(
            final TextView contentTextView, FeedItem item) {
        contentTextView.setClickable(true);
        contentTextView.setMovementMethod(LinkMovementMethod.getInstance());
        // 消息文本内容
        contentTextView.setText(item.text);
        // 文本内容
        final String content = item.text;
        final Context context = contentTextView.getContext();
        //
        SpannableStringBuilder contentSsb = new SpannableStringBuilder(content);
        // 添加话题
        renderTopics(context, item, contentSsb);
        // 渲染好友
        renderFriends(context, item, contentSsb);
        // 渲染url链接
        renderUrls(context, item.text, contentSsb);
        // 多一个空格
        contentSsb.append(" ");
        // 将文本设置到TextView上
        contentTextView.setText(contentSsb);
    }

    private static void renderUrls(Context context, String feedText,
                                   SpannableStringBuilder contentSsb) {
        List<String> urlList = UrlMatcher.recognizeUrls(feedText);
        if (CommonUtils.isListEmpty(urlList)) {
            return;
        }

        for (final String url : urlList) {
            List<DecorationItem> items = findTagsInText(feedText, url);
            for (DecorationItem decoratorItem : items) {
                makeStringClickable(contentSsb, decoratorItem.start, decoratorItem.text,
                        new UrlClickSpan(context, url));
            }
        }

    }

    /**
     * 渲染好友文本</br>
     *
     * @param activity
     * @param friends
     * @param contentSsb
     * @param content
     */
    private static void renderFriends(Context context, FeedItem feedItem,
                                      SpannableStringBuilder contentSsb) {
        String name = null;
        // int start = 0;
        for (CommUser friend : feedItem.atFriends) {
            name = "@" + friend.name;

            List<DecorationItem> items = findTagsInText(feedItem.text, name);
            for (DecorationItem decoratorItem : items) {
                makeStringClickable(contentSsb, decoratorItem.start, decoratorItem.text,
                        new UserClickSpan(
                                context, friend));
            }
        }
    }

    private static List<DecorationItem> findTagsInText(String fullString, String tag) {
        // String str = "helloslkhellodjladfjhello";
        // String findStr = "hello";
        int lastIndex = 0;
        List<DecorationItem> decoratorItems = new LinkedList<DecorationItem>();
        while (lastIndex != -1) {
            lastIndex = fullString.indexOf(tag, lastIndex);
            if (lastIndex != -1) {
                decoratorItems.add(new DecorationItem(lastIndex, tag));
                lastIndex += tag.length();
            }
        }

        return decoratorItems;
    }

    /**
     * 渲染评论中</br>
     *
     * @param activity
     * @param textView
     * @param users
     */
    public static void renderFriendText(Context context, TextView textView, List<CommUser> users) {
        textView.setClickable(true);
        textView.setMovementMethod(TextViewFixTouchConsume.LocalLinkMovementMethod.getInstance());
        String content = textView.getText().toString();
        SpannableStringBuilder contentSsb = new SpannableStringBuilder(content);
        String name = "";
        // int start = -1;
        for (CommUser user : users) {
            name = user.name;

            List<DecorationItem> items = findTagsInText(content, name);
            for (DecorationItem decorationItem : items) {
                makeStringClickable(contentSsb, decorationItem.start, decorationItem.text,
                        new UserClickSpan(context, user));
            }
        }

        renderUrls(context, textView.getText().toString(), contentSsb);
        textView.setText(contentSsb);
    }

    /**
     * 渲染话题文本
     */
    private static void renderTopics(final Context context, FeedItem feedItem, SpannableStringBuilder contentSsb) {
        for (final Topic topic : feedItem.topics) {
            String name = topic.name;
            if (TextUtils.isEmpty(name)) {
                continue;
            }

            List<DecorationItem> items = findTagsInText(feedItem.text, name);
            for (DecorationItem decoratorItem : items) {
                makeStringClickable(contentSsb, decoratorItem.start, decoratorItem.text, new TopicClickSpan(context, topic));
            }

        }
    }

    /**
     * @param tv
     * @param start
     * @param text
     * @param clickableSpan
     */
    private static void makeStringClickable(SpannableStringBuilder contentSsb,
                                            int start, final String text, ClickableSpan clickableSpan) {
        contentSsb.setSpan(clickableSpan, start, start + text.length(), 0);
    }

    static class DecorationItem {
        int start;
        String text;

        public DecorationItem(int start, String text) {
            this.start = start;
            this.text = text;
        }
    }

}
