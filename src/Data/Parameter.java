package Data;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.ContentDisposition;
import javax.mail.internet.MimePart;

public class Parameter{
    BodyPart body;
    byte[] data;

    public Parameter(BodyPart body, byte[] data){
        this.body = body;
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }

    public String getDataAsString(){ return new String(data); }

    public String getFileName(){
        try {
            return body.getFileName();
        } catch (MessagingException e) {
            e.printStackTrace();
            return "READ FILENAME ERROR";
        }
    }

    public BodyPart getBody(){
        return body;
    }
}
