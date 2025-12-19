package com.fc.apibanco.security;

import java.io.IOException;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private final ApiKeyService apiKeyService;
    
    public ApiKeyFilter(ApiKeyService apiKeyService) {
        this.apiKeyService = apiKeyService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String apiKeyHeader = request.getHeader("X-API-KEY");
        if (apiKeyHeader == null || apiKeyHeader.isBlank()) {
            filterChain.doFilter(request, response);
            return;
        }

        logger.debug("ApiKeyFilter - X-API-KEY recibida: {}", apiKeyHeader);

        Optional<ApiKey> opt = apiKeyService.findByClave(apiKeyHeader);
        if (opt.isEmpty() || !opt.get().isActivo()) {
            logger.warn("ApiKeyFilter - API key inválida o inactiva: {}", apiKeyHeader);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "API key inválida o inactiva");
            return;
        }

        ApiKey apiKey = opt.get();
        String metodo = request.getMethod();

        // Validar permisos
        if ("GET".equalsIgnoreCase(metodo) && !apiKey.isLectura()) {
            logger.warn("ApiKeyFilter - consumidor={} intentó GET sin permiso", apiKey.getConsumidor());
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "La API key no tiene permiso de lectura");
            return;
        }
        if ("POST".equalsIgnoreCase(metodo) && !apiKey.isEscritura()) {
            logger.warn("ApiKeyFilter - consumidor={} intentó POST sin permiso", apiKey.getConsumidor());
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "La API key no tiene permiso de escritura");
            return;
        }
        if ("PUT".equalsIgnoreCase(metodo) && !apiKey.isActualizacion()) {
            logger.warn("ApiKeyFilter - consumidor={} intentó PUT sin permiso", apiKey.getConsumidor());
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "La API key no tiene permiso de actualización");
            return;
        }
        if ("DELETE".equalsIgnoreCase(metodo) && !apiKey.isEliminacion()) {
            logger.warn("ApiKeyFilter - consumidor={} intentó DELETE sin permiso", apiKey.getConsumidor());
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "La API key no tiene permiso de eliminación");
            return;
        }

        request.setAttribute("consumidor", apiKey.getConsumidor());

        logger.info("ApiKeyFilter - consumidor={} autenticado con permisos {} en {}", 
            apiKey.getConsumidor(), metodo, request.getRequestURI());

        filterChain.doFilter(request, response);
    }
}
