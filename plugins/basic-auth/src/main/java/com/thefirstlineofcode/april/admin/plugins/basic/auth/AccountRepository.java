package com.thefirstlineofcode.april.admin.plugins.basic.auth;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository  extends PagingAndSortingRepository<Account, Long> {
	Account findByName(String name);
}
