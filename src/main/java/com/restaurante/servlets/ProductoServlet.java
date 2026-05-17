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

@WebServlet(name = "ProductoServlet", urlPatterns = {"/ProductoServlet"})
public class ProductoServlet extends HttpServlet {

    private static final String URL = "jdbc:mysql://localhost:3306/restaurante";
    private static final String USER = "root";
    private static final String PSW = "7381Jul*";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<String[]> productos = new ArrayList<>();
        try {
            Connection conn = DriverManager.getConnection(URL, USER, PSW);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM productos");
            while (rs.next()) {
                productos.add(new String[]{
                    rs.getString("id"),
                    rs.getString("nombre"),
                    rs.getString("descripcion"),
                    rs.getString("precio"),
                    rs.getString("categoria")
                });
            }
            conn.close();
        } catch (Exception e) {
            request.setAttribute("error", e.getMessage());
        }
        request.setAttribute("productos", productos);
        request.getRequestDispatcher("/productos.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String accion = request.getParameter("accion");
        try {
            Connection conn = DriverManager.getConnection(URL, USER, PSW);
            if ("insertar".equals(accion)) {
                String nombre = request.getParameter("nombre");
                String descripcion = request.getParameter("descripcion");
                double precio = Double.parseDouble(request.getParameter("precio"));
                String categoria = request.getParameter("categoria");
                PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO productos (nombre, descripcion, precio, categoria) VALUES (?, ?, ?, ?)");
                ps.setString(1, nombre);
                ps.setString(2, descripcion);
                ps.setDouble(3, precio);
                ps.setString(4, categoria);
                ps.executeUpdate();
            } else if ("eliminar".equals(accion)) {
                int id = Integer.parseInt(request.getParameter("id"));
                PreparedStatement ps = conn.prepareStatement("DELETE FROM productos WHERE id=?");
                ps.setInt(1, id);
                ps.executeUpdate();
            }
            conn.close();
        } catch (Exception e) {
            request.setAttribute("error", e.getMessage());
        }
        response.sendRedirect("ProductoServlet");
    }
}