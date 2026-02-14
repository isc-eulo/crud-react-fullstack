import React, { useState, useEffect } from 'react';
import { useNavigate, useParams, Link } from 'react-router-dom';
// CORRECCIÓN: 'service' en lugar de 'services'
import ProductoService from '../service/ProductoService';
// CORRECCIÓN: 'model' en lugar de 'models'
import { Producto } from '../model/Producto.model';

/**
 * COMPONENTE: AddProductoComponent
 */
const AddProductoComponent = () => {
    const [producto, setProducto] = useState(new Producto());
    const [error, setError] = useState(null);

    const navigate = useNavigate();
    const { id } = useParams();

    const esModoEdicion = id && id !== "0" && !isNaN(Number(id));

    useEffect(() => {
        if (esModoEdicion) {
            ProductoService.obtenerPorId(id)
                .then((response) => {
                    setProducto(Producto.fromJson(response.data));
                })
                .catch(err => {
                    console.error("Error al recuperar producto:", err);
                    setError("No se pudo cargar el producto.");
                });
        } else {
            setProducto(new Producto());
        }
    }, [id, esModoEdicion]);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setProducto({ ...producto, [name]: value });
    };

    const saveOrUpdateProducto = (e) => {
        e.preventDefault();

        if (!producto.nombre.trim() || producto.precioUnitario < 0) {
            alert("El nombre es obligatorio y el precio no puede ser negativo.");
            return;
        }

        if (esModoEdicion) {
            ProductoService.actualizarProducto(id, producto)
                .then(() => navigate('/productos'))
                .catch(err => alert("Error al actualizar: " + err.message));
        } else {
            ProductoService.crearProducto(producto)
                .then(() => navigate('/productos'))
                .catch(err => alert("Error al crear: " + err.message));
        }
    };

    return (
        <div className="container mt-5">
            <div className="card col-md-6 offset-md-3">
                <h2 className="text-center">
                    {esModoEdicion ? 'Actualizar Producto' : 'Nuevo Producto'}
                </h2>
                <div className="card-body">
                    {error && <div className="alert alert-danger">{error}</div>}
                    <form onSubmit={saveOrUpdateProducto}>
                        <div className="form-group mb-2">
                            <label className="form-label">Nombre del Producto:</label>
                            <input
                                type="text"
                                name="nombre"
                                className="form-control"
                                value={producto.nombre}
                                onChange={handleChange}
                                required
                            />
                        </div>
                        <div className="form-group mb-2">
                            <label className="form-label">Categoría:</label>
                            <input
                                type="text"
                                name="categoria"
                                className="form-control"
                                value={producto.categoria}
                                onChange={handleChange}
                            />
                        </div>
                        <div className="form-group mb-2">
                            <label className="form-label">Precio Unitario:</label>
                            <input
                                type="number"
                                name="precioUnitario"
                                className="form-control"
                                step="0.01"
                                value={producto.precioUnitario}
                                onChange={handleChange}
                            />
                        </div>
                        <div className="form-group mb-2">
                            <label className="form-label">Stock Inicial:</label>
                            <input
                                type="number"
                                name="stock"
                                className="form-control"
                                value={producto.stock}
                                onChange={handleChange}
                            />
                        </div>
                        <div className="mt-4">
                            <button type="submit" className="btn btn-success">
                                {esModoEdicion ? 'Actualizar' : 'Guardar'}
                            </button>
                            <Link to="/productos" className="btn btn-danger ms-2">Cancelar</Link>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    );
};

export default AddProductoComponent;