package com.thefirstlineofcode.april.admin.examples.plugins.test.data;

public interface ITestDataService {
	void loadTestData();
	void clearTestData();
	long getTotalUsers();
	long getTotalPosts();
}
