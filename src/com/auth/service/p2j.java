package com.auth.service;
import java.io.*;
import java.net.URLDecoder;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.util.Enumeration;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;


public class p2j
{
	
	
	
	@Autowired
	static
    ServletContext context;
    public p2j()
    {    
    }

    public static void main(String args[])
        throws Exception
    {
    	
    	
    	ClassLoader classLoader = p2j.class.getClassLoader();
    	 
        //String root = System.getProperty("user.dir");	
    	String root = p2j.class.getResource("/").getPath();
    	
    	System.out.println("rootpath"+root);
    	System.out.println(args.length);
    	
        if(args.length < 1)
        {
            System.out.println("usage: java p2j eMudra Certi.pfx Auth@1234 oupt.jks");
            System.exit(1);
        }
     
         File fileIn = new File(URLDecoder.decode(classLoader.getResource(args[0]).getFile(), "UTF-8"));
               
        
         System.out.println("filename"+fileIn);
         
     
        File fileOut=null;
        if(args.length == 3)
        {   
        	
        	//System.out.println("folder create path"+);
        	
        	    fileOut = new File(root+args[2]);
        	
        	   System.out.println("resources path"+classLoader.getResource("/resource/"));
        	
        	  //  fileOut = new File("resources/"+args[2]);
        	    System.out.println("fileOut"+fileOut);
        	    

        }else{
        	
        System.exit(1);}
        if(!fileIn.canRead())
        {
            System.out.println("Unable to access input keystore: " + fileIn.getPath());
            System.exit(2);
        }
        if(fileOut.exists() && !fileOut.canWrite())
        {
            System.out.println("Output file is not writable: " + fileOut.getPath());
            System.exit(2);
        }
        
        
     
        KeyStore kspkcs12 = KeyStore.getInstance("pkcs12");
        KeyStore ksjks = KeyStore.getInstance("jks");

        char inphrase[] = args[1].toCharArray();
        char outphrase[] = args[1].toCharArray();
        
       
        
        
        
        kspkcs12.load(new FileInputStream(fileIn), inphrase);
      
        System.out.println("kspkcs12"+kspkcs12);
       
        
        ksjks.load(fileOut.exists() ? ((java.io.InputStream) (new FileInputStream(fileOut))) : null, outphrase);
      
        
        System.out.println("kspkcs12"+kspkcs12);
        System.out.println("ksjks"+ksjks);
       
        
    	System.out.println("eAlises"+kspkcs12);
    	System.out.println("nextelement"+kspkcs12.getClass());
        
        Enumeration eAliases = kspkcs12.aliases();
        int n = 0;
        do
        {
        
            if(!eAliases.hasMoreElements())
                break;
            String strAlias = (String)eAliases.nextElement();
            
            System.out.println("Alias Name List"+strAlias);
           
            if(kspkcs12.isKeyEntry(strAlias))
            {
                java.security.Key key = kspkcs12.getKey(strAlias, inphrase);
                Certificate chain[] = kspkcs12.getCertificateChain(strAlias);
               
                
                ksjks.setKeyEntry(strAlias, key, outphrase, chain);
                
                
            }
        } while(true);
        OutputStream out = new FileOutputStream(fileOut);
        ksjks.store(out, outphrase);
        out.close();
        System.out.println("Java Key Store created successfully");
    }
}

