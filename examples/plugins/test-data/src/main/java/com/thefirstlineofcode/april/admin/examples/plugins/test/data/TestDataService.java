package com.thefirstlineofcode.april.admin.examples.plugins.test.data;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thefirstlineofcode.april.admin.examples.plugins.data.accessor.Post;
import com.thefirstlineofcode.april.admin.examples.plugins.data.accessor.PostRepository;
import com.thefirstlineofcode.april.admin.examples.plugins.data.accessor.User;
import com.thefirstlineofcode.april.admin.examples.plugins.data.accessor.UserRepository;

@Service
@Transactional
public class TestDataService implements ITestDataService {
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private PostRepository postRepository;

	@Override
	public void loadTestData() {
		if (userRepository.count() != 0)
			throw new RuntimeException("Not empty DB.");
		
		userRepository.saveAll(readUsers());
		postRepository.saveAll(readPosts());
	}

	private List<User> readUsers() {
		String usersData = readTestDat("/users.json");
		ObjectMapper objectMapper = new ObjectMapper();
		List<User> users;
		try {
			users = objectMapper.readValue(usersData.getBytes(), new TypeReference<List<User>>(){});
		} catch (Exception e) {
			throw new RuntimeException("Can't read users from json data.", e);
		}
		
		return users;
	}
	
	private List<Post> readPosts() {
		String postsData = readTestDat("/posts.json");
		ObjectMapper objectMapper = new ObjectMapper();
		List<Post> posts;
		try {
			posts = objectMapper.readValue(postsData.getBytes(), new TypeReference<List<Post>>(){});
		} catch (Exception e) {
			throw new RuntimeException("Can't read posts from json data.", e);
		}
		
		return posts;
	}

	private String readTestDat(String resourcePath) {
		URL testDataUrl = getClass().getResource(resourcePath);
		
		if (testDataUrl == null)
			throw new RuntimeException(String.format("Resource for test data which's path is '%s' can't be found.", resourcePath));
		
		StringBuilder sb = new StringBuilder();
		try {
			Reader reader = new InputStreamReader(testDataUrl.openStream());
			char[] buff = new char[256];
			int size = -1;
			while ((size = reader.read(buff)) != -1) {
				sb.append(buff, 0, size);
			}
			
			return sb.toString();
		} catch (IOException e) {
			throw new RuntimeException("Error occurred when reading test data.", e);
		}
	}

	@Override
	public void clearTestData() {
		postRepository.deleteAll();
		userRepository.deleteAll();
	}

	@Override
	public long getTotalUsers() {
		return userRepository.count();
	}
	
	@Override
	public long getTotalPosts() {
		return postRepository.count();
	}
}
