package com.auth.domain;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;


 

@Entity
@Table(name = "pan_verification_details")
public class Employee {
	
	
	@Id
	@GeneratedValue
	@Column(name = "user_id")
	private int id;
	
	//private String name;
	@Column(name = "user_pannumber")
	private String pannumber;
	
	@Column(name ="user_name")
	private String name;
	
	@Column(name="dlu")
	private String doa;
	
	@Column(name="pan_status")
	private int status;
	
	@Column(name="datecreated")
	private String currentdate;
	
	@Column(name="verify_by")
	private String verify_by;
	
	@Column(name="pan_error")
	private String pan_error;
	
	
	
	public Employee(String pannumber,String name,String doa,int status,String currentdate,String verify_by,String pan_error)
	{
		
		this.pannumber = pannumber;
		this.name = name;
		this.doa = doa;
		this.status = status;
		this.currentdate = currentdate;
		this.pan_error  = pan_error;
		this.verify_by = verify_by;
		
	}
	
	
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getpanNumber() {
		return pannumber;
	}

	public void setpanNumber(String pannumber) {
		this.pannumber = pannumber;
	}
	
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getDoa() {
		return doa;
	}

	public void setDop(String doa) {
		this.doa = doa;
	}
	
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
	
	public String getCurrentdate() {
		return currentdate;
	}

	public void setCurrentdate(String currentdate) {
		this.currentdate = currentdate;
	}



	public String getVerify_by() {
		return verify_by;
	}



	public void setVerify_by(String verify_by) {
		this.verify_by = verify_by;
	}



	public String getPan_error() {
		return pan_error;
	}



	public void setPan_error(String pan_error) {
		this.pan_error = pan_error;
	}
	
	
}
