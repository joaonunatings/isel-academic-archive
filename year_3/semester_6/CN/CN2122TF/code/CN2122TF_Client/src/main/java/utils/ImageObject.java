package utils;

public class ImageObject {

    final String NAME;
    final double CERTAINTY_LEVEL;

    public ImageObject(String NAME, double CERTAINTY_LEVEL) {
        this.NAME = NAME;
        this.CERTAINTY_LEVEL = CERTAINTY_LEVEL;
    }

    @Override
    public String toString() {
        return "ImageObject{" +
                "NAME='" + NAME + '\'' +
                ", CERTAINTY_LEVEL=" + CERTAINTY_LEVEL +
                '}';
    }
}
