package io.github.leocalheiros.msavaliadorcredito.application;

import feign.FeignException;
import io.github.leocalheiros.msavaliadorcredito.application.exceptions.NotFoundException;
import io.github.leocalheiros.msavaliadorcredito.application.exceptions.SolicitacaoCartaoException;
import io.github.leocalheiros.msavaliadorcredito.application.exceptions.UnknownComunicationException;
import io.github.leocalheiros.msavaliadorcredito.domain.model.*;
import io.github.leocalheiros.msavaliadorcredito.infra.clients.CartoesResourceClient;
import io.github.leocalheiros.msavaliadorcredito.infra.clients.ClienteResourceClient;
import io.github.leocalheiros.msavaliadorcredito.infra.mqueue.SolicitacaoEmissaoCartaoPublisher;
import lombok.RequiredArgsConstructor;
import org.bouncycastle.util.encoders.UTF8;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AvaliadorCreditoService {
    private final ClienteResourceClient clientesClient;
    private final CartoesResourceClient cartoesClient;
    private final SolicitacaoEmissaoCartaoPublisher emissaoCartaoPublisher;

    public SituacaoCliente getClientStatus(String cpf) throws NotFoundException, UnknownComunicationException {
        try {
            ResponseEntity<DadosCliente> dadosClienteResponse = clientesClient.dadosCliente(cpf);
            ResponseEntity<List<CartaoCliente>> cartoesResponse = cartoesClient.getCartoesByCliente(cpf);

            return SituacaoCliente.builder()
                    .cliente(dadosClienteResponse.getBody())
                    .cartoes(cartoesResponse.getBody())
                    .build();
        } catch (FeignException.FeignClientException e) {
            int status = e.status();
            if (HttpStatus.NOT_FOUND.value() == status) {
                throw new NotFoundException();
            }
            throw new UnknownComunicationException(e.getMessage(), status);
        }
    }

    public RetornoAvaliacaoCliente doAvaliacao(String cpf, Long renda) throws NotFoundException,
            UnknownComunicationException {
        try {
            ResponseEntity<DadosCliente> dadosClienteResponse = clientesClient.dadosCliente(cpf);
            ResponseEntity<List<Cartao>> cartoesResponse = cartoesClient.getCartoesUntilRenda(renda);

            List<Cartao> cartoes = cartoesResponse.getBody();
            var listaCartoesAprovados = cartoes.stream().map(cartao -> {
                DadosCliente dadosCliente = dadosClienteResponse.getBody();

                BigDecimal limiteBasico = cartao.getLimiteBasico();
                BigDecimal idadeBD = BigDecimal.valueOf(dadosCliente.getIdade());

                BigDecimal fator = idadeBD.divide(BigDecimal.valueOf(10));
                BigDecimal limiteAprovado = fator.multiply(limiteBasico);

                CartaoAprovado aprovado = new CartaoAprovado();
                aprovado.setCartao(cartao.getNome());
                aprovado.setBandeira(cartao.getBandeiraCartao());
                aprovado.setLimiteAprovado(limiteAprovado);

                return aprovado;
            }).collect(Collectors.toList());

            return new RetornoAvaliacaoCliente(listaCartoesAprovados);
        } catch (FeignException.FeignClientException e) {
            int status = e.status();
            if (HttpStatus.NOT_FOUND.value() == status) {
                throw new NotFoundException();
            }
            throw new UnknownComunicationException(e.getMessage(), status);
        }
    }

    public ProtocoloSolicitacaoCartao requestEmissaoCartao(DadosSolicitacaoEmissaoCartao dados){
        try{
            emissaoCartaoPublisher.requestCartao(dados);
            var numberProtocolo = UUID.randomUUID().toString();
            return new ProtocoloSolicitacaoCartao(numberProtocolo);
        }catch (Exception e){
            throw new SolicitacaoCartaoException(e.getMessage());
        }
    }
}
