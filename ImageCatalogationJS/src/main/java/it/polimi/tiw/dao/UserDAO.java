package it.polimi.tiw.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import it.polimi.tiw.beans.User;

public class UserDAO {
	private final Connection connection;
	
	public UserDAO(Connection connection) {
		this.connection = connection;
	}
	
	public User checkCredentials(String username, String password) throws SQLException {
		String query = "SELECT id, username, name, surname FROM user WHERE username = ? AND password = ?";
		try (PreparedStatement pStatement = connection.prepareStatement(query)) {
			pStatement.setString(1, username);
			pStatement.setString(2, password);
			try (ResultSet result = pStatement.executeQuery()) {
				if(!result.isBeforeFirst()) // there is no result
					return null;
				else {
					result.next();
					User user = new User();
					user.setId(result.getInt("id"));
					user.setUsername(result.getString("username"));
					user.setName(result.getString("name"));
					user.setSurname(result.getString("surname"));
					return user;
				}
			}
		}
	}
}
