package com.auth.service;
import java.sql.SQLException;


	public interface UserLoginService
	
		{
	
		    public boolean isValidUser(String username, String password) throws SQLException;
	
		}
	

