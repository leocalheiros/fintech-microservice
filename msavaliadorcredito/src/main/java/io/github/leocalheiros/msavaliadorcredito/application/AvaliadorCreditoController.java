package io.github.leocalheiros.msavaliadorcredito.application;

import io.github.leocalheiros.msavaliadorcredito.application.exceptions.NotFoundException;
import io.github.leocalheiros.msavaliadorcredito.application.exceptions.SolicitacaoCartaoException;
import io.github.leocalheiros.msavaliadorcredito.application.exceptions.UnknownComunicationException;
import io.github.leocalheiros.msavaliadorcredito.domain.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("avaliacoes-credito")
@RequiredArgsConstructor
public class AvaliadorCreditoController {

    private final AvaliadorCreditoService avaliadorCreditoService;

    @GetMapping
    public String status(){
        return "ok";
    }

    @GetMapping(value = "situacao-cliente", params = "cpf")
    public ResponseEntity clientStatusQuery(@RequestParam("cpf") String cpf){
        try {
            SituacaoCliente situacaoCliente = avaliadorCreditoService.getClientStatus(cpf);
            return ResponseEntity.ok(situacaoCliente);
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (UnknownComunicationException e) {
            return ResponseEntity.status(HttpStatus.resolve(e.getStatus())).body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity doAvaliacao(@RequestBody DadosAvaliacao dados){
        try {
            RetornoAvaliacaoCliente retornoAvaliacaoCliente = avaliadorCreditoService.doAvaliacao(dados.getCpf(),
                    dados.getRenda());
            return ResponseEntity.ok(retornoAvaliacaoCliente);
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (UnknownComunicationException e) {
            return ResponseEntity.status(HttpStatus.resolve(e.getStatus())).body(e.getMessage());
        }
    }

    @PostMapping("solicitacoes-cartao")
    public ResponseEntity requestCartao(@RequestBody DadosSolicitacaoEmissaoCartao dados){
        try{
            ProtocoloSolicitacaoCartao protocoloSolicitacaoCartao = avaliadorCreditoService.requestEmissaoCartao(dados);
            return ResponseEntity.ok(protocoloSolicitacaoCartao);
        }catch (SolicitacaoCartaoException e){
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}
