/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import dto.Cart_DTO;
import dto.Response_DTO;
import dto.User_DTO;
import entity.Cart;
import entity.Product;
import entity.User;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.HibernateUtil;
import model.Validations;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author User
 */
@WebServlet(name = "RemoveFromCart", urlPatterns = {"/RemoveFromCart"})
public class RemoveFromCart extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        Response_DTO response_DTO = new Response_DTO();
        try {

            String id = request.getParameter("id");

            if (!Validations.isInteger(id)) {
                //Product not found
                response_DTO.setContent("Product not found");
            } else {

                int productId = Integer.parseInt(id);

                Product product = (Product) session.get(Product.class, productId);

                if (product != null) {
                    //Product found
                    if (request.getSession().getAttribute("user") != null) {
                        //DB Cart

                        User_DTO user_DTO = (User_DTO) request.getSession().getAttribute("user");

                        //get db user
                        Criteria criteria1 = session.createCriteria(User.class);
                        criteria1.add(Restrictions.eq("email", user_DTO.getEmail()));
                        User user = (User) criteria1.uniqueResult();

                        //check in db cart
                        Criteria criteria2 = session.createCriteria(Cart.class);
                        criteria2.add(Restrictions.eq("user", user));
                        criteria2.add(Restrictions.eq("product", product));

                        if (criteria2.list().isEmpty()) {
                            //item not found in cart
                            response_DTO.setContent("Request cannot be processed. Please try again later.");

                        } else {
                            //item already found in cart
                            Cart cartItem = (Cart) criteria2.uniqueResult();
                            session.delete(cartItem);
                            transaction.commit();
                            response_DTO.setSuccess(true);
                            response_DTO.setContent("Product removed successfully!");
                        }

                    } else {
                        //Session Cart

                        HttpSession httpSession = request.getSession();

                        if (httpSession.getAttribute("sessionCart") != null) {
                            //session cart found
                            ArrayList<Cart_DTO> sessionCart = (ArrayList<Cart_DTO>) httpSession.getAttribute("sessionCart");

                            Cart_DTO foundCart_DTO = null;

                            for (Cart_DTO cart_DTO : sessionCart) {

                                if (cart_DTO.getProduct().getId() == product.getId()) {
                                    foundCart_DTO = cart_DTO;
                                    break;
                                }
                            }
                            if (foundCart_DTO != null) {
                                //product found
                                sessionCart.remove(foundCart_DTO);
                                response_DTO.setSuccess(true);
                                response_DTO.setContent("Product removed successfully!");

                            } else {
                                //product not found
                                response_DTO.setContent("Request cannot be processed. Please try again later.");

                            }
                        } else {
                            //session cart not found
                            response_DTO.setContent("Request cannot be processed. Please try again later.");
                        }

                    }

                } else {
                    //Product not found
                    response_DTO.setContent("Product not found");

                }

            }

        } catch (Exception e) {
            e.printStackTrace();
            response_DTO.setContent("Request cannot be processed. Please try again later.");
        }
        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(response_DTO));
        session.close();
    }

}
