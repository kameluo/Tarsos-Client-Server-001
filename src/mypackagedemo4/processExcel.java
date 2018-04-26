package mypackagedemo4;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import marytts.util.math.MathUtils;

public class processExcel {
	static ArrayList<float[]> arraylistaverage=new ArrayList<float[]>();
	public static String processExcel(double[] mfccArrayDouble) {
		
		String[] categories={"babycrying","bird","doorbell","doorknock","dooropen","Footstep","glassbreaking","Shouts","traffic"};
		
		boolean matrixOrDiagonal=false;//this is a flag to indicate if we are going to use the whole sigma matrix or the diagonal,true means we are going to use the sigma diagonal,false means the whole matrix
		int numIterations=1;
		double postion=0.0;
		
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
					double tmp = 0;
					for (int ngauss = 0; ngauss < 30; ngauss++) {
						double prob1=MathUtils.getGaussianPdfValue(mfccArrayDouble,readexcel.getMuArray1d(category,ngauss),readexcel.getArrayDeterminantSigmaMatrices(category,ngauss),readexcel.getArrayInverseSigmaMatrices(category,ngauss));
						
						double prob2=MathUtils.getGaussianPdfValue(mfccArrayDouble,readexcel.getMuArray1d(category,ngauss), readexcel.getArrayInverseSigmaMatrices(category,ngauss),MathUtils.getGaussianPdfValueConstantTerm(mfccArrayDouble.length, readexcel.getArrayDeterminantSigmaMatrices(category,ngauss)));
						
						//System.out.println("prooop is :" + prob2);
						double[] weights=readexcel.getComponentProportionElement(category);
						tmp += weights[ngauss] * prob1;
					}
					System.out.println("prop of "+categories[category] +" is :" + tmp);
					double[] logLikelihoods = new double[1];
					logLikelihoods[numIterations - 1] += Math.log(tmp);
					probabilityArray[category]=logLikelihoods[numIterations-1];
					//System.out.println("looklikle is :" + probabilityArray[category]);
				}
				double max=probabilityArray[0];
				for(int i=1;i<probabilityArray.length;i++) {
			        if(max<probabilityArray[i]) {
			        	postion=(double)i;
			            max=probabilityArray[i];
			        }
				}
		}
		return categories[(int)postion];
	}
	public static void send(float[]array) {
		arraylistaverage.add(array);
	}
	public static float[] averageAndClear() {
		//deleteMaxMin();
		int allength=arraylistaverage.size();
		float[] arraywithoutdivide=new float[13];
		for(int i=0;i<allength;i++) {
			float[] arrayinsideal=arraylistaverage.get(i);
			for(int insidearray=0;insidearray<13;insidearray++) {
				arraywithoutdivide[insidearray]+=arrayinsideal[insidearray];
			}
		}
		for(int k=0;k<13;k++) {
			arraywithoutdivide[k]=arraywithoutdivide[k]/allength;	
		}
		arraylistaverage.clear();
		return arraywithoutdivide;
	}
	public static void deleteMaxMin() {
		float max=0f;
		int indexMax=0;
		float min=0f;
		int indexMin=0;
		for(int i=0;i<arraylistaverage.size();i++) {
			float[] array=arraylistaverage.get(i);
			float firstnumber=0f;
			firstnumber=array[0];
				if(firstnumber>max){
					max=firstnumber;
					indexMax=i;
				}
		}
		arraylistaverage.remove(indexMax);
		for(int i=0;i<arraylistaverage.size();i++) {
			float[] array=arraylistaverage.get(i);
			float firstnumber=0f;
			firstnumber=array[0];
				if(firstnumber<min){
					min=firstnumber;
					indexMin=i;
				}
		}
		arraylistaverage.remove(indexMin);
	}
	
	
	
}