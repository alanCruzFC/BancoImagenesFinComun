package com.fc.apibanco.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class Archivo {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String nombreOriginal;
  private String nombreFinal;
  private String tipo;
  private String rutaLocal;
  private LocalDateTime fechaSubida;
  private LocalDateTime fechaEliminacion;

  @ManyToOne private Registro registro;
  @ManyToOne private Usuario usuario;
  public Long getId() {
	return id;
  }
  public void setId(Long id) {
	this.id = id;
  }
  public String getNombreOriginal() {
	return nombreOriginal;
  }
  public void setNombreOriginal(String nombreOriginal) {
	this.nombreOriginal = nombreOriginal;
  }
  public String getNombreFinal() {
	return nombreFinal;
  }
  public void setNombreFinal(String nombreFinal) {
	this.nombreFinal = nombreFinal;
  }
  public String getTipo() {
	return tipo;
  }
  public void setTipo(String tipo) {
	this.tipo = tipo;
  }
  public String getRutaLocal() {
	return rutaLocal;
  }
  public void setRutaLocal(String rutaLocal) {
	this.rutaLocal = rutaLocal;
  }
  public LocalDateTime getFechaSubida() {
	return fechaSubida;
  }
  public void setFechaSubida(LocalDateTime fechaSubida) {
	this.fechaSubida = fechaSubida;
  }
  public LocalDateTime getFechaEliminacion() {
	return fechaEliminacion;
  }
  public void setFechaEliminacion(LocalDateTime fechaEliminacion) {
	this.fechaEliminacion = fechaEliminacion;
  }
  public Registro getRegistro() {
	return registro;
  }
  public void setRegistro(Registro registro) {
	this.registro = registro;
  }
  public Usuario getUsuario() {
	return usuario;
  }
  public void setUsuario(Usuario usuario) {
	this.usuario = usuario;
  }
  
  
}
