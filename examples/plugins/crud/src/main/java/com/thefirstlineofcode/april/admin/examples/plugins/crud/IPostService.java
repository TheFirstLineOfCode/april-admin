package com.thefirstlineofcode.april.admin.examples.plugins.crud;

import com.thefirstlineofcode.april.admin.examples.plugins.data.accessor.Post;
import com.thefirstlineofcode.april.admin.framework.crud.IBasicCrudService;

public interface IPostService extends IBasicCrudService<Long, Post> {}
