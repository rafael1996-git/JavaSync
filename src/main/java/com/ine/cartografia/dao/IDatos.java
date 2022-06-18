package com.ine.cartografia.dao;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ine.cartografia.controlador.Controlador;
import com.ine.cartografia.model.tableSyncTemporal;



public interface IDatos extends JpaRepository<tableSyncTemporal, Integer>{
	
	static String name =Controlador.NAME_TABLA;
	@Query(value = "SELECT *  FROM public.autorizacion EXCEPT SELECT * FROM public_fwd.autorizacion ", nativeQuery = true)
	Collection<String> findAllDatosDiff();
	
	@Query(value = "SELECT *  FROM public.autorizacion  ", nativeQuery = true)
	Collection<String> findAllDatosLocales();
	
	@Query(value = " SELECT * FROM public_fwd.autorizacion ", nativeQuery = true)
	Collection<String> findAllDatosRemotos();
	
	@Query(value = " SELECT table_name \r\n"
			+ "FROM information_schema.tables \r\n"
			+ "WHERE table_schema='public' \r\n"
			+ "AND table_type='BASE TABLE' ", nativeQuery = true)
	Collection<String> NombreTable();
	
}
