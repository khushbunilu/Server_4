package com.khushbu;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

import Jama.Matrix;
import Jama.SingularValueDecomposition;

import com.khushbu.imageimport.Image;
import com.khushbu.imageimport.ImageImporter;
import com.khushbu.imageimport.ImagePreprocessor;
import com.khushbu.matrix.MatrixBuilder;
import org.apache.commons.io.FileUtils;

public class ImageRecognizer {

	private static final int IMAGE_X_SIZE = 92; //= 92;
	private static final int IMAGE_Y_SIZE = 112; //= 112;

	private static final short MAXIMUM_GREY_VALUE = 255;

	private String trainingSetPath;
	private List<Image> images;

	private Image images1;

	private static SingularValueDecomposition svd;
	private Map<String, Matrix> mappedImages;
	private Map<String, Matrix> mappedImages1;


	public ImageRecognizer() {
		mappedImages = new HashMap<String, Matrix>();
	}

	public void setTrainingSetPath(String path) {
		trainingSetPath = path;
	}

	public void buildUp() {
		images = loadImages();
		Matrix m = buildMatrix();
		System.out.println("Image loading successful.");

		svd = m.svd();
		System.out.println("SVD complete");
        int Number_of_Files=new File("TrainingSet").list().length;

        try {
            File f1=new File("EigenFaces");

            if(!f1.exists())
            {
                f1.mkdir();
            }

            FileUtils.cleanDirectory(new File("EigenFaces"));
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        exportFirstEigenfaces(svd, Number_of_Files);
		System.out.println("Exported eigenfaces");

		mapImagesToLatentSpace();

	}



	public Matrix buildUp1(String imagename) {
		images1 = loadImages1(imagename);
        int Number_of_Files=new File("TrainingSet").list().length;
        //Matrix m = buildMatrix1(Number_of_Files);
		System.out.println("Image loading successful.");

		//svd1 = m.svd();
	//	System.out.println("SVD complete");

		Matrix m1=mapImagesToLatentSpace1();
		return m1;
	}






	private void mapImagesToLatentSpace() {
		for (Image image : images) {
			Matrix vector = image.getDataAsSingleDimensionalVector();
			Matrix mappedImage = null;
			Matrix u = svd.getU();
			mappedImage = u.transpose().times(vector);
			//System.out.println("mapped images name : " + image.getName());
			mappedImages.put(image.getName(), mappedImage);
		}
	}

	private Matrix mapImagesToLatentSpace1() {

		Matrix vector = images1.getDataAsSingleDimensionalVector();
		Matrix mappedImage = null;
		Matrix u = svd.getU();
		mappedImage = u.transpose().times(vector);
		//System.out.println("mapped images name : " + images1.getName());
		return mappedImage;

	}

	/**
	 * Finds the k most similar images
	 * @param imageName
	 * @param k
	 */
	public String findMostSimilarImages(String imageName, int k,Matrix m1) {

		Matrix imageVector = m1;

		double d[][]= m1.getArray();


		NavigableMap<Double, List<String>> largestSimilarities = new TreeMap<Double, List<String>>();

		for (Entry<String, Matrix> mappedImage : mappedImages.entrySet()) {
			//  System.out.println("\n\n"+Arrays.deepToString(mappedImage.getValue().getArray()));
			double similarity = cosineSimilarity(mappedImage.getValue(), imageVector);
			//System.out.println("similarity is "+similarity);
			List<String> similarities = largestSimilarities.get(similarity);
			if (similarities == null) {
				largestSimilarities.put(similarity, new ArrayList<String>());
			}
			largestSimilarities.get(similarity).add(mappedImage.getKey());
		}

		//System.out.println("Most similar images: ");
		NavigableSet<Double> similarities = largestSimilarities.descendingKeySet();
		Iterator<Double> it = similarities.iterator();
        String s=null;
        String s1=null;
		for (int i = 0; i < k; i++) {
			Double similarity = it.next();
			List<String> similarImages = largestSimilarities.get(similarity);

			for (String similarImageName : similarImages) {
                s = similarImageName.substring(similarImageName.indexOf("TrainingSet/")+12,similarImageName.length());
				//System.out.println("Similarity " + similarity + " for image " + s);

                if(i==0)
                {
                    s1=s;
                }
			}

		}


        return s1;


	}

	public void findMostSimilarImages1(Matrix m1,Matrix m2) {


		double similarity = cosineSimilarity(m1, m2);
		System.out.println("similarity is "+similarity);
	}

	private double cosineSimilarity(Matrix vector1, Matrix vector2) {

		double dotProduct = vector1.arrayTimes(vector2).norm1();
		double eucledianDist = vector1.normF() * vector2.normF();
		return dotProduct / eucledianDist;

	}

	/**
	 * Exports the top k eigenfaces.
	 * @param svd
	 * @param k
	 */
	private void exportFirstEigenfaces(SingularValueDecomposition svd, int k) {
		EigenfaceCreator creator = new EigenfaceCreator(svd, IMAGE_X_SIZE, IMAGE_Y_SIZE, MAXIMUM_GREY_VALUE);

		List<Image> eigenfaces = creator.getFirstEigenfaces(k);

		int i = 0;
		for (Image eigenface : eigenfaces) {
			try {
				eigenface.exportToPgm("EigenFaces/eigenface" + i + ".pgm");
			} catch (IOException e) {
				e.printStackTrace();
			}
			i++;
		}
	}

	private void exportFirstEigenfaces1(SingularValueDecomposition svd, int k) {
		EigenfaceCreator creator = new EigenfaceCreator(svd, IMAGE_X_SIZE, IMAGE_Y_SIZE, MAXIMUM_GREY_VALUE);
		List<Image> eigenfaces = creator.getFirstEigenfaces(k);

		int i = 0;
		for (Image eigenface : eigenfaces) {
			try {
				eigenface.exportToPgm("EigenFaces/eigenface" + i + ".pgm");
			} catch (IOException e) {
				e.printStackTrace();
			}
			i++;
		}
	}

	/**
	 * Loads all pgm images from the specified path and all subfolders
	 * @return
	 */
	private List<Image> loadImages() {
		ImageImporter importer = new ImageImporter(IMAGE_X_SIZE, IMAGE_Y_SIZE);
		ImagePreprocessor preprocessor = new ImagePreprocessor();

		File trainingSetDirectory = new File(trainingSetPath);
		return loadImages(importer, preprocessor, trainingSetDirectory);
	}

	private Image loadImages1(String imagename) {
		ImageImporter importer = new ImageImporter(IMAGE_X_SIZE, IMAGE_Y_SIZE);
		ImagePreprocessor preprocessor = new ImagePreprocessor();

		return loadImages1(importer, preprocessor, imagename);
	}

	/**
	 * puts all loaded images in a matrix
	 * @return
	 */
	private Matrix buildMatrix() {
		MatrixBuilder builder = new MatrixBuilder(IMAGE_X_SIZE, IMAGE_Y_SIZE);
		for (Image image : images) {
			builder.addImage(image);
		}

		return builder.buildMatrix();
	}

	private Matrix buildMatrix1(int k) {
		MatrixBuilder builder = new MatrixBuilder(IMAGE_X_SIZE, IMAGE_Y_SIZE);
		for(int i=0;i<k;i++) {
			builder.addImage(images1);
		}
		return builder.buildMatrix();
	}

	/**
	 * Loads all images in the given directory recursively.
	 * @param importer
	 * @param preprocessor
	 * @param file
	 * @return
	 */
	private List<Image> loadImages(ImageImporter importer, ImagePreprocessor preprocessor, File file) {
		List<Image> images = new ArrayList<Image>();
		if (!file.isDirectory()) {
			try {
				String fileName = file.getCanonicalPath();
				String fileEnding = file.getAbsolutePath().substring(fileName.length() - 4);
				if (!fileEnding.equals(".pgm")) {
					System.out.println("File named " + fileName + " is no pgm file.");
				} else {
					Image image = importer.loadSingleImage(fileName);
					preprocessor.interpolateNewMaximumGrey(image, MAXIMUM_GREY_VALUE);
					images.add(image);

				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			return images;
		} else {
			File[] contents = file.listFiles();
			for (int i = 0; i < contents.length; i++) {
				images.addAll(loadImages(importer, preprocessor, contents[i]));
			}
			return images;
		}
	}





	private Image loadImages1(ImageImporter importer, ImagePreprocessor preprocessor, String fileName) {

		Image image=null;
		try {
			String fileEnding = fileName.substring(fileName.length() - 4);
			if (!fileEnding.equals(".pgm")) {
				System.out.println("File named " + fileName + " is no pgm file.");
			} else {
				image = importer.loadSingleImage(fileName);
				preprocessor.interpolateNewMaximumGrey(image, MAXIMUM_GREY_VALUE);


			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return image;

	}















}
