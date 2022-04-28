package VanMorrison;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.awt.Color;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import be.quodlibet.boxable.BaseTable;
import be.quodlibet.boxable.Cell;
import be.quodlibet.boxable.Row;

public class PDFExport {

    // Function adding 3 cells/columns to provided row with provided names  
    private static void addHeaderRow(Row row, List<String> names) {
        int size = names.size();
        
        for (int i = 0; i < size; i++) {
            int width;
            //Different width for first column because of the division by 3
            if (i == 0) {width = 34;}
            else {width = 33;};
            Cell<PDPage> cell = row.createCell(width, names.get(i));
            cell.setFillColor(new Color(164, 152, 138));
            cell.setFont(PDType1Font.HELVETICA_BOLD);
            cell.setFontSize(15);
        }
    }

    // Function adding 3 cells/columns to provided row with values of provided item 
    private static void addItemRow(BaseTable table, Item item) {
        Row<PDPage> row = table.createRow(20);
        Cell<PDPage> cell = row.createCell(34, item.getName());
        cell.setFontSize(15);
        cell = row.createCell(33, item.getArtNr());
        cell.setFontSize(15);
        cell = row.createCell(33, item.getPrice());
        cell.setFontSize(15);
    }

    public static byte [] getPdf() throws IOException {

        try (PDDocument doc = new PDDocument()) {

            PDPage myPage = new PDPage();
            doc.addPage(myPage);

            try (PDPageContentStream cont = new PDPageContentStream(doc, myPage)) {

                cont.beginText();

                cont.setFont(PDType1Font.TIMES_ROMAN, 12);
                cont.setLeading(14.5f);

                cont.newLineAtOffset(25, 700);
                String line1 = "Häcken är ett bra lag.";
                cont.showText(line1);

                cont.newLine();

                String line2 = "Men HBK är bättre sämre!";
                cont.showText(line2);
                cont.newLine();

                cont.endText();
            }

            // Saves pdf to an outputstream, which fills a byte array. Then returns byte array.
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            doc.save(byteArrayOutputStream);
            doc.close();
            byte [] bytearray = new byte [(int) byteArrayOutputStream.size()];
            InputStream inputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
            BufferedInputStream bis = new BufferedInputStream(inputStream);
            bis.read(bytearray, 0, bytearray.length);

            return bytearray;
        } catch (IOException e) {System.out.println(e); return null;}
    }
}