package model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
@Entity
public class Admin {
    @Id
    private Long id;
    @NotNull
    private String username;
    @NotNull
    private String password;
}
