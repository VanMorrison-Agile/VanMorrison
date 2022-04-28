package VanMorrison;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.awt.Color;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import be.quodlibet.boxable.BaseTable;
import be.quodlibet.boxable.Cell;
import be.quodlibet.boxable.Row;
import be.quodlibet.boxable.line.LineStyle;

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

    public static byte [] getPdf(List<Item> items) throws IOException {

        try (PDDocument document = new PDDocument()) {

            // Create a document and add a page to it (A4 for printing)
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            try (PDPageContentStream cos = new PDPageContentStream(document, page)) {

                float margin = 50;
                // starting y position is whole page height subtracted by top and bottom margin
                float yStartNewPage = page.getMediaBox().getHeight() - (2 * margin);
                // we want table across whole page width (subtracted by left and right margin ofcourse)
                float tableWidth = page.getMediaBox().getWidth() - (2 * margin);

                boolean drawContent = true;

                float bottomMargin = 70;
                // y position is your coordinate of top left corner of the table
                float yPosition = yStartNewPage;

                BaseTable table = new BaseTable(yPosition, yStartNewPage,
                    bottomMargin, tableWidth, margin, document, page, true, drawContent);

                // Row for the headers
                Row<PDPage> headerRow = table.createRow(30);
                List<String> headers = new ArrayList<String>();
                headers.addAll(Arrays.asList("Vara", "Artikelnummer", "Antal"));

                addHeaderRow(headerRow, headers);
                table.addHeaderRow(headerRow);

                // Add all products/items  from provided list
                items.forEach((item) -> addItemRow(table, item));
                Integer totalPrice = 0;
                try {
                    totalPrice = items.stream()
                        .mapToInt(item -> Integer.parseInt(item.getPrice()))
                            .sum();
                } catch (Exception e) {
                    throw new IllegalArgumentException("One price value is in wrong format");
                }

                table.draw();
            }

            // Saves pdf to an outputstream, which fills a byte array. Then returns byte array.
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            document.save(byteArrayOutputStream);
            document.close();
            byte [] bytearray = new byte [(int) byteArrayOutputStream.size()];
            InputStream inputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
            BufferedInputStream bis = new BufferedInputStream(inputStream);
            bis.read(bytearray, 0, bytearray.length);

            return bytearray;
        } catch (IOException e) {System.out.println(e); return null;}
    }
}