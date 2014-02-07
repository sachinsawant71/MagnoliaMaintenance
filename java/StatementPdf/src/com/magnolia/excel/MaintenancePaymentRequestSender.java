package com.magnolia.excel;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import jxl.Sheet;
import jxl.Workbook;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;


public class MaintenancePaymentRequestSender {
	

	  private String inputFile;
	  final public static String statementPath11 = "C:/myData/sas/other/magnolia/";
	  
	  private List<String> alreadyReceivedList =  new ArrayList<String>(Arrays.asList("A1004","A102", "A104","A105", "A1101", "A1102", "A1106", "A201", "A202", "A204",
			   "A302", "A305", "A401", "A404", "A504", "A703", "A704", "A802", "B1003",  "B101",  "B1101",  "B304",  "B404",  "B501",  "B504",
			     "B603", "B703", "B803", "B806", "B902",  "C1002",  "C1103",  "C203",  "C204",  "C404",  "C503",  "C601",  "C603",  "C701",  "C703",
			       "C902",  "C903",  "D1002",  "D101",  "D102",  "D201",  "D202",  "D302",  "D303",  "D304",  "D704",  "D903", "D904"));
	  
	  
	  
	  public void setInputFile(String inputFile) {
	    this.inputFile = inputFile;
	  }

	  public void read() throws IOException  {
		  
//	    final String username = "magnolia.pashan@gmail.com";
//	    final String password = "blue$987";
		  
		final String username = "magnoliapashan.finance@gmail.com";
		final String password = "gowind105";

	 
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");		  
		  
		  
	    File inputWorkbook = new File(inputFile);
	    Workbook w;
	    try {
	    	
	     VelocityEngine ve = new VelocityEngine();
	     Properties velocityProperties = new Properties();
	     velocityProperties.setProperty("file.resource.loader.path",
	    		   "c://infinity//codebase//StatementPdf//templateData");
     
	     
	     ve.init(velocityProperties);
	      w = Workbook.getWorkbook(inputWorkbook);
	      // Get the first sheet
	      Sheet sheet = w.getSheet(0);
	      // Loop over first 10 column and lines
	      
		  Session session = Session.getInstance(props,
				  new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(username, password);
					}
				  });

	        for (int i = 1; i < 239; i++) {
              String flatNumber = sheet.getCell(0, i).getContents();
	          String ownerName =  sheet.getCell(1, i).getContents();
	          String emailIds = sheet.getCell(2, i).getContents();
	          String salablearea = sheet.getCell(6, i).getContents();
	          String totalContribution = sheet.getCell(7, i).getContents();
	          
	          System.out.println("Processing Data for " + flatNumber);
	          
	          String flatNoWithoutSpace = flatNumber.replaceAll(" ","");
	          
	          if (!emailIds.trim().equals("") && !alreadyReceivedList.contains(flatNoWithoutSpace.trim())) {
     	  
		          VelocityContext context = new VelocityContext();
		          context.put("flatNumber", flatNumber);
		          context.put("ownerName", ownerName);
		          context.put("salablearea", salablearea);
		          context.put("totalContribution", totalContribution);
		         
		          String templateName = "maintenanceRequestLetter.vm";
		          
	     
		          // Getting the Template
		 	     Template temp = ve.getTemplate(templateName);
		          
		          StringWriter wr = new StringWriter();
		          temp.merge( context, wr );
		          
		          
				  Message message = new MimeMessage(session);
				  
				  message.setFrom(new InternetAddress("magnoliapashan.finance@gmail.com"));
				  message.addRecipient(Message.RecipientType.CC,
                          new InternetAddress("magnolia.pashan@gmail.com"));
				  message.setSubject("Magnolia Apartment Maintenance Payment Request (Period October-13 to March-14) for " + flatNumber + "(Updated)");
				  
				  StringTokenizer tokenizer = new StringTokenizer(emailIds, ";");
				  InternetAddress[] to_address = new InternetAddress[tokenizer.countTokens()];			  
				  int k=0;
				  while (tokenizer.hasMoreElements()) {
					  String emailId = tokenizer.nextToken();
					  to_address[k] = new InternetAddress(emailId);
					  k++;				  
				  }
				  for (InternetAddress receipient : to_address) {				  
					  message.addRecipient(Message.RecipientType.TO, receipient);
				  }
				  
				  MimeBodyPart body = new MimeBodyPart();
				  body.setContent(wr.toString(),"text/html");
	          
				  //do attachment
		          MimeBodyPart attachMent1 = new MimeBodyPart();
		          String fileName1 = "Magnolia_Maintenance_Payment_Options.pdf";
		          FileDataSource dataSource1 = new FileDataSource(new File( statementPath11 + fileName1));
		          attachMent1.setDataHandler(new DataHandler(dataSource1));
		          attachMent1.setFileName(fileName1);
		          attachMent1.setDisposition(MimeBodyPart.ATTACHMENT);
         
		          Multipart multipart = new MimeMultipart();	          
		          multipart.addBodyPart(body);
		          multipart.addBodyPart(attachMent1);
		          message.setContent(multipart);			  
				
				  Transport.send(message,message.getAllRecipients());
	        	  
	          } 

	        }

	    } catch (Exception e) {
	      e.printStackTrace();
	    }
	  }

	  public static void main(String[] args) throws IOException {
		MaintenancePaymentRequestSender test = new MaintenancePaymentRequestSender();
	    test.setInputFile("C:/myData/sas/other/magnolia/financialStatements/output.xls");
	    test.read();
	  }

}
