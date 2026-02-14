import clienteAxios from '../api/axiosConfig'; // Nuestra instancia con interceptor

const ProductoService = {
    // Nota: EventSource es nativo y no usa Axios, por lo que si requiere token,
    // se debe pasar por URL o mediante una librería de SSE que soporte headers.
    //no existe un interceptor nativo para EventSource como lo hay para Axios.
    obtenerStreamProductos() {
        return new EventSource("http://localhost:8081/api/productos/v2");
    },

    crearProducto(producto) {
        return clienteAxios.post('', producto); // La URL base ya está en el interceptor
    },

    actualizarProducto(id, producto) {
        return clienteAxios.put(`/${id}`, producto);
    },

    obtenerPorId(id) {
        return clienteAxios.get(`/${id}`);
    }
};

export default ProductoService;