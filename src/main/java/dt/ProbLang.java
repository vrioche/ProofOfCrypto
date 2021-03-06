package dt;

import api.AntlrAPI;
import business.Configuration;
import business.Distribution;
import business.Memory;
import business.Program;
import exceptions.ErrorSyntaxException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by lucas on 13/01/17.
 */
public class ProbLang {

    public String s2Program = "q:=5;b:={0,1};kPrimeD:={1,2,3};kPrimeE:=4^kPrimeD%q;kE:=kPrimeE;kD:=kPrimeD;";

    public AntlrAPI api = new AntlrAPI(s2Program);

    //DEBUG
    public AntlrAPI api2 = new AntlrAPI("x:=1;");

    public ProbLang(String s2Program) {
        this.s2Program = s2Program;
        this.api = new AntlrAPI(this.s2Program);
    }

    //A list with all the available rules
    public static List<ARule> allRules = new ArrayList<ARule>() {{
        add(new AffectationRule("AFFECTATION"));
        add(new WhileRule("WHILE"));
        add(new IfRule("IF"));
    }};

    /**
     * Return the rule corresponding to the name in param
     *
     * @param name
     * @return
     */
    public static ARule getRuleToApply(String name) {
        for (ARule r : allRules) {
            if (r.hasSameRuleWord(name))
                return r;
        }
        //default
        return null;
    }

    //Je fabrique la distribution initiale
    private Distribution init() {
        //Create memory
        Memory m = new Memory();
        //Create Program
        Program p = new Program();
        //Create initial distribution
        Distribution d0 = new Distribution();

        //Get all initial tokens
        Set<String> varTokens = api.getVarTokens();
        //Iterate trought the tokens in order to add the var in memory
        for (String t : varTokens) {
            m.getVarAndVal().put(t, 0);
        }
        //Add the program code to the program object
        p.setInstructions(api.getProgramRoot());
        //Create configuration with the program and the memory
        Configuration c = new Configuration(p, m);
        //Add the configuration to the initial distribution
        //Because it's the initial distribution, the probability of this configuration is 1
        d0.getConfigWithProbability().put(c, 1.0);
        //return distribution
        return d0;
    }

    public Distribution getDF() {
        try {
            //First check if the program is correct
            api.launchValidationProcess();
            //Create the initial distribution
            Distribution d0 = init();
            //calculate the final distribution
            return api.launchMarkovProcess(d0);
        } catch (ErrorSyntaxException e) {
            e.printStackTrace();
        }
        //Default return
        return null;
    }
}
