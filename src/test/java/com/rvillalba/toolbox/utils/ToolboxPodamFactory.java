package com.rvillalba.toolbox.utils;

import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

/**
 * Factoría de Podam compartida por los tests: colecciones de 3 elementos y memoización
 * activada para no regenerar objetos ya creados.
 */
public final class ToolboxPodamFactory {

    private static final PodamFactory CUSTOM_FACTORY = createFactory();

    private ToolboxPodamFactory() {
    }

    public static PodamFactory getCustomFactory() {
        return CUSTOM_FACTORY;
    }

    private static PodamFactory createFactory() {
        var factory = new PodamFactoryImpl();
        factory.getStrategy().setDefaultNumberOfCollectionElements(3);
        factory.getStrategy().setMemoization(true);
        return factory;
    }
}
