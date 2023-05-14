package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import beans.Category;


public class CategoryDAO {
	private Connection con;
	
	public CategoryDAO(Connection connection){
		this.con=connection;
	}
	public Category checkCategory(String ID) throws SQLException {
		String name=null;
		String query =
				  "SELECT  id, name"
				+ "FROM category"
				+ "WHERE id = ?";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setString(1, ID);
			pstatement.setString(2, name);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst()) 
					return null;
				else {
					result.next();
					Category category = new Category();
					category.setId(Integer.parseInt(ID));
					category.setName(name);
					addSubparts(category,String.valueOf(category.getId()));
					return category;
				}
			}
		}
	}
	private void addSubparts(Category category,String ID) throws SQLException {
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
				
			}
			
		}
		
	}
}
