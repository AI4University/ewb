package gr.cite.intelcomp.evaluationworkbench.model.cencorship;

import gr.cite.intelcomp.evaluationworkbench.convention.ConventionService;
import gr.cite.tools.data.censor.Censor;
import gr.cite.tools.fieldset.FieldSet;


public class BaseCensor implements Censor {

	protected final ConventionService conventionService;

	public BaseCensor(ConventionService conventionService){
		this.conventionService = conventionService;
	}

	protected Boolean isEmpty(FieldSet fields) {
		return fields == null || fields.isEmpty();
	}

	protected String asIndexerPrefix(String part){
		return this.conventionService.asPrefix(part);
	}
}
