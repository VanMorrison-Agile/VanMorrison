package VanMorrison;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.apache.pdfbox.pdmodel.interactive.form.FieldUtils;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import Data.Parameter;

public class Server {

    public static final int PORT = 80;

    private String header = "",style ="", body = "", footer = "", script = "";

    CSVReader csv = new CSVReader("src/main/resources/exampleList.csv");

    private static Server s;

    public static void main(String[] args) throws Exception {
        s = new Server();
        s.generateMain();
        s.server.start();
    }

    /**
     * Adds the input html code to the end of the website's body.
     * @param html The code to add
     */
    public void addBody(String html) {
        body += html;
    }

    public void addScript(String javascript) {
        script += javascript;
    }

    private void addStyle(){ style += csv.getStyle(); }

    HttpServer server;

    public Server() throws Exception {
        this.server = HttpServer.create(new InetSocketAddress(PORT), 0);

        server.createContext("/", (HttpExchange t) -> {
            String response =
                "<!doctype html>" +
                        "<head>\n" +
                        "<meta charset=\"UTF-8\">\n" +
                        "</head>"+
                "<header>" +
                    style +
                    header +
                "</header>" +
                "<body>" +
                    body +
                "</body>" +
                "<footer>" +
                    footer +
                "</footer>" +
                        "<script>" +
                        script +
                        "</script>" +
                        "</html>";
            byte[] bytes = response.getBytes();
            t.sendResponseHeaders(200, bytes.length);
            OutputStream os = t.getResponseBody();
            os.write(bytes);
            os.close();
            generateMain();
        });

        server.createContext("/viewProvider", (HttpExchange t) -> {
            //String response = readHTML("src/viewProvider.html");
            HtmlParser h = new HtmlParser("src/viewProvider.html");
            StringBuilder htmlProviders = new StringBuilder();

            String[] pathnames;

            File folder = new File("provider");
            //File[] listOfFiles = folder.listFiles();

            // Populates the array with names of files and directories
            pathnames = folder.list();

            // For each pathname in the pathnames array
            for (String pathname : pathnames) {
                // Print the names of files and directories
                pathname = pathname.substring(0,pathname.lastIndexOf("."));
                htmlProviders.append("<li><a href=\"/products/"+pathname+"\">"+pathname+"</a></li>");

            }

            h.set("lis", htmlProviders.toString());
            String response = h.getString();

            byte[] bytes = response.getBytes();
            t.sendResponseHeaders(200, bytes.length);
            OutputStream os = t.getResponseBody();
            os.write(bytes);
            os.close();

        });

        server.createContext("/products", (HttpExchange t) -> {
            //String response = readHTML("src/viewProvider.html");
//            System.out.println("test");
//            HtmlParser h = new HtmlParser("src/viewProvider.html");
//            h.set("hej", "inte hej");
//            h.set("main", "main2");
//            h.set("vad är den", "här");
//
//            System.out.println(h.getString());

            String response = t.getRequestURI().toString();

            String provider = response.substring(10);
            csv = new CSVReader("provider/" + provider + ".csv");

            response =
                    "<head>\n" +
                    "<meta charset=\"UTF-8\">\n" +
                    "<link rel=\"stylesheet\" href=\"/styles/viewProvider.css\">" +
                    "</head>"+
                    "<header>" +
                    response.substring(10) +
                    "</header>" +
                    "<body>" +
                    csv.printToString() +
                    generateCartDisplay() +
                    readHTML("src/html/cartSubmitForm.html") +
                    "</body>" +
                    "<script>" +
                    "var provider = '" + provider + "';" +
                    generateCartScript() + 
                    """
                        var form = document.getElementById("sendOrderForm");

                        var providerInput = document.createElement('input');
                        providerInput.setAttribute('name', 'provider');
                        providerInput.setAttribute('type', 'hidden');
                        providerInput.setAttribute('value', '""" + provider + "');" +
                        "form.appendChild(providerInput);" +
                    "</script>";

                    
            byte[] bytes = response.getBytes();
            t.sendResponseHeaders(200, bytes.length);
            OutputStream os = t.getResponseBody();
            os.write(bytes);
            os.close();

        });

        server.createContext("/addProvider", (HttpExchange t) -> {
            String response = readHTML("src/addProvider.html");
            Map<String, Parameter> params = HTMLUtility.getMimeParameters(t.getRequestBody());

            //DO BUSINESS LOGIC
            byte[] csvData = params.get("uploadedFile").getData();
            String prov = params.get("provider").getDataAsString();

            if (prov.equals("new")) {
                prov = params.get("uploadedFile").getFileName();
                if (prov.lastIndexOf(".") != -1)
                    prov = prov.substring(0,prov.lastIndexOf("."));
            }

            String tempString = "provider/" + prov + ".csv";
            File file = new File(tempString);
            file.delete();
            file.createNewFile();
            try {

                FileOutputStream writer = new FileOutputStream(file);
                writer.write(csvData);
                writer.close();

            } catch (Exception e) {
                e.getStackTrace();
            }


            byte[] bytes = response.getBytes();
            t.sendResponseHeaders(200, bytes.length);
            OutputStream os = t.getResponseBody();
            os.write(bytes);
            os.close();
            try {
                generateMain();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        
        server.createContext("/getpdf", (HttpExchange t) -> {
            // Add the required response header for a PDF file
            Headers h = t.getResponseHeaders();
            h.add("Content-Type", "application/pdf");

            Map<String, Parameter> params = HTMLUtility.getMimeParameters(t.getRequestBody());


            String[] itemNr = params.get("itemNr").getDataAsStringArray();
            String[] itemCount = params.get("itemCount").getDataAsStringArray();


            CSVReader csvReader = new CSVReader("provider/" + params.get("provider").getDataAsString() + ".csv");


            List<Item> sortiment = csvReader.getItemList();

            List<Item> items = new ArrayList<Item>();
            for (int i = 0; i < itemNr.length; i++) {

                Item currentItem = null;
                for (Item item : sortiment) {
                    if (item.getArtNr().equals(itemNr[i])) {
                        currentItem = item;
                        break;
                    }
                }
                
                if (currentItem == null){
                    System.out.println("Invalid item: " + itemNr[i]);
                    items.add(new Item(itemNr[i], "Error: PDF generation aborted due to invalid item:", "0"));
                    break;
                }

                for (int j = 0; j < Integer.parseInt(itemCount[i]); j++) {
                    items.add(currentItem);
                }
            }

            //Get byte array containing pdf
            byte [] docBytes = PDFExport.getPdf(items);

            // Send the response.
            t.sendResponseHeaders(200, docBytes.length);
            OutputStream os = t.getResponseBody();
            os.write(docBytes,0, docBytes.length);
            os.close();
        });



        server.createContext("/styles", (HttpExchange t) -> {

            //generate path to file from URI
            String file = t.getRequestURI().toString().substring(8);
            String path = "src/styles/"+file;

            //Reads css file to string
            StringBuilder html = new StringBuilder();
            try {
                FileReader reader = new FileReader(path);
                while (reader.ready()) html.append((char)reader.read());
            } catch (Exception e) {
                String msg = "File cannot be found";
                byte[] msgBytes = msg.getBytes();
                System.out.println(e);
                t.sendResponseHeaders(404, msgBytes.length);
                OutputStream os = t.getResponseBody();
                os.write(msgBytes);
                os.close();
                return;
            }
            String response =  html.toString();

            //Sends file to client
            byte[] bytes = response.getBytes();
            t.sendResponseHeaders(200, bytes.length);
            OutputStream os = t.getResponseBody();
            os.write(bytes);
            os.close();
        });
    }





    public String readHTML(String filename){
        StringBuilder html = new StringBuilder();
        try {
            FileReader reader = new FileReader(filename);
            while (reader.ready()) html.append((char)reader.read());
        } catch (Exception e) {System.out.println(e);}
        return html.toString();
    }

    /**
     * Compiles a form for creating/updating product lists for the current providers or a new one.
     * @return A complete html form represented as a string
     */
    public String addProviderForm(){
        String readData = readHTML("src/form.html");

        StringBuilder aa = new StringBuilder();
        File folder = new File("provider");
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                String companyName = listOfFiles[i].getName();
                if (companyName.contains(".")){
                    companyName = companyName.substring(0, companyName.lastIndexOf('.'));
                }

                aa.append("<option value=\"" + companyName + "\">" + companyName + "</option>");
            }
        }

        return readData.replace("#OPTIONS#", aa.toString());
    }


    public String generateCartDisplay() {
        String cartDisplay = "<div>";

        for (Item item:
                csv.items) {
            cartDisplay += "<div id='cart" + item.getArtNr() + "' style ='display:none;'>";
            cartDisplay += item.getName();
            cartDisplay += "<p id='cart"+ item.getArtNr()+ "Number'> </p>";
            cartDisplay += "</div>";
        }

        cartDisplay += "</div>";

        return cartDisplay;
    }

    public String generateCartScript() {
        String cartItemsContent = "";
        for (Item item:
             csv.items) {
            cartItemsContent += item.getArtNr() + " : 0 , "; //I think js is alright with a trailing comma
        }
        
        return """
            var cartItems = {
            """
                + cartItemsContent +
            """
            };
            
            
            
            
            function updateItem(id) {   
                document.getElementById('cart' + id + 'Number').innerHTML = cartItems[id];
                var cartItemDisplay = document.getElementById('cart' + id);
                if (cartItems[id] == 0) {
                    cartItemDisplay.style.display="none";
                } else {
                    cartItemDisplay.style.display="block"
                }
            }
            
            function addToCart(id){
                cartItems[id] = 1;
                updateItem(id);
            }

            function addItem(id) {
                cartItems[id] ++;
                updateItem(id);
            }
            
            function removeItem(id) {
                if(cartItems[id] > 0)
                    cartItems[id]--;
                updateItem(id);
            }
            
            function removeAll(id){
                cartItems[id] = 0;
                updateItem(id);
            }


            function addItemsToCartForm(){
                var form = document.getElementById("sendOrderForm");
                
                for(const [artNr, count] of Object.entries(cartItems)){
                    if (count != 0){
                        var itemInput = document.createElement("input");
                        itemInput.setAttribute("name", "itemNr[]");
                        itemInput.setAttribute("type", "hidden");
                        itemInput.setAttribute("value", artNr);
                        form.appendChild(itemInput);
                        
                        itemInput = document.createElement("input");
                        itemInput.setAttribute("name", "itemCount[]");
                        itemInput.setAttribute("type", "hidden");
                        itemInput.setAttribute("value", count);
                        form.appendChild(itemInput);
                    }
                }
            }
            """;
    }

    public void generateMain() {
        ///TODO: Add elements to the site by calling methods on s

        body = "";
        header = "<meta charset=\"UTF-16\">";
        addStyle();
        addBody("Hello world!");
        addBody(addProviderForm());
        addBody("<Br />");
        addBody("<a href=\"/pdf\" download=\"perfectOrder.pdf\">Download PDF</a>");

        addBody(csv.printToString());


        

    }
}

