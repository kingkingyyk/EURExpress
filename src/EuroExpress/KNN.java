package EuroExpress;

/*
 * This class perfoms KNN classification on an sample matrxi. 
 * The input are feature matrix of images(t), the feature vector(x) of an image to be tested, 
 * class vector(group) for labeling the class of an image belonging to, and k is the number of neighbours.
 * The output is classification (0 or 1); 0 means negative; 1 means positive;
 * @liangxiu han;
 * @version 1.0, 
 * 04/04/2009
 * Version 1.2
 * 12/05/2010
 */

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.spark.api.java.function.Function;


public class KNN {
	
	public static void main () {
		List<Properties> dummylist=new ArrayList<>();
		dummylist.add(EuroExpress.prop);
		EuroExpress.sContext.parallelize(dummylist).filter(new Function<Properties, Boolean>() {
			private static final long serialVersionUID = 1L;

			@Override
			public Boolean call(Properties p) throws Exception {
				perform(p);
				return false;
			}
			
		}).count();
	}
	
	private static void perform (Properties p) {
		int i=0;
		int [] Features=new int[Integer.parseInt(p.getProperty("NumberOfFeatures"))];
		String SelectedFeatureFile=p.getProperty("TestResultDir")+"/SF"+p.getProperty("NumberOfFeatures")+p.getProperty("TestAnnotationID")
		+p.getProperty("TestAnnotationLevelFromorOnly")+p.getProperty("TestAnnotationLevel")+".txt";
		System.out.println("Using SelectedFeatureFile: "+SelectedFeatureFile);
		System.out.println("Reading Training set classify information and generating group vector....");
		String [] TrainingSamples=FileIO.txtdataread(p.getProperty("TrainingSetFile"));
		int [] TrainingGroup=ImagePreProcUtil.TrainGroup(p.getProperty("TrainingSetFile"),p.getProperty("TestAnnotationLevelFromorOnly"),
				Integer.parseInt(p.getProperty("TestAnnotationID")),Integer.parseInt(p.getProperty("TestAnnotationLevel")));
		//for(int ttt=0;ttt<10;ttt++){
		//System.out.println(TrainingGroup[ttt]);}
		double [][] FC=new double [TrainingGroup.length][Integer.parseInt(p.getProperty("NumberOfFeatures"))];
		System.out.println("Training Set Read and Group File Generated!");
		System.out.println("Reading SelectedFeatureFile.");
		String [] SelectedFeature=FileIO.txtdataread(SelectedFeatureFile);
		for(i=0;i<Integer.parseInt(p.getProperty("NumberOfFeatures"));i++)
		{
			Features[i]=Integer.parseInt(SelectedFeature[i+3]);
		}
		int dotint=TrainingGroup.length/10;
		int dti=dotint;
		for(i=0;i<TrainingGroup.length;i++)
		{
			String TSet[]=TrainingSamples[i].split(", ");
			String WvDecFile=p.getProperty("WvdecFileDir")+"/e"+TSet[1]+"_1.dat";
			double[] C=FileIO.Xread(WvDecFile);
			for (int j=0;j<Integer.parseInt(p.getProperty("NumberOfFeatures"));j++)
			{
				FC[i][j]=C[Features[j]];
			}	
			dti--;
			if(dti==0){
			System.out.print(".");
			dti=dotint;}
		}
		System.out.println("Training Set Feature Matrix generated");
		String [] TestSamples=FileIO.txtdataread(p.getProperty("TestSetFile"));
		//System.out.println(TestSamples.length);
		double []FCT=new double[Integer.parseInt(p.getProperty("NumberOfFeatures"))];
		String ClassifyResultFile=p.getProperty("TestResultDir")+"/TestResultOf"+p.getProperty("NumberOfFeatures")+p.getProperty("TestAnnotationID")
		+p.getProperty("TestAnnotationLevelFromorOnly")+p.getProperty("TestAnnotationLevel")+".txt";
		try { 
			FileWriter fw = new FileWriter(ClassifyResultFile);
			PrintWriter out = new PrintWriter(fw); 
			out.println("TestSetData: "+p.getProperty("TestSetFile"));
			out.println("NumberOfFeatures: "+p.getProperty("NumberOfFeatures"));
			for(i=0;i<3;i++){
			out.println(SelectedFeature[i]);}	
			for (i=0;i<TestSamples.length;i++)
			{	
				String TSet[]=TestSamples[i].split(", ");
				String WvDecFile=p.getProperty("WvdecFileDir")+"/e"+TSet[1]+"_1.dat";
				double[] TC=FileIO.Xread(WvDecFile);
				for (int j=0;j<Integer.parseInt(p.getProperty("NumberOfFeatures"));j++)
				{
					FCT[j]=TC[Features[j]];
				}
				int Class=KNNclassify(FCT,FC,TrainingGroup,Integer.parseInt(p.getProperty("NumberOfNeighbour")));
				//System.out.println(Class);
				dti--;
				if(dti<=0){
				System.out.print(".");
				dti=dotint;}
				out.println("e"+TSet[1]+", "+Integer.toString(Class));
			}
			out.close(); 
			fw.close(); 
			} catch (IOException e) { 
			e.printStackTrace(); 
			} 
		System.out.println("Test Set Classify accompolished");
		System.out.println("Result File is: "+ClassifyResultFile);
		//System.out.println("FC.length is "+FC.length);
		//System.out.println("FCT.length is "+FCT.length);
		//System.out.println("group.length is "+TrainingGroup.length);
		
		
		//double [][]t={{16,17,18},{13,14,15},{11,12,13},{2,13,4},{1,3,10},{0,12,-2}};
		//double []x={0,15,-13};
		//int []group={0,0,0,1,1,1};
		//int k=3;
		//int classtype=0;
		//classtype=KNNclassify(x,t,group,k);
		//System.out.println("classtype is \t"+classtype);
	}
	
	
	
	public static int KNNclassify(double []x,double [][]t,int[]group,int k){
		int classtype;int ik;
		double []td=new double[t[0].length];
		double []dist=new double [t.length];
		int []fes=new int [t.length];
		double xp;
		int xi;
		int ns=0;int ps=0;
		if(t.length!=group.length){
			System.out.println("training set sample number should be equal group rows");
			
		}
		if(x.length!=t[0].length){
			System.out.println("test sample should have same features as training samples");
			
		}
		for(int i=0;i<t.length;i++){
			for(int j=0;j<t[0].length;j++){
				td[j]=t[i][j];
			}
			dist[i]=eurodist(x,td);
			fes[i]=i;
		}
		//sort feature index based on Fisher Ratio
		for(int i=0;i<t.length;i++){
	 		for (int j=i;j<t.length;j++){
	 			if(dist[j]>dist[i]){
	 				xp=dist[i];
	 				xi=fes[i];
	 				dist[i]=dist[j];
	 				fes[i]=fes[j];
	 				dist[j]=xp;
	 				fes[j]=xi;
	 			}
	 		}
	 	}
		//sorted:	
		for (int i=t.length; i>t.length-k;i--){
			ik=fes[i-1];
			switch (group[ik]){
			case 0:
				ns++;
			case 1:
				ps++;
			}
		}
		if (ps>ns){
			classtype=1;	
		}
		else{
			classtype=0;
		}
		return classtype;
	}
	
	public static double eurodist(double []a,double []b){
		double d=0.0;
		for(int i=0;i<a.length;i++){
			d=d+(a[i]-b[i])*(a[i]-b[i]);
		}
		d=Math.sqrt(d);
		return d;
	}
	
}
