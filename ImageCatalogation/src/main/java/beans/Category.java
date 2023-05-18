package beans;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Category implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private int id;
	private String name;
	private Boolean isTop = false;
	private Map<Category, Integer> children = new HashMap<Category, Integer>();
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Map<Category, Integer> getSubparts() {
		return children;
	}
	public void addSubpart(Category name, Integer q) {
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