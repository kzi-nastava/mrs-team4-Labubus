package com.example.ubre.ui.dtos;

public class BlockUserRequest {
    private String note;

    public BlockUserRequest(String note) {
        this.note = note;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
