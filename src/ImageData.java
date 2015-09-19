import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

public class ImageData {

	private String filename;
	private String filepath;
	private List<String> categories;
	private List<String> tags;

	private double colorSimilarity;

	private double[] colorHistogram;

	public ImageData(String filename, String filepath, List<String> tags) {
		this.filename = filename;
		this.categories = new ArrayList<String>();
		this.filepath = filepath;
		this.tags = tags;
	}

	public String filename() {
		return filename;
	}

	public List<String> getCategories() {
		return categories;
	}

	public BufferedImage getImage() throws IOException {
		File file = new File(filepath);
		BufferedImage img = ImageIO.read(file);
		return img;
	}

	public List<String> getTags() {
		return tags;
	}

	public double[] getColorHistogram() {
		return colorHistogram;
	}

	public double getColorSimilarity() {
		return colorSimilarity;
	}

	public void setCategories(List<String> categories) {
		this.categories = categories;
	}

	public void setColorHistogram(double[] colorHistogram) {
		this.colorHistogram = colorHistogram;
	}

	public void setColorSimilarity(double colorSimilarity) {
		this.colorSimilarity = colorSimilarity;
	}

	@Override
	public String toString() {
		return String.format("Filename:\n\t%s\nCategories:\n\t%s\nTags:\n\t%s\n", filename,
				Arrays.toString(categories.toArray()),
				tags != null ? Arrays.toString(tags.toArray()) : "[]");
	}
}
