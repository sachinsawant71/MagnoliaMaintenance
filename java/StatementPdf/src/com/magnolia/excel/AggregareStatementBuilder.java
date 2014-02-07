package com.magnolia.excel;


import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.NumberFormat;
import java.util.Properties;

import jxl.Sheet;
import jxl.Workbook;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;


public class AggregareStatementBuilder {
	

	  private String inputFile;
	  final public static String statementPath = "C:/myData/sas/other/magnolia/statements/final/";

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
	    		   "c://infinity//codebase//StatementPdf//templateData");
	     
     
	     ve.init(velocityProperties);
	     
	     // Getting the Template
	     Template temp = ve.getTemplate("finalStatementTemplate.vm");
	     NumberFormat nf = NumberFormat.getInstance();   
	     nf.setMaximumFractionDigits(0);

	    	
	      w = Workbook.getWorkbook(inputWorkbook);
	      // Get the first sheet
	      Sheet sheet = w.getSheet(0);
	      // Loop over first 10 column and lines

	        for (int i = 177; i < 178 ; i++) {
          
	          String flatNumber = sheet.getCell(0, i).getContents();
	          String ownerName =  sheet.getCell(1, i).getContents();
	          String sellableArea =  sheet.getCell(10, i).getContents();
	          
	          int sellableAreaInt = Integer.parseInt(sellableArea.trim());
	          
	          System.out.println("Processing Data for " + flatNumber);
	          
	          double maintenanceDue0910 =  sellableAreaInt*2.0*5;
	          double maintenanceDue1011 =  sellableAreaInt*2.0*12;
	          double maintenanceDue1112 =  sellableAreaInt*2.0*12;
	          double maintenanceDue1213 =  sellableAreaInt*2.0*6 + sellableAreaInt*2.5*6;
	          double maintenanceDueTotal = maintenanceDue0910 + maintenanceDue1011 + maintenanceDue1112 + maintenanceDue1213;
	          
	          String maintenancePaid0910 =  sheet.getCell(5, i).getContents();
	          String maintenancePaid1011 =  sheet.getCell(6, i).getContents();
	          String maintenancePaid1112 =  sheet.getCell(7, i).getContents();
	          String maintenancePaid1213 =  sheet.getCell(8, i).getContents();
	          String maintenancePaidTotal =  sheet.getCell(9, i).getContents();
	          
	          String totalDues = sheet.getCell(12, i).getContents();
	          
	          VelocityContext context = new VelocityContext();
	          context.put("flatNumber", flatNumber);
	          context.put("ownerName", ownerName);
	          context.put("sellableArea", sellableArea);
	          context.put("maintenanceDue0910", nf.format(maintenanceDue0910));
	          context.put("maintenanceDue1011", nf.format(maintenanceDue1011));
	          context.put("maintenanceDue1112", nf.format(maintenanceDue1112));
	          context.put("maintenanceDue1213", nf.format(maintenanceDue1213));
	          context.put("maintenanceDueTotal", nf.format(maintenanceDueTotal));
	          
	          context.put("maintenancePaid0910", maintenancePaid0910);
	          context.put("maintenancePaid1011", maintenancePaid1011);
	          context.put("maintenancePaid1112", maintenancePaid1112);
	          context.put("maintenancePaid1213", maintenancePaid1213);
	          context.put("maintenancePaidTotal", maintenancePaidTotal);
	          

	          context.put("totalDues", totalDues);
	          
	          if (totalDues.equals("0") || totalDues.startsWith("-")) {
	        	  context.put("duesPending", true);
	          }
	          
	          
	          StringWriter wr = new StringWriter();
	          temp.merge( context, wr );
	          
	          String fileName = flatNumber.toLowerCase().replaceAll(" ", "");
	          
	          String htmlFile = statementPath + fileName + ".html";
 
	          
	          PrintWriter out = new PrintWriter(htmlFile);
	          out.println(wr);
	          
	          out.close();
   
	          
	          String pdfFile = statementPath + fileName + ".pdf";
	          
	          String scriptFile = statementPath + "rasterize.js";
	          System.out.println("Creating pdf file");
	          
	          
	          
	          String pdfToHtmlCommand = "C:\\SoftwareDownlaods\\phantomjs-1.9.1-windows\\phantomjs-1.9.1-windows\\phantomjs " + scriptFile + " " + htmlFile + " " + pdfFile;
	          Runtime.getRuntime().exec(pdfToHtmlCommand);


	        }

	    } catch (Exception e) {
	      e.printStackTrace();
	    }
	  }

	  public static void main(String[] args) throws IOException {
		AggregareStatementBuilder test = new AggregareStatementBuilder();
	    test.setInputFile("C:/myData/sas/other/magnolia/financialStatements/financedatafinal.xls");
	    test.read();
	  }

}
