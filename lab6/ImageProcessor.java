import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;

public class ImageProcessor {

    public static Image scale(Image source, int width, int height) {
        if (width <= 0 || height <= 0) return source;

        WritableImage output = new WritableImage(width, height);
        PixelReader reader = source.getPixelReader();
        PixelWriter writer = output.getPixelWriter();

        double sourceWidth = source.getWidth();
        double sourceHeight = source.getHeight();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int srcX = (int) Math.min(sourceWidth - 1, Math.floor(x * (sourceWidth / width)));
                int srcY = (int) Math.min(sourceHeight - 1, Math.floor(y * (sourceHeight / height)));
                writer.setColor(x, y, reader.getColor(srcX, srcY));
            }
        }
        return output;
    }

    public static Image rotate(Image source, double angle) {
        int w = (int) source.getWidth();
        int h = (int) source.getHeight();

        int newW = (Math.abs(angle) == 90 || Math.abs(angle) == 270) ? h : w;
        int newH = (Math.abs(angle) == 90 || Math.abs(angle) == 270) ? w : h;

        WritableImage output = new WritableImage(newW, newH);
        PixelReader reader = source.getPixelReader();
        PixelWriter writer = output.getPixelWriter();

        Rotate r = Transform.rotate(angle, w / 2.0, h / 2.0);

        for (int y = 0; y < newH; y++) {
            for (int x = 0; x < newW; x++) {
                javafx.geometry.Point2D srcPoint;
                if (angle == 90) {
                    srcPoint = new javafx.geometry.Point2D(y, h - 1 - x);
                } else if (angle == -90 || angle == 270) {
                    srcPoint = new javafx.geometry.Point2D(w - 1 - y, x);
                } else {
                    srcPoint = new javafx.geometry.Point2D(w - 1 - x, h - 1 - y);
                }

                int sx = (int) srcPoint.getX();
                int sy = (int) srcPoint.getY();

                if (sx >= 0 && sx < w && sy >= 0 && sy < h) {
                    writer.setColor(x, y, reader.getColor(sx, sy));
                } else {
                    writer.setColor(x, y, Color.BLACK);
                }
            }
        }
        return output;
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
                writer.setColor(x, y, new Color(1.0 - c.getRed(), 1.0 - c.getGreen(), 1.0 - c.getBlue(), 1.0));
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

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                if (x == 0 || y == 0 || x == w - 1 || y == h - 1) {
                    writer.setColor(x, y, Color.BLACK);
                    continue;
                }

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