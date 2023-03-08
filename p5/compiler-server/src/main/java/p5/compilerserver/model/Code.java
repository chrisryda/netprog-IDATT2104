package p5.compilerserver.model;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Code {
    
    private String code;
    
    @JsonCreator
    public Code(@JsonProperty("code") String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }
}
