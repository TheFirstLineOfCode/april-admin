package com.thefirstlineofcode.april.admin.react.admin.crud.adapter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class TreeMenuSupportedResource {
	private String name;
	private String label;
	private boolean parentMenu;
	private String menuParent;
	private String recordRepresentation;
	private String listViewName;
	private String showViewName;
	private String createViewName;
	private String editViewName;
	
	public TreeMenuSupportedResource(String name, String label, boolean parentMenu, String menuParent) {
		this.name = name;
		this.label = label;
		this.parentMenu = parentMenu;
		this.menuParent = menuParent; 
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getLabel() {
		return label;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}
	
	public boolean isParentMenu() {
		return parentMenu;
	}
	
	public void setParentMenu(boolean parentMenu) {
		this.parentMenu = parentMenu;
	}
	
	public String getMenuParent() {
		return menuParent;
	}
	
	public void setMenuParent(String menuParent) {
		this.menuParent = menuParent;
	}
	
	public String getRecordRepresentation() {
		return recordRepresentation;
	}

	public void setRecordRepresentation(String recordRepresentation) {
		this.recordRepresentation = recordRepresentation;
	}

	public String getListViewName() {
		return listViewName;
	}

	public void setListViewName(String listViewName) {
		this.listViewName = listViewName;
	}
	
	public String getShowViewName() {
		return showViewName;
	}
	
	public void setShowViewName(String showViewName) {
		this.showViewName = showViewName;
	}

	public String getCreateViewName() {
		return createViewName;
	}

	public void setCreateViewName(String createViewName) {
		this.createViewName = createViewName;
	}

	public String getEditViewName() {
		return editViewName;
	}

	public void setEditViewName(String editViewName) {
		this.editViewName = editViewName;
	}
}
