import React, { useState, useEffect } from 'react';
import ProductoService from '../service/ProductoService';
import { Producto } from '../model/Producto.model';
import { Link } from 'react-router-dom';

/**
 * COMPONENTE: ListProductoComponent
 * Maneja la visualización reactiva de los productos.
 */
const ListProductoComponent = () => {
    const [productos, setProductos] = useState([]);
    const [cargando, setCargando] = useState(true);

    //al entrar aqui
    useEffect(() => {
        // 1. Iniciamos la conexión al Stream (SSE)
        const eventSource = ProductoService.obtenerStreamProductos();

        // 2. Escuchamos cada mensaje que envía el Flux de Java
        eventSource.onmessage = (event) => {
            const data = JSON.parse(event.data);
            
            // Usamos nuestro MODELO para transformar el JSON
            const nuevoProducto = Producto.fromJson(data);

            // Actualizamos el estado agregando el nuevo producto al array existente
            setProductos((prevProductos) => [...prevProductos, nuevoProducto]);
            setCargando(false);
        };

        // 3. Manejo de errores de conexión
        eventSource.onerror = (error) => {
            console.error("Error en el Stream:", error);
            eventSource.close(); // Cerramos si hay error
            setCargando(false);
        };

        // 4. Limpieza: Cerramos la conexión cuando el usuario sale del componente
        return () => {
            eventSource.close();
        };
    }, []);

    return (
        <div className="container mt-4">
            <h2 className="text-center">Inventario Reactivo (MVC)</h2>
            <Link to="/add-producto" className="btn btn-primary mb-2">Agregar Producto</Link>
            
            {cargando && productos.length === 0 && (
                <div className="alert alert-info">Esperando datos del servidor (Stream activo)...</div>
            )}

            <table className="table table-bordered table-striped">
                <thead className="table-dark">
                    <tr>
                        <th>ID</th>
                        <th>Nombre</th>
                        <th>Categoría</th>
                        <th>Precio</th>
                        <th>Stock</th>
                        <th>Acciones</th>
                    </tr>
                </thead>
                <tbody>
                    {productos.map((producto) => (
                        <tr key={producto.id}>
                            <td>{producto.id}</td>
                            <td>{producto.nombre}</td>
                            <td>{producto.categoria}</td>
                            <td>${producto.precioUnitario}</td>
                            <td>{producto.stock}</td>
                            <td>
                                <Link className="btn btn-info" to={`/edit-producto/${producto.id}`}>Editar</Link>
                            </td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
};

export default ListProductoComponent;