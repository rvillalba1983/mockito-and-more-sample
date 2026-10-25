package com.rvillalba.toolbox.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import com.rvillalba.toolbox.api.SampleResponse;
import com.rvillalba.toolbox.service.ToolboxService;

import net.datafaker.Faker;

/**
 * Test de la capa web: levanta solo el slice de Spring MVC (controlador, serialización JSON,
 * mapeo de rutas) y sustituye el servicio por un mock con {@link MockitoBean}
 * (reemplazo de @MockBean desde Spring Framework 6.2 / Spring Boot 3.4).
 * {@link MockMvcTester} permite escribir las aserciones con AssertJ.
 */
@WebMvcTest(ToolboxController.class)
class ToolboxControllerWebMvcTest {

    @Autowired
    private MockMvcTester mvc;

    @MockitoBean
    private ToolboxService toolboxService;

    private final Faker faker = new Faker();

    @Test
    void whenGetSample_thenReturnsJsonFromService() {
        var id = faker.idNumber().valid();
        var response = SampleResponse.builder()
                .message("The id is " + id)
                .address(faker.address().fullAddress())
                .build();
        given(toolboxService.process(id)).willReturn(response);

        var result = mvc.get().uri("/sample/{id}", id).accept(MediaType.APPLICATION_JSON);

        assertThat(result).hasStatus(HttpStatus.OK)
                .hasContentTypeCompatibleWith(MediaType.APPLICATION_JSON);
        assertThat(result).bodyJson().extractingPath("$.message").isEqualTo(response.getMessage());
        assertThat(result).bodyJson().extractingPath("$.address").isEqualTo(response.getAddress());
    }

    @Test
    void whenGetUnknownPath_thenNotFound() {
        assertThat(mvc.get().uri("/unknown")).hasStatus(HttpStatus.NOT_FOUND);
    }
}
