package it.finanze.sanita.fse2.ms.iniclient.client.impl.shield;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum XDSFaultTypeEnum {

    SECURITY_TOKEN_UNAVAILABLE(47, "Politiche di accesso"),
    INVALID_SECURITY_TOKEN(48, "Politiche di accesso"),
    MESSAGE_EXPIRED(49, "Politiche di accesso"),
    FAILED_AUTHENTICATION(50, "Politiche di accesso"),
    FAILED_CHECK(51, "Politiche di accesso"),

    XDS_REGISTRY_ERROR_QUERY(52, "Query"),
    XDS_UNKNOWN_STORED_QUERY(53, "Query"),
    XDS_STORED_QUERY_MISSING_PARAM(54, "Query"),
    XDS_REGISTRY_ERROR_QUERY_WARNING(55, "Query"),

    XDS_DOCUMENT_UNIQUE_ID_ERROR(56, "Retrieve"),
    XDS_REPOSITORY_ERROR(57, "Retrieve"),
    XDS_UNKNOWN_COMMUNITY(58, "Retrieve"),
    XDS_REPOSITORY_ERROR_WARNING(59, "Retrieve"),

    NOTIFY_ERROR(60, "Notification"),
    NOTIFY_CONSENT_ERROR(61, "Notification"),

    XDS_REGISTRY_ERROR_REGISTER(62, "Register"),
    XDS_REGISTRY_ERROR_REGISTER_WARNING(63, "Register"),
    XDS_METADATA_VERSION_ERROR(64, "Register"),

    UNRESOLVED_REFERENCE_EXCEPTION(65, "Delete"),

    XDS_REGISTRY_ERROR_INDEX_TRANSFER_WARNING(66, "IndexTransfer"),
    XDS_REGISTRY_ERROR_INDEX_TRANSFER(67, "IndexTransfer"),

    QUERY_ERROR(68, "Consent Management"),
    REGISTER_CONSENT_SET_ERROR(69, "Consent Management"),

    DISCLOSURE_ERROR(70, "Disclosure Management"),

    CONNECTION_FAILED(71, "Comunicazione tra nodi");

    private final Integer table;
    private final String title;
}
