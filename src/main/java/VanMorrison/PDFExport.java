package VanMorrison;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

            // Create a document and add all pages to it (A4 for printing)
            PDPage productPage = new PDPage(PDRectangle.A4);
            PDPage metadataPage = new PDPage(PDRectangle.A4);
            document.addPage(productPage);
            document.addPage(metadataPage);

            // For product page
            try (PDPageContentStream cont = new PDPageContentStream(document, productPage)) {

                float margin = 50;
                // starting y position is whole page height subtracted by top and bottom margin
                float yStartNewPage = productPage.getMediaBox().getHeight() - (2 * margin);
                // we want table across whole page width (subtracted by left and right margin ofcourse)
                float tableWidth = productPage.getMediaBox().getWidth() - (2 * margin);

                boolean drawContent = true;

                float bottomMargin = 70;
                // y position is your coordinate of top left corner of the table
                float yPosition = yStartNewPage;

                BaseTable productTable = new BaseTable(yPosition, yStartNewPage,
                    bottomMargin, tableWidth, margin, document, productPage, true, drawContent);

                // Row for the headers
                List<String> headers = new ArrayList<String>();
                headers.addAll(Arrays.asList("Vara", "Artikelnummer", "Antal"));

                addRow(true, productTable, headers);

                // Add all products/items  from provided list
                items.forEach((item) -> addRow(false, productTable, item.getValues()));

                Integer totalPrice = 0;
                try {
                    totalPrice = items.stream()
                        .mapToInt(item -> Integer.parseInt(item.getPrice()))
                            .sum();
                } catch (Exception e) {
                    throw new IllegalArgumentException("One price value is in wrong format");
                }

                // Add last row for the total sum of prices
                Row<PDPage> totalPriceRow = productTable.createRow(20);
                Cell<PDPage> cell = totalPriceRow.createCell(100/1.5f, "Total summa exkl. moms:");
                cell.setFontSize(15);
                cell = totalPriceRow.createCell(100f/3, totalPrice.toString());
                cell.setFontSize(15);

                productTable.draw();
            }

            
            // For metadata page
            try (PDPageContentStream cont = new PDPageContentStream(document,  metadataPage)) {
                float margin = 50;
                // starting y position is whole page height subtracted by top and bottom margin
                float yStartNewPage = metadataPage.getMediaBox().getHeight() - (2 * margin);
                // we want table across whole page width (subtracted by left and right margin ofcourse)
                float tableWidth = metadataPage.getMediaBox().getWidth() - (2 * margin);

                boolean drawContent = true;

                float bottomMargin = 70;
                // y position is your coordinate of top left corner of the table
                float yPosition = yStartNewPage - 150;

                // Initial text
                cont.beginText();
                cont.setFont(PDType1Font.HELVETICA_BOLD, 30);
                cont.setLeading(30f);
                
                cont.newLineAtOffset(margin, yStartNewPage);
                String title1 = "Direktupphandlingsblankett för";
                String title2 = "inköp mellan 0kr - 10.000kr";
                cont.showText(title1);
                cont.newLine();
                cont.showText(title2);

                // Bullet points
                String bulletPoint1 = "\u2022 Ifylld blankett skickas till funktionsbrevlådan för ditt inköpsområde.";
                String bulletPoint2 = "\u2022 När beställningen tagits emot hanteras den inom 5 arbetsdagar.";
                String bulletPoint3 = "\u2022 Efter leverans av varorna mejla bekräftelse till inköp för leveranskvittens.";
                cont.setFont(PDType1Font.HELVETICA, 12);
                cont.newLineAtOffset(0.5f*margin, 0);
                cont.newLine();
                cont.showText(bulletPoint1);
                cont.newLine();
                cont.newLineAtOffset(0, 12);
                cont.showText(bulletPoint2);
                cont.newLine();
                cont.newLineAtOffset(0, 12);
                cont.showText(bulletPoint3);

                // Adding checkboxes and the relevant text
                String order = "Beställningen gäller:";
                cont.newLineAtOffset(-0.5f*margin, 0);
                cont.setFont(PDType1Font.HELVETICA_BOLD, 12);
                cont.newLine();
                cont.showText(order);
                String orderOptions = "         Direktupphandling             Hämtköp";
                cont.setFont(PDType1Font.HELVETICA, 12);
                cont.showText(orderOptions);
                
                // Last bit of text
                String pickUpDescription = "Namn på person som hämtar varan:";
                cont.newLineAtOffset(0, 12);
                cont.setFont(PDType1Font.HELVETICA_BOLD, 12);
                cont.newLine();
                cont.showText(pickUpDescription);

                String space = " ";
                // Replace with data from website
                String pickUp = "Bemil";
                cont.setFont(PDType1Font.HELVETICA, 12);
                cont.showText(space);
                cont.showText(pickUp);
                cont.endText();

                // Actual checkboxes
                cont.addRect(180, 615, 15, 15);
                // Replace true with boolean data from website
                if (true) cont.fill();
                else {
                    cont.setLineWidth(1);
                    cont.setNonStrokingColor(Color.WHITE);
                    cont.setStrokingColor(Color.BLACK);
                    cont.stroke();
                }

                cont.addRect(320, 615, 15, 15);
                if (false) cont.fill();
                else {
                    cont.setLineWidth(1);
                    cont.setNonStrokingColor(Color.WHITE);
                    cont.setStrokingColor(Color.BLACK);
                    cont.stroke();
                }
                cont.close();

                // Adding tables
                BaseTable buyerTable = new BaseTable(yPosition, yStartNewPage,
                    bottomMargin, tableWidth, margin, document, metadataPage, true, drawContent);

                // Row for the headers for buyerTable
                List<String> buyerHeaders = new ArrayList<String>();
                buyerHeaders.add("Uppgifter beställare");
                addRow(true, buyerTable, buyerHeaders);

                // Example 
                Map<String, String> personalInfo = new HashMap<String, String>();
                personalInfo.put("För- och efternamn", "Emil");
                personalInfo.put("Enhet", "Adn");
                personalInfo.put("Leveransadress", "Lindholmen");
                personalInfo.put("TelefonNummer till verksamheten", "0046");
                personalInfo.put("Ansvar", "None");
                personalInfo.put("Verkkod", "666");

                personalInfo.forEach((k,v) -> addRow(false, buyerTable, Arrays.asList(k, v)));

                float newYPosition = buyerTable.draw();
                
            } catch (Exception e) {System.out.println(e);}


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