package editimage.fliter;

/**
 * Created by shine on 16-2-19.
 */
public enum FliterType {

    Original("原色"),
    Instafix("轻柔"),
    Ansel("月色"),
    Testino("经典"),
    XPro("绚丽"),
    Retro("复古"),
    BlackWhite("黑白"),
    Sepia("胶片"),
    Cyano("回忆"),
    Georgia("优格"),
    Sahara("流年"),
    HDR("光绚");
    public static final FliterType[] FILTER_TYPES = {Original,
            Instafix,
            Ansel,
            Testino,
            XPro,
            Retro,
            BlackWhite,
            Sepia,
            Cyano,
            Georgia,
            Sahara,
            HDR};
    public String name;

    FliterType(String s) {
        name = s;
    }

}
