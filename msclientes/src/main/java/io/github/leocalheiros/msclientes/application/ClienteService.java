package io.github.leocalheiros.msclientes.application;

import io.github.leocalheiros.msclientes.domain.Cliente;
import io.github.leocalheiros.msclientes.infra.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClienteService {
    private static final Logger log = LoggerFactory.getLogger(ClientesController.class);

    private final ClienteRepository clienteRepository;

    @Transactional
    public Cliente save(Cliente cliente){
        return clienteRepository.save(cliente);
    }

    public Optional<Cliente> getByCpf(String cpf){
        return clienteRepository.findByCpf(cpf);
    }
}
