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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.auth.controller.SecurityBased.DummyHostnameVerifier;
import com.auth.controller.SecurityBased.DummyTrustManager;
import com.auth.domain.Employee;
import com.auth.domain.LoginDelegate;
import com.auth.domain.OnlyLettersDigitsCommas;
import com.auth.domain.User;
import com.auth.service.UserService;
import com.auth.util.WorkerThread;

@Controller
public class ApiController {

	@Autowired
	private UserService userService;
	private Employee eml;

	@RequestMapping(value = "/apiRequest", method = { RequestMethod.POST })
	public ModelAndView apiRequest(@RequestParam(value = "username", required = true) String username,
			@RequestParam(value = "password", required = true) String password,
			@RequestParam(value = "pandetails", required = true) String pandetails, @ModelAttribute("user") User user,
			Model model, HttpSession session, HttpServletRequest request) throws Exception {

		String uname = request.getParameter("username").trim();
		String pass = request.getParameter("password").trim();
		String pan = request.getParameter("pandetails").trim();

		Date date = new Date();
		String strDateFormat = "yyyy/MM/dd";
		DateFormat dateFormat = new SimpleDateFormat(strDateFormat);
		String formattedDate = dateFormat.format(date);

		if (uname == "" && pass == "" && pan == "") {

			JsonObjectBuilder err = Json.createObjectBuilder().add("Error",
					Json.createObjectBuilder().add("Code", "400").add("Message", "Bad Request")

			);

			model.addAttribute("model", err.build());

		}

		else if (uname == "" && pass == "") {
			JsonObjectBuilder err = Json.createObjectBuilder().add("Error",
					Json.createObjectBuilder().add("Code", "400").add("Message", "Bad Request")

			);

			model.addAttribute("model", err.build());

		} else if (uname == "" && pan == "") {
			JsonObjectBuilder err = Json.createObjectBuilder().add("Error",
					Json.createObjectBuilder().add("Code", "400").add("Message", "Bad Request")

			);

			model.addAttribute("model", err.build());
		}

		else if (pass == "" && pan == "") {
			JsonObjectBuilder err = Json.createObjectBuilder().add("Error",
					Json.createObjectBuilder().add("Code", "400").add("Message", "Bad Request")

			);

			model.addAttribute("model", err.build());

		} else if (uname == "") {
			JsonObjectBuilder err = Json.createObjectBuilder().add("Error",
					Json.createObjectBuilder().add("Code", "400").add("Message", "Bad Request")

			);

			model.addAttribute("model", err.build());

		} else if (pass == "") {
			JsonObjectBuilder err = Json.createObjectBuilder().add("Error",
					Json.createObjectBuilder().add("Code", "400").add("Message", "Bad Request")

			);

			model.addAttribute("model", err.build());

		} else if (pan == "") {

			JsonObjectBuilder err = Json.createObjectBuilder().add("Error",
					Json.createObjectBuilder().add("Code", "400").add("Message", "Bad Request")

			);

			model.addAttribute("model", err.build());

		}

		else {
			// #####CHECH AUTHENTICATION#####///

			int ispanValidUser = LoginDelegate.isValidUser(uname, pass, session);

			if (ispanValidUser == 0) {

				JsonObjectBuilder err = Json.createObjectBuilder().add("Error",
						Json.createObjectBuilder().add("Code", "401").add("Message", "Authentication failed")

				);

				model.addAttribute("model", err.build());

			} else if (ispanValidUser == 1) {

				/// ######REQUEST GO TO PAN SERVER#######//

				String authdatasigned = null;
				String pancarddetails = pan.trim();

				if (OnlyLettersDigitsCommas.isOnlyLettersDigitsCommas(pancarddetails) == true) {

					if (pan.contains(",")) {
						authdatasigned = "V0122601^" + pancarddetails.replaceAll("\\,", "\\^");
					} else {
						authdatasigned = "V0122601^" + pancarddetails;
					}

					// ##REQUEST SEND TO NSDL##//

					ClassLoader classLoader = getClass().getClassLoader();
					ExecutorService executor = Executors.newFixedThreadPool(400);

					Runnable worker = new WorkerThread(authdatasigned);
					executor.execute(worker);

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
							signature = IOUtils.toString(br1);

							data = authdatasigned;

						} catch (FileNotFoundException e) {

							JsonObjectBuilder err = Json.createObjectBuilder().add("Error", Json.createObjectBuilder()
									.add("Code", "500").add("Message", "Some technical issue,Please try after sometime")

							);
							Employee employee = new Employee(pan.replaceAll("\\,", "\\^"), "NA", "NA", 0, formattedDate,
									session.getAttribute("user_login_name").toString(), "500");
							userService.addUser(employee);
							model.addAttribute("model", err.build());
							logMsg += "::Exception: " + e.getMessage() + "Error" + System.err + " ::Program Start Time:"
									+ startTime + "::nonce= " + nonce;

						}

					} catch (Exception e) {

						JsonObjectBuilder err = Json.createObjectBuilder().add("Error", Json.createObjectBuilder()
								.add("Code", "500").add("Message", "Some technical issue,Please try after sometime")

						);
						Employee employee = new Employee(pan.replaceAll("\\,", "\\^"), "NA", "NA", 0, formattedDate,
								session.getAttribute("user_login_name").toString(), "500");
						userService.addUser(employee);
						model.addAttribute("model", err.build());

						logMsg += "::Exception: " + e.getMessage() + " ::Program Start Time:" + startTime + "::nonce= "
								+ nonce;
					}

					SSLContext sslcontext = null;
					try {
						sslcontext = SSLContext.getInstance("TLSv1.2");
						sslcontext.init(new KeyManager[0], new TrustManager[] { new DummyTrustManager() },
								new SecureRandom());

					} catch (NoSuchAlgorithmException e) {

						JsonObjectBuilder err = Json.createObjectBuilder().add("Error", Json.createObjectBuilder()
								.add("Code", "500").add("Message", "Some technical issue,Please try after sometime")

						);

						Employee employee = new Employee(pan.replaceAll("\\,", "\\^"), "NA", "NA", 0, formattedDate,
								session.getAttribute("user_login_name").toString(), "500");
						userService.addUser(employee);
						model.addAttribute("model", err.build());
						logMsg += "::Exception: " + e.getMessage() + " ::Program Start Time:" + startTime + "::nonce= "
								+ nonce;
						out.write(logMsg);
						out.close();
					} catch (KeyManagementException e) {

						JsonObjectBuilder err = Json.createObjectBuilder().add("Error", Json.createObjectBuilder()
								.add("Code", "500").add("Message", "Some technical issue,Please try after sometime"));

						Employee employee = new Employee(pan.replaceAll("\\,", "\\^"), "NA", "NA", 0, formattedDate,
								session.getAttribute("user_login_name").toString(), "500");
						userService.addUser(employee);
						model.addAttribute("model", err.build());

						logMsg += "::Exception: " + e.getMessage() + " ::Program Start Time:" + startTime + "::nonce= "
								+ nonce;
						out.write(logMsg);
						out.close();
					}

					SSLSocketFactory factory = sslcontext.getSocketFactory();
					String urlParameters = "data=";

					// ##REQUEST PARAMETER TRY CATCH BLOCK##//

					try {

						urlParameters = urlParameters + URLEncoder.encode(data, "UTF-8") + "&signature="
								+ URLEncoder.encode(signature, "UTF-8");

					} catch (Exception e) {

						JsonObjectBuilder err = Json.createObjectBuilder().add("Error", Json.createObjectBuilder()
								.add("Code", "500").add("Message", "Some technical issue,Please try after sometime")

						);

						Employee employee = new Employee(pan.replaceAll("\\,", "\\^"), "NA", "NA", 0, formattedDate,
								session.getAttribute("user_login_name").toString(), "500");
						userService.addUser(employee);
						model.addAttribute("model", err.build());
						logMsg += "::Exception: " + e.getMessage() + " ::Program Start Time:" + startTime + "::nonce= "
								+ nonce;
						out.write(logMsg);
						out.close();
					}

					// ##POST REQUEST SEND TO NSDL##//
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
						connection.setRequestProperty("Content-Length",
								"" + Integer.toString(urlParameters.getBytes().length));
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
						System.out.println("line numeber" + line);
						if (line.trim().equals("7")) {

							JsonObjectBuilder err = Json.createObjectBuilder().add("Error",
									Json.createObjectBuilder().add("Code", "503").add("Message", "Excess PanCard Enter")

							);

							model.addAttribute("model", err.build());

						}
						if (line.trim().equals("8")) {

							JsonObjectBuilder err = Json.createObjectBuilder().add("Error",
									Json.createObjectBuilder().add("Code", "505").add("Message", "Not enough balance")

							);

							model.addAttribute("model", err.build());

						}
						if (line.trim().equals("4")) {

							JsonObjectBuilder err = Json.createObjectBuilder().add("Error",
									Json.createObjectBuilder().add("Code", "504").add("Message",
											"Not Authorized Please Contact Technical Support")

							);

							model.addAttribute("model", err.build());

						}
						if (line.trim().equals("2")) {

							JsonObjectBuilder err = Json.createObjectBuilder().add("Error", Json.createObjectBuilder()
									.add("Code", "506").add("Message", "Internal system error")

							);

							model.addAttribute("model", err.build());

						}

						if (line.trim().equals("6")) {

							JsonObjectBuilder err = Json.createObjectBuilder().add("Error", Json.createObjectBuilder()
									.add("Code", "507").add("Message", "User Expired,Contact with technical suppport")

							);

							model.addAttribute("model", err.build());

						}
						if (line.trim().equals("11")) {

							JsonObjectBuilder err = Json.createObjectBuilder().add("Error",
									Json.createObjectBuilder().add("Code", "508").add("Message",
											"Slab change running please contact technical support  Error in NSDL Server")

							);

							model.addAttribute("model", err.build());

						}

						if (line.trim().equals("9")) {

							logMsg += "::Request: IS_HTTP_REQUEST" + "::Program Start Time:" + startTime + "::nonce= "
									+ nonce;
							out.write(logMsg);
							out.close();

						}
						if (line.trim().equals("10")) {
							logMsg += "::Request: IS_GET_REQUEST" + "::Program Start Time:" + startTime + "::nonce= "
									+ nonce;
							out.write(logMsg);
							out.close();

						}
						if (line.trim().equals("5")) {

							JsonObjectBuilder err = Json.createObjectBuilder().add("Error",
									Json.createObjectBuilder().add("Code", "509").add("Message", "No Pan Entered")

							);

							model.addAttribute("model", err.build());

						}
						if (line.trim().equals("3")) {

							JsonObjectBuilder err = Json.createObjectBuilder().add("Error",
									Json.createObjectBuilder().add("Code", "510").add("Message",
											"Authentication Failed!Please Contact Service Provider")

							);

							model.addAttribute("model", err.build());

						} else if (line.trim().contains("1")) {
							System.out.println("line numeber1");

							String line3 = line.replaceFirst("\\d{1,2}", "").replaceAll("^\\^", "");
							String[] tokens = line3.split("(\\^\\^\\^\\^)");

							JsonArrayBuilder builder = Json.createArrayBuilder();
							int i = 0;
							for (i = 0; i < tokens.length; i++) {

								ArrayList<String> str2 = new ArrayList<String>();

								if (tokens[i].contains("^E^")) {

									String[] innertokens = tokens[i].replaceAll("^\\^", "").trim().trim()
											.split("(\\^)");
									String name = innertokens[5] + " " + innertokens[3] + " " + innertokens[4] + " "
											+ innertokens[2];

									JsonObjectBuilder b = Json.createObjectBuilder().add("pandetails",
											Json.createObjectBuilder().add("Code", "200")
													.add("PanNumber", innertokens[0])
													.add("Name",
															innertokens[5] + " " + innertokens[3] + " " + innertokens[4]
																	+ " " + innertokens[2])
													.add("LastUpdate", innertokens[6]).add("Status", "E")

									);

									builder.add(b.build());
									Employee employee = new Employee(innertokens[0], name, innertokens[6], 1,
											formattedDate, session.getAttribute("user_login_name").toString(), "NA");
									userService.addUser(employee);

								} else if (tokens[i].contains("^N")) {
									String[] innertokens = tokens[i].replaceAll("^\\^", "").trim().trim()
											.split("(\\^)");

									JsonObjectBuilder n = Json.createObjectBuilder().add("pandetails",
											Json.createObjectBuilder().add("Code", "201")
													.add("PanNumber", innertokens[0]).add("Name", "NA")
													.add("LastUpdate", "NA").add("Status", "N")

									);

									builder.add(n.build());
									Employee employee = new Employee(innertokens[0], "NA", "NA", 0, formattedDate,
											session.getAttribute("user_login_name").toString(), "NA");
									userService.addUser(employee);

								}

								else if (tokens[i].contains("^F")) {
									String[] innertokens = tokens[i].replaceAll("^\\^", "").trim().trim()
											.split("(\\^)");

									JsonObjectBuilder f = Json.createObjectBuilder().add("pandetails",
											Json.createObjectBuilder().add("Code", "202")
													.add("PanNumber", innertokens[0]).add("Name", "NA")
													.add("LastUpdate", "NA").add("Status", "N")

									);

									builder.add(f.build());
									Employee employee = new Employee(innertokens[0], "NA", "NA", -1, formattedDate,
											session.getAttribute("user_login_name").toString(), "NA");
									userService.addUser(employee);

								}

							}

							JsonArray arr = builder.build();
							model.addAttribute("model", arr);
							is.close();
							in.close();

						}

					} catch (ConnectException e) {

						JsonObjectBuilder err = Json.createObjectBuilder().add("Error",
								Json.createObjectBuilder().add("Code", "501").add("Message", "CIDR connect error")

						);

						model.addAttribute("model", err.build());

					} catch (Exception e) {

						JsonObjectBuilder err = Json.createObjectBuilder().add("Error",
								Json.createObjectBuilder().add("Code", "502").add("Message", "Please Check Request Url")

						);

						model.addAttribute("model", err.build());

					}

				} else {

					JsonObjectBuilder err = Json.createObjectBuilder().add("Error",
							Json.createObjectBuilder().add("Code", "408").add("Message", "Input Pan Parameter Invalid")

					);

					model.addAttribute("model", err.build());

				}

			} else {

				JsonObjectBuilder err = Json.createObjectBuilder().add("Error",
						Json.createObjectBuilder().add("Code", "401").add("Message", "Authentication failed")

				);

				model.addAttribute("model", err.build());

			}

		}

		return new ModelAndView("panapi");

	}

}
