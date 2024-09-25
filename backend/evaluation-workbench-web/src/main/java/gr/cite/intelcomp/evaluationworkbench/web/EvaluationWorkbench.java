package gr.cite.intelcomp.evaluationworkbench.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({
        "gr.cite.intelcomp.evaluationworkbench",
        "gr.cite.tools",
        "gr.cite.commons"})
@EntityScan({
        "gr.cite.intelcomp.evaluationworkbench.data"})
public class EvaluationWorkbench {

    public static void main(String[] args) {
        SpringApplication.run(EvaluationWorkbench.class, args);
    }

}
