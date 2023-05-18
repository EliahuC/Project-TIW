package beans;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Category implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private String id;
	private String name;
	private Boolean isTop = false;
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
	public void addSubpart(Category name, String q) {
		children.put(name, q);
	}
	public void removeSubpart(Category p) {
		children.remove(p);
	}
	public void setIsTop(boolean x) {
		this.isTop=x;
	}
	
	public boolean getIsTop() {
		return isTop;
	}
}