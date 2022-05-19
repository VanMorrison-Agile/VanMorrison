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

    public static byte [] getPdf(List<Item> items, List<Integer> amounts, Map<String, String> metadata ) throws IOException {
        
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
                addRow(true, productTable, Arrays.asList("Vara", "Artikelnummer", "Antal"));
                
                // Add all products/items  from provided list
                for (int i = 0; i < items.size(); i++) {
                    List<String> values = items.get(i).getValues(); 
                    String amount = amounts.get(i).toString();
                    values.add(amount);
                    addRow(false, productTable, values);
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
                Row<PDPage> totalPriceRow = productTable.createRow(20);
                Cell<PDPage> cell = totalPriceRow.createCell(100/1.5f, "Total summa exkl. moms:");
                cell.setFontSize(15);
                cell = totalPriceRow.createCell(100f/3, totalPrice.toString());
                cell.setFontSize(15);

                productTable.draw();
            } catch (Exception e) {throw e;}

            
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
                String pickUp = metadata.get("pickUp");
                cont.setFont(PDType1Font.HELVETICA, 12);
                cont.showText(space);
                cont.showText(pickUp);
                cont.endText();

                // Checks if there is a selected option for order applies, if not sets to empty to avoid comparing with null
                String orderApplies = metadata.get("orderApplies");
                if (orderApplies == null) orderApplies = "";
                
                // Actual checkboxes
                cont.addRect(180, 615, 15, 15);
                if (orderApplies.equals("direktupphandling")) cont.fill();
                else {
                    cont.setLineWidth(1);
                    cont.setNonStrokingColor(Color.WHITE);
                    cont.setStrokingColor(Color.BLACK);
                    cont.stroke();
                }
                
                // Resetting color for drawing next checkbox
                cont.setNonStrokingColor(Color.BLACK);
                cont.setStrokingColor(Color.BLACK);

                cont.addRect(320, 615, 15, 15);
                if (orderApplies.equals("hämtköp")) cont.fill();
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
                addRow(true, buyerTable, Arrays.asList("Uppgifter beställare"));

                addRow(false, buyerTable, Arrays.asList("För- och efternamn", metadata.get("firstName") + " " + metadata.get("surname")));
                addRow(false, buyerTable, Arrays.asList("Enhet", metadata.get("unit")));
                addRow(false, buyerTable, Arrays.asList("Leveransadress", metadata.get("deliveryAdress")));
                addRow(false, buyerTable, Arrays.asList("Telefonnummer till verksamheten", metadata.get("phone")));
                addRow(false, buyerTable, Arrays.asList("Ansvar", metadata.get("responsibility")));
                addRow(false, buyerTable, Arrays.asList("Verkkod", metadata.get("verificationNumber")));

                float newYPosition = buyerTable.draw();

                BaseTable supplierTable = new BaseTable(newYPosition - 20, yStartNewPage,
                bottomMargin, tableWidth, margin, document, metadataPage, true, drawContent);

                List<String> supplierHeaders = new ArrayList<String>();
                supplierHeaders.add("Uppgifter leverantör");
                addRow(true, supplierTable, supplierHeaders);

                addRow(false, supplierTable, Arrays.asList("Namn", metadata.get("supplierName")));
                addRow(false, supplierTable, Arrays.asList("Organisationsnummer", metadata.get("organisationNumber")));
                addRow(false, supplierTable, Arrays.asList("Mejladress", metadata.get("mail")));

                supplierTable.draw();
            } catch (Exception e) {throw e;}


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