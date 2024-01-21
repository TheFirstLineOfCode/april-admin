package com.thefirstlineofcode.april.admin.core.crud.jpa;

import com.thefirstlineofcode.april.admin.core.data.IIdProvider;

public abstract class LongTypeIdBasicCrudService<T extends IIdProvider<Long>> extends BasicCrudService<Long, T> {
	@Override
	protected Long[] getIds(String[] sIds) {
		Long[] ids = new Long[sIds.length];
		
		for (int i = 0; i < sIds.length; i++) {
			ids[i] = Long.valueOf(sIds[i]);
		}
		
		return ids;
	}
}
