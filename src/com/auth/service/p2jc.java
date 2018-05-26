package com.auth.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Enumeration;

public class p2jc {

	public void taskHappend(String cert, String pass, String file) throws KeyStoreException, NoSuchAlgorithmException,
			CertificateException, FileNotFoundException, IOException, UnrecoverableKeyException {

		ClassLoader classLoader = p2jc.class.getClassLoader();

		// String root = System.getProperty("user.dir");
		String root = p2jc.class.getResource("/").getPath();

		System.out.println("rootpath" + root);

		File fileIn = new File(URLDecoder.decode(classLoader.getResource(cert).getFile(), "UTF-8"));

		File fileOut = null;
		fileOut = new File(root + file);

		System.out.println("root file path" + root + file);

		// fileOut = new File("resources/"+args[2]);
		System.out.println("fileOut" + fileOut);

		KeyStore kspkcs12 = KeyStore.getInstance("pkcs12");
		KeyStore ksjks = KeyStore.getInstance("jks");

		char inphrase[] = pass.toCharArray();
		char outphrase[] = pass.toCharArray();

		kspkcs12.load(new FileInputStream(fileIn), inphrase);

		System.out.println("kspkcs12" + kspkcs12);

		ksjks.load(fileOut.exists() ? ((java.io.InputStream) (new FileInputStream(fileOut))) : null, outphrase);

		System.out.println("kspkcs12" + kspkcs12);
		System.out.println("ksjks" + ksjks);

		System.out.println("eAlises" + kspkcs12);
		System.out.println("nextelement" + kspkcs12.getClass());

		Enumeration eAliases = kspkcs12.aliases();
		int n = 0;
		do {

			if (!eAliases.hasMoreElements())
				break;
			String strAlias = (String) eAliases.nextElement();

			System.out.println("Alias Name List" + strAlias);

			if (kspkcs12.isKeyEntry(strAlias)) {
				java.security.Key key = kspkcs12.getKey(strAlias, inphrase);
				Certificate chain[] = kspkcs12.getCertificateChain(strAlias);

				ksjks.setKeyEntry(strAlias, key, outphrase, chain);

			}
		} while (true);
		OutputStream out = new FileOutputStream(fileOut);
		ksjks.store(out, outphrase);
		out.close();
		System.out.println("Java Key Store created successfully");

	}

}
