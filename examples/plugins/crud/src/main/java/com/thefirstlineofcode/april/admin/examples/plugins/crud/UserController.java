package com.thefirstlineofcode.april.admin.examples.plugins.crud;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thefirstlineofcode.april.admin.core.crud.BasicCrudController;
import com.thefirstlineofcode.april.admin.core.crud.IBasicCrudService;
import com.thefirstlineofcode.april.admin.examples.plugins.data.accessor.User;
import com.thefirstlineofcode.april.admin.react.admin.BootMenuItem;
import com.thefirstlineofcode.april.admin.react.admin.Resource;

@RestController
@RequestMapping("/users")
@Resource(name = "users", recordRepresentation = "name", listViewName = "UserListView", menuItem = @BootMenuItem(label = "ca.title.users", priority = BootMenuItem.PRIORITY_MEDIUM + 500))
public class UserController extends BasicCrudController<Long, User> {
	@Autowired
	private UserService userService;
	
	@Override
	public IBasicCrudService<Long, User> getService() {
		return userService;
	}
	
	@Override
	protected boolean isDeleteResourceEnabled() {
		return false;
	}
	
	@Override
	protected boolean isUpdateResourceEnabled() {
		return false;
	}
}
