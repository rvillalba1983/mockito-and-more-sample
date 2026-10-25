package com.rvillalba.toolbox.service;

import org.springframework.stereotype.Component;

/**
 * Simula un servicio externo (por ejemplo, una API de direcciones).
 * En los tests unitarios se sustituye por un mock.
 */
@Component
public class AddressExternalService {

    public String findAddress(String id) {
        return "Street Dummy " + id;
    }
}
