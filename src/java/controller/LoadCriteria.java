package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dto.Response_DTO;
import entity.Category;
import entity.SubCategory;
import entity.Product_Condition;
import java.io.IOException;
import java.io.PrintWriter;
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

@WebServlet(name = "LoadCriteria", urlPatterns = {"/LoadCriteria"})
public class LoadCriteria extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        Gson gson = new Gson();
        
        Session session = HibernateUtil.getSessionFactory().openSession();
        
        Criteria criteria1 = session.createCriteria(Category.class);
        criteria1.addOrder(Order.asc("name"));
        List<Category> categoryList = criteria1.list();
        
        Criteria criteria2 = session.createCriteria(SubCategory.class);
        criteria2.addOrder(Order.asc("name"));
        List<Category> subCategoryList = criteria2.list();
        
        Criteria criteria3 = session.createCriteria(Product_Condition.class);
        criteria3.addOrder(Order.asc("name"));
        List<Category> conditionList = criteria3.list();
        
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("categoryList", gson.toJsonTree(categoryList));
        jsonObject.add("subCategoryList", gson.toJsonTree(subCategoryList));
        jsonObject.add("conditionList", gson.toJsonTree(conditionList));
        
        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(jsonObject));
        System.out.println(gson.toJson(jsonObject));
        
    }

}
