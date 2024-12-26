/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import dto.Response_DTO;
import dto.User_DTO;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author User
 */
@WebServlet(name = "LoadHeaderDetails", urlPatterns = {"/LoadHeaderDetails"})
public class LoadHeaderDetails extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();
        Response_DTO response_DTO = new Response_DTO();

        if (request.getSession().getAttribute("user") != null) {
            User_DTO user_DTO = (User_DTO) request.getSession().getAttribute("user");
            response_DTO.setSuccess(true);
            response_DTO.setContent(gson.toJson(user_DTO));

        }else{
            response_DTO.setContent("NotLoggedIn");
        }
        
        response.getWriter().write(gson.toJson(response_DTO));

    }

}
