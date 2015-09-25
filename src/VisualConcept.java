import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class VisualConcept {
	private static final String unprocessedImagePaths = System.getProperty("user.dir") + File.separator + "bin" + File.separator + "unprocessedImagePaths.txt"; 
	private static final String visualConceptExeFileWorkingDir = ".." + File.separator + "FeatureExtractor" + File.separator + "semanticFeature";
	private static final String visualConceptExeFile = visualConceptExeFileWorkingDir + File.separator + "image_classification.exe";
	
    public static void preprocess(Map<String, ImageData> imageDataMaps) throws IOException {
        //prepare a path input file
        File imagePathsFile = new File(unprocessedImagePaths);
        if(imagePathsFile.exists()) {
        	imagePathsFile.delete();
        }
        createInputPathFile(imageDataMaps);

        //run the visual concept tool - THIS TAKE REALLY LONG
        runVisualConceptTool();
    }
    
    public static void computeSimilarity(List<ImageData> images, ImageData queryImage) throws IOException {
    }
    
    //private helper methods
    private static double computeSimilarity(double[] hist, double[] h) {
    	return 0;
    }
    
    private static void createInputPathFile(Map<String, ImageData> imageDataMaps) {
    	try (BufferedWriter bw = new BufferedWriter(new FileWriter(unprocessedImagePaths))) {
    		for(ImageData imageData: imageDataMaps.values()) {
        		String fileName = imageData.getFilePath();
        		String visualConceptFileName = Utils.changeExtension(fileName, "txt");
        		//check if this image has been process or not
        		File visualConceptFile = new File(visualConceptFileName);
        		if(!visualConceptFile.exists()) {
            		bw.write(".." + File.separator + fileName);
            		bw.newLine();
        		}
        	}
    		bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static void runVisualConceptTool() throws IOException {
    	ProcessBuilder processBuilder = new ProcessBuilder(visualConceptExeFile, unprocessedImagePaths);
        processBuilder.directory(new File(visualConceptExeFileWorkingDir).getAbsoluteFile());
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();
        
        //print the output and wait for the process to end
        Scanner s = new Scanner(process.getInputStream());
        StringBuilder text = new StringBuilder();
        while (s.hasNextLine()) {
          System.out.println(s.nextLine());
        }
        s.close();
		try {
	        int result;
			result = process.waitFor();
	        System.out.printf( "Process exited with result %d and output %s%n", result, text );
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }
}
