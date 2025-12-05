package com.fc.apibanco.security;

import java.io.IOException;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fc.apibanco.model.ApiKey;
import com.fc.apibanco.service.ApiKeyService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class ApiKeyFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(ApiKeyFilter.class);

    @Autowired
    private ApiKeyService apiKeyService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // Permitir preflight
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        // Si viene JWT, dejar que el filtro JWT lo procese
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String apiKeyHeader = request.getHeader("X-API-KEY");
        if (apiKeyHeader == null || apiKeyHeader.isBlank()) {
            // No hay API key: no autenticamos aquí, dejamos que la cadena continúe
            filterChain.doFilter(request, response);
            return;
        }

        logger.debug("ApiKeyFilter - X-API-KEY: {}", apiKeyHeader);

        Optional<ApiKey> opt = apiKeyService.findByClave(apiKeyHeader);
        if (opt.isEmpty() || !opt.get().isActivo()) {
            logger.warn("ApiKeyFilter - API key inválida o inactiva: {}", apiKeyHeader);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "API key inválida o inactiva");
            return;
        }

        ApiKey apiKey = opt.get();

        // Validar permisos por método HTTP usando los booleanos de la entidad
        String metodo = request.getMethod();
        if ("GET".equalsIgnoreCase(metodo) && !apiKey.isLectura()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "La API key no tiene permiso de lectura");
            return;
        }
        if ("POST".equalsIgnoreCase(metodo) && !apiKey.isEscritura()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "La API key no tiene permiso de escritura");
            return;
        }
        if ("PUT".equalsIgnoreCase(metodo) && !apiKey.isActualizacion()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "La API key no tiene permiso de actualización");
            return;
        }
        if ("DELETE".equalsIgnoreCase(metodo) && !apiKey.isEliminacion()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "La API key no tiene permiso de eliminación");
            return;
        }

        request.setAttribute("consumidor", apiKey.getConsumidor());


        logger.debug("ApiKeyFilter - autenticado consumidor={} y continuando", apiKey.getConsumidor());

        filterChain.doFilter(request, response);
    }
}

