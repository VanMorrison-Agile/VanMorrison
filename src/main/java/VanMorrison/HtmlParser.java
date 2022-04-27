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
    private List<String> matches;
    private Map<String, String> keys;
    private String[] htmlParts;
    private String matchReg = "(?:(\\$\\{))(.*?)(?:\\})";
    // (?:\${)(.*?)(?:\})

    public HtmlParser(String path){
        matches = new ArrayList<>();
        keys = new HashMap<>();
        rawHtml = readHTML(path);
        parse();
    }

    private void parse(){
        System.out.println("matches:");
        htmlParts = rawHtml.split(matchReg);

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

    public String getString(){
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < htmlParts.length-1; i++){
            sb.append(htmlParts[i]);
            sb.append(keys.get(matches.get(i)));
        }
        sb.append(htmlParts[htmlParts.length-1]);
        return sb.toString();
    }

    private String readHTML(String filename){
        StringBuilder html = new StringBuilder();
        try {
            FileReader reader = new FileReader(filename);
            while (reader.ready()) html.append((char)reader.read());
        } catch (Exception e) {System.out.println(e);}
        return html.toString();
    }
}
