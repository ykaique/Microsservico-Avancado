package br.com.alura.microservice.loja.repository;

import br.com.alura.microservice.loja.model.Compra;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompraRespository extends CrudRepository<Compra, Long> {
}
