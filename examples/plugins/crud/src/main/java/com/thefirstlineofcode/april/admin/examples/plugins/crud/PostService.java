package com.thefirstlineofcode.april.admin.examples.plugins.crud;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Service;

import com.thefirstlineofcode.april.admin.core.crud.jpa.LongTypeIdBasicCrudService;
import com.thefirstlineofcode.april.admin.examples.plugins.data.accessor.Post;
import com.thefirstlineofcode.april.admin.examples.plugins.data.accessor.PostRepository;

@Service
public class PostService extends LongTypeIdBasicCrudService<Post> implements IPostService {
	@Autowired
	private PostRepository postRepository;
	
	@Override
	protected PagingAndSortingRepository<Post, Long> getRepository() {
		return postRepository;
	}
}
