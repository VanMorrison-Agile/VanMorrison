package Data;

import javax.mail.BodyPart;

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

    public BodyPart getBody(){
        return body;
    }
}
