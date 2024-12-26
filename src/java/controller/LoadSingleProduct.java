/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import entity.Product;
import entity.SubCategory;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.HibernateUtil;
import model.Validations;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author User
 */
@WebServlet(name = "LoadSingleProduct", urlPatterns = {"/LoadSingleProduct"})
public class LoadSingleProduct extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        Session session = HibernateUtil.getSessionFactory().openSession();

        String pid = request.getParameter("id");

        if (Validations.isInteger(pid)) {
            Product product = (Product) session.get(Product.class, Integer.parseInt(pid));

            if (product != null) {

                product.setUser(null);

                Criteria criteria1 = session.createCriteria(SubCategory.class);
                criteria1.add(Restrictions.eq("category", product.getSubCategory().getCategory()));
                List<SubCategory> subCategoryList = criteria1.list();
                
                Criteria criteria2 = session.createCriteria(Product.class);
                criteria2.add(Restrictions.in("subCategory", subCategoryList));
                criteria2.add(Restrictions.ne("id", product.getId()));
                criteria2.setMaxResults(4);

                List<Product> productList = criteria2.list();

                for (Product item : productList) {
                    item.setUser(null);
                }

                JsonObject jsonObject = new JsonObject();
                jsonObject.add("product", gson.toJsonTree(product));
                jsonObject.add("productList", gson.toJsonTree(productList));

                response.setContentType("application/json");
                response.getWriter().write(gson.toJson(jsonObject));

            }

        } else {

        }

    }

}
