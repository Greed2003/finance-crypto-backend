package com.finance_crypto.controller;

import com.finance_crypto.dto.RankingAtivoDTO;
import com.finance_crypto.service.RankingService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ranking")
public class RankingController {

    private final RankingService rankingService;

    public RankingController(RankingService rankingService) {
        this.rankingService = rankingService;
    }

    @GetMapping("/lucrativos")
    public List<RankingAtivoDTO> obterLucrativos(@RequestParam(defaultValue = "1") Long usuarioId) {
        // Passamos o ID do usuário para o serviço fazer o cruzamento de dados
        return rankingService.obterRankingAtivos(usuarioId);
    }
}