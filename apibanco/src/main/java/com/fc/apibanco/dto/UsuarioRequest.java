package com.fc.apibanco.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class UsuarioRequest {

    @NotBlank(message = "El nombre de usuario es obligatorio")
    private String username;

    @NotBlank(message = "El nombre es obligatorio")
    private String firstName;

    @NotBlank(message = "El apellido es obligatorio")
    private String lastName;

    @Email(message = "Debe proporcionar un correo válido")
    @NotBlank(message = "El correo es obligatorio")
    private String email;

    private String password;

    @NotBlank(message = "El rol es obligatorio")
    private String rol;

    @NotNull(message = "El estado activo/inactivo es obligatorio")
    private Boolean activo;

    private String team;
    private String department;

    private Long supervisorId;

    //--------------------- Getters y setters------------------------------------------
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public Boolean isActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }

    public String getTeam() { return team; }
    public void setTeam(String team) { this.team = team; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public Long getSupervisorId() { return supervisorId; }
    public void setSupervisorId(Long supervisorId) { this.supervisorId = supervisorId; }
    
    
}
