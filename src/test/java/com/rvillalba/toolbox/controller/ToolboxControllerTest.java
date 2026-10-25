package com.rvillalba.toolbox.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.rvillalba.toolbox.api.SampleResponse;
import com.rvillalba.toolbox.service.ToolboxService;
import com.rvillalba.toolbox.utils.ToolboxPodamFactory;

import net.datafaker.Faker;
import uk.co.jemos.podam.api.PodamFactory;

/**
 * Test unitario del controlador llamando al método directamente, sin capa HTTP.
 * Para probar la capa web completa ver {@link ToolboxControllerWebMvcTest}.
 */
@ExtendWith(MockitoExtension.class)
class ToolboxControllerTest {

    @Mock
    private ToolboxService toolboxService;

    @InjectMocks
    private ToolboxController toolboxController;

    private final PodamFactory podamFactory = ToolboxPodamFactory.getCustomFactory();
    private final Faker faker = new Faker();

    @Test
    void whenSample_thenReturnsServiceResponse_withPodam() {
        var id = podamFactory.manufacturePojo(String.class);
        var expected = podamFactory.manufacturePojoWithFullData(SampleResponse.class);
        given(toolboxService.process(id)).willReturn(expected);

        var result = toolboxController.sample(id);

        assertThat(result).isSameAs(expected);
    }

    @Test
    void whenSample_thenReturnsServiceResponse_withDatafaker() {
        var id = faker.idNumber().valid();
        var expected = SampleResponse.builder()
                .message(faker.lorem().sentence())
                .address(faker.address().fullAddress())
                .build();
        given(toolboxService.process(id)).willReturn(expected);

        var result = toolboxController.sample(id);

        assertThat(result).isEqualTo(expected);
    }
}
