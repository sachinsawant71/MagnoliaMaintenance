package com.magnolia.excel;

import java.io.File;
import java.io.IOException;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;


public class ExcelPropertyTaxDataReader {

	public void readAndProcess() throws IOException, RowsExceededException, WriteException  {
	    File referenceWorkbook = new File("C:\\myData\\sas\\other\\magnolia\\financialStatements\\MagnoliaPropertyTaxData.xls");
	    
	    Workbook referenceFile;
	    
	    try {
	    		referenceFile = Workbook.getWorkbook(referenceWorkbook);
	            Sheet referenceSheet = referenceFile.getSheet(0);

		          for (int i = 1; i < referenceSheet.getRows(); i++) {
		        	  Cell dataCell = referenceSheet.getCell(2,i);
		        	  String taxNumber = dataCell.getContents();	    
		        	  String accountNo = taxNumber.substring(taxNumber.lastIndexOf('/') + 1);
		        	  System.out.println("'" + accountNo + "',");
		        	  
	        
	      }

		  referenceFile.close();
		    
	    } catch (BiffException e) {
	      e.printStackTrace();
	    }
	    
 
	    
	  }



	  public static void main(String[] args) throws Exception {
		  ExcelPropertyTaxDataReader test = new ExcelPropertyTaxDataReader();
		  test.readAndProcess();
		   
	  }


    
}
