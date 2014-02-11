package com.magnolia.excel;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
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


public class StatementSender {
	

	  private String inputFile;
	  final public static String statementPath = "C:/myData/sas/other/magnolia/statements/final/";

	  public void setInputFile(String inputFile) {
	    this.inputFile = inputFile;
	  }

	  public void read() throws IOException  {
		  
	    final String username = "magnoliapashan.finance@gmail.com";
	    final String password = "xxxxx";
	    //final String password = "xxxx";
	 
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
	      
		  Session session = Session.getInstance(props,
				  new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(username, password);
					}
				  });

	        for (int i = 0; i < 1; i++) {
          
		      String flatNumber = sheet.getCell(0, i).getContents();
		      String ownerName =  sheet.getCell(1, i).getContents();
	          String emailIds = sheet.getCell(2, i).getContents();

	          System.out.println("Processing Data for " + flatNumber);
	          
	          if (!emailIds.trim().equals("")) {
	        	  
		          VelocityContext context = new VelocityContext();
		          context.put("flatNumber", flatNumber);
		          context.put("ownerName", ownerName.toUpperCase());
		         
		          String templateName = "finalRequestLetter.vm";
		          
		 	     // Getting the Template
		 	     Template temp = ve.getTemplate(templateName);
		          
		          StringWriter wr = new StringWriter();
		          temp.merge( context, wr );
		          
		          
				  Message message = new MimeMessage(session);
				  
				  message.setFrom(new InternetAddress("magnoliapashan.finance@gmail.com"));
				  message.setSubject("Magnolia Apartment Maintenance Payment Summary for " + flatNumber);
				  
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
				  
				  message.addRecipient(Message.RecipientType.CC, new InternetAddress("magnolia.pashan@gmail.com"));
				  
				  MimeBodyPart body = new MimeBodyPart();
				  body.setContent(wr.toString(),"text/html");
	          
				  //do attachment
		          MimeBodyPart attachMent1 = new MimeBodyPart();
		          String fileName1 = flatNumber.toLowerCase().replaceAll(" ", "") + ".pdf";
		          FileDataSource dataSource1 = new FileDataSource(new File( statementPath + fileName1));
		          attachMent1.setDataHandler(new DataHandler(dataSource1));
		          attachMent1.setFileName(fileName1);
		          attachMent1.setDisposition(MimeBodyPart.ATTACHMENT);
		          
//		          MimeBodyPart attachMent2 = new MimeBodyPart();
//		          String fileName2 = flatNumber.toLowerCase().replaceAll(" ", "") + "_year2012_13.pdf";
//		          FileDataSource dataSource2 = new FileDataSource(new File( statementPath12 + fileName2));
//		          attachMent2.setDataHandler(new DataHandler(dataSource2));
//		          attachMent2.setFileName(fileName2);
//		          attachMent2.setDisposition(MimeBodyPart.ATTACHMENT);
		          
		          Multipart multipart = new MimeMultipart();	          
		          multipart.addBodyPart(body);
		          multipart.addBodyPart(attachMent1);
		          //multipart.addBodyPart(attachMent2);
		          message.setContent(multipart);			  
				
				  Transport.send(message,message.getAllRecipients());
	        	  
	          } 

	        }

	    } catch (Exception e) {
	      e.printStackTrace();
	    }
	  }

	  public static void main(String[] args) throws IOException {
		StatementSender test = new StatementSender();
	    test.setInputFile("C:/myData/sas/other/magnolia/financialStatements/sampledata.xls");
	    test.read();
	  }

}
