import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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

    private static final String reportPath = ".." + File.separator;
    
    Map<String, ImageData> queryImages;
    ImageSearch is;

    public QueryProcessor(ImageSearch is) {
        queryImages = new HashMap<String, ImageData>();
        this.is = is;
        loadQueryData();
    }
    
    public List<ImageData> processQuery(List<SearchType> searchTypes, File queryFile, List<ImageData> feedback) throws IOException {
        ImageData id = getQueryImage(queryFile);
        List<ImageData> results = is.search(searchTypes, id, feedback);
        printF1(results, id);
        return results;
    }
    
    public void generateReport(List<SearchType> searchTypes) {
        double[] overallMetrics = new double[3];
        int queryCount = 0;
        Map<String, List<Double>> categoryMetrics = new HashMap<String, List<Double>>();
        try {
            for (File folder : new File(querydatasetpath).listFiles()) {
                File dir = new File(folder.getPath());
                if (dir.isDirectory()) {
                    File[] files = dir.listFiles();
                    for (int count = 0; count < files.length; count++) {
                        if (!Utils.getExtension(files[count]).equals("sift") && !Utils.getExtension(files[count]).equals("txt") ) {
                            queryCount++;
                            ImageData id = getQueryImage(files[count]);
                            List<ImageData> results = is.search(searchTypes, id, new ArrayList<ImageData>());
                            double[] metrics = printF1(results, id);
                            overallMetrics[0] += metrics[0];
                            overallMetrics[1] += metrics[1];
                            overallMetrics[2] += metrics[2];
                            
                            for(String category: id.getCategories()) {
                                if(!categoryMetrics.containsKey(category)){
                                    categoryMetrics.put(category, new ArrayList<Double>(Arrays.asList(0.0, 0.0, 0.0, 0.0)));
                                }
                                categoryMetrics.get(category).set(0, categoryMetrics.get(category).get(0) + metrics[0]);
                                categoryMetrics.get(category).set(1, categoryMetrics.get(category).get(1) + metrics[1]);
                                categoryMetrics.get(category).set(2, categoryMetrics.get(category).get(2) + metrics[2]);
                                categoryMetrics.get(category).set(3, categoryMetrics.get(category).get(3) + 1);
                            }            
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        String reportFileName = "";
        for (SearchType type: searchTypes) {
            reportFileName += type;
        }
        
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(reportPath + reportFileName + ".html"))) {
            String template = "<html><body>%s</body></html>";
            String catTemplate = "<h3>%s</h3><p>%s</p><br>";
            
            double overallPrecision = overallMetrics[0] / queryCount;
            double overallRecall = overallMetrics[1] / queryCount;
            double overallF1 = overallMetrics[2] / queryCount;
            
            String header = String.format("<h2>Overall Average Metrics:<h2><p>Precision: %s<br>Recall: %s<Br>F1: %s</p>", 
                    overallPrecision, overallRecall, overallF1);
            
            String body = "";
            for(String key: categoryMetrics.keySet()) {
                double avgPrecision = categoryMetrics.get(key).get(0) / categoryMetrics.get(key).get(3);
                double avgRecall = categoryMetrics.get(key).get(1) / categoryMetrics.get(key).get(3);
                double avgF1 = categoryMetrics.get(key).get(2) / categoryMetrics.get(key).get(3);
                String stats = String.format("Precision: %s<br>Recall: %s<Br>F1: %s", avgPrecision, avgRecall, avgF1);
                String line = String.format(catTemplate, key, stats);
                body += line;
            }
            
            bw.write(String.format(template, header+body));
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            if (dir.isDirectory()) {
                File[] files = dir.listFiles();
                for (int count = 0; count < files.length; count++) {
                    String filename = files[count].getName();
                    ImageData id = new ImageData(filename,
                            files[count].getPath(), tags.get(filename));
                    id.setCategories(categories.get(filename));
                    queryImages.put(filename, id);
                }
            } else {
                String filename = dir.getName();
                ImageData id = new ImageData(filename,
                        dir.getPath(), tags.get(filename));
                id.setCategories(categories.get(filename));
                queryImages.put(filename, id);
            }
        }
    }
    
    private double[] printF1(List<ImageData> results, ImageData id) {
        double metrics[] = new double[3];
        double truePositives = 0;
        Map<String, Integer> count = new HashMap<String, Integer>();
        for(int i = 0; i < is.getResultSize(); i ++) {
            Set<String> categories = results.get(i).getCategories();
            Set<String> intersection = new HashSet<String>(categories);
            if(id.getCategories() != null) {
                intersection.retainAll(id.getCategories());
            }
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
        
        double totalRelevant = 20;
        double totalSelected = is.getResultSize();
        double precision = truePositives / totalSelected;
        double recall = truePositives / totalRelevant;
        double f1;
        if (!((precision + recall) == 0.0)) {
           f1 = 2 * ((precision * recall) / (precision + recall));
        } else {
           f1 = 0.0;
        }
        System.out.printf("Precision: %s, Recall: %s, F1: %s\n", precision, recall, f1);
        metrics[0] = precision;
        metrics[1] = recall;
        metrics[2] = f1;
        return metrics;
    }
}
