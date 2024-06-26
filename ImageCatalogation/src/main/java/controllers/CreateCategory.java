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

import dao.CategoryDAO;

/**
 * Servlet implementation class CheckCategoryLink
 */
@WebServlet("/CreateCategory")
public class CreateCategory extends HttpServlet {
	private static final long serialVersionUID = 1L;
      private TemplateEngine templateEngine;
    	private Connection connection;
    	 
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CreateCategory() {
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
   			throw new UnavailableException("ERROR WITH THE DATABASE DRIVERS");
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
	 * Creates a new category with the selected father
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String name = null;
		String fatherID=null;
		boolean badRequest = false;
		try {
			name = request.getParameter("name");
			fatherID = request.getParameter("father");
			
			if (name.isEmpty()||fatherID.isEmpty()) {
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
		

		try {
	
			category.createCategory(name,fatherID);
		} catch (Exception e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"CANNOT CREATE A NEW CATEGORY");
			return;
		}
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