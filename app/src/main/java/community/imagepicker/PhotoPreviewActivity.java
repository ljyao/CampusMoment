
package community.imagepicker;

import android.os.Bundle;

import java.util.List;

import community.imagepicker.domain.PhotoSelectorDomain;
import community.imagepicker.model.PhotoConstants;
import community.imagepicker.model.PhotoModel;
import community.imagepicker.util.ImagePickerUtils;


/**
 * 照片预览Activity
 */
public class PhotoPreviewActivity extends BasePhotoPreviewActivity implements
        PhotoSelectorActivity.OnLocalReccentListener {

    private PhotoSelectorDomain photoSelectorDomain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        photoSelectorDomain = new PhotoSelectorDomain(getApplicationContext());

        init(getIntent().getExtras());
    }

    @SuppressWarnings("unchecked")
    protected void init(Bundle extras) {
        if (extras == null)
            return;

        if (extras.containsKey(PhotoConstants.PHOTO_PRVIEW_PHOTO)) { // 预览图片
            photos = (List<PhotoModel>) extras.getSerializable(PhotoConstants.PHOTO_PRVIEW_PHOTO);
            current = extras.getInt(PhotoConstants.PHOTO_POSITION, 0);
            updatePercent();
            bindData();
        } else if (extras.containsKey(PhotoConstants.PHOTO_ALBUM)) { // 点击图片查看
            String albumName = extras.getString(PhotoConstants.PHOTO_ALBUM); // 相册
            this.current = extras.getInt(PhotoConstants.PHOTO_POSITION);
            if (!ImagePickerUtils.isNull(albumName)
                    && albumName.equals(PhotoSelectorActivity.RECCENT_PHOTO)) {
                photoSelectorDomain.getReccent(this);
            } else {
                photoSelectorDomain.getAlbum(albumName, this);
            }
        }
    }

    @Override
    public void onPhotoLoaded(List<PhotoModel> photos) {
        this.photos = photos;
        updatePercent();
        bindData(); // 更新界面
    }

}
