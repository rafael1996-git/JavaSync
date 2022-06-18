package com.ine.cartografia.model;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;



@Entity
@Table(name = "TableTemporal")
public class tableSyncTemporal implements java.io.Serializable{

	private static final long serialVersionUID = 1L;
	@Id
	@Column(name = "id", unique = true, nullable = false)
	private Long id;
	@Column(name="detalle")
	private String detalle;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getDetalle() {
		return detalle;
	}
	public void setDetalle(String detalle) {
		this.detalle = detalle;
	}
	public tableSyncTemporal() {
	
	}
	public tableSyncTemporal(Long id, String detalle) {
		this.id = id;
		this.detalle = detalle;
	}
	

	
	
	

	

}
