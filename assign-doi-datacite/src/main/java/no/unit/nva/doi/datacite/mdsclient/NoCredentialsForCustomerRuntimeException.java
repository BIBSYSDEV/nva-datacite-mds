package no.unit.nva.doi.datacite.mdsclient;

import no.unit.nva.doi.datacite.clients.exception.ClientException;
import no.unit.nva.doi.datacite.clients.exception.ClientRuntimeException;

public class NoCredentialsForCustomerRuntimeException extends ClientRuntimeException {

    public NoCredentialsForCustomerRuntimeException() {
        super();
    }

    public NoCredentialsForCustomerRuntimeException(ClientException e) {
        super(e);
    }
}
