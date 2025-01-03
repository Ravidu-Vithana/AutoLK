/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import dto.Response_DTO;
import dto.User_DTO;
import entity.Category;
import entity.SubCategory;
import entity.Product;
import entity.Product_Condition;
import entity.Product_Status;
import entity.User;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import model.HibernateUtil;
import model.Validations;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

@MultipartConfig
@WebServlet(name = "ProductListing", urlPatterns = {"/ProductListing"})
public class ProductListing extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Response_DTO response_DTO = new Response_DTO();

        Gson gson = new Gson();

        String categoryId = request.getParameter("categoryId");
        String subCategoryId = request.getParameter("subCategoryId");
        String title = request.getParameter("title");
        String description = request.getParameter("description");
        String conditionId = request.getParameter("conditionId");
        String price = request.getParameter("price");
        String quantity = request.getParameter("quantity");

        Part image1 = request.getPart("image1");
        Part image2 = request.getPart("image2");
        Part image3 = request.getPart("image3");
        Part image4 = request.getPart("image4");
        Part image5 = request.getPart("image5");

        Session session = HibernateUtil.getSessionFactory().openSession();

        if (!Validations.isInteger(categoryId) || categoryId.equals("0")) {
            response_DTO.setContent("Invalid Category");
        } else if (!Validations.isInteger(subCategoryId) || subCategoryId.equals("0")) {
            response_DTO.setContent("Invalid Sub Category");
        } else if (title.isEmpty()) {
            response_DTO.setContent("Please enter the title");
        } else if (description.isEmpty()) {
            response_DTO.setContent("Please enter the description");
        } else if (!Validations.isInteger(conditionId) || conditionId.equals("0")) {
            response_DTO.setContent("Invalid Condition");
        } else if (price.isEmpty()) {
            response_DTO.setContent("Please enter the price");
        } else if (!Validations.isDouble(price)) {
            response_DTO.setContent("Invalid Price");
        } else if (Double.parseDouble(price) <= 0) {
            response_DTO.setContent("Price must be greater than 0");
        } else if (quantity.isEmpty()) {
            response_DTO.setContent("Please enter the quantity");
        } else if (!Validations.isInteger(quantity)) {
            response_DTO.setContent("Invalid quantity");
        } else if (Integer.parseInt(quantity) <= 0) {
            response_DTO.setContent("Quantity must be greater than 0");
        } else if (image1.getSubmittedFileName() == null) {
            response_DTO.setContent("Please upload image 1");
        } else if (image2.getSubmittedFileName() == null) {
            response_DTO.setContent("Please upload image 2");
        } else if (image3.getSubmittedFileName() == null) {
            response_DTO.setContent("Please upload image 3");
        } else if (image4.getSubmittedFileName() == null) {
            response_DTO.setContent("Please upload image 4");
        } else if (image5.getSubmittedFileName() == null) {
            response_DTO.setContent("Please upload image 5");
        } else {
            Category category = (Category) session.get(Category.class, Integer.parseInt(categoryId));

            if (category == null) {
                response_DTO.setContent("Please select a valid category");
            } else {

                SubCategory subCategory = (SubCategory) session.get(SubCategory.class, Integer.parseInt(subCategoryId));

                if (subCategory == null) {
                    response_DTO.setContent("Please select a valid Sub Category");
                } else {

                    if (subCategory.getCategory().getId() != category.getId()) {
                        response_DTO.setContent("Please select a valid Sub Category");
                    } else {

                        Product_Condition product_condition = (Product_Condition) session.get(Product_Condition.class, Integer.parseInt(conditionId));

                        if (product_condition == null) {
                            response_DTO.setContent("Please select a valid product condition");
                        } else {

                            Product product = new Product();
                            product.setDate_time(new Date());
                            product.setDescription(description);
                            product.setSubCategory(subCategory);
                            product.setPrice(Double.parseDouble(price));

                            //get active status
                            Product_Status product_Status = (Product_Status) session.get(Product_Status.class, 1);
                            product.setStatus(product_Status);

                            product.setCondition(product_condition);
                            product.setQty(Integer.parseInt(quantity));
                            product.setTitle(title);

                            //get user
                            User_DTO user_DTO = (User_DTO) request.getSession().getAttribute("user");
                            Criteria criteria1 = session.createCriteria(User.class);
                            criteria1.add(Restrictions.eq("email", user_DTO.getEmail()));
                            User user = (User) criteria1.uniqueResult();
                            product.setUser(user);

                            int pid = (int) session.save(product);
                            session.beginTransaction().commit();

                            String applicationPath = request.getServletContext().getRealPath("");
                            String newApplicationPath = applicationPath.replace("build" + File.separator + "web", "web");

                            File folder = new File(newApplicationPath + "//product-images//" + pid);
                            folder.mkdir();

                            File file1 = new File(folder, "image1.png");
                            InputStream inputStream1 = image1.getInputStream();
                            Files.copy(inputStream1, file1.toPath(), StandardCopyOption.REPLACE_EXISTING);

                            File file2 = new File(folder, "image2.png");
                            InputStream inputStream2 = image2.getInputStream();
                            Files.copy(inputStream2, file2.toPath(), StandardCopyOption.REPLACE_EXISTING);

                            File file3 = new File(folder, "image3.png");
                            InputStream inputStream3 = image3.getInputStream();
                            Files.copy(inputStream3, file3.toPath(), StandardCopyOption.REPLACE_EXISTING);
                            
                            File file4 = new File(folder, "image4.png");
                            InputStream inputStream4 = image4.getInputStream();
                            Files.copy(inputStream4, file4.toPath(), StandardCopyOption.REPLACE_EXISTING);
                            
                            File file5 = new File(folder, "image5.png");
                            InputStream inputStream5 = image5.getInputStream();
                            Files.copy(inputStream5, file5.toPath(), StandardCopyOption.REPLACE_EXISTING);

                            response_DTO.setSuccess(true);
                            response_DTO.setContent("New Product Added");

                        }

                    }//

                }

            }

        }

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(response_DTO));
    }

}
