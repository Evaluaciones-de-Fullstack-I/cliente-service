package cl.duoc.clientes.mapper;

import cl.duoc.clientes.model.Cliente;
import cl.duoc.clientes.dto.CreateRequestCliente;
import cl.duoc.clientes.dto.UpdateRequestCliente;

public class ClienteMapper {

    // CREATE - RECORDATORIO: Corregido para mapear a los usuarios de la web como "NUEVO"
    public static Cliente toCliente(CreateRequestCliente request){

        Cliente cliente = new Cliente();

        cliente.setNombre(request.nombre());
        cliente.setPassword(request.password());
        cliente.setCorreo(request.correo());
        cliente.setRol(request.rol());

        // Antes decía "CLIENTES ANTIGUOS". Lo cambiamos a "NUEVO" para Pedro y los futuros registros.
        cliente.setTipoCliente("NUEVO");

        return cliente;
    }

    // UPDATE
    public static void updateCliente(
            Cliente cliente,
            UpdateRequestCliente request
    ){
        cliente.setNombre(request.nombre());
        cliente.setPassword(request.password());
        cliente.setCorreo(request.correo());
        cliente.setRol(request.rol());
    }
}