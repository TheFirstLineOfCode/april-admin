package com.thefirstlineofcode.april.admin.framework.crud;

import com.thefirstlineofcode.april.admin.framework.data.IDataProtocolAdapter;

public interface IDataProtocolAdapterAware {
	void setDataProtocolAdapter(IDataProtocolAdapter dataProtocolAdapter);
}
