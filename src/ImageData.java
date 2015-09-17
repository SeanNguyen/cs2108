import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

public class ImageData {

    private String filename;
    private String filepath;
    private String category;
    private List<String> tags;
    
    private double colorSimilarity;
    
    private double[] colorHistogram;
    
    public ImageData(String filename, String filepath, String category, List<String> tags) {
        this.filename = filename;
        this.category = category;
        this.filepath = filepath;
        this.tags = tags;
    }
    
    public String filename() {
        return filename;
    }
    
    public String getCategory() {
        return category;
    }
    
    public BufferedImage getImage() throws IOException {
        File file = new File(filepath);
        BufferedImage img = ImageIO.read(file);
        return img;
    }
    
    public List<String> getTags() {
        return tags;
    }
    
    public double[] getColorHistogram() {
        return colorHistogram;
    }
    
    public double getColorSimilarity() {
        return colorSimilarity;
    }
    
    public void setColorHistogram(double[] colorHistogram) {
        this.colorHistogram = colorHistogram;
    }
    
    public void setColorSimilarity(double colorSimilarity) {
        this.colorSimilarity = colorSimilarity;
    }
}
