package com.clientes.react.dominio.entity;

import lombok.*;
import org.springframework.data.annotation.Id; // IMPORTANTE: Usa esta
import org.springframework.data.relational.core.mapping.Table; // IMPORTANTE: Usa esta
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Table("clientes") // Anotación de Spring Data R2DBC
public class Cliente {
    @Id
    private Long id;
    private String nombre;
    private String identificacion;
    private String email;
    private LocalDateTime fechaRegistro;

    // NOTA: Borra el @PrePersist y @Column de JPA, no funcionan aquí.
}