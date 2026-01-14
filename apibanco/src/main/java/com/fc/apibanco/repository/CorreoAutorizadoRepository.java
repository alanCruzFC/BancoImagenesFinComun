package com.fc.apibanco.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fc.apibanco.model.CorreoAutorizado;

@Repository
public interface CorreoAutorizadoRepository extends JpaRepository<CorreoAutorizado, Long> {
    List<CorreoAutorizado> findByRegistroId(Long registroId);
}
