package cl.duoc.clientes.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "clientes")//recurso es el nombre de la tabla que tendria en neon
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {

    //definicion de atributos 

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "correo",nullable = false, unique = true, length = 150)
    private String correo;

    @Column(name = "password",nullable = false, length = 60)
    private String password;

    @Column(name = "rol", nullable = false, length = 20)
    private String rol; // refiriendose a si esDMIN, VENDEDOR, CLIENTE

    public int getId() {
        return id;
    }

}

