package com.fc.apibanco.controller;

import java.security.Principal;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
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

import jakarta.servlet.http.HttpServletRequest;

@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@RestController
@RequestMapping("/api")
public class RegistroController {

    @Autowired private RegistroService registroService;

    @PostMapping("/registro")
    public ResponseEntity<?> crearRegistro(@RequestBody RegistroRequest request,
                                           Principal principal,
                                           HttpServletRequest httpRequest) {
        String username;
        if (principal != null) {
            username = principal.getName();
        } else {
            username = (String) httpRequest.getAttribute("consumidor");
            if (username == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                     .body("Falta autenticación o API key válida");
            }
            // 👉 si viene por API key, usar primer correo autorizado
            if (request.getCorreosAutorizados() != null && !request.getCorreosAutorizados().isEmpty()) {
                username = request.getCorreosAutorizados().get(0);
            }
        }
        Registro registro = registroService.crearRegistro(request, username);
        String url = "/visualizar/" + registro.getNumeroSolicitud();

        return ResponseEntity.ok(Map.of(
            "mensaje", "Registro creado exitosamente",
            "url", url
        ));
    }
}

