import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class QueryProcessor {
    
    private static final String querydatasetpath = ".." + File.separator + "ImageData" + File.separator + "test" + File.separator + "data"; // query image set                                                   // dataset
    private static final String queryGroundTruthsPath = ".." + File.separator + "Groundtruth" + File.separator + "test"; // query groundtruths                                                                            // file
    private static final String queryImageListPath = ".." + File.separator + "ImageList" + File.separator + "test" + File.separator + "TestImagelist.txt"; // query image list                                                                                   // file
    private static final String queryImageTagsPath = ".." + File.separator + "ImageData" + File.separator + "test" + File.separator + "test_tags.txt"; // query image tags                                                                           // tags

    Map<String, ImageData> queryImages;
    ImageSearch is;

    public QueryProcessor(ImageSearch is) {
        queryImages = new HashMap<String, ImageData>();
        this.is = is;
        loadQueryData();
    }
    
    public List<ImageData> processQuery(List<SearchType> searchTypes, File queryFile) throws IOException {
        ImageData id = getQueryImage(queryFile);
        List<ImageData> results = is.search(searchTypes, id);
        printF1(results, id);
        return results;
    }
    
    private ImageData getQueryImage(File queryFile) {
        String filename = queryFile.getName();
        if(queryImages.containsKey(filename)) {
            return queryImages.get(filename);
        } else {
            return new ImageData(filename, queryFile.getPath(), null);
        }
    }
    
    private void loadQueryData() {
        Map<String, Set<String>> tags = Commons.getTags(queryImageTagsPath);
        Map<String, Set<String>> categories = Commons.getCategories(queryImageListPath, queryGroundTruthsPath);

        loadQueryImageData(tags, categories);
    }

    private void loadQueryImageData(Map<String, Set<String>> tags,
        Map<String, Set<String>> categories) {
        for (File folder : new File(querydatasetpath).listFiles()) {
            File dir = new File(folder.getPath());
            File[] files = dir.listFiles();
            for (int count = 0; count < files.length; count++) {
                String filename = files[count].getName();
                ImageData id = new ImageData(filename,
                        files[count].getPath(), tags.get(filename));
                id.setCategories(categories.get(filename));
                queryImages.put(filename, id);
            }
        }
    }
    
    private void printF1(List<ImageData> results, ImageData id) {
        double truePositives = 0;
        Map<String, Integer> count = new HashMap<String, Integer>();
        for(int i = 0; i < is.getResultSize(); i ++) {
            Set<String> categories = results.get(i).getCategories();
            Set<String> intersection = new HashSet<String>(categories);
            intersection.retainAll(id.getCategories());
            if (intersection.size() > 0) {
                truePositives ++;
            }
            for(String category: categories) {
                if(!count.containsKey(category)) {
                    count.put(category, 0);
                }
                count.put(category, count.get(category) + 1);
            }
        }
        
        System.out.println("Input image has categories: " + id.getCategories());
        System.out.println("Input image has tags: " + id.getTags());
        System.out.println("Query results come from the following categories:");
        for(String category: count.keySet()){
            System.out.printf("Category: %s, Count: %s\n", category, count.get(category));
        }
        
        double totalRelevant = 50;
        double totalSelected = is.getResultSize();
        double precision = truePositives / totalSelected;
        double recall = truePositives / totalRelevant;
        double f1 = 2 * ((precision * recall) / (precision + recall));
        System.out.printf("Precision: %s, Recall: %s, F1: %s\n", precision, recall, f1);
        
    }
}
