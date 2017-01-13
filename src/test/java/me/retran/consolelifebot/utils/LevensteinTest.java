package me.retran.consolelifebot.utils;

import static org.junit.Assert.*;

import javax.validation.constraints.AssertTrue;

import org.junit.Test;

public class LevensteinTest {
    @Test
    public void testDistance() {
        String a = "Sonic The Hedgehog";

        String b1 = "Sonic";
        String b2 = "Mario";
        String b3 = "Sonic Hedgehog";

        int e1 = Levenstein.distance(a, b1);
        int e2 = Levenstein.distance(a, b2);
        int e3 = Levenstein.distance(a, b3);

        assertTrue(e1 < e2 && e1 > e3);
        assertTrue(e2 > e1 && e2 > e3);
        assertTrue(e3 < e1 && e3 < e2);
    }

    @Test
    public void testComplex() {
        String a = "NBA Jam";

        String b1 = "nba";
        String b2 = "nba jam";
        String b3 = "jam";
        String b4 = "naa jam";
        String b5 = "nba aam";
        String b6 = "nba am";
        String b7 = "na jam";
        String b8 = "mario";
        String b9 = "sonic";

        int e1 = Levenstein.distance(a.toLowerCase(), b1.toLowerCase());
        int e2 = Levenstein.distance(a.toLowerCase(), b2.toLowerCase());
        int e3 = Levenstein.distance(a.toLowerCase(), b3.toLowerCase());
        int e4 = Levenstein.distance(a.toLowerCase(), b4.toLowerCase());
        int e5 = Levenstein.distance(a.toLowerCase(), b5.toLowerCase());
        int e6 = Levenstein.distance(a.toLowerCase(), b6.toLowerCase());
        int e7 = Levenstein.distance(a.toLowerCase(), b7.toLowerCase());
        int e8 = Levenstein.distance(a.toLowerCase(), b8.toLowerCase());
        int e9 = Levenstein.distance(a.toLowerCase(), b9.toLowerCase());

        assertTrue(e1 > e2 && e3 > e2);
        assertTrue(e4 < e1 && e5 < e1 && e6 < e1 && e7 < e1);
        assertTrue(e9 > e1 && e9 > e2 && e9 > e4 && e9 > e5 && e9 > e6 && e9 > e7);
        assertTrue(e8 > e1 && e8 > e2 && e8 > e4 && e8 > e5 && e8 > e6 && e8 > e7);
    }
}
