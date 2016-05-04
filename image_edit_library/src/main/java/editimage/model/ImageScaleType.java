package editimage.model;

/**
 * Created by Shine on 2016/5/1.
 */
public enum ImageScaleType {
    wider("4:3"),
    higher("3:4"),
    square("1:1"),
    Wrap("1:1");
    public float scale;

    ImageScaleType(String scaleStr) {
        switch (scaleStr) {
            case "4:3":
                scale = 4f / 3f;
                break;
            case "3:4":
                scale = 3f / 4f;
                break;
            case "1:1":
                scale = 1;
                break;
        }
    }
}
