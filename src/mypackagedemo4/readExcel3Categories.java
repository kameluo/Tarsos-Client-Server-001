package mypackagedemo4;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import marytts.util.math.MathUtils;

public class readExcel3Categories {
	
	String[] excelSheets = {"f1.xls","f2.xls","f3.xls"};
	
	//Reading All The Excel Sheets
	static double[][][][] arraysSigmaMatrices=new double[3][5][13][13];
	static double[][][][] arrayDeterminantSigmaMatrices=new double[3][5][1][1];
	static double[][][][] arrayInverseSigmaMatrices=new double[3][5][13][13];
	static double[][][][] arraysSigmaDiagonalMatrices=new double[3][5][1][13];
	static double[][][] arraysMuMatrices=new double[3][5][13];
	static double[][][] arraysComponentProportionalMatrices=new double[3][1][5];
	
	public void readExcelsheets() throws FileNotFoundException, IOException {
		
		System.out.println("Wait to Read The Files...");
		arraysSigmaMatrices();
		arrayDeterminantSigmaMatrices();
		arrayinverseSigmaMatrices();
		//arraysSigmaDiagonalMatrices();  
		arraysMuMatrices();
		arraysComponentProportionalMatrices();
		System.out.println("Done");
	}
	public void arraysSigmaMatrices() throws FileNotFoundException, IOException {
		for(int catrgory = 0; catrgory < excelSheets.length; catrgory++) {
			HSSFWorkbook workbook=new HSSFWorkbook(new FileInputStream(excelSheets[catrgory]));//to be able to create everything in the excel sheet
			for(int ngauss = 0; ngauss < 5; ngauss++){
				String sheetname="Sigma"+(String.valueOf(ngauss+1));//adding the index to the sheet name
	            HSSFSheet sheet=workbook.getSheet(sheetname);//getting the sheet
				for(int row= 0; row < 13; row++) {
					for(int column= 0; column < 13; column++) {
						HSSFRow rows=sheet.getRow(row);
						arraysSigmaMatrices[catrgory][ngauss][row][column]=rows.getCell(column).getNumericCellValue();
					}
				}
			}	
			workbook.close();
		}
	}
	
	public void arrayDeterminantSigmaMatrices() throws FileNotFoundException, IOException {
		for(int catrgory = 0; catrgory < excelSheets.length; catrgory++) {
			HSSFWorkbook workbook=new HSSFWorkbook(new FileInputStream(excelSheets[catrgory]));//to be able to create everything in the excel sheet
			for(int ngauss = 0; ngauss < 5; ngauss++){
				String sheetname="determinantSigma"+(String.valueOf(ngauss+1));//adding the index to the sheet name
	            HSSFSheet sheet=workbook.getSheet(sheetname);//getting the sheet
				int column= 0;
				int row= 0;
						HSSFRow rows=sheet.getRow(row);
						arrayDeterminantSigmaMatrices[catrgory][ngauss][row][column]=rows.getCell(column).getNumericCellValue();
			}
			workbook.close();
		}
	}			

	public void arrayinverseSigmaMatrices() throws FileNotFoundException, IOException {
		for(int catrgory = 0; catrgory < excelSheets.length; catrgory++) {
			HSSFWorkbook workbook=new HSSFWorkbook(new FileInputStream(excelSheets[catrgory]));//to be able to create everything in the excel sheet
			for(int ngauss = 0; ngauss < 5; ngauss++){
				String sheetname="inverseSigma"+(String.valueOf(ngauss+1));//adding the index to the sheet name
	            HSSFSheet sheet=workbook.getSheet(sheetname);//getting the sheet
				for(int row= 0; row < 13; row++) {	
					for(int column= 0; column < 13; column++) {
						HSSFRow rows=sheet.getRow(row);
						arrayInverseSigmaMatrices[catrgory][ngauss][row][column]=rows.getCell(column).getNumericCellValue();
					}
				}
			}
			workbook.close();
		}
	}
	
	public void arraysSigmaDiagonalMatrices() throws FileNotFoundException, IOException {
		for(int catrgory = 0; catrgory < excelSheets.length; catrgory++) {
			HSSFWorkbook workbook=new HSSFWorkbook(new FileInputStream(excelSheets[catrgory]));//to be able to create everything in the excel sheet
			for(int ngauss = 0; ngauss < 5; ngauss++){
				String sheetname="SigmaDiagonal"+(String.valueOf(ngauss+1));//adding the index to the sheet name
	            HSSFSheet sheet=workbook.getSheet(sheetname);//getting the sheet
					for(int column= 0; column < 13; column++) {
						HSSFRow row=sheet.getRow(0);
						arraysSigmaDiagonalMatrices[catrgory][ngauss][0][column]=row.getCell(column).getNumericCellValue();
					}
			}
			workbook.close();
		}
	}
	
	public void arraysMuMatrices() throws FileNotFoundException, IOException {
		for(int catrgory = 0; catrgory < excelSheets.length; catrgory++) {
			HSSFWorkbook workbook=new HSSFWorkbook(new FileInputStream(excelSheets[catrgory]));//to be able to create everything in the excel sheet
			String sheetname="mu";//adding the index to the sheet name
            HSSFSheet sheet=workbook.getSheet(sheetname);//getting the sheet
			for(int row= 0; row < 5; row++) {
				for(int column= 0; column < 13; column++) {
					HSSFRow rows=sheet.getRow(row);
					arraysMuMatrices[catrgory][row][column]=rows.getCell(column).getNumericCellValue();
				}
			}
			workbook.close();
		}
	}
	public void arraysComponentProportionalMatrices() throws FileNotFoundException, IOException {
		for(int catrgory = 0; catrgory < excelSheets.length; catrgory++) {
			HSSFWorkbook workbook=new HSSFWorkbook(new FileInputStream(excelSheets[catrgory]));//to be able to create everything in the excel sheet
			String sheetname="ComponentProportion";//adding the index to the sheet name
            HSSFSheet sheet=workbook.getSheet(sheetname);//getting the sheet
			for(int column= 0; column < 5; column++) {
				HSSFRow rows=sheet.getRow(0);
				arraysComponentProportionalMatrices[catrgory][0][column]=rows.getCell(column).getNumericCellValue();
			}
			workbook.close();
		}
	}


	public double[][] getSigmaArrays2d(int category,int Sigma){
		//sigma is an integer from 0 to 29 to indicate which matrix do we want from that category(we have 5 matrices)
		double[][] arraysigma2d=new double[13][13];
		for(int row=0;row<=12;row++) {
			for(int column=0;column<=12;column++) {
				arraysigma2d[row][column]=arraysSigmaMatrices[category][Sigma][row][column];
			}
		}
		return arraysigma2d;
	}
	
	public double[] getMuArray1d(int category,int row) {
		double[] getMuArray1d=new double[13];
		for(int column=0;column<=12;column++) {
			getMuArray1d[column]=arraysMuMatrices[category][row][column];
		}
	return getMuArray1d;
	}

	public double[] getComponentProportionElement(int category) {
		double[] componentArrayElement=new double[5];
		for(int column=0;column<=4;column++) {
			componentArrayElement[column]=arraysComponentProportionalMatrices[category][0][column];
		}
		return componentArrayElement;
	}

	public double[] getSigmaArrayDiagonal(int category,int sigma) {
		double[] arraySigmaDiagonal=new double[13];
		for(int column=0;column<=12;column++) {
			arraySigmaDiagonal[column]=arraysSigmaDiagonalMatrices[category][sigma][0][column];
		}
	return arraySigmaDiagonal;
	}
	
	public double getArrayDeterminantSigmaMatrices(int category,int sigma) {
		double arraySigmaDiagonal=arrayDeterminantSigmaMatrices[category][sigma][0][0];
		return arraySigmaDiagonal;
	}
	
	public double[][] getArrayInverseSigmaMatrices(int category,int Sigma){
		double[][] arrayInverseSigma2d=new double[13][13];
		for(int row=0;row<=12;row++) {
			for(int column=0;column<=12;column++) {
				arrayInverseSigma2d[row][column]=arrayInverseSigmaMatrices[category][Sigma][row][column];
			}
		}
		return arrayInverseSigma2d;
	}	
}
