package com.fc.apibanco.controller;

import java.security.Principal;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fc.apibanco.dto.RegistroRequest;
import com.fc.apibanco.model.Registro;
import com.fc.apibanco.service.RegistroService;
import com.fc.apibanco.util.Constantes;

import jakarta.servlet.http.HttpServletRequest;

@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@RestController
@RequestMapping("/api")
public class RegistroController {

    private final RegistroService registroService;
    
    public RegistroController(RegistroService registroService) {
        this.registroService = registroService;
    }

    @PostMapping("/registro")
    @PreAuthorize("hasAnyRole('USER','SUPERVISOR','SUPERADMIN')")
    public ResponseEntity<?> crearRegistro(@RequestBody RegistroRequest request,
                                           Principal principal,
                                           HttpServletRequest httpRequest) {
        String creadorUsername;

        if (principal != null) {
            creadorUsername = principal.getName();
        } else {
            String consumidor = (String) httpRequest.getAttribute("consumidor");
            if (consumidor == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                     .body(Map.of(Constantes.MSG, "Falta autenticación o API key válida"));
            }

            if (request.getCorreosAutorizados() != null && !request.getCorreosAutorizados().isEmpty()) {
                creadorUsername = request.getCorreosAutorizados().get(0);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                     .body(Map.of(Constantes.MSG, "Se requieren correos autorizados para crear registro vía API key"));
            }
        }

        Registro registro = registroService.crearRegistro(request, creadorUsername);

        String url = "/visualizar/" + registro.getNumeroSolicitud();
        return ResponseEntity.ok(Map.of(
        		Constantes.MSG, "Registro creado exitosamente",
            "url", url
        ));
    }
}
