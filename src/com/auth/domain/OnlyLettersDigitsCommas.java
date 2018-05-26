package com.auth.domain;
import  java.util.regex.Pattern;
import  java.util.regex.Matcher;


public class OnlyLettersDigitsCommas {

	
	
    public static final Matcher lettersCommasMtchr = Pattern.compile("^[a-zA-Z0-9,]+$").matcher("");

	   public static final boolean isOnlyLettersDigitsCommas(String to_test)  {
	      return  lettersCommasMtchr.reset(to_test).matches();
	   }
	
	
}
