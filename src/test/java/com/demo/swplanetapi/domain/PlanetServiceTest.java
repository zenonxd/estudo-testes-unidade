package com.demo.swplanetapi.domain;

import static com.demo.swplanetapi.common.PlanetConstrants.INVALID_PLANET;
import static com.demo.swplanetapi.common.PlanetConstrants.PLANET;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PlanetServiceTest {
    @InjectMocks
    private PlanetService planetService;
    @Mock
    private PlanetRepository planetRepository;
    @Test
    public void createPlanet_WithValidData_ReturnsPlanet() {
        //AAA
        //ARRANGE - ARRUMA OS DADOS PRO TESTE
        when(planetRepository.save(PLANET)).thenReturn(PLANET);

        //ACT - FAZ A OPERAÇÃO DE FATO QUE QUEREMOS TESTAR
       Planet sut = planetService.create(PLANET);

       //planeta criado pelo service é igual ao que criei agora?

        //ASSERT - AFERE SE O SISTEMA SOB TESTE É O QUE ESPERAMOS.
       assertThat(sut).isEqualTo(PLANET);

       // como esse metódo ^ trabalha com igualdade, não esquecer de implementar
        // o equals do apache na classe planet.
    }

    @Test
    public void createPlanet_WithInvalidData_ReturnsPlanet() {
        when(planetRepository.save(INVALID_PLANET)).thenThrow(RuntimeException.class);

        //verifica se o invalid_planet executou a exception ali em cima
        assertThatThrownBy(() -> planetService.create(INVALID_PLANET)).isInstanceOf(RuntimeException.class);
    }

    @Test
    public void getPlanet_ByExistingId_ReturnsPlanet() {
        //PASSAR NO findById o parâmetro a ser testado, retornará o planeta.
        when(planetRepository.findById(1L)).thenReturn(Optional.of(PLANET));

        Optional<Planet> sut = planetService.get(1L);

        assertThat(sut).isNotEmpty();
        assertThat(sut.get()).isEqualTo(PLANET);
    }

    @Test
    public void getPlanet_ByUnexistingId_ReturnsPlanet() {
        when(planetRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Planet> sut = planetService.get(1L);

        assertThat(sut).isEmpty();
    }

    @Test
    public void getPlanet_ByExistingName_ReturnsPlanet() {
        when(planetRepository.findByName(PLANET.getName())).thenReturn(Optional.of(PLANET));

        Optional<Planet> sut = planetService.getByName(PLANET.getName());

        assertThat(sut).isNotEmpty();
        assertThat(sut.get()).isEqualTo(PLANET);
    }

    @Test
    public void getPlanet_ByUnexistingName_ReturnsPlanet() {
        final String name = "Unexisting name";
        when(planetRepository.findByName(name)).thenReturn(Optional.empty());

        Optional<Planet> sut = planetService.getByName(name);

        assertThat(sut).isEmpty();
    }
}
