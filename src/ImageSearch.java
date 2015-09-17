
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
    private static final int resultSize = 9;    // size of the searching result
    private static final String datasetpath = "..\\ImageData\\train\\data"; // the path of image dataset
    private static final String catagoryTextPath = "..\\ImageData\\category_names.txt"; // category text file
    private static final String imageTagsPath = "..\\ImageData\\train\\train_tags.txt"; // for text tags
    
    List<ImageData> images;
    
    ColorHist colorHist = new ColorHist();

    public ImageSearch() {
        images = new ArrayList<ImageData>();
        loadTrainingData();
    }
    
    public int getResultSize() {
        return resultSize;
    }
    
    // we'll probably want to change this to accept multiple combinations
    public List<ImageData> search(SearchType type, BufferedImage bi) throws IOException {
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
    // we'll probably want to write a custom ranking function for multiple search type combinations
    private List<ImageData> rankColorHistogram() {
        List<ImageData> results = new ArrayList<ImageData>(images);
        Collections.sort(results, new colorHistogramComparator());
        return results;
    }
    
    class colorHistogramComparator implements Comparator<ImageData> {
        public int compare(ImageData a, ImageData b) {
            return a.getColorSimilarity() > b.getColorSimilarity() ? -1 : a.getColorSimilarity() == b.getColorSimilarity() ? 0 : 1;
        }
    }
    
    private void calculateSimilarities(BufferedImage bi) throws IOException {
        colorHist.computeSimilarity(images, bi);
        // add sift, feature, text similarity calculators here later
    }
    
    // reads all the image files and create imageData object to hold information about it
    // for now just preprocess color histogram, and also add the image category and tags
    private void loadTrainingData() {
        Map<String, List<String>> tags = getTags();
        List<String> categories = getCategories();
        try {
            for(String category: categories) {
                String path = datasetpath+"\\"+category;
                File dir = new File(path);
                File[] files = dir.listFiles();
                for (int count=0; count < files.length; count++) {
                    BufferedImage img = ImageIO.read(files[count]);
                    String filename = files[count].getName();
                    ImageData id = new ImageData(filename,  files[count].getPath(), category, tags.get(filename));
                    images.add(id);
                    
                    double[] colorHistogram = colorHist.getHist(img);
                    id.setColorHistogram(colorHistogram);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
    
    private List<String> getCategories() {
        List<String> filePaths = new ArrayList<String>();
        
        try(BufferedReader br = new BufferedReader(new FileReader(catagoryTextPath))) {
            String line = br.readLine();

            while (line != null) {
                filePaths.add(line);
                line = br.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return filePaths;
    }
    
    private Map<String, List<String>> getTags() {
        Map<String, List<String>> tags = new HashMap<String, List<String>>();
        
        try(BufferedReader br = new BufferedReader(new FileReader(imageTagsPath))) {
            String line = br.readLine();

            while (line != null) {
                ArrayList<String> tokens = new ArrayList<String>();
                tokens.addAll(Arrays.asList(line.split("\\s+")));
                String key = tokens.remove(0);
                tags.put(key,  tokens);
                line = br.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tags;
    }
}
