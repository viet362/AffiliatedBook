package controller;

import entity.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import service.FavoriteService;

import java.io.IOException;

@WebServlet(name = "FavoriteController", value = "/favorite")
public class FavoriteController extends HttpServlet {

    private FavoriteService favoriteService = new FavoriteService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);

        if (session == null || session.getAttribute("currentUser") == null) {
            resp.sendRedirect(req.getContextPath() + "/auth?action=signIn");
            return;
        }

        User user = (User) session.getAttribute("currentUser");

        int productId = Integer.parseInt(req.getParameter("productId"));
        String action = req.getParameter("action");

        if ("add".equals(action)) {
            favoriteService.addFavorite(user.getId(), productId);
        } else if ("remove".equals(action)) {
            favoriteService.removeFavorite(user.getId(), productId);
        }

        resp.sendRedirect(req.getHeader("referer"));
    }
}