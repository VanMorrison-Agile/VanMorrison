package VanMorrison;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.apache.pdfbox.pdmodel.interactive.form.FieldUtils;

import java.io.*;
import java.lang.reflect.Array;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import Data.Parameter;

public class Server {

    public static final int PORT = 80;

    CSVReader csv = new CSVReader("src/main/resources/exampleList.csv");

    private static Server s;

    /** 
     * Runs the "VanMorrison" web server on port 80
     * @param args command-line arguments, unused
     * @throws Exception An exception thrown by the program
     */
    public static void main(String[] args) throws Exception {
        s = new Server();
        s.server.start();
    }

    HttpServer server;

    public Server() throws Exception {
        this.server = HttpServer.create(new InetSocketAddress(PORT), 0);

        HttpHandler mainHandler = (HttpExchange t) -> {
            HtmlParser h = new HtmlParser("src/html/viewProvider.html");
            StringBuilder htmlProviders = new StringBuilder();

            // Populates an array with names of files and directories in provider directory
            String[] pathNames = new File("provider").list();

            // For each pathname in the pathNames array
            for (String pathName : pathNames) {
                // Remove suffix if it exists
                if (pathName.lastIndexOf(".") != -1) pathName = pathName.substring(0, pathName.lastIndexOf("."));
                // Add a list item for provider
                String listItem = "<li><a href=\"/products/%PROVIDER%\">%PROVIDER%<div class=\"i-wrapper\"><i class=\"fa fa-arrow-circle-right\"></i></div></a></li>";
                htmlProviders.append(listItem.replaceAll("%PROVIDER%", pathName));
            }

            h.set("lis", htmlProviders.toString());

            h.set("addProviderOptions", generateProviderOptions());

            String response = h.getString();

            byte[] bytes = response.getBytes();
            t.sendResponseHeaders(200, bytes.length);
            OutputStream os = t.getResponseBody();
            os.write(bytes);
            os.close();

        };
        server.createContext("/", mainHandler);

        server.createContext("/products", (HttpExchange t) -> {

            //Gets the full query
            String response = t.getRequestURI().toString();

            String provider = response.substring(10);
            csv = new CSVReader("provider/" + provider + ".csv");

            
            String extraScript = "<script>" + 
            generateCartScript() + 
            """
                var form = document.getElementById("sendOrderForm");

                var providerInput = document.createElement('input');
                providerInput.setAttribute('name', 'provider');
                providerInput.setAttribute('type', 'hidden');
                providerInput.setAttribute('value', '""" + provider + "');" +
                "form.appendChild(providerInput);" +
                "</script>";


            HtmlParser p = new HtmlParser("src/html/productView.html");
            // Sets key ${providerName} in the html text to the part of
            // the query that comes after /provider/...
            p.set("providerName" ,response.substring(10));
            p.set("extraScript", extraScript);
            p.set("cartDisplay", generateCartDisplay());
            //Adding cart submit form makes this not work. Figure out why later

            //Send html to web client
            response = p.getString();
            byte[] bytes = response.getBytes();
            t.sendResponseHeaders(200, bytes.length);
            OutputStream os = t.getResponseBody();
            os.write(bytes);
            os.close();

        });

        server.createContext("/addProvider", (HttpExchange t) -> {
            Map<String, Parameter> params = HTMLUtility.getMimeParameters(t.getRequestBody());

            //DO BUSINESS LOGIC
            byte[] csvData = params.get("uploadedFile").getData();
            String prov = params.get("provider").getDataAsString();

            if (prov.equals("new")) {
                prov = params.get("uploadedFile").getFileName();
                if (prov.lastIndexOf(".") != -1)
                    prov = prov.substring(0,prov.lastIndexOf("."));
            }

            File file = new File("provider/" + prov + ".csv");
            file.delete();
            file.createNewFile();
            try {

                FileOutputStream writer = new FileOutputStream(file);
                writer.write(csvData);
                writer.close();

            } catch (Exception e) {
                e.getStackTrace();
            }

            mainHandler.handle(t);
        });
        
        server.createContext("/getpdf", (HttpExchange t) -> {
            // Add the required response header for a PDF file
            Headers h = t.getResponseHeaders();
            h.add("Content-Type", "application/pdf");

            Map<String, Parameter> params = HTMLUtility.getMimeParameters(t.getRequestBody());
            Map<String, String> metadata = new HashMap<String, String>();
            params.forEach((k, v) -> metadata.put(k, v.getDataAsString()));

            String[] itemNr = params.get("itemNr").getDataAsStringArray();
            String[] itemCount = params.get("itemCount").getDataAsStringArray();


            CSVReader csvReader = new CSVReader("provider/" + params.get("provider").getDataAsString() + ".csv");


            List<Item> sortiment = csvReader.getItemList();

            List<Item> items = new ArrayList<Item>();
            List<Integer> amounts = new ArrayList<Integer>();
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
                
                items.add(currentItem);
                amounts.add(Integer.parseInt(itemCount[i]));
            }

            //Get byte array containing pdf
            byte [] docBytes = PDFExport.getPdf(items, amounts, metadata);

            // Send the response.
            t.sendResponseHeaders(200, docBytes.length);
            OutputStream os = t.getResponseBody();
            os.write(docBytes,0, docBytes.length);
            os.close();
        });

        server.createContext("/search", (HttpExchange t) -> {
            //Fetch the query
            String queryParams = t.getRequestURI().getQuery();

            //Get the queries as a map
            Map<String,String> queries = queryToMap(queryParams);

            //Retrieves products from the provider that is in the query
            CSVReader csvTest = new CSVReader("provider/"+ queries.get("provider") + ".csv");
            Search search = new Search(csvTest.getItems());

            //Searching by the  key "query" that is in the URL query
            List<Item> items = search.search(queries.get("query"));

            //Transform the results from searching to html
            CSVReader reader = new CSVReader(items);
            String res = reader.printToString();

            //Sends a response to web client
            byte[] bytes = res.getBytes(StandardCharsets.UTF_8);

            t.sendResponseHeaders(200, bytes.length);
            OutputStream os = t.getResponseBody();
            os.write(bytes);
            os.close();
        });

        //Handling request of .css files from web client
        server.createContext("/styles", (HttpExchange t) -> {

            //generate path to file from URI
            String file = t.getRequestURI().toString().substring(8);
            String path = "src/styles/"+file;

            //Reads css file to string
            StringBuilder html = new StringBuilder();
            try {
                //Appends context of file to string line by line
                FileReader reader = new FileReader(path);
                while (reader.ready()) html.append((char)reader.read());
            } catch (Exception e) {
                //If file could not be found, send error message and code 404 (not found) to web client
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

        //Handling request of .js files from web client
        server.createContext("/javascripts", (HttpExchange t) -> {

            //generate path to file from URI
            String file = t.getRequestURI().toString().substring(13);
            String path = "src/javascripts/"+file;

            //Reads css file to string
            StringBuilder html = new StringBuilder();
            try {
                //Appends context of file to string line by line
                FileReader reader = new FileReader(path);
                while (reader.ready()) html.append((char)reader.read());
            } catch (Exception e) {
                //If file could not be found, send error message and code 404 (not found) to web client
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

    /**
     * Compiles a form for creating/updating product lists for the current providers or a new one.
     * @return A complete html form represented as a string
     */
    public String generateProviderOptions(){
        StringBuilder aa = new StringBuilder();
        File folder = new File("provider");
        File[] listOfFiles = folder.listFiles();

        for (File file : listOfFiles) {
            if (file.isFile()) {
                String companyName = file.getName();
                if (companyName.contains(".")){
                    companyName = companyName.substring(0, companyName.lastIndexOf('.'));
                }

                aa.append("<option value=\"" + companyName + "\">" + companyName + "</option>");
            }
        }

        return aa.toString();
    }

    //TODO document this, I don't understand it well enough to get the terminology right
    /** 
     * 
     * @param query
     * @return Map<String, String>
     */
    public Map<String, String> queryToMap(String query){
        if(query == null){
            return null;
        }
        Map<String, String> result = new HashMap<>();
        for(String param : query.split("&")){
            String[] entry = param.split("=");
            if(entry.length > 1) {
                result.put(entry[0], entry[1]);
            }else{
                result.put(entry[0], "");
            }
        }
        return result;
    }

    /**
     * displays products that is placed in the cart.
     * @return a complete html div as a string
     */
    public String generateCartDisplay() {
        String cartDisplay = "<div id ='cart'>\n";

        for (Item item: csv.items) {
            if (item.getArtNr().equals("artNr")) continue; //Skip header, couldn't come up with a better method
            HtmlParser p = new HtmlParser("src/html/cartItem.html");
            p.set("id", item.getArtNr());
            p.set("name", item.getName());

            cartDisplay += (p.getString().substring(1)+"\n");
        }

        cartDisplay += "</div>";

        return cartDisplay;
    }
    /**
     * generates a JavaScript containing methods for addind and removing products from cart
     * @return a string with the complete JS-script
     */
    public String generateCartScript() {
        String cartItemsContent = "";
        String cartPricesContent = "";
        boolean skippedHeader = false; //Set to true once the header has been skipped
        for (Item item:
             csv.items) {
            if (!skippedHeader) {
                skippedHeader = true;
                continue; //Skip header
            }
            cartItemsContent += "'" + item.getArtNr() + "' : 0, "; //I think js is alright with a trailing comma
            cartPricesContent += "'" + item.getArtNr() + "' : "+ item.getPrice() +", ";
        }

        HtmlParser cartReader = new HtmlParser("src/javascripts/cartScript.js");

        cartReader.set("cartItemsContent", cartItemsContent);
        cartReader.set("cartPricesContent", cartPricesContent);

        return cartReader.getString();
    }
}
