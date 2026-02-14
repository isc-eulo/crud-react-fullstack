import React, { useEffect, useState } from 'react';
import ClienteService from '../service/ClienteService';
import { useNavigate, useParams } from 'react-router-dom';

export const AddClienteComponent = () => {
    const [nombre, setNombre] = useState('');
    const [identificacion, setIdentificacion] = useState('');
    const [email, setEmail] = useState('');
    
    // Estado para activar visualmente las validaciones de Bootstrap
    const [validated, setValidated] = useState(false);

    const navigate = useNavigate();
    const { id } = useParams();

 const saveOrUpdateCliente = (e) => {
    // Evita que la página se recargue, permitiendo que React maneje el envío
    if (e) e.preventDefault();

    // --- INICIO DE VALIDACIÓN MANUAL ---
    // Verificamos que los campos obligatorios no estén vacíos antes de seguir.
    // .trim() quita los espacios en blanco al inicio y al final.
    if (!nombre.trim() || !identificacion.trim() || !email.trim()) {
        alert("Por favor, rellene el Nombre y la Identificación. Son obligatorios.");
        return; // Detiene la ejecución aquí mismo si falta algún dato
    }
    // --- FIN DE VALIDACIÓN MANUAL ---

    // Aquí se construye el objeto 'cliente' usando los valores actuales de las variables de estado.
    // Estas variables (nombre, identificacion, email) se llenaron "al vuelo" 
    // en el formulario con el onChange cada vez que el usuario presionó una tecla.
    const cliente = { nombre, identificacion, email };

    // Agregamos un log para confirmar en consola qué datos se están enviando
    console.log("Datos capturados al vuelo listos para enviar:", cliente);

    if (id && id !== "0") {
        // Lógica para actualizar un cliente existente pasando el ID que faltaba en la URL
        ClienteService.updateCliente(id, cliente)
            .then(() => {
                console.log("Cliente actualizado con éxito");
                navigate('/clientes');
            })
            .catch(error => {
                console.log("Error al actualizar (Revisa el ID o el CORS):", error);
            });
    } else {
        // Lógica para crear un nuevo cliente (POST)
        ClienteService.createCliente(cliente)
            .then(() => {
                console.log("Cliente creado con éxito");
                navigate('/clientes');
            })
            .catch(error => {
                console.log("Error al crear:", error);
            });
    }
};

    useEffect(() => {
        if (id && id !== "0") {
            ClienteService.getClienteById(id)
                .then((response) => {
                    setNombre(response.data.nombre);
                    setIdentificacion(response.data.identificacion);
                    setEmail(response.data.email);
                })
                .catch(error => console.log("Error al obtener datos:", error));
        }
    }, [id]);

    const title = id && id !== "0" ? "Actualizar Cliente" : "Registrar Cliente";

    return (
        <div className='container'>
            <div className='row'>
                <div className='card col-md-6 offset-md-3 mt-5 shadow'>
                    <h2 className='text-center mt-3'>{title}</h2> 
                    <div className='card-body'>
                        {/* noValidate evita el popup feo del navegador para usar el de Bootstrap */}
                       <form noValidate> {/* Quitamos las clases de validación de Bootstrap aquí */}
    {/* Campo Nombre */}
    <div className='form-group mb-3'>
        <label className='form-label'>Nombre:</label>
        <input
            type='text'
            placeholder='Escriba el nombre completo'
            className='form-control'
            value={nombre}
            onChange={(e) => setNombre(e.target.value)}
        />
        {/* BORRADO: <div className="invalid-feedback">El nombre es obligatorio.</div> */}
    </div>

    {/* Campo Identificación */}
    <div className='form-group mb-3'>
        <label className='form-label'>Identificación:</label>
        <input
            type='text'
            placeholder='Número de documento'
            className='form-control'
            value={identificacion}
            onChange={(e) => setIdentificacion(e.target.value)}
        />
        {/* BORRADO: <div className="invalid-feedback">La identificación es obligatoria.</div> */}
    </div>

    {/* Campo Email - Ya configurado como texto simple */}
    <div className='form-group mb-3'>
        <label className='form-label'>Email:</label>
        <input
            type='text' 
            placeholder='ejemplo@correo.com'
            className='form-control'
            value={email}
            onChange={(e) => setEmail(e.target.value)}
        />
    </div>

    <div className='mt-4'>
        <button type='button'
            onClick={(e) => saveOrUpdateCliente(e)}
            className='btn btn-success me-2'>
            Guardar
        </button>
        <button type='button' className='btn btn-danger' onClick={() => navigate('/clientes')}>
            Cancelar
        </button>
    </div>
</form>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default AddClienteComponent;