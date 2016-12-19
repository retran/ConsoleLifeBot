package me.retran.consolelifebot.utils;

import static org.junit.Assert.*;

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
}
