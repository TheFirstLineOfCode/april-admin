package com.thefirstlineofcode.april.admin.examples.plugins.data.accessor;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends PagingAndSortingRepository<User, Long> {
	@Query("SELECT u FROM User u WHERE u.name LIKE %:name% AND u.company.name LIKE %:companyName%")
	List<User> findAllByNameAndCompanyName(Pageable pageable, @Param("name") String name, @Param("companyName") String companyName);
	@Query("SELECT count(*) FROM User u WHERE u.name LIKE %:name% AND u.company.name LIKE %:companyName%")
	long countByNameAndCompanyName(@Param("name") String name, @Param("companyName") String companyName);
}
