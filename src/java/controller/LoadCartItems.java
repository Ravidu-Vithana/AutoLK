/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import dto.Cart_DTO;
import dto.User_DTO;
import entity.Cart;
import entity.Product;
import entity.User;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.HibernateUtil;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author User
 */
@WebServlet(name = "LoadCartItems", urlPatterns = {"/LoadCartItems"})
public class LoadCartItems extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();
        Session session = HibernateUtil.getSessionFactory().openSession();
        ArrayList<Cart_DTO> cart_DTO_List = new ArrayList<>();

        try {

            if (request.getSession().getAttribute("user") != null) {
                //DB Cart

                User_DTO user_DTO = (User_DTO) request.getSession().getAttribute("user");

                Criteria criteria1 = session.createCriteria(User.class);
                criteria1.add(Restrictions.eq("email", user_DTO.getEmail()));
                User user = (User) criteria1.uniqueResult();

                Criteria criteria2 = session.createCriteria(Cart.class);
                criteria2.add(Restrictions.eq("user", user));

                List<Cart> cartList = criteria2.list();

                for (Cart cart : cartList) {
                    Cart_DTO cart_DTO = new Cart_DTO();

                    Product product = cart.getProduct();
                    product.setUser(null);
                    cart_DTO.setProduct(product);

                    cart_DTO.setQty(cart.getQty());

                    cart_DTO_List.add(cart_DTO);

                }

            } else {
                //Session Cart
                if (request.getSession().getAttribute("sessionCart") != null) {

                    cart_DTO_List = (ArrayList<Cart_DTO>) request.getSession().getAttribute("sessionCart");

                    for (Cart_DTO cart_DTO : cart_DTO_List) {
                        cart_DTO.getProduct().setUser(null);
                    }
                } else {
                    //cart Empty
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        session.close();
        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(cart_DTO_List));

    }

}
