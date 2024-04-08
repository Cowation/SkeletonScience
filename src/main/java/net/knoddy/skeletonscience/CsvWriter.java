package net.knoddy.skeletonscience;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class CsvWriter {
    private File csvFile;
    private String[] header;

    public CsvWriter(String filePath, String[] header) {
        this.csvFile = new File(filePath);
        this.header = header;
        initialize();
    }

    private void initialize() {
        // Check if the file already exists
        if (!csvFile.exists()) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(csvFile, false))) {
                // Write the header to the file
                writer.println(String.join(",", header));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void appendLine(String[] data) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(csvFile, true))) {
            // Convert the data array to a comma-separated string and append it to the file
            writer.println(String.join(",", data));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

