package view;

import PDI.Pdi;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Optional;

@SuppressWarnings("unused")
public class AULA2Controller {
    @FXML
    Label lbR, lbG, lbB;

    @FXML
    ImageView img1, img2, img3;

    @FXML
    TextField red, green, blue, qtCores, tamBorda, opcImg1, opcImg2;

    @FXML
    Slider limiar;

    @FXML
    ToggleGroup ruidoGrp;

    @FXML
    ComboBox<String> comboCores = new ComboBox<>();

    private int[] areaSelecionada = new int[4];

    private Image imagem1, imagem2, imagem3;

    private File f;

    @FXML
    public void initialize() {
        comboCores.getItems().add("Vermelho");
        comboCores.getItems().add("Verde");
        comboCores.getItems().add("Azul");
    }

    @FXML
    @SuppressWarnings("unused")
    public void abreImagem1() {
        f = selecionarImagem();

        if (f != null) {
            imagem1 = new Image(f.toURI().toString());
            img1.setImage(imagem1);
            img1.setFitWidth(imagem1.getWidth());
            img1.setFitHeight(imagem1.getHeight());

            imagem3 = new Image(f.toURI().toString());
            img3.setImage(imagem3);
            img3.setFitWidth(imagem3.getWidth());
            img3.setFitHeight(imagem3.getHeight());
        }
    }

    @FXML
    public void usarImg3() {
        imagem1 = imagem3;
        img1.setImage(imagem3);
        img1.setFitWidth(imagem3.getWidth());
        img1.setFitHeight(imagem3.getHeight());
    }

    @FXML
    public void reset() {
        imagem3 = imagem1;
        atualizaImg3();
    }

    @FXML
    @SuppressWarnings("unused")
    public void abreImagem2() {
        f = selecionarImagem();

        if (f != null) {
            imagem2 = new Image(f.toURI().toString());
            img2.setImage(imagem2);
            img2.setFitWidth(imagem2.getWidth());
            img2.setFitHeight(imagem2.getHeight());
        }
    }

    private File selecionarImagem() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(
                "Imagens", "*.jpg", "*.JPG", "*.png", "*.PNG", "*.gif", "*.GIF", "*.bmp", "*.BMP"));
        fileChooser.setInitialDirectory(new File("C:\\Users\\Usuário\\Pictures\\Saved Pictures"));

        try {
            return fileChooser.showOpenDialog(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @FXML
    @SuppressWarnings("unused")
    public void rasterImg(MouseEvent evt) {
        ImageView imageView = (ImageView) evt.getTarget();

        if (imageView.getImage() != null) {
            verificaCor(imageView.getImage(), (int) evt.getX(), (int) evt.getY());
        }
    }

    private void verificaCor(Image img, int x, int y) {
        try {
            Color cor = img.getPixelReader().getColor(x, y);
            lbR.setText("R: " + (int) (cor.getRed() * 255));
            lbG.setText("G: " + (int) (cor.getGreen() * 255));
            lbB.setText("B: " + (int) (cor.getBlue() * 255));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    @SuppressWarnings("unused")
    public void salvar() {
        if (imagem3 != null) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(
                    "Imagens", "*.png", "*.PNG"));
            fileChooser.setInitialDirectory(new File("C:\\Users\\Usuário\\Pictures\\Saved Pictures"));

            File file = fileChooser.showSaveDialog(null);
            if (file != null) {
                BufferedImage bImg = SwingFXUtils.fromFXImage(imagem3, null);
                try {
                    ImageIO.write(bImg, "PNG", file);
                    exibeMsg("Salvar imagem", "Imagem salva com sucesso!", Alert.AlertType.CONFIRMATION);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            exibeMsg("", "", Alert.AlertType.ERROR);
        }
    }

    @FXML
    @SuppressWarnings("unused")
    public void limparLabels() {
        lbR.setText("R: ");
        lbG.setText("G: ");
        lbB.setText("B: ");
    }

    private void exibeMsg(String titulo, String cabecalho, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(cabecalho);
        alert.showAndWait();
    }

    private void exibeDialog(String titulo, String texto) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(texto);

        ButtonType cinza = new ButtonType("Cinza");
        ButtonType negativa = new ButtonType("Negativa");
        ButtonType adicionar = new ButtonType("Adicionar");
        ButtonType subtrair = new ButtonType("Subtrair");
        ButtonType cancelar = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(cinza, negativa, adicionar, subtrair, cancelar);

        Optional<ButtonType> result = alert.showAndWait();

        if (result.get() == cinza) {
            imagem3 = Pdi.cinza(imagem1, "0", "0", "0", areaSelecionada);
        } else if (result.get() == negativa) {
            imagem3 = Pdi.negativa(imagem1, areaSelecionada);
        } else if (result.get() == adicionar) {
            imagem3 = Pdi.adicionar(imagem1, imagem2, 50.0, 50.0, areaSelecionada);
        } else if (result.get() == subtrair) {
            imagem3 = Pdi.subtrair(imagem1, imagem2, areaSelecionada);
        }
    }

    @SuppressWarnings("unused")
    public void cinza() {
        if (red.getText().equals(""))
            red.setText("0");
        if (green.getText().equals(""))
            green.setText("0");
        if (blue.getText().equals(""))
            blue.setText("0");
        imagem3 = Pdi.cinza(imagem1, red.getText(), green.getText(), blue.getText(), new int[]{0, Integer.MAX_VALUE, 0, Integer.MAX_VALUE});
        atualizaImg3();
    }

    @SuppressWarnings("unused")
    public void negativar() {
        imagem3 = Pdi.negativa(imagem1, new int[]{0, Integer.MAX_VALUE, 0, Integer.MAX_VALUE});
        atualizaImg3();
    }

    @SuppressWarnings("unused")
    public void limiar() {
        imagem3 = Pdi.limiar(imagem1, limiar.getValue());
        atualizaImg3();
    }

    @SuppressWarnings("unused")
    public void desafio1() {
        imagem3 = Pdi.desafio1(imagem1, 170);
        atualizaImg3();
    }

    public void removeRuidoMedia() {
        RadioButton rb = (RadioButton) ruidoGrp.getSelectedToggle();
        imagem3 = Pdi.removeRuido(imagem1, rb.getText(), true);
        atualizaImg3();
    }

    public void removeRuidoMediana() {
        RadioButton rb = (RadioButton) ruidoGrp.getSelectedToggle();
        imagem3 = Pdi.removeRuido(imagem1, rb.getText(), false);
        atualizaImg3();
    }

    private void atualizaImg3() {
        img3.setImage(imagem3);
        img3.setFitWidth(imagem3.getWidth());
        img3.setFitHeight(imagem3.getHeight());
    }

    @FXML
    public void gerarHistograma(ActionEvent event) {
        try {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("HistogramaModal.fxml"));
            Parent root = loader.load();
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
            stage.setTitle("Histograma");
//            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(((Node) event.getSource()).getScene().getWindow());
            stage.show();
            HistogramaController controller = loader.getController();

            if (imagem1 != null)
                Pdi.geraGrafico(imagem1, controller.hist1);

            if (imagem2 != null)
                Pdi.geraGrafico(imagem2, controller.hist2);

            if (imagem3 != null)
                Pdi.geraGrafico(imagem3, controller.hist3);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void equalizar() {
        imagem3 = Pdi.equalizar(imagem1);
        atualizaImg3();
    }

    @FXML
    public void desafio2() {
        int qtCores;
        try {
            qtCores = Integer.parseInt(this.qtCores.getText());
        } catch (NumberFormatException e) {
            qtCores = 3;
        }
        imagem3 = Pdi.desafio2(imagem1, qtCores);
        atualizaImg3();
    }

    @FXML
    public void criarBorda() {
        try {
            imagem3 = Pdi.criaBorda(imagem1, comboCores.getSelectionModel().getSelectedItem(), Integer.parseInt(tamBorda.getText()));
            atualizaImg3();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void divisaoHorizontal() {
        imagem3 = Pdi.divisaoHorizontal(imagem1);
        atualizaImg3();
    }

    @FXML
    public void circleOrSquare() {
        String texto = Pdi.isSquare(imagem1) ? "É um quadrado" : "É um círculo";
        exibeMsg("Resultado", texto, Alert.AlertType.INFORMATION);
    }

    @FXML
    public void girarEsquerda() {
        imagem3 = Pdi.girar(imagem1, true);
        atualizaImg3();
    }

    @FXML
    public void girarDireita() {
        imagem3 = Pdi.girar(imagem1, false);
        atualizaImg3();
    }

    @FXML
    public void aumentar() {
        imagem3 = Pdi.aumentar(imagem1);
        atualizaImg3();
    }

    @FXML
    public void diminuir() {
        imagem3 = Pdi.diminuir(imagem1);
        atualizaImg3();
    }

    @FXML
    public void adicionar() {
        double opc1 = Double.parseDouble(opcImg1.getText());
        double opc2 = Double.parseDouble(opcImg2.getText());
        if (opc1 + opc2 != 100) {
            exibeMsg("Erro", "A soma das opacidades deve ser igual a 100", Alert.AlertType.ERROR);
            return;
        }
        imagem3 = Pdi.adicionar(imagem1, imagem2, opc1, opc2, new int[]{0, Integer.MAX_VALUE, 0, Integer.MAX_VALUE});
        atualizaImg3();
    }

    @FXML
    public void subtrair() {
        imagem3 = Pdi.subtrair(imagem1, imagem2, new int[]{0, Integer.MAX_VALUE, 0, Integer.MAX_VALUE});
        atualizaImg3();
    }

    @FXML
    public void clicou(MouseEvent event) {
        areaSelecionada[0] = (int) event.getX();
        areaSelecionada[1] = (int) event.getX();
        areaSelecionada[2] = (int) event.getY();
        areaSelecionada[3] = (int) event.getY();
    }

    @FXML
    public void soltou(MouseEvent event) {
        areaSelecionada[0] = areaSelecionada[0] < (int) event.getX() ? areaSelecionada[0] : (int) event.getX();
        areaSelecionada[1] = areaSelecionada[1] > (int) event.getX() ? areaSelecionada[1] : (int) event.getX();
        areaSelecionada[2] = areaSelecionada[2] < (int) event.getY() ? areaSelecionada[2] : (int) event.getY();
        areaSelecionada[3] = areaSelecionada[3] > (int) event.getY() ? areaSelecionada[3] : (int) event.getY();

        exibeDialog("Alterar imagem", "Escolha um filtro");
        atualizaImg3();
    }

    @FXML
    public void identificaRostos() {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        CascadeClassifier faceDetector = new CascadeClassifier("haarcascade_frontalface_alt.xml");
        Mat image = Imgcodecs.imread(f.getAbsolutePath());
        MatOfRect faceDetections = new MatOfRect();
        faceDetector.detectMultiScale(image, faceDetections);
        System.out.println(String.format("Detected %s faces", faceDetections.toArray().length));
        for(Rect rect: faceDetections.toArray()) {
            Imgproc.rectangle(image, new Point(rect.x, rect.y),
                                     new Point(rect.x + rect.width, rect.y + rect.height),
                                     new Scalar(0, 0, 255), 3);
        }
        MatOfByte mtb = new MatOfByte();
        Imgcodecs.imencode(".png", image, mtb);
        imagem3 = new Image(new ByteArrayInputStream(mtb.toArray()));
        atualizaImg3();
    }
}
