package com.fc.apibanco.dto;

public class UsuarioDTO {
    private final Long id;
    private final String username;
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String rol;
    private final boolean activo;
    private final String team;
    private final String department;
    private final Long supervisorId;
    private final String supervisorName;

    // Campo para mostrar solo a ADMIN/SUPERADMIN
    private final String passwordDesencriptada;

    public UsuarioDTO(Long id, String username, String firstName, String lastName,
                      String email, String rol, boolean activo,
                      String team, String department,
                      Long supervisorId, String supervisorName,
                      String passwordDesencriptada) {
        this.id = id;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.rol = rol;
        this.activo = activo;
        this.team = team;
        this.department = department;
        this.supervisorId = supervisorId;
        this.supervisorName = supervisorName;
        this.passwordDesencriptada = passwordDesencriptada; // puede ser null si no se permite ver
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getRol() { return rol; }
    public boolean isActivo() { return activo; }
    public String getTeam() { return team; }
    public String getDepartment() { return department; }
    public Long getSupervisorId() { return supervisorId; }
    public String getSupervisorName() { return supervisorName; }

    // Getter explícito con el nombre esperado por el frontend
    public String getPasswordDesencriptada() { return passwordDesencriptada; }

    @Override
    public String toString() {
        return "UsuarioDTO{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", rol='" + rol + '\'' +
                ", activo=" + activo +
                ", team='" + team + '\'' +
                ", department='" + department + '\'' +
                ", supervisorId=" + supervisorId +
                ", supervisorName='" + supervisorName + '\'' +
                ", passwordDesencriptada=" + (passwordDesencriptada != null ? "[visible]" : "null") +
                '}';
    }
}
