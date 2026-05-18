package com.restaurante.servlets;

/**
 * ClienteServlet.java
 * Servlet para gestionar las operaciones CRUD de clientes del restaurante.
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
@WebServlet(name = "ClienteServlet", urlPatterns = {"/ClienteServlet"})
public class ClienteServlet extends HttpServlet {

    // Constantes de conexión a la base de datos
    private static final String URL = "jdbc:mysql://localhost:3306/restaurante";
    private static final String USER = "root";
    private static final String PSW = "7381Jul*";

    /**
     * Método GET: Consulta todos los clientes de la base de datos
     * y los envía al JSP para su visualización.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Lista para almacenar los clientes consultados
        List<String[]> clientes = new ArrayList<>();
        
        try {
            // Establecer conexión con la base de datos
            Connection conn = DriverManager.getConnection(URL, USER, PSW);
            Statement stmt = conn.createStatement();
            
            // Consultar todos los clientes
            ResultSet rs = stmt.executeQuery("SELECT * FROM clientes");
            
            // Recorrer resultados y agregarlos a la lista
            while (rs.next()) {
                clientes.add(new String[]{
                    rs.getString("id"),
                    rs.getString("nombre"),
                    rs.getString("telefono"),
                    rs.getString("email")
                });
            }
            conn.close(); // Cerrar conexión
            
        } catch (Exception e) {
            // Enviar mensaje de error al JSP
            request.setAttribute("error", e.getMessage());
        }
        
        // Enviar lista de clientes al JSP
        request.setAttribute("clientes", clientes);
        request.getRequestDispatcher("/clientes.jsp").forward(request, response);
    }

    /**
     * Método POST: Procesa las acciones de insertar, actualizar y eliminar clientes.
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
                String telefono = request.getParameter("telefono");
                String email = request.getParameter("email");
                
                // Insertar nuevo cliente usando PreparedStatement (evita SQL injection)
                PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO clientes (nombre, telefono, email) VALUES (?, ?, ?)");
                ps.setString(1, nombre);
                ps.setString(2, telefono);
                ps.setString(3, email);
                ps.executeUpdate();
                
            } else if ("eliminar".equals(accion)) {
                // Obtener ID del cliente a eliminar
                int id = Integer.parseInt(request.getParameter("id"));
                
                // Eliminar cliente por ID
                PreparedStatement ps = conn.prepareStatement(
                    "DELETE FROM clientes WHERE id=?");
                ps.setInt(1, id);
                ps.executeUpdate();
                
            } else if ("actualizar".equals(accion)) {
                // Obtener parámetros para actualización
                int id = Integer.parseInt(request.getParameter("id"));
                String nombre = request.getParameter("nombre");
                String telefono = request.getParameter("telefono");
                String email = request.getParameter("email");
                
                // Actualizar datos del cliente
                PreparedStatement ps = conn.prepareStatement(
                    "UPDATE clientes SET nombre=?, telefono=?, email=? WHERE id=?");
                ps.setString(1, nombre);
                ps.setString(2, telefono);
                ps.setString(3, email);
                ps.setInt(4, id);
                ps.executeUpdate();
            }
            
            conn.close(); // Cerrar conexión
            
        } catch (Exception e) {
            request.setAttribute("error", e.getMessage());
        }
        
        // Redirigir al listado de clientes
        response.sendRedirect("ClienteServlet");
    }
}