package controller;

import entity.Brand;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.BrandService;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "BrandController", value = "/brands")
public class BrandController  extends HttpServlet {
    private final BrandService brandService = new BrandService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
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
        List<Brand> brands = brandService.getByNameContains(keyword);
        req.setAttribute("brands", brands);
        req.setAttribute("targetProductId", productId);
        req.getRequestDispatcher("brands/homeBrand.jsp").forward(req, resp);
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
                addBrand(req, resp);
                break;
            case "delete":
                delete(req, resp);
                break;
            case "edit":
                editBrand(req, resp);
                break;
            default:
                showNotFound(req, resp);
        }
    }

    private void addBrand(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name = req.getParameter("name");
        Brand brand = new Brand(name);
        String targetProductId = req.getParameter("targetProductId");
        brandService.add(brand);
        sendRedirectWithProductId(req, resp, targetProductId);
    }

    private void editBrand(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int id = Integer.parseInt(req.getParameter("id"));
        String name = req.getParameter("name");
        Brand brand = new Brand(id, name);
        String targetProductId = req.getParameter("targetProductId");
        brandService.update(id, brand);
        sendRedirectWithProductId(req, resp, targetProductId);
    }

    private void delete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int id = Integer.parseInt(req.getParameter("id"));
        String targetProductId = req.getParameter("targetProductId");
        brandService.delete(id);
        sendRedirectWithProductId(req, resp, targetProductId);
    }

    private void sendRedirectWithProductId(HttpServletRequest req, HttpServletResponse resp, String productId) throws IOException {
        String url = req.getContextPath() + "/brands?page=home";
        if (productId != null && !productId.isEmpty() && !productId.equals("null")) {
            url += "&productId=" + productId;
        }
        resp.sendRedirect(url);
    }
}
