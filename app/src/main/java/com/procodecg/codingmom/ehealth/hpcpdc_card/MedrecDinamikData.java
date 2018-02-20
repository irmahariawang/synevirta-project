package com.procodecg.codingmom.ehealth.hpcpdc_card;

/**
 * Created by Atia on 20-Nov-17.
 */

public class MedrecDinamikData {
    static final int MEDREC_DINAMIK_SIZE = 5;

    public String timestamp, timezone, idPuskesmas, poli, rujukan, keluhan, anamnesa, riwayatPenyakitDahulu,
        riwayatPenyakitBawaan, pemeriksaanFisik, lain, tinggi, beratbadan, systole, diastole, nadi,
        suhu, respirasi, kesadaran, labFlag, expertiseLab, catatanLab, terapi, resep, catatanResep,
        eksekusiResepFlag, repetisiResep, prognosa, kdICDStatusDiagnosa1, kdICDStatusDiagnosa2, kdICDStatusDiagnosa3,
            kdICDStatusDiagnosa4, kdICDStatusDiagnosa5, kdICDStatusDiagnosa6, kdICDStatusDiagnosa7,
            kdICDStatusDiagnosa8, kdICDStatusDiagnosa9, kdICDStatusDiagnosa10, kdICD1, kdICD2, kdICD3,
            kdICD4, kdICD5, kdICD6, kdICD7, kdICD8, kdICD9, kdICD10, nikPengubahDS;

    public static int writeIndex;

    MedrecDinamikData(String timestamp, String timezone, String idPuskesmas, String poli, String rujukan, String keluhan, String anamnesa, String riwayatPenyakitDahulu,
                      String riwayatPenyakitBawaan, String pemeriksaanFisik, String lain, String tinggi, String beratbadan, String systole, String diastole, String nadi,
                      String suhu, String respirasi, String kesadaran, String labFlag, String expertiseLab, String catatanLab, String terapi, String resep, String catatanResep,
                      String eksekusiResepFlag, String repetisiResep, String prognosa, String kdICDStatusDiagnosa1, String kdICDStatusDiagnosa2, String kdICDStatusDiagnosa3,
                      String kdICDStatusDiagnosa4, String kdICDStatusDiagnosa5, String kdICDStatusDiagnosa6, String kdICDStatusDiagnosa7,
                      String kdICDStatusDiagnosa8, String kdICDStatusDiagnosa9, String kdICDStatusDiagnosa10, String kdICD1, String kdICD2, String kdICD3,
                      String kdICD4, String kdICD5, String kdICD6, String kdICD7, String kdICD8, String kdICD9, String kdICD10, String nikPengubahDS) {
        this.timestamp = timestamp;
        this.timezone = timezone;
        this.idPuskesmas = idPuskesmas;
        this.poli = poli;
        this.rujukan = rujukan;
        this.keluhan = keluhan;
        this.anamnesa = anamnesa;
        this.riwayatPenyakitDahulu = riwayatPenyakitDahulu;
        this.riwayatPenyakitBawaan = riwayatPenyakitBawaan;
        this.pemeriksaanFisik = pemeriksaanFisik;
        this.lain = lain;
        this.tinggi = tinggi;
        this.beratbadan = beratbadan;
        this.systole = systole;
        this.diastole = diastole;
        this.nadi = nadi;
        this.suhu = suhu;
        this.respirasi = respirasi;
        this.kesadaran = kesadaran;
        this.labFlag = labFlag;
        this.expertiseLab = expertiseLab;
        this.catatanLab = catatanLab;
        this.terapi = terapi;
        this.resep = resep;
        this.catatanResep = catatanResep;
        this.eksekusiResepFlag = eksekusiResepFlag;
        this.repetisiResep = repetisiResep;
        this.prognosa = prognosa;
        this.kdICDStatusDiagnosa1 = kdICDStatusDiagnosa1;
        this.kdICDStatusDiagnosa2 = kdICDStatusDiagnosa2;
        this.kdICDStatusDiagnosa3 = kdICDStatusDiagnosa3;
        this.kdICDStatusDiagnosa4 = kdICDStatusDiagnosa4;
        this.kdICDStatusDiagnosa5 = kdICDStatusDiagnosa5;
        this.kdICDStatusDiagnosa6 = kdICDStatusDiagnosa6;
        this.kdICDStatusDiagnosa7 = kdICDStatusDiagnosa7;
        this.kdICDStatusDiagnosa8 = kdICDStatusDiagnosa8;
        this.kdICDStatusDiagnosa9 = kdICDStatusDiagnosa9;
        this.kdICDStatusDiagnosa10 = kdICDStatusDiagnosa10;
        this.kdICD1 = kdICD1;
        this.kdICD2 = kdICD2;
        this.kdICD3 = kdICD3;
        this.kdICD4 = kdICD4;
        this.kdICD5 = kdICD5;
        this.kdICD6 = kdICD6;
        this.kdICD7 = kdICD7;
        this.kdICD8 = kdICD8;
        this.kdICD9 = kdICD9;
        this.kdICD10 = kdICD10;
        this.nikPengubahDS = nikPengubahDS;
    }

    MedrecDinamikData(String poli, String tinggi, String keluhan) {
        this.poli = poli;
        this.tinggi = tinggi;
        this.keluhan = keluhan;
    }
}
