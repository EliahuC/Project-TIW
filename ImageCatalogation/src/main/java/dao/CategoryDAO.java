package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import beans.Category;

/**
 * Dao that handles all the categories and their methods
 */
public class CategoryDAO {
	private Connection con;
	private final static ArrayList<String> alreadyCopied=new ArrayList<>();
	public CategoryDAO(Connection connection){
		this.con=connection;
	}
	
	
	/**
	 * The method creates copiedCategory requested by CopyCategory and calls addSubparts to add all the children to their father
	 * @param id of the father
	 * @return the father category and its subtree
	 * @throws SQLException
	 */
	public Category checkCategory(String id) throws SQLException {
		
		String query = "SELECT * FROM category WHERE id = ?";
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
	
	
	/**
	 * selects the children from the category passed and for each calls the method checkCategory to create their subtree
	 * @param category to add to the father
	 * @param ID of the father
	 * @throws SQLException
	 */
	public void addSubparts(Category category, String ID) throws SQLException {
		String query="SELECT child FROM relationships WHERE father=?";
		
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
	
	
	public void setCopied(Category father) throws SQLException {
		String query="SELECT child FROM relationships WHERE father=?";
		
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setString(1, father.getId());
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst())
					return;
				while(result.next()) {
					father.setCopied(true);
					//TODO : chiama setCopied per ogni figlio
			    }
			}
		}
	}
	
	
	/**
	 * Method that is used to create a new category or to paste an existing category into another one
	 * @param name of the category to create/paste
	 * @param id of the father category
	 * @throws SQLException
	 */
	 public void createCategory(String name, String id) throws SQLException {
    	 String idchild=getNewID(id);
    	 System.out.println(idchild + " " + name);
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
     
	 
     /**
      * 
      * @param fatherID
      * @return
     * @throws SQLException 
      */
     public String getNewID(String fatherID) throws SQLException {
	     if(fatherID=="0") {
	    	 String query ="SELECT * FROM category WHERE LENGTH(id) = 1";
	    	 try (PreparedStatement pstatement = con.prepareStatement(query);) {
	 			try (ResultSet result = pstatement.executeQuery();) {
	 				if (!result.isBeforeFirst()) 
	 					return null;
	 				else {
	 					ArrayList<String> n=new ArrayList<>();
	 					while(result.next()) {
	 						n.add(result.getString("id"));
	 					}
	 					int lastNumber=Integer.parseInt(n.get(n.size()-1));
	 					if(lastNumber==9)
	 						return null;
	 					lastNumber++;
	 					return String.valueOf(lastNumber);
	 					}
	 				}
	     }
	     }
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
	

 	public void paste(String fatherId, String fatherNewId) throws SQLException {
 		String query = "SELECT child FROM relationships WHERE father = ?";
 		
 		try (PreparedStatement pstatement = con.prepareStatement(query);) {
 			pstatement.setString(1, fatherId);
 			try (ResultSet result = pstatement.executeQuery();) {
 				if (!result.isBeforeFirst())
 					return;
 				while(result.next()) {
 					String query2 = "SELECT * FROM category WHERE id = ?";
 					try (PreparedStatement pstatement1 = con.prepareStatement(query2);) {
 						pstatement1.setString(1, result.getString("child"));
 						try (ResultSet result1 = pstatement1.executeQuery();) {
 							if (!result1.isBeforeFirst())
 								return;
 							while(result1.next()) {
 								String childNewId = getNewID(fatherNewId);
 								createCategory(result1.getString("name"), fatherNewId);
 								paste(result1.getString("id"), childNewId);
 							}
 						}
 					}
 			    }
 			}
 		}
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


	public List<Category> findAllCategories(ArrayList<String> allCopiedCategories) throws SQLException {
		List<Category> categories = new ArrayList<Category>();
		String query = "SELECT * FROM Category";
		try (PreparedStatement pstatement = con.prepareStatement(query);){
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					Category category = new Category();
					category.setId(result.getString("id"));
					category.setName(result.getString("name"));
					if(allCopiedCategories.contains(category.getId())) {
						category.setCopied(true);
					}
					if(alreadyCopied.contains(category.getId())){
						category.setAlreadyCopied(true);
					}
					categories.add(category);
				}
			}
		}
		alreadyCopied.addAll(allCopiedCategories);
		return categories;
	}
}


