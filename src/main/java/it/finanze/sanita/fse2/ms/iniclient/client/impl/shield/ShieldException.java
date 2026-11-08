package it.finanze.sanita.fse2.ms.iniclient.client.impl.shield;

import lombok.Getter;

@Getter
public class ShieldException extends RuntimeException {

    private final XDSFaultEnum fault;

    public ShieldException(XDSFaultEnum fault, String message) {
        super(message);
        this.fault = fault;
    }
}