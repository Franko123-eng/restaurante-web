<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Gestión de Productos - Restaurante</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: Arial, sans-serif; background: #f5f5f5; }
        .navbar { background: #c0392b; color: white; padding: 15px 30px; display: flex; align-items: center; gap: 20px; }
        .navbar h1 { font-size: 22px; }
        .navbar a { color: white; text-decoration: none; padding: 8px 16px; border-radius: 4px; background: rgba(255,255,255,0.2); }
        .navbar a:hover { background: rgba(255,255,255,0.3); }
        .container { max-width: 1000px; margin: 30px auto; padding: 0 20px; }
        h2 { color: #c0392b; margin-bottom: 20px; }
        .form-card { background: white; padding: 25px; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.1); margin-bottom: 30px; }
        .form-group { margin-bottom: 15px; }
        .form-group label { display: block; margin-bottom: 5px; font-weight: bold; color: #333; }
        .form-group input, .form-group select { width: 100%; padding: 10px; border: 1px solid #ddd; border-radius: 4px; font-size: 14px; }
        .form-group input:focus, .form-group select:focus { outline: none; border-color: #c0392b; }
        .btn { padding: 10px 20px; border: none; border-radius: 4px; cursor: pointer; font-size: 14px; font-weight: bold; }
        .btn-primary { background: #c0392b; color: white; }
        .btn-danger { background: #e74c3c; color: white; font-size: 12px; padding: 6px 12px; }
        .table-card { background: white; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.1); overflow: hidden; }
        table { width: 100%; border-collapse: collapse; }
        th { background: #c0392b; color: white; padding: 12px 15px; text-align: left; }
        td { padding: 12px 15px; border-bottom: 1px solid #eee; }
        tr:hover { background: #fef9f9; }
        .error { background: #fee; border: 1px solid #fcc; padding: 10px; border-radius: 4px; color: #c00; margin-bottom: 15px; }
    </style>
    <script>
        function confirmarEliminar(id) {
            if (confirm('¿Está seguro de eliminar este producto?')) {
                document.getElementById('eliminar-' + id).submit();
            }
        }
        function validarPrecio() {
            var precio = document.getElementById('precio').value;
            if (precio <= 0) {
                alert('El precio debe ser mayor a 0');
                return false;
            }
            return true;
        }
    </script>
</head>
<body>

<div class="navbar">
    <h1>🍽️ Food Services</h1>
    <a href="ClienteServlet">👥 Clientes</a>
    <a href="ProductoServlet">🍕 Productos</a>
    <a href="PedidoServlet">📋 Pedidos</a>
</div>

<div class="container">
    <h2>🍕 Gestión de Productos</h2>

    <% if(request.getAttribute("error") != null) { %>
        <div class="error">Error: <%= request.getAttribute("error") %></div>
    <% } %>

    <div class="form-card">
        <h3 style="margin-bottom:15px; color:#333;">➕ Nuevo Producto</h3>
        <form action="ProductoServlet" method="post" onsubmit="return validarPrecio()">
            <input type="hidden" name="accion" value="insertar">
            <div class="form-group">
                <label>Nombre del producto</label>
                <input type="text" name="nombre" placeholder="Ej: Pizza Margherita" required />
            </div>
            <div class="form-group">
                <label>Descripción</label>
                <input type="text" name="descripcion" placeholder="Ej: Pizza con tomate y mozzarella" />
            </div>
            <div class="form-group">
                <label>Precio ($)</label>
                <input type="number" id="precio" name="precio" placeholder="Ej: 25000" min="1" required />
            </div>
            <div class="form-group">
                <label>Categoría</label>
                <select name="categoria">
                    <option value="Plato fuerte">Plato fuerte</option>
                    <option value="Bebida">Bebida</option>
                    <option value="Entrada">Entrada</option>
                    <option value="Postre">Postre</option>
                </select>
            </div>
            <button type="submit" class="btn btn-primary">Guardar Producto</button>
        </form>
    </div>

    <div class="table-card">
        <table>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Nombre</th>
                    <th>Descripción</th>
                    <th>Precio</th>
                    <th>Categoría</th>
                    <th>Acciones</th>
                </tr>
            </thead>
            <tbody>
                <%
                List<String[]> productos = (List<String[]>) request.getAttribute("productos");
                if (productos != null && !productos.isEmpty()) {
                    for (String[] p : productos) {
                %>
                <tr>
                    <td><%= p[0] %></td>
                    <td><%= p[1] %></td>
                    <td><%= p[2] %></td>
                    <td>$<%= p[3] %></td>
                    <td><%= p[4] %></td>
                    <td>
                        <form id="eliminar-<%= p[0] %>" action="ProductoServlet" method="post" style="display:inline">
                            <input type="hidden" name="accion" value="eliminar">
                            <input type="hidden" name="id" value="<%= p[0] %>">
                            <button type="button" class="btn btn-danger" onclick="confirmarEliminar('<%= p[0] %>')">Eliminar</button>
                        </form>
                    </td>
                </tr>
                <% } } else { %>
                <tr><td colspan="6" style="text-align:center; color:#999;">No hay productos registrados</td></tr>
                <% } %>
            </tbody>
        </table>
    </div>
</div>

</body>
</html>