import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

// seperated the image search logic from the ui
public class ImageSearch {

	// using relative path assuming default assignment folder structure
	private static final int resultSize = 20; // size of the searching result
	private static final String datasetpath = "..\\ImageData\\train\\data"; // training image set													// dataset
	private static final String groundTruthsPath = "..\\Groundtruth\\train"; // groundtruths																			// file
	private static final String imageListPath = "..\\ImageList\\train\\TrainImagelist.txt"; // image list																					// file
	private static final String imageTagsPath = "..\\ImageData\\train\\train_tags.txt"; // image tags																			// tags

	Map<String, ImageData> images;

	ColorHist colorHist = new ColorHist();

	public ImageSearch() {
		images = new HashMap<String, ImageData>();
		loadTrainingData();
	}

	public int getResultSize() {
		return resultSize;
	}

	// we'll probably want to change this to accept multiple combinations
	public List<ImageData> search(SearchType type, BufferedImage bi)
			throws IOException {
		calculateSimilarities(bi);

		switch (type) {
		case COLORHIST:
			return rankColorHistogram();

		case SIFT:
			break;

		case FEATURE:
			break;

		case TEXT:
			break;

		default:
			break;
		}
		return null;
	}

	// for ranking results of color histogram similarity results
	// we'll probably want to write a custom ranking function for multiple
	// search type combinations
	private List<ImageData> rankColorHistogram() {
		List<ImageData> results = new ArrayList<ImageData>(images.values());
		Collections.sort(results, new colorHistogramComparator());
		return results;
	}

	class colorHistogramComparator implements Comparator<ImageData> {
		public int compare(ImageData a, ImageData b) {
			return a.getColorSimilarity() > b.getColorSimilarity() ? -1 : a
					.getColorSimilarity() == b.getColorSimilarity() ? 0 : 1;
		}
	}

	private void calculateSimilarities(BufferedImage bi) throws IOException {
		colorHist.computeSimilarity(new ArrayList<ImageData>(images.values()), bi);
		// add sift, feature, text similarity calculators here later
	}

	// reads all the image files and create imageData object to hold information
	// about it
	// for now just preprocess color histogram, and also add the image category
	// and tags
	private void loadTrainingData() {
		Map<String, List<String>> tags = getTags();
		Map<String, List<String>> categories = getCategories();

		loadImageData(tags, categories);
	}

	private void loadImageData(Map<String, List<String>> tags,
			Map<String, List<String>> categories) {
		try {
			for (File folder : new File(datasetpath).listFiles()) {
				File dir = new File(folder.getPath());
				File[] files = dir.listFiles();
				for (int count = 0; count < files.length; count++) {
					BufferedImage img = ImageIO.read(files[count]);
					String filename = files[count].getName();
					ImageData id = new ImageData(filename,
							files[count].getPath(), tags.get(filename));
					id.setCategories(categories.get(filename));
					images.put(filename, id);
					
					double[] colorHistogram = colorHist.getHist(img);
					id.setColorHistogram(colorHistogram);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Map<String, List<String>> getCategories() {
		Map<String, List<String>> imageCategories = new HashMap<String, List<String>>();
		List<String> imageList = new ArrayList<String>();

		// get keys
		try (BufferedReader br = new BufferedReader(new FileReader(
				imageListPath))) {
			String line = br.readLine();

			while (line != null) {
				imageList.add(line);
				imageCategories.put(line, new ArrayList<String>());
				line = br.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		// populate tables with categories from ground truths

		for (File groundTruthFile : new File(groundTruthsPath).listFiles()) {
			try (BufferedReader br = new BufferedReader(new FileReader(
					groundTruthFile))) {
				String line = br.readLine();

				String filename = groundTruthFile.getName();
				String category = filename.substring(filename.indexOf("_") + 1,
						filename.indexOf("."));
				int c = 0;
				while (line != null) {
					if (line.equals("1")) {
						imageCategories.get(imageList.get(c)).add(category);
					}
					line = br.readLine();
					c++;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return imageCategories;
	}

	private Map<String, List<String>> getTags() {
		Map<String, List<String>> tags = new HashMap<String, List<String>>();

		try (BufferedReader br = new BufferedReader(new FileReader(
				imageTagsPath))) {
			String line = br.readLine();

			while (line != null) {
				ArrayList<String> tokens = new ArrayList<String>();
				tokens.addAll(Arrays.asList(line.split("\\s+")));
				String key = tokens.remove(0);
				tags.put(key, tokens);
				line = br.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return tags;
	}
}
