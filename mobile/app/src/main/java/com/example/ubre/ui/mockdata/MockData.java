package com.example.ubre.ui.mockdata;

import com.example.ubre.ui.dtos.ProfileChangeDto;

import java.util.ArrayList;
import java.util.List;

public class MockData {
    public static List<ProfileChangeDto> profileProfileChanges() {
        List<ProfileChangeDto> list = new ArrayList<>();

        ProfileChangeDto a = new ProfileChangeDto();
        a.id = 1L;
        a.userId = 1L;
        a.oldName = "Marko"; a.newName = "Marko";
        a.oldSurname = "Markovic"; a.newSurname = "Markovic";
        a.oldAddress = "Stara 1"; a.newAddress = "Nova 22";
        a.oldPhone = "061111111"; a.newPhone = "062222222";
        a.oldAvatarUrl = ""; a.newAvatarUrl = "";
        list.add(a);

        ProfileChangeDto b = new ProfileChangeDto();
        b.id = 2L;
        b.userId = 3L;
        b.oldName = "Ana"; b.newName = "Ana";
        b.oldSurname = "Ilic"; b.newSurname = "Ilic";
        b.oldAddress = "Bulevar 10"; b.newAddress = "Bulevar 10";
        b.oldPhone = "063333333"; b.newPhone = "063333333";
        b.oldAvatarUrl = "https://example.com/old.png";
        b.newAvatarUrl = "https://example.com/new.png";
        list.add(b);

        return list;
    }
}
