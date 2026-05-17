package com.restaurante.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "ClienteServlet", urlPatterns = {"/ClienteServlet"})
public class ClienteServlet extends HttpServlet {

    private static final String URL = "jdbc:mysql://localhost:3306/restaurante";
    private static final String USER = "root";
    private static final String PSW = "7381Jul*";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<String[]> clientes = new ArrayList<>();
        try {
            Connection conn = DriverManager.getConnection(URL, USER, PSW);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM clientes");
            while (rs.next()) {
                clientes.add(new String[]{
                    rs.getString("id"),
                    rs.getString("nombre"),
                    rs.getString("telefono"),
                    rs.getString("email")
                });
            }
            conn.close();
        } catch (Exception e) {
            request.setAttribute("error", e.getMessage());
        }
        request.setAttribute("clientes", clientes);
        request.getRequestDispatcher("/clientes.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String accion = request.getParameter("accion");
        try {
            Connection conn = DriverManager.getConnection(URL, USER, PSW);
            if ("insertar".equals(accion)) {
                String nombre = request.getParameter("nombre");
                String telefono = request.getParameter("telefono");
                String email = request.getParameter("email");
                PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO clientes (nombre, telefono, email) VALUES (?, ?, ?)");
                ps.setString(1, nombre);
                ps.setString(2, telefono);
                ps.setString(3, email);
                ps.executeUpdate();
            } else if ("eliminar".equals(accion)) {
                int id = Integer.parseInt(request.getParameter("id"));
                PreparedStatement ps = conn.prepareStatement("DELETE FROM clientes WHERE id=?");
                ps.setInt(1, id);
                ps.executeUpdate();
            } else if ("actualizar".equals(accion)) {
                int id = Integer.parseInt(request.getParameter("id"));
                String nombre = request.getParameter("nombre");
                String telefono = request.getParameter("telefono");
                String email = request.getParameter("email");
                PreparedStatement ps = conn.prepareStatement(
                    "UPDATE clientes SET nombre=?, telefono=?, email=? WHERE id=?");
                ps.setString(1, nombre);
                ps.setString(2, telefono);
                ps.setString(3, email);
                ps.setInt(4, id);
                ps.executeUpdate();
            }
            conn.close();
        } catch (Exception e) {
            request.setAttribute("error", e.getMessage());
        }
        response.sendRedirect("ClienteServlet");
    }
}