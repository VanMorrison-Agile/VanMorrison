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
    private static void addItemRow(BaseTable table, Item item, Integer amount) {
        Row<PDPage> row = table.createRow(20);
        Cell<PDPage> cell = row.createCell(34, item.getName());
        cell.setFontSize(15);
        cell = row.createCell(33, item.getArtNr());
        cell.setFontSize(15);
        cell = row.createCell(33, amount.toString());
        cell.setFontSize(15);
    }

    public static byte [] getPdf(List<Item> items, List<Integer> amounts) throws IOException {

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
                for (int i = 0; i < items.size(); i++) {
                    addItemRow(table, items.get(i), amounts.get(i));
                }
                Integer totalPrice = 0;
                try {
                    for (int i = 0; i < items.size(); i++) {
                        totalPrice += Integer.parseInt(items.get(i).getPrice().replace(" ", "")) * amounts.get(i);
                    }
                } catch (Exception e) {
                    System.out.println("Oh no, a price value is invalid! Throwing an exception, let's hope it doesn't disappear :thinking:");
                    System.out.print("In all seriousness, I have no idea where this exception ends up, so I'm just gonna print the error here: " + e.getMessage());
                    throw new IllegalArgumentException("One price value is in wrong format");
                }
                
                // Add last row for the total sum of prices
                Row<PDPage> totalPriceRow = table.createRow(20);
                Cell<PDPage> cell = totalPriceRow.createCell(67, "Total summa exkl. moms:");
                cell.setFontSize(15);
                cell = totalPriceRow.createCell(33, totalPrice.toString());
                cell.setFontSize(15);

                table.draw();
            } catch (Exception e) {
                throw e;
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
        } catch (Exception e) {System.out.println(e); return null;}
    }
}