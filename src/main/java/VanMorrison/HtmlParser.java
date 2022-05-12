package VanMorrison;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlParser {

    private String rawHtml;
    private List<String> matches; // All keys ( ${...} ) in order, even duplicates
    private Map<String, String> keys; // Dictionary of what value keys map to
    private String[] htmlParts; // The regular html parts that sits between the keys
    private String matchReg = "(?:(\\$\\{))(.*?)(?:\\})";
    // (?:\${)(.*?)(?:\})

    public HtmlParser(String path){
        matches = new ArrayList<>();
        keys = new HashMap<>();
        rawHtml = readHTML(path);
        parse();
    }

    private void parse(){
        // Splits the raw html text on the regex pattern that match on the key format (${...}).
        htmlParts = rawHtml.split(matchReg);

        // Retrieves all keys from the raw html
        Pattern pat = Pattern.compile(matchReg);
        Matcher mat = pat.matcher(rawHtml);

        while(mat.find()){
            String m = mat.group();
            keys.put(m.substring(2,m.length()-1), "");
            matches.add(m.substring(2,m.length()-1));
        }
    }

    public void set(String keyName, String value){
        if(keys.containsKey(keyName)){
            keys.put(keyName, value);
        }
    }

    /**
     * 
     * @return the string of the html document where the keys are replaced with their corresponding values
     */
    public String getString(){
        StringBuilder sb = new StringBuilder();
        // combining the html parts with the values
        for(int i = 0; i < Math.max(htmlParts.length, matches.size()); i++){
            if(i < htmlParts.length) sb.append(htmlParts[i]);
            if(i < matches.size()) sb.append(keys.get(matches.get(i)));
        }
        return sb.toString();
    }

    /**
     * Reads a file
     * @param filename the file 
     * @return a complete html file as a string 
     */

    private String readHTML(String filename){
        StringBuilder html = new StringBuilder();
        try {
            FileReader reader = new FileReader(filename);
            while (reader.ready()) html.append((char)reader.read());
        } catch (Exception e) {System.out.println(e);}
        return html.toString();
    }


}
