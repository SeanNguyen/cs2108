import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Vector;

public class VisualConcept {
	private static final String unprocessedImagePaths = System.getProperty("user.dir") + File.separator + "bin" + File.separator + "unprocessedImagePaths.txt"; 
	private static final String visualConceptExeFileWorkingDir = ".." + File.separator + "FeatureExtractor" + File.separator + "semanticFeature";
	private static final String visualConceptExeFile = visualConceptExeFileWorkingDir + File.separator + "image_classification.exe";
	
	private static final int categorySize = 1000;
	
    public static void preprocess(Map<String, ImageData> imageDataMaps) throws IOException {
        //prepare a path input file
        File imagePathsFile = new File(unprocessedImagePaths);
        if(imagePathsFile.exists()) {
        	imagePathsFile.delete();
        }
        
        //get a list of unprocessed jpg file
        Vector<String> unprocessedFiles = getUnprocessedFiles(imageDataMaps);
        if(unprocessedFiles.size() > 0) {
            createInputPathFile(unprocessedFiles);
            //run the visual concept tool - THIS TAKE REALLY LONG
            runVisualConceptTool();
        }
    }
    
    public static void computeSimilarity(List<ImageData> images, ImageData queryImage) throws IOException {
    	String queryImageVCFilePath = Utils.changeExtension(queryImage.getFilePath(), Utils.txt);
    	File queryImageVCFile = new File(queryImageVCFilePath);
		if(!queryImageVCFile.exists()) {
			//the image file haven't been process, let's process it
			Vector<String> unprocessFiles = new Vector<>();
			unprocessFiles.add(queryImage.getFilePath());
			createInputPathFile(unprocessFiles);
			runVisualConceptTool();
		}
		double[] queryImageScores = getVisualConceptScoreFromFile(queryImage);
    	
    	for(ImageData imageData: images) {
    		double[] imageScores = imageData.getVisualConceptScores();
    		double similarity = computeSimilarity(queryImageScores, imageScores);
    		imageData.setVisualConceptSimilarity(similarity);
    	}
    }
    
    //private helper methods
    private static double computeSimilarity(double[] classificationScoreImg1, double[] classificationScoreImg2) {
    	// Ok, so how we get this similarity score is very optional
    	// First we see if 2 imgs is in the same category, 
    	// If they have a common category then get the lower score to add to the total similarity score
    	// As a result, imgs with more common category and score higher tgt in some common category will have better result.
    	double similarity = 0;
    	if(classificationScoreImg1 == null || classificationScoreImg2 == null 
    			|| classificationScoreImg1.length < 1000 || classificationScoreImg2.length < 1000) {
    		return 0;
    	}
    	for(int i = 0; i < categorySize; i++) {
    		if(classificationScoreImg1[i] > 0 && classificationScoreImg2[i] > 0) {
    			similarity += Math.min(classificationScoreImg1[i], classificationScoreImg2[i]);
    		}
    	}
    	return similarity;
    }
    
    private static Vector<String> getUnprocessedFiles(Map<String, ImageData> imageDataMaps) throws FileNotFoundException {
    	Vector<String> unprocessedFiles = new Vector<>();
		for(ImageData imageData: imageDataMaps.values()) {
    		String fileName = imageData.getFilePath();
    		String visualConceptFileName = Utils.changeExtension(fileName, "txt");
    		//check if this image has been process or not
    		File visualConceptFile = new File(visualConceptFileName);
    		if(visualConceptFile.exists()) {
    			double[] visualConceptScores = getVisualConceptScoreFromFile(imageData);
    			imageData.setVisualConceptScores(visualConceptScores);
    		} else {
    			unprocessedFiles.add(imageData.getFilePath());
    		}
    	}
		return unprocessedFiles;
    }
    
    private static void createInputPathFile(Vector<String> unprocessedFiles) {
    	try (BufferedWriter bw = new BufferedWriter(new FileWriter(unprocessedImagePaths))) {
    		for(String filePath: unprocessedFiles) {
        		bw.write(".." + File.separator + filePath);
        		bw.newLine();
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

    private static double[] getVisualConceptScoreFromFile(ImageData image) throws FileNotFoundException {
    	double[] result = new double[categorySize];
    	//read data from pre-computed file
		String visualConceptDataPath = Utils.changeExtension(image.getFilePath(), Utils.txt);
		File visualConceptDataFile = new File(visualConceptDataPath);
		Scanner scanner = new Scanner(visualConceptDataFile);
		
		int count = 0;
		while(scanner.hasNext()) {
			double score = scanner.nextDouble();
			result[count] = score;
			count++;
		}
		scanner.close();
		return result;
    }
}
