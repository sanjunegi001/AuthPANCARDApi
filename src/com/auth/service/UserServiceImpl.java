package com.auth.service;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.auth.dao.UserDao;
import com.auth.domain.Employee;
import com.auth.domain.User;


	@Service
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public class UserServiceImpl implements UserService {

	

//		@Override
//		public void addUser(User user) {
//			System.out.println("H"+user);
//		   // System.exit(0);
//			userDao.saveUser(user);
//		}
	
   @Autowired
     UserDao userDao;

@Override
public void addUser(Employee employee) {
	userDao.saveUser(employee);
	
}
   
    
}