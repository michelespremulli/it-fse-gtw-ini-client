/* SPDX-License-Identifier: AGPL-3.0-or-later */
package it.finanze.sanita.fse2.ms.iniclient.enums.guided;

import it.finanze.sanita.fse2.ms.iniclient.dto.iti42.GuidedPublicationRequest.Code;

/**
 * Guided-only snapshot of the dispatcher vocabularies (2026-11-09).
 * Kept separate from the older enums used by legacy endpoints. GuidedChoices exposes
 * code - display strings; enum names are internal (ACCESS_UPDATE emits "ACCESS UPDATE").
 */
public final class GuidedVocabulary {
    private GuidedVocabulary() {}

    public interface Selection {
        String code();
        String display();

        default Code classification() {
            return new Code(code(), display());
        }
    }

    public enum Role implements Selection {
        AAS("AAS", "Personale di assistenza ad alta specializzazione"),
        APR("APR", "Medico Medicina Generale Pediatra di Libera Scelta"),
        PSS("PSS", "Professionista del sociale"), INF("INF", "Personale infermieristico"),
        FAR("FAR", "Farmacista"), DSA("DSA", "Direttore sanitario"), DAM("DAM", "Direttore amministrativo"),
        OAM("OAM", "Operatore amministrativo"), ASS("ASS", "Assistito"), TUT("TUT", "Tutore"),
        ING("ING", "Informal giver (Delegato volontario)"), GEN("GEN", "Genitore"), NOR("NOR", "Nodo regionale"),
        DRS("DRS", "Dirigente sanitario"), RSA("RSA", "Medico RSA"), MRP("MRP", "Medico Rete di Patologia"),
        INI("INI", "Infrastruttura Nazionale per l’Interoperabilità"),
        OGC("OGC", "Operatore per la gestione dei consensi. Ruolo per la gestione dei consensi"),
        OPI("OPI", "Operatore di informativa. Ruolo dell'operatore che può inserire informative regionali e moduli per l'acquisizione dei consensi e delle revoche"),
        MDS("MDS", "Ruolo del Ministero della Salute per la gestione del DGC"),
        GTW("GTW", "Ruolo per il Gateway EDS"), DAP("DAP", "Dispositivo Azienda Produttrice");
        private final String code, display;
        Role(String code, String display) { this.code = code; this.display = display; }
        public String code() { return code; }
        public String display() { return display; }
    }

    public enum Purpose implements Selection {
        TREATMENT("TREATMENT", "Trattamento di cura ordinario"), EMERGENCY("EMERGENCY", "Trattamento in emergenza"),
        PUBEMERGENCY("PUBEMERGENCY", "Trattamento per la salvaguardia di un terzo o della collettività"),
        SYSADMIN("SYSADMIN", "Trasferimento del FSE e Avvio del processo di catena dell’oscuramento."),
        PERSONAL("PERSONAL", "Consultazione del FSE"), UPDATE("UPDATE", "Invalidamento e aggiornamento di un documento"),
        ACCESS_UPDATE("ACCESS UPDATE", "Aggiornamento di un documento e della politica di oscuramento P99"),
        CONSENT("CONSENT", "Comunicazione valori consensi"), ADMINISTRATIVE("ADMINISTRATIVE", "Operazioni amministrative");
        private final String code, display;
        Purpose(String code, String display) { this.code = code; this.display = display; }
        public String code() { return code; }
        public String display() { return display; }
    }

    public enum Organization implements Selection {
        REGIONE_PIEMONTE("010", "Regione Piemonte"), REGIONE_VALLE_AOSTA("020", "Regione Valle d'Aosta"),
        REGIONE_LOMBARDIA("030", "Regione Lombardia"), REGIONE_BOLZANO("041", "P.A. Bolzano"),
        REGIONE_TRENTO("042", "P.A. Trento"), REGIONE_VENETO("050", "Regione Veneto"),
        REGIONE_FRIULI_VENEZIA_GIULIA("060", "Regione Friuli Venezia Giulia"), REGIONE_LIGURIA("070", "Regione Liguria"),
        REGIONE_EMILIA_ROMAGNA("080", "Regione Emilia-Romagna"), REGIONE_TOSCANA("090", "Regione Toscana"),
        REGIONE_UMBRIA("100", "Regione Umbria"), REGIONE_MARCHE("110", "Regione Marche"),
        REGIONE_LAZIO("120", "Regione Lazio"), REGIONE_ABRUZZO("130", "Regione Abruzzo"),
        REGIONE_MOLISE("140", "Regione Molise"), REGIONE_CAMPANIA("150", "Regione Campania"),
        REGIONE_PUGLIA("160", "Regione Puglia"), REGIONE_BASILICATA("170", "Regione Basilicata"),
        REGIONE_CALABRIA("180", "Regione Calabria"), REGIONE_SICILIA("190", "Regione Sicilia"),
        REGIONE_SARDEGNA("200", "Regione Sardegna"), INI("000", "INI"), SISTEMA_TS("970", "Sistema TS"),
        SASN("001", "SASN"), MDS("999", "MDS"), GTW("950", "GTW");
        private final String code, display;
        Organization(String code, String display) { this.code = code; this.display = display; }
        public String code() { return code; }
        public String display() { return display; }
    }

    public enum Confidentiality implements Selection {
        NORMAL("N", "Normal"), RESTRICTED("R", "Restricted"), VERY_RESTRICTED("V", "Very Restricted");
        private final String code, display;
        Confidentiality(String code, String display) { this.code = code; this.display = display; }
        public String code() { return code; }
        public String display() { return display; }
    }

    public enum DocumentClass implements Selection {
        WOR("WOR", "Documento di workflow"), REF("REF", "Referto"),
        LDO("LDO", "Lettera di dimissione sia ospedaliera, sia non ospedaliera"), RIC("RIC", "Richiesta"),
        SUM("SUM", "Sommario"), TAC("TAC", "Taccuino"), PRS("PRS", "Prescrizione"), PRE("PRE", "Prestazioni"),
        ESE("ESE", "Esenzione"), PDC("PDC", "Piano di cura"), VAC("VAC", "Vaccino"), CER("CER", "Certificato per DGC"),
        VRB("VRB", "Verbale"), CON("CON", "Documento di consenso"), CNT("CNT", "Documento di controllo"),
        CRT("CRT", "Certificato Amministrativo Generico"), LET("LET", "Lettera"), PRO("PRO", "Promemoria"),
        COL("COL", "Collezione documentale");
        private final String code, display;
        DocumentClass(String code, String display) { this.code = code; this.display = display; }
        public String code() { return code; }
        public String display() { return display; }
    }

    public enum Format implements Selection {
        TXT("TXT", "TXT"), PDF("PDF", "PDF"), SCAN("ScanDocument", "Scansione Documento Cartaceo"),
        FHIR_TACCUINO("FHIRDocumentTaccuino", "Documento on-demand contenente i dati di Taccuino"),
        EROGATO_SISTEMATS("SistemaTS-Prestazione", "Erogato Sistema TS"),
        EROGATO_SISTEMATS_FARMACEUTICA("2.16.840.1.113883.2.9.10.1.13.1.1", "Erogato SistemaTS farmaceutica"),
        EROGATO_SISTEMATS_SPECIALISTICA("2.16.840.1.113883.2.9.10.1.13.1.2", "Erogato SistemaTS specialistica"),
        PRESCRIZIONE_SISTEMATS("SistemaTS-Prescrizione", "Prescrizione Sistema TS"),
        PRESCRIZIONE_FARMACEUTICA_SISTEMATS("2.16.840.1.113883.2.9.10.1.2.1", "Prescrizione farmaceutica Sistema TS"),
        PRESCRIZIONE_SPECIALISTICA_SISTEMATS("2.16.840.1.113883.2.9.10.1.2.2", "Prescrizione specialistica Sistema TS"),
        ESENZIONE_REDDITO_SISTEMATS("SistemaTS-Esenzione", "Esenzione da reddito Sistema TS"),
        PRESCRIZIONE("2.16.840.1.113883.2.9.10.1.2", "Prescrizione"),
        REFERTO_LABORATORIO("2.16.840.1.113883.2.9.10.1.1", "Referto di Laboratorio"),
        PROFILO_SANITARIO_SINTETICO("2.16.840.1.113883.2.9.10.1.4.1.1", "Profilo Sanitario Sintetico"),
        LETTERA_DIMISSIONE_OSPEDALIERA("2.16.840.1.113883.2.9.10.1.5", "Lettera di Dimissione Ospedaliera"),
        REFERTO_RADIOLOGIA("2.16.840.1.113883.2.9.10.1.7.1", "Referto di Radiologia"),
        PIANO_TERAPEUTICO("2.16.840.1.113883.2.9.4.3.14", "Piano Terapeutico"),
        SCHEDA_SINGOLA_VACCINAZIONE("2.16.840.1.113883.2.9.10.1.11.1.1", "Scheda della singola Vaccinazione"),
        CERTIFICATO_VACCINALE("2.16.840.1.113883.2.9.10.1.11.1.2", "Certificato Vaccinale"),
        VERBALE_PRONTO_SOCCORSO("2.16.840.1.113883.2.9.10.1.6.1", "Verbale di Pronto Soccorso"),
        REFERTO_SPECIALISTICA_AMBULATORIALE("2.16.840.1.113883.2.9.10.1.9.1", "Referto di Specialistica Ambulatoriale"),
        DOCUMENTO_GENERICO("2.16.840.1.113883.2.9.10.1.12.1", "Documento generico"),
        REFERTO_DI_ANATOMIA_PATOLOGICA("2.16.840.1.113883.2.9.10.1.8.1", "Referto di Anatomia Patologica");
        private final String code, display;
        Format(String code, String display) { this.code = code; this.display = display; }
        public String code() { return code; }
        public String display() { return display; }
    }

    public enum Facility implements Selection {
        Ospedale("Ospedale"), Prevenzione("Prevenzione"), Territorio("Territorio"), SistemaTS("SistemaTS"),
        Cittadino("Cittadino"), MdsPN_DGC("MdsPN-DGC");
        private final String code;
        Facility(String code) { this.code = code; }
        public String code() { return code; }
        public String display() { return code; }
    }

    public enum ClinicalActivity implements Selection {
        PHR("PHR", "Personal Health Record Update"), CON("CON", "Consulto"), DIS("DIS", "Discharge"),
        ERP("ERP", "Erogazione Prestazione Prenotata"), Sistema_TS("SistemaTS", "Documenti sistema TS"),
        INI("INI", "Documenti INI"), PN_DGC("PN-DGC", "Documenti PN-DGC"), OBS("OBS", "Documento stato di salute");
        private final String code, display;
        ClinicalActivity(String code, String display) { this.code = code; this.display = display; }
        public String code() { return code; }
        public String display() { return display; }
    }

    public enum DocumentType implements Selection {
        CODE_57833_6("57833-6", "Prescrizione farmaceutica"),
        CODE_60591_5("60591-5", "Profilo Sanitario Sintetico"),
        CODE_11502_2("11502-2", "Referto di Laboratorio"),
        CODE_57829_4("57829-4", "Prescrizione per prodotto o apparecchiature mediche"),
        CODE_34105_7("34105-7", "Lettera di dimissione ospedaliera"),
        CODE_18842_5("18842-5", "Lettera di dimissione non ospedaliera"),
        CODE_59258_4("59258-4", "Verbale di pronto soccorso"), CODE_68604_8("68604-8", "Referto di radiologia"),
        CODE_11526_1("11526-1", "Referto di anatomia patologica"), CODE_59284_0("59284-0", "Documento dei consensi"),
        CODE_104531_9("104531-9", "Certificato di malattia"),
        CODE_57832_8("57832-8", "Prescrizione diagnostica o specialistica"), CODE_60593_1("60593-1", "Erogazione farmaceutica"),
        CODE_11488_4("11488-4", "Referto specialistico"), CODE_57827_8("57827-8", "Documento di esenzione"),
        CODE_81223_0("81223-0", "Erogazione specialistica"), CODE_18776_5("18776-5", "Piano terapeutico"),
        CODE_97500_3("97500-3", "Certificazione verde Covid-19 (Digital Green Certificate)6"),
        CODE_87273_9("87273-9", "Scheda di singola vaccinazione"), CODE_82593_5("82593-5", "Certificato vaccinale"),
        CODE_97499_8("97499-8", "Certificato di guarigione da Covid-19"),
        CODE_55750_4("55750-4", "Resoconto relativo alla sicurezza del paziente"),
        CODE_68814_3("68814-3", "Bilanci di salute pediatrici"),
        CODE_103140_0("103140-0", "Personal health attachment (documento generico inserito dal paziente in caso di taccuino)"),
        CODE_102033_8("102033-8", "Pathology network list (documento proveniente da reti di patologie)"),
        CODE_103144_2("103144-2", "Medical equipment dispensed.brief"),
        CODE_103145_9("103145-9", "Medical equipment dispensed.extended"),
        CODE_103146_7("103146-7", "Specialist care dispensed.brief"),
        CODE_103147_5("103147-5", "Specialist care dispensed.extended"),
        CODE_101136_0("101136-0", "Lettera di fine trattamento"), CODE_101134_5("101134-5", "Promemoria di appuntamento"),
        CODE_101133_7("101133-7", "Consenso alla donazione di organo e/o tessuto"), CODE_100971_1("100971-1", "Cartella clinica"),
        CODE_53576_5("53576-5", "Documento contenente dati Taccuino, Bundle di tipo collection"),
        CODE_101881_1("101881-1", "Tessera portatori di impianto"),
        CODE_108276_7("108276-7", "Lettera di invito per screening o altri percorsi di prevenzione"),
        CODE_111490_9("111490-9", "Lettera di invito per vaccinazione"),
        CODE_112062_5("112062-5", "Relazione di Telemonitoraggio"), CODE_75496_0("75496-0", "Referto di Televisita"),
        CODE_85208_7("85208-7", "Referto di Teleconsulto"), CODE_112063_3("112063-3", "Referto di Teleassistenza"),
        CODE_111827_2("111827-2", "Report settimanale di telemonitoraggio");
        private final String code, display;
        DocumentType(String code, String display) { this.code = code; this.display = display; }
        public String code() { return code; }
        public String display() { return display; }
    }
}
