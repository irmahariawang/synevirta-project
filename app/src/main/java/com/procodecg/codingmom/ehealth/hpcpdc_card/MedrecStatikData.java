package com.procodecg.codingmom.ehealth.hpcpdc_card;

/**
 * Created by Atia on 20-Nov-17.
 */

public class MedrecStatikData {
    String golonganDarah,
            alergi,
            riwayatOperasi,
            riwayatRawatRS,
            riwayatPenyakitKronis,
            riwayatPenyakitBawaan,
            faktorResiko;

    MedrecStatikData(String golonganDarah,
                     String alergi,
                     String riwayatOperasi,
                     String riwayatRawatRS,
                     String riwayatPenyakitKronis,
                     String riwayatPenyakitBawaan,
                     String faktorResiko) {
        this.golonganDarah = golonganDarah;
        this.alergi = alergi;
        this.riwayatOperasi = riwayatOperasi;
        this.riwayatRawatRS = riwayatRawatRS;
        this.riwayatPenyakitKronis = riwayatPenyakitKronis;
        this.riwayatPenyakitBawaan = riwayatPenyakitBawaan;
        this.faktorResiko = faktorResiko;
    }
}
