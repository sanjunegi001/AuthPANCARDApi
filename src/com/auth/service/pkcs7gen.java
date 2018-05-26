package com.auth.service;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;

import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;

import java.util.Enumeration;
import java.util.ArrayList;

import org.bouncycastle.util.encoders.Base64;
import org.springframework.beans.factory.annotation.Autowired;

import java.security.cert.*;

import javax.servlet.ServletContext;

public class pkcs7gen {
	
	@Autowired
	static
    ServletContext context;
	
	public static void main(String args[]) throws Exception {
		
		
		
	//	String panCardDetails =args[3];
		
		ClassLoader classLoader = pkcs7gen.class.getClassLoader();
		
		      // String root = System.getProperty("user.dir");
		        String root = p2j.class.getResource("/").getPath();
		
		
	if(args.length < 2)
        {
            System.out.println("java pkcs7gen oupt.jks pfxpswd dataToBeSigned oupt.sig");
            System.exit(1);
        }


	
		    	KeyStore keystore = KeyStore.getInstance("jks");
			  //  InputStream input = new FileInputStream("resources"+"/"+args[0]);
			    InputStream input = new FileInputStream(root+args[0]);
			
			try {
				char[] password=args[1].toCharArray();
				keystore.load(input, password);
			} catch (IOException e) {
			} finally {

			}
			Enumeration e = keystore.aliases();
			String alias = "";

			if(e!=null)
			{
				while (e.hasMoreElements())
				{
					String  n = (String)e.nextElement();
					if (keystore.isKeyEntry(n))
					{
						alias = n;
					}
				}
			}
			PrivateKey privateKey=(PrivateKey) keystore.getKey(alias, args[1].toCharArray());
			X509Certificate myPubCert=(X509Certificate) keystore.getCertificate(alias);
			byte[] dataToSign=args[3].getBytes();
     
			 //byte[] dataToSign = panCardDetails.getBytes();
			
      
			CMSSignedDataGenerator sgen = new CMSSignedDataGenerator();
			Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider ());
			sgen.addSigner(privateKey, myPubCert,CMSSignedDataGenerator.DIGEST_SHA1);
			Certificate[] certChain =keystore.getCertificateChain(alias);
			ArrayList certList = new ArrayList();
			CertStore certs = null;
			for (int i=0; i < certChain.length; i++)
				certList.add(certChain[i]); 
			sgen.addCertificatesAndCRLs(CertStore.getInstance("Collection", new CollectionCertStoreParameters(certList), "BC"));
			CMSSignedData csd = sgen.generate(new CMSProcessableByteArray(dataToSign),true, "BC");
			byte[] signedData = csd.getEncoded();
			byte[] signedData64 = Base64.encode(signedData); 
			FileOutputStream out = new FileOutputStream(root+args[2]);
			
			//FileOutputStream out = new FileOutputStream("resources"+"/"+args[2]);
			out.write(signedData64);
			out.close(); 
			System.out.println("Signature file written to "+args[2]);
		}

	
	}

