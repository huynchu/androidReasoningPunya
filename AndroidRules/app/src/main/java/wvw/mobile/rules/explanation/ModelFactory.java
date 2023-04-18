package wvw.mobile.rules.explanation;

import java.math.BigDecimal;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.reasoner.rulesys.GenericRuleReasoner;
import com.hp.hpl.jena.reasoner.rulesys.Rule;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.util.PrintUtil;
import com.hp.hpl.jena.vocabulary.RDF;

public class ModelFactory {
    // Global URI
    private static String ex  = "http://example.com/";

    // Related to AIME Tutorial Model.
    private static String rdfURI = RDF.getURI();
    private static String schemaURI = "http://schema.org/";
    private static String ateURI      = ex + "ate";
    private static String observationURI = schemaURI + "Observation";
    private static String weightURI = schemaURI + "weight";
    private static String unitURI = schemaURI + "unitText";
    private static String variableMeasuredURI = schemaURI + "variableMeasured";

    private static String foaf = "http://xmlns.com/foaf/0.1/";
    private static String personURI = foaf + "Person";
    private static String usdaURI = "http://idea.rpi.edu/heals/kb/usda-ontology#";

    public static String getGlobalURI() {
        return ex;
    }

    public static String getPersonURI() {
        return personURI;
    }

    public static String getObservavtionURI() {
        return observationURI;
    }

    public static Model getTransitiveBaseModel() {

        Model model = com.hp.hpl.jena.rdf.model.ModelFactory.createDefaultModel();

        // Constructs RDF
        Resource A  = model.createResource("A");
        Resource B  = model.createResource("B");
        Resource C  = model.createResource("C");
        Resource D  = model.createResource("D");

        Property equals = model.createProperty(ex + "equals");

        A.addProperty(equals, B);
        B.addProperty(equals, C);
        C.addProperty(equals, D);

        return model;
    }

    public static InfModel getTransitiveInfModel() {
        Model model = getTransitiveBaseModel();
        PrintUtil.registerPrefix("ex", ex);
        // Create Rules:
        // See https://jena.apache.org/documentation/inference/#RULEsyntax for specifics on rule syntax.
        String rules = "[transitiveRule: (?a ex:equals ?b) (?b ex:equals ?c) -> (?a ex:equals ?c)]";

        // Construct the reasoner and new model to include reasoning over the rules.
        Reasoner reasoner = new GenericRuleReasoner(Rule.parseRules(rules));
        reasoner.setDerivationLogging(true);
        return com.hp.hpl.jena.rdf.model.ModelFactory.createInfModel(reasoner, model);
    }

    public static Model getAIMEBaseModel() {

        // creating the model used in AIME tutorial with Person, Observe:eat usda:Apple
        Model model = com.hp.hpl.jena.rdf.model.ModelFactory.createDefaultModel();

        // create the resource
        Resource user = model.createResource(personURI);
        Resource observation = model.createResource(observationURI);
        // Apple usda food, User recored food weight, weight unit
        Resource usdaFood = model.createResource("http://idea.rpi.edu/heals/kb/usda#09003"); // An Apple
        Literal foodWeight = model.createTypedLiteral(new BigDecimal(200)); // xsd:decimal
        String unitText   = "g";
        // add the property
        user.addProperty(model.createProperty(ateURI), observation);
        user.addProperty(model.createProperty(rdfURI + "type"), user);


        observation.addLiteral(model.createProperty(weightURI), foodWeight);
        observation.addProperty(model.createProperty(unitURI), unitText);
        observation.addProperty(model.createProperty(variableMeasuredURI), usdaFood);
        usdaFood.addLiteral(model.createProperty(usdaURI + "sugar"), model.createTypedLiteral(new BigDecimal(10.39)));

        // set prefix for better printing
        model.setNsPrefix( "schema", schemaURI );
        model.setNsPrefix( "ex", ex );
        model.setNsPrefix( "foaf", foaf );
        model.setNsPrefix( "usda", usdaURI );
        return model;
    }

    public static InfModel getAIMEInfModel() {
        Model baseModel = getAIMEBaseModel();

        // Create the ruleset from AIME tutorial
        PrintUtil.registerPrefix("schema", schemaURI);
        PrintUtil.registerPrefix("usda", usdaURI);
        PrintUtil.registerPrefix("rdf", rdfURI);
        PrintUtil.registerPrefix("ex", ex);
        PrintUtil.registerPrefix("foaf", foaf);

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

        Reasoner reasoner = new GenericRuleReasoner(Rule.parseRules(rules));

        reasoner.setDerivationLogging(true);
        return com.hp.hpl.jena.rdf.model.ModelFactory.createInfModel(reasoner, baseModel);
    }

    public static Model getAIMEBaseModelMultipleFood() {

        // creating the model used in AIME tutorial with Person, Observe:eat usda:Apple
        Model model = com.hp.hpl.jena.rdf.model.ModelFactory.createDefaultModel();

        // create the resource
        Resource user = model.createResource(personURI);
        Resource observation1 = model.createResource(observationURI);
        Resource observation2 = model.createResource(observationURI);
        user.addLiteral(model.createProperty(ex + "totalSugars"), model.createTypedLiteral(new BigDecimal(0)));

        // usda food, User recored food weight, weight unit
        Resource usdaApple = model.createResource("http://idea.rpi.edu/heals/kb/usda#09003");
        Resource usdaBanana = model.createResource("http://idea.rpi.edu/heals/kb/usda#09040");
        Literal appleWeight = model.createTypedLiteral(new BigDecimal(83)); // xsd:decimal
        Literal bananaWeight = model.createTypedLiteral(new BigDecimal(118)); // xsd:decimal
        String unitText   = "g";
        // add the property
        user.addProperty(model.createProperty(ateURI), observation1);
        user.addProperty(model.createProperty(ateURI), observation2);
        user.addProperty(model.createProperty(rdfURI + "type"), user);

        observation1.addProperty(model.createProperty(variableMeasuredURI), usdaApple);
        usdaApple.addLiteral(model.createProperty(usdaURI + "sugar"), model.createTypedLiteral(new BigDecimal(10)));
        usdaApple.addLiteral(model.createProperty(weightURI), appleWeight);

        observation2.addProperty(model.createProperty(variableMeasuredURI), usdaBanana);
        usdaBanana.addLiteral(model.createProperty(usdaURI + "sugar"), model.createTypedLiteral(new BigDecimal(12)));
        usdaBanana.addLiteral(model.createProperty(weightURI), bananaWeight);

        // set prefix for better printing
        model.setNsPrefix( "schema", schemaURI );
        model.setNsPrefix( "ex", ex );
        model.setNsPrefix( "foaf", foaf );
        model.setNsPrefix( "usda", usdaURI );
        return model;
    }
    public static InfModel getAIMEInfModelMultipleFood() {
        Model baseModel = getAIMEBaseModelMultipleFood();

        // Create the ruleset from AIME tutorial
        PrintUtil.registerPrefix("schema", schemaURI);
        PrintUtil.registerPrefix("usda", usdaURI);
        PrintUtil.registerPrefix("rdf", rdfURI);
        PrintUtil.registerPrefix("ex", ex);
        PrintUtil.registerPrefix("foaf", foaf);

        // https://jena.apache.org/documentation/inference/#RULEsyntax for specifics on rule syntax
        String rule1 = "[rule1: ";
        rule1 += "( ?var schema:variableMeasured ?foodstuff ) ";
        rule1 += "( ?foodstuff schema:weight ?weight ) ";
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
        String rule3 = "[rule3: ";
//        rule3 += "( ?user rdf:type foaf:Person) ";
//        rule3 += "listMapAsObject(?s, ?p ?l) ";
//        rule3 += "print(?l)";
        rule3 += "]";

        String rules = rule1 + " " + rule2 + " " + rule3;

        Reasoner reasoner = new GenericRuleReasoner(Rule.parseRules(rules));

        reasoner.setDerivationLogging(true);
        InfModel infModel = com.hp.hpl.jena.rdf.model.ModelFactory.createInfModel(reasoner, baseModel);
        infModel.write(System.out);
        return infModel;
    }
}


