package com.thefirstlineofcode.april.admin.react.admin.crud.adapter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.pf4j.PluginManager;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thefirstlineofcode.april.admin.react.admin.BootMenuItem;
import com.thefirstlineofcode.april.admin.react.admin.CustomView;
import com.thefirstlineofcode.april.admin.react.admin.Resource;
import com.thefirstlineofcode.april.admin.react.admin.StructuralMenu;
import com.thefirstlineofcode.april.boot.IPluginManagerAware;
import com.thefirstlineofcode.april.boot.ISpringConfiguration;

@RestController
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class UiConfigurationController implements IPluginManagerAware, ApplicationContextAware, InitializingBean {
	private List<Resource> resources;
	private List<CustomView> customViews;
	private List<Menu> menus;
	
	private UiConfiguration uiConfiguration;
	
	@GetMapping("/ui-configuration")
	public UiConfiguration getUiConfiguration() {
		return uiConfiguration;
	}

	private List<TreeMenuSupportedResource> generateTreeMenuSupportedResources() {
		return generateTreeMenuSupportedResources(getMenuAndElements(null));
	}

	private List<TreeMenuSupportedResource> generateTreeMenuSupportedResources(List<MenuAndElement> menuAndElements) {
		List<TreeMenuSupportedResource> treeMenuSupportedResources = new ArrayList<>();
		
		for (MenuAndElement menuAndElement : menuAndElements) {
			treeMenuSupportedResources.add(getTreeMenuSupportedResource(menuAndElement));
		}
		
		return treeMenuSupportedResources;
	}

	private TreeMenuSupportedResource getTreeMenuSupportedResource(MenuAndElement menuAndElement) {
		Menu menu = menuAndElement.menu;
		TreeMenuSupportedResource treeMenuSupportedResource = new TreeMenuSupportedResource(menu.name, menu.label, !menu.leaf, "".equals(menu.parent) ? null : menu.parent);
		
		if (menuAndElement.element == null) {
			// NOOP
		} else if (menuAndElement.element instanceof Resource) {
			Resource resource = (Resource)menuAndElement.element;
			
			if (!"".equals(resource.recordRepresentation()))
				treeMenuSupportedResource.setRecordRepresentation(resource.recordRepresentation());
			
			treeMenuSupportedResource.setListViewName(getNullableComponentName(resource.listViewName()));
			treeMenuSupportedResource.setShowViewName(getNullableComponentName(resource.showViewName()));
			treeMenuSupportedResource.setCreateViewName(getNullableComponentName(resource.createViewName()));
			treeMenuSupportedResource.setEditViewName(getNullableComponentName(resource.editViewName()));
		} else if (menuAndElement.element instanceof CustomView) {
			CustomView view = (CustomView)menuAndElement.element;
			treeMenuSupportedResource.setListViewName(view.viewName());
		} else {
			throw new RuntimeException(String.format("Error: Unknown type of element. Element class: '%s'.", menuAndElement.element.getClass()));
		}
		
		return treeMenuSupportedResource;
	}

	private String getNullableComponentName(String componentName) {
		return "".equals(componentName) ? null : componentName;
	}

	private List<MenuAndElement> getMenuAndElements(MenuAndElement parent) {
		List<MenuAndElement> children = findChildren(parent);
		
		
		List<MenuAndElement> allMenuAndElements = new ArrayList<>();
		for (int i = 0; i < children.size(); i++) {
			MenuAndElement child = children.get(i);
			
			if (child.menu.leaf) {
				allMenuAndElements.add(child);
			} else {
				List<MenuAndElement> descendants = findDescendants(child);
				if (!noLeafMenu(descendants)) {
					allMenuAndElements.add(child);
					allMenuAndElements.addAll(descendants);
				}				
			}
		}
		
		return allMenuAndElements;
	}

	private boolean noLeafMenu(List<MenuAndElement> descendants) {
		for (MenuAndElement descendant : descendants) {
			if (descendant.menu.leaf)
				return false;
		}
		
		return true;
	}

	private List<MenuAndElement> findChildren(MenuAndElement parent) {
		String parentMenuName = parent == null ? "" : parent.menu.name;
		
		List<MenuAndElement> children = new ArrayList<>();
		for (Menu menu : menus) {
			if (parentMenuName.equals(menu.parent))
				children.add(new MenuAndElement(menu));
		}
		
		for (Resource resource : resources) {
			if (parentMenuName.equals(resource.menuItem().parent()))
				children.add(new MenuAndElement(getMenu(resource.name(), resource.menuItem()), resource));
		}
		
		for (CustomView customView : customViews) {
			if (parentMenuName.equals(customView.menuItem().parent()))
				children.add(new MenuAndElement(getMenu(customView.name(), customView.menuItem()), customView));
		}
		
		children.sort(new Comparator<MenuAndElement>() {
			@Override
			public int compare(MenuAndElement menuAndElement1, MenuAndElement menuAndElement2) {
				return menuAndElement2.menu.priority - menuAndElement1.menu.priority;
			}
		});
		
		return children;
	}
	
	private List<MenuAndElement> findDescendants(MenuAndElement ancestor) {
		List<MenuAndElement> descendants = new ArrayList<>();
		
		List<MenuAndElement> children = findChildren(ancestor);
		for (int i = 0; i < children.size(); i++) {
			MenuAndElement child = children.get(i);
			descendants.add(child);
			descendants.addAll(findDescendants(child));
		}
		
		return descendants;
	}

	private Menu getMenu(String elementName, BootMenuItem bootMenuItem) {
		return new Menu(elementName, 
				bootMenuItem.label() != null ? bootMenuItem.label() : elementName,
				"".equals(bootMenuItem.parent()) ? null : bootMenuItem.parent(),
				true, bootMenuItem.priority());
	}
	
	private class MenuAndElement {
		public Menu menu;
		public Object element;
		
		public MenuAndElement(Menu menu) {
			this(menu, null);
		}
		
		public MenuAndElement(Menu menu, Object element) {
			this.menu = menu;
			this.element = element;
		}
	}
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		resources = new ArrayList<>();
		customViews = new ArrayList<>();
		
		String[] restControllerBeanNames = applicationContext.getBeanNamesForAnnotation(RestController.class);
		for (String restControllerBeanName : restControllerBeanNames) {
			Class<?> restControllerClass = applicationContext.getType(restControllerBeanName);
			
			Resource[] someResources = restControllerClass.getAnnotationsByType(Resource.class);
			if (someResources != null && someResources.length > 0) {
				for (Resource resource : someResources) {
					resources.add(resource);
				}
			}
			
			CustomView[] someCustomViews = restControllerClass.getAnnotationsByType(CustomView.class);
			if (someCustomViews != null && someCustomViews.length > 0) {
				for (CustomView customView : someCustomViews) {
					customViews.add(customView);
				}
			}
		}
	}

	@Override
	public void setPluginManager(PluginManager pluginManager) {
		menus = new ArrayList<>();
		
		List<Class<? extends ISpringConfiguration>> contributedSpringConfigurationClasses =
				pluginManager.getExtensionClasses(ISpringConfiguration.class);
		if (contributedSpringConfigurationClasses == null || contributedSpringConfigurationClasses.size() == 0) {
			return;
		}
		
		for (Class<?> contributedSpringConfigurationClass : contributedSpringConfigurationClasses) {
			StructuralMenu[] someMenus = contributedSpringConfigurationClass.getAnnotationsByType(StructuralMenu.class);
			
			for (StructuralMenu menu : someMenus) {
				menus.add(getMenu(menu));
			}
		}
	}

	private Menu getMenu(StructuralMenu menu) {
		return new Menu(menu.name(),
				menu.label() != null ? menu.label() : menu.name(),
				menu.parent(), false, menu.priority());
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		uiConfiguration = new UiConfiguration();
		uiConfiguration.setResources(generateTreeMenuSupportedResources());
	}
	
	private class Menu {
		public String name;
		public String label;
		public String parent;
		public boolean leaf;
		public int priority;
		
		public Menu(String name, String label, String parent, boolean leaf, int priority) {
			this.name = name;
			this.label = label;
			this.parent = parent;
			this.leaf = leaf;
			this.priority = priority;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof Menu))
				return false;
			
			Menu other = (Menu)obj;
			if (name != other.name)
				return false;
			
			if (parent == null)
				return other.parent == null;
			
			return parent.equals(other.parent);
		}
	}
}
