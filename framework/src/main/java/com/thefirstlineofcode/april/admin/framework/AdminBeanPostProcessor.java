package com.thefirstlineofcode.april.admin.framework;

import java.util.List;

import org.pf4j.PluginManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import com.thefirstlineofcode.april.admin.framework.crud.IDataProtocolAdapterAware;
import com.thefirstlineofcode.april.admin.framework.data.IDataProtocolAdapter;
import com.thefirstlineofcode.april.boot.IPluginManagerAware;

public class AdminBeanPostProcessor implements BeanPostProcessor, IPluginManagerAware {
	private static final Logger logger = LoggerFactory.getLogger(AdminBeanPostProcessor.class);
	
	private IDataProtocolAdapter dataProtocolAdapter;

	@Override
	public void setPluginManager(PluginManager pluginManager) {
		dataProtocolAdapter = getDataProtocolAdapter(pluginManager);
	}
	
	private IDataProtocolAdapter getDataProtocolAdapter(PluginManager pluginManager) {
		List<IDataProtocolAdapter> dataProtocolAdapters = pluginManager.getExtensions(IDataProtocolAdapter.class);
		if (dataProtocolAdapters == null || dataProtocolAdapters.size() == 0) {
			logger.error("Error: Data protocol adapter not found.");
			throw new RuntimeException("Error: Data protocol adapter not found. You may need to configure a data protocol adapter plugin.");
		}
		
		if (dataProtocolAdapters.size() != 1)
			throw new RuntimeException("Multiple  data protocol adapters found. Something is wrong.");
		
		return dataProtocolAdapters.get(0);
	}
	
	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		if (bean instanceof IDataProtocolAdapterAware) {
			((IDataProtocolAdapterAware)bean).setDataProtocolAdapter(dataProtocolAdapter);
		}
		
		return bean;
	}
}
