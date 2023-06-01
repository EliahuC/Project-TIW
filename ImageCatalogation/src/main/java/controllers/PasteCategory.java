package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import beans.Category;
import dao.CategoryDAO;

/**
 * Servlet implementation class PasteCategory: it's used when the link "Copia qui" is pressed
 */
@WebServlet("/PasteCategory")
public class PasteCategory extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection connection;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public PasteCategory() {
        super();
    }

    /**
     * Init method of the servlet
     */
    public void init() throws ServletException {
    	try {
			ServletContext context = getServletContext();
			String driver = context.getInitParameter("dbDriver");
			String url = context.getInitParameter("dbUrl");
			String user = context.getInitParameter("dbUser");
			String password = context.getInitParameter("dbPassword");
			Class.forName(driver);
			connection = DriverManager.getConnection(url, user, password);

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new UnavailableException("ERROR WITH DATABASE DRIVERS");
		} catch (SQLException e) {
			e.printStackTrace();
			throw new UnavailableException("DATABASE CONNECTION ERROR");
		}
    	
    	ServletContext servletContext = getServletContext();
    	ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
    	templateResolver.setTemplateMode(TemplateMode.HTML);
    	this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
    }

    /**
     * Pastes the category and its subtree into a selected father calling createCategory
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String destination=null;
		boolean badRequest = false;
		Category copiedCategory = (Category) request.getSession().getAttribute("copiedCategory");
		
		try {
			destination = request.getParameter("categoryId");
			if (destination.isEmpty()) {
				badRequest = true;
			}
		} catch (NullPointerException | NumberFormatException e) {
			badRequest = true;
		}

		if (badRequest) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "MISSING OR INCORRECT PARAMETERS");
			return;
		}
		
		CategoryDAO category= new CategoryDAO(connection);
		String copiedCategoryNewId = new String();
		
		try {
			connection.setAutoCommit(false);
			copiedCategoryNewId = category.getNewID(destination);
	        category.createCategory(copiedCategory.getName(), destination);
	        category.paste(copiedCategory.getId(), copiedCategoryNewId);
		} catch (Exception e) {
			try {
				connection.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"CANNOT CREATE A NEW CATEGORY");
			return;
		}
		try {
			connection.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
        //rimozione variabile storeata
	    this.getServletConfig().getServletContext().removeAttribute("copiedCategory");
	    String ctxpath = getServletContext().getContextPath();
		 String path = ctxpath + "/GoToHomePage";
		 response.sendRedirect(path);
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
