package controller;

import entity.Category;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.CategoryService;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "CategoryController", value = "/categories")
public class CategoryController extends HttpServlet {
    private final CategoryService categoryService = new CategoryService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String page = req.getParameter("page");

        switch (page) {
            case "home":
                showHomePage(req, resp);
                break;
            default:
                showNotFound(req, resp);
        }
    }

    private void showHomePage(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String keyword = req.getParameter("keyword");
        String productId = req.getParameter("productId");
        List<Category> categories = categoryService.getByNameContains(keyword);
        req.setAttribute("categories", categories);
        req.setAttribute("targetProductId", productId);
        req.getRequestDispatcher("categories/homeCategory.jsp").forward(req, resp);
    }

    private void showNotFound(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        RequestDispatcher dispatcher = req.getRequestDispatcher("errors/not-found.jsp");
        dispatcher.forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        if (action == null) action = "";
        switch (action) {
            case "add":
                addCategory(req, resp);
                break;
            case "delete":
                delete(req, resp);
                break;
            case "edit":
                editCategory(req, resp);
                break;
            default:
                showNotFound(req, resp);
        }
    }

    private void addCategory(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name = req.getParameter("name");
        Category category = new Category(name);
        String targetProductId = req.getParameter("targetProductId");
        categoryService.add(category);
        sendRedirectWithProductId(req, resp, targetProductId);
    }

    private void editCategory(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int id = Integer.parseInt(req.getParameter("id"));
        String name = req.getParameter("name");
        Category category = new Category(id, name);
        String targetProductId = req.getParameter("targetProductId");
        categoryService.update(id, category);
        sendRedirectWithProductId(req, resp, targetProductId);
    }

    private void delete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int id = Integer.parseInt(req.getParameter("id"));
        String targetProductId = req.getParameter("targetProductId");
        categoryService.delete(id);
        sendRedirectWithProductId(req, resp, targetProductId);
    }

    private void sendRedirectWithProductId(HttpServletRequest req, HttpServletResponse resp, String productId) throws IOException {
        String url = req.getContextPath() + "/categories?page=home";
        if (productId != null && !productId.isEmpty() && !productId.equals("null")) {
            url += "&productId=" + productId;
        }
        resp.sendRedirect(url);
    }
}
