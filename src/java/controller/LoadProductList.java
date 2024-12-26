/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import entity.Product;
import entity.Product_Status;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.HibernateUtil;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author User
 */
@WebServlet(name = "LoadProductList", urlPatterns = {"/LoadProductList"})
public class LoadProductList extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        Gson gson = new Gson();
        
        Session session = HibernateUtil.getSessionFactory().openSession();
        
        Product_Status product_Status = (Product_Status) session.get(Product_Status.class, 1);
        
        Criteria criteria1 = session.createCriteria(Product.class);
        criteria1.add(Restrictions.eq("status", product_Status));
        criteria1.addOrder(Order.desc("date_time"));
        criteria1.setMaxResults(4);
        
        List<Product> productList = criteria1.list();
        
        for (Product product : productList) {
            product.setUser(null);
            product.setCondition(null);
            product.setDate_time(null);
            product.setDescription(null);
            product.setStatus(null);
            product.setSubCategory(null);
            product.setQty(0);
        }
        
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("productList", gson.toJsonTree(productList));
        
        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(jsonObject));
        System.out.println(gson.toJson(jsonObject));
        
    }

}
