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


public class SyncExcelPropertyTaxData {

	public void readAndProcess() throws IOException, RowsExceededException, WriteException  {
	    File inputWorkbook = new File("C:\\myData\\sas\\other\\magnolia\\financialStatements\\output.xls");
	    File outputWorkbook = new File("C:\\myData\\sas\\other\\magnolia\\financialStatements\\outputUpdates.xls");
	    File referenceWorkbook = new File("C:\\myData\\sas\\other\\magnolia\\financialStatements\\MagnoliaPropertyTaxInfo.xls");
	    
	    Workbook inputFile;
	    Workbook referenceFile;
	    
	    try {
	      inputFile = Workbook.getWorkbook(inputWorkbook);	      
	      referenceFile = Workbook.getWorkbook(referenceWorkbook);
	      WritableWorkbook copy = Workbook.createWorkbook(outputWorkbook, inputFile);
	      
	      WritableSheet sheetToEdit = copy.getSheet(0);      
	      Sheet referenceSheet = referenceFile.getSheet(0);


	      for (int j = 1; j < sheetToEdit.getRows(); j++) {
	          Cell cell = sheetToEdit.getCell(0, j);
	          String aptNumber = cell.getContents().trim().replaceAll(" ","");
	          aptNumber = aptNumber.replaceAll("-", "").toUpperCase();
	          boolean dataFound = false;
	          for (int i = 1; i < referenceSheet.getRows(); i++) {
	        	  Cell dataCell = referenceSheet.getCell(0,i);
	        	  String aptNumberFromReferenceFile = dataCell.getContents().trim().replaceAll(" ","");	    
	        	  aptNumberFromReferenceFile = aptNumberFromReferenceFile.replaceAll("-", "").toUpperCase();
	        	  if (aptNumber.equalsIgnoreCase(aptNumberFromReferenceFile)) {
	        		  dataFound = true;
	        		  String propertyTaxNo = referenceSheet.getCell(3,i).getContents();	   
	        		  String propertyTaxPaid = referenceSheet.getCell(4,i).getContents();	 
	        		  
	        		  WritableCell taxCell;
	        		  Label l3 = new Label(5,j,propertyTaxNo);
	        		  taxCell = (WritableCell) l3;
	        		  sheetToEdit.addCell(taxCell);
	        		  
	        		  WritableCell statusCell;
	        		  Label l4 = new Label(6,j,propertyTaxPaid);
	        		  statusCell = (WritableCell) l4;
	        		  sheetToEdit.addCell(statusCell);    		  

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
		  SyncExcelPropertyTaxData test = new SyncExcelPropertyTaxData();
		  test.readAndProcess();
		   
	  }


    
}
