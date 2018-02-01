package sample;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.TextAlignment;
import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Window;

public class Controller {

  @FXML public TextField sourceFileTarget;
  @FXML public TextField destTarget;
  @FXML public CheckBox checkBox;

  @FXML
  public void handleFindButtonAction(ActionEvent actionEvent) {
    Node source = (Node) actionEvent.getSource();
    Window stage = source.getScene().getWindow();
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Open File");
    fileChooser.getExtensionFilters().add(new ExtensionFilter("Text doc(*.txt", "*.txt"));
    File file = fileChooser.showOpenDialog(stage);
    if (file != null) {
      sourceFileTarget.setText(file.getAbsolutePath());
    }
  }

  @FXML
  public void chooseButtonAction(ActionEvent actionEvent) {
    Node source = (Node) actionEvent.getSource();
    Window stage = source.getScene().getWindow();
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Save file");
    fileChooser.getExtensionFilters().add(new ExtensionFilter("PDF doc(*.pdf)", "*.pdf"));
    fileChooser.setInitialFileName("testPDF.pdf");
    File file = fileChooser.showSaveDialog(stage);
    if (file != null) {
      destTarget.setText(file.getAbsolutePath());
    }
  }

  @FXML
  public void handleConvertButtonAction(ActionEvent actionEvent) throws IOException {
    String path = sourceFileTarget.getText();
    String dest = destTarget.getText();
    createPdf(path, dest);
    if (checkBox.isSelected()) {
      File file = new File(dest);
      if (!Desktop.isDesktopSupported()) {
        System.out.println("Desktop is not supported");
        return;
      }
      new Thread(() -> {
        try {
          Desktop desktop = Desktop.getDesktop();
          if (file.exists()) {
            desktop.open(file);
          }
        } catch (IOException e) {
          e.printStackTrace();
        }
      }).start();

    }
  }

  private void createPdf(String text, String dest) throws IOException {
    Document document = null;
    BufferedReader br = null;
    String line;
    PdfFont normal = PdfFontFactory.createFont(StandardFonts.COURIER);

    try (PdfDocument pdf = new PdfDocument(new PdfWriter(dest))) {
      document = new Document(pdf).setTextAlignment(TextAlignment.JUSTIFIED);
      br = new BufferedReader(new FileReader(text));
      while ((line = br.readLine()) != null) {
        String whiteSpaceLine = line.replaceAll("\\s", "\u00a0");
        document.add(new Paragraph(whiteSpaceLine).setFont(normal));
      }
    } finally {
      if (document != null) document.close();
      if (br != null) br.close();
    }
  }


}
