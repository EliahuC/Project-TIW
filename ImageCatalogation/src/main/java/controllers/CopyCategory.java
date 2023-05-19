package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

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
 * Servlet implementation class CopyCategory
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
        // TODO Auto-generated constructor stub
    }
    
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
    
    //doPost Put the copied parts in the data base
    //doGet ?create the category selected?

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String fatherID=null;
		boolean badRequest = false;
		try {
			fatherID = request.getParameter("father");
			if (fatherID.isEmpty()) {
				badRequest = true;
			}
		} catch (NullPointerException | NumberFormatException e) {
			badRequest = true;
		}

		if (badRequest) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing or incorrect parameters");
			return;
		}
		CategoryDAO category= new CategoryDAO(connection);
		try {
			copiedCategory=category.checkCategory(fatherID);
		} catch (Exception e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"Error in creating the product in the database");
			return;
		}
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String destination=null;
		boolean badRequest = false;
		try {
			destination = request.getParameter("father");
			if (destination.isEmpty()) {
				badRequest = true;
			}
		} catch (NullPointerException | NumberFormatException e) {
			badRequest = true;
		}

		if (badRequest) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing or incorrect parameters");
			return;
		}
		
		
		//Selection of all the parts to add in the database
		HashMap<Category,ArrayList<Category>> tree=new HashMap<>();
		
		for(Category c: copiedCategory.getSubparts().keySet()) {
			  ArrayList<Category> cC=new ArrayList<>();
			  addSubparts(cC,c);
			  tree.put(c, cC);
			}
		
		
		CategoryDAO category= new CategoryDAO(connection);
		try {
	        category.createCategory(copiedCategory.getName(),destination);	
		} catch (Exception e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"Error in creating the product in the database");
			return;
		}
		String newDestination=category.getNewID(destination);
		if(newDestination==null)
			newDestination=String.valueOf(Integer.parseInt(destination)-1);
		else
			newDestination=String.valueOf(Integer.parseInt(newDestination)-1);
		//adding the sons categories
		for(int i=1;i<tree.size();i++) {
			try {
				destination=category.getNewID(newDestination);
				category.createCategory(tree.get(i).get(0).getName(),destination);
				for(int j=1;j<tree.get(i).size();j++) {
		        category.createCategory(tree.get(i).get(j).getName(),destination);
		        }
			} catch (Exception e) {
				e.printStackTrace();
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
						"Error in creating the product in the database");
				return;
			}
		}
		
		 String ctxpath = getServletContext().getContextPath();
		 String path = ctxpath + "/GoToHomePage";
		 response.sendRedirect(path);
		}
	
	
	private void addSubparts(ArrayList<Category> copiedCategories,Category c) {
		copiedCategories.add(c);
		for(Category c1: c.getSubparts().keySet()) {
			addSubparts(copiedCategories,c1);
		}
	}

}
