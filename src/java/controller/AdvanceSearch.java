/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import entity.Category;
import entity.Product;
import entity.Product_Condition;
import entity.Product_Status;
import entity.SubCategory;
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
@WebServlet(name = "AdvancedSearch", urlPatterns = {"/AdvancedSearch"})
public class AdvanceSearch extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();

        JsonObject responseJsonObject = new JsonObject();
        responseJsonObject.addProperty("success", false);

        JsonObject requestJsonObject = gson.fromJson(request.getReader(), JsonObject.class);
        Session session = HibernateUtil.getSessionFactory().openSession();

        int categoryId = requestJsonObject.get("categoryId").getAsInt();
        int subCategoryId = requestJsonObject.get("subCategoryId").getAsInt();
        double startPrice = requestJsonObject.get("startPrice").getAsDouble();
        double endPrice = requestJsonObject.get("endValue").getAsDouble();
        String sortBy = requestJsonObject.get("sortBy").getAsString();
        String conditionState = requestJsonObject.get("condition").getAsString();
        
        System.out.println(categoryId);
        System.out.println(subCategoryId);
        System.out.println(startPrice);
        System.out.println(endPrice);
        System.out.println(sortBy);
        System.out.println(conditionState);

        Criteria criteria1 = session.createCriteria(Product.class);
        
        Product_Status product_Status = (Product_Status) session.get(Product_Status.class, 1);
        criteria1.add(Restrictions.eq("status", product_Status));

        if (categoryId != 0) {
            Category category = (Category) session.get(Category.class, categoryId);

            if (category != null) {
                Criteria subCriteria = session.createCriteria(SubCategory.class);
                subCriteria.add(Restrictions.eq("category", category));
                List subCategoryList = subCriteria.list();

                if (!subCategoryList.isEmpty()) {
                    criteria1.add(Restrictions.in("subCategory", subCategoryList));
                }

            }

        }

        if (subCategoryId != 0) {
            SubCategory subCategory = (SubCategory) session.get(SubCategory.class, subCategoryId);

            if (subCategory != null) {
                criteria1.add(Restrictions.eq("subCategory", subCategory));
            }

        }

        int conditionId = 0;

        if (conditionState.equals("brandNew")) {
            conditionId = 1;
        } else if (conditionState.equals("used")) {
            conditionId = 2;
        }

        if (conditionId != 0) {
            Product_Condition product_Condition = (Product_Condition) session.get(Product_Condition.class, conditionId);
            criteria1.add(Restrictions.eq("condition", product_Condition));
        }

        criteria1.add(Restrictions.ge("price", startPrice));
        criteria1.add(Restrictions.le("price", endPrice));

        if (sortBy.equals("dateDesc")) {

            criteria1.addOrder(Order.desc("id"));

        } else if (sortBy.equals("dateAsc")) {

            criteria1.addOrder(Order.asc("id"));

        } else if (sortBy.equals("nameAsc")) {

            criteria1.addOrder(Order.asc("title"));

        } else if (sortBy.equals("nameDesc")) {

            criteria1.addOrder(Order.desc("title"));

        } else if (sortBy.equals("priceAsc")) {

            criteria1.addOrder(Order.asc("price"));

        } else if (sortBy.equals("priceDesc")) {

            criteria1.addOrder(Order.desc("price"));

        }
        
        //get product list
        List<Product> productList = criteria1.list();

        if (productList.isEmpty()) {
            responseJsonObject.addProperty("message", "noProducts");
        } else {
            responseJsonObject.addProperty("message", "successful");
            for (Product product : productList) {
                product.setUser(null);
            }

            int productCount = productList.size();
            
            responseJsonObject.addProperty("productCount", productCount);
            responseJsonObject.add("productList", gson.toJsonTree(productList));
        }

        responseJsonObject.addProperty("success", true);
        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseJsonObject));

    }

}
