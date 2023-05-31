package it.polimi.tiw.controllers;

import com.google.gson.Gson;
import it.polimi.tiw.dao.CategoryDAO;
import it.polimi.tiw.utils.ConnectionHandler;
import org.apache.commons.lang.StringEscapeUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;


@WebServlet("/CopyCategoryJS")
@MultipartConfig
public class CopyCategoryJS extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private Connection connection;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public CopyCategoryJS() {
        super();
    }


    /**
     * Init method of the servlet
     */
    public void init() throws ServletException {
        connection = ConnectionHandler.getConnection(getServletContext());
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String newID;
        String fatherId;
        try {
            fatherId = StringEscapeUtils.escapeJava(request.getParameter("father"));

            if (fatherId.isEmpty()) {
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
            newID=category.getNewID(fatherId);
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("Not possible to create category");
            return;
        }
        Gson gson=new Gson();
        String json=gson.toJson(newID);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json);

    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        doGet(request, response);
    }


    public void destroy() {
        try {
            ConnectionHandler.closeConnection(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}

