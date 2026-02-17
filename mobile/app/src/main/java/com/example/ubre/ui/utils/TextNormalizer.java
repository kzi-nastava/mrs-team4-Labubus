package com.example.ubre.ui.utils;

public class TextNormalizer {

    private TextNormalizer() {
    }

    public static String toLatin(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        StringBuilder sb = new StringBuilder(input.length());
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            switch (c) {
                case 'А': sb.append("A"); break;
                case 'Б': sb.append("B"); break;
                case 'В': sb.append("V"); break;
                case 'Г': sb.append("G"); break;
                case 'Д': sb.append("D"); break;
                case 'Ђ': sb.append("Đ"); break;
                case 'Е': sb.append("E"); break;
                case 'Ж': sb.append("Ž"); break;
                case 'З': sb.append("Z"); break;
                case 'И': sb.append("I"); break;
                case 'Ј': sb.append("J"); break;
                case 'К': sb.append("K"); break;
                case 'Л': sb.append("L"); break;
                case 'Љ': sb.append("Lj"); break;
                case 'М': sb.append("M"); break;
                case 'Н': sb.append("N"); break;
                case 'Њ': sb.append("Nj"); break;
                case 'О': sb.append("O"); break;
                case 'П': sb.append("P"); break;
                case 'Р': sb.append("R"); break;
                case 'С': sb.append("S"); break;
                case 'Т': sb.append("T"); break;
                case 'Ћ': sb.append("Ć"); break;
                case 'У': sb.append("U"); break;
                case 'Ф': sb.append("F"); break;
                case 'Х': sb.append("H"); break;
                case 'Ц': sb.append("C"); break;
                case 'Ч': sb.append("Č"); break;
                case 'Џ': sb.append("Dž"); break;
                case 'Ш': sb.append("Š"); break;
                case 'а': sb.append("a"); break;
                case 'б': sb.append("b"); break;
                case 'в': sb.append("v"); break;
                case 'г': sb.append("g"); break;
                case 'д': sb.append("d"); break;
                case 'ђ': sb.append("đ"); break;
                case 'е': sb.append("e"); break;
                case 'ж': sb.append("ž"); break;
                case 'з': sb.append("z"); break;
                case 'и': sb.append("i"); break;
                case 'ј': sb.append("j"); break;
                case 'к': sb.append("k"); break;
                case 'л': sb.append("l"); break;
                case 'љ': sb.append("lj"); break;
                case 'м': sb.append("m"); break;
                case 'н': sb.append("n"); break;
                case 'њ': sb.append("nj"); break;
                case 'о': sb.append("o"); break;
                case 'п': sb.append("p"); break;
                case 'р': sb.append("r"); break;
                case 'с': sb.append("s"); break;
                case 'т': sb.append("t"); break;
                case 'ћ': sb.append("ć"); break;
                case 'у': sb.append("u"); break;
                case 'ф': sb.append("f"); break;
                case 'х': sb.append("h"); break;
                case 'ц': sb.append("c"); break;
                case 'ч': sb.append("č"); break;
                case 'џ': sb.append("dž"); break;
                case 'ш': sb.append("š"); break;
                default: sb.append(c);
            }
        }
        return sb.toString();
    }
}
