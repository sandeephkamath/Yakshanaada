package com.lovoctech.yakshanaada.repository;

import com.lovoctech.yakshanaada.R;
import com.lovoctech.yakshanaada.model.Tanpur;

import java.util.Arrays;
import java.util.List;

public class HindusthaniRepository {

    private static Tanpur C = new Tanpur(R.raw.cshrutipatanpura, R.raw.cshrutimatanpura, "C", "C", "C");
    private static Tanpur C_SHARP = new Tanpur(R.raw.csharpshrutipatanpura, R.raw.csharpshrutimatanpura, "C#", "C#", "C#");
    private static Tanpur D = new Tanpur(R.raw.dshrutipatanpura, R.raw.dshrutimatanpura, "D", "D", "D");
    private static Tanpur D_SHARP = new Tanpur(R.raw.dsharpshrutipatanpura, R.raw.dsharpshrutimatanpura, "D#", "D#", "D#");
    private static Tanpur E = new Tanpur(R.raw.eshrutipatanpura, R.raw.eshrutimatanpura, "E", "E", "E");
    private static Tanpur F = new Tanpur(R.raw.fshrutipatanpura, R.raw.fshrutimatanpura, "F", "F", "F");
    private static Tanpur F_SHARP = new Tanpur(R.raw.fsharpshrutipatanpura, R.raw.fsharpshrutimatanpura, "F#", "F#", "F#");
    private static Tanpur G = new Tanpur(R.raw.gshrutipatanpura, R.raw.gshrutimatanpura, "G", "G", "G");
    private static Tanpur G_SHARP = new Tanpur(R.raw.gsharpshrutipatanpura, R.raw.gsharpshrutimatanpura, "G#", "G#", "G#");
    private static Tanpur A = new Tanpur(R.raw.ashrutipatanpura, R.raw.ashrutimatanpura, "A", "A", "A");
    private static Tanpur A_SHARP = new Tanpur(R.raw.asharpshrutipatanpura, R.raw.asharpshrutimatanpura, "A#", "A#", "A#");
    private static Tanpur B = new Tanpur(R.raw.bshrutipatanpura, R.raw.bshrutimatanpura, "B", "B", "B");


    public static List<Tanpur> getAll() {
        return Arrays.asList(C, C_SHARP, D, D_SHARP, E, F, F_SHARP, G, G_SHARP, A, A_SHARP, B);
    }

}
