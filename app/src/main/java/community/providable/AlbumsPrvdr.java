package community.providable;

import android.text.TextUtils;

import com.umeng.comm.core.CommunitySDK;
import com.umeng.comm.core.beans.AlbumItem;
import com.umeng.comm.core.beans.ImageItem;
import com.umeng.comm.core.constants.ErrorCode;
import com.umeng.comm.core.listeners.Listeners;
import com.umeng.comm.core.nets.responses.AlbumResponse;
import com.umeng.comm.core.nets.uitls.NetworkUtils;
import com.uy.App;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ljy on 16/1/4.
 */
public class AlbumsPrvdr {
    private String mNextPageUrl;
    private CommunitySDK mCommunitySDK;
    private String userid;

    public AlbumsPrvdr(String id) {
        userid = id;
        mCommunitySDK = App.getCommunitySDK();
    }

    public void getFirstPageData(final NetLoaderListener<List<ImageItem>> listener) {
        mCommunitySDK.fetchAlbums(userid, new Listeners.FetchListener<AlbumResponse>() {
            @Override
            public void onStart() {

            }

            @Override
            public void onComplete(AlbumResponse albumResponse) {
                mNextPageUrl = albumResponse.nextPageUrl;
                listener.onComplete(true, parseImageItem(albumResponse));
            }
        });
    }

    public void getNextPageData(final NetLoaderListener<List<ImageItem>> listener) {
        if (TextUtils.isEmpty(mNextPageUrl)) {
            return;
        }
        mCommunitySDK.fetchNextPageData(mNextPageUrl, AlbumResponse.class,
                new Listeners.SimpleFetchListener<AlbumResponse>() {

                    @Override
                    public void onComplete(AlbumResponse response) {
                        if (NetworkUtils.handleResponseAll(response)) {
                            if (response.errCode == ErrorCode.NO_ERROR) {
                                mNextPageUrl = "";
                            }
                            return;
                        }
                        mNextPageUrl = response.nextPageUrl;
                        if (!TextUtils.isEmpty(mNextPageUrl) && "null".equals(mNextPageUrl)) {
                            mNextPageUrl = "";
                        }
                        listener.onComplete(true, parseImageItem(response));
                    }
                });

    }

    private List<ImageItem> parseImageItem(AlbumResponse response) {
        List<ImageItem> newItems = new ArrayList<>();
        for (AlbumItem albumItem : response.result) {
            newItems.addAll(albumItem.images);
        }
        return newItems;
    }
}
