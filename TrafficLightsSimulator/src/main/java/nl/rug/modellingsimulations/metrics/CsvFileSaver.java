package nl.rug.modellingsimulations.metrics;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CsvFileSaver implements MetricsStepResultSaver {
    private final int STEPS_PER_SAVE = 100;
    private int stepCounter = 0;
    private boolean firstCharacterWritten = false;
    private BufferedWriter writer;
    private StringBuffer buffer;

    public CsvFileSaver(String identifier) {
        try {
            LocalDateTime dateTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss");

            this.writer = new BufferedWriter(new FileWriter(
                    identifier + "_" + dateTime.format(formatter) + ".csv",
                    true
            ));

            this.buffer = new StringBuffer();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    @Override
    public void addData(String data) {
        if (firstCharacterWritten) {
            buffer.append(',');
        }
        buffer.append(data);
        firstCharacterWritten = true;
    }

    @Override
    public void save() {
        stepCounter += 1;
        buffer.append('\n');
        firstCharacterWritten = false;
        if (stepCounter >= STEPS_PER_SAVE) {
            try {
                writer.write(buffer.toString());
                writer.flush();
                buffer = new StringBuffer();
                stepCounter = 0;
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(0);
            }
        }
    }
}
