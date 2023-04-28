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


    public static void runContextualExplanationTest(){
        // Set-up rules
        String rules = "[transitiveRule: (?a ex:equals ?b) (?b ex:equals ?c) -> (?a ex:equals ?c)]";

        // Create and set-up the Explainer
        Explainer explainer = new Explainer();
        explainer.Model(ModelFactory.getTransitiveBaseModel());
        explainer.Rules(rules);

        // Generate the contextual explanation.
        String results = "Transitive_Explainer -- ContextualExplanation\n";
        results += explainer.GetShallowContextualExplanation(
                explainer.Model().getResource("A"),
                explainer.Model().getProperty("http://example.com/equals"),
                explainer.Model().getResource("D")
        );

        results += "\n";

        results += explainer.GetSimpleContextualExplanation(
                explainer.Model().getResource("A"),
                explainer.Model().getProperty("http://example.com/equals"),
                explainer.Model().getResource("D")
        );

        print(results + "\n");
    }

    public static void runCounterfactualExplanationTest(){

        // Set-up the Explainer
        Explainer explainer2 = new Explainer();
        explainer2.Model(ModelFactory.getAIMEBaseModel());
        explainer2.Rules(ModelFactory.getAIMERules());

        // Set-up the Additional Model needed to run the counterfactual explanation
        InfModel infModel = ModelFactory.getAIMEInfModel();

        Resource person  = infModel.getResource(ModelFactory.getPersonURI());
        Property totalSugars = infModel.getProperty("http://example.com/totalSugars");

        StmtIterator itr = infModel.listStatements(person, totalSugars, (RDFNode) null);

        // Use the Explainer to generate a counterfactual explanation.
        String res = "AIME_Explainer -- CounterfactualExplanation\n";
        while(itr.hasNext()) {
            Statement s = itr.next();
            res += explainer2.GetFullCounterfactualExplanation(s, ModelFactory.getAIMEBaseModelBanana());
        }
        print(res);
    }

    public static void runTraceBasedExplanationTest(){

        // Set up the Explainer
        String rules = "[transitiveRule: (?a ex:equals ?b) (?b ex:equals ?c) -> (?a ex:equals ?c)]";
        Explainer explainer = new Explainer();
        explainer.Model(ModelFactory.getTransitiveBaseModel());
        explainer.Rules(rules);

        // Demonstrate a trace-based explanation.
        String traceResponse = explainer.GetFullTracedBaseExplanation(explainer.Model().getResource("A"),
                explainer.Model().getProperty("http://example.com/equals"),
                explainer.Model().getResource("D"));
        print(traceResponse);
    }

    public static void run () {
        // Create model...
        PrintUtil.registerPrefix("ex", ModelFactory.getGlobalURI());

        runContextualExplanationTest();
        runCounterfactualExplanationTest();
        runTraceBasedExplanationTest();
    }
}