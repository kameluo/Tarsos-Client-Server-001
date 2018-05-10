package mypackagedemo4;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import marytts.util.math.MathUtils;

public class processExcel {
	static ArrayList<double[]> arraylistaverage=new ArrayList<double[]>();
	static ArrayList<double[]> arrayListMFCCReaTime=new ArrayList<double[]>();
	static int position;
	public static String processExcel(double[] mfccArrayDouble) {
		
		String[] categories={"babycrying","bird","doorbell","doorknock","dooropen","Footstep","glassbreaking","Shouts","traffic"};
		
		boolean matrixOrDiagonal=false;//this is a flag to indicate if we are going to use the whole sigma matrix or the diagonal,true means we are going to use the sigma diagonal,false means the whole matrix
		int numIterations=1;
		//int position;
		
		readExcel readexcel=new readExcel();
		
		if(matrixOrDiagonal){
				for (int category = 0; category < categories.length; category++) {
					double tmp = 0.0;
					for (int ngauss = 0; ngauss < 30; ngauss++) {
						for (int row=0;row<13;row++) {
							double prob=MathUtils.getGaussianPdfValue(mfccArrayDouble,readexcel.getMuArray1d(category,ngauss), readexcel.getSigmaArrayDiagonal(category,ngauss), MathUtils.getGaussianPdfValueConstantTerm(mfccArrayDouble.length,readexcel.getArrayDeterminantSigmaMatrices(category,ngauss)));
							//double prob=MathUtils.getGaussianPdfValue(mfccArrayDouble,getMuArray1d(getMuArray2d(excelSheets[category]),ngauss), getSigmaArrayDiagonal(getSigmaArraysDiagonal(excelSheets[category]),ngauss), MathUtils.getGaussianPdfValueConstantTerm(mfccArrayDouble.length, MathUtils.determinant(getSigmaArrays2d(getSigmaArrays3d(excelSheets[category]),ngauss))));
							double[] weights=readexcel.getComponentProportionElement(category);
							tmp += weights[ngauss] * prob;
						}
					}
					double[] logLikelihoods = new double[1];
					logLikelihoods[numIterations-1] += Math.log(tmp);
					System.out.println("prob is :"+logLikelihoods[numIterations - 1]);
				}
		}else{
			double[] probabilityArray=new double[9];
				for (int category = 0; category < categories.length; category++) {
					double tmp = 0.0;
					for (int ngauss = 0; ngauss < 30; ngauss++) {
						double prob1=MathUtils.getGaussianPdfValue(mfccArrayDouble,readexcel.getMuArray1d(category,ngauss),readexcel.getArrayDeterminantSigmaMatrices(category,ngauss),readexcel.getArrayInverseSigmaMatrices(category,ngauss));
						
						//double prob2=MathUtils.getGaussianPdfValue(mfccArrayDouble,readexcel.getMuArray1d(category,ngauss), readexcel.getArrayInverseSigmaMatrices(category,ngauss),MathUtils.getGaussianPdfValueConstantTerm(mfccArrayDouble.length, readexcel.getArrayDeterminantSigmaMatrices(category,ngauss)));
						
						System.out.println("prooop is :" + prob1+" category:"+categories[category]);
						double[] weights=readexcel.getComponentProportionElement(category);
						//tmp +=weights[ngauss]*prob1;
						
						if(prob1==Double.NaN || prob1==Double.NEGATIVE_INFINITY || prob1==Double.POSITIVE_INFINITY || prob1==0.0) {
							tmp +=0.0;
						}else {
							tmp +=weights[ngauss]*prob1;
						}
						
						//System.out.println("----------------------------->"+tmp);
					}
					double[] logLikelihoods = new double[1];
					if(tmp==Double.NaN || tmp==Double.NEGATIVE_INFINITY || tmp==Double.POSITIVE_INFINITY || tmp==0.0) {
					logLikelihoods[numIterations-1] +=0.0;
					}else {
					logLikelihoods[numIterations-1] +=Math.log(tmp);
					}
					probabilityArray[category]=logLikelihoods[numIterations-1];
					//System.out.println("looklikle is :" +categories[category] + " is  :"+tmp);
				}
				double max=probabilityArray[0];
				for(int i=1;i<probabilityArray.length;i++) {
			        if(max<probabilityArray[i]) {
			        	position=i;
			            max=probabilityArray[i];
			        }
				}
				if (max > 0.1) {
					System.out.println("prop of "+categories[position] +" is :" +probabilityArray[position]);
					}
		}
		//return null;
		return categories[position];
	}
	public static void send(double[]array) {
		arraylistaverage.add(array);
	}
	
	public static void sendRealTime(double[]array) {
		arrayListMFCCReaTime.add(array);
	}
	public static double[] averageAndClear() {
		//deleteMaxMin();
		int allength=arraylistaverage.size();
		double[] arraywithoutdivide=new double[13];
		for(int i=0;i<allength;i++) {
			double[] arrayinsideal=arraylistaverage.get(i);
			for(int insidearray=0;insidearray<13;insidearray++) {
				arraywithoutdivide[insidearray]+=arrayinsideal[insidearray];
			}
		}
		for(int k=0;k<13;k++) {
			arraywithoutdivide[k]=arraywithoutdivide[k]/allength;	
		}
		//arraylistaverage.clear();
		return arraywithoutdivide;
	}
	public static void deleteMaxMin() {
		double max=0.0;
		int indexMax=0;
		double min=0.0;
		int indexMin=0;
		for(int i=0;i<arraylistaverage.size();i++) {
			double[] array=arraylistaverage.get(i);
			double firstnumber=0f;
			firstnumber=array[0];
				if(firstnumber>max){
					max=firstnumber;
					indexMax=i;
				}
		}
		arraylistaverage.remove(indexMax);
		for(int i=0;i<arraylistaverage.size();i++) {
			double[] array=arraylistaverage.get(i);
			double firstnumber=0f;
			firstnumber=array[0];
				if(firstnumber<min){
					min=firstnumber;
					indexMin=i;
				}
		}
		arraylistaverage.remove(indexMin);
	}
	public static double[] averageAndClearRealTime() {
		int allength=arrayListMFCCReaTime.size();
		double[] arraywithoutdivide=new double[13];
		for(int i=0;i<allength;i++) {
			double[] arrayinsideal=arrayListMFCCReaTime.get(i);
			for(int insidearray=0;insidearray<13;insidearray++) {
				arraywithoutdivide[insidearray]+=arrayinsideal[insidearray];
			}
		}
		for(int k=0;k<13;k++) {
			arraywithoutdivide[k]=arraywithoutdivide[k]/allength;	
		}
		arrayListMFCCReaTime.clear();
		return arraywithoutdivide;
	}
}