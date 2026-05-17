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

@WebServlet(name = "PedidoServlet", urlPatterns = {"/PedidoServlet"})
public class PedidoServlet extends HttpServlet {

    private static final String URL = "jdbc:mysql://localhost:3306/restaurante";
    private static final String USER = "root";
    private static final String PSW = "7381Jul*";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<String[]> pedidos = new ArrayList<>();
        List<String[]> clientes = new ArrayList<>();
        try {
            Connection conn = DriverManager.getConnection(URL, USER, PSW);
            ResultSet rs = conn.createStatement().executeQuery(
                "SELECT p.id, c.nombre, p.fecha, p.total, p.estado " +
                "FROM pedidos p JOIN clientes c ON p.id_cliente = c.id");
            while (rs.next()) {
                pedidos.add(new String[]{
                    rs.getString("id"),
                    rs.getString("nombre"),
                    rs.getString("fecha"),
                    rs.getString("total"),
                    rs.getString("estado")
                });
            }
            ResultSet rs2 = conn.createStatement().executeQuery("SELECT id, nombre FROM clientes");
            while (rs2.next()) {
                clientes.add(new String[]{rs2.getString("id"), rs2.getString("nombre")});
            }
            conn.close();
        } catch (Exception e) {
            request.setAttribute("error", e.getMessage());
        }
        request.setAttribute("pedidos", pedidos);
        request.setAttribute("clientes", clientes);
        request.getRequestDispatcher("/pedidos.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String accion = request.getParameter("accion");
        try {
            Connection conn = DriverManager.getConnection(URL, USER, PSW);
            if ("insertar".equals(accion)) {
                int idCliente = Integer.parseInt(request.getParameter("id_cliente"));
                double total = Double.parseDouble(request.getParameter("total"));
                PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO pedidos (id_cliente, total, estado) VALUES (?, ?, 'pendiente')");
                ps.setInt(1, idCliente);
                ps.setDouble(2, total);
                ps.executeUpdate();
            } else if ("eliminar".equals(accion)) {
                int id = Integer.parseInt(request.getParameter("id"));
                PreparedStatement ps = conn.prepareStatement("DELETE FROM pedidos WHERE id=?");
                ps.setInt(1, id);
                ps.executeUpdate();
            } else if ("actualizar".equals(accion)) {
                int id = Integer.parseInt(request.getParameter("id"));
                String estado = request.getParameter("estado");
                PreparedStatement ps = conn.prepareStatement(
                    "UPDATE pedidos SET estado=? WHERE id=?");
                ps.setString(1, estado);
                ps.setInt(2, id);
                ps.executeUpdate();
            }
            conn.close();
        } catch (Exception e) {
            request.setAttribute("error", e.getMessage());
        }
        response.sendRedirect("PedidoServlet");
    }
}