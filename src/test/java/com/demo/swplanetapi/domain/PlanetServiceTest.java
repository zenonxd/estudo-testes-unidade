package com.demo.swplanetapi.domain;

import static com.demo.swplanetapi.common.PlanetConstrants.PLANET;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = PlanetService.class)
public class PlanetServiceTest {
    private PlanetService planetService;

    @Test
    public void createPlanet_WithValidData_ReturnsPlanet() {
       Planet sut = planetService.create(PLANET);

       //planeta criado pelo service é igual ao que criei agora?
       assertThat(sut).isEqualTo(PLANET);

       // como esse metódo ^ trabalha com igualdade, não esquecer de implementar
        // o equals do apache na classe planet.
    }
}
