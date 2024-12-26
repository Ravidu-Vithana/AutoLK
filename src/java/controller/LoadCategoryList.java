/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dto.Response_DTO;
import entity.Category;
import entity.SubCategory;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedList;
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
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author User
 */
@WebServlet(name = "LoadCategoryList", urlPatterns = {"/LoadCategoryList"})
public class LoadCategoryList extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        Gson gson = new Gson();
        
        Session session = HibernateUtil.getSessionFactory().openSession();
        Criteria criteria1 = session.createCriteria(Category.class);
        criteria1.addOrder(Order.asc("name"));
        List<Category> categoryList = criteria1.list();
        
        LinkedList<HashMap<String,Object>> finalList = new LinkedList<>();
        
        for (Category category : categoryList) {
            Criteria criteria2 = session.createCriteria(SubCategory.class);
            criteria2.add(Restrictions.eq("category", category));
            criteria2.setProjection(Projections.property("name"));
            List<String> subCategoryList = criteria2.list();
            
            HashMap<String,Object> categoriesMap = new HashMap<>();
            categoriesMap.put("categoryName", category.getName());
            categoriesMap.put("subCategoryList", subCategoryList);
            
            finalList.add(categoriesMap);
            
        }
        
        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(finalList));
        System.out.println(gson.toJson(finalList));
        
    }

}
