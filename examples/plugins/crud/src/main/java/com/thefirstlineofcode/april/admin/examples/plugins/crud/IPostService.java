package com.thefirstlineofcode.april.admin.examples.plugins.crud;

import com.thefirstlineofcode.april.admin.core.crud.IBasicCrudService;
import com.thefirstlineofcode.april.admin.examples.plugins.data.accessor.Post;

public interface IPostService extends IBasicCrudService<Long, Post> {}
