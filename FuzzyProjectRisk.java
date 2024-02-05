// Compile: javac -cp ".;./lib/*" FuzzyProjectRisk.java
// Run: java -cp ".;./lib/*" FuzzyProjectRisk

import com.fuzzylite.*;
import com.fuzzylite.defuzzifier.*;
import com.fuzzylite.activation.*;
import com.fuzzylite.norm.s.*;
import com.fuzzylite.norm.t.*;
import com.fuzzylite.rule.*;
import com.fuzzylite.term.*;
import com.fuzzylite.variable.*;


class FuzzyProjectRisk{

    // Define our FuzzyLite objects
    private Engine engine;
    private InputVariable project_funding;
    private InputVariable project_staffing;
    private OutputVariable risk;
	private RuleBlock ruleBlock;

    public FuzzyProjectRisk(){
        // Constructor
        engine = new Engine();
        engine.setName("Fuzzy Project Risk");

        // Create our input variable project_funding
        project_funding = new InputVariable();
        project_funding.setEnabled(true);
        project_funding.setName("project_funding");
        project_funding.setRange(0.00, 100.00);
        // Add each term to the variable
        project_funding.addTerm(new Trapezoid("inadequate",0,0,20,30));
        project_funding.addTerm(new Triangle("marginal", 20, 50, 80));
        project_funding.addTerm(new Trapezoid("adequate", 60,80,100,100));
        engine.addInputVariable(project_funding);

        // Create our input variable project_staffing
        project_staffing = new InputVariable();
        project_staffing.setEnabled(true);
        project_staffing.setName("project_staffing");
        project_staffing.setRange(0.00, 100.00);
        // Add each term to the variable
        project_staffing.addTerm(new Trapezoid("small",0,0,30,57));
        project_staffing.addTerm(new Trapezoid("large", 40,60,100,100));
        engine.addInputVariable(project_staffing);

        // Create our output variable
        risk = new OutputVariable();
        risk.setEnabled(true);
        risk.setName("risk");
        risk.setRange(0.00, 100.00);
        risk.fuzzyOutput().setAggregation(new Maximum());
        risk.setDefuzzifier(new Centroid(100));
        risk.setDefaultValue(0.000);
        risk.setLockPreviousValue(false);
        // Add the terms
        risk.addTerm(new Trapezoid("low", 0,0,20,40));
        risk.addTerm(new Triangle("normal",20, 50, 80));
        risk.addTerm(new Trapezoid("high", 60, 80, 100, 100));
        engine.addOutputVariable(risk);

        // Add rules
        ruleBlock = new RuleBlock();
        ruleBlock.setEnabled(true);
        ruleBlock.setName("rule block");
        ruleBlock.setConjunction(new Minimum());
        ruleBlock.setDisjunction(new Maximum());
        ruleBlock.setImplication(new Minimum());
        ruleBlock.setActivation(new General());

        // Start writing rules
        // RULE 1
        ruleBlock.addRule(Rule.parse("if (project_funding is adequate or project_staffing is small) then risk is low", engine));
        // RULE 2
        // RULE 3

        engine.addRuleBlock(ruleBlock);
    }

    public double evaluate(double funding, double staffing){
        project_funding.setValue(funding);
        project_staffing.setValue(staffing);
        engine.process();
        risk.defuzzify();
        return (double)(risk.getValue());
    }

    public static void main(String[] args) {

        FuzzyProjectRisk fuzzyRisk = new FuzzyProjectRisk();

        double funding_input = 25.0;
        double staffing_input = 55.0;

        double result = fuzzyRisk.evaluate(funding_input, staffing_input);

        System.out.printf("Result: Risk is = %.2f \n",result);
        
    }
}