package com.drovyng.npi;

import com.drovyng.npi.panel.NPIManager;
import com.google.common.base.Charsets;

import java.io.*;
import java.nio.file.Files;

public class NPIStorage {
    public static final NPIStorage Instance = new NPIStorage();

    private NPIStorage() {}

    public File panelsFile;

    public void Load() {
        final File configFolder = NPI.Instance.getDataFolder();
        if (!configFolder.exists()) {
            configFolder.mkdirs();
        }
        panelsFile = new File(NPI.Instance.getDataFolder(), "palens.noedit");

        if (!panelsFile.exists()) {
            try {
                panelsFile.createNewFile();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        try {
            try {
                for (var line : Files.readAllLines(panelsFile.toPath())) {
                    NPIManager.ParseLine(line);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void Save() {
        try {
            Writer writer = new OutputStreamWriter(new FileOutputStream(panelsFile), Charsets.UTF_8);
            try {
                writer.write(NPIManager.SaveString());
            } finally {
                writer.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}