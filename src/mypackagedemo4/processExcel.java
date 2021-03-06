package mypackagedemo4;

import java.util.ArrayList;

import marytts.util.math.MathUtils;

public class processExcel {
	static ArrayList<double[]> arraylistaverage=new ArrayList<double[]>();
	static ArrayList<double[]> arrayListMFCCReaTime=new ArrayList<double[]>();
	static int position;
	static double logLikelihoods=0.0;
	static double tmp = 0.0;
	public static String processExcel(double[] mfccArrayDouble) {
		String[] categories={"babycrying","doorbell","doorknock"};
		readExcel3Categories readexcel=new readExcel3Categories();
		
			double[] probabilityArray=new double[3];
				for (int category = 0; category < categories.length; category++) {
					for (int ngauss = 0; ngauss < 26; ngauss++) {
						double prob1=MathUtils.getGaussianPdfValue(mfccArrayDouble,readexcel.getMuArray1d(category,ngauss),readexcel.getArrayDeterminantSigmaMatrices(category,ngauss),readexcel.getArrayInverseSigmaMatrices(category,ngauss));
						double[] weights=readexcel.getComponentProportionElement(category);
						if(prob1==Double.NaN || prob1==Double.POSITIVE_INFINITY || prob1==Double.NEGATIVE_INFINITY ||prob1==0.0) {
							tmp += 0.0;
							System.out.println("temp Adds 0.0-------> "+tmp);
						}else {
							tmp += weights[ngauss]*prob1;
							System.out.println("temp add some value-------> "+tmp);
						}
						if(tmp==Double.NaN || tmp==Double.POSITIVE_INFINITY || tmp==Double.NEGATIVE_INFINITY || tmp==0.0) 
						{
							logLikelihoods+=0.0;
						}else {
							//System.out.println("temp-------> "+tmp);
							logLikelihoods += Math.log(tmp);
						}
					}//end of ngauss loop
					//probabilityArray[category]=Math.abs(logLikelihoods);
					probabilityArray[category]=logLikelihoods;
					System.out.println("teeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeemp"+tmp);
					System.out.println("log of the category"+logLikelihoods);
					tmp = 0.0;
					logLikelihoods=0.0;
				}//end of the category loop
				double max=Math.abs(probabilityArray[0]);
				for(int i=0;i<probabilityArray.length;i++) {
					System.out.println("end loop is :" + probabilityArray[i]);
			        if(max<=Math.abs(probabilityArray[i])) {
			        	position=i;
			            max=Math.abs(probabilityArray[i]);
			        }
				}
				if (max > 0.1) {
					System.out.println("prop of "+categories[position] +" is :" +probabilityArray[position]);
				}else {
					System.out.println("-------------------------------------------------  NADA   --------------------------------------");
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