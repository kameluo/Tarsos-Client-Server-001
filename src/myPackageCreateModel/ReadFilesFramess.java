package myPackageCreateModel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import javax.sound.sampled.UnsupportedAudioFileException;

import jvm.be.tarsos.dsp.io.jvm.AudioDispatcherFactory;
import core.be.tarsos.dsp.AudioDispatcher;
import core.be.tarsos.dsp.AudioEvent;
import core.be.tarsos.dsp.AudioProcessor;
import core.be.tarsos.dsp.mfcc.MFCC;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.FileOutputStream;

public class ReadFilesFramess {
static ArrayList<float[]> arraylistMFCCforOneAudioFile=new ArrayList<float[]>();
 
public static void main(String[] args) throws UnsupportedAudioFileException, IOException {
 
	File folderBabycrying = new File("C:\\Users\\usuario\\Desktop\\categories 16000\\baby");
	File folderBirds = new File("C:\\Users\\usuario\\Desktop\\categories 16000\\bird");
	File folderDoorbell = new File("C:\\Users\\usuario\\Desktop\\categories 16000\\doorbell");
	File folderDoorknock = new File("C:\\Users\\usuario\\Desktop\\categories 16000\\doorknock");
	File folderDooropenclose = new File("C:\\Users\\usuario\\Desktop\\categories 16000\\dooropen");
	File folderFootstep = new File("C:\\Users\\usuario\\Desktop\\categories 16000\\footstep");
	File folderGlassbreaking = new File("C:\\Users\\usuario\\Desktop\\categories 16000\\glass");
	File folderScream = new File("C:\\Users\\usuario\\Desktop\\categories 16000\\shouts");
	File folderTraffic = new File("C:\\Users\\usuario\\Desktop\\categories 16000\\traffic");

File[] folders={folderBabycrying,folderBirds,folderDoorbell,folderDoorknock,folderDooropenclose,folderFootstep,folderGlassbreaking,folderScream,folderTraffic};
float sampleRate=16000f;
float framesize=0.0125f;//25ms
    int bufferSize= Math.round(sampleRate*framesize);
    int bufferOverlap=(int) (0.015f * sampleRate);
   
    SXSSFWorkbook workbook=new SXSSFWorkbook();
   
for(int folderCounter=0;folderCounter<folders.length;folderCounter++){
File[] listOfFiles = folders[folderCounter].listFiles();
 
System.out.println("-------"+folders[folderCounter].getName());
 
 
 
for(int fileCounter=0;fileCounter<listOfFiles.length;fileCounter++){
 
File file=listOfFiles[fileCounter];
 
System.out.println(file.getName());
AudioDispatcher dispatcher = AudioDispatcherFactory.fromFile(file,bufferSize, bufferOverlap);
MFCC mfcc = new MFCC(bufferSize,sampleRate,13,20,300f,3700f);
dispatcher.addAudioProcessor(mfcc);
dispatcher.addAudioProcessor(new AudioProcessor() {
       
        @Override
        public boolean process(AudioEvent audioEvent) {
        float[] mfccArrayFloat = mfcc.getMFCC(); 
       
        sendArray(mfccArrayFloat);
            return true;
      }
        @Override
        public void processingFinished() {
       
       
        //System.out.println(arrayFinalAverage[0]+" "+arrayFinalAverage[1]+" "+arrayFinalAverage[2]+" "+arrayFinalAverage[3]+" "+arrayFinalAverage[4]+" "+arrayFinalAverage[5]+" "+arrayFinalAverage[6]+" "+arrayFinalAverage[7]+" "+arrayFinalAverage[8]+" "+arrayFinalAverage[9]+" "+arrayFinalAverage[10]+" "+arrayFinalAverage[11]+" "+arrayFinalAverage[12]);
        System.out.println("DONE");
        }
    });
    dispatcher.run();
}//end of the files loop
 
 
//when we want to create an excel sheet we have to create only one "workbook" to be able to create  different sheets,rows and cells ,and after finishing we will call the outputstream write method only one time to write all we have created in the sheets in one time
writeExcelSheet(folders[folderCounter].getName(),workbook);
}//end of the folder loop
 
 
 
//calling the outputstream write method after finishing saving the data in rows and column and different sheets 
FileOutputStream outputStream=new FileOutputStream("Mel Coef Excel Sheet Framessssss.xlsx");
workbook.write(outputStream);
outputStream.close();
}
 
public static void sendArray(float[] array) {
arraylistMFCCforOneAudioFile.add(array);
}
 
public static void writeExcelSheet(String categoryName,SXSSFWorkbook workbook) throws IOException {
//Workbook workbook=new HSSFWorkbook();
Sheet sheet= workbook.createSheet(categoryName);
    int allength=arraylistMFCCforOneAudioFile.size();
   
    for(int row=0;row<allength;row++){
    Row currentRow = sheet.createRow(row);
        for(int column = 0; column < 13; column++){
        Cell cell = currentRow.createCell(column);
            cell.setCellValue(arraylistMFCCforOneAudioFile.get(row)[column]);
        }
    }
   
    arraylistMFCCforOneAudioFile.clear();
}
 
 
}