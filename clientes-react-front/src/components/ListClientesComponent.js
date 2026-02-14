import React, { useState, useEffect } from 'react';
import { Table, Container, Card } from 'react-bootstrap';
import ClienteService from '../service/ClienteService';
import { Link, useNavigate } from 'react-router-dom';

export const ListClientesComponent = () => {
    // Inicializamos con un array vacío para evitar errores antes de cargar datos
    const [clientes, setClientes] = useState([
    ]);

 // Esta función carga los datos al principio y también después de borrar
const listarClientes = () => {
    ClienteService.getAllClientes()
        .then(response => {
            setClientes(response.data); // Esto es lo que realmente actualiza la pantalla
            console.log("Lista actualizada al vuelo");
        })
        .catch(error => {
            console.log(error);
        });
};

 const navigate = useNavigate();
    useEffect(() => {
listarClientes();
    }, []); // 3. EL ARRAY VACÍO ES VITAL para evitar bucles infinitos

    const deleteCliente = (id) => {
    // Es buena práctica pedir confirmación antes de borrar definitivamente
    if (window.confirm("¿Está seguro de que desea eliminar este cliente?")) {
        
        // Llamamos al servicio pasando el ID del cliente que queremos borrar
        ClienteService.deleteCliente(id)
            .then((response) => {
                // Si el servidor responde con éxito, refrescamos la lista
                // Aquí deberías llamar a la función que carga tus clientes (ej. getAllClientes())
                console.log("Cliente eliminado correctamente:", response.data);
                listarClientes();
            })
            .catch(error => {
                // Si falla, notificamos el error (puede ser por el ID o problemas de red/CORS)
                console.log("Error al eliminar el cliente:", error);
            });
    }
};


    return (
        <Container className="mt-5">
            <Card className="shadow">
                <Card.Header className="bg-primary text-white">
                    <h3 className="mb-0">Listado de Clientes</h3>
                    <Link to='/add-cliente' className='btn btn-primary mb-2'>
                Agregar Cliente
            </Link>
                </Card.Header>
                <Card.Body>
                    <Table striped bordered hover responsive>
                        <thead className="table-dark">
                            <tr>
                                <th>ID</th>
                                <th>Nombre</th>
                                <th>Identificación</th>
                                <th>Email</th>
                                <th>Fecha Registro</th>
                                <th>Acciones</th>
                            </tr>
                        </thead>
                        <tbody>
                            {clientes.length > 0 ? (
                                clientes.map((cliente) => (
                                    <tr key={cliente.id}>
                                        <td>{cliente.id}</td>
                                        <td>{cliente.nombre}</td>
                                        <td>{cliente.identificacion}</td>
                                        <td>{cliente.email}</td>
                                        <td>
                                            {/* Formateo sencillo de la fecha de tu entidad Java */}
                                            {new Date(cliente.fechaRegistro).toLocaleDateString()}
                                        </td>
                                        <td>
                                            <Link
                                                className="btn btn-info me-2"
                                                to={`/edit-cliente/${cliente.id}`}
                                            >
                                                Actualizar
                                            </Link>

                                            <button
    className="btn btn-danger"
    onClick={() => deleteCliente(cliente.id)} // Llama a la función pasando el ID del cliente actual
>
    Eliminar
</button>
                                        </td>
                                    </tr>
                                ))
                            ) : (
                                <tr>
                                    <td colSpan="5" className="text-center">
                                        No hay clientes registrados.
                                    </td>
                                </tr>
                            )}
                        </tbody>
                    </Table>
                </Card.Body>
            </Card>
        </Container>
    );
}

export default ListClientesComponent;