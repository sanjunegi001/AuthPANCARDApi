package com.auth.util;

import com.auth.service.p2jc;
import com.auth.service.pkcs7genc;

public class WorkerThread implements Runnable {

	private String authdatasigned;

	public WorkerThread(String s) {
		// TODO Auto-generated constructor stub
		this.authdatasigned = s;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

		try {

			p2jc test = new p2jc();
			test.taskHappend("eMudhraCertificate.pfx", "Auth1234", "oupt.jks");
			pkcs7genc test2 = new pkcs7genc();
			test2.genHappend("oupt.jks", "Auth1234", "oupt.sig", authdatasigned);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
