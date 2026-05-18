package com.restaurante.servlets;

/**
 * ProductoServlet.java
 * Servlet para gestionar las operaciones CRUD de productos del menú.
 * Maneja peticiones HTTP GET (consultar) y POST (insertar, eliminar).
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
@WebServlet(name = "ProductoServlet", urlPatterns = {"/ProductoServlet"})
public class ProductoServlet extends HttpServlet {

    // Constantes de conexión a la base de datos
    private static final String URL = "jdbc:mysql://localhost:3306/restaurante";
    private static final String USER = "root";
    private static final String PSW = "7381Jul*";

    /**
     * Método GET: Consulta todos los productos del menú
     * y los envía al JSP para su visualización.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Lista para almacenar los productos consultados
        List<String[]> productos = new ArrayList<>();

        try {
            // Establecer conexión con la base de datos
            Connection conn = DriverManager.getConnection(URL, USER, PSW);
            Statement stmt = conn.createStatement();

            // Consultar todos los productos
            ResultSet rs = stmt.executeQuery("SELECT * FROM productos");

            // Recorrer resultados y agregarlos a la lista
            while (rs.next()) {
                productos.add(new String[]{
                    rs.getString("id"),
                    rs.getString("nombre"),
                    rs.getString("descripcion"),
                    rs.getString("precio"),
                    rs.getString("categoria")
                });
            }
            conn.close(); // Cerrar conexión

        } catch (Exception e) {
            // Enviar mensaje de error al JSP
            request.setAttribute("error", e.getMessage());
        }

        // Enviar lista de productos al JSP
        request.setAttribute("productos", productos);
        request.getRequestDispatcher("/productos.jsp").forward(request, response);
    }

    /**
     * Método POST: Procesa las acciones de insertar y eliminar productos.
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
                String nombre = request.getParameter("nombre");
                String descripcion = request.getParameter("descripcion");
                double precio = Double.parseDouble(request.getParameter("precio"));
                String categoria = request.getParameter("categoria");

                // Insertar nuevo producto usando PreparedStatement
                PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO productos (nombre, descripcion, precio, categoria) VALUES (?, ?, ?, ?)");
                ps.setString(1, nombre);
                ps.setString(2, descripcion);
                ps.setDouble(3, precio);
                ps.setString(4, categoria);
                ps.executeUpdate();

            } else if ("eliminar".equals(accion)) {
                // Obtener ID del producto a eliminar
                int id = Integer.parseInt(request.getParameter("id"));

                // Eliminar producto por ID
                PreparedStatement ps = conn.prepareStatement(
                    "DELETE FROM productos WHERE id=?");
                ps.setInt(1, id);
                ps.executeUpdate();
            }

            conn.close(); // Cerrar conexión

        } catch (Exception e) {
            request.setAttribute("error", e.getMessage());
        }

        // Redirigir al listado de productos
        response.sendRedirect("ProductoServlet");
    }
}