package io.github.leocalheiros.msavaliadorcredito.infra.clients;

import io.github.leocalheiros.msavaliadorcredito.domain.model.DadosCliente;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "msclientes", path = "/clientes")
public interface ClienteResourceClient {

    @GetMapping
    ResponseEntity<DadosCliente> dadosCliente(@RequestParam("cpf") String cpf);
}
