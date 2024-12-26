/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dto.Cart_DTO;
import dto.User_DTO;
import entity.Cart;
import entity.User;
import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.HibernateUtil;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author User
 */
@WebServlet(name = "LoadCartCount", urlPatterns = {"/LoadCartCount"})
public class LoadCartCount extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        if (request.getSession().getAttribute("user") != null) {
            //dbcart
            Session session = HibernateUtil.getSessionFactory().openSession();

            User_DTO user_DTO = (User_DTO) request.getSession().getAttribute("user");

            Criteria criteria1 = session.createCriteria(User.class);
            criteria1.add(Restrictions.eq("email", user_DTO.getEmail()));
            User user = (User) criteria1.uniqueResult();

            Criteria criteria2 = session.createCriteria(Cart.class);
            criteria2.add(Restrictions.eq("user", user));

            System.out.println("DB Cart item count: " + criteria2.list().size());
            response.getWriter().write(String.valueOf(criteria2.list().size()));

        } else {
            //session cart
            HttpSession httpSession = request.getSession();

            if (httpSession.getAttribute("sessionCart") != null) {
                //session cart found
                ArrayList<Cart_DTO> sessionCart = (ArrayList<Cart_DTO>) httpSession.getAttribute("sessionCart");
                response.getWriter().write(String.valueOf(sessionCart.size()));
                System.out.println("Session Cart item count: " + sessionCart.size());

            } else {
                //session cart not found
                response.getWriter().write("0");
                System.out.println("Session Cart item count: " + 0);

            }
        }

    }

}
