package com.thefirstlineofcode.april.admin.examples.plugins.crud;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thefirstlineofcode.april.admin.core.crud.BasicCrudController;
import com.thefirstlineofcode.april.admin.core.crud.IBasicCrudService;
import com.thefirstlineofcode.april.admin.core.error.ValidationException;
import com.thefirstlineofcode.april.admin.examples.plugins.data.accessor.Post;
import com.thefirstlineofcode.april.admin.examples.plugins.data.accessor.PostRepository;
import com.thefirstlineofcode.april.admin.react.admin.BootMenuItem;
import com.thefirstlineofcode.april.admin.react.admin.Resource;

@RestController
@RequestMapping("/posts")
@Resource(name = "posts",
	listViewName = "PostListView",
	showViewName = "PostShowView",
	editViewName =  "PostEditView",
	createViewName = "PostCreateView",
	menuItem = @BootMenuItem(label = "application.title.posts", priority = BootMenuItem.PRIORITY_MEDIUM + 400))
public class PostController extends BasicCrudController<Long, Post> {
	@Autowired
	private PostService postService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private PostRepository postRepository;
	
	@Override
	protected void validUpdated(Post updated, Post existed) {
		validTitleAndBodyNotNullAndNotEmpty(updated);
	}

	private void validTitleAndBodyNotNullAndNotEmpty(Post post) {
		if (post.getTitle() == null || "".equals(post.getTitle()))
			throw new ValidationException("Null post title.");
		
		if (post.getBody() == null || "".equals(post.getBody()))
			throw new ValidationException("Null post body.");
	}
	
	@Override
	protected Post doCreateResource(Post created) {
		validCreated(created);
		
		Long id = postRepository.count() + 1;
		created.setId(id);
		
		return getService().create(created);
	}
	
	@Override
	protected void validCreated(Post created) throws ValidationException {
		validTitleAndBodyNotNullAndNotEmpty(created);
		
		if (created.getUserId() == null)
			throw new ValidationException("Null user ID.");			
		
		if (!isUserIdVaild(created.getUserId()))
			throw new ValidationException(String.format("User which's ID is '%s' not found.", created.getUserId()));
	}
	
	private boolean isUserIdVaild(Long userId) {
		return userService.exists(userId);
	}

	@Override
	public IBasicCrudService<Long, Post> getService() {
		return postService;
	}
}
