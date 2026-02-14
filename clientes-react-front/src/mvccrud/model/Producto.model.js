/**
 * MODELO: Producto
 * * ¿Por qué usamos una clase en lugar de un objeto simple?
 * * 1. Mapeo del DTO: Garantiza que el Frontend hable el mismo "idioma" que el Backend (ProductoRespuestaDTO.java).
 * 2. Valores por Defecto: Evita errores de "undefined" en la vista al inicializar campos como stock en 0 o strings vacíos.
 * 3. Encapsulamiento: Si en el futuro el backend cambia el nombre de un campo (ej. de 'precioUnitario' a 'precio'), 
 * solo modificamos este modelo y no todos los componentes de la App.
 * 4. Tipado Manual: Actúa como una documentación viva de la estructura de datos que maneja nuestro sistema MVC.
 */

export class Producto {
    constructor(id = null, nombre = '', categoria = '', precioUnitario = 0, stock = 0) {
        // Campos que vienen de ProductoBaseDTO y ProductoRespuestaDTO
        this.id = id;
        this.nombre = nombre;
        this.categoria = categoria; // Este puede ser el String o el objeto DTO según lo necesites
        this.precioUnitario = precioUnitario;
        this.stock = stock;
    }

    /**
     * Método estático para convertir la respuesta del servidor (JSON) 
     * en una instancia real de nuestra clase.
     */
    static fromJson(json) {
        return new Producto(
            json.id,
            json.nombre,
            json.categoria,
            json.precioUnitario,
            json.stock
        );
    }
}