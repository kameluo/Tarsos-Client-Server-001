package mypackagedemo4;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import marytts.util.math.MathUtils;

public class readExcel {
	String[] excelSheets = {"f1.xls","f2.xls","f3.xls","f4.xls","f5.xls","f6.xls","f7.xls","f8.xls","f9.xls"};
	
	//Reading All The Excel Sheets
	double[][][][] arraysSigmaMatrices=new double[9][30][13][13];
	double[][][][] arrayDeterminantSigmaMatrices=new double[9][30][1][1];
	double[][][][] arrayInverseSigmaMatrices=new double[9][30][13][13];
	double[][][][] arraysSigmaDiagonalMatrices=new double[9][30][1][13];
	double[][][] arraysMuMatrices=new double[9][30][13];
	double[][][] arraysComponentProportionalMatrices=new double[9][1][30];
	
	
	
	public void readExcelsheets() throws FileNotFoundException, IOException {
		System.out.println("Wait to Read The Files...");
		arraysSigmaMatrices();
		arrayDeterminantSigmaMatrices();
		arrayinverseSigmaMatrices();
		arraysSigmaDiagonalMatrices();
		arraysMuMatrices();
		arraysComponentProportionalMatrices();
		System.out.println("Done");
	}
	public void arraysSigmaMatrices() throws FileNotFoundException, IOException {
		for(int catrgory = 0; catrgory < excelSheets.length; catrgory++) {
			for(int ngauss = 0; ngauss < 30; ngauss++){
				for(int row= 0; row < 13; row++) {
					for(int column= 0; column < 13; column++) {
						HSSFWorkbook workbook=new HSSFWorkbook(new FileInputStream(excelSheets[catrgory]));//to be able to create everything in the excel sheet
						String sheetname="Sigma"+(String.valueOf(ngauss+1));//adding the index to the sheet name
			            HSSFSheet sheet=workbook.getSheet(sheetname);//getting the sheet
						HSSFRow rows=sheet.getRow(row);
						arraysSigmaMatrices[catrgory][ngauss][row][column]=rows.getCell(column).getNumericCellValue();
					}
				}
			}			
		}
	}
	
	public void arrayDeterminantSigmaMatrices() throws FileNotFoundException, IOException {
		for(int catrgory = 0; catrgory < excelSheets.length; catrgory++) {
			for(int ngauss = 0; ngauss < 30; ngauss++){
				for(int row= 0; row < 1; row++) {
					for(int column= 0; column < 1; column++) {
						HSSFWorkbook workbook=new HSSFWorkbook(new FileInputStream(excelSheets[catrgory]));//to be able to create everything in the excel sheet
						String sheetname="determinantSigma"+(String.valueOf(ngauss+1));//adding the index to the sheet name
			            HSSFSheet sheet=workbook.getSheet(sheetname);//getting the sheet
						HSSFRow rows=sheet.getRow(row);
						arrayDeterminantSigmaMatrices[catrgory][ngauss][row][column]=rows.getCell(column).getNumericCellValue();
					}
				}
			}			
		}
	}
	public void arrayinverseSigmaMatrices() throws FileNotFoundException, IOException {
		for(int catrgory = 0; catrgory < excelSheets.length; catrgory++) {
			for(int ngauss = 0; ngauss < 30; ngauss++){
				for(int row= 0; row < 13; row++) {	
					for(int column= 0; column < 13; column++) {
						HSSFWorkbook workbook=new HSSFWorkbook(new FileInputStream(excelSheets[catrgory]));//to be able to create everything in the excel sheet
						String sheetname="inverseSigma"+(String.valueOf(ngauss+1));//adding the index to the sheet name
			            HSSFSheet sheet=workbook.getSheet(sheetname);//getting the sheet
						HSSFRow rows=sheet.getRow(row);
						arrayInverseSigmaMatrices[catrgory][ngauss][row][column]=rows.getCell(column).getNumericCellValue();
					}
				}
			}
		}
	}
	
	public void arraysSigmaDiagonalMatrices() throws FileNotFoundException, IOException {
		for(int catrgory = 0; catrgory < excelSheets.length; catrgory++) {
			for(int ngauss = 0; ngauss < 30; ngauss++){
					for(int column= 0; column < 13; column++) {
						HSSFWorkbook workbook=new HSSFWorkbook(new FileInputStream(excelSheets[catrgory]));//to be able to create everything in the excel sheet
						String sheetname="SigmaDiagonal"+(String.valueOf(ngauss+1));//adding the index to the sheet name
			            HSSFSheet sheet=workbook.getSheet(sheetname);//getting the sheet
						HSSFRow row=sheet.getRow(0);
						arraysSigmaDiagonalMatrices[catrgory][ngauss][0][column]=row.getCell(column).getNumericCellValue();
					}
			}
		}
	}
	
	public void arraysMuMatrices() throws FileNotFoundException, IOException {
		for(int catrgory = 0; catrgory < excelSheets.length; catrgory++) {
			for(int row= 0; row < 30; row++) {
				for(int column= 0; column < 13; column++) {
					HSSFWorkbook workbook=new HSSFWorkbook(new FileInputStream(excelSheets[catrgory]));//to be able to create everything in the excel sheet
					String sheetname="mu";//adding the index to the sheet name
		            HSSFSheet sheet=workbook.getSheet(sheetname);//getting the sheet
					HSSFRow rows=sheet.getRow(row);
					arraysMuMatrices[catrgory][row][column]=rows.getCell(column).getNumericCellValue();
				}
			}
		}
	}
	public void arraysComponentProportionalMatrices() throws FileNotFoundException, IOException {
		for(int catrgory = 0; catrgory < excelSheets.length; catrgory++) {
			for(int column= 0; column < 30; column++) {
				HSSFWorkbook workbook=new HSSFWorkbook(new FileInputStream(excelSheets[catrgory]));//to be able to create everything in the excel sheet
				String sheetname="ComponentProportion";//adding the index to the sheet name
	            HSSFSheet sheet=workbook.getSheet(sheetname);//getting the sheet
				HSSFRow rows=sheet.getRow(0);
				arraysComponentProportionalMatrices[catrgory][0][column]=rows.getCell(column).getNumericCellValue();
			}
		}
	}


	public double[][] getSigmaArrays2d(int category,int Sigma){
		//sigma is an integer from 0 to 29 to indicate which matrix do we want from that category(we have 30 matrices)
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
		double[] componentArrayElement=new double[30];
		for(int column=0;column<=12;column++) {
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
