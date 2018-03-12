package com.procodecg.codingmom.ehealth.hpcpdc_card;

import java.util.Date;

/**
 * Created by Atia on 20-Nov-17.
 */

public class MedrecDinamikData {
    static final int MEDREC_DINAMIK_SIZE = 5;

    String idPuskesmas, pemberiRujukan, keluhanUtama, riwayatPenyakitSekarang,
            riwayatPenyakitDahulu, riwayatPenyakitKeluarga, kepala, thorax, abdomen, genitalia,
            extremitas, kulit, neurologi, laboratorium, radiologi, diagnosisKerja, diagnosisBanding,
            icd10, resep, catatanResep, tindakan;
    Date tglPeriksa;
    int noIndex, systole, diastole, nadi, respirasi, tinggi, berat;
    float suhu;
    byte poli, kesadaran, statusLabRadio, statusResep, repetisiResep, adVitam, adFunctionam, adSanationam;


    public MedrecDinamikData(int noIndex, Date tglPeriksa, String idPuskesmas, byte poli,
                             String pemberiRujukan, int systole, int diastole, float suhu, int nadi,
                             int respirasi, String keluhanUtama, String riwayatPenyakitSekarang,
                             String riwayatPenyakitDahulu, String riwayatPenyakitKeluarga, int tinggi,
                             int berat, byte kesadaran, String kepala, String thorax, String abdomen,
                             String genitalia, String extremitas, String kulit, String neurologi,
                             String laboratorium, String radiologi, byte statusLabRadio,
                             String diagnosisKerja, String diagnosisBanding, String icd10, String resep,
                             String catatanResep, byte statusResep, byte repetisiResep, String tindakan,
                             byte adVitam, byte adFunctionam, byte adSanationam) {

        this.noIndex = noIndex;
        this.tglPeriksa = tglPeriksa;
        this.idPuskesmas = idPuskesmas;
        this.poli = poli;
        this.pemberiRujukan = pemberiRujukan;
        this.systole = systole;
        this.diastole = diastole;
        this.suhu = suhu;
        this.nadi = nadi;
        this.respirasi = respirasi;
        this.keluhanUtama = keluhanUtama;
        this.riwayatPenyakitSekarang = riwayatPenyakitSekarang;
        this.riwayatPenyakitDahulu = riwayatPenyakitDahulu;
        this.riwayatPenyakitKeluarga = riwayatPenyakitKeluarga;
        this.tinggi = tinggi;
        this.berat = berat;
        this.kesadaran = kesadaran;
        this.kepala = kepala;
        this.thorax = thorax;
        this.abdomen = abdomen;
        this.genitalia = genitalia;
        this.extremitas = extremitas;
        this.kulit = kulit;
        this.neurologi = neurologi;
        this.laboratorium = laboratorium;
        this.radiologi = radiologi;
        this.statusLabRadio = statusLabRadio;
        this.diagnosisKerja = diagnosisKerja;
        this.diagnosisBanding = diagnosisBanding;
        this.icd10 = icd10;
        this.resep = resep;
        this.catatanResep = catatanResep;
        this.statusResep = statusResep;
        this.repetisiResep = repetisiResep;
        this.tindakan = tindakan;
        this.adVitam = adVitam;
        this.adFunctionam = adFunctionam;
        this.adSanationam = adSanationam;
    }

    public static int writeIndex;
}
