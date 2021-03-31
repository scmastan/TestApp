package com.exalca.controllers;



import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


import com.exalca.model.Message;
import com.exalca.model.SignModel;
import com.exalca.model.SignResult;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfSignatureAppearance;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.security.BouncyCastleDigest;
import com.itextpdf.text.pdf.security.CertificateUtil;
import com.itextpdf.text.pdf.security.CrlClient;
import com.itextpdf.text.pdf.security.CrlClientOnline;
import com.itextpdf.text.pdf.security.ExternalDigest;
import com.itextpdf.text.pdf.security.ExternalSignature;
import com.itextpdf.text.pdf.security.MakeSignature;
import com.itextpdf.text.pdf.security.OcspClient;
import com.itextpdf.text.pdf.security.OcspClientBouncyCastle;
import com.itextpdf.text.pdf.security.PrivateKeySignature;
import com.itextpdf.text.pdf.security.TSAClient;
import com.itextpdf.text.pdf.security.TSAClientBouncyCastle;

@RestController
public class DigitalSIgnature {	
	private static  Logger logger = LoggerFactory.getLogger(DigitalSIgnature.class);
	
	
	
	//test
	
	
	@RequestMapping(value = "/test", method = RequestMethod.GET, produces = "application/json")
	public Message getMessage() {
		Message message = new Message();
		int id = 1;
		if (id == 1) {
			message.setId(1);
			message.setType("Inspirational");
			message.setMessage("PORADITHE Poyedi emi ledu....!!............ Banisa sankellu tappa");
		} else if (id == 2) {
			message.setId(2);
			message.setType("Inspirational");
			message.setMessage(
					"illemo dhooram dharantha gathukulu asale cheekati chetilo deepam ledu kaani naa dhairyam ee kavacham");
		}
		return message;
	}
	
	
	
	
	
	
	@RequestMapping(value = "/sign", method = RequestMethod.POST, produces = "application/json" ,consumes="application/json")
	public SignResult sign(@RequestBody SignModel sign) {
		SignResult result=null;
		try {
			result=signDocument(sign.getInputPDF(),sign.getTitle1(),sign.getTitle2());
			if(result==null) {
				result=new SignResult();
				result.setStatus("signFailed");
			}else {
				//result.setSigndata(signdata);
				result.setStatus("Signed successfully");
			}
		}
		catch(Exception e) {
			if(result==null) {
				result=new SignResult();
				result.setStatus("signFailed");
				 logger.info(" Error in sign null " + e.getMessage());
			}else {
				result.setStatus("Exception : "+e.getMessage());
				 logger.info(" Error in sign " + e.getMessage());
			}
		}
		return result;
	}
	
	
	
	
	
//	public static void main(String[] args) {
//		String inputFile = "C:\\Users\\80068\\Desktop\\testds\\inbox\\input.pdf";
//		String dirName = "C:\\Users\\80068\\Desktop\\testds\\outbox\\";
//		String title1 = "Mastan";
//		String title2 = "Exalca";
//		boolean result = signDocument(inputFile, dirName, title1, title2);
//		if (result) {
//			System.err.println("Signed succesfully");
//		} else {
//			System.err.println("Document is not signed");
//		}
//	}
//	

	public static SignResult signDocument(byte [] inputFile,  String title1, String title2) {
		//boolean result = false;
		SignResult result=null;
		try {
			
		
			
			
			
			logger.info("STep1");
			KeyStore keystore = KeyStore.getInstance("Windows-MY");
			//KeyStore keystore = KeyStore.getInstance("Windows-ROOT");
			String certName = "DS eMudhra Test 6";
			//String alias=certName;
			//keystore.cuu
			keystore.load(null, null);
			// String alias="DS Mudhra Test 3";
			logger.info("STep2 "+keystore);
			Enumeration<String> aliasList = keystore.aliases();
			logger.info("STep21 "+aliasList.toString());
		String alias = aliasList.nextElement();
			logger.info("STep3");
		    X509Certificate cert = (X509Certificate) keystore.getCertificate(certName);
		    logger.info("STep4 "+cert);
			PrivateKey privateKey = (PrivateKey) keystore.getKey(alias, certName.toCharArray());
			logger.info("STep5 "+privateKey);
			//Certificate cert = keystore.getCertificate(certName);
		//	logger.info("STep6 "+cert.toString());
			Certificate[] chain = keystore.getCertificateChain(alias);
			logger.info("STep7 ");
//			Path path = Paths.get(inputFile);
//		    String fileName = path.getFileName().toString();
//			 String outputFilePath=  dirName+"signed_"+fileName;		
			File opdf=new File("sign.pdf");
//			File opdf=new File("sign.pdf");
//			if(opdf.exists()) {
//				opdf.delete();
//			}
			//File iif=new File(inputFile);
				//	byte []  b=Files.readAllBytes(iif.toPath());
			PdfReader reader = new PdfReader(inputFile);
			logger.info("STep8");
			FileOutputStream fout = new FileOutputStream("sign.pdf");
			PdfStamper stp = PdfStamper.createSignature(reader, fout, '\0');
			logger.info("STep9");
			PdfSignatureAppearance sap = stp.getSignatureAppearance();
			sap.setCertificate(cert);
			sap.setVisibleSignature(new Rectangle(520, 220, 390, 260), 1, null);
			sap.setReason(title1);
			sap.setLocation(title2);
			sap.setAcro6Layers(false);
			sap.setLayer4Text(PdfSignatureAppearance.questionMark);
			sap.setReasonCaption("");
			sap.setLocationCaption("");			
//			X509CertificateHolder x509CertificateHolder = new X509CertificateHolder(certfic.getEncoded());
//			String certAlgorithm = certfic.getPublicKey().getAlgorithm();			
//			CMSSignedDataGenerator dataGenerator = new CMSSignedDataGenerator();			
			logger.info("STep10");
			ExternalSignature externalSignature = new PrivateKeySignature(privateKey, "SHA-256", null);
			logger.info("STep101");
			ExternalDigest externalDigest = new BouncyCastleDigest();	
			logger.info("STep101");
			TSAClient tsaClient = null;
	        for (int i = 0; i < chain.length; i++) {
	            X509Certificate cert1 = (X509Certificate)chain[i];
	            String tsaUrl = CertificateUtil.getTSAURL(cert1);
	         //   System.err.println(tsaUrl  + chain[i]);
	            if (tsaUrl != null) {
	                tsaClient = new TSAClientBouncyCastle(tsaUrl);
	                break;
	            }
	        }
	        logger.info("STep11");
			OcspClient ocspClient = new OcspClientBouncyCastle();
			List<CrlClient> crlClient = new ArrayList<CrlClient>();
			crlClient.add( new CrlClientOnline( new java.security.cert.Certificate[]{cert} ) );
			MakeSignature.signDetached(sap, new BouncyCastleDigest(), externalSignature,chain , crlClient, ocspClient, tsaClient, 0, MakeSignature.CryptoStandard.CMS);
			 logger.info("STep12");
//			MakeSignature.signDetached(sap, externalDigest, externalSignature, chain, null, null, null, 0,
//					MakeSignature.CryptoStandard.CMS);
			
			byte []  a=Files.readAllBytes(opdf.toPath());
			
			try (FileOutputStream stream = new FileOutputStream("test.pdf")) {
			    stream.write(a);
			}
			result=new SignResult();
			result.setSigndata(a);
			 logger.info("STep13");
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(" Error " + e.getMessage());
			 logger.info(" Error in signDocument " + e.getMessage());
		}
		return result;
	}


	
	
}
