package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import beans.Category;


public class CategoryDAO {
	private Connection con;
	
	public CategoryDAO(Connection connection){
		this.con=connection;
	}
	public Category checkCategory(String id) throws SQLException {
		
		String query = "SELECT id,name FROM category WHERE id = ?";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setString(1, id);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst()) 
					return null;
				else {
					result.next();
					Category category = new Category();
					category.setId(id);
					category.setName(result.getString("name"));
					addSubparts(category,id);
					return category;
				}
			}
		}
	}
	
	
	public void addSubparts(Category category,String ID) throws SQLException {
		String query=
				"SELECT child FROM relationships WHERE father=?";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setString(1,ID);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst())
					return;
				while(result.next()) {
				Category child=checkCategory(result.getString("child"));
				category.addSubpart(child, child.getId());
			    }
			}
		}
	}
		
	


     public void createCategory(String name, String id) throws SQLException {
    	 String idchild=getNewID(id);
    	 if(idchild==null) {
    		return; 
    	 }
    	 String idfather=id;
    	
    	 String query=
    			 "INSERT into db_images.Category(id,name) VALUES(?,?)";
    	 try(PreparedStatement pstatement = con.prepareStatement(query);){
    		 pstatement.setString(1, idchild);
    		 pstatement.setString(2, name);
    		 pstatement.executeUpdate();
    	 }
    	 query=
    			 "INSERT into db_images.relationships(father,child) VALUES(?,?)";
    	 try(PreparedStatement pstatement = con.prepareStatement(query);){
    		 pstatement.setString(1, idfather);
    		 pstatement.setString(2, idchild);
    		 pstatement.executeUpdate();
    	 }
    }
     
     
     
	public String getNewID(String fatherID) {
	     Category father;
		try {
		 father=checkCategory(fatherID);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		
		if(father.getSubparts().keySet().size()>8) {
			return null;
		}
		String idchild = fatherID + String.valueOf(father.getSubparts().keySet().size() + 1);
		return idchild;
	}
	

	
	public List<Category> findAllCategories() throws SQLException{
		List<Category> categories = new ArrayList<Category>();
		String query = "SELECT * FROM Category";
		try (PreparedStatement pstatement = con.prepareStatement(query);){
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					Category category = new Category();
					category.setId(result.getString("id"));
					category.setName(result.getString("name"));
					categories.add(category);
				}
			}
		}
		return categories;
	}
	
}


