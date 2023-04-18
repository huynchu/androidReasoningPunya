package wvw.mobile.rules.explanation;

import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;

import wvw.mobile.rules.eyebrow.Reasoner;

/**
 * Provides user-friendly explanations
 */
public abstract class Explainer {

    private Model baseModel;
    private Reasoner reasoner;

    public Explainer(){}

    // TODO: Change this to be accomodating for multiple different types
    //       of explanations.
    public abstract String GetFullExplanation();

}
