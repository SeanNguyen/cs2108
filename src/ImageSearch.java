import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

// seperated the image search logic from the ui
public class ImageSearch {

	// using relative path assuming default assignment folder structure
	private static final int resultSize = 20; // size of the searching result
	private static final String datasetpath = ".." + File.separator + "ImageData" + File.separator + "train" + File.separator + "data"; // training image set													// dataset
	private static final String groundTruthsPath = ".." + File.separator + "Groundtruth" + File.separator + "train"; // groundtruths																			// file
	private static final String imageListPath = ".." + File.separator + "ImageList" + File.separator + "train" + File.separator  +"TrainImagelist.txt"; // image list																					// file
	private static final String imageTagsPath = ".." + File.separator + "ImageData" + File.separator + "train" + File.separator + "train_tags.txt"; // image tags																			// tags

	Map<String, ImageData> images;

	TextFeature textFeature = new TextFeature();

	public ImageSearch() {
		images = new HashMap<String, ImageData>();
		loadTrainingData();
	}

	public int getResultSize() {
		return resultSize;
	}

	public List<ImageData> search(List<SearchType> searchTypes, ImageData queryImage)
			throws IOException {
		calculateSimilarities(searchTypes, queryImage);
		List<ImageData> results = rankResults(searchTypes);
		return results;
	}
	
	// handle calculation of similarity values 
    private void calculateSimilarities(List<SearchType> searchTypes, ImageData queryImage) throws IOException {
        for(SearchType searchType: searchTypes) {
            switch (searchType) {
                case COLORHIST:
                    ColorHist.computeSimilarity(new ArrayList<ImageData>(images.values()), queryImage);
                    break;
    
                case SIFT:
                    Sift.computeSimilarity(new ArrayList<ImageData>(images.values()), queryImage);
                    break;
    
                case FEATURE:
                    break;
    
                case TEXT:
                    TextFeature.computeSimilarity(images, queryImage.getTags());
                    break;
    
                default:
                    break;
            }
            // add sift, feature, text similarity calculators here later
        }
    }

	private List<ImageData> rankResults(List<SearchType> searchTypes) {
		List<ImageData> results = new ArrayList<ImageData>(images.values());
		Collections.sort(results, new imageRankComparator(searchTypes));
		return results;
	}

	// compare images for sorting using a simple addition of different
	// similarity weights each for now
	class imageRankComparator implements Comparator<ImageData> {
	    
	    List<SearchType> searchTypes;
	    
	    public imageRankComparator(List<SearchType> searchTypes) {
	        this.searchTypes = searchTypes;
	    }
	    
		public int compare(ImageData a, ImageData b) {
		    
		    double simA = 0.0;
		    double simB = 0.0;
		    
	        for(SearchType searchType: searchTypes) {
	            switch (searchType) {
	                case COLORHIST:
	                    simA += a.getColorSimilarity();
	                    simB += b.getColorSimilarity();
	                    break;
	    
	                case SIFT:
                        simA += a.getSiftSimilarity();
                        simB += b.getSiftSimilarity();
	                    break;
	    
	                case FEATURE:
	                    break;
	    
	                case TEXT:
                        simA += a.getTextSimilarity() * 2;
                        simB += b.getTextSimilarity() * 2;
	                    break;
	    
	                default:
	                    break;
	            }
	        }
			return simA > simB ? -1 : simA == simB ? 0 : 1;
		}
	}

	// loading of image data and calculation of color histogram etc
	private void loadTrainingData() {
	    try {
    		Map<String, Set<String>> tags = Commons.getTags(imageTagsPath);
    		Map<String, Set<String>> categories = Commons.getCategories(imageListPath, groundTruthsPath);
    
    		loadImageData(tags, categories);
            ColorHist.preprocess(images);
            Sift.preprocess(images);
        } catch (IOException e) {
            e.printStackTrace();
        }
	}

	private void loadImageData(Map<String, Set<String>> tags,
			Map<String, Set<String>> categories) {
		try {
			for (File folder : new File(datasetpath).listFiles()) {
				File dir = new File(folder.getPath());
				File[] files = dir.listFiles();
				for (int count = 0; count < files.length; count++) {
				    if (!Utils.getExtension(files[count]).equals("sift")) {
	                    String filename = files[count].getName();
	                    ImageData id = new ImageData(filename,
	                            files[count].getPath(), tags.get(filename));
	                    id.setCategories(categories.get(filename));
	                    images.put(filename, id);
				    }
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
