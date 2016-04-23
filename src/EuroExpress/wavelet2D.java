package EuroExpress;

/*This class perfoms wavelet transformation on 2D matrix. 
 * The input are imagematrix, level and db name,e.g. db2, db3.. The output is wavelet coefficients represented as vectors
 * @liangxiu han;
 * @version 1.0, 
 * 02/04/2009
 * @version 1.2,
 * 07/04/2009
 * 12/05/2010
 */

public class wavelet2D {

	public static void main(String args[]){
		//int mat1[][]={{16,17,18,19},{13,14,15,18},{11,12,13,13},{0,0,0,1},{1,2,3,4}};
		int mat1[][]={{16,17,18,19},{13,14,15,20},{11,12,13,98},{7,9,45,67},{7,9,45,89},{7,9,45,67},{16,17,18,69},{16,17,18,69},{16,17,18,69}};
		int level=2;
		int dbname=3;
		double C[];
		C=lwvimg2(mat1,level,dbname);
		for(int i=0;i<mat1.length*mat1[0].length;i++){
		System.out.println("\t"+C[i]);
		}
	}
	
	public static double[] lwvimg2 (int[][]imgmatrix,int level,int dbname) {
		double []C=new double [imgmatrix.length*imgmatrix[0].length];
		double [][]x=new double[imgmatrix.length][imgmatrix[0].length];
		double [][] xDec =new double [imgmatrix.length][imgmatrix[0].length];
		for(int i=0;i<imgmatrix.length;i++){
			for (int j=0;j<imgmatrix[0].length;j++){
				x[i][j]=imgmatrix[i][j];
			}
		}
		xDec = lwt2db(x,dbname,level);
		int k=0;
		for(int i=0;i<xDec.length;i++){
			for (int j=0;j<xDec[0].length;j++){
				C[k]=xDec[i][j];
				k++;
			}
		}
		return C;
	}
	
	public static double [][] lwt2db(double [][]x,int dbname,int level){
		int LSr=dbname+1;//dbname is the number followed by 'db',such as 1 for db1,2 for db2...
		double LSend1=0.0,LSend2=0.0;
		double [][]liftFILT=new double[LSr][3];
		int []llf=new int[LSr];
		int []DF=new int[LSr];
		for (int i=0;i<LSr;i++){  //Initialize liftFILT
			for (int j=0;j<3;j++){
				liftFILT[i][j]=0.0;
			}
		}
		switch (dbname){
	    //== db1 ============================================================%
	    case 1:
	    	liftFILT[0][0]=-1;liftFILT[1][0]=0.5;
	        llf[0]=1;llf[1]=1;
	        DF[0]=0;DF[1]=0;
	        LSend1=Math.sqrt(2.0);
	        LSend2=Math.sqrt(2.0)/2.0;
	        break;
	    //== db2 ============================================================%
	    case 2:
	    	liftFILT[0][0]=-Math.sqrt(3);liftFILT[1][0]=(Math.sqrt(3)-2)/4.0;
	    	liftFILT[1][1]=(Math.sqrt(3))/4.0;
	    	liftFILT[2][0]=1.0;
	    	llf[0]=1;llf[1]=2;llf[2]=1;
	    	DF[0]=0;DF[1]=1;DF[2]=-1;
	    	LSend1=(Math.sqrt(3.0)+1.0)/Math.sqrt(2.0);
	        LSend2=(Math.sqrt(3.0)-1.0)/Math.sqrt(2.0);	
	        break;
	    //== db3 ============================================================%
	    case 3:
	    	liftFILT[0][0]=-2.4254972439123361;
	    	liftFILT[1][0]=-0.0793394561587384; liftFILT[1][1]=0.3523876576801823;
	    	liftFILT[2][0]=2.8953474543648969; liftFILT[2][1]=-0.5614149091879961;
	    	liftFILT[3][0]=0.0197505292372931;
	    	llf[0]=1;llf[1]=2;llf[2]=2;llf[3]=1;
	    	DF[0]=0;DF[1]=1;DF[2]=-1;DF[3]=2;
	    	LSend1=2.3154580432421348;
	        LSend2=0.4318799914853075;
	        break;
	    //== db4 ============================================================%
	    case 4:
	    	liftFILT[0][0]=-0.3222758879971411;
	    	liftFILT[1][0]=-1.1171236051605939; liftFILT[1][1]=-0.3001422587485443;
	    	liftFILT[2][0]=-0.0188083527262439; liftFILT[2][1]=0.1176480867984784;
	    	liftFILT[3][0]=2.1318167127552199; liftFILT[3][1]= 0.6364282711906594;
	    	liftFILT[4][0]=-0.4690834789110281; liftFILT[4][1]= 0.1400392377326117;liftFILT[4][2]=-0.0247912381571950;
	    	llf[0]=1;llf[1]=2;llf[2]=2;llf[3]=2;llf[4]=3;
	    	DF[0]=1;DF[1]=0;DF[2]=2;DF[3]=0;DF[4]=0;
	    	LSend1=0.7341245276832514;
	    	LSend2=1.3621667200737697;
	    	break;
	    case 5:
	    	liftFILT[0][0]=-0.2651451428113514;
	    	liftFILT[1][0]=0.9940591341382633; liftFILT[1][1]=0.2477292913288009;
	    	liftFILT[2][0]=-0.5341246460905558; liftFILT[2][1]=0.2132742982207803;
	    	liftFILT[3][0]=0.2247352231444452; liftFILT[3][1]=-0.7168557197126235;
	    	liftFILT[4][0]=-0.0775533344610336; liftFILT[4][1]=0.0121321866213973;
	    	liftFILT[5][0]=-0.0357649246294110; 
	    	llf[0]=1;llf[1]=2;llf[2]=2;llf[3]=2;llf[4]=3;llf[5]=1;
	    	DF[0]=1;DF[1]=0;DF[2]=0;DF[3]=2;DF[4]=-2;DF[5]=3;
	    	LSend1=0.7632513182465389;
	        LSend2=1.3101844387211246;
	        break;
	    case 6:
	    	liftFILT[0][0]=-4.4344683000391223;
	    	liftFILT[1][0]=-0.0633131925095066; liftFILT[1][1]=0.2145934499409130;
	    	liftFILT[2][0]=9.9700156175718320; liftFILT[2][1]=-4.4931131753641633;
	    	liftFILT[3][0]=-0.0236634936395882; liftFILT[3][1]=0.0574139367993266;
	    	liftFILT[4][0]= 2.3564970162896977; liftFILT[4][1]=-0.6787843541162683;
	    	liftFILT[5][0]= -0.0009911655293238; liftFILT[5][1]=0.0071835631074942;
	    	liftFILT[6][0]=0.0941066741175849; 
	    	llf[0]=1;llf[1]=2;llf[2]=2;llf[3]=2;llf[4]=2;llf[5]=2;llf[6]=1;
	    	DF[0]=0;DF[1]=1;DF[2]=-1;DF[3]=3;DF[4]=-3;DF[5]=5;DF[6]=-5;
	    	LSend1=3.1214647228121661;
	    	LSend2=0.3203624223883869;
	    	break;
	    case 7:
	    	liftFILT[0][0]=5.0934984843051252;
	    	liftFILT[1][0]=-0.1890420920712265; liftFILT[1][1]=0.05739872598827620;
	    	liftFILT[2][0]=5.9592087615113751; liftFILT[2][1]=-12.2854449956285200;
	    	liftFILT[3][0]=-0.0604278631256078; liftFILT[3][1]=0.0291354832685777;
	    	liftFILT[4][0]= 1.5604402591648248; liftFILT[4][1]=-3.9707106658519669;
	    	liftFILT[5][0]= -0.0126913773028362; liftFILT[5][1]=0.0033065734202625;
	    	liftFILT[6][0]= 0.0508158836098717; liftFILT[6][1]=-0.4141984501693177;
	    	liftFILT[7][0]=-0.0004062144890730; 
	    	llf[0]=1;llf[1]=2;llf[2]=2;llf[3]=2;llf[4]=2;llf[5]=2;llf[6]=2;llf[7]=1;
	    	DF[0]=0;DF[1]=0;DF[2]=2;DF[3]=-2;DF[4]=4;DF[5]=-4;DF[6]=6;DF[7]=-6;
	    	LSend1=0.2990107076865977;
	    	LSend2=3.3443618381992222;
	    	break;
	    case 8:
	    	liftFILT[0][0]=-5.7496416141714990;
	    	liftFILT[1][0]=-0.0522692017330962; liftFILT[1][1]=0.16881724365694210;
	    	liftFILT[2][0]=14.5428210043618850; liftFILT[2][1]=-7.4021068366100549;
	    	liftFILT[3][0]=-0.0324020739512596; liftFILT[3][1]=0.0609092564633227;
	    	liftFILT[4][0]= 5.8187164907231610; liftFILT[4][1]=-2.7556987881059287;
	    	liftFILT[5][0]= 0.9452952681157910; liftFILT[5][1]=0.2420216844324576;
	    	liftFILT[6][0]= 0.0001888402536823; liftFILT[6][1]=-0.0018038158742157;
	    	liftFILT[7][0]= -0.9526138318957663; liftFILT[7][1]=-0.2241381624167550;
	    	liftFILT[8][0]=1.0497432943790195; liftFILT[8][1]=-0.2469917331775993;liftFILT[8][2]=0.0271973973533717;
	    	llf[0]=1;llf[1]=2;llf[2]=2;llf[3]=2;llf[4]=2;llf[5]=2;llf[6]=2;llf[7]=2;llf[8]=3;
	    	DF[0]=0;DF[1]=1;DF[2]=-1;DF[3]=3;DF[4]=-3;DF[5]=5;DF[6]=-3;DF[7]=5;DF[8]=-5;
	    	LSend1=3.5493622541356347;
	    	LSend2=0.2817407546481972;
	    	break;

		}
		///////////////////////////////////////////////
		int hlfc=x[0].length/2;
		int hlfLc,hlfHc;
		if(2*hlfc==x[0].length){
			hlfLc=hlfc;//even columns 
			hlfHc=hlfc;
		}
		else{
			hlfLc=hlfc+1;//odd columns
			hlfHc=hlfc;
		}
		double [][]L=new double[x.length][hlfLc];
		double [][]H=new double[x.length][hlfHc];
		// Splitting.
		for(int i=0;i<x.length;i++){
			for(int j=0;j<x[0].length/2+1;j++){
				if((2*j)<x[0].length){
					L[i][j]=x[i][2*j];
					if((2*j+1)<x[0].length){
						H[i][j]=x[i][2*j+1];
					}
				}
			}
		}
		for(int j=0;j<H[0].length;j++){
		}
		int []sL ={L.length,L[0].length};
		int []sH ={H.length,H[0].length};

		//Lifting.
		double []liftF=new double[3];
		int llts=0;
		int DFs=0;
		int liftTYPE=2;
		if(LSr==8){
			liftTYPE=1;//db7 is different to others
		}
		int option=1;//1: row
		for (int i=0;i<LSr;i++){
			for(int j=0;j<3;j++){
				liftF[j]=liftFILT[i][j];
			}
			llts=llf[i];
			DFs=DF[i];		
			switch (liftTYPE){
			case 1: 
				double [][]FH=new double[sL[0]][sL[1]];
				FH=lsup(option,H,liftF,llts,DFs,sL);
				for(int ii=0;ii<sL[0];ii++){
					for(int j=0;j<sL[1];j++){
						L[ii][j]=L[ii][j]+FH[ii][j];
					}
				}
				liftTYPE=2;
				break;
			case 2: 
				double [][]FL=new double[sH[0]][sH[1]];
				FL=lsup(option,L,liftF,llts,DFs,sH);
				for(int ii=0;ii<sH[0];ii++){
					for(int j=0;j<sH[1];j++){
						H[ii][j]=H[ii][j]+FL[ii][j];
					}
				}
				liftTYPE=1;
				break;
			}
			
		}
		// Splitting.
		int llfr=L.length/2;
		int llfLr,llfHr;
		if(2*llfr==L.length){
			llfLr=llfr;
			llfHr=llfr;
		}
		else{
			llfLr=llfr+1;
			llfHr=llfr;
		}
		int hlfr=H.length/2;
		int hlfLr,hlfHr;
		if(2*hlfr==H.length){
			hlfLr=hlfr;
			hlfHr=hlfr;
		}
		else{
			hlfLr=hlfr+1;
			hlfHr=hlfr;
		}
		double [][]a=new double[llfLr][L[0].length];
		double [][]h=new double[llfHr][L[0].length];
		double [][]v=new double[hlfLr][H[0].length];
		double [][]d=new double[hlfHr][H[0].length];
		for(int j=0;j<L[0].length;j++){
			for(int i=0;i<llfLr;i++){
				if((2*i)<L.length){
					a[i][j]=L[2*i][j];
					if((2*i+1)<L.length){
						h[i][j]=L[2*i+1][j];
					}
				}	
			}
		}
		int []sa ={a.length,a[0].length};
		int []sh ={h.length,h[0].length};
		
		for(int j=0;j<H[0].length;j++){
			for(int i=0;i<hlfLr;i++){
				if((2*i)<H.length){
					v[i][j]=H[2*i][j];
					if((2*i+1)<H.length){
						d[i][j]=H[2*i+1][j];
					}
				}	
			}
		}
		int []sv ={v.length,v[0].length};
		int []sd ={d.length,d[0].length};
		
		// Lifting.
		liftTYPE=2;
		if(LSr==8){
			liftTYPE=1;//db7 is different to others
		}
		for (int i=0;i<LSr;i++){
			for(int j=0;j<3;j++){
				liftF[j]=liftFILT[i][j];
			}
			llts=llf[i];
			DFs=DF[i];		
			option=2;
			switch (liftTYPE){
			case 1:
				double [][]Fh=new double[sa[0]][sa[1]];
				Fh=lsup(option,h,liftF,llts,DFs,sa);
				for(int ii=0;ii<sa[0];ii++){
					for(int j=0;j<sa[1];j++){
						a[ii][j]=a[ii][j]+Fh[ii][j];
					}
				}
				double [][]Fd=new double[sv[0]][sv[1]];
				Fd=lsup(option,d,liftF,llts,DFs,sv);
				for(int ii=0;ii<sv[0];ii++){
					for(int j=0;j<sv[1];j++){
						v[ii][j]=v[ii][j]+Fd[ii][j];
					}
				}
				liftTYPE=2;
				break;
			case 2: 
				double [][]Fa=new double[sh[0]][sh[1]];
				Fa=lsup(option,a,liftF,llts,DFs,sh);
				for(int ii=0;ii<sh[0];ii++){
					for(int j=0;j<sh[1];j++){
						h[ii][j]=h[ii][j]+Fa[ii][j];
					}
				}
				double [][]Fv=new double[sd[0]][sd[1]];
				Fv=lsup(option,v,liftF,llts,DFs,sd);
				for(int ii=0;ii<sd[0];ii++){
					for(int j=0;j<sd[1];j++){
						d[ii][j]=d[ii][j]+Fv[ii][j];
					}
				}
				liftTYPE=1;
				break;
			}	
		}
		//Normalization.
		for(int i=0;i<a.length;i++){
			for(int j=0;j<a[0].length;j++){
				a[i][j]=a[i][j]*LSend1*LSend1;
			}
		}
		for(int i=0;i<h.length;i++){
			for(int j=0;j<h[0].length;j++){
				h[i][j]=h[i][j]*LSend1*LSend2;
			}
		}
		for(int i=0;i<v.length;i++){
			for(int j=0;j<v[0].length;j++){
				v[i][j]=v[i][j]*LSend2*LSend1;
			}
		}
		for(int i=0;i<d.length;i++){
			for(int j=0;j<d[0].length;j++){
				d[i][j]=d[i][j]*LSend2*LSend2;
			}
		}
		// Recursion if level > 1.
		if (level>1){
		   level = level-1;
		   a = lwt2db(a,dbname,level);
		}
		// Store in place.
		
		for(int i=0;i<a.length;i++){
			for(int j=0;j<a[0].length;j++){
				x[2*i][2*j]=a[i][j];
				}
		}
		for(int i=0;i<h.length;i++){
			for(int j=0;j<h[0].length;j++){
				x[2*i+1][2*j]=h[i][j];}
		}
		for(int i=0;i<v.length;i++){
			for(int j=0;j<v[0].length;j++){
				x[2*i][2*j+1]=v[i][j];}
		}
		for(int i=0;i<d.length;i++){
			for(int j=0;j<d[0].length;j++){
				x[2*i+1][2*j+1]=d[i][j];}
		}		
		return x;
	}
	/////////////////////////////
	
	public static double [][]lsup(int option,double [][]x,double []F,int lF,int DF,int[]S){
		//lF:length of F;1,2,or 3
		int []sx ={x.length,x[0].length};
		double [][]y=new double[sx[0]][sx[1]];
		double [][]Y=new double[S[0]][S[1]];
		for (int i=0;i<sx[0];i++){
			for(int j=0;j<sx[1];j++){
				y[i][j]=0.0;
			}
		}
		switch (option){
	     
	  case 1:
		  Y=lsM(x,y,Y,F,lF,DF,S,sx);
	      break;
	  case 2:
		  x=transpose(x);
		  y=transpose(y);
		  Y=transpose(Y);
		  S=dimstranspose(S);
		  sx=dimstranspose(sx);
		  Y=lsM(x,y,Y,F,lF,DF,S,sx);
		  x=transpose(x);
		  y=transpose(y);
		  Y=transpose(Y);
		  S=dimstranspose(S);
		  sx=dimstranspose(sx);
		}
		return Y;
	}
	
	public static double [][]lsM(double [][]x,double [][]y,double [][]Y,double []F,int lF,int DF,int[]S,int[]sx){
	int k=0;
	int jk=0;
	int d=0;
	for (int j=0;j<lF;j++){
  	  k=DF-j;
  	  double [][]t=new double[x.length][x[0].length];
  	  //System.out.println("x[0].length: \t"+x[0].length);
  	  for(int i=0;i<x.length;i++){
  		  for(int ij=0;ij<x[0].length;ij++){
  			  t[i][ij]=F[j]*x[i][ij];
  		  }
  	  }
        if(k>0){
      	  for(int i=0; i<t.length;i++){
      		  jk=0;
      		  for(int ij=k;ij<t[0].length;ij++){
      			 t[i][jk]=t[i][ij]; 
      			 jk++;
      		  }
      		  for(int ij=jk;ij<t[0].length;ij++){
      			  t[i][ij]=0;
      		  }
      	  }
        }
        else{
      	  if(k<0){
      		  if(-k<t[0].length){
      			  for(int i=0; i<t.length;i++){
      				for (int ij=t[0].length-1; ij>=-k;ij--){
    					  t[i][ij]=t[i][ij+k];
    				  }
    				  for(int ij=0;ij<-k;ij++){
    					  t[i][ij]=0;
    				  }
    			  }
      		  }
      		  else{
      			  for(int i=0; i<t.length;i++){
      				  if(-k>t[0].length){
      					  for(int ij=0;ij<t[0].length;ij++){
	        					  t[i][ij]=0;
      				  }}
      				  else{
      				  for(int ij=0;ij<-k;ij++){
      					  t[i][ij]=0;  
      				  }}
      			  }
      		  }
      		  }
      	  }
      	  for(int i=0;i<t.length;i++){
      		  for (int ij=0;ij<sx[1];ij++){
      			  y[i][ij]=y[i][ij]+t[i][ij];
      		  }
      	  }
        }
    d = S[1]-sx[1];
    if (d>0){
      for(int i=0;i<sx[0];i++){
      	for(int ij=0;ij<sx[1];ij++){
      		Y[i][ij]=y[i][ij];
      	}
      	for(int ij=sx[1];ij<S[1];ij++){
      		Y[i][ij]=0;
      	}
      }
     }
    else{
  	  if(d<=0){
  		  for(int i=0;i<sx[0];i++){
  			  for(int ij=0;ij<S[1];ij++){
  				  Y[i][ij]=y[i][ij];
  			  }
  		  }
  	  }
    }
	return Y;
	}
	public static double [][]transpose(double a[][]){
		double [][]b=new double [a[0].length][a.length];
		for(int i=0;i<a.length;i++){
			for(int j=0;j<a[0].length;j++){
				b[j][i]=a[i][j];
				}
		}
		return b;
		}
		
	
	public static int[] dimstranspose(int s[]){
		int []d=new int [2];
		d[0]=s[1];
		d[1]=s[0];
		return d;
	}	
}