package com.thefirstlineofcode.april.admin.examples.plugins.crud;

import com.thefirstlineofcode.april.admin.core.crud.IBasicCrudService;
import com.thefirstlineofcode.april.admin.examples.plugins.data.accessor.User;

public interface IUserService extends IBasicCrudService<Long, User> {}
