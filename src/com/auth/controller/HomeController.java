package com.auth.controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.auth.controller.SecurityBased.DummyHostnameVerifier;
import com.auth.controller.SecurityBased.DummyTrustManager;
import com.auth.domain.Employee;
import com.auth.domain.User;
import com.auth.service.UserService;
import com.auth.service.p2j;
import com.auth.service.pkcs7gen;

@Controller
public class HomeController {

	@Autowired
	private UserService userService;
	private Employee eml;

	@RequestMapping(value = "/home")
	public ModelAndView Home(@ModelAttribute("user") User user, Model model) throws Exception {

		return new ModelAndView("Home");
	}

	@RequestMapping(value = "/processPancard", method = RequestMethod.POST)
	public ModelAndView processPancardData(@ModelAttribute("user") User user, Model model) throws Exception {

		Date date = new Date();
		String strDateFormat = "yyyy/MM/dd";
		DateFormat dateFormat = new SimpleDateFormat(strDateFormat);
		String formattedDate = dateFormat.format(date);

		String authdatasigned = null;
		String pancarddetails = user.getPancardnumber().trim();

		if (pancarddetails.contains(",")) {
			authdatasigned = "V0122601^" + pancarddetails.trim().replaceAll("\\s+", "").replaceAll("\\,", "\\^");

		} else {
			authdatasigned = "V0122601^" + pancarddetails.trim();

		}

		// String root = System.getProperty("user.dir");

		ClassLoader classLoader = getClass().getClassLoader();

		// String root = p2j.class.getResource("/").getPath();

		p2j.main(new String[] { "eMudhraCertificate.pfx", "Auth1234", "oupt.jks" });
		pkcs7gen.main(new String[] { "oupt.jks", "Auth1234", "oupt.sig", authdatasigned });

		Date startTime = null;
		Calendar c1 = Calendar.getInstance();
		startTime = c1.getTime();

		Date connectionStartTime = null;
		String logMsg = "\n-";
		BufferedWriter out = null;
		BufferedWriter out1 = null;
		FileWriter fstream = null;
		FileWriter fstream1 = null;
		Calendar c = Calendar.getInstance();
		long nonce = c.getTimeInMillis();

		String urlOfNsdl = "https://59.163.46.2/TIN/PanInquiryBackEnd";
		String data = null;
		String signature = null;

		Properties prop = new Properties();
		try {

			try {

				FileInputStream br1 = new FileInputStream(classLoader.getResource("oupt.sig").getFile());

				// FileInputStream br1 = new FileInputStream(root+"oupt.sig");

				signature = IOUtils.toString(br1);

				data = authdatasigned;

			} catch (FileNotFoundException e) {

				e.printStackTrace(System.err);

			}

			// FileInputStream inputdata = new FileInputStream("oupt.sig");

			// prop.load(new FileInputStream("params.properties"));
			// data=prop.getProperty("data");
			// signature=prop.getProperty("signature");

			// prop.load(new FileInputStream("oupt.sig"));
			// signature = prop.getProperty("");

		} catch (Exception e) {
			logMsg += "::Exception: " + e.getMessage() + " ::Program Start Time:" + startTime + "::nonce= " + nonce;
		}

		try {

			// fstream= new FileWriter(root+"API_PAN_verification.logs",true);
			fstream = new FileWriter(classLoader.getResource("API_PAN_verification.logs").getFile(), true);

			out = new BufferedWriter(fstream);

		} catch (Exception e) {
			logMsg += "::Exception: " + e.getMessage() + " ::Program Start Time:" + startTime + "::nonce= " + nonce;
			out.write(logMsg);
			out.close();
		}

		SSLContext sslcontext = null;
		try {
			sslcontext = SSLContext.getInstance("TLSv1.2");

			sslcontext.init(new KeyManager[0], new TrustManager[] { new DummyTrustManager() }, new SecureRandom());
		} catch (NoSuchAlgorithmException e) {
			logMsg += "::Exception: " + e.getMessage() + " ::Program Start Time:" + startTime + "::nonce= " + nonce;
			e.printStackTrace(System.err);
			out.write(logMsg);
			out.close();
		} catch (KeyManagementException e) {
			logMsg += "::Exception: " + e.getMessage() + " ::Program Start Time:" + startTime + "::nonce= " + nonce;
			e.printStackTrace(System.err);
			out.write(logMsg);
			out.close();
		}

		SSLSocketFactory factory = sslcontext.getSocketFactory();

		String urlParameters = "data=";

		try {

			System.out.println("signature" + signature);

			urlParameters = urlParameters + URLEncoder.encode(data, "UTF-8") + "&signature="
					+ URLEncoder.encode(signature, "UTF-8");

		} catch (Exception e) {
			logMsg += "::Exception: " + e.getMessage() + " ::Program Start Time:" + startTime + "::nonce= " + nonce;
			e.printStackTrace();
			out.write(logMsg);
			out.close();
		}

		try {
			URL url;
			HttpsURLConnection connection;
			InputStream is = null;

			String ip = urlOfNsdl;
			url = new URL(ip);
			System.out.println("URL " + ip);
			connection = (HttpsURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			connection.setRequestProperty("Content-Length", "" + Integer.toString(urlParameters.getBytes().length));
			connection.setRequestProperty("Content-Language", "en-US");
			connection.setUseCaches(false);
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setSSLSocketFactory(factory);
			connection.setHostnameVerifier(new DummyHostnameVerifier());
			OutputStream os = connection.getOutputStream();
			OutputStreamWriter osw = new OutputStreamWriter(os);
			osw.write(urlParameters);
			osw.flush();
			connectionStartTime = new Date();
			logMsg += "::Request Sent At: " + connectionStartTime;
			logMsg += "::Request Data: " + data;
			osw.close();
			is = connection.getInputStream();
			BufferedReader in = new BufferedReader(new InputStreamReader(is));
			String line = null;
			line = in.readLine();

			System.out.println("line" + line);

			if (line.trim().equals("7"))

			{

				ArrayList<String> str2 = new ArrayList<String>();

				str2.add("<font style='color:#FF0000;'>You Are Not Allowed More then 5 Pan Card Entry</font>");

				is.close();
				in.close();
				model.addAttribute("model", str2);
				// model.addAttribute("model",str2);

			} else if (line.trim().equals("2")) {

				ArrayList<String> str2 = new ArrayList<String>();
				str2.add("<font style='color:#FF0000;'>System Error, Please Contact Technical Team</font>");

				is.close();
				in.close();
				model.addAttribute("model", str2);

			}

			else if (line.trim().equals("3")) {

				ArrayList<String> str2 = new ArrayList<String>();
				str2.add("<font style='color:#FF0000;'>Authentication Failed, Please Contact Technical Team</font>");

				is.close();
				in.close();
				model.addAttribute("model", str2);

			}

			else if (line.trim().equals("4")) {

				ArrayList<String> str2 = new ArrayList<String>();
				str2.add("<font style='color:#FF0000;'>User Not Authorized, Please Contact Technical Team</font>");

				is.close();
				in.close();
				model.addAttribute("model", str2);

			} else if (line.trim().equals("5")) {

				ArrayList<String> str2 = new ArrayList<String>();
				str2.add("<font style='color:#FF0000;'>No Pannumber Entered, Please Contact Technical Team</font>");

				is.close();
				in.close();
				model.addAttribute("model", str2);

			} else if (line.trim().equals("6")) {

				ArrayList<String> str2 = new ArrayList<String>();
				str2.add("<font style='color:#FF0000;'>Your ClientId Is Expired, Please Contact Technical Team</font>");

				is.close();
				in.close();
				model.addAttribute("model", str2);

			}

			else if (line.trim().equals("8")) {

				ArrayList<String> str2 = new ArrayList<String>();
				str2.add("<font style='color:#FF0000;'>Not Enough Space, Please Contact Technical Team</font>");

				is.close();
				in.close();
				model.addAttribute("model", str2);

			} else if (line.trim().equals("9")) {

				ArrayList<String> str2 = new ArrayList<String>();
				str2.add("<font style='color:#FF0000;'>Not A HTTPS Request, Please Contact Technical Team</font>");

				is.close();
				in.close();
				model.addAttribute("model", str2);

			}

			else if (line.trim().equals("10")) {

				ArrayList<String> str2 = new ArrayList<String>();
				str2.add("<font style='color:#FF0000;'>Post Method Not Used, Please Contact Technical Team</font>");

				is.close();
				in.close();
				model.addAttribute("model", str2);

			}

			else if (line.trim().equals("11")) {

				ArrayList<String> str2 = new ArrayList<String>();
				str2.add("<font style='color:#FF0000;'>SLAB CHANGE RUNNING, Please Contact Technical Team</font>");

				is.close();
				in.close();
				model.addAttribute("model", str2);

			}

			else {

				String line3 = line.replaceFirst("\\d{1,2}", "").replaceAll("^\\^", "");
				String[] tokens = line3.split("(\\^\\^\\^\\^)");

				ArrayList<String> str2 = new ArrayList();

				int i = 0;
				str2.add("<tr><th>PANCARD NUMBER</th><th>NAME</th><th>DATE OF APPLY </th><th>STATUS</th>");
				for (i = 0; i < tokens.length; i++) {

					if (tokens[i].contains("^E^")) {

						String[] innertokens = tokens[i].replaceAll("^\\^", "").trim().trim().split("(\\^)");

						String name = innertokens[5] + " " + innertokens[3] + " " + innertokens[4] + " "
								+ innertokens[2];

						// str2.add("pan"+innertokens[0]+"name"+innertokens[5]+"
						// "+innertokens[3]+" "+innertokens[4]+"
						// "+innertokens[2]+"dop"+innertokens[6]+"1");

						str2.add("<tr><td>" + innertokens[0] + "</td><td>" + innertokens[5] + " " + innertokens[3] + " "
								+ innertokens[4] + " " + innertokens[2] + "</td><td>" + innertokens[6]
								+ "</td><td><font style='color:#33CC00;'>Active</font></td></tr>");
						Employee employee = new Employee(innertokens[0], name, innertokens[6], 1, formattedDate, "app",
								"NA");
						userService.addUser(employee);
					}

					else if (tokens[i].contains("^N")) {

						String name = "NA";
						String dop = "NA";

						String[] innertokens = tokens[i].replaceAll("^\\^", "").trim().trim().split("(\\^)");

						str2.add("<tr><td>" + innertokens[0]
								+ "</td><td>NA</td><td>NA</td><td><font style='color:#FF0000;'>INVALID<font></td></tr>");
						Employee employee = new Employee(innertokens[0], name, dop, 0, formattedDate, "app", "NA");
						userService.addUser(employee);
					}

				}

				// System.out.println(jsonString);
				is.close();
				in.close();

				model.addAttribute("model", str2);
			}

			// return new ModelAndView("Home","model",model);

		} catch (ConnectException e) {
			logMsg += "::Exception: " + e.getMessage() + "::Program Start Time:" + startTime + "::nonce= " + nonce;
			out.write(logMsg);
			out.close();
		} catch (Exception e) {
			logMsg += "::Exception: " + e.getMessage() + "::Program Start Time:" + startTime + "::nonce= " + nonce;
			out.write(logMsg);
			out.close();
			e.printStackTrace();
		}

		out.write(logMsg);
		out.close();
		// return new ModelAndView("redirect:Home");
		return new ModelAndView("Home", "user", user);
		// return "home";

	}

}
