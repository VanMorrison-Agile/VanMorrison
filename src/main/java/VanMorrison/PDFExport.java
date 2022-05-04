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

public class PDFExport {

    // Function adding evenly split cells/columns to provided row with provided names  
    private static void addRow(Boolean isHeader, BaseTable table, List<String> names) {
        Row<PDPage> row = table.createRow(20);
        int size = names.size();
        
        for (int i = 0; i < size; i++) {
            Cell<PDPage> cell = row.createCell((100f/size), names.get(i));
            if (isHeader) {
                cell.setFillColor(new Color(164, 152, 138));
                cell.setFont(PDType1Font.HELVETICA_BOLD);
                table.addHeaderRow(row);
            };
            cell.setFontSize(15);
        }

    }

    public static byte [] getPdf(List<Item> items) throws IOException {

        try (PDDocument document = new PDDocument()) {

            // Create a document and add a page to it (A4 for printing)
            PDPage productPage = new PDPage(PDRectangle.A4);
            document.addPage(productPage);

            // For product page
            try (PDPageContentStream cos = new PDPageContentStream(document, productPage)) {

                float margin = 50;
                // starting y position is whole page height subtracted by top and bottom margin
                float yStartNewPage = productPage.getMediaBox().getHeight() - (2 * margin);
                // we want table across whole page width (subtracted by left and right margin ofcourse)
                float tableWidth = productPage.getMediaBox().getWidth() - (2 * margin);

                boolean drawContent = true;

                float bottomMargin = 70;
                // y position is your coordinate of top left corner of the table
                float yPosition = yStartNewPage;

                BaseTable table = new BaseTable(yPosition, yStartNewPage,
                    bottomMargin, tableWidth, margin, document, productPage, true, drawContent);

                // Row for the headers
                List<String> headers = new ArrayList<String>();
                headers.addAll(Arrays.asList("Vara", "Artikelnummer", "Antal"));

                addRow(true, table, headers);

                // Add all products/items  from provided list
                items.forEach((item) -> addRow(false, table, item.getValues()));

                Integer totalPrice = 0;
                try {
                    totalPrice = items.stream()
                        .mapToInt(item -> Integer.parseInt(item.getPrice()))
                            .sum();
                } catch (Exception e) {
                    throw new IllegalArgumentException("One price value is in wrong format");
                }

                // Add last row for the total sum of prices
                Row<PDPage> totalPriceRow = table.createRow(20);
                Cell<PDPage> cell = totalPriceRow.createCell(100/1.5f, "Total summa exkl. moms:");
                cell.setFontSize(15);
                cell = totalPriceRow.createCell(100f/3, totalPrice.toString());
                cell.setFontSize(15);

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