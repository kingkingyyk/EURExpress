package EuroExpress;

/*
 * This class can read/or write a double vector from/or to a Binary data file/a text file,
 * @lianxiu Han;
 * @version 1.2 12/05/2010
 */

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;


public class FileIO {

	//Read and write Text files
	
	// Add File I/O********************************************************************	
	public static String[] txtdataread(String FileName) {
		try {
			BufferedReader br=new BufferedReader(new FileReader(FileName));
			ArrayList<String> list=new ArrayList<>();
			String s;
			while ((s=br.readLine())!=null) {
				list.add(s);
			}
			br.close();
			return list.toArray(new String[list.size()]);
		} catch (IOException e) {e.printStackTrace();}
		return new String[0];
	}
	

	public static String[] txtdatawrite(String FileName,String[] StrSqls,int Line) {
		try {
			PrintWriter pw=new PrintWriter(new BufferedWriter(new FileWriter(FileName)));
			for(int i=0;i<Line;i++){
				pw.println(StrSqls[i]);
			}
			pw.close(); 
		} catch (IOException e) { e.printStackTrace(); } 
		return null;
	}

	
	public static double [] Xread(String DataFile) {	
		int ni=0;
		try  {   
    		DataInputStream in=new DataInputStream(new BufferedInputStream(new FileInputStream(DataFile)));  
    		ni=in.readInt();
    		in.close();  
    	} catch (Exception e)   {e.printStackTrace();}
		double[]X=new double [ni];
		try{
    		DataInputStream in=new DataInputStream(new BufferedInputStream(new FileInputStream(DataFile)));  
			ni=in.readInt();
    		for (int i=0;i<ni;i++){
    				X[i]=in.readDouble();
    			}
    		in.close();  
    	} catch (Exception e)   {e.printStackTrace();}
    	//System.out.println(ni);
		return X;  
	}
	
	
	
	
	public static void XWrite(String WvdecFile, double []x)
	//Write a double vector to a Binary data file,the first integer item is the number of double items 
	   {
		   int j;
		   try  
   			{
			   DataOutputStream out=new DataOutputStream(   
   									new BufferedOutputStream(   
   									new FileOutputStream(WvdecFile)));
			   out.writeInt(x.length);//first item of each row is the number of items of x
			   for (j=0;j<x.length;j++)
			   	{
				   out.writeDouble(x[j]);   					
			   	}
			   out.close(); 
   			}
		   catch (Exception e)   
	    	{   
	    		e.printStackTrace();   
	    	}
		   return;
	   } 
   		
   		
   
}
