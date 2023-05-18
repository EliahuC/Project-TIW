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
		String name=null;
		String query =
				  "SELECT  id, name"
				+ "FROM category"
				+ "WHERE id = ?";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setString(1, id);
			pstatement.setString(2, name);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst()) 
					return null;
				else {
					result.next();
					Category category = new Category();
					category.setId(Integer.parseInt(id));
					category.setId(Integer.parseInt(id));
					category.setName(name);
					addSubparts(category,String.valueOf(category.getId()));
					return category;
				}
			}
		}
	}
	
	
	public void addSubparts(Category category,String ID) throws SQLException {
		String query=
				"SELECT child"
				+ "FROM relationships"
				+ "WHERE father=?";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setString(1,ID);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst())
					return;
				else {
					result.next();
					Category child=checkCategory(ID);
					category.addSubpart(child, child.getId());
				}
				result.next();
				
			    String [] ids=query.split(" ");
			    for(String s: ids) {
				Category child=checkCategory(s);
				category.addSubpart(child, child.getId());
			    }
			}
			
		}
		
	}


     public void createCategory(String name, String id) throws SQLException {
    	 Integer idchild=getNewID(id);
    	 if(idchild==-1) {
    		return; 
    	 }
    	 Integer idfather=Integer.parseInt(id);
    	
    	 String query=
    			 "INSERT into db_images.Category(id,name)"
    			 + "VALUES(?,?)";
    	 try(PreparedStatement pstatement = con.prepareStatement(query);){
    		 pstatement.setInt(1, idchild);
    		 pstatement.setString(2, name);
    		 pstatement.executeUpdate();
    	 }
    	 query=
    			 "INSERT into db_images.relationships(father,child)"
    			 + "VALUES(?,?)";
    	 try(PreparedStatement pstatement = con.prepareStatement(query);){
    		 pstatement.setInt(1, idfather);
    		 pstatement.setInt(2, idchild);
    		 pstatement.executeUpdate();
    	 }
    }
     
     
     
	private int getNewID(String fatherID) {
		Category father=null;
		try {
		     father=checkCategory(fatherID);
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
		if(father.getSubparts().keySet().size()>8) {
			return -1;
		}
		String idchild = fatherID + String.valueOf(father.getSubparts().keySet().size() + 1);
		return Integer.parseInt(idchild);
	}
	

	
	public List<Category> findAllCategories() throws SQLException{
		List<Category> categories = new ArrayList<Category>();
		String query = "SELECT * FROM Category";
		try (PreparedStatement pstatement = con.prepareStatement(query);){
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					Category category = new Category();
					category.setId(result.getInt("id"));
					category.setName(result.getString("name"));
					categories.add(category);
				}
			}
		}
		return categories;
	}
	
}


