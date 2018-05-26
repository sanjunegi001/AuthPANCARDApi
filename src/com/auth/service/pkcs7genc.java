package com.auth.service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertStore;
import java.security.cert.CertStoreException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;

import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.util.encoders.Base64;

public class pkcs7genc {

	
	public void genHappend(String jksfile,String pass,String file2,String signed) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, FileNotFoundException, IOException, UnrecoverableKeyException, CertStoreException, InvalidAlgorithmParameterException, NoSuchProviderException, CMSException
	{

		
		
		//	String panCardDetails =args[3];
			
			ClassLoader classLoader = pkcs7gen.class.getClassLoader();
			
			      // String root = System.getProperty("user.dir");
			        String root = p2j.class.getResource("/").getPath();
	
			    	KeyStore keystore = KeyStore.getInstance("jks");
				  //  InputStream input = new FileInputStream("resources"+"/"+args[0]);
				    InputStream input = new FileInputStream(root+jksfile);
				
				try {
					char[] password=pass.toCharArray();
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
				PrivateKey privateKey=(PrivateKey) keystore.getKey(alias, pass.toCharArray());
				X509Certificate myPubCert=(X509Certificate) keystore.getCertificate(alias);
				byte[] dataToSign=signed.getBytes();
	     
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
				FileOutputStream out = new FileOutputStream(root+file2);
				
				//FileOutputStream out = new FileOutputStream("resources"+"/"+args[2]);
				out.write(signedData64);
				out.close(); 
				System.out.println("Signature file written to "+file2);

		
	}
	
	
}
