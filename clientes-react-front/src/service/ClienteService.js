import axios from "axios";

const CLIENTE_BASE_API_URI="http://localhost:8080/api/v1/clientes";

class ClienteService{

    getAllClientes(){
    return axios.get(CLIENTE_BASE_API_URI);
    }

        createCliente(cliente){
    return axios.post(CLIENTE_BASE_API_URI,cliente);
    }

            getClienteById(id){
    return axios.get(CLIENTE_BASE_API_URI+'/'+id);
    }
       updateCliente(id,cliente){
   return axios.patch(CLIENTE_BASE_API_URI+'/'+id, cliente);
    }

    // Método para eliminar un cliente por su ID
deleteCliente(id) {
    return axios.delete(CLIENTE_BASE_API_URI+'/'+id);
}
}

export default new ClienteService();
