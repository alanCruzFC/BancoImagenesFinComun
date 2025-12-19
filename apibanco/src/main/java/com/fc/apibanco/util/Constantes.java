package com.fc.apibanco.util;

import java.util.Set;

public final class Constantes {
	
	private Constantes() {
		
	}
	
	public static final String ARCHIVOS_CARP = "Archivos";
	public static final String NOT_FOUND = "Registro no encontrado";
	public static final String NO_AUTORIZADO = "Usuario no valido";
	public static final String SUPERADMIN = "SUPERADMIN";
	public static final String ADMIN = "ADMIN";
	public static final String USER = "USER";
	public static final String SUPERVISOR = "SUPERVISOR";
	public static final String MSG = "Mensaje";
	public static final String URL_DESC = "/api/descargar/";
	public static final Set<String> TIPOS_FIJOS = 
		    Set.of("INE", "COMPROBANTE_DOMICILIO", "ESTADO_CUENTA", "FOTONEGOCIO1", "FOTONEGOCIO2", "SELFIE");
	public static final Set<String> EXT_PER = 
			Set.of("jpg","jpeg","png","gif","pdf","docx","xlsx");


}
