
package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import it.polimi.tiw.dao.CategoryDAO;
import it.polimi.tiw.utils.ConnectionHandler;
import org.apache.commons.lang.StringEscapeUtils;


/**
 * Servlet implementation class CheckCategoryLink
 */
@WebServlet("/CreateCategoryJS")
@MultipartConfig
public class CreateCategoryJS extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private Connection connection;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public CreateCategoryJS() {
        super();
    }


    /**
     * Init method of the servlet
     */
    public void init() throws ServletException {
        connection = ConnectionHandler.getConnection(getServletContext());
    }

    /**
     * Creates a new category with the selected father
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request,response);
    }
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String name = null;
        String fatherID=null;
        try {
            name = StringEscapeUtils.escapeJava(request.getParameter("name"));
            fatherID = StringEscapeUtils.escapeJava(request.getParameter("father"));

            if (name.isEmpty()||fatherID.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().println("Incorrect or missing request parameters");
                return;
            }

        } catch (NullPointerException | NumberFormatException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("Incorrect or missing request parameters");
            return;
        }


        CategoryDAO category= new CategoryDAO(connection);


        try {
            category.createCategory(name,fatherID);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("Not possible to create category");
            return;
        }
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        response.setCharacterEncoding("ISO-8859-1");
        response.getWriter().print(category);
    }
    @Override
    public void destroy() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e){

            }
        }
    }
}