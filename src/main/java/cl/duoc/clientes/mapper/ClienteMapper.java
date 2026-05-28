package cl.duoc.clientes.mapper;

import cl.duoc.clientes.model.Cliente;
import cl.duoc.clientes.dto.CreateRequestCliente;
import cl.duoc.clientes.dto.UpdateRequestCliente;

public class ClienteMapper {
public static Cliente toCliente(CreateRequestCliente request){
   
    Cliente cliente = new Cliente();
    cliente.setId(request.id());
    cliente.setNombre(request.nombre());        
    cliente.setPassword(request.password());
    cliente.setCorreo(request.correo());
    cliente.setRol(request.rol());
    return cliente;
}
   public static Cliente toCliente(
            int id,
            UpdateRequestCliente request
    ){

        Cliente cliente = new Cliente();

        cliente.setId(id);
        cliente.setNombre(request.nombre());
        cliente.setPassword(request.password());
        cliente.setCorreo(request.correo());
        cliente.setRol(request.rol());

        return cliente;
    }
}