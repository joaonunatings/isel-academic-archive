package sample;

public class Sample {

    private final int number;

    private final int[][] data;

    public Sample(int number, int[][] data) {
        this.number = number;
        this.data = data;
    }

    public int getNumber() {
        return number;
    }

    public int[][] getData() {
        return data;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Number: ");
        sb.append(number);
        sb.append("\nData:\n");
        for(int y = 0; y < 28; y++) {
            for(int x = 0; x < 28; x++) {
                if (data[y][x] != 0) sb.append("#");
                else sb.append(" ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
