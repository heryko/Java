import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class ImageApp extends Application {

    private Image originalImage;
    private Image processedImage;
    private boolean isModified = false;

    private ImageView originalView = new ImageView();
    private ImageView processedView = new ImageView();
    private Button btnLoad, btnSave, btnScale, btnRotateLeft, btnRotateRight, btnExecute;
    private ComboBox<String> operationComboBox;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Przetwarzanie Obrazów");

        VBox headerBox = new VBox(5);
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setPadding(new Insets(10));
        Label titleLabel = new Label("Aplikacja do Obróbki Obrazów");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        Label logoLabel = new Label("Politechnika Wrocławska");
        logoLabel.setTextFill(Color.DARKORANGE);
        headerBox.getChildren().addAll(titleLabel, logoLabel);

        HBox controlPanel = new HBox(10);
        controlPanel.setAlignment(Pos.CENTER);
        controlPanel.setPadding(new Insets(10));

        btnLoad = new Button("Wczytaj obraz");
        btnSave = new Button("Zapisz obraz");
        btnScale = new Button("Skaluj");
        btnRotateLeft = new Button("↺ 90°");
        btnRotateRight = new Button("↻ 90°");

        operationComboBox = new ComboBox<>(FXCollections.observableArrayList("Negatyw", "Progowanie", "Konturowanie"));
        operationComboBox.setPromptText("Wybierz operację");
        operationComboBox.setValue(null);

        btnExecute = new Button("Wykonaj");
        controlPanel.getChildren().addAll(btnLoad, operationComboBox, btnExecute, btnScale, btnRotateLeft, btnRotateRight, btnSave);

        HBox imagePanel = new HBox(20);
        imagePanel.setAlignment(Pos.CENTER);
        imagePanel.setPadding(new Insets(15));

        originalView.setFitWidth(350);
        originalView.setFitHeight(350);
        originalView.setPreserveRatio(true);

        processedView.setFitWidth(350);
        processedView.setFitHeight(350);
        processedView.setPreserveRatio(true);

        VBox boxOriginal = new VBox(5, new Label("Oryginał:"), originalView);
        VBox boxProcessed = new VBox(5, new Label("Po zmianach:"), processedView);
        boxOriginal.setAlignment(Pos.CENTER);
        boxProcessed.setAlignment(Pos.CENTER);
        imagePanel.getChildren().addAll(boxOriginal, boxProcessed);

        HBox footerBox = new HBox();
        footerBox.setAlignment(Pos.CENTER);
        footerBox.setPadding(new Insets(10));
        Label authorLabel = new Label("Autor: Mateusz");
        footerBox.getChildren().add(authorLabel);

        setButtonsState(false);

        btnLoad.setOnAction(e -> loadImage(primaryStage));
        btnSave.setOnAction(e -> openSaveModal(primaryStage));
        btnScale.setOnAction(e -> openScaleModal(primaryStage));

        btnRotateLeft.setOnAction(e -> {
            if (processedImage != null) {
                processedImage = ImageProcessor.rotate(processedImage, -90);
                processedView.setImage(processedImage);
                isModified = true;
            }
        });

        btnRotateRight.setOnAction(e -> {
            if (processedImage != null) {
                processedImage = ImageProcessor.rotate(processedImage, 90);
                processedView.setImage(processedImage);
                isModified = true;
            }
        });

        btnExecute.setOnAction(e -> {
            String selected = operationComboBox.getValue();
            if (selected == null) {
                showToast(primaryStage, "Nie wybrano operacji do wykonania", true);
                return;
            }
            try {
                if (selected.equals("Negatyw")) {
                    processedImage = ImageProcessor.negative(processedImage);
                    showToast(primaryStage, "Negatyw został wygenerowany pomyślnie!", false);
                } else if (selected.equals("Progowanie")) {
                    openThresholdModal(primaryStage);
                    return;
                } else if (selected.equals("Konturowanie")) {
                    processedImage = ImageProcessor.edgeDetection(processedImage);
                    showToast(primaryStage, "Konturowanie zostało przeprowadzone pomyślnie!", false);
                }
                processedView.setImage(processedImage);
                isModified = true;
            } catch (Exception ex) {
                if (selected.equals("Negatyw")) showToast(primaryStage, "Nie udało się wykonać negatywu.", true);
                else if (selected.equals("Konturowanie")) showToast(primaryStage, "Nie udało się wykonać konturowania.", true);
            }
        });

        BorderPane root = new BorderPane();
        root.setTop(headerBox);
        VBox centerBox = new VBox(10, controlPanel, imagePanel);
        root.setCenter(centerBox);
        root.setBottom(footerBox);

        primaryStage.setScene(new Scene(root, 850, 620));
        primaryStage.show();
    }

    private void setButtonsState(boolean active) {
        btnSave.setDisable(!active);
        btnScale.setDisable(!active);
        btnRotateLeft.setDisable(!active);
        btnRotateRight.setDisable(!active);
        btnExecute.setDisable(!active);
        operationComboBox.setDisable(!active);
    }

    private void loadImage(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Wybierz plik obrazu");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Obrazy JPG", "*.jpg"));

        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            if (!selectedFile.getName().toLowerCase().endsWith(".jpg")) {
                showToast(stage, "Niedozwolony format pliku", true);
                return;
            }
            try {
                if (originalImage != null) {
                    originalImage = null;
                    processedImage = null;
                    System.gc();
                }
                originalImage = new Image(selectedFile.toURI().toString());
                processedImage = originalImage;
                originalView.setImage(originalImage);
                processedView.setImage(processedImage);
                isModified = false;
                setButtonsState(true);
                showToast(stage, "Pomyślnie załadowano plik", false);
            } catch (Exception e) {
                showToast(stage, "Nie udało się załadować pliku", true);
            }
        }
    }

    private void openScaleModal(Stage parent) {
        Stage modal = new Stage(StageStyle.UTILITY);
        modal.initModality(Modality.WINDOW_MODAL);
        modal.initOwner(parent);
        modal.setTitle("Skalowanie obrazu");

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(15));
        layout.setAlignment(Pos.CENTER_LEFT);

        TextField txtWidth = new TextField();
        txtWidth.setPromptText("Szerokość (0-3000)");
        Label lblWidthError = new Label();
        lblWidthError.setTextFill(Color.RED);

        TextField txtHeight = new TextField();
        txtHeight.setPromptText("Wysokość (0-3000)");
        Label lblHeightError = new Label();
        lblHeightError.setTextFill(Color.RED);

        txtWidth.textProperty().addListener((obs, oldV, newV) -> {
            if (!newV.matches("\\d*")) txtWidth.setText(newV.replaceAll("[^\\d]", ""));
        });
        txtHeight.textProperty().addListener((obs, oldV, newV) -> {
            if (!newV.matches("\\d*")) txtHeight.setText(newV.replaceAll("[^\\d]", ""));
        });

        Button btnReset = new Button("Przywróć oryginalne wymiary");
        btnReset.setOnAction(e -> {
            txtWidth.setText(String.valueOf((int)originalImage.getWidth()));
            txtHeight.setText(String.valueOf((int)originalImage.getHeight()));
        });

        Button btnConfirm = new Button("Zmień rozmiar");
        Button btnCancel = new Button("Anuluj");
        btnCancel.setOnAction(e -> modal.close());

        btnConfirm.setOnAction(e -> {
            lblWidthError.setText("");
            lblHeightError.setText("");
            boolean valid = true;
            if (txtWidth.getText().isEmpty()) { lblWidthError.setText("Pole jest wymagane"); valid = false; }
            if (txtHeight.getText().isEmpty()) { lblHeightError.setText("Pole jest wymagane"); valid = false; }

            if (valid) {
                int w = Integer.parseInt(txtWidth.getText());
                int h = Integer.parseInt(txtHeight.getText());
                if (w <= 0 || w > 3000 || h <= 0 || h > 3000) {
                    showToast(modal, "Wartości muszą być z przedziału (0-3000)", true);
                    return;
                }
                processedImage = ImageProcessor.scale(processedImage, w, h);
                processedView.setImage(processedImage);
                isModified = true;
                modal.close();
            }
        });

        HBox buttons = new HBox(10, btnConfirm, btnCancel, btnReset);
        layout.getChildren().addAll(new Label("Szerokość:"), txtWidth, lblWidthError, new Label("Wysokość:"), txtHeight, lblHeightError, buttons);
        modal.setScene(new Scene(layout, 350, 250));
        modal.showAndWait();
    }

    private void openThresholdModal(Stage parent) {
        Stage modal = new Stage(StageStyle.UTILITY);
        modal.initModality(Modality.WINDOW_MODAL);
        modal.initOwner(parent);
        modal.setTitle("Ustaw próg");

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(15));

        TextField txtThreshold = new TextField("127");
        txtThreshold.textProperty().addListener((obs, oldV, newV) -> {
            if (!newV.matches("\\d*")) txtThreshold.setText(newV.replaceAll("[^\\d]", ""));
        });

        Button btnOk = new Button("Wykonaj progowanie");
        Button btnCancel = new Button("Anuluj");
        btnCancel.setOnAction(e -> modal.close());

        btnOk.setOnAction(e -> {
            if(txtThreshold.getText().isEmpty()) return;
            int val = Integer.parseInt(txtThreshold.getText());
            if(val < 0 || val > 255) {
                showToast(modal, "Wartość musi być w zakresie 0-255", true);
                return;
            }
            try {
                processedImage = ImageProcessor.threshold(processedImage, val);
                processedView.setImage(processedImage);
                isModified = true;
                showToast(parent, "Progowanie zostało przeprowadzone pomyślnie!", false);
                modal.close();
            } catch (Exception ex) {
                showToast(parent, "Nie udało się wykonac progowania.", true);
                modal.close();
            }
        });

        HBox hb = new HBox(10, btnOk, btnCancel);
        layout.getChildren().addAll(new Label("Wartość progu (0-255):"), txtThreshold, hb);
        modal.setScene(new Scene(layout, 250, 150));
        modal.showAndWait();
    }

    private void openSaveModal(Stage parent) {
        Stage modal = new Stage(StageStyle.UTILITY);
        modal.initModality(Modality.WINDOW_MODAL);
        modal.initOwner(parent);
        modal.setTitle("Zapisz plik");

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(15));

        Label lblAlert = new Label();
        if (!isModified) {
            lblAlert.setText("Na pliku nie zostały wykonane żadne operacje!");
            lblAlert.setTextFill(Color.ORANGE);
            lblAlert.setStyle("-fx-font-weight: bold;");
        }

        TextField txtName = new TextField();
        txtName.setPromptText("Nazwa pliku (3-100 znaków)");
        txtName.textProperty().addListener((obs, oldV, newV) -> {
            if (newV.length() > 100) txtName.setText(oldV);
        });

        Label lblError = new Label();
        lblError.setTextFill(Color.RED);

        Button btnSaveFile = new Button("Zapisz");
        Button btnCancel = new Button("Anuluj");
        btnCancel.setOnAction(e -> modal.close());

        btnSaveFile.setOnAction(e -> {
            String name = txtName.getText().trim();
            if (name.length() < 3) {
                lblError.setText("Wpisz co najmniej 3 znaki");
                return;
            }

            String userHome = System.getProperty("user.home");
            File picturesDir = new File(userHome, "Pictures");
            if(!picturesDir.exists()) picturesDir = new File(userHome, "Obrazy");

            File outputFile = new File(picturesDir, name + ".jpg");
            if (outputFile.exists()) {
                showToast(modal, "Plik " + name + ".jpg już istnieje w systemie. Podaj inną nazwę pliku!", true);
                return;
            }

            try {
                java.awt.image.BufferedImage bImage = SwingFXUtils.fromFXImage(processedImage, null);
                boolean success = ImageIO.write(bImage, "jpg", outputFile);
                if (success) {
                    showToast(parent, "Zapisano obraz w pliku " + name + ".jpg", false);
                    modal.close();
                } else {
                    showToast(modal, "Nie udało się zapisać pliku " + name + ".jpg", true);
                }
            } catch (IOException ex) {
                showToast(modal, "Nie udało się zapisać pliku " + name + ".jpg", true);
            }
        });

        HBox hb = new HBox(10, btnSaveFile, btnCancel);
        layout.getChildren().addAll(lblAlert, new Label("Podaj nazwę nowego pliku (.jpg zostanie dodane):"), txtName, lblError, hb);
        modal.setScene(new Scene(layout, 400, 220));
        modal.showAndWait();
    }

    private void showToast(Stage ownerStage, String message, boolean isError) {
        Stage toastStage = new Stage();
        toastStage.initOwner(ownerStage);
        toastStage.initStyle(StageStyle.TRANSPARENT);

        Label label = new Label(message);
        label.setFont(Font.font("Arial", 13));
        label.setTextFill(Color.WHITE);
        label.setPadding(new Insets(8, 15, 8, 15));

        String backgroundStyle = isError ? "-fx-background-color: #E74C3C;" : "-fx-background-color: #2ECC71;";
        label.setStyle(backgroundStyle + " -fx-background-radius: 15;");

        StackPane root = new StackPane(label);
        root.setStyle("-fx-background-color: transparent;");

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        toastStage.setScene(scene);

        toastStage.setOnShown(e -> {
            toastStage.setX(ownerStage.getX() + ownerStage.getWidth() / 2 - toastStage.getWidth() / 2);
            toastStage.setY(ownerStage.getY() + ownerStage.getHeight() - 100);
        });

        toastStage.show();
        new Thread(() -> {
            try { Thread.sleep(2500); } catch (InterruptedException ex) {}
            javafx.application.Platform.runLater(toastStage::close);
        }).start();
    }
}