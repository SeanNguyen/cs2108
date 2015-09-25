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
	private double siftSimilarity;
	private double visualConceptSimilarity;

	private double[] colorHistogram;
	private double[] siftHistogram;
	private double[] visualConceptScores;

	public ImageData(String filename, String filepath, Set<String> tags) {
		this.filename = filename;
		this.categories = new HashSet<String>();
		this.filepath = filepath;
		this.tags = tags != null ? tags : new HashSet<String>();
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

	public String getFilePath() {
		return filepath;
	}

	public Set<String> getTags() {
		return tags;
	}

	public double[] getColorHistogram() {
		return colorHistogram;
	}

	public double[] getSiftHistogram() {
		return siftHistogram;
	}

	public double[] getVisualConceptScores() {
		return visualConceptScores;
	}
	
	public double getColorSimilarity() {
		return colorSimilarity;
	}

	public double getTextSimilarity() {
		return textSimilarity;
	}

	public double getSiftSimilarity() {
		return siftSimilarity;
	}
	
	public double getVisualConceptSimilarity() {
		return visualConceptSimilarity;
	}
	
	public void setCategories(Set<String> categories) {
		if (categories != null) {
			this.categories = categories;
		}
	}

	public void setColorHistogram(double[] colorHistogram) {
		this.colorHistogram = colorHistogram;
	}

	public void setSiftHistogram(double[] siftHistogram) {
		this.siftHistogram = siftHistogram;
	}

	public void setVisualConceptScores(double[] visualConceptScores) {
		this.visualConceptScores = visualConceptScores;
	}
	
	public void setColorSimilarity(double colorSimilarity) {
		this.colorSimilarity = colorSimilarity;
	}

	public void setTextSimilarity(double textSimilarity) {
		this.textSimilarity = textSimilarity;
	}

	public void setSiftSimilarity(double siftSimilarity) {
		this.siftSimilarity = siftSimilarity;
	}

	public void setVisualConceptSimilarity(double visualConceptSimilarity) {
		this.visualConceptSimilarity = visualConceptSimilarity;
	}
	
	@Override
	public String toString() {
		return String.format("Filename:\n\t%s\nCategories:\n\t%s\nTags:\n\t%s\nSimilarities:\n\tColor: %s\t Text: %s\n",
				filename, Arrays.toString(categories.toArray()), Arrays.toString(tags.toArray()), colorSimilarity,
				textSimilarity);
	}
}
