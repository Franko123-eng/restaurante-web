package com.restaurante.servlets;

/**
 * PedidoServlet.java
 * Servlet para gestionar las operaciones CRUD de pedidos del restaurante.
 * Maneja peticiones HTTP GET (consultar) y POST (insertar, actualizar, eliminar).
 *
 * @author Frank Franco
 * @version 1.0
 */

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// Mapeo de URL para este servlet
@WebServlet(name = "PedidoServlet", urlPatterns = {"/PedidoServlet"})
public class PedidoServlet extends HttpServlet {

    // Constantes de conexión a la base de datos
    private static final String URL = "jdbc:mysql://localhost:3306/restaurante";
    private static final String USER = "root";
    private static final String PSW = "7381Jul*";

    /**
     * Método GET: Consulta todos los pedidos junto con el nombre del cliente,
     * y la lista de clientes para el formulario de nuevo pedido.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Listas para almacenar pedidos y clientes
        List<String[]> pedidos = new ArrayList<>();
        List<String[]> clientes = new ArrayList<>();

        try {
            // Establecer conexión con la base de datos
            Connection conn = DriverManager.getConnection(URL, USER, PSW);

            // Consultar pedidos con JOIN para obtener nombre del cliente
            ResultSet rs = conn.createStatement().executeQuery(
                "SELECT p.id, c.nombre, p.fecha, p.total, p.estado " +
                "FROM pedidos p JOIN clientes c ON p.id_cliente = c.id");

            // Recorrer resultados de pedidos
            while (rs.next()) {
                pedidos.add(new String[]{
                    rs.getString("id"),
                    rs.getString("nombre"),
                    rs.getString("fecha"),
                    rs.getString("total"),
                    rs.getString("estado")
                });
            }

            // Consultar clientes para el selector del formulario
            ResultSet rs2 = conn.createStatement().executeQuery(
                "SELECT id, nombre FROM clientes");
            while (rs2.next()) {
                clientes.add(new String[]{
                    rs2.getString("id"),
                    rs2.getString("nombre")
                });
            }

            conn.close(); // Cerrar conexión

        } catch (Exception e) {
            // Enviar mensaje de error al JSP
            request.setAttribute("error", e.getMessage());
        }

        // Enviar listas al JSP
        request.setAttribute("pedidos", pedidos);
        request.setAttribute("clientes", clientes);
        request.getRequestDispatcher("/pedidos.jsp").forward(request, response);
    }

    /**
     * Método POST: Procesa las acciones de insertar, actualizar estado y eliminar pedidos.
     * La acción se determina por el parámetro "accion" del formulario.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Obtener la acción solicitada desde el formulario
        String accion = request.getParameter("accion");

        try {
            // Establecer conexión con la base de datos
            Connection conn = DriverManager.getConnection(URL, USER, PSW);

            if ("insertar".equals(accion)) {
                // Obtener parámetros del formulario
                int idCliente = Integer.parseInt(request.getParameter("id_cliente"));
                double total = Double.parseDouble(request.getParameter("total"));

                // Insertar nuevo pedido con estado inicial 'pendiente'
                PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO pedidos (id_cliente, total, estado) VALUES (?, ?, 'pendiente')");
                ps.setInt(1, idCliente);
                ps.setDouble(2, total);
                ps.executeUpdate();

            } else if ("eliminar".equals(accion)) {
                // Obtener ID del pedido a eliminar
                int id = Integer.parseInt(request.getParameter("id"));

                // Eliminar pedido por ID
                PreparedStatement ps = conn.prepareStatement(
                    "DELETE FROM pedidos WHERE id=?");
                ps.setInt(1, id);
                ps.executeUpdate();

            } else if ("actualizar".equals(accion)) {
                // Obtener ID y nuevo estado del pedido
                int id = Integer.parseInt(request.getParameter("id"));
                String estado = request.getParameter("estado");

                // Actualizar estado del pedido
                PreparedStatement ps = conn.prepareStatement(
                    "UPDATE pedidos SET estado=? WHERE id=?");
                ps.setString(1, estado);
                ps.setInt(2, id);
                ps.executeUpdate();
            }

            conn.close(); // Cerrar conexión

        } catch (Exception e) {
            request.setAttribute("error", e.getMessage());
        }

        // Redirigir al listado de pedidos
        response.sendRedirect("PedidoServlet");
    }
}