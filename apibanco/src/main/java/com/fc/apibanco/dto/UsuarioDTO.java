package com.fc.apibanco.dto;

public class UsuarioDTO {
    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String rol;
    private boolean activo;
    private String team;
    private String department;
    private Long supervisorId;
    private String supervisorName;

    private String password;

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
        this.password = passwordDesencriptada;
    }

	public Long getId() {
		return id;
	}

	public String getUsername() {
		return username;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getEmail() {
		return email;
	}

	public String getRol() {
		return rol;
	}

	public boolean isActivo() {
		return activo;
	}

	public String getTeam() {
		return team;
	}

	public String getDepartment() {
		return department;
	}

	public Long getSupervisorId() {
		return supervisorId;
	}

	public String getSupervisorName() {
		return supervisorName;
	}

	public String getPasswordDesencriptada() {
		return password;
	}
}

