/*
 * Copyright (c) 2023 Rohan Khayech
 */

package com.rohankhayech.choona.controller.fileio;

import static org.junit.Assert.assertEquals;

import com.rohankhayech.music.Tuning;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

public class TuningFileIOTest {

    @Test
    public void testParseTunings() {
        Set<Tuning> tunings = TuningFileIO.parseTunings("{\"tunings\":[{\"name\":\"Standard\",\"strings\":\"E4 B3 G3 D3 A2 E2\"},{\"name\":\"Drop D\",\"strings\":\"E4 B3 G3 D3 A2 D2\"},{\"strings\":\"G3 D3 A2 E2\"}]}");
        Set<Tuning> expected = new HashSet<>();
        expected.add(Tuning.STANDARD);
        expected.add(Tuning.DROP_D);
        expected.add(Tuning.fromString("G3 D3 A2 E2"));
        assertEquals(expected, tunings);
    }

    @Test
    public void testEncodeTunings() {
        Set<Tuning> tunings = new HashSet<>();
        tunings.add(Tuning.STANDARD);
        tunings.add(Tuning.DROP_D);
        tunings.add(Tuning.fromString("G3 D3 A2 E2"));
        String json = TuningFileIO.encodeTunings(tunings);
        assertEquals("{\"tunings\":[{\"strings\":\"E4 B3 G3 D3 A2 E2\",\"name\":\"Standard\"},{\"strings\":\"E4 B3 G3 D3 A2 D2\",\"name\":\"Drop D\"},{\"strings\":\"G3 D3 A2 E2\"}]}", json);
    }
}