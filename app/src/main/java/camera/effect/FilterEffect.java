package camera.effect;


import camera.util.GPUImageFilterTools;

public class FilterEffect {

    private String title;
    private GPUImageFilterTools.FilterType type;
    private int degree;

    /**
     * @param title
     */
    public FilterEffect(String title, GPUImageFilterTools.FilterType type, int degree) {
        this.type = type;
        this.degree = degree;
        this.title = title;
    }


    public GPUImageFilterTools.FilterType getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public int getDegree() {
        return degree;
    }

}
