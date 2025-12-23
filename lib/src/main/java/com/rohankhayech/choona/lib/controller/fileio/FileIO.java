/*
 * Choona - Guitar Tuner
 * Copyright (C) 2025 Rohan Khayech
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.rohankhayech.choona.lib.controller.fileio;

import android.content.Context;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * The File I/O class handles writing and reading text to/from files.
 *
 * @author Rohan Khayech
 */
public final class FileIO {

    /** The file extension used for saved files. */
    static final String FILE_EXT = ".json";

    /** Private constructor to prevent instantiation. */
    private FileIO() {}

    /**
     * Writes the specified string to the specified file.
     * @param context The current Android system context.
     * @param filepath The path of the file to write to.
     * @param contents The string to write to the file.
     * @throws IOException If an error occurs while writing to the file.
     */
    static void writeToFile(Context context, String filepath, String contents) throws IOException {
        try (FileOutputStream fos = context.openFileOutput(filepath, Context.MODE_PRIVATE)) {
            fos.write(contents.getBytes());
        }
    }

    /**
     * Reads the contents of the specified file.
     * @param context The current Android system context.
     * @param filepath The path of the file to read from.
     * @return A string containing the contents of the file.
     * @throws IOException If an error occurs while reading from the file.
     */
    static String readFromFile(Context context, String filepath) throws IOException {
        FileInputStream fis = context.openFileInput(filepath);
        InputStreamReader inputStreamReader = new InputStreamReader(fis);
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(inputStreamReader)) {
            String line = reader.readLine();
            while (line != null) {
                stringBuilder.append(line).append('\n');
                line = reader.readLine();
            }
        }
        return stringBuilder.toString();
    }
}
