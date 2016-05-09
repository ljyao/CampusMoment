package editimage.model;


public class RatioItem {
	private String text;
	private ImageScaleType scaleType;
	private int index;

	public RatioItem(ImageScaleType scaleType) {
		super();
		text = scaleType.scaleName;
		this.scaleType = scaleType;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public ImageScaleType getScaleType() {
		return scaleType;
	}

	public void setScaleType(ImageScaleType scaleType) {
		this.scaleType = scaleType;
	}
}// end class
