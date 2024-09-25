package gr.cite.intelcomp.evaluationworkbench.web.config.help;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

@ConfigurationProperties(prefix = "help")
public class HelpProperties {

    private final String manualPath;
    private final String faqPath;

    @ConstructorBinding
    public HelpProperties(String manualPath, String faqPath) {
        this.manualPath = manualPath;
        this.faqPath = faqPath;
    }

    public String getManualPath() {
        return manualPath;
    }

    public String getFaqPath() {
        return faqPath;
    }
}
