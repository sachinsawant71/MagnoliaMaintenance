package com.magnolia.excel;

import java.io.File;
import java.io.IOException;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableCell;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;


public class SyncExcelDataFinal {

	public void readAndProcess() throws IOException, RowsExceededException, WriteException  {
	    File inputWorkbook = new File("C:\\myData\\sas\\other\\magnolia\\financialStatements\\MagnoliaPropertyTaxInfo20Jan.xlsx");
	    File outputWorkbook = new File("C:\\myData\\sas\\other\\magnolia\\financialStatements\\outputUpdated.xls");
	    File referenceWorkbook = new File("C:\\myData\\sas\\other\\magnolia\\financialStatements\\output.xls");
	    
	    Workbook inputFile;
	    Workbook referenceFile;
	    
	    try {
	      inputFile = Workbook.getWorkbook(inputWorkbook);	      
	      referenceFile = Workbook.getWorkbook(referenceWorkbook);
	      WritableWorkbook copy = Workbook.createWorkbook(outputWorkbook, inputFile);
	      
	      WritableSheet sheetToEdit = copy.getSheet(0);      
	      Sheet referenceSheet = referenceFile.getSheet(0);


	      for (int j = 0; j < sheetToEdit.getRows(); j++) {
	          Cell cell = sheetToEdit.getCell(0, j);
	          String aptNumber = cell.getContents().trim().replaceAll(" ","");
	          aptNumber = aptNumber.replaceAll("-", "").toUpperCase();
	          boolean dataFound = false;
	          for (int i = 1; i < referenceSheet.getRows(); i++) {
	        	  Cell dataCell = referenceSheet.getCell(0,i);
	        	  String aptNumberFromReferenceFile = dataCell.getContents().trim().replaceAll(" ","");	        	  
	        	  if (aptNumber.equalsIgnoreCase(aptNumberFromReferenceFile)) {
	        		  dataFound = true;
	        		  String ownerName = referenceSheet.getCell(1,i).getContents();
	        		  String emailID = referenceSheet.getCell(2,i).getContents();
	        		  String phone1 = referenceSheet.getCell(3,i).getContents();
	        		  String phone2 = referenceSheet.getCell(4,i).getContents();
	        		  
	        		  WritableCell nameCell;
	        		  Label l1 = new Label(1,j,ownerName);
	        		  nameCell = (WritableCell) l1;
	        		  sheetToEdit.addCell(nameCell);
	        		  
	        		  WritableCell emailCell;
	        		  Label l2 = new Label(2,j,emailID);
	        		  emailCell = (WritableCell) l2;
	        		  sheetToEdit.addCell(emailCell);	

	        		  
	        		  WritableCell p1cell;
	        		  Label l3 = new Label(3,j,phone1);
	        		  p1cell = (WritableCell) l3;
	        		  sheetToEdit.addCell(p1cell);	
	        		  
	        		  WritableCell p2cell;
	        		  Label l4 = new Label(4,j,phone2);
	        		  p2cell = (WritableCell) l4;
	        		  sheetToEdit.addCell(p2cell);	
	        	  } 	        	  
	          }
         
	          
	        
	      }
	      
	      copy.write();
	      copy.close();
		  inputFile.close();
		  referenceFile.close();
		    
	    } catch (BiffException e) {
	      e.printStackTrace();
	    }
	    
 
	    
	  }



	  public static void main(String[] args) throws Exception {
		  SyncExcelDataFinal test = new SyncExcelDataFinal();
		  test.readAndProcess();
		   
	  }


    
}
