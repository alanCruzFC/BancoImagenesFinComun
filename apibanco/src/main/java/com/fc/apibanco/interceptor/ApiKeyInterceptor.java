package com.fc.apibanco.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.fc.apibanco.model.ApiKey;
import com.fc.apibanco.repository.ApiKeyRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class ApiKeyInterceptor implements HandlerInterceptor {

    @Autowired
    private ApiKeyRepository apiKeyRepository;
    

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        System.out.println("Interceptando: " + request.getRequestURI());

        // 👇 Si viene Authorization con Bearer, dejamos que el filtro JWT se encargue
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return true; // no exigir API key
        }

        // 👇 Si no hay token, entonces sí exigimos API key
        String apiKeyHeader = request.getHeader("X-API-KEY");
        if (apiKeyHeader == null || apiKeyHeader.isBlank()) {
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "Falta autenticación o API key válida");
            return false;
        }

        ApiKey apiKey = apiKeyRepository.findByClaveAndFechaEliminacionIsNull(apiKeyHeader)
                .orElse(null);

        if (apiKey == null || !apiKey.isActivo()) {
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "API key inválida o inactiva");
            return false;
        }

        // Validar permisos según método HTTP
        String metodo = request.getMethod();
        if (metodo.equals("GET") && !apiKey.isLectura()) {
            response.sendError(HttpStatus.FORBIDDEN.value(), "La API key no tiene permiso de lectura");
            return false;
        }
        if (metodo.equals("POST") && !apiKey.isEscritura()) {
            response.sendError(HttpStatus.FORBIDDEN.value(), "La API key no tiene permiso de escritura");
            return false;
        }
        if (metodo.equals("PUT") && !apiKey.isActualizacion()) {
            response.sendError(HttpStatus.FORBIDDEN.value(), "La API key no tiene permiso de actualización");
            return false;
        }
        if (metodo.equals("DELETE") && !apiKey.isEliminacion()) {
            response.sendError(HttpStatus.FORBIDDEN.value(), "La API key no tiene permiso de eliminación");
            return false;
        }

        request.setAttribute("consumidor", apiKey.getConsumidor());
        return true;
    }


}