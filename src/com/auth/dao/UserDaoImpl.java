package com.auth.dao;

import java.util.List;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.auth.domain.Employee;
import com.auth.domain.User;

@Repository("userDao")
public class UserDaoImpl implements UserDao {

	
	@Autowired
	private SessionFactory sessionfactory;

	@Override
	public void saveUser(Employee employee) {
		System.out.println(employee);
		sessionfactory.getCurrentSession().saveOrUpdate(employee);
	}

	
//	@Autowired
//	private SessionFactory sessionfactory;
//
//	@Override
//	public void saveUser(Employee employee) {
//		sessionfactory.getCurrentSession().saveOrUpdate(employee);
//	}

	
}
