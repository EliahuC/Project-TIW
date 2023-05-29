package it.polimi.tiw.beans;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Bean of the category
 */
public class Category implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private String id;
	private String name;
	private Boolean copied = false;
	private Boolean alreadyCopied=false;
	private Map<Category, String> children = new HashMap<Category, String>();
	
	public String getId() {
		return id;
	}
	public void setId(String id2) {
		this.id = id2;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Map<Category, String> getSubparts() {
		return children;
	}
	public void addSubpart(Category category, String q) {
		children.put(category, q);
	}
	public void removeSubpart(Category c) {
		children.remove(c);
	}
	public void setCopied(boolean x) {
		this.copied=x;
	}
	
	public boolean getCopied() {
		return copied;
	}
	public void setAlreadyCopied(boolean b) {
		this.alreadyCopied=b;
	}
	public boolean getAlreadyCopied(){
		return this.alreadyCopied;
	}
}