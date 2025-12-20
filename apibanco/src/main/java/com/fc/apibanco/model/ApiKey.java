package com.fc.apibanco.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

@Entity
public class ApiKey {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(unique = true)
  private String clave;
  private String consumidor;

  private boolean lectura;
  private boolean escritura;
  private boolean actualizacion;
  private boolean eliminacion;
  private boolean activo = true; 

  private LocalDateTime fechaCreacion; 
  private LocalDateTime fechaActualizacion; 
  private LocalDateTime fechaEliminacion; 
  
  @PrePersist 
  public void prePersist() { 
	  this.fechaCreacion = LocalDateTime.now(); 
  } 
  
  @PreUpdate 
  public void preUpdate() { 
	  this.fechaActualizacion = LocalDateTime.now(); 
  }
  
  
  public Long getId() {
	return id;
  }
  public void setId(Long id) {
	this.id = id;
  }
  public String getClave() {
	return clave;
  }
  public void setClave(String clave) {
	this.clave = clave;
  }
  public String getConsumidor() {
	return consumidor;
  }
  public void setConsumidor(String consumidor) {
	this.consumidor = consumidor;
  }
  public boolean isLectura() {
	return lectura;
  }
  public void setLectura(boolean lectura) {
	this.lectura = lectura;
  }
  public boolean isEscritura() {
	return escritura;
  }
  public void setEscritura(boolean escritura) {
	this.escritura = escritura;
  }
  public boolean isActualizacion() {
	return actualizacion;
  }
  public void setActualizacion(boolean actualizacion) {
	this.actualizacion = actualizacion;
  }
  public boolean isEliminacion() {
	return eliminacion;
  }
  public void setEliminacion(boolean eliminacion) {
	this.eliminacion = eliminacion;
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
  public boolean isActivo() {
	return activo;
  }
  public void setActivo(boolean activo) {
	this.activo = activo;
  }
  
  
}

