package com.eokwingster.karkinoscore.core.gametext;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class GameTextMapper {
    private static final int MAX_STROKES = 3;
    private static final int STROKE_COUNT = 12;
    private static final int PUNCTUATION_COUNT = 32;
    private static final int NUMBER_COUNT = 10;
    private static final Pattern LETTER_PATTERN;
    private static final Pattern NUMBER_PATTERN;
    private static final Pattern PUNCTUATION_PATTERN;
    private static final Pattern SPACE_PATTERN;
    private static final Pattern SPLIT_PATTERN;
    public static final List<Integer> STROKE_CODE;
    public static final List<Integer> SPECIAL_CHARACTERS_CODE;
    private static final int LEN1 = 4;
    private static final int LEN2 = 8;
    private static final int MAX_GLYPHS = 3;
    private static final List<int[]> GLYPHS;
    private static final BigInteger BASE;

    static {
        LETTER_PATTERN = Pattern.compile("[A-Za-z]+");
        PUNCTUATION_PATTERN = Pattern.compile("[!\"#$%&'()*+,\\-./:;<=>?@\\[\\]\\\\^_`{|}~]\\s*");
        SPACE_PATTERN = Pattern.compile("\\s+");
        NUMBER_PATTERN = Pattern.compile("\\d");
        SPLIT_PATTERN = Pattern.compile(String.join("|",
                LETTER_PATTERN.pattern(),
                NUMBER_PATTERN.pattern(),
                SPACE_PATTERN.pattern(),
                PUNCTUATION_PATTERN.pattern()
        ));
        STROKE_CODE = IntStream.range(0, STROKE_COUNT).boxed().toList();
        SPECIAL_CHARACTERS_CODE = IntStream.range(0, STROKE_COUNT + NUMBER_COUNT + PUNCTUATION_COUNT + 1).map(x -> x + STROKE_COUNT).boxed().toList();
        List<int[]> all = new ArrayList<>(218);
        int[] pool = new int[STROKE_COUNT];
        for (int i = 0; i < STROKE_COUNT; i++) pool[i] = STROKE_CODE.get(i);
        for (int k = 1; k <= MAX_STROKES; k++) combine(pool, 0, k, new int[k], all);
        if (all.size() != 298) throw new IllegalStateException("glyph count != 298");
        GLYPHS = Collections.unmodifiableList(all);
        BASE = BigInteger.valueOf(GLYPHS.size());
    }

    private static void combine(int[] pool, int start, int k, int[] cur, List<int[]> out) {
        if (k == 0) { out.add(Arrays.copyOf(cur, cur.length)); return; }
        for (int i = start; i <= pool.length - k; i++) {
            cur[cur.length - k] = pool[i];
            combine(pool, i + 1, k - 1, cur, out);
        }
    }

    private static List<String> split(String text) {
        List<String> out = new ArrayList<>();
        Matcher m = SPLIT_PATTERN.matcher(text);
        while (m.find()) {
            String token = m.group();
            if (token.matches("[.,!?;:'\"()\\[\\]{}\\-]\\s*")) {
                out.add(token.replaceAll("\\s+$", ""));
            } else {
                out.add(token);
            }
        }
        return out;
    }

    private static int[][] mapToken(String token) {
        if (NUMBER_PATTERN.matcher(token).matches()) return new int [][]{{SPECIAL_CHARACTERS_CODE.get(Integer.parseInt(token))}};
        String punctuations = "!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~";
        if (PUNCTUATION_PATTERN.matcher(token).matches()) return new int [][]{{punctuations.indexOf(token) + STROKE_COUNT + NUMBER_COUNT}};
        if (SPACE_PATTERN.matcher(token).matches()) return new int[][]{{STROKE_COUNT + NUMBER_COUNT + PUNCTUATION_COUNT}};

        String norm = token.toLowerCase();
        byte[] hash = sha256(norm);
        BigInteger num = new BigInteger(1, hash);

        int L = decideGlyphCount(norm.length());
        int[][] out = new int[L][];
        for (int i = 0; i < L; i++) {
            int idx = num.mod(BASE).intValue();
            out[i] = GLYPHS.get(idx);
            num = num.divide(BASE);
        }
        return out;
    }

    private static byte[] sha256(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return md.digest(s.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) { throw new RuntimeException(e); }
    }

    private static int decideGlyphCount(int tokenLen) {
        if (tokenLen <= LEN1) return 1;
        if (tokenLen <= LEN2) return 2;
        return MAX_GLYPHS;
    }

    public static List<int[][]> mapText(String text) {
        List<String> tokens = split(text);
        return tokens.stream().map(GameTextMapper::mapToken).toList();
    }

    public static boolean isSpace(int code) {
        return code == STROKE_COUNT + NUMBER_COUNT + PUNCTUATION_COUNT;
    }
}


