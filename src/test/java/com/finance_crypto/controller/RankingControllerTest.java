package com.finance_crypto.controller;

import com.finance_crypto.dto.RankingAtivoDTO;
import com.finance_crypto.service.RankingService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RankingControllerTest {

    @Test
    void deveRetornarRankingComSucessoQuandoServiceRetornarDados() {
        RankingService rankingService = mock(RankingService.class);
        RankingController controller = new RankingController(rankingService);
        
        // Criamos um ID de usuário fictício para passar para o método novo
        Long mockUsuarioId = 1L;

        List<RankingAtivoDTO> mockLista = List.of(
                new RankingAtivoDTO("BTC", 300000.0, 350000.0, 16.6)
        );

        // Atualizado para usar o método novo: obterRankingAtivos(Long)
        when(rankingService.obterRankingAtivos(mockUsuarioId)).thenReturn(mockLista);

        // Supondo que o Controller também foi atualizado para receber o ID do usuário
        List<RankingAtivoDTO> response = controller.obterLucrativos(mockUsuarioId);

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals("BTC", response.get(0).getSimbolo());
        verify(rankingService, times(1)).obterRankingAtivos(mockUsuarioId);
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoHouverAtivosNoService() {
        RankingService rankingService = mock(RankingService.class);
        RankingController controller = new RankingController(rankingService);
        Long mockUsuarioId = 1L;

        when(rankingService.obterRankingAtivos(mockUsuarioId)).thenReturn(List.of());

        List<RankingAtivoDTO> response = controller.obterLucrativos(mockUsuarioId);

        assertNotNull(response);
        assertTrue(response.isEmpty());
        verify(rankingService, times(1)).obterRankingAtivos(mockUsuarioId);
    }

    @Test
    void deveLancarExcecaoQuandoServicoDeRankingFalhar() {
        RankingService rankingService = mock(RankingService.class);
        RankingController controller = new RankingController(rankingService);
        Long mockUsuarioId = 1L;

        when(rankingService.obterRankingAtivos(mockUsuarioId)).thenThrow(new RuntimeException("Falha ao calcular ranking"));

        assertThrows(RuntimeException.class, () -> {
            controller.obterLucrativos(mockUsuarioId);
        });

        verify(rankingService, times(1)).obterRankingAtivos(mockUsuarioId);
    }
}