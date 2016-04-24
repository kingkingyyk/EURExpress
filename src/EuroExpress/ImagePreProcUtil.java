package EuroExpress;

/*
 * This is an Image Utility Class created for Image preprocessing stage on EURExpress data set; methods in this class include:
 * 1) load an image (input: image; output: image);
 * 2) get the Matrix from the image(input: image;output:matrix);
 * 3) apply median filter (3*3) (input:matrix (obtained from the image); output:matrix);
 * 4) get the image from the matrix(input:matrix; output:image);
 * 6) rescale image (input: image and predefined height and width of the image; output:image)
 * 7) show image on a panel(input: image; output: an image on the panel);
 * 8) FisherRatio algorithm (input: matrix, group vector for representing class(i.e.,0,or 1) and number of features to be selected; output: index of feature vector)
 * 9) Generate labels of images (positive, negative)
 * @lianxiu Han;
 * @version 1.0 27/03/2009;
 * @version 1.2 12/05/2010
 */
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.WritableRaster;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.swing.ImageIcon;

import org.apache.spark.api.java.function.Function;

public class ImagePreProcUtil {
	
	
	public static BufferedImage loadImage(String path) throws IOException {
		BufferedImage img = ImageIO.read(new File(path));
		if(img.getType() != BufferedImage.TYPE_BYTE_GRAY) {
			ColorConvertOp conv = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY),null);
			conv.filter(img, img);
		}
		return img;
	}
	
	public static void SaveImageToDisk(BufferedImage img, String type, File imgfile ) throws IOException {
		FileOutputStream outputfile =new FileOutputStream(imgfile);
		ImageWriter jpgWriter = ImageIO.getImageWritersByFormatName("jpg").next();
	    ImageWriteParam jpgWriteParam = jpgWriter.getDefaultWriteParam();
	    jpgWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
	    jpgWriteParam.setCompressionQuality(1f);
	    jpgWriter.setOutput(outputfile);
	     //better to use ImageOutputStream
	    jpgWriter.write(null,new IIOImage(img, null, null), jpgWriteParam);
	    outputfile.close();
	}
	
	public static int[][] GetImageMatrix(BufferedImage img) {
		int height = img.getHeight();
		int width  = img.getWidth();
		int imageMatrix[][] = new int[height][width];
		WritableRaster wr = img.getRaster();
		for (int y=0; y < height; y++) {
		  for(int x=0; x < width; x++) {
		    imageMatrix[y][x] = wr.getSample(x, y, 0);
		  }
		}
		return imageMatrix;
	}
	
	//median filter of the image 3*3
	public static int [][] ApplyFilter(int[][] matrix) {
		final int K = 4;
		int height = matrix.length;
		int width = matrix[0].length;
		int[] P = new int[2*K+1];
		int[][] filteredMatrix = new int[height][width]; 
		for(int v=1; v<=height-2; v++){
			for(int u=1; u<=width-2; u++){
				int n= 0;
				for(int j=-1; j<=1;j++){
					for(int i=-1; i<=1; i++){
						P[n]=matrix[v+j][u+i];
						n++;
					}
				}
				Arrays.sort(P);
				matrix[v][u]=P[K];
			}
		}
	  
		filteredMatrix =matrix;
	
		return filteredMatrix;
	}
	
	
	//get the image from the matrix created;
	public static BufferedImage GetImageFromMatrix(int[][] matrix) {
		int height = matrix.length;
		int width = matrix[0].length;
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
		WritableRaster wr = img.getRaster();
		for (int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) {
				int[] pixel = new int [3];
				for(int i =0; i<3; i++) {
					pixel[0] = matrix[y][x];
				}
				wr.setPixel(x,y,pixel);
			}
		}
		return img;	
	}
	
	//Rescaled the image;
	public static BufferedImage RescaleImage(BufferedImage img, int preferWidth, int preferHeight) {
		int height = preferHeight;
		int width =  preferWidth;
		Image rescaledImage = null;
		rescaledImage = img.getScaledInstance(preferWidth, preferHeight, Image.SCALE_SMOOTH);
		Image temp = new ImageIcon(rescaledImage).getImage();
		BufferedImage bufferedImage = new BufferedImage(temp.getWidth(null),temp.getHeight(null),BufferedImage.TYPE_BYTE_GRAY);
		Graphics2D graphics2D = bufferedImage.createGraphics();
		graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		graphics2D.drawImage(img, 0, 0, width, height, null);
		return bufferedImage;	
	}
	
	/*
	// show image on the panel;
	private static void showIcon(BufferedImage img) {
    ImageIcon icon = new ImageIcon(img);
    JLabel label = new JLabel(icon, JLabel.CENTER);
    JOptionPane.showMessageDialog(null, label, "icon", -1);
}	*/
	
	public static void main() {
		BufferedReader inFile;
		try {
	    	inFile=new BufferedReader(new FileReader(EuroExpress.DatasetFile));
	    	String InfoData;
	    	List<String> dList=new ArrayList<>();
	    	HashMap<String,String> WcDecFileMap=new HashMap<>();
			while ((InfoData=inFile.readLine() )!= null){
				String Records[]=InfoData.split(", ");
				//System.out.println(InfoData);
				String s=EuroExpress.ImageFliesDir+"/e"+Records[1]+"_1.jpg";
				dList.add(s);
				WcDecFileMap.put(s,EuroExpress.WvdecFileDir+"/e"+Records[1]+"_1.dat");
			}
			inFile.close();
			
			EuroExpress.sContext.parallelize(dList).filter(new Function<String, Boolean>() {
				private static final long serialVersionUID = 1L;

				public Boolean call(String s) throws IOException {
					  FileIO.XWrite(WcDecFileMap.get(s), ImageReadDec(loadImage(s)));
					  return false;
				  }
			}).count();
			
		}
		catch (IOException e) {e.printStackTrace();    }
	}

	public static double[] ImageReadDec(BufferedImage img) throws IOException	
	{
		BufferedImage scaledimg;
		scaledimg=RescaleImage(img,118,190);
		
		//getting the image from the matrix
		int[][] imgMatrix1 = GetImageMatrix(scaledimg);
		
		int[][] filteredImage = ApplyFilter(imgMatrix1);//median filter;
		// testing wavelet transform on the image;	

		double[] C=wavelet2D.lwvimg2(filteredImage, 3, 4);

		return C;
	}


	
	// FisherRatio algorithm
	
	public static int[] FeatureVector(double [][] matrix, int [] group,int NumberOfFeatures){
		int i,j,k,kp,kn;   //i,j,k: counter; kp,kn:number of positive and negative samples,respectively.
		double sumofp,sumofn,pmean,nmean,std2; //every feature's sum,average of positive and negative samples, and the sum of the stds 
		double minstds=1; //temporary variable, minimal non-zero std value
		double []FR =new double [matrix[0].length]; //Fisher Ratios vector
		int []fes=new int [matrix[0].length];       //fes:feature index vector 
		int [] feature =new int [NumberOfFeatures]; //selected features' index vector
		int height = matrix.length;
		int width = matrix[0].length;
		int LengthOfGroup = group.length;

		if (LengthOfGroup!=height){
			System.out.println("error");
			System.exit(1);
		}
		//for every column (feature), calculate Fisher Ratio
		for (j=0;j<width;j++){ 
			kp=0;
			sumofp=0;
			kn=0;
			sumofn=0;
			// calculate the summary value of positive and negative samples on this feature, respectively
			for(i=0;i<height;i++){
				if(group[i]==1){
					sumofp=sumofp+matrix[i][j];
					kp++;
				}
				else
				{
					sumofn=sumofn+matrix[i][j];
					kn++;
				}
			}
			//calculate the average value of positive and negative samples on this feature, respectively
			pmean=sumofp/kp;
			nmean=sumofn/kn;
			//calculate the std square value of positive and negative samples on this feature and add them together
			sumofp=0;
			sumofn=0;
			for (i=0;i<height;i++){
				if(group[i]==1){
					sumofp=sumofp+(matrix[i][j]-pmean)*(matrix[i][j]-pmean);
				}
				else
				{
					sumofn=sumofn+(matrix[i][j]-nmean)*(matrix[i][j]-nmean);
				}
			}
			std2=sumofp/kp+sumofn/kn;
			//calculate the Fisher Ratio of the feature
			if(std2==0){
				if(pmean!=nmean){
					FR[j]=-(pmean-nmean)*(pmean-nmean);//in case of the stds is zero but the average not same, mark it with negative value to deal with later
					fes[j]=j;
				}
				else{
					FR[j]=0;//if stds and average difference are both zero, set Fisher Ratio to 0
					fes[j]=j;
				}
			}
			
			else{
				FR[j]=(pmean-nmean)*(pmean-nmean)/std2;//calculate Fisher Ratio value
				fes[j]=j;
				if(std2<minstds){
					minstds=std2;   //renew the minimum non-zero std value, if poissable
				}
			}
			pmean=Math.abs(pmean);
			nmean=Math.abs(nmean);
			if (pmean<1e-6){
				if (nmean<1e-6){
					FR[j]=0;       //to avoid same very small noise signal's effect,set those features Fisher Ratio value zero
					fes[j]=j;
				}
			}
		}
		for(j=0;j<width;j++){
			if(FR[j]<0){
				FR[j]=-FR[j]/minstds; //those marked negative Fisher Ratio values are divided by the minimum stds value to avoid divided zero 
			}
		}
		double xp;//xp is a temporary variable for Fisher Ratio exchange
		int xi;   //xi is a temporary variable for feature index exchange
		//sort feature index based on Fisher Ratio
		for(int ii=0;ii<NumberOfFeatures;ii++){
	 		for (int jj=ii;jj<FR.length;jj++){
	 			if(FR[jj]>FR[ii]){
	 				xp=FR[ii];
	 				xi=fes[ii];
	 				FR[ii]=FR[jj];
	 				fes[ii]=fes[jj];
	 				FR[jj]=xp;
	 				fes[jj]=xi;
	 			}
	 		}
	 	}
		//get selected features' index based on High Fisher Ratio priority	
		for (k=0;k<NumberOfFeatures;k++){
			feature[k]=fes[k];
		}
		
		return feature;
	}
	
//////////////////////////////////////////////
	public static int []FisherSelect(double [][] X, int[] group, int NumberOfFeatures)
	{
		//double [][] X=new double[6][6];
		//int group[]={0, 0, 0, 1, 1, 1};
		//int NumberOfFeatures=3;
		//int selectedFeatures[] = new int[NumberOfFeatures];
		//for(int i=0; i < 6 ; i++){
		//	for (int j=0; j<6; j++){
		//		X[i][j]=Math.random();
		//		System.out.println("\t"+X[i][j]);
		//	}
		//}
		
		int []selectedFeatures=FeatureVector(X, group,NumberOfFeatures);
  
		//for(int i=0; i < NumberOfFeatures ; i++)
		//System.out.println("\t"+selectedFeatures[i]);
	
		return selectedFeatures;
		
		//testing the output of the matrix of the image...
		/*String out ="";
			for (int y = 0; y<imgMatrix1.length; y++){
				for (int x =0; x<imgMatrix1[0].length; x++){
					out+=Integer.toHexString(imgMatrix1[y][x]) + "";
				}
			System.out.println(out+="\n");
				}
			*/
	
		}
	
	
	//generate label of images ("0", "1" )
	
	public static int [] TrainGroup(String TrainingSetFile,String TestAnnotationLevelFromorOnly, int TestAnnotationID,int TestAnnotationLevel) {
		String [] TrainingSamples=FileIO.txtdataread(TrainingSetFile);
		int i=0;
		//while(TrainingSamples[i]!=null){
		//	i++;
		//}//obtain accurate number of training samples 
		int [] TrainingGroup=new int [TrainingSamples.length];
		int Psamples=0;
		for(i=0;i<TrainingGroup.length;i++) {
			//System.out.println(i);
			String TSet[]=TrainingSamples[i].split(", ");
			if(TestAnnotationLevelFromorOnly.equals("From"))
			{
				if (Integer.parseInt(TSet[TestAnnotationID+4])>=TestAnnotationLevel)
				{
					TrainingGroup[i]=1;
					Psamples++;
				}
				else
				{
					TrainingGroup[i]=0;
				}
				//System.out.println(TrainingGroup[i]);
			}
			else
			{
				if (Integer.parseInt(TSet[TestAnnotationID+4])==TestAnnotationLevel)
				{
					TrainingGroup[i]=1;
					Psamples++;
				}
				else
				{
					TrainingGroup[i]=0;
				}
			}
		}
		if (Psamples<1){System.out.println("No Possitive Sample in Training Set.");}
		if (Psamples==TrainingGroup.length){System.out.println("No Negative Sample in Training Set.");}
		return TrainingGroup;
	}
	
	
	
}

	
	

