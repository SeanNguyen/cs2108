import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Commons {

    public static Map<String, Set<String>> getCategories(String imageListPath, String groundTruthsPath) {
        Map<String, Set<String>> imageCategories = new HashMap<String, Set<String>>();
        List<String> imageList = new ArrayList<String>();

        // get keys
        try (BufferedReader br = new BufferedReader(new FileReader(
                imageListPath))) {
            String line = br.readLine();

            while (line != null) {
                imageList.add(line);
                imageCategories.put(line, new HashSet<String>());
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

    public static Map<String, Set<String>> getTags(String imageTagsPath) {
        Map<String, Set<String>> tags = new HashMap<String, Set<String>>();

        try (BufferedReader br = new BufferedReader(new FileReader(
                imageTagsPath))) {
            String line = br.readLine();

            while (line != null) {
                Set<String> tokens = new HashSet<String>();
                tokens.addAll(Arrays.asList(line.split("\\s+")));
                String key = line.substring(0, line.indexOf(" "));
                tokens.remove(key);
                tags.put(key, tokens);
                line = br.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tags;
    }
}
