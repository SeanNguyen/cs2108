import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Sift {

    private static final int words = 593;
    private static final String histogramFilePath = ".." + File.separator + "ImageData" + File.separator + "train" + File.separator + "visual_words_for_training_data";
    private static final String histogramIndexFilePath = ".." + File.separator + "ImageData" + File.separator + "train" + File.separator + "image_indexes_train";
    private static final String generateScriptPath = ".." + File.separator + "FeatureExtractor" + File.separator + "Bag-of-Visual-Words-Image" + File.separator + "generate.py";
    private static final String codeBookFilePath = ".." + File.separator + "ImageData" + File.separator + "train" + File.separator + "codebook_b.file";
    
    public static void preprocess(Map<String, ImageData> images) throws IOException {
        List<String> filenameList = new ArrayList<String>();

        try (BufferedReader br = new BufferedReader(new FileReader(histogramIndexFilePath))) {
            String line = br.readLine();

            while (line != null) {
                filenameList.add(line.substring(line.lastIndexOf("\\") + 1));
                line = br.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        File histogramFile = new File(histogramFilePath);
        try (BufferedReader br = new BufferedReader(new FileReader(histogramFile))) {
            String line = br.readLine();
            int i = 0;
            
            while (line != null) {
                String histS[] = line.split("\\s");
                double hist[] = new double[words];
                for (int j = 0; j < histS.length; j++) {
                    hist[j] = Double.parseDouble(histS[j].substring(histS[j].indexOf(":") + 1));
                };
                
                String key = filenameList.get(i);
                images.get(key).setSiftHistogram(hist);

                line = br.readLine();
                i++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void computeSimilarity(List<ImageData> images, ImageData queryImage) throws IOException {
        double[] hist = getHist(queryImage);
        for (int count = 0; count < images.size(); count++) {
            double[] h = images.get(count).getSiftHistogram();
            double sim = computeSimilarity(hist, h);
            images.get(count).setSiftSimilarity(sim);
        }
    }
    
    private static double computeSimilarity(double[] hist, double[] h) {
        // cosine similarity
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        for (int i = 0; i < words; i++) {
            dotProduct += hist[i] * h[i];
            normA += Math.pow(hist[i], 2);
            normB += Math.pow(h[i], 2);
        }   
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    private static double[] getHist(ImageData queryImage) throws IOException {
        ProcessBuilder pb = new ProcessBuilder("python", generateScriptPath, "-c", codeBookFilePath, queryImage.getFilePath());
        Process p = pb.start();
        double hist[] = new double[words];
        try (BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
            String line = br.readLine();
            String values = line.substring(line.indexOf("[") + 1, line.indexOf("]"));
            String histS[] = values.split(",\\s");
            for (int i = 0; i < histS.length; i++) {
                hist[i] = Double.parseDouble(histS[i]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }   
        return hist;
    }

}
