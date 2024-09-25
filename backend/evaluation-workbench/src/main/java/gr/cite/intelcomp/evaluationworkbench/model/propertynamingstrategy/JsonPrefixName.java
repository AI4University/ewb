package gr.cite.intelcomp.evaluationworkbench.model.propertynamingstrategy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
public @interface JsonPrefixName {

    String value();
}
