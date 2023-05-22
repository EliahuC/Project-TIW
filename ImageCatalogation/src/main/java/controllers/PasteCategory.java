package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
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
			throw new UnavailableException("Can't load database driver");
		} catch (SQLException e) {
			e.printStackTrace();
			throw new UnavailableException("Couldn't get db connection");
		}
    	
    	ServletContext servletContext = getServletContext();
    	ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
    	templateResolver.setTemplateMode(TemplateMode.HTML);
    	this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
    }

    /**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("Sei dentro PasteCategory");
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
		List<Category> categories = null;
		String copiedCategoryNewId = new String();
		
		try {
			copiedCategoryNewId = category.getNewID(destination);
	        category.createCategory(copiedCategory.getName(), destination);
	        category.paste(copiedCategory.getId(), copiedCategoryNewId);
		} catch (Exception e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"CANNOT CREATE A NEW CATEGORY");
			return;
		}
		
		/*String newDestination = category.getNewID(destination);
		if(newDestination==null)
		{
			char lastDigit = destination.charAt(destination.length() - 1);
			int lastDigitValue = Character.getNumericValue(lastDigit);
			lastDigitValue--;
			char newLastDigit = Character.forDigit(lastDigitValue, 10);
			newDestination = destination.substring(0, destination.length() - 1) + newLastDigit;


			//newDestination=String.valueOf(Long.parseLong(destination)-1);
			//String support=null;
			//String support2=null;
			
			
			//support2=String.valueOf(Integer.parseInt( String.valueOf(destination.charAt(destination.length()-1))));
			//support=destination.substring(0, destination.length()-2);
			//newDestination=support+support2;
		}
			
		else {
			char lastDigit = newDestination.charAt(newDestination.length() - 1);
			int lastDigitValue = Character.getNumericValue(lastDigit);
			lastDigitValue--;
			char newLastDigit = Character.forDigit(lastDigitValue, 10);
			newDestination = newDestination.substring(0, newDestination.length() - 1) + newLastDigit;

			//newDestination=String.valueOf(Long.parseLong(newDestination)-1);
			//String support=null;
			//String support2=null;
			//support2=String.valueOf(Integer.parseInt( String.valueOf(newDestination.charAt(newDestination.length()-1))));
			//support=newDestination.substring(0, newDestination.length()-2);
			//newDestination=support+support2;
		}
			
		for(Category c: copiedCategory.getSubparts().keySet()) {
			putSubparts(c,newDestination,category,response);
		}*/
		
        //rimozione variabile storeata
	    this.getServletConfig().getServletContext().removeAttribute("copiedCategory");
	    String ctxpath = getServletContext().getContextPath();
		 String path = ctxpath + "/GoToHomePage";
		 response.sendRedirect(path);
	}

	
	/*private void putSubparts(Category c, String newDestination,CategoryDAO category,HttpServletResponse response) throws IOException {
		try {
	        category.createCategory(c.getName(),newDestination);	
		} catch (Exception e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"CANNOT CREATE A NEW CATEGORY");
			return;
		}
		
		String destination=category.getNewID(newDestination);
		
		if(destination==null) {
			char lastDigit = newDestination.charAt(newDestination.length() - 1);
			int lastDigitValue = Character.getNumericValue(lastDigit);
			lastDigitValue--;
			char newLastDigit = Character.forDigit(lastDigitValue, 10);
			destination = newDestination.substring(0, newDestination.length() - 1) + newLastDigit;

			//destination=String.valueOf(Long.parseLong(newDestination)-1);
			//String support=null;
			//String support2=null;
			//support2=String.valueOf(Integer.parseInt( String.valueOf(newDestination.charAt(newDestination.length()-1))));
			//support=newDestination.substring(0, newDestination.length()-2);
			//destination=support+support2;
		}	
		else {
			char lastDigit = destination.charAt(destination.length() - 1);
			int lastDigitValue = Character.getNumericValue(lastDigit);
			lastDigitValue--;
			char newLastDigit = Character.forDigit(lastDigitValue, 10);
			destination = destination.substring(0, destination.length() - 1) + newLastDigit;

			//destination=String.valueOf(Long.parseLong(destination)-1);
			//String support=null;
			//String support2=null;
			//support2=String.valueOf(Integer.parseInt( String.valueOf(destination.charAt(destination.length()-1))));
			//support=destination.substring(0, destination.length()-2);
			//destination=support+support2;
		}
		
		
		for(Category c1: c.getSubparts().keySet()) {
			putSubparts(c1,destination,category,response);
		}
	}*/
	
	
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
