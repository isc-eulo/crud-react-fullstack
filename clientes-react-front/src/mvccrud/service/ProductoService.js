// src/mvccrud/service/ProductoService.js
import clienteAxios from '../api/axiosConfig'; 
import { EventSourcePolyfill } from 'event-source-polyfill'; 

/**
 * CAPA DE SERVICIO: ProductoService
 * * Esta clase actúa como el "Controlador de Datos" en nuestro patrón MVC.
 * Su responsabilidad es abstraer la complejidad de las peticiones HTTP y 
 * la gestión de flujos reactivos (SSE) para que los componentes de React 
 * solo reciban datos listos para usar.
 */

const API_URL = "http://localhost:8081/api/productos/v2";

const ProductoService = {

    /**
     * MÉTODO: obtenerStreamProductos (REACTIVIDAD SSE)
     * * ¿Por qué usar EventSourcePolyfill?
     * 1. Seguridad: El EventSource nativo del navegador no permite enviar Headers (Authorization).
     * 2. Compatibilidad: Permite inyectar el Token JWT para que Spring Security valide la conexión.
     * 3. Flujo Continuo: Se conecta al endpoint de Spring WebFlux que emite datos cada 3 segundos.
     * * @returns {EventSourcePolyfill} Una conexión abierta que emite eventos cada vez que llega un producto.
     */
    obtenerStreamProductos() {
        // Recuperamos el token que el sistema de seguridad (o el Shell) almacenó
        const token = localStorage.getItem('token');
        
        console.log("Iniciando conexión SSE con seguridad activada...");

        return new EventSourcePolyfill(API_URL, {
            headers: {
                // Inyectamos manualmente el header de seguridad ya que aquí no aplica el interceptor de Axios
                'Authorization': `Bearer ${token}` 
            },
            // heartbeatTimeout: Evita que la conexión se cierre prematuramente si el servidor tarda en enviar datos
            heartbeatTimeout: 60000 
        });
    },

    /**
     * MÉTODO: crearProducto
     * Envía los datos al servidor para persistir un nuevo registro.
     * * @param {Producto} producto - Instancia del modelo con los datos del formulario.
     * @returns {Promise} Respuesta de Axios gestionada por el Interceptor global.
     */
    crearProducto(producto) {
        // Al usar clienteAxios, la URL base y el Token se gestionan automáticamente en axiosConfig.js
        return clienteAxios.post('', producto);
    },

    /**
     * MÉTODO: actualizarProducto
     * Actualiza un producto existente utilizando su ID.
     * * @param {number|string} id - Identificador único (Long en Java).
     * @param {Producto} producto - Objeto con los cambios a aplicar (ProductoBaseDTO).
     */
    actualizarProducto(id, producto) {
        // Concatenamos el ID a la ruta base definida en el interceptor
        return clienteAxios.put(`/${id}`, producto);
    },

    /**
     * MÉTODO: obtenerPorId
     * Recupera la información completa de un solo producto (ProductoRespuestaDTO).
     * Útil para cargar datos en el formulario de edición.
     * * @param {number|string} id - ID del producto a buscar.
     */
    obtenerPorId(id) {
        return clienteAxios.get(`/${id}`);
    }
};

export default ProductoService;