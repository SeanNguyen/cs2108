import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TextFeature {
    
    public static void computeSimilarity(Map<String, ImageData> images, Set<String> queryTags) {
        // normalize
        double baseSimilarityValue = 1.0 / queryTags.size();
        for (ImageData image: images.values()) {
            Set<String> intersection = new HashSet<String>(image.getTags());
            intersection.retainAll(queryTags);
            if (intersection.size() > 0) {
                image.setTextSimilarity(intersection.size() * baseSimilarityValue);
            } else {
                image.setTextSimilarity(0);
            }
        }
    }
}
