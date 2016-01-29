package com.chat.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.widget.TextView;

import com.chat.animate.AnimatedGifDrawable;
import com.chat.animate.AnimatedImageSpan;
import com.chat.util.LruCaCheFactory;
import com.uy.util.ScreenUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by nice on 15/11/30.
 */
public class EmojiGifTextview extends TextView {
    private static final int emojiSize = 30;

    public EmojiGifTextview(Context context) {
        super(context);
    }

    public EmojiGifTextview(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        SpannableStringBuilder sb = new SpannableStringBuilder(text);
        try {
            sb = addEmojis(this, text);

        } catch (Exception e) {
            e.printStackTrace();
        }
        super.setText(sb, type);
    }

    private SpannableStringBuilder addEmojis(final TextView gifTextView, CharSequence text) {
        SpannableStringBuilder sb = new SpannableStringBuilder(text);
        String regex = "(\\#\\[face/png/f_static_)\\d{3}(.png\\]\\#)";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(text);
        while (m.find()) {
            String tempText = m.group();
            try {
                String num = tempText.substring(
                        "#[face/png/f_static_".length(), tempText.length()
                                - ".png]#".length());
                String gif = "face/gif/f" + num + ".gif";
                /**
                 * 如果open这里不抛异常说明存在gif，则显示对应的gif 否则说明gif找不到，则显示png
                 * */
                InputStream is = getContext().getAssets().open(gif);
                AnimatedImageSpan drawableSpan = new AnimatedImageSpan(
                        new AnimatedGifDrawable(getContext(), num, is,
                                new AnimatedGifDrawable.UpdateListener() {
                                    @Override
                                    public void update() {
                                        gifTextView.postInvalidate();
                                    }
                                }));

                sb.setSpan(drawableSpan, m.start(), m.end(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                is.close();
            } catch (Exception e) {
                String png = tempText.substring("#[".length(),
                        tempText.length() - "]#".length());
                try {
                    Bitmap bitmap;
                    if (LruCaCheFactory.getEmojiCache().get(png) != null) {
                        bitmap = LruCaCheFactory.getEmojiCache().get(png);
                    } else {
                        bitmap = BitmapFactory.decodeStream(getContext()
                                .getAssets().open(png));
                        LruCaCheFactory.getEmojiCache().put(png, bitmap);
                    }
                    BitmapDrawable bd = new BitmapDrawable(bitmap);
                    Drawable drawable = bd;
                    int val = ScreenUtils.dp2px(emojiSize, getContext());
                    drawable.setBounds(0, 0, val, val);
                    sb.setSpan(new ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM),
                            m.start(), m.end(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }

        return sb;
    }
}
