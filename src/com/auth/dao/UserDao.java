package com.auth.dao;

import java.util.List;
import com.auth.domain.Employee;
import com.auth.domain.User;

public interface UserDao {
	
	//public void saveUser ( User user );
		
public void saveUser(Employee employee );
//public List<User> getUser();
}
