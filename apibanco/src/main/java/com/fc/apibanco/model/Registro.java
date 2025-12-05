package com.fc.apibanco.model;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class Registro {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String numeroSolicitud;
    private String carpetaRuta;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaEliminacion;

    @ManyToOne
    @JoinColumn(name = "creador_id", nullable = true)
    private Usuario creador;

    @OneToMany(mappedBy = "registro", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CorreoAutorizado> correosAutorizados;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNumeroSolicitud() {
		return numeroSolicitud;
	}

	public void setNumeroSolicitud(String numeroSolicitud) {
		this.numeroSolicitud = numeroSolicitud;
	}

	public String getCarpetaRuta() {
		return carpetaRuta;
	}

	public void setCarpetaRuta(String carpetaRuta) {
		this.carpetaRuta = carpetaRuta;
	}

	public LocalDateTime getFechaCreacion() {
		return fechaCreacion;
	}

	public void setFechaCreacion(LocalDateTime fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}

	public LocalDateTime getFechaEliminacion() {
		return fechaEliminacion;
	}

	public void setFechaEliminacion(LocalDateTime fechaEliminacion) {
		this.fechaEliminacion = fechaEliminacion;
	}

	public Usuario getCreador() {
		return creador;
	}

	public void setCreador(Usuario creador) {
		this.creador = creador;
	}

	public List<CorreoAutorizado> getCorreosAutorizados() {
		return correosAutorizados;
	}

	public void setCorreosAutorizados(List<CorreoAutorizado> correosAutorizados) {
		this.correosAutorizados = correosAutorizados;
	}
    
    
}

