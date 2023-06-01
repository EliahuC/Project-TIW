package it.polimi.tiw.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.beans.Category;

/**
 * Dao that handles all the categories and their methods
 */
public class CategoryDAO {
    private final Connection connection;
    public CategoryDAO(Connection connection){
        this.connection =connection;
    }


    /**
     * The method creates copiedCategory requested by CopyCategory and calls addSubparts to add all the children to their father
     * @param id of the father
     * @return the father category and its subtree
     * @throws SQLException server exception
     */
    public Category checkCategory(String id) throws SQLException {

        String query = "SELECT * FROM category WHERE id = ?";
        try (PreparedStatement pstatement = connection.prepareStatement(query)) {
            pstatement.setString(1, id);
            try (ResultSet result = pstatement.executeQuery()) {
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
     * selects the children from the category passed and for each call the method checkCategory to create their subtree
     * @param category to add to the father
     * @param ID of the father
     * @throws SQLException server exception
     */
    public void addSubparts(Category category, String ID) throws SQLException {
        String query="SELECT child FROM relationships WHERE father=?";

        try (PreparedStatement pstatement = connection.prepareStatement(query)) {
            pstatement.setString(1,ID);
            try (ResultSet result = pstatement.executeQuery()) {
                if (!result.isBeforeFirst())
                    return;
                while(result.next()) {
                    Category child=checkCategory(result.getString("child"));
                    category.addSubpart(child, child.getId());
                }
            }
        }
    }

    /**
     * Method that is used to create a new category or to paste an existing category into another one
     * @param name of the category to create/paste
     * @param id of the father category
     * @throws SQLException server exception
     */
    public void createCategory(String name, String id) throws SQLException {
        String idchild=getNewID(id);
        if(idchild==null) {
            return;
        }

        String query=
                "INSERT into db_images.Category(id,name) VALUES(?,?)";
        try(PreparedStatement pstatement = connection.prepareStatement(query)){
            pstatement.setString(1, idchild);
            pstatement.setString(2, name);
            pstatement.executeUpdate();
        }
        if(!id.equals("0")) {
            query=
                    "INSERT into db_images.relationships(father,child) VALUES(?,?)";
            try(PreparedStatement pstatement = connection.prepareStatement(query)){
                pstatement.setString(1, id);
                pstatement.setString(2, idchild);
                pstatement.executeUpdate();
            }
        }
    }


    /**
     * Get the new id of the created child
     * @param fatherID father id
     * @return new id
     * @throws SQLException server exception
     */
    public String getNewID(String fatherID) throws SQLException {
        if(fatherID.equals("0")) {
            String query ="SELECT * FROM category WHERE LENGTH(id) = 1";
            try (PreparedStatement pstatement = connection.prepareStatement(query)) {
                try (ResultSet result = pstatement.executeQuery()) {
                    if (!result.isBeforeFirst()) {
                        return null;
                    }else {
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
        return fatherID + (father.getSubparts().keySet().size() + 1);
    }


    /**
     * pastes the subtree into the selected category
     * @param fatherId father id
     * @param fatherNewId new father id
     * @throws SQLException server exception
     */
    public void paste(String fatherId, String fatherNewId) throws SQLException {
        String query = "SELECT child FROM relationships WHERE father = ?";

        try (PreparedStatement pstatement = connection.prepareStatement(query)) {
            pstatement.setString(1, fatherId);
            try (ResultSet result = pstatement.executeQuery()) {
                if (!result.isBeforeFirst())
                    return;
                while(result.next()) {
                    String query2 = "SELECT * FROM category WHERE id = ?";
                    try (PreparedStatement pstatement1 = connection.prepareStatement(query2)) {
                        pstatement1.setString(1, result.getString("child"));
                        try (ResultSet result1 = pstatement1.executeQuery()) {
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

    /**
     * creates a list with all categories in the database
     * @return the list
     * @throws SQLException server exception
     */
    public List<Category> findAllCategories() throws SQLException{
        List<Category> categories = new ArrayList<>();
        String query = "SELECT * FROM Category";
        try (PreparedStatement pstatement = connection.prepareStatement(query)){
            try (ResultSet result = pstatement.executeQuery()) {
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


    public void updateName(String categoryId, String newName) throws SQLException{
        String query = "UPDATE category SET name = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, newName);
            statement.setString(2, categoryId);
            statement.executeUpdate();
        }
    }
}

