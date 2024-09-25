package gr.cite.intelcomp.evaluationworkbench.web.controllers;

import gr.cite.intelcomp.evaluationworkbench.web.config.help.HelpProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;

@RestController
@RequestMapping(path = "/api/help", produces = MediaType.APPLICATION_JSON_VALUE)
public class HelpController {
    private final HelpProperties helpProperties;

    public HelpController(HelpProperties helpProperties) {
        this.helpProperties = helpProperties;
    }

    @GetMapping("manual")
    public ResponseEntity getManual() throws IOException {

        File file = ResourceUtils.getFile(helpProperties.getManualPath());
        InputStream is = new FileInputStream(file);

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentLength(is.available());
        responseHeaders.setContentType(MediaType.TEXT_HTML);
        responseHeaders.set("Content-Disposition", "attachment;filename=manual.html");
        responseHeaders.set("Access-Control-Expose-Headers", "Content-Disposition");
        responseHeaders.get("Access-Control-Expose-Headers").add("Content-Type");

        byte[] content = new byte[is.available()];
        is.read(content);
        is.close();

        return new ResponseEntity<>(content, responseHeaders, HttpStatus.OK);
    }

    @GetMapping("faq")
    public ResponseEntity getFaq() throws IOException {

        File file = ResourceUtils.getFile(helpProperties.getFaqPath());
        InputStream is = new FileInputStream(file);

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentLength(is.available());
        responseHeaders.setContentType(MediaType.TEXT_HTML);
        responseHeaders.set("Content-Disposition", "attachment;filename=faq.html");
        responseHeaders.set("Access-Control-Expose-Headers", "Content-Disposition");
        responseHeaders.get("Access-Control-Expose-Headers").add("Content-Type");

        byte[] content = new byte[is.available()];
        is.read(content);
        is.close();

        return new ResponseEntity<>(content, responseHeaders, HttpStatus.OK);
    }
}
