package com.thefirstlineofcode.april.admin.examples.plugins.crud;

import com.thefirstlineofcode.april.admin.examples.plugins.data.accessor.User;
import com.thefirstlineofcode.april.admin.framework.crud.IBasicCrudService;

public interface IUserService extends IBasicCrudService<Long, User> {}