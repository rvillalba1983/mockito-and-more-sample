package com.rvillalba.toolbox.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.rvillalba.toolbox.api.SampleResponse;
import com.rvillalba.toolbox.utils.ToolboxPodamFactory;

import net.datafaker.Faker;
import uk.co.jemos.podam.api.PodamFactory;

/**
 * Test unitario puro: sin contexto de Spring. Mockito sustituye la dependencia externa
 * y Podam / Datafaker generan los datos de entrada.
 */
@ExtendWith(MockitoExtension.class)
class ToolboxServiceTest {

    private static final String MESSAGE_PREFIX = "The id is ";
    private static final String ID_PREFIX = "Id";

    @InjectMocks
    private ToolboxService toolboxService;

    @Mock
    private AddressExternalService addressExternalService;

    @Captor
    private ArgumentCaptor<String> idCaptor;

    private final PodamFactory podamFactory = ToolboxPodamFactory.getCustomFactory();
    private final Faker faker = new Faker();

    // --- Podam: rellena POJOs completos con datos aleatorios ---

    @Test
    void givenIdWithPrefix_whenProcess_thenMessageIsUpperCase_withPodam() {
        var id = ID_PREFIX + podamFactory.manufacturePojo(String.class);
        var expected = podamFactory.manufacturePojoWithFullData(SampleResponse.class);
        expected.setMessage(MESSAGE_PREFIX + id.toUpperCase());
        given(addressExternalService.findAddress(id)).willReturn(expected.getAddress());

        var result = toolboxService.process(id);

        assertThat(result).isEqualTo(expected);
        then(addressExternalService).should().findAddress(id);
        then(addressExternalService).shouldHaveNoMoreInteractions();
    }

    @Test
    void givenIdWithoutPrefix_whenProcess_thenMessageIsLowerCase_withPodam() {
        var id = "x" + podamFactory.manufacturePojo(String.class);
        var expected = podamFactory.manufacturePojoWithFullData(SampleResponse.class);
        expected.setMessage(MESSAGE_PREFIX + id.toLowerCase());
        given(addressExternalService.findAddress(id)).willReturn(expected.getAddress());

        var result = toolboxService.process(id);

        assertThat(result).isEqualTo(expected);
    }

    // --- Datafaker: datos realistas (direcciones, identificadores...) ---

    @Test
    void givenIdWithPrefix_whenProcess_thenMessageIsUpperCase_withDatafaker() {
        var id = ID_PREFIX + faker.idNumber().valid();
        var address = faker.address().fullAddress();
        given(addressExternalService.findAddress(id)).willReturn(address);

        var result = toolboxService.process(id);

        assertThat(result.getMessage()).isEqualTo(MESSAGE_PREFIX + id.toUpperCase());
        assertThat(result.getAddress()).isEqualTo(address);
    }

    @Test
    void givenIdWithoutPrefix_whenProcess_thenMessageIsLowerCase_withDatafaker() {
        var id = faker.idNumber().valid();
        var address = faker.address().fullAddress();
        given(addressExternalService.findAddress(id)).willReturn(address);

        var result = toolboxService.process(id);

        assertThat(result)
                .extracting(SampleResponse::getMessage, SampleResponse::getAddress)
                .containsExactly(MESSAGE_PREFIX + id.toLowerCase(), address);
    }

    // --- Mockito avanzado: captura de argumentos y verificación de interacciones ---

    @Test
    void whenProcess_thenExternalServiceReceivesOriginalId() {
        var id = ID_PREFIX + faker.idNumber().valid();
        given(addressExternalService.findAddress(anyString())).willReturn(faker.address().fullAddress());

        toolboxService.process(id);

        then(addressExternalService).should().findAddress(idCaptor.capture());
        assertThat(idCaptor.getValue())
                .as("El servicio externo recibe el id sin normalizar")
                .isEqualTo(id);
    }

    @Test
    void whenProcessFails_thenExceptionIsPropagated() {
        var id = faker.idNumber().valid();
        given(addressExternalService.findAddress(id)).willThrow(new IllegalStateException("Servicio caído"));

        assertThatThrownBy(() -> toolboxService.process(id))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Servicio caído");
        then(addressExternalService).should(times(1)).findAddress(id);
    }
}
