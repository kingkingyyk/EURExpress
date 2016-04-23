package EuroExpress;

/*
 * This is FeatureSelection class , which is similar to FisherRasio parallel
 * @lianxiu Han;
 * @version 1.0 12/05/2010
 */


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.spark.api.java.function.Function;


public class FeatureSelection {

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
		// TODO Auto-generated method stub
		//reading setup file
		int i=0;
		int TestAnnotationID=1;
		int TestAnnotationLevel=2;
		int NumberOfFeatures=24;
		System.out.println("Reading Training Set Files and generating group file...");
		System.out.println("MeanP,MeanN,StdP,StdN, calculating....");
		double []MP=new double [1];
		double []MN=new double [1];
		double [][]Mean = PNMSTD("Mean",MP,MN,p.getProperty("TrainingSetFile"),p.getProperty("WvdecFileDir"),p.getProperty("TestAnnotationLevelFromorOnly"),TestAnnotationID,TestAnnotationLevel);
		//Mean[0][]: MeanP; mean[1][]:MeanN
		double MeanP[]=new double[Mean[0].length];
		double MeanN[]=new double[Mean[0].length];
		double StdP[]=new double[Mean[0].length];
		double StdN[]=new double[Mean[0].length];
		for (i=0;i<Mean[0].length;i++){
			MeanP[i]=Mean[0][i];
			MeanN[i]=Mean[1][i];
		}
		System.out.println("MeanP,MeanN calculated");
		double [][]Std=PNMSTD("Std",MeanP,MeanN,p.getProperty("TrainingSetFile"),p.getProperty("WvdecFileDir"),
				p.getProperty("TestAnnotationLevelFromorOnly"),TestAnnotationID,TestAnnotationLevel);
		for (i=0;i<Std[0].length;i++){
			StdP[i]=Std[0][i];
			StdN[i]=Std[1][i];
		}
		System.out.println("StdP,StdN caculated.");
		///////////////////////////////////////////
		int[] Features=FisherCalculate(MeanP, StdP, MeanN, StdN, NumberOfFeatures);
		String SelectedFeatureFiles=p.getProperty("TestResultDir")+"/SF"+Integer.toString(NumberOfFeatures)+Integer.toString(TestAnnotationID)
		+p.getProperty("TestAnnotationLevelFromorOnly")+Integer.toString(TestAnnotationLevel)+".txt";
		String []FeatureStrs=new String [NumberOfFeatures+3];
		FeatureStrs[0]="Annotation: "+Integer.toString(TestAnnotationID);
		FeatureStrs[1]="AnnotationLevel: "+p.getProperty("TestAnnotationLevelFromorOnly")+Integer.toString(TestAnnotationLevel);
		FeatureStrs[2]="TrainingSetFile: "+p.getProperty("TrainingSetFile");
		for (i=0;i<NumberOfFeatures;i++){
		//System.out.println(Features[i]);
		FeatureStrs[i+3]=Integer.toString(Features[i]);}
		FileIO.txtdatawrite(SelectedFeatureFiles, FeatureStrs,NumberOfFeatures+3);
		System.out.println("Feature Selection Accomplished");
		System.out.println("Result File: "+SelectedFeatureFiles);
		
		
	}
		
		
		@SuppressWarnings("unused")
		public static double[][] PNMSTD(String MS,double []MP, double []MN,String TrainingSetFile,String WvdecFileDir,
				String TestAnnotationLevelFromorOnly,int TestAnnotationID,int TestAnnotationLevel)
		{
		BufferedReader inFile;
		int Psamples=0;
		int Nsamples=0;	
		int N=0;
	    try {
	    	inFile=new BufferedReader(
	    			new FileReader(TrainingSetFile));
	    	String StrSqls=inFile.readLine();
   		 	String TSet[]=StrSqls.split(", ");
			String DataFile=WvdecFileDir+"/e"+TSet[1]+"_1.dat";
			double C[]=FileIO.Xread(DataFile);
			N=C.length;
			inFile.close();
    		}
	    	catch (FileNotFoundException e)
	    	{
	    		e.printStackTrace();
	    	} 
	    	catch (IOException e)
	    	{
	    		e.printStackTrace();       
	    	}			
			double []MSP=new double[N];
			double []MSN=new double[N];
			double [][] MeanStd=new double[2][N];
			for(int i=0;i<N;i++)
			{
				MSP[i]=0;
				MSN[i]=0;
			}
		try {
		    inFile=new BufferedReader(
		    		new FileReader(TrainingSetFile));
	    	String StrSqls;
			while ((StrSqls=inFile.readLine() )!= null){
	    		//String StrSqls=inFile.readLine();
	    		//System.out.println(StrSqls);
	   		 	String TSet[]=StrSqls.split(", ");
				String DataFile=WvdecFileDir+"/e"+TSet[1]+"_1.dat";
				double C[]=FileIO.Xread(DataFile);	
	 			 if(TestAnnotationLevelFromorOnly.equals("From"))
	 			 {
					if (Integer.parseInt(TSet[TestAnnotationID+4])>=TestAnnotationLevel)
					{
						for(int i=0;i<C.length;i++)
						{
							if (MS.equals("Mean")){
							MSP[i]=MSP[i]+C[i];}
							else{MSP[i]=MSP[i]+(C[i]-MP[i])*(C[i]-MP[i]);
							}
						}
						Psamples++;
					}
					else
					{
						for(int i=0;i<C.length;i++)
						{
							if (MS.equals("Mean")){MSN[i]=MSN[i]+C[i];}
							else{MSN[i]=MSN[i]+(C[i]-MN[i])*(C[i]-MN[i]);}
						
						}
						Nsamples++;
					}
				}
				else
				{
					if (Integer.parseInt(TSet[TestAnnotationID+4])==TestAnnotationLevel)
					{
						for(int i=0;i<C.length;i++)
						{if (MS.equals("Mean")){MSP[i]=MSP[i]+C[i];}
						else{MSP[i]=MSP[i]+(C[i]-MP[i])*(C[i]-MP[i]);}
						}
						Psamples++;
					}
					else
					{
						for(int i=0;i<C.length;i++)
						{
							if (MS.equals("Mean")){MSN[i]=MSN[i]+C[i];}
							else{MSN[i]=MSN[i]+(C[i]-MN[i])*(C[i]-MN[i]);}
						}
						Nsamples++;
					}
				}	
			}
	    	inFile.close();
    	}
	    catch (FileNotFoundException e)
	    {
	      e.printStackTrace();
	    } 
	    catch (IOException e)
	    {
	      e.printStackTrace();       
	    }	
	    for(int i=1;i<N;i++)
			 {
				 MeanStd[0][i]=MSP[i]/Psamples;
				 MeanStd[1][i]=MSN[i]/Psamples;
			 }   	
    return MeanStd;
	}
	
public static int[] FisherCalculate(double [] MeanP, double [] StdP, double[] MeanN, double[] StdN, int NumberOfFeatures){
	int i,k;   //i,j,k: counter; kp,kn:number of positive and negative samples,respectively.
	double minstds=1; //temporary variable, minimal non-zero std value
	int [] feature = new int [NumberOfFeatures]; //selected features's index vector;
	//int LengthOfMeanP = MeanP.length; // number of means of positive samples;
	//int LengthOfMeanN = MeanN.length; //number of means of negative samples;
	int LengthOfFeature = MeanN.length;
	double std2;
	double []FR = new double [LengthOfFeature];// Fisher Ratio Vector;
	int []fes=new int [LengthOfFeature];       //fes:feature index vector 
	

	for (i = 0; i<LengthOfFeature; i++){
  	//System.out.println(LengthOfFeature);
  	//System.out.println(MeanP.length);
  	//System.out.println(MeanN.length);
  	 std2= StdP[i]+StdN[i];
  	
  		//calculate the Fisher Ratio of the feature
			if(std2==0){
				if(MeanP[i]!=MeanN[i]){
					FR[i]=-(MeanP[i]-MeanN[i])*(MeanP[i]-MeanN[i]);//in case of the stds is zero but the average not same, mark it with negative value to deal with later
					fes[i]=i;
				}
				else{
					FR[i]=0;//if stds and average difference are both zero, set Fisher Ratio to 0
					fes[i]=i;
				}
			}
			else{
				FR[i]=(MeanP[i]-MeanN[i])*(MeanP[i]-MeanN[i])/std2;//calculate Fisher Ratio value
				fes[i]=i;
				if(std2<minstds){
					minstds=std2;   //renew the minimum non-zero std value, if poissable
				}
			}
			MeanP[i]=Math.abs(MeanP[i]);
			MeanN[i]=Math.abs(MeanN[i]);
			if (MeanP[i]<1e-6){
				if (MeanN[i]<1e-6){
					FR[i]=0;       //to avoid same very small noise signal's effect,set those features Fisher Ratio value zero
					fes[i]=i;
				}
			}
		}
			
		for(i=0;i<LengthOfFeature;i++){
			if(FR[i]<0){
				FR[i]=-FR[i]/minstds; //those marked negative Fisher Ratio values are divided by the minimum stds value to avoid divided zero 
			}
		}
		
		double xp;//xp is a temporary variable for Fisher Ratio exchange
		int xi;   //xi is a temporary variable for feature index exchange
		//sort feature index based on Fisher Ratio
		for(int ii=0;ii<FR.length;ii++){
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
	

	
	
	
}	
