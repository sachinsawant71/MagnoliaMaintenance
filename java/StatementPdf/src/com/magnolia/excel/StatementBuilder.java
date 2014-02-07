package com.magnolia.excel;


import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Properties;

import jxl.Sheet;
import jxl.Workbook;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;


public class StatementBuilder {
	

	  private String inputFile;
	  final public static String statementPath = "C:/myData/sas/other/magnolia/statements/year12/";

	  public void setInputFile(String inputFile) {
	    this.inputFile = inputFile;
	  }

	  public void read() throws IOException  {
	    File inputWorkbook = new File(inputFile);
	    Workbook w;
	    try {
	    	
	     VelocityEngine ve = new VelocityEngine();
	     Properties velocityProperties = new Properties();
	     velocityProperties.setProperty("file.resource.loader.path",
	    		   "c://infinity//development//workspace//StatementPdf//templateData");
	     
	     
	     ve.init(velocityProperties);
	     
	     // Getting the Template
	     Template temp = ve.getTemplate("statementTemplate12.vm");
	    	
	      w = Workbook.getWorkbook(inputWorkbook);
	      // Get the first sheet
	      Sheet sheet = w.getSheet(3);
	      // Loop over first 10 column and lines

	        for (int i = 2; i < 67 ; i++) {
          
	          String flatNumber = sheet.getCell(0, i).getContents();
	          String ownerName =  sheet.getCell(1, i).getContents();
	          String sellableArea =  sheet.getCell(2, i).getContents();
	          String openingBalance =  sheet.getCell(4, i).getContents();
	          
	          System.out.println("Processing Data for " + flatNumber);
	          
	          String period1Rec = sheet.getCell(5, i).getContents();
	          String period2Rec = sheet.getCell(7, i).getContents();	          
	          String totalrec = sheet.getCell(8, i).getContents();
	          
	          String period1Payment = sheet.getCell(10, i).getContents();
	          String period2Payment = sheet.getCell(11, i).getContents();	          
	          String totalPayment = sheet.getCell(12, i).getContents();
	          
	          String totalDues = sheet.getCell(13, i).getContents();
	          
	          VelocityContext context = new VelocityContext();
	          context.put("flatNumber", flatNumber);
	          context.put("ownerName", ownerName);
	          context.put("sellableArea", sellableArea);
	          context.put("openingBalance", openingBalance);
	          context.put("period1Rec", period1Rec);
	          context.put("period2Rec", period2Rec);
	          context.put("totalrec", totalrec);
	          
	          context.put("period1Payment", period1Payment);
	          context.put("period2Payment", period2Payment);
	          context.put("totalPayment", totalPayment);
	          context.put("totalDues", totalDues);
	          
	          if (totalDues.equals("0") || totalDues.startsWith("-")) {
	        	  context.put("duesPending", true);
	          }
	          
	          
	          StringWriter wr = new StringWriter();
	          temp.merge( context, wr );
	          
	          String fileName = flatNumber.toLowerCase().replaceAll(" ", "") + "_year2012_13";
	          
	          String htmlFile = statementPath + fileName + ".html";
 
	          
	          PrintWriter out = new PrintWriter(htmlFile);
	          out.println(wr);
	          
	          out.close();
   
	          
	          String pdfFile = statementPath + fileName + ".pdf";
	          
	          String scriptFile = statementPath + "rasterize.js";
	          System.out.println("Creating pdf file");
	          
	          String pdfToHtmlCommand = "C:\\software\\phantomjs-1.8.2-windows\\phantomjs-1.8.2-windows\\phantomjs " + scriptFile + " " + htmlFile + " " + pdfFile;
	          Runtime.getRuntime().exec(pdfToHtmlCommand);


	        }

	    } catch (Exception e) {
	      e.printStackTrace();
	    }
	  }

	  public static void main(String[] args) throws IOException {
		StatementBuilder test = new StatementBuilder();
	    test.setInputFile("C:/myData/sas/other/magnolia/magnolia_12-13_test.xls");
	    test.read();
	  }

}
