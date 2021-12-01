/* Parser.java */
/* Generated By:JavaCC: Do not edit this line. Parser.java */
package modelCheckCTL.controller.ctl.Parser;

import java.io.IOException;
import java.util.Set;
import java.util.HashSet;
import modelCheckCTL.controller.types.modelCheckRelated.ModelCheckInputs;
import modelCheckCTL.controller.types.kripke.Kripke;
import modelCheckCTL.controller.types.kripke.State;
import modelCheckCTL.controller.types.kripke.Transition;

import static modelCheckCTL.utils.Utils.statesWithLabel;
import static modelCheckCTL.utils.Utils.union;
import static modelCheckCTL.utils.Utils.intersection;
import static modelCheckCTL.utils.Utils.subtract;
import static modelCheckCTL.utils.Utils.contains;
import static modelCheckCTL.utils.Utils.areEqual;

/**
* The Parser class does the heavy lifting for the model checking and contains the SAT algorithms. This Parser.java file is automatically generated from the Parser.jj file using JavaCC (using the terminal line `javacc Parser.jj`) inside this folder. That line also generates all the other files in this directory.
* The CTL parser rules approach from https://github.com/pedrogongora/antelope/blob/master/AntelopeCore/src/antelope/ctl/parser/CTLParser.jj, accessed 9/20
* All the individual methods like EX, EF, EG etc are modeled after the psuedocode SAT algorithms on page 227 of Logic In Computer Science by Michael Huth
* A compiler was used (the javaCC compiler specifically) so infinitely nested CTL formulas could be parsed correctly.
*/
public class Parser implements ParserConstants {

    /**
    * {@link Kripke} pulled out of the modelCheckInputs param. The Kripke is an object representation of the kripke text file (which is a representation of a directed graph) and contains the all the states, transitions and labels specified in the text file.
    */
    static Kripke kripke;

    /**
    * A {@link Set} of {@link State}s which has all the states in the "world" specified by the kripke. By the time this Parser is run, the Validator has already been run and has confirmed that the kripke contains no syntax errors.
    */
    static Set S;

    /**
   * A {@link String} of the CTL formula being checked. By the time this Parser is run, the Validator has already been run and has confirmed that the formula is well formed (contains no syntax errors).
    */
    static String formula;

    /**
    * The sole Parser constructor. It takes in a (@link ModelCheckInputs}, grabs the formula stream and sets the class properties from it.
    * @param {@link ModelCheckInputs} class has four properties - the first three are inputs: Kripke, the state to check and the model. The fourth, the modelStream, is generated within the constructor.
    */
    public Parser(ModelCheckInputs modelCheckInputs) {
          this(modelCheckInputs.getFormulaStream(), null); // Not sure what this call does exactly, but it's needed to avoid JavaCC throwing a null error. Compare to the autogenerated constructor in CtlValidator.java line 168
          kripke = modelCheckInputs.getKripke();
          formula = modelCheckInputs.getFormula();
          S = kripke.getStates();
    }

    /**
    * "Phi exists in a next state"
    * @param phi is a {@link Set} of {@link State}s about which we want to know which states satisfy EX
    * @return The {@link Set} of {@link State}s that hold for EX(phi)
    * @throws IOException
    */
    public static Set EX(Set phi) throws IOException {
        return preE(phi);
    }

    /**
    * "Phi exists in a future state"
    * @param phi is a {@link Set} of {@link State}s about which we want to know which states satisfy EF
    * @return The {@link Set} of {@link State}s that hold for EF(phi)
    * @throws ParseException
    * @throws IOException
    */
    public static Set EF(Set phi) throws IOException {
        Set tautology = S;
        return EU(tautology,phi);
    }

    /**
    * "Phi exists globally"
    * @param phi is a {@link Set} of {@link State}s about which we want to know which states satisfy EG
    * @return The {@link Set} of {@link State}s that hold for EG(phi)
    * @throws ParseException
    * @throws IOException
    */
    public static Set EG(Set phi) throws IOException  {
        return not(AF(not(phi)));
    }

    /**
    * "Phi is in all next states"
    * @param phi is a {@link Set} of {@link State}s about which we want to know which states satisfy AX
    * @return The {@link Set} of {@link State}s that hold for AX(phi)
    * @throws ParseException
    * @throws IOException
    */
    public static Set AX(Set phi) throws IOException {
        Set notPhi = not(phi);
        Set EXNotPhi = EX(notPhi);
        Set notEXNotPhi = not(EXNotPhi);
        return notEXNotPhi;
    }

    /**
    * "Phi is in the future in all paths from the current state"
    * @param phi is a {@link Set} of {@link State}s about which we want to know which states satisfy AF
    * @return The {@link Set} of {@link State}s that hold for AF(phi)
    * @throws ParseException
    * @throws IOException
    */
    public static Set AF(Set phi) throws IOException {
        Set X = S;
        Set Y = phi;
        while (!areEqual(X,Y)) {
            X = Y;
            Y = union(Y,preA(Y));
        }
        return Y;
    }

    /**
    * "Phi is globally present in paths from the current state"
    * @param phi is a {@link Set} of {@link State}s about which we want to know which states satisfy AG
    * @return The {@link Set} of {@link State}s that hold for AG(phi)
    * @throws ParseException
    * @throws IOException
    */
    public static Set AG(Set phi) throws IOException {
        return not(EF(not(phi)));
    }

    /**
    * "Phi until psi in a path from the current state"
    * @param phi {@link Set} of {@link State}s about which we want to know if they happen until psi
    * @param psi {@link Set} of {@link State}s about which we want to know if phi happens unil
    * @return The {@link Set} of {@link State}s that hold for E[phi U psi]
    * @throws ParseException
    * @throws IOException
    */
    public static Set EU(Set phi, Set psi) throws IOException {
        Set W = phi;
        Set X = S;
        Set Y = psi;
        while (!areEqual(X,Y)) {
            X = Y;
            Y = union(Y,intersection(W,preE(Y)));
        }
        return Y;
    }

    /**
    * "Phi until psi in all path from the current state"
    * @param phi {@link Set} of {@link State}s about which we want to know if they happen in all paths until psi
    * @param psi {@link Set} of {@link State}s about which we want to know if phi happens in all paths unil
    * @return The {@link Set} of {@link State}s that hold for A[phi U psi]
    * @throws ParseException
    * @throws IOException
    */
    public static Set AU(Set phi, Set psi) throws IOException {
        Set EGNotPsi = EG(not(psi));
        Set notPhiAndNotPsi = and(not(phi),not(psi));
        Set ENotPsiUNotPhiAndNotPsi = EU(not(psi),notPhiAndNotPsi);
        Set ENotPsiUNotPhiAndNotPsiOrEGNotPsi = or(ENotPsiUNotPhiAndNotPsi,EGNotPsi);
        Set notENotPsiUNotPhiAndNotPsiOrEGNotPsi = not(ENotPsiUNotPhiAndNotPsiOrEGNotPsi);
        return notENotPsiUNotPhiAndNotPsiOrEGNotPsi;
    }

    /**
    * The "or" set operator: "a or b"
    * @param a {@link Set} of {@link State}s we want to "or" with b
    * @param b {@link Set} of {@link State}s we want to "or" with a
    * @return The {@link Set} of {@link State}s in the union of a and b
    * @throws ParseException
    * @throws IOException
    */
    public static Set or(Set a, Set b) throws IOException {
        return union(a,b);
    }

    /**
    * The "and" set operator: "a and b"
    * @param a {@link Set} of {@link State}s we want to "and" with b
    * @param b {@link Set} of {@link State}s we want to "and" with a
    * @return The {@link Set} of {@link State}s in the intersection of a and b
    * @throws ParseException
    * @throws IOException
    */
    public static Set and(Set a, Set b) throws IOException {
        return intersection(a,b);
    }

    /**
    * The "not" set operator: "not a"
    * @param a {@link Set} of {@link State}s we want to use the "not" operator on
    * @return The {@link Set} of {@link State}s not in set a
    * @throws ParseException
    * @throws IOException
    */
    public static Set not(Set a) throws IOException {
        return subtract(S,a);
    }

    /**
    * The preE operation used in the CTL SAT algorithms.
    * @param phi {@link Set} of {@link State}s we want to use the preE operation on
    * @return The {@link Set} of {@link State}s that have a transition into phi (including states in phi that transition into other states in phi)
    * @throws ParseException
    * @throws IOException
    */
    public static Set preE(Set phi) throws IOException {
        Set S = kripke.getStates();
        Set preE = new HashSet();
        Set statesToCheck = S;
        for (Object stateToCheckObj : statesToCheck) {
            State stateToCheck = (State) stateToCheckObj;
            for (Object transitionToCheckObj : stateToCheck.getTransitions()) {
                Transition transitionToCheck = (Transition) transitionToCheckObj;
                State toState = transitionToCheck.getTo();
                if (contains(phi,toState)) {
                    preE.add(stateToCheck);
                }
            }
        }
        return preE;
    }

   /**
    * The preA operation used in the CTL SAT algorithms.
    * @param phi {@link Set} of {@link State}s we want to use the preA operation on
    * @return The {@link Set} of {@link State}s that only transition into phi (including states in phi that only transition into other states in phi)
    * @throws ParseException
    * @throws IOException
    */
    public static Set preA(Set phi) throws IOException {
        Set S = kripke.getStates();
        Set preA = new HashSet();
        Set statesToCheck = S;
        for (Object stateToCheckObj : statesToCheck) {
            Boolean isPreA = true;
            State stateToCheck = (State) stateToCheckObj;
            if (stateToCheck.getTransitions().size() > 0) {
                for (Object transitionToCheckObj : stateToCheck.getTransitions()) {
                    Transition transitionToCheck = (Transition) transitionToCheckObj;
                    State toState = transitionToCheck.getTo();
                    if (!contains(phi,toState)) {
                        isPreA = false;
                    }
                }
            } else {
                isPreA = false;
            }
            if (isPreA) {
                preA.add(stateToCheck);
            }
        }
        return preA;
    }

/**
* Root production. This is the method called to kick off the model checking
* @return {@link Set} of {@link State}s (if any) in the kripke which hold for the formula
*/
  final public Set Parse() throws ParseException, ParseException, IOException {Set f;
    f = formula(kripke.getStates());
    jj_consume_token(0);
{if ("" != null) return f;}
    throw new Error("Missing return statement in function");
}

/**
* A formula can be an expression or an expression with a binary operator
* @param {@link Set} of {@link State}s of which we want to see if they hold for the formula
* @return {@link Set} of {@link State}s (if any) which hold for the formula
*/
  final public Set formula(Set states) throws ParseException, ParseException, IOException {Set e;
    Set b = null;
    e = expression(states);
    switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
    case AND:
    case OR:
    case IMPLIES:{
      b = binaryOp(e,states);
      break;
      }
    default:
      jj_la1[0] = jj_gen;
      ;
    }
if (b != null) { {if ("" != null) return b;} }
            else { {if ("" != null) return e;} }
    throw new Error("Missing return statement in function");
}

/**
* An expression can be: an atom, a not operation, a formula with parentheses around it, a temporal expression or an AU or EU operation (binary temporal expressions)
* @param {@link Set} of {@link State}s of which we want to see if they hold for the expression
* @return {@link Set} of {@link State}s (if any) which hold for the expression
*/
  final public Set expression(Set states) throws ParseException, ParseException, IOException {Token t;
 Set f;
 Set e;
 Set b;
    switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
    case ATOM:{
      t = jj_consume_token(ATOM);
Set statesWithLabels = statesWithLabel(states, t);
            // System.out.println(statesWithLabels);
            {if ("" != null) return statesWithLabels;}
      break;
      }
    case NOT:{
      jj_consume_token(NOT);
      f = formula(states);
{if ("" != null) return not(f);}
      break;
      }
    case LPAREN:{
      jj_consume_token(LPAREN);
      f = formula(states);
      jj_consume_token(RPAREN);
{if ("" != null) return f;}
      break;
      }
    case AX:
    case AF:
    case AG:
    case EX:
    case EF:
    case EG:{
      f = temporalExpression(states);
{if ("" != null) return f;}
      break;
      }
    case E:{
      jj_consume_token(E);
      jj_consume_token(LPAREN);
      e = expression(states);
      jj_consume_token(U);
      b = expression(states);
      jj_consume_token(RPAREN);
{if ("" != null) return EU(e,b);}
      break;
      }
    case A:{
      jj_consume_token(A);
      jj_consume_token(LPAREN);
      e = expression(states);
      jj_consume_token(U);
      b = expression(states);
      jj_consume_token(RPAREN);
{if ("" != null) return AU(e,b);}
      break;
      }
    default:
      jj_la1[1] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
}

/**
* A binary operation can be: an and operation, an or operation or an implies operation
* @param subject is the {@link Set} of {@link State}s on the left side of the binary operator (ie, "a" in "a or b")
* @param states is the {@link Set} of {@link State}s of which we want to see which hold for the binary operation
* @return {@link Set} of {@link State}s (if any) which hold for the binary operation
*/
  final public Set binaryOp(Set subject, Set states) throws ParseException, ParseException, IOException {Set predicate;
    switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
    case AND:{
      jj_consume_token(AND);
      predicate = formula(states);
{if ("" != null) return and(subject,predicate);}
      break;
      }
    case OR:{
      jj_consume_token(OR);
      predicate = formula(states);
{if ("" != null) return or(subject,predicate);}
      break;
      }
    case IMPLIES:{
      jj_consume_token(IMPLIES);
      predicate = formula(states);
{if ("" != null) return or(not(subject),predicate);}  /* (not subject or predicate) */

      break;
      }
    default:
      jj_la1[2] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
}

/**
* A temporal expression can be the following operations: AX, AF, AG, EX, EF or EG.
* @param {@link Set} of {@link State}s of which we want to see if they hold for the temporal expression
* @return {@link Set} of {@link State}s (if any) which hold for the temporal expression
*/
  final public Set temporalExpression(Set s) throws ParseException, ParseException, IOException {Set e;
    switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
    case AX:{
      jj_consume_token(AX);
      e = expression(s);
{if ("" != null) return AX(e);}
      break;
      }
    case AF:{
      jj_consume_token(AF);
      e = expression(s);
{if ("" != null) return AF(e);}
      break;
      }
    case AG:{
      jj_consume_token(AG);
      e = expression(s);
{if ("" != null) return AG(e);}
      break;
      }
    case EX:{
      jj_consume_token(EX);
      e = expression(s);
{if ("" != null) return EX(e);}
      break;
      }
    case EF:{
      jj_consume_token(EF);
      e = expression(s);
{if ("" != null) return EF(e);}
      break;
      }
    case EG:{
      jj_consume_token(EG);
      e = expression(s);
{if ("" != null) return EG(e);}
      break;
      }
    default:
      jj_la1[3] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
}

  /** Generated Token Manager. */
  public ParserTokenManager token_source;
  SimpleCharStream jj_input_stream;
  /** Current token. */
  public Token token;
  /** Next token. */
  public Token jj_nt;
  private int jj_ntk;
  private int jj_gen;
  final private int[] jj_la1 = new int[4];
  static private int[] jj_la1_0;
  static {
	   jj_la1_init_0();
	}
	private static void jj_la1_init_0() {
	   jj_la1_0 = new int[] {0x1c0,0x15fe20,0x1c0,0x7e00,};
	}

  /** Constructor with InputStream. */
  public Parser(java.io.InputStream stream) {
	  this(stream, null);
  }
  /** Constructor with InputStream and supplied encoding */
  public Parser(java.io.InputStream stream, String encoding) {
	 try { jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
	 token_source = new ParserTokenManager(jj_input_stream);
	 token = new Token();
	 jj_ntk = -1;
	 jj_gen = 0;
	 for (int i = 0; i < 4; i++) jj_la1[i] = -1;
  }

  /** Reinitialise. */
  public void ReInit(java.io.InputStream stream) {
	  ReInit(stream, null);
  }
  /** Reinitialise. */
  public void ReInit(java.io.InputStream stream, String encoding) {
	 try { jj_input_stream.ReInit(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
	 token_source.ReInit(jj_input_stream);
	 token = new Token();
	 jj_ntk = -1;
	 jj_gen = 0;
	 for (int i = 0; i < 4; i++) jj_la1[i] = -1;
  }

  /** Constructor. */
  public Parser(java.io.Reader stream) {
	 jj_input_stream = new SimpleCharStream(stream, 1, 1);
	 token_source = new ParserTokenManager(jj_input_stream);
	 token = new Token();
	 jj_ntk = -1;
	 jj_gen = 0;
	 for (int i = 0; i < 4; i++) jj_la1[i] = -1;
  }

  /** Reinitialise. */
  public void ReInit(java.io.Reader stream) {
	if (jj_input_stream == null) {
	   jj_input_stream = new SimpleCharStream(stream, 1, 1);
	} else {
	   jj_input_stream.ReInit(stream, 1, 1);
	}
	if (token_source == null) {
 token_source = new ParserTokenManager(jj_input_stream);
	}

	 token_source.ReInit(jj_input_stream);
	 token = new Token();
	 jj_ntk = -1;
	 jj_gen = 0;
	 for (int i = 0; i < 4; i++) jj_la1[i] = -1;
  }

  /** Constructor with generated Token Manager. */
  public Parser(ParserTokenManager tm) {
	 token_source = tm;
	 token = new Token();
	 jj_ntk = -1;
	 jj_gen = 0;
	 for (int i = 0; i < 4; i++) jj_la1[i] = -1;
  }

  /** Reinitialise. */
  public void ReInit(ParserTokenManager tm) {
	 token_source = tm;
	 token = new Token();
	 jj_ntk = -1;
	 jj_gen = 0;
	 for (int i = 0; i < 4; i++) jj_la1[i] = -1;
  }

  private Token jj_consume_token(int kind) throws ParseException {
	 Token oldToken;
	 if ((oldToken = token).next != null) token = token.next;
	 else token = token.next = token_source.getNextToken();
	 jj_ntk = -1;
	 if (token.kind == kind) {
	   jj_gen++;
	   return token;
	 }
	 token = oldToken;
	 jj_kind = kind;
	 throw generateParseException();
  }


/** Get the next Token. */
  final public Token getNextToken() {
	 if (token.next != null) token = token.next;
	 else token = token.next = token_source.getNextToken();
	 jj_ntk = -1;
	 jj_gen++;
	 return token;
  }

/** Get the specific Token. */
  final public Token getToken(int index) {
	 Token t = token;
	 for (int i = 0; i < index; i++) {
	   if (t.next != null) t = t.next;
	   else t = t.next = token_source.getNextToken();
	 }
	 return t;
  }

  private int jj_ntk_f() {
	 if ((jj_nt=token.next) == null)
	   return (jj_ntk = (token.next=token_source.getNextToken()).kind);
	 else
	   return (jj_ntk = jj_nt.kind);
  }

  private java.util.List<int[]> jj_expentries = new java.util.ArrayList<int[]>();
  private int[] jj_expentry;
  private int jj_kind = -1;

  /** Generate ParseException. */
  public ParseException generateParseException() {
	 jj_expentries.clear();
	 boolean[] la1tokens = new boolean[21];
	 if (jj_kind >= 0) {
	   la1tokens[jj_kind] = true;
	   jj_kind = -1;
	 }
	 for (int i = 0; i < 4; i++) {
	   if (jj_la1[i] == jj_gen) {
		 for (int j = 0; j < 32; j++) {
		   if ((jj_la1_0[i] & (1<<j)) != 0) {
			 la1tokens[j] = true;
		   }
		 }
	   }
	 }
	 for (int i = 0; i < 21; i++) {
	   if (la1tokens[i]) {
		 jj_expentry = new int[1];
		 jj_expentry[0] = i;
		 jj_expentries.add(jj_expentry);
	   }
	 }
	 int[][] exptokseq = new int[jj_expentries.size()][];
	 for (int i = 0; i < jj_expentries.size(); i++) {
	   exptokseq[i] = jj_expentries.get(i);
	 }
	 return new ParseException(token, exptokseq, tokenImage);
  }

  private boolean trace_enabled;

/** Trace enabled. */
  final public boolean trace_enabled() {
	 return trace_enabled;
  }

  /** Enable tracing. */
  final public void enable_tracing() {
  }

  /** Disable tracing. */
  final public void disable_tracing() {
  }

}
