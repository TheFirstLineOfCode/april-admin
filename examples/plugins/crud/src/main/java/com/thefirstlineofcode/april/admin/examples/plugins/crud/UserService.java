package com.thefirstlineofcode.april.admin.examples.plugins.crud;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Service;

import com.thefirstlineofcode.april.admin.examples.plugins.data.accessor.User;
import com.thefirstlineofcode.april.admin.examples.plugins.data.accessor.UserRepository;
import com.thefirstlineofcode.april.admin.framework.crud.jpa.LongTypeIdBasicCrudService;
import com.thefirstlineofcode.april.admin.framework.data.Filters;

@Service
public class UserService extends LongTypeIdBasicCrudService<User> implements IUserService {
	@Autowired
	private UserRepository userRepository;
	
	@Override
	protected PagingAndSortingRepository<User, Long> getRepository() {
		return userRepository;
	}
	
	@Override
	protected List<User> getListByFilters(Pageable pageable, Filters filters) {
		String name = filters.getString("name", "");
		String companyName = filters.getString("company.name", "");
		
		return userRepository.findAllByNameAndCompanyName(pageable, name, companyName);
	}
	
	@Override
	protected long getTotalByFilters(Pageable pageable, Filters filters) {
		String name = filters.getString("name", "");
		String companyName = filters.getString("company.name", "");
		
		return userRepository.countByNameAndCompanyName(name, companyName);
	}
}
