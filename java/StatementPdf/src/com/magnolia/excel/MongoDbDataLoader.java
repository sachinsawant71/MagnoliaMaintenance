package com.magnolia.excel;


import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import jxl.Sheet;
import jxl.Workbook;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.util.StringUtils;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.util.JSON;


public class MongoDbDataLoader {
	

	  private String inputFile;
	  final public static String statementPath = "C:/myData/sas/other/magnolia/statements/final/";

	  public void setInputFile(String inputFile) {
	    this.inputFile = inputFile;
	  }

	  public void read() throws IOException  {
	    File inputWorkbook = new File(inputFile);
	    Workbook w;
	    try {
	    	
		Mongo mongo = new Mongo("localhost", 27017);
		DB db = mongo.getDB("magnolia");
		DBCollection collection = db.getCollection("apartmentData");
		
		DBCursor cursorDoc = collection.find();
		while (cursorDoc.hasNext()) {
			collection.remove(cursorDoc.next());
		}	
	    	
	     VelocityEngine ve = new VelocityEngine();
	     Properties velocityProperties = new Properties();
	     velocityProperties.setProperty("file.resource.loader.path",
	    		   "c://infinity//codebase//StatementPdf//templateData");
	     
     
	     ve.init(velocityProperties);
	     
	     // Getting the Template
	     Template temp = ve.getTemplate("dbRecordTemplate.vm");
	     NumberFormat nf = NumberFormat.getInstance();   
	     nf.setMaximumFractionDigits(0);

	    	
	      w = Workbook.getWorkbook(inputWorkbook);
	      // Get the first sheet
	      Sheet sheet = w.getSheet(0);
	      // Loop over first 10 column and lines

	        for (int i = 0; i < 237 ; i++) {
          
	          String flatNumber = sheet.getCell(0, i).getContents().trim().replace(" ","");
	          String ownerName =  sheet.getCell(1, i).getContents().trim();	 
	          String emailIdString = sheet.getCell(2, i).getContents().trim();
	          String phoneNumer1 = sheet.getCell(3, i).getContents().trim();
	          String phoneNumber2 = sheet.getCell(4, i).getContents().trim();
	          String notes = sheet.getCell(5, i).getContents().trim();
	          
	          List<String> emailIdList = new ArrayList<String>();
	          
	          StringTokenizer emailTokenizer = new StringTokenizer(emailIdString,";");
	          while (emailTokenizer.hasMoreElements()) {
	        	  emailIdList.add((String)emailTokenizer.nextElement());
	  		  }
	          
	          StringBuffer emailListString = new StringBuffer();
	          for (int k=0; k< emailIdList.size() ; k++) {
	        	  emailListString.append("\"").append(emailIdList.get(k)).append("\"");
	        	  if (k < emailIdList.size()-1 ) {
	        		  emailListString.append(",");
	        	  }
	          }
	          
	          
	          String sellableArea =  sheet.getCell(11, i).getContents();
	          
	          int sellableAreaInt = Integer.parseInt(sellableArea.trim());
	          
	          System.out.println("Processing Data for " + flatNumber);
	          
	          double maintenanceDue0910 =  sellableAreaInt*2.0*5;
	          double maintenanceDue1011 =  sellableAreaInt*2.0*12;
	          double maintenanceDue1112 =  sellableAreaInt*2.0*12;
	          double maintenanceDue1213 =  sellableAreaInt*2.0*6 + sellableAreaInt*2.5*6;
	          double maintenanceDueTotal = maintenanceDue0910 + maintenanceDue1011 + maintenanceDue1112 + maintenanceDue1213;
	          
	          String maintenancePaid0910 =  sheet.getCell(6, i).getContents();
	          String maintenancePaid1011 =  sheet.getCell(7, i).getContents();
	          String maintenancePaid1112 =  sheet.getCell(8, i).getContents();
	          String maintenancePaid1213 =  sheet.getCell(9, i).getContents();
	          String maintenancePaidTotal =  sheet.getCell(10, i).getContents();
	          
	          String totalDues = sheet.getCell(12, i).getContents();
	          
	          VelocityContext context = new VelocityContext();
	          context.put("flatNumber", flatNumber);
	          context.put("ownerName", toCamelCase(ownerName));
	          context.put("sellableArea", sellableArea);
	          context.put("emails", emailListString);
	          context.put("phone1", phoneNumer1);
	          context.put("phone2", phoneNumber2);
	          context.put("notes",notes);
	          
	          
	          context.put("maintenanceDue0910", maintenanceDue0910);
	          context.put("maintenanceDue1011", maintenanceDue1011);
	          context.put("maintenanceDue1112", maintenanceDue1112);
	          context.put("maintenanceDue1213", maintenanceDue1213);
	          context.put("maintenanceDueTotal",maintenanceDueTotal);
	          
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
	          System.out.println(wr);
	          
	  		  DBObject dbObject = (DBObject) JSON.parse(wr.toString());	 
			  collection.insert(dbObject);	  
	          
	        }
	               

	        
			cursorDoc = collection.find();
			while (cursorDoc.hasNext()) {
				System.out.println(cursorDoc.next());
			}	    

	    } catch (Exception e) {
	      e.printStackTrace();
	    }    
	    
	    
	  }
	  
	  
	  static String toCamelCase(String s){
          String[] parts = s.split(" ");
          String camelCaseString = "";
          for (String part : parts){
              if(part!=null && part.trim().length()>0)
             camelCaseString = camelCaseString + toProperCase(part);
              else
                  camelCaseString=camelCaseString+part+" ";   
          }
          return camelCaseString;
       }

       static String toProperCase(String s) {
           String temp=s.trim();
           String spaces="";
           if(temp.length()!=s.length())
           {
           int startCharIndex=s.charAt(temp.indexOf(0));
           spaces=s.substring(0,startCharIndex);
           }
           temp=temp.substring(0, 1).toUpperCase() +
           spaces+temp.substring(1).toLowerCase()+" ";
           return temp;

       }

	  public static void main(String[] args) throws IOException {
		MongoDbDataLoader test = new MongoDbDataLoader();
	    test.setInputFile("C:/myData/sas/other/magnolia/financialStatements/financedataDataLoad.xls");
	    test.read();
	  }

}
