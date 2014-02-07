package com.magnolia.excel;

import java.io.File;
import java.io.IOException;

import jxl.Cell;
import jxl.CellType;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableCell;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;


public class SyncExcelData {

	public void readAndProcess() throws IOException, RowsExceededException, WriteException  {
	    File inputWorkbook = new File("C:\\myData\\sas\\other\\magnolia\\ConsolidatedContactsData.xls");
	    File dataWorkbook = new File("C:\\myData\\sas\\other\\magnolia\\OwnerTenantData4March2013.xls");
	    
	    File dataWorkbook2 = new File("C:\\myData\\sas\\other\\magnolia\\gaskyc.xls");
	    
	    Workbook inputFile;
	    Workbook dataFile;
	    Workbook data2File;
	    
	    try {
	      inputFile = Workbook.getWorkbook(inputWorkbook);	      
	      dataFile = Workbook.getWorkbook(dataWorkbook);
	      
	      data2File = Workbook.getWorkbook(dataWorkbook2);
	      
	      WritableWorkbook workbookCopy = Workbook.createWorkbook(new File("C:\\myData\\sas\\other\\magnolia\\output.xls"), inputFile);
	      WritableSheet sheetToEdit = workbookCopy.getSheet(0);

	      Sheet dataSheet = dataFile.getSheet(0);	      
	      Sheet data2Sheet = data2File.getSheet(0);


	      for (int j = 1; j < sheetToEdit.getRows(); j++) {
	          Cell cell = sheetToEdit.getCell(0, j);
	          String aptNumber = cell.getContents().trim().replaceAll(" ","");
	          boolean dataFound = false;
	          for (int i = 1; i < dataSheet.getRows(); i++) {
	        	  Cell dataCell = dataSheet.getCell(1,i);
	        	  if (aptNumber.equals(dataCell.getContents())) {
	        		  dataFound = true;
	        		  String emailID = dataSheet.getCell(6,i).getContents();
	        		  String phone1 = dataSheet.getCell(8,i).getContents();
	        		  String phone2 = dataSheet.getCell(9,i).getContents();
	        		  WritableCell emailcell;
	        		  Label l = new Label(2,j,emailID);
	        		  emailcell = (WritableCell) l;
	        		  sheetToEdit.addCell(emailcell);	
	        		  
	        		  WritableCell p1cell;
	        		  Label l1 = new Label(3,j,phone1);
	        		  p1cell = (WritableCell) l1;
	        		  sheetToEdit.addCell(p1cell);	
	        		  
	        		  WritableCell p2cell;
	        		  Label l2 = new Label(4,j,phone2);
	        		  p2cell = (WritableCell) l2;
	        		  sheetToEdit.addCell(p2cell);	
	        	  } 	        	  
	          }
	          if (!dataFound) {
	        	  
		          for (int i = 1; i < data2Sheet.getRows(); i++) {
		        	  Cell dataCell = data2Sheet.getCell(0,i);
		        	  if (aptNumber.equals(dataCell.getContents())) {

		        		  String emailID = data2Sheet.getCell(4,i).getContents();
		        		  String phone1 = data2Sheet.getCell(2,i).getContents();
		        		  String phone2 = data2Sheet.getCell(3,i).getContents();
		        		  WritableCell emailcell;
		        		  Label l = new Label(2,j,emailID);
		        		  emailcell = (WritableCell) l;
		        		  sheetToEdit.addCell(emailcell);	
		        		  
		        		  WritableCell p1cell;
		        		  Label l1 = new Label(3,j,phone1);
		        		  p1cell = (WritableCell) l1;
		        		  sheetToEdit.addCell(p1cell);	
		        		  
		        		  WritableCell p2cell;
		        		  Label l2 = new Label(4,j,phone2);
		        		  p2cell = (WritableCell) l2;
		        		  sheetToEdit.addCell(p2cell);	
		        	  } 	        	  
		          }
	        	  
	        	  
	          }
	          
	          
	        
	      }
	      
	      workbookCopy.write();
		  inputFile.close();
		  dataFile.close();   
		  workbookCopy.close();
		    
	    } catch (BiffException e) {
	      e.printStackTrace();
	    }
	    
 
	    
	  }



	  public static void main(String[] args) throws Exception {
		  SyncExcelData test = new SyncExcelData();
		  test.readAndProcess();
		   
	  }


    
}
