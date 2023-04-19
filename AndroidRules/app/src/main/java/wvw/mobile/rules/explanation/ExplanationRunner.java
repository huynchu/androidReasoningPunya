package wvw.mobile.rules.explanation;

import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.reasoner.Derivation;
import com.hp.hpl.jena.reasoner.rulesys.RuleDerivation;
import com.hp.hpl.jena.util.PrintUtil;

public class ExplanationRunner {


    public static void print(String message) {
        Log.d("Explanation-Runner", message);
    }

    /**
     * Shows a demonstration of how the contrastive explanation works.
     */
    public static void runContrastiveExplanationExample(){
        InfModel infModel = ModelFactory.getAIMEInfModel();
        String rule1 = "[rule1: ";
        rule1 += "( ?var schema:weight ?weight ) ";
        rule1 += "( ?var schema:variableMeasured ?foodstuff ) ";
        rule1 += "( ?foodstuff usda:sugar ?sugarsPer100g ) ";
        rule1 += "quotient(?weight, '100.0'^^http://www.w3.org/2001/XMLSchema#float, ?scaledWeight) ";
        rule1 += "product(?scaledWeight, ?sugarsPer100g, ?sugars) ";
        rule1 += "-> (?var ex:sugars ?sugars)";
        rule1 += "]";
        String rule2 = "[rule2: ";
        rule2 += "( ?user rdf:type foaf:Person) ";
        rule2 += "( ?user ex:ate ?food) ";
        rule2 += "( ?food ex:sugars ?sugar) ";
        rule2 += "sum(?sugar, '0.0'^^http://www.w3.org/2001/XMLSchema#float, ?totalSugars) ";
        rule2 += "-> ( ?user ex:totalSugars ?totalSugars ) ";
        rule2 += "]";

        String rules = rule1 + " " + rule2;

        Explainer AIME_Explainer = new Explainer();
        AIME_Explainer.Model(ModelFactory.getAIMEBaseModel());
        AIME_Explainer.Rules(rules);

        Resource person  = infModel.getResource(ModelFactory.getPersonURI());
        Property totalSugars = infModel.getProperty("http://example.com/totalSugars");
        Property sugars = infModel.getProperty("http://example.com/sugars");
        Resource observation = infModel.getResource(ModelFactory.getObservavtionURI());
        RDFNode value = null;

        StmtIterator itr = infModel.listStatements(person, totalSugars, value);
        while(itr.hasNext()) {
            Statement s = itr.next();
            print("AIME_Explainer -- ContrastiveExplanation");
            print(AIME_Explainer.GetFullContrastiveExplanation(s, ModelFactory.getAIMEBaseModel()));
        }
    }

    public static void runContextualExplanationExample(){
        // Create model...
        PrintUtil.registerPrefix("ex", ModelFactory.getGlobalURI());
        String rules = "[transitiveRule: (?a ex:equals ?b) (?b ex:equals ?c) -> (?a ex:equals ?c)]";
        Explainer explainer = new Explainer();
        explainer.Model(ModelFactory.getTransitiveBaseModel());
        explainer.Rules(rules);


        String results = explainer.GetShallowContextualExplanation(
                explainer.Model().getResource("A"),
                explainer.Model().getProperty("http://example.com/equals"),
                explainer.Model().getResource("D")
        );
        print("\n" + results);

        results = explainer.GetSimpleContextualExplanation(
                explainer.Model().getResource("A"),
                explainer.Model().getProperty("http://example.com/equals"),
                explainer.Model().getResource("D")
        );

        print("\n" + results);

    }

    public static void run () {
        //runContrastiveExplanationExample();
        runContextualExplanationExample();
    }
}