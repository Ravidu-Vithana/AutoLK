/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dto.User_DTO;
import entity.Address;
import entity.Cart;
import entity.City;
import entity.Order_Item;
import entity.Order_Status;
import entity.Orders;
import entity.Product;
import entity.User;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.HibernateUtil;
import model.PayHere;
import model.Validations;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author User
 */
@WebServlet(name = "Checkout", urlPatterns = {"/Checkout"})
public class Checkout extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();

        JsonObject requestjsonObject = gson.fromJson(request.getReader(), JsonObject.class);

        JsonObject responsejsonObject = new JsonObject();
        responsejsonObject.addProperty("success", false);

        HttpSession httpsession = request.getSession();
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();

        boolean isCurrentaddress = requestjsonObject.get("isCurrentAddress").getAsBoolean();
        String firstName = requestjsonObject.get("firstName").getAsString();
        String lastName = requestjsonObject.get("lastName").getAsString();
        String city_id = requestjsonObject.get("city_id").getAsString();
        String line1 = requestjsonObject.get("line1").getAsString();
        String line2 = requestjsonObject.get("line2").getAsString();
        String postal_code = requestjsonObject.get("postal_code").getAsString();
        String mobile = requestjsonObject.get("mobile").getAsString();

        System.out.println(isCurrentaddress);
        System.out.println(firstName);
        System.out.println(lastName);
        System.out.println(city_id);
        System.out.println(line1);
        System.out.println(line2);
        System.out.println(postal_code);
        System.out.println(mobile);

        if (httpsession.getAttribute("user") != null) {
            //user signed in

            User_DTO user_DTO = (User_DTO) request.getSession().getAttribute("user");
            Criteria criteria1 = session.createCriteria(User.class);
            criteria1.add(Restrictions.eq("email", user_DTO.getEmail()));
            User user = (User) criteria1.uniqueResult();
            if (isCurrentaddress) {
                //get current aaddress
                Criteria criteria2 = session.createCriteria(Address.class);
                criteria2.add(Restrictions.eq("user", user));
                criteria2.addOrder(Order.desc("id"));
                criteria2.setMaxResults(1);
                if (criteria2.list().isEmpty()) {
                    //current address not found
                    responsejsonObject.addProperty("message", "No address found. Please insert a new address");
                } else {
                    //current address found
                    Address address = (Address) criteria2.list().get(0);

                    //proceed to checkout process
                    saveOrders(session, transaction, user, address, responsejsonObject);
                }

            } else {
                //create new address
                if (firstName.isEmpty()) {
                    responsejsonObject.addProperty("message", "First Name cannot be empty");
                } else if (lastName.isEmpty()) {
                    responsejsonObject.addProperty("message", "Last Name cannot be empty");
                } else if (!Validations.isInteger(city_id)) {
                    responsejsonObject.addProperty("message", "Invalid City");
                } else {
                    //check city from DB
                    Criteria criteria3 = session.createCriteria(City.class);
                    criteria3.add(Restrictions.eq("id", Integer.parseInt(city_id)));

                    if (criteria3.list().isEmpty()) {
                        responsejsonObject.addProperty("message", "Invalid City");
                    } else {
                        //city found
                        City city = (City) criteria3.list().get(0);

                        if (line1.isEmpty()) {
                            responsejsonObject.addProperty("message", "Line 1 cannot be empty");
                        } else if (line2.isEmpty()) {
                            responsejsonObject.addProperty("message", "Line 2 cannot be empty");

                        } else if (postal_code.isEmpty()) {
                            responsejsonObject.addProperty("message", "Postal Code cannot be empty");

                        } else if (postal_code.length() != 5) {
                            responsejsonObject.addProperty("message", "Invalid Postal code cannot be empty");

                        } else if (!Validations.isInteger(postal_code)) {
                            responsejsonObject.addProperty("message", "Invalid Postal code cannot be empty");

                        } else if (mobile.isEmpty()) {
                            responsejsonObject.addProperty("message", "Mobile Number cannot be empty");

                        } else if (!Validations.isMobileNumberValid(mobile)) {
                            responsejsonObject.addProperty("message", "Invalid Mobile Number cannot be empty");

                        } else {
                            //create new address
                            Address address = new Address();
                            address.setCity(city);
                            address.setFirst_name(firstName);
                            address.setLast_name(lastName);
                            address.setLine1(line1);
                            address.setLine2(line2);
                            address.setMobile(mobile);
                            address.setPostal_code(postal_code);
                            address.setUser(user);

                            session.save(address);

                            //Proceed to checkout process
                            saveOrders(session, transaction, user, address, responsejsonObject);

                        }

                    }
                }

            }
        } else {
            //user not signed in
            responsejsonObject.addProperty("message", "User not logged in");
        }
        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responsejsonObject));
        session.close();
    }

    private void saveOrders(Session session, Transaction transaction, User user, Address address, JsonObject responsejsonObject) {
        try {
            //create order in DB
            Orders order = new Orders();
            order.setAddress(address);
            order.setDate_time(new Date());
            order.setUser(user);
            int order_id = (int) session.save(order);

            session.save(order);

            //Get Cart Order
            Criteria criteria4 = session.createCriteria(Cart.class);
            criteria4.add(Restrictions.eq("user", user));
            List<Cart> cartList = criteria4.list();

            //get Pending status order from DB
            Order_Status order_Status = (Order_Status) session.get(Order_Status.class, 5);

            //create order Item in DB
            double amount = 0;
            String items = "";
            for (Cart cartItem : cartList) {
                //calculate amount
                amount += cartItem.getQty() * cartItem.getProduct().getPrice();
                if (address.getCity().getId() == 5) {
                    amount += 1000;
                } else {
                    amount += 2500;
                }
                //calculated amount 

                //Get Items Details
                items += cartItem.getProduct().getTitle() + "x" + cartItem.getQty();
                //Get Items Details

                //get product
                Product product = cartItem.getProduct();

                Order_Item order_item = new Order_Item();
                order_item.setOrder(order);
                order_item.setOrder_status(order_Status);
                order_item.setProduct(product);
                order_item.setQty(cartItem.getQty());
                session.save(order_item);

                //update Product in DB
                product.setQty(product.getQty() - cartItem.getQty());
                session.update(product);

                //Delete cart Item from DB
                session.delete(cartItem);

            }

            transaction.commit();

            //START :set payment data
            String merchant_id = "1221370";
            String formatedAmount = new DecimalFormat("0.00").format(amount);
            String currency = "LKR";
            String merchantSecret = "MTA3OTY5OTQ3MjkzODY1NjIwMzM2NTk4OTQ5NjQ3MjkzMDkyMg";
            String merchantSecretMd5Hash = PayHere.genaratemd5(merchantSecret);

            JsonObject payhere = new JsonObject();
            payhere.addProperty("merchant_id", merchant_id);

            payhere.addProperty("return_url", "");
            payhere.addProperty("cancel_url", "");
            payhere.addProperty("notify_url", " https://de24-112-135-76-13.ngrok-free.SmartTrade/VerrifyPayments -> http://localhost:80 ");//***

            payhere.addProperty("first_name", user.getFirst_name());
            payhere.addProperty("last_name", user.getLast_name());
            payhere.addProperty("email", user.getEmail());
            payhere.addProperty("phone", "0765906974");
            payhere.addProperty("address", "390/14 Hettigedara Maspotha");
            payhere.addProperty("city", "Kurunegla");
            payhere.addProperty("country", "Sri Lanka");
            payhere.addProperty("order_id", String.valueOf(order_id));
            payhere.addProperty("items", items);
            payhere.addProperty("currency", currency);
            payhere.addProperty("amount", formatedAmount);
            payhere.addProperty("sandbox", true);

            //Genarate MD5 hash
            String md5Hash = PayHere.genaratemd5(merchant_id + order_id + formatedAmount + currency + merchantSecretMd5Hash);
            payhere.addProperty("hash", md5Hash);
            //Genarate MD5 hash
            //END :set payment data
            responsejsonObject.addProperty("success", true);
            responsejsonObject.addProperty("message", "Checkout Completed");

            Gson gson = new Gson();
            responsejsonObject.add("payhereJson", gson.toJsonTree(payhere));
        } catch (Exception e) {
            transaction.rollback();
        }
    }

}
