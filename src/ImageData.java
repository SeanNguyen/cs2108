import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.imageio.ImageIO;

public class ImageData {

	private String filename;
	private String filepath;
	private Set<String> categories;
	private Set<String> tags;

	private double colorSimilarity;
	private double textSimilarity;

	private double[] colorHistogram;

	public ImageData(String filename, String filepath, Set<String> tags) {
		this.filename = filename;
		this.categories = new HashSet<String>();
		this.filepath = filepath;
		this.tags = tags != null ? tags : new HashSet<String>();
		this.textSimilarity = 0.0;
	}

	public String getFilename() {
		return filename;
	}

	public Set<String> getCategories() {
		return categories;
	}

	public BufferedImage getImage() throws IOException {
		File file = new File(filepath);
		BufferedImage img = ImageIO.read(file);
		return img;
	}

	public Set<String> getTags() {
		return tags;
	}

	public double[] getColorHistogram() {
		return colorHistogram;
	}

	public double getColorSimilarity() {
		return colorSimilarity;
	}
	
	public double getTextSimilarity() {
	    return textSimilarity;
	}

	public void setCategories(Set<String> categories) {
		this.categories = categories;
	}

	public void setColorHistogram(double[] colorHistogram) {
		this.colorHistogram = colorHistogram;
	}

	public void setColorSimilarity(double colorSimilarity) {
		this.colorSimilarity = colorSimilarity;
	}
	
	public void setTextSimilarity(double textSimilarity) {
	    this.textSimilarity = textSimilarity;
	}

	@Override
	public String toString() {
		return String.format("Filename:\n\t%s\nCategories:\n\t%s\nTags:\n\t%s\nSimilarities:\n\tColor: %s\t Text: %s\n", filename,
				Arrays.toString(categories.toArray()),
				Arrays.toString(tags.toArray()),
				colorSimilarity, textSimilarity);
	}
}
