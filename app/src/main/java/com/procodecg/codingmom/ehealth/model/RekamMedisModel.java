package com.procodecg.codingmom.ehealth.model;

import java.util.Date;

/**
 * Created by neo on 11/02/18.
 */

public class RekamMedisModel {
    private int id;
    private java.sql.Date tanggal;
    private String nama_dokter;


    public RekamMedisModel(int id, String tanggal, String nama_dokter) {
        this.id = id;
        this.tanggal = java.sql.Date.valueOf(tanggal);
        this.nama_dokter = nama_dokter;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public java.sql.Date getTanggal() {
        return tanggal;
    }

    public void setTanggal(Date tanggal) {
        this.tanggal = java.sql.Date.valueOf(String.valueOf(tanggal));
    }

    public String getNama_dokter() {
        return nama_dokter;
    }

    public void setNama_dokter(String nama_dokter) {
        this.nama_dokter = nama_dokter;
    }
}
