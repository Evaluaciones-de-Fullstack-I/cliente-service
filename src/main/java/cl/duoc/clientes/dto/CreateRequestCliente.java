package cl.duoc.clientes.dto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;



public record CreateRequestCliente(
@NotBlank(message = "El ID es obligatorio") @PositiveOrZero(message = "El ID debe ser un número positivo o cero") int id,
@NotBlank(message = "El nombre es obligatorio") String nombre,
@NotBlank(message = "La contraseña es obligatoria") 
@Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")String password,

@NotBlank(message = "El correo es obligatorio")
@Email(message = "El correo no tiene un formato válido") String correo,
@NotBlank(message = "El rol es obligatorio") String rol){

    
}


    
    
