package p5.compilerserver.controller;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.*;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import p5.compilerserver.model.Code;

@CrossOrigin
@RestController
@EnableAutoConfiguration
public class Controller {
    
    @PostMapping("/compile")
    public String handleRequest(@RequestBody Code code) throws IOException {
        writeToFile(code.getCode());
        return compile();
    }

    public static void writeToFile(String code) throws IOException {
        FileWriter myWriter = new FileWriter("compiler-server/src/main/java/p5/compilerserver/compile/main.cpp");
        myWriter.write(code);
        myWriter.close();
    }

    String compile() {
        try {
            String res = "";
            Process process = Runtime.getRuntime().exec("docker build compiler-server/src/main/java/p5/compilerserver/compile/ -t gcc");
            res += getCmdOutput(process).toString();
            
            res += "\n---------Output of program:---------\n\n";
            process = Runtime.getRuntime().exec("docker run --rm gcc:latest"); // TODO: Async
            res += getCmdOutput(process).toString();
            return res;
          } catch (IOException e) {
            e.printStackTrace();
            return "An error occurred.";
          }
    }

    public static StringBuilder getCmdOutput(Process process) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        StringBuilder strBuilder = new StringBuilder();
        String line = "";
        while ((line = reader.readLine()) != null) {
            strBuilder.append(line + "\n");
        }
        return strBuilder;
    }
}
