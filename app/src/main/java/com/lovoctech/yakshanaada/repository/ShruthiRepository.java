package com.lovoctech.yakshanaada.repository;

import com.lovoctech.yakshanaada.R;
import com.lovoctech.yakshanaada.model.Shruthi;

import java.util.Arrays;
import java.util.List;

public class ShruthiRepository {

    public static final Shruthi BILI_1 = new Shruthi(
            R.raw.bili_11111,
            "bili_11111",
            "ಬಿಳಿ 1",
            "C",
            R.mipmap.yakshanaada
    );

    private static final Shruthi BILI_7 = new Shruthi(
            R.raw.bili_7,
            "bili_7",
            "ಬಿಳಿ 7",
            "B",
            R.mipmap.yakshanaada
    );

    public static final Shruthi BILI_2 = new Shruthi(
            R.raw.d,
            "D",
            "ಬಿಳಿ 2",
            "D",
            R.mipmap.yakshanaada
    );

    public static final Shruthi KAPPU_1 = new Shruthi(
            R.raw.kappu_1,
            "kappu_1",
            "ಕಪ್ಪು 1",
            "C#",
            R.mipmap.yakshanaada
    );

    public static final Shruthi KAPPU_5 = new Shruthi(
            R.raw.kappu_5,
            "kappu_5",
            "ಕಪ್ಪು 5",
            "G#",
            R.mipmap.yakshanaada
    );

    public static final Shruthi KAPPU_3 = new Shruthi(
            R.raw.kappu_3,
            "kappu_3",
            "ಕಪ್ಪು 3",
            "E#",
            R.mipmap.yakshanaada
    );

    public static final Shruthi KAPPU_2 = new Shruthi(
            R.raw.ds,
            "DS",
            "ಕಪ್ಪು 2",
            "D#",
            R.mipmap.yakshanaada
    );

    private static final Shruthi KAPPU_4 = new Shruthi(
            R.raw.kappu_4,
            "kappu_4",
            "ಕಪ್ಪು 4",
            "F#",
            R.mipmap.yakshanaada
    );

    public static final Shruthi BILI_3 = new Shruthi(
            R.raw.e,
            "E",
            "ಬಿಳಿ 3",
            "E",
            R.mipmap.yakshanaada
    );

    public static final Shruthi BILI_4 = new Shruthi(
            R.raw.f,
            "F",
            "ಬಿಳಿ 4",
            "F",
            R.mipmap.yakshanaada
    );

    public static final Shruthi BILI_5 = new Shruthi(
            R.raw.g,
            "G",
            "ಬಿಳಿ 5",
            "G",
            R.mipmap.yakshanaada
    );

    private static final Shruthi BILI_6 = new Shruthi(
            R.raw.bili_6,
            "bili_6",
            "ಬಿಳಿ 6",
            "A",
            R.mipmap.yakshanaada
    );

    public static List<Shruthi> getShruthis() {
        return Arrays.asList(BILI_1, KAPPU_1, BILI_2, KAPPU_2, BILI_3, BILI_4, KAPPU_3, BILI_5, KAPPU_4, BILI_6, KAPPU_5, BILI_7);
    }

}
