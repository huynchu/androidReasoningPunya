package wvw.mobile.rules.explanation;

import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;

import wvw.mobile.rules.eyebrow.Reasoner;

/**
 * The <code>Explainer</code> component produces user-friendly
 * explanations of recommendations derived from the <code>Reasoner</code>
 */
/*
@DesignerComponent(version = PunyaVersion.EXPLAINER_COMPONENT_VERSION,
    nonVisible = true,
    category = ComponentCategory.LINKEDDATA,
)
@SimpleObject
 */
public class Explainer {

    private Model baseModel;
    private Reasoner reasoner;
    private String rules;

    /**
     * Creates a new Explainer component.
     */
    public Explainer(){}

    /// region Properties

    public Model Model(){
        return this.baseModel;
    }

    public void Model(Model model){
        this.baseModel = model;
    }

    public Reasoner Reasoner(){
        return this.reasoner;
    }

    public void Reasoner(Reasoner reasoner){
        this.reasoner = reasoner;
    }

    public String Rules(){
        return this.rules;
    }

    public void Rules(String rules){
        this.rules = rules;
    }

    ///endregion
    ///region Methods

    public String GetFullContrastiveExplanation(){
        return "Not Implemented Yet";
    }

    public String GetFullContextualExplanation() {
        return "Not Implemented Yet";
    }

}
