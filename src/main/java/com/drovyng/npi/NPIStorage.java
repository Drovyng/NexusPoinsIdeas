package com.drovyng.npi;

import com.drovyng.npi.panel.NPIManager;
import com.google.common.base.Charsets;

import java.io.*;

public class NPIStorage {
    public static final NPIStorage Instance = new NPIStorage();
    private NPIStorage(){}

    public File panelsFile;

    public void Load(){
        panelsFile = new File(NPI.Instance.getDataFolder(), "palens.noedit");

        if (!panelsFile.exists())
            NPI.Instance.saveResource("palens.noedit", false);

        try {
            final FileInputStream stream = new FileInputStream(panelsFile);

            final var input = new BufferedReader(new InputStreamReader(stream, Charsets.UTF_8));

            try {
                String line;

                while ((line = input.readLine()) != null) {
                    NPIManager.ParseLine(line);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                input.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    public void Save(){
        Writer writer = null;
        try {
            writer = new OutputStreamWriter(new FileOutputStream(panelsFile), Charsets.UTF_8);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
        finally {
            try {
                writer.write(NPIManager.SaveString());
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                try {
                    writer.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
