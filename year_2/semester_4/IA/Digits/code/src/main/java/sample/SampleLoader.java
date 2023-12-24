package sample;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class SampleLoader {

    private static final int SAMPLE_WIDTH = 28;
    private static final int SAMPLE_HEIGHT = 28;

    public static List<Sample> load(String filePath) throws IOException {
        var file = new File(filePath);
        var samples = new LinkedList<Sample>();
        var reader = new BufferedReader(new FileReader(file));
        String line;

        while((line = reader.readLine()) != null) {
            samples.add(getSample(line));
        }
        reader.close();
        return samples;
    }

    public static List<Sample> load(String filePath, int limit) throws IOException {
        var file = new File(filePath);
        var samples = new LinkedList<Sample>();
        var reader = new BufferedReader(new FileReader(file));
        String line;

        while((line = reader.readLine()) != null && samples.size() < limit) {
            samples.add(getSample(line));
        }
        reader.close();
        return samples;
    }

    private static Sample getSample(String line) {
        var parts = line.split(",");
        var number = Integer.parseInt(parts[0]);
        var data = new int[SAMPLE_HEIGHT][SAMPLE_WIDTH];
        for(int y = 0; y < 28; y++) {
            for (int x = 0; x < 28; x++) {
                data[y][x] = Integer.parseInt(parts[y * 28 + x + 1]);
            }
        }
        return new Sample(number, data);
    }
}
