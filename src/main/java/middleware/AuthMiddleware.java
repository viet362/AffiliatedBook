package middleware;

import entity.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;

@WebFilter("*")
public class AuthMiddleware extends HttpFilter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        HttpSession session = request.getSession();
        User user = (User)session.getAttribute("currentUser");
        String path = request.getServletPath();
        // Cho phép các trang public
//        boolean isPublic = path.equals("/auth");
        String pageParam = request.getParameter("page");

        // Kiểm tra nếu người dùng đang truy cập vào /auth với tham số page=favorite
        boolean isFavoritePage = "/auth".equals(path) && "favorite".equals(pageParam);

        if (isFavoritePage && user == null) {
            response.sendRedirect(request.getContextPath() + "/auth?page=signIn");
            return;
        }

        // 2. Kiểm tra trang chỉ dành cho Admin
        String servletPath = request.getServletPath();
        String adminPageParam = request.getParameter("page");

        boolean isAdminPage = "/products".equals(servletPath) && Arrays.asList("add", "edit", "delete").contains(adminPageParam);

        if (isAdminPage) {
            boolean isNotAdmin = user == null || user.getUserRole().getId() != 1;
            if (isNotAdmin) {
                response.sendRedirect(request.getContextPath() + "/errors/not-found.jsp");
                return;
            }
        }

        chain.doFilter(req, res);
    }
}
