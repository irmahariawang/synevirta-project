package com.procodecg.codingmom.ehealth.hpcpdc_card;

/**
 * Created by irma on 24/01/18.
 */

public class HPCData {
    String holderRole, nik; // cert
    String nama, sip; // hpdata

    public HPCData(String holderRole,
                   String nik,
                   String nama,
                   String sip) {
        this.holderRole = holderRole;
        this.nama = nama;
        this.nik = nik;
        this.sip = sip;
    }
}
