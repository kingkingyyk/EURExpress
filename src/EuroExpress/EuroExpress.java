package EuroExpress;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.SparkConf;

// Execution steps :
//  - Modify the TaskSetup file
//    - The IP address of master
//    - Add shared folder (image files, etc) to all the slaves node
//    - The executable jar file's name. (should be the jar itself).
public class EuroExpress {

	private static String TaskSetFile="TaskSetup.txt";
	public static String TrainingSetFile="";
	public static String WvdecFileDir="";
	public static String TestResultDir="";
	public static String DatasetFile="";
	public static String ImageFliesDir="";
	public static String TestSetFile="";
	public static int NumberOfFeatures=24;
	public static int TestAnnotationID=1;
	public static String TestAnnotationLevelFromorOnly="From";
	public static int TestAnnotationLevel=2;
	public static int NumberOfNeighbour=1;
	public static long load, scaleimg, matrix, filter, wavelet=0;
	public static SparkConf sConf;
	public static JavaSparkContext sContext;
	public static Properties prop;
	
	public static void main (String [] args) {
		Properties p=new Properties();
		try {
			FileInputStream fis=new FileInputStream(TaskSetFile);
			p.load(fis);
			fis.close();
		} catch (IOException e) {e.printStackTrace();}
		prop=p;
		TrainingSetFile=p.getProperty("TrainingSetFile", "TrainingSet.txt");
		WvdecFileDir=p.getProperty("WvdecFileDir","F:/test/WvDecFileDir");
		TestResultDir=p.getProperty("TestResultDir","F:/test/Results");
		ImageFliesDir=p.getProperty("ImageFliesDir","F:/test/ImageFiles");
		DatasetFile=p.getProperty("DatasetFile","Infodata.txt");
		TestSetFile=p.getProperty("TestSetFile","TestSet.txt");
		NumberOfFeatures=Integer.parseInt(p.getProperty("NumberOfFeatures","24"));
		TestAnnotationID=Integer.parseInt(p.getProperty("TestAnnotationID","1"));
		TestAnnotationLevelFromorOnly=p.getProperty("TestAnnotationLevelFromorOnly","From");
		TestAnnotationLevel=Integer.parseInt(p.getProperty("TestAnnotationLevel","2"));
		NumberOfNeighbour=Integer.parseInt(p.getProperty("NumberOfNeighbour","1"));
		
		sConf=new SparkConf().setAppName("Eurexpress").setMaster("spark://"+p.getProperty("Master","localhost:7077"));
		sConf.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer");
		//sConf.set("spark.dynamicAllocation.maxExecutors", "1");
		sConf.setJars(new String [] {p.getProperty("Jarfilename", "Eurexpress.jar")});
		sContext=new JavaSparkContext(sConf);
		long before=System.currentTimeMillis();
		ImagePreProcUtil.main();
		FeatureSelection.main();
		KNN.main();
		long after=System.currentTimeMillis();
		System.out.println((after-before)+"ms taken.");
	}
	
}
