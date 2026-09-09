package com.agilis.api.infrastructure.web;

import com.agilis.api.application.support.CreateSupportMessageUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/support")
public class SupportController {

    private final CreateSupportMessageUseCase createSupportMessageUseCase;

    public SupportController(CreateSupportMessageUseCase createSupportMessageUseCase) {
        this.createSupportMessageUseCase = createSupportMessageUseCase;
    }

    @PostMapping("/messages")
    public ResponseEntity<CreateSupportMessageUseCase.Output> create(
            @RequestBody CreateSupportMessageUseCase.Input input
    ) {
        // se o usuário estiver logado, associa a mensagem a ele automaticamente
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userId = (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal()))
                ? (String) auth.getPrincipal()
                : input.userId();

        var inputWithUser = new CreateSupportMessageUseCase.Input(
                userId, input.name(), input.email(), input.subject(), input.message()
        );
        return ResponseEntity.ok(createSupportMessageUseCase.execute(inputWithUser));
    }

    @GetMapping("/faq")
    public ResponseEntity<List<Map<String, String>>> getFaq() {
        return ResponseEntity.ok(List.of(
                Map.of("question", "Como faço para contratar um serviço?",
                        "answer", "Busque o serviço desejado, escolha um horário disponível e confirme o agendamento."),
                Map.of("question", "Como funciona o pagamento?",
                        "answer", "O pagamento é realizado de forma segura através do Mercado Pago. Ao finalizar a contratação, você será direcionado para o ambiente de pagamento, onde poderá escolher a forma de pagamento disponível."),
                Map.of("question", "Posso cancelar um serviço agendado?",
                        "answer", "Sim, você pode cancelar um agendamento pendente ou confirmado antes da data marcada."),
                Map.of("question", "Os profissionais são verificados?",
                        "answer", "Todo prestador passa por um cadastro com CNPJ e é avaliado pelos clientes após cada serviço."),
                Map.of("question", "O que fazer se tiver um problema com o serviço?",
                        "answer", "Entre em contato pelo formulário de suporte abaixo, descrevendo o ocorrido.")
        ));
    }
}