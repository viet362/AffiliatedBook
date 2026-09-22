package controller;

import entity.Brand;
import entity.Category;
import entity.Product;
import entity.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import service.BrandService;
import service.CategoryService;
import service.ProductCategoryService;
import service.ProductService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@WebServlet(name = "ProductController", value = "/products")
public class ProductController extends HttpServlet {

    private final ProductService productService = new ProductService();
    private final BrandService brandService = new BrandService();
    private final CategoryService categoryService = new CategoryService();
    private final ProductCategoryService pcService = new ProductCategoryService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String page = req.getParameter("page");
        if (page == null) page = "home";

        switch (page) {
            case "home":
                showHomePage(req, resp);
                break;

            case "detail":
                showDetailPage(req, resp);
                break;

            case "delete":
                deleteProduct(req, resp);
                break;

            case "add":
                showAddPage(req, resp);
                break;

            case "edit":
                showEditPage(req, resp);
                break;

            default:
                try {
                    Integer.parseInt(page);
                    showHomePage(req, resp); // Nếu là số (1, 2, 3...), chạy hàm này
                } catch (NumberFormatException e) {
                    showNotFound(req, resp);
                }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String action = req.getParameter("action");
        if (action == null) action = "";

        switch (action) {
            case "add":
                addProduct(req, resp);
                break;

            case "edit":
                editProduct(req, resp);
                break;

            default:
                showNotFound(req, resp);
        }
    }

    private void showHomePage(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String keyword = req.getParameter("search");
        if (keyword == null) keyword = "";

        String[] brands = req.getParameterValues("brand");
        String[] categories = req.getParameterValues("category");

        int page = 1;
        int pageSize = 6;

        try {
            page = Integer.parseInt(req.getParameter("page"));
            if (page < 1) page = 1;
        } catch (Exception e) {
            page = 1;
        }

        // Lấy data đã phân trang từ DB
        List<Product> products = productService.filter(keyword, brands, categories, page, pageSize);

        // Lấy tổng số sản phẩm (để tính số trang)
        int totalItems = productService.count(keyword, brands, categories);
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);

        if (totalPages == 0) totalPages = 1;
        if (page > totalPages) page = totalPages;

        // Gửi dữ liệu sang JSP
        req.setAttribute("products", products);
        req.setAttribute("brands", brandService.getAll());
        req.setAttribute("categories", categoryService.getAll());

        req.setAttribute("currentPage", page);
        req.setAttribute("totalPages", totalPages);
        req.setAttribute("search", keyword);

        // Favorite
        HttpSession session = req.getSession(false);

        if (session != null) {
            User user = (User) session.getAttribute("currentUser");

            if (user != null) {
                List<Product> favorites = productService.getFavoriteByUser(user.getId());
                req.setAttribute("favorites", favorites);

                Set<Integer> favoriteIds = new HashSet<>();
                for (Product p : favorites) {
                    favoriteIds.add(p.getId());
                }
                req.setAttribute("favoriteIds", favoriteIds);
            }
        }

        req.getRequestDispatcher("products/home.jsp").forward(req, resp);
    }

    private void showDetailPage(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            int id = Integer.parseInt(req.getParameter("id"));
            Product product = productService.getById(id);

            if (product == null) {
                resp.sendRedirect("products?page=home");
                return;
            }

            req.setAttribute("product", product);

            HttpSession session = req.getSession(false);
            if (session != null) {
                User user = (User) session.getAttribute("currentUser");
                if (user != null) {
                    List<Product> favorites = productService.getFavoriteByUser(user.getId());
                    Set<Integer> favoriteIds = new HashSet<>();
                    for (Product p : favorites) {
                        favoriteIds.add(p.getId());
                    }
                    req.setAttribute("favoriteIds", favoriteIds);
                }
            }

            req.getRequestDispatcher("products/detail.jsp").forward(req, resp);
        } catch (NumberFormatException e) {
            resp.sendRedirect("products?page=home");
        }
    }

    private void deleteProduct(HttpServletRequest req,
                               HttpServletResponse resp)
            throws IOException {

        int id = Integer.parseInt(req.getParameter("id"));

        productService.delete(id);

        resp.sendRedirect("/products?action=home");
    }

    public List<Product> filter(String keyword, String[] brands, String[] categories,
                                int page, int pageSize) {
        return productService.filter(keyword, brands, categories, page, pageSize);
    }

    public int count(String keyword, String[] brands, String[] categories) {
        return productService.count(keyword, brands, categories);
    }

    private void showAddPage(HttpServletRequest req,
                             HttpServletResponse resp)
            throws ServletException, IOException {

        req.setAttribute("brands", brandService.getAll());
        req.setAttribute("categories", categoryService.getAll());

        req.getRequestDispatcher("products/add.jsp")
                .forward(req, resp);
    }

    private void showEditPage(HttpServletRequest req,HttpServletResponse resp) throws ServletException, IOException {

        int id = Integer.parseInt(req.getParameter("id"));
        Product product = productService.getById(id);

        if (product != null) {

            List<Category> selectedCategories = pcService.getByProductId(id);

            product.setCategories(selectedCategories);
        }

        req.setAttribute("product", product);
        req.setAttribute("brands", brandService.getAll());
        req.setAttribute("categories", categoryService.getAll());

        req.getRequestDispatcher("products/edit.jsp").forward(req, resp);
    }

    private void addProduct(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String name = req.getParameter("name");
        double price = Double.parseDouble(req.getParameter("price"));
        String image = req.getParameter("image");
        String summary = req.getParameter("summary");
        String alink = req.getParameter("alink");

        int brandId = Integer.parseInt(req.getParameter("brandId"));
        Brand brand = new Brand(brandId);

        String[] categoryIds = req.getParameterValues("categoryIds");
        List<Category> categories = new ArrayList<>();
        if (categoryIds != null) {
            for (String cId : categoryIds) {
                categories.add(new Category(Integer.parseInt(cId)));
            }
        }

        Product product = new Product(
                name,
                price,
                brand,
                image,
                summary,
                alink
        );

        product.setCategories(categories);

        productService.add(product);
        int newProductId = product.getId();

        // Lưu vào bảng trung gian product_category
        if (newProductId > 0 && categories != null && !categories.isEmpty()) {
            pcService.update(newProductId, categories);
        }

        resp.sendRedirect("/products?action=home");
    }

    private void editProduct(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        int id = Integer.parseInt(req.getParameter("id"));

        String name = req.getParameter("name");
        double price = Double.parseDouble(req.getParameter("price"));
        String image = req.getParameter("image");
        String summary = req.getParameter("summary");
        String alink = req.getParameter("alink");

        int brandId = Integer.parseInt(req.getParameter("brandId"));
        Brand brand = new Brand(brandId);

        String[] categoryIds = req.getParameterValues("categoryIds");
        List<Category> categories = new ArrayList<>();
        if (categoryIds != null) {
            for (String cId : categoryIds) {
                categories.add(new Category(Integer.parseInt(cId)));
            }
        }

        Product product = new Product(
                id,
                name,
                price,
                brand,
                image,
                summary,
                alink
        );
        product.setCategories(categories);

        productService.update(id, product);

        resp.sendRedirect("/products?action=home");
    }

    private void showNotFound(HttpServletRequest req,
                              HttpServletResponse resp)
            throws ServletException, IOException {

        req.getRequestDispatcher("errors/not-found.jsp")
                .forward(req, resp);
    }
}