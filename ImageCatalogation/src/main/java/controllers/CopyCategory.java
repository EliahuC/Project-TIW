package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
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
 * Servlet implementation class CopyCategory: it's used when the link "Copia" is pressed
 */
@WebServlet("/CopyCategory")
public class CopyCategory extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection connection;
	private Category copiedCategory;
	
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CopyCategory() {
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
	 * Handles the GET request from Home.html when the link copy is pressed
	 * It takes the id of the selected category, then saves all the categories in the list categories and the entire subtree to
	 * copy in copiedCategory. To conclude, reloads the page with the boolean linkClicked set to true
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("Sei in CopyCategory");
		String fatherID=null;
		boolean badRequest = false;
		
		try {
			fatherID = request.getParameter("categoryId");
			if (fatherID.isEmpty()) {
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
		
		try {
			copiedCategory = category.checkCategory(fatherID);
			ArrayList<String> allCopiedCategories=new ArrayList<>();
			getAllCopied(copiedCategory, allCopiedCategories);
			categories = category.findAllCategories(allCopiedCategories);
		} catch (Exception e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"CANNOT CREATE A NEW CATEGORY");
			return;
		}

		String path = "/WEB-INF/Home.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("allCategories", categories);
		ctx.setVariable("linkClicked", true);
		request.getSession().setAttribute("copiedCategory", copiedCategory);
		templateEngine.process(path, ctx, response.getWriter());
	}
	
	/**
	 * 
	 * @param copiedCategory2
	 * @param allCopiedCategories
	 */
	private void getAllCopied(Category copiedCategory2, ArrayList<String> allCopiedCategories) {
		allCopiedCategories.add(copiedCategory2.getId());
		
		for(Category c:copiedCategory2.getSubparts().keySet()) {
			getAllCopied(c,allCopiedCategories);
		}
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
