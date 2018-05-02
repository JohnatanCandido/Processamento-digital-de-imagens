package PDI;

import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.util.*;

public class Pdi {

    public static Image cinza(Image img, String red, String green, String blue, int[] areaSelecionada) {
        int w = (int) img.getWidth();
        int h = (int) img.getHeight();

        WritableImage wi = new WritableImage(w, h);
        PixelReader pr = img.getPixelReader();
        PixelWriter pw = wi.getPixelWriter();

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                Color cor = pr.getColor(i, j);
                Color corNova;

                if (i >= areaSelecionada[0] && i <= areaSelecionada[1] && j >= areaSelecionada[2] && j <= areaSelecionada[3]) {
                    double valor;

                    if (red.equals("0") && green.equals("0") && blue.equals("0")) {
                        valor = (cor.getRed() + cor.getGreen() + cor.getBlue()) / 3;
                    } else {
                        double oldRed = Double.parseDouble(red);
                        double oldGreen = Double.parseDouble(green);
                        double oldBlue = Double.parseDouble(blue);

                        double total = oldRed + oldGreen + oldBlue;

                        double vml = (cor.getRed() * oldRed) / total;
                        double vrd = (cor.getGreen() * oldGreen) / total;
                        double azl = (cor.getBlue() * oldBlue) / total;

                        valor = (vml + vrd + azl) / 3;
                    }

                    corNova = new Color(valor, valor, valor, cor.getOpacity());
                } else {
                    corNova = cor;
                }
                pw.setColor(i, j, corNova);
            }
        }

        return wi;
    }

    public static Image negativa(Image img, int[] areaSelecionada) {
        int w = (int) img.getWidth();
        int h = (int) img.getHeight();

        WritableImage wi = new WritableImage(w, h);
        PixelReader pr = img.getPixelReader();
        PixelWriter pw = wi.getPixelWriter();

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                if (i >= areaSelecionada[0] && i <= areaSelecionada[1] && j >= areaSelecionada[2] && j <= areaSelecionada[3]) {
                    Color cor = pr.getColor(i, j);
                    Color corNova = new Color(1 - cor.getRed(), 1 - cor.getGreen(), 1 - cor.getBlue(), cor.getOpacity());
                    pw.setColor(i, j, corNova);
                } else {
                    pw.setColor(i, j, pr.getColor(i, j));
                }
            }
        }

        return wi;
    }

    public static Image limiar(Image img, double limiar) {
        int w = (int) img.getWidth();
        int h = (int) img.getHeight();

        WritableImage wi = new WritableImage(w, h);
        PixelReader pr = img.getPixelReader();
        PixelWriter pw = wi.getPixelWriter();

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                Color cor = pr.getColor(i, j);

                Color corNova;
                double media = (cor.getRed() + cor.getGreen() + cor.getBlue()) / 3;
                if (media > limiar)
                    corNova = new Color(1, 1, 1, cor.getOpacity());
                else
                    corNova = new Color(0, 0, 0, cor.getOpacity());
                pw.setColor(i, j, corNova);
            }
        }

        return wi;
    }

    public static Image desafio1(Image img, double limiar) {
        int w = (int) img.getWidth();
        int h = (int) img.getHeight();

        WritableImage wi = new WritableImage(w, h);
        PixelReader pr = img.getPixelReader();
        PixelWriter pw = wi.getPixelWriter();

        for (int i = 0; i < w / 3; i++) {
            for (int j = 0; j < h; j++) {
                Color cor = pr.getColor(i, j);
                double valor = (cor.getRed() + cor.getGreen() + cor.getBlue()) / 3;
                Color corNova = new Color(valor, valor, valor, cor.getOpacity());
                pw.setColor(i, j, corNova);
            }
        }

        for (int i = w / 3; i < w / 3 * 2; i++) {
            for (int j = 0; j < h; j++) {
                Color cor = pr.getColor(i, j);
                Color corNova = new Color(1 - cor.getRed(), 1 - cor.getGreen(), 1 - cor.getBlue(), cor.getOpacity());
                pw.setColor(i, j, corNova);
            }
        }

        for (int i = w / 3 * 2; i < w; i++) {
            for (int j = 0; j < h; j++) {
                Color cor = pr.getColor(i, j);

                Color corNova;
                if (cor.getRed() > (limiar / 255))
                    corNova = new Color(1, 1, 1, cor.getOpacity());
                else
                    corNova = new Color(0, 0, 0, cor.getOpacity());
                pw.setColor(i, j, corNova);
            }
        }

        return wi;
    }

    public static Image removeRuido(Image img, String tipo, boolean isMedia) {
        int w = (int) img.getWidth();
        int h = (int) img.getHeight();

        WritableImage wi = new WritableImage(w, h);
        PixelReader pr = img.getPixelReader();
        PixelWriter pw = wi.getPixelWriter();

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                Map<String, List<Double>> cores = new HashMap<>();
                cores.put("Red", new ArrayList<>());
                cores.put("Green", new ArrayList<>());
                cores.put("Blue", new ArrayList<>());

                if (tipo.equals("Diagonais") || tipo.equals("3x3")) {
                    getDiagonal(pr, i, j, w, h, cores);
                }

                if (tipo.equals("Cruz") || tipo.equals("3x3")) {
                    getCruz(pr, i, j, w, h, cores);
                }

                processaCores(pw, pr.getColor(i, j), i, j, cores, isMedia);
            }
        }

        return wi;
    }

    private static void getDiagonal(PixelReader pr, int i, int j, int width, int height, Map<String, List<Double>> cores) {
        if (i > 0 && j > 0) {
            cores.get("Red").add(pr.getColor(i - 1, j - 1).getRed());
            cores.get("Green").add(pr.getColor(i - 1, j - 1).getGreen());
            cores.get("Blue").add(pr.getColor(i - 1, j - 1).getBlue());
        }

        if (i > 0 && j < height - 1) {
            cores.get("Red").add(pr.getColor(i - 1, j + 1).getRed());
            cores.get("Green").add(pr.getColor(i - 1, j + 1).getGreen());
            cores.get("Blue").add(pr.getColor(i - 1, j + 1).getBlue());
        }

        if (i < width - 1 && j > 0) {
            cores.get("Red").add(pr.getColor(i + 1, j - 1).getRed());
            cores.get("Green").add(pr.getColor(i + 1, j - 1).getGreen());
            cores.get("Blue").add(pr.getColor(i + 1, j - 1).getBlue());
        }

        if (i < width - 1 && j < height - 1) {
            cores.get("Red").add(pr.getColor(i + 1, j + 1).getRed());
            cores.get("Green").add(pr.getColor(i + 1, j + 1).getGreen());
            cores.get("Blue").add(pr.getColor(i + 1, j + 1).getBlue());
        }
    }

    private static void getCruz(PixelReader pr, int i, int j, int width, int height, Map<String, List<Double>> cores) {
        if (i > 0) {
            cores.get("Red").add(pr.getColor(i - 1, j).getRed());
            cores.get("Green").add(pr.getColor(i - 1, j).getGreen());
            cores.get("Blue").add(pr.getColor(i - 1, j).getBlue());
        }

        if (j < height - 1) {
            cores.get("Red").add(pr.getColor(i, j + 1).getRed());
            cores.get("Green").add(pr.getColor(i, j + 1).getGreen());
            cores.get("Blue").add(pr.getColor(i, j + 1).getBlue());
        }

        if (i < width - 1) {
            cores.get("Red").add(pr.getColor(i + 1, j).getRed());
            cores.get("Green").add(pr.getColor(i + 1, j).getGreen());
            cores.get("Blue").add(pr.getColor(i + 1, j).getBlue());
        }

        if (j > 0) {
            cores.get("Red").add(pr.getColor(i, j - 1).getRed());
            cores.get("Green").add(pr.getColor(i, j - 1).getGreen());
            cores.get("Blue").add(pr.getColor(i, j - 1).getBlue());
        }
    }

    private static void processaCores(PixelWriter pw, Color cor, int i, int j, Map<String, List<Double>> cores, boolean isMedia) {
        Double red = 0D;
        Double green = 0D;
        Double blue = 0D;

        if (isMedia) {
            for (int index = 0; index < cores.get("Red").size(); index++) {
                red += cores.get("Red").get(index);
                green += cores.get("Green").get(index);
                blue += cores.get("Blue").get(index);
            }

            red /= cores.get("Red").size();
            green /= cores.get("Green").size();
            blue /= cores.get("Blue").size();


        } else {
            cores.get("Red").sort(Comparator.naturalOrder());
            cores.get("Green").sort(Comparator.naturalOrder());
            cores.get("Blue").sort(Comparator.naturalOrder());

            red = cores.get("Red").get(Math.round(cores.get("Red").size() / 2));
            green = cores.get("Green").get(Math.round(cores.get("Red").size() / 2));
            blue = cores.get("Blue").get(Math.round(cores.get("Red").size() / 2));
        }

        pw.setColor(i, j, new Color(red, green, blue, cor.getOpacity()));
    }

    private static int[] histograma(Image img, int canal) {
        int[] qt = new int[256];
        PixelReader pr = img.getPixelReader();
        int w = (int) img.getWidth();
        int h = (int) img.getHeight();

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                switch (canal) {
                    case 1:
                        qt[(int) (pr.getColor(i, j).getRed() * 255)]++;
                        break;
                    case 2:
                        qt[(int) (pr.getColor(i, j).getGreen() * 255)]++;
                        break;
                    case 3:
                        qt[(int) (pr.getColor(i, j).getBlue() * 255)]++;
                        break;
                    default:
                        qt[(int) (pr.getColor(i, j).getRed() * 255)]++;
                        qt[(int) (pr.getColor(i, j).getGreen() * 255)]++;
                        qt[(int) (pr.getColor(i, j).getBlue() * 255)]++;
                        break;
                }
            }
        }
        return qt;
    }

    @SuppressWarnings("unchecked")
    public static void geraGrafico(Image img, BarChart<String, Number> grafico) {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Canal");
        yAxis.setLabel("valor");
        XYChart.Series vlrR = new XYChart.Series();
        vlrR.setName("R");
        XYChart.Series vlrG = new XYChart.Series();
        vlrG.setName("G");
        XYChart.Series vlrB = new XYChart.Series();
        vlrB.setName("B");
        int[] histR = histograma(img, 1);
        for (int i = 0; i < histR.length; i++) {
            vlrR.getData().add(new XYChart.Data(i + "", histR[i]));
        }
        int[] histG = histograma(img, 2);
        for (int i = 0; i < histG.length; i++) {
            vlrG.getData().add(new XYChart.Data(i + "", histG[i]));
        }
        int[] histB = histograma(img, 3);
        for (int i = 0; i < histB.length; i++) {
            vlrB.getData().add(new XYChart.Data(i + "", histB[i]));
        }
        grafico.getData().add(vlrR);
        grafico.getData().add(vlrG);
        grafico.getData().add(vlrB);
    }

    public static Image equalizar(Image img) {
        int w = (int) img.getWidth();
        int h = (int) img.getHeight();

        WritableImage wi = new WritableImage(w, h);
        PixelReader pr = img.getPixelReader();
        PixelWriter pw = wi.getPixelWriter();

        int[] histAcR = histogramaAcumulado(histograma(img, 1));
        int[] histAcG = histogramaAcumulado(histograma(img, 2));
        int[] histAcB = histogramaAcumulado(histograma(img, 3));

        double n = w * h;

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                Color cor = pr.getColor(i, j);
                double r = ((254.0 / n) * histAcR[new Double(cor.getRed() * 255).intValue()]) / 255;
                double g = ((254.0 / n) * histAcG[new Double(cor.getGreen() * 255).intValue()]) / 255;
                double b = ((254.0 / n) * histAcB[new Double(cor.getBlue() * 255).intValue()]) / 255;

                Color c = new Color(r, g, b, cor.getOpacity());
                pw.setColor(i, j, c);
            }
        }

        return wi;
    }

    private static int[] histogramaAcumulado(int[] hist) {
        int[] retorno = new int[hist.length];
        int vl = hist[0];
        for (int i = 0; i < hist.length - 1; i++) {
            retorno[i] = vl;
            vl += hist[i + 1];
        }
        return retorno;
    }

    public static Image desafio2(Image img, int qtCores) {
        int w = (int) img.getWidth();
        int h = (int) img.getHeight();
        PixelReader pr = img.getPixelReader();
        WritableImage wi = (WritableImage) cinza(img, "0", "0", "0", new int[]{0, w - 1, 0, h - 1});
        PixelWriter pw = wi.getPixelWriter();
        int[] hist = histograma(img, 4);

        Map<String, Integer> params = getParamsSegmentacao(hist, qtCores);

        int[] cores = getCores(hist, qtCores, params);
        Color[] novasCores = new Color[qtCores];
        int[][] matriz = new int[w][h];

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                Color oldColor = pr.getColor(i, j);
                double cor = getCor(cores, (int) (oldColor.getRed() * 255), params);
                Color newColor = new Color(cor / 255, cor / 255, cor / 255, 1);
                pw.setColor(i, j, newColor);
                matriz[i][j] = (int) cor;
            }
        }

        for (int i = 20; i < w; i++) {
            for (int j = 20; j < h; j++) {
                for (int k = 0; k < qtCores; k++) {
                    if (matriz[i][j] == cores[k] && novasCores[k] == null) {
                        novasCores[k] = pr.getColor(i, j);
                        break;
                    }
                }
            }
        }
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                for (int k = 0; k < qtCores; k++) {
                    if (matriz[i][j] == cores[k]) {
                        pw.setColor(i, j, novasCores[k]);
                        break;
                    }
                }
            }
        }

        return removeRuido(wi, "3x3", false);
    }

    private static int[] getCores(int[] hist, int qtCores, Map<String, Integer> params) {
        int[] cores = new int[qtCores];
        for (int i = 0; i < qtCores; i++) {
            int cor = 0;
            for (int j = (params.get("inicio") + (params.get("tamanhoCorte") * i));
                 j < (params.get("inicio") + (params.get("tamanhoCorte") * (i + 1)));
                 j++) {

                if (hist[cor] < hist[j]) {
                    cor = j;
                }
            }
            cores[i] = cor;
        }
        return cores;
    }

    private static Map<String, Integer> getParamsSegmentacao(int[] hist, int qtCores) {
        Map<String, Integer> params = new HashMap<>();

        int tamanho = hist.length;
        int inicio = -1;
        int fim = -1;
        for (int i = 0; i < tamanho; i++) {
            if (hist[i] != 0 && inicio == -1)
                inicio = i;
            if (hist[tamanho - 1 - i] != 0 && fim == -1)
                fim = tamanho - 1 - i;
            if (inicio != -1 && fim != -1)
                break;
        }

        params.put("tamanhoCorte", (fim - inicio) / qtCores);
        params.put("inicio", inicio);
        return params;
    }

    private static double getCor(int[] cores, int cor, Map<String, Integer> params) {
        int inicio = params.get("inicio");
        int tamCorte = params.get("tamanhoCorte");

        for (int i = 0; i < cores.length; i++) {
            if ((inicio + (tamCorte * i)) <= cor && cor <= (inicio + (tamCorte * (i + 1)))) {
                return cores[i];
            }
            if (i == 0 && cor < (inicio + (tamCorte * i))) {
                return cores[i];
            }
            if (i == cores.length - 1 && cor > (inicio + (tamCorte * (i + 1)))) {
                return cores[i];
            }
        }
        return 0;
    }

    public static Image criaBorda(Image img, String cor, int tamanho) {
        int w = (int) img.getWidth();
        int h = (int) img.getHeight();

        WritableImage wi = new WritableImage(w, h);
        PixelReader pr = img.getPixelReader();
        PixelWriter pw = wi.getPixelWriter();

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                if (i < tamanho || j < tamanho || i + tamanho >= w || j + tamanho >= h) {
                    pw.setColor(i, j, getColorByName(cor));
                } else {
                    pw.setColor(i, j, pr.getColor(i, j));
                }
            }
        }
        return wi;
    }

    private static Color getColorByName(String nome) {
        switch (nome) {
            case "Vermelho":
                return new Color(1, 0, 0, 1);
            case "Verde":
                return new Color(0, 1, 0, 1);
            case "Azul":
                return new Color(0, 0, 1, 1);
        }
        return new Color(0, 0, 0, 0);
    }

    public static Image divisaoHorizontal(Image img) {
        int w = (int) img.getWidth();
        int h = (int) img.getHeight();

        int meio = h / 2;

        WritableImage wi = new WritableImage(w, h);
        PixelReader pr = img.getPixelReader();
        PixelWriter pw = wi.getPixelWriter();

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                Color cor = pr.getColor(i, j);
                Color corNova;
                if (j < meio) {
                    corNova = new Color(1 - cor.getRed(), 1 - cor.getGreen(), 1 - cor.getBlue(), cor.getOpacity());
                } else {
                    double valor = (cor.getRed() + cor.getGreen() + cor.getBlue()) / 3;
                    corNova = new Color(valor, valor, valor, cor.getOpacity());
                }
                pw.setColor(i, j, corNova);
            }
        }
        return wi;
    }

    public static boolean isSquare(Image img) {
        int w = (int) img.getWidth();
        int h = (int) img.getHeight();

        PixelReader pr = img.getPixelReader();
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                if (pr.getColor(i, j).getRed() == 0.0) {
                    return pr.getColor(i, j + 1).getRed() == 0.0 && pr.getColor(i + 1, j).getRed() == 0.0;
                }
            }
        }
        return false;
    }

    public static Image girar(Image img, boolean esquerda) {
        int w = (int) img.getWidth();
        int h = (int) img.getHeight();

        WritableImage wi = new WritableImage(h, w);
        PixelReader pr = img.getPixelReader();
        PixelWriter pw = wi.getPixelWriter();

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                if (esquerda) {
                    pw.setColor(j, w - 1 - i, pr.getColor(i, j));
                } else {
                    pw.setColor(h - 1 - j, i, pr.getColor(i, j));
                }
            }
        }
        return wi;
    }

    public static Image aumentar(Image img) {
        int w = (int) img.getWidth();
        int h = (int) img.getHeight();

        WritableImage wi = new WritableImage(w * 2, h * 2);
        PixelReader pr = img.getPixelReader();
        PixelWriter pw = wi.getPixelWriter();

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                for (int k = i * 2; k < i * 2 + 2; k++) {
                    for (int l = j * 2; l < j * 2 + 2; l++) {
                        pw.setColor(k, l, pr.getColor(i, j));
                    }
                }
            }
        }

        return wi;
    }

    public static Image diminuir(Image img) {
        int w = (int) img.getWidth();
        int h = (int) img.getHeight();

        WritableImage wi = new WritableImage(w / 2, h / 2);
        PixelReader pr = img.getPixelReader();
        PixelWriter pw = wi.getPixelWriter();

        for (int i = 0; i < w - 1; i += 2) {
            for (int j = 0; j < h - 1; j += 2) {
                pw.setColor(i / 2, j / 2, pr.getColor(i, j));
            }
        }

        return wi;
    }

    public static Image adicionar(Image img1, Image img2, double opc1, double opc2, int[] areaSelecionada) {
        int w = (int) Math.max(img1.getWidth(), img2.getWidth());
        int h = (int) Math.max(img1.getHeight(), img2.getHeight());

        WritableImage wi = new WritableImage(w, h);
        PixelReader pr1 = img1.getPixelReader();
        PixelReader pr2 = img2.getPixelReader();
        PixelWriter pw = wi.getPixelWriter();

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                if (i >= areaSelecionada[0] && i <= areaSelecionada[1] && j >= areaSelecionada[2] && j <= areaSelecionada[3]) {
                    double red = 0, green = 0, blue = 0;
                    if (i < img1.getWidth() && j < img1.getHeight()) {
                        red += pr1.getColor(i, j).getRed() * opc1;
                        green += pr1.getColor(i, j).getGreen() * opc1;
                        blue += pr1.getColor(i, j).getBlue() * opc1;
                    }

                    if (i < img2.getWidth() && j < img2.getHeight()) {
                        red += pr2.getColor(i, j).getRed() * opc2;
                        green += pr2.getColor(i, j).getGreen() * opc2;
                        blue += pr2.getColor(i, j).getBlue() * opc2;

                    }
                    pw.setColor(i, j, new Color(red / 100, green / 100, blue / 100, 1));
                } else {
                    pw.setColor(i, j, pr2.getColor(i, j));
                }
            }
        }
        return wi;
    }

    public static Image subtrair(Image img1, Image img2, int[] areaSelecionada) {
        int w = (int) Math.max(img1.getWidth(), img2.getWidth());
        int h = (int) Math.max(img1.getHeight(), img2.getHeight());

        WritableImage wi = new WritableImage(w, h);
        PixelReader pr1 = img1.getPixelReader();
        PixelReader pr2 = img2.getPixelReader();
        PixelWriter pw = wi.getPixelWriter();

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                if (i >= areaSelecionada[0] && i <= areaSelecionada[1] && j >= areaSelecionada[2] && j <= areaSelecionada[3]) {
                    double red = 0, green = 0, blue = 0;
                    if (i < img1.getWidth() && j < img1.getHeight()) {
                        red = pr1.getColor(i, j).getRed();
                        green = pr1.getColor(i, j).getGreen();
                        blue = pr1.getColor(i, j).getBlue();
                    }

                    if (i < img2.getWidth() && j < img2.getHeight()) {
                        red -= pr2.getColor(i, j).getRed();
                        green -= pr2.getColor(i, j).getGreen();
                        blue -= pr2.getColor(i, j).getBlue();

                    }
                    pw.setColor(i, j, new Color(Math.abs(red), Math.abs(green), Math.abs(blue), 1));
                } else {
                    pw.setColor(i, j, pr2.getColor(i, j));
                }
            }
        }
        return wi;
    }
}
