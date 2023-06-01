package it.polimi.tiw.controllers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.polimi.tiw.beans.CategoryChanges;
import it.polimi.tiw.dao.CategoryDAO;
import it.polimi.tiw.utils.ConnectionHandler;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;


@WebServlet("/SaveCategoriesJS")
@MultipartConfig
public class SaveCategoriesJS extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private Connection connection;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public SaveCategoriesJS() {
        super();
    }


    /**
     * Init method of the servlet
     */
    public void init() throws ServletException {
        connection = ConnectionHandler.getConnection(getServletContext());
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws  IOException {
        doPost(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws  IOException {
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        try {
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null)
                stringBuilder.append(line);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("Missing or incorrect parameters");
            e.printStackTrace();
        }

        Gson gson = new Gson();
        Type categoryListType = new TypeToken<ArrayList<CategoryChanges>>() {
        }.getType();
        ArrayList<CategoryChanges> categoryUpdateArray;

        try {
            categoryUpdateArray = gson.fromJson(stringBuilder.toString(), categoryListType);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("Missing or incorrect parameters");
            e.printStackTrace();
            return;
        }

        CategoryDAO category = new CategoryDAO(connection);

        try {
            connection.setAutoCommit(false);
            for (CategoryChanges c : categoryUpdateArray) {
                String copiedCategoryNewId = c.getNewId();
                String name=category.checkCategory(c.getCategoryId()).getName();
                category.createCategory(name, c.getNewFatherId());
                category.paste(c.getCategoryId(), copiedCategoryNewId);
            }
        } catch (Exception e) {
            try {
                connection.rollback();
            } catch (SQLException e1) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().println("Cannot RollBack");
                return;
            }
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("Cannot Save");
            return;
        }

        try {
            connection.commit();
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("Cannot Commit on the database");
            return;
        }
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        response.setCharacterEncoding("ISO-8859-1");
        response.getWriter().print(category);
    }
}