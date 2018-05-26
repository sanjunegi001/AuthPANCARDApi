package com.auth.domain;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;



import java.text.DateFormat;
import java.text.SimpleDateFormat;

import com.auth.controller.MD5Auth;
import com.auth.service.UserLoginService;
import com.mysql.jdbc.Connection;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


public class LoginDelegate {

	
	
	
	
	private UserLoginService userloginService;

	public void setUserLoginService(UserLoginService userloginService)
	{
		this.userloginService = userloginService;
	}

	public UserLoginService getUserLoginService()
	{
		return this.userloginService;
	}

	public static int isAcessDetails(String username,HttpSession session) throws Exception
	{
		int result = 0;
		
		MD5Auth mdauth = new MD5Auth();
        final String QUERY = "select * from pan_user_details WHERE user_loginname ='"+username.trim()+"'";
		
      
		Connection con = getDBConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
        	
        	 ps = con.prepareStatement(QUERY);
             rs = ps.executeQuery();
           
             while(rs.next()){
         		
         	  
         	     result = Integer.parseInt(rs.getString("user_access_flag"));
   			     return result;
                 
             }
         
        }
        catch (SQLException e) {
            e.printStackTrace();
        }finally{
            try {
                ps.close();
                con.close();
            } catch (SQLException e){
                e.printStackTrace();
            }
        }
		
		return result;
	}
	
	
	
	
	
	public static int isPassExpire(String username, String password,HttpSession session) throws Exception
	{
		
		
        int result = 0;
		
		MD5Auth mdauth = new MD5Auth();
		
		final String QUERY = "select user_expired_on from pan_user_details WHERE user_loginname ='"+username.trim()+"' AND user_password='"+mdauth.encode(password).trim()+"'";
		
		Connection con = getDBConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            ps = con.prepareStatement(QUERY);
             
          
            rs = ps.executeQuery();
             
            Calendar cal = Calendar.getInstance();
            Date today = cal.getTime();
           
           	
            	while(rs.next()){
            		
            		
            		
            		if(today.after(rs.getDate("user_expired_on")))
            		{
            			
            			result = 0;
            			return result;
            		}
            		else
            		{
            			
            			result = 1;
            			return result;
            		}
            		
                    
                }
            	
            	
            	
           
            
             
        } catch (SQLException e) {
            e.printStackTrace();
        }finally{
            try {
                ps.close();
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
		return result;
		
		
	}
	
	
	
	public static int isPassChaange(String oldpassword, String newpassowrd,String username) throws Exception
	{
		
		
	    MD5Auth mdauth = new MD5Auth();
	    //String encryptedHash = app.encode(oldpassword);
	      
	    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		System.out.println("curent format date"+dateFormat.format(date));
		

        Calendar c = Calendar.getInstance();
        c.setTime(new Date()); // Now use today date.
        c.add(Calendar.DATE,90); // Adding 90 days
        String expireddate = dateFormat.format(c.getTime());
      

		
		int result = 0;
		
		final String QUERY = "UPDATE pan_user_details SET user_password = '"+mdauth.encode(newpassowrd)+"',last_password_changed = '"+dateFormat.format(date)+"',user_expired_on='"+expireddate+"' WHERE user_password ='"+mdauth.encode(oldpassword).trim()+"' AND user_loginname='"+username.trim()+"'";
		
		
		
		Connection con = getDBConnection();
        PreparedStatement ps = null;
        int rs = 0;
        try {
            ps = con.prepareStatement(QUERY);
           result = ps.executeUpdate();
      
            return result;
            
             
        } catch (SQLException e) {
            e.printStackTrace();
        }finally{
            try {
                ps.close();
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
		return result;
        
        

		
	}
	public static int isUpdateValidUser(String username,String oldpassword,String newpassword,HttpSession session)throws Exception
	{
		
		    MD5Auth mdauth = new MD5Auth();
		    //String encryptedHash = app.encode(oldpassword);
		      
		    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = new Date();
			System.out.println("curent format date"+dateFormat.format(date));
			

	        Calendar c = Calendar.getInstance();
	        c.setTime(new Date()); // Now use today date.
	        c.add(Calendar.DATE,90); // Adding 90 days
	        String expireddate = dateFormat.format(c.getTime());
	      

			
			int result1 = 0;
			
			final String QUERY = "UPDATE pan_user_details SET user_password = '"+mdauth.encode(newpassword)+"',last_password_changed = '"+dateFormat.format(date)+"',user_expired_on='"+expireddate+"' WHERE user_password ='"+mdauth.encode(oldpassword).trim()+"' AND user_loginname='"+username.trim()+"'";
			
			
			
			Connection con = getDBConnection();
	        PreparedStatement ps = null;
	        int rs = 0;
	        try {
	            ps = con.prepareStatement(QUERY);
	           result1 = ps.executeUpdate();
	           System.out.println("update"+result1);
	            return result1;
	           
	             
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }finally{
	            try {
	                ps.close();
	                con.close();
	            } catch (SQLException e) {
	                e.printStackTrace();
	            }
	        }
			return result1;
		
		
	}
	

	
	
	public static int isChangeValidUser(String username,String oldpassword,String newpassword,HttpSession session) throws Exception
	{
		
		int result2 = 0;
		
		MD5Auth mdauth = new MD5Auth();
		final String QUERY = "select * from pan_user_details WHERE user_loginname ='"+username.trim()+"' AND user_password='"+mdauth.encode(oldpassword).trim()+"'";
		
		Connection con = getDBConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = con.prepareStatement(QUERY);
             
          
            rs = ps.executeQuery();
             
        
           
           	
            	while(rs.next()){
            		
            		result2 = rs.getRow();
            	    
            	    return result2;
                }
            	
            	
            	
           
            
             
        } catch (SQLException e) {
            e.printStackTrace();
        }finally{
            try {
                ps.close();
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
		return result2;
		
		
		
	}
	
	public static int isValidUser(String username, String password,HttpSession session) throws Exception
	{
		
		int result = 0;
		
		MD5Auth mdauth = new MD5Auth();
		
		final String QUERY = "select * from pan_user_details WHERE user_loginname ='"+username.trim()+"' AND user_password='"+mdauth.encode(password).trim()+"'";
		
		System.out.println("MyQuery"+QUERY);
		
		Connection con = getDBConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = con.prepareStatement(QUERY);
             
            System.out.println("ps"+ps);
            rs = ps.executeQuery();
             
            System.out.println("rs"+rs);
           
           	
            	while(rs.next()){
            		
            		 result = rs.getRow();
            		 System.out.println("result"+result);
            		 
            		 session.setAttribute("user_login_name",rs.getString("user_loginname"));
            		 return result;
                    //System.out.println("Employee ID="+rs.getString("user_name"));
                    
                }
            	
    
             
        } catch (SQLException e) {
            e.printStackTrace();
        }finally{
            try {
                ps.close();
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
		return result;
    
		
	}
	
	
	
	
	private static Connection getDBConnection() {

		
	        Properties props = new Properties();
	        FileInputStream fis = null;
	        java.sql.Connection con = null;
	        try {
	        	
	        	 ClassLoader classLoader = LoginDelegate.class.getClassLoader();
	     		 //**Security Related Information production server**// 
	    		
	    		 
	    		 String db  = classLoader.getResource("myjdbc.properties").getFile();
	        
	            fis = new FileInputStream(db);
	            props.load(fis);
	            
	            // load the Driver Class
	            Class.forName(props.getProperty("database.driver"));
	 
	            // create the connection now
	            con = DriverManager.getConnection(props.getProperty("database.url"),
	                    props.getProperty("database.user"),
	                    props.getProperty("database.password"));
	        } catch (SQLException e) {
	            System.out.println("Check database is UP and configs are correct");
	            e.printStackTrace();
	        } catch (IOException e) {
	            System.out.println("Looks like db.property file has some issues");
	            e.printStackTrace();
	        } catch (ClassNotFoundException e) {
	            System.out.println("Please include JDBC API jar in classpath");
	            e.printStackTrace();
	        }finally{
	            try {
	                fis.close();
	            } catch (IOException e) {
	                System.out.println("File Close issue, lets ignore it.");
	            }
	        }
	        return (Connection) con;
	   

	}
	
	
	
}
