import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.ImageView;

public class ImageProcessor {

    public static Image scale(Image source, int width, int height) {
        ImageView imageView = new ImageView(source);
        imageView.setFitWidth(width);
        imageView.setFitHeight(height);
        imageView.setSmooth(true);
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        return imageView.snapshot(params, null);
    }

    public static Image rotate(Image source, double angle) {
        ImageView imageView = new ImageView(source);
        imageView.setRotate(angle);
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        return imageView.snapshot(params, null);
    }

    public static Image negative(Image source) {
        int w = (int) source.getWidth();
        int h = (int) source.getHeight();
        WritableImage output = new WritableImage(w, h);
        PixelReader reader = source.getPixelReader();
        PixelWriter writer = output.getPixelWriter();

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                Color c = reader.getColor(x, y);
                writer.setColor(x, y, new Color(1.0 - c.getRed(), 1.0 - c.getGreen(), 1.0 - c.getBlue(), c.getOpacity()));
            }
        }
        return output;
    }

    public static Image threshold(Image source, int thresholdValue) {
        int w = (int) source.getWidth();
        int h = (int) source.getHeight();
        WritableImage output = new WritableImage(w, h);
        PixelReader reader = source.getPixelReader();
        PixelWriter writer = output.getPixelWriter();

        double normThreshold = thresholdValue / 255.0;

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                Color c = reader.getColor(x, y);
                double gray = 0.2126 * c.getRed() + 0.7152 * c.getGreen() + 0.0722 * c.getBlue();
                if (gray >= normThreshold) {
                    writer.setColor(x, y, Color.WHITE);
                } else {
                    writer.setColor(x, y, Color.BLACK);
                }
            }
        }
        return output;
    }

    public static Image edgeDetection(Image source) {
        int w = (int) source.getWidth();
        int h = (int) source.getHeight();
        WritableImage output = new WritableImage(w, h);
        PixelReader reader = source.getPixelReader();
        PixelWriter writer = output.getPixelWriter();

        for (int y = 1; y < h - 1; y++) {
            for (int x = 1; x < w - 1; x++) {
                double val = getGray(reader.getColor(x, y)) * 4
                        - getGray(reader.getColor(x - 1, y))
                        - getGray(reader.getColor(x + 1, y))
                        - getGray(reader.getColor(x, y - 1))
                        - getGray(reader.getColor(x, y + 1));

                val = Math.min(1.0, Math.max(0.0, val));
                writer.setColor(x, y, new Color(val, val, val, 1.0));
            }
        }
        return output;
    }

    private static double getGray(Color c) {
        return 0.2126 * c.getRed() + 0.7152 * c.getGreen() + 0.0722 * c.getBlue();
    }
}