import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ColorHist {

	// changed the dim from 64 to 16
	// at 64 each image consumes 2MB of ram for 12KB image
	// thats absurd
	// even at 16 set your VM to -Xmx1024m just in case
	private static final int dim = 16;
	private static final String colorHistCacheFile = ".." + File.separator + "ImageData" + File.separator + "train" + File.separator + "colorhist" + dim + ".txt";
	
	public static void preprocess(Map<String, ImageData> images) throws IOException {
	    File cacheFile = new File(colorHistCacheFile);
	    if (cacheFile.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(colorHistCacheFile))) {
                String line = br.readLine();

                while (line != null) {
                    double[] bins = new double[dim * dim * dim];
                    String key = line.substring(0, line.indexOf(" "));
                    String values = line.substring(line.indexOf("[") + 1, line.indexOf("]"));
                    String binS[] = values.split(",\\s");
                    for (int i = 0; i < binS.length; i++) {
                        bins[i] = Double.parseDouble(binS[i]);
                    }
                    images.get(key).setColorHistogram(bins);
                    line = br.readLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
	    } else {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(colorHistCacheFile))) {
                for (ImageData id: images.values()) {
                    double[] colorHistogram = getHist(id);
                    id.setColorHistogram(colorHistogram);
                    
                    String line = String.format("%s %s%n", id.getFilename(), Arrays.toString(colorHistogram));
                    bw.write(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
	    }
	}
	
	public static void computeSimilarity(List<ImageData> images,
	        ImageData queryImage) throws IOException {
		double[] hist = getHist(queryImage);

		for (int count = 0; count < images.size(); count++) {
			double[] h = images.get(count).getColorHistogram();
			double sim = computeSimilarity(hist, h);
			images.get(count).setColorSimilarity(sim);
		}
	}

	private static double[] getHist(ImageData queryImage) throws IOException {
	    BufferedImage image = queryImage.getImage();
		int imHeight = image.getHeight();
		int imWidth = image.getWidth();
		double[] bins = new double[dim * dim * dim];
		int step = 256 / dim;
		Raster raster = image.getRaster();
		for (int i = 0; i < imWidth; i++) {
			for (int j = 0; j < imHeight; j++) {
				// rgb->ycrcb
				int r = raster.getSample(i, j, 0);
				int g = raster.getSample(i, j, 1);
				int b = raster.getSample(i, j, 2);

				// Changed Codes.
				int y = (int) (0 + 0.299 * r + 0.587 * g + 0.114 * b);
				int cb = (int) (128 - 0.16874 * r - 0.33126 * g + 0.50000 * b);
				int cr = (int) (128 + 0.50000 * r - 0.41869 * g - 0.08131 * b);

				int ybin = y / step;
				int cbbin = cb / step;
				int crbin = cr / step;

				// Changed Codes.
				bins[ybin * dim * dim + cbbin * dim + crbin]++;
			}
		}

		// Changed Codes.
		for (int i = 0; i < dim * dim * dim; i++) {
			bins[i] = bins[i] / (imHeight * imWidth);
		}
		return bins;
	}

	private static double computeSimilarity(double[] hist1, double[] hist2) {
		double distance = calculateDistance(hist1, hist2);
		return 1 - distance;
	}

	private static double calculateDistance(double[] array1, double[] array2) {
		// Euclidean distance
		/*
		 * double Sum = 0.0; for(int i = 0; i < array1.length; i++) { Sum = Sum
		 * + Math.pow((array1[i]-array2[i]),2.0); } return Math.sqrt(Sum);
		 */

		// Bhattacharyya distance
		double h1 = 0.0;
		double h2 = 0.0;
		int N = array1.length;
		for (int i = 0; i < N; i++) {
			h1 = h1 + array1[i];
			h2 = h2 + array2[i];
		}

		double Sum = 0.0;
		for (int i = 0; i < N; i++) {
			Sum = Sum + Math.sqrt(array1[i] * array2[i]);
		}
		double dist = Math.sqrt(1 - Sum / Math.sqrt(h1 * h2));
		return dist;
	}
}
