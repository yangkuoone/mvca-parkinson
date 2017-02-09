/* Generated By:JavaCC: Do not edit this line. BifParser.java */
package org.latlab.io.io.bif;

import org.latlab.graph.Edge;
import org.latlab.io.AbstractParser;
import org.latlab.io.BeliefNodeProperty;
import org.latlab.io.BeliefNodeProperty.ConnectionConstraint;
import org.latlab.io.DependencyEdgeProperty;
import org.latlab.io.Properties;
import org.latlab.io.bif.BifParserTokenManager;
import org.latlab.model.BayesNet;
import org.latlab.model.BeliefNode;
import org.latlab.util.Function;
import org.latlab.util.Variable;

import java.awt.*;
import java.util.ArrayList;

public class BifParser extends AbstractParser implements BifParserConstants {
        public void parse(BayesNet network) throws org.latlab.io.ParseException {
                try {
                        properties = new Properties();
                        Network(network);
                }
                catch (Throwable e) {
                        throw new org.latlab.io.ParseException(e);
                }
        }


        public Properties getProperties() {
                return properties;
        }

        private static int computeNumberOfCells(ArrayList<Variable> variables) {
                if (variables.size() == 0)
                        return 0;

                int product = 1;
                for (Variable v : variables) {
                        product *= v.getStates().size();
                }

                return product;
        }

        private Properties properties;

  final public void Network(BayesNet network) throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case UTF8_INDICATOR:
      jj_consume_token(UTF8_INDICATOR);
      break;
    default:
      jj_la1[0] = jj_gen;
      ;
    }
    NetworkDeclaration(network);
    label_1:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case VARIABLE:
        ;
        break;
      default:
        jj_la1[1] = jj_gen;
        break label_1;
      }
      VariableDeclaration(network);
    }
    label_2:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case EDGE:
        ;
        break;
      default:
        jj_la1[2] = jj_gen;
        break label_2;
      }
      EdgeDeclaration(network);
    }
    label_3:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case PROBABILITY:
        ;
        break;
      default:
        jj_la1[3] = jj_gen;
        break label_3;
      }
      ProbabilityDeclaration(network);
    }
    jj_consume_token(0);
  }

  final public void NetworkDeclaration(BayesNet network) throws ParseException {
  String name = null;
    jj_consume_token(NETWORK);
    name = IdentifierValue();
    jj_consume_token(46);
    jj_consume_token(47);
                                                   network.setName(name);
  }

  final public void VariableDeclaration(BayesNet network) throws ParseException {
        String name;
        ArrayList<String> states;
        BeliefNode node = null;
    jj_consume_token(VARIABLE);
    name = IdentifierValue();
    jj_consume_token(46);
    states = VariableStatesDeclaration();
                  node = network.addNode(new Variable(name, states));
    label_4:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case PROPERTY:
        ;
        break;
      default:
        jj_la1[4] = jj_gen;
        break label_4;
      }
      VariableProperty(node, properties);
    }
    jj_consume_token(47);
  }

  final public void EdgeDeclaration(BayesNet network) throws ParseException {
  String head;
  String tail;
  BeliefNode headNode = null;
  BeliefNode tailNode = null;
  Edge edge = null;
    jj_consume_token(EDGE);
    jj_consume_token(48);
    jj_consume_token(HEAD);
    jj_consume_token(49);
    head = IdentifierValue();
    jj_consume_token(50);
    jj_consume_token(TAIL);
    jj_consume_token(49);
    tail = IdentifierValue();
    jj_consume_token(50);
                  headNode = (BeliefNode)network.getNode(head);
                  tailNode = (BeliefNode)network.getNode(tail);
                  edge = network.addEdge(headNode,tailNode);
    jj_consume_token(51);
    jj_consume_token(46);
    label_5:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case 48:
        ;
        break;
      default:
        jj_la1[5] = jj_gen;
        break label_5;
      }
      EdgeProperty(edge, properties);
    }
    jj_consume_token(47);
  }

  final public void ProbabilityDeclaration(BayesNet network) throws ParseException {
        ArrayList<Variable> variables = new ArrayList<Variable>();
        String childName = null;
        String parentName = null;
        BeliefNode node = null;
        Function function = null;
    jj_consume_token(PROBABILITY);
    jj_consume_token(48);
    childName = IdentifierValue();
                node = (BeliefNode) network.getNode(childName);
                variables.add(node.getVariable());
    label_6:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case IDENTIFIER:
      case 52:
      case 53:
        ;
        break;
      default:
        jj_la1[6] = jj_gen;
        break label_6;
      }
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case 52:
      case 53:
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case 52:
          jj_consume_token(52);
          break;
        case 53:
          jj_consume_token(53);
          break;
        default:
          jj_la1[7] = jj_gen;
          jj_consume_token(-1);
          throw new ParseException();
        }
        break;
      default:
        jj_la1[8] = jj_gen;
        ;
      }
      parentName = IdentifierValue();
                        BeliefNode parentNode = (BeliefNode) network.getNode(parentName);
                        if(node.getEdge(parentNode)== null)
                                network.addEdge(node, parentNode);
                        variables.add(parentNode.getVariable());
    }
    jj_consume_token(51);
    jj_consume_token(46);
    function = ProbabilityDefinition(variables);
                  node.setCpt(function);
    jj_consume_token(47);
  }

/**
	Reads a probability definition for a given node.
	The first variable in the argument is the node variable, and the
	remaining variables (if exist) are the parent variables.
	The probabilities specified in the file depend on the order 
	of these variables.
*/
  final public Function ProbabilityDefinition(ArrayList<Variable> variables) throws ParseException {
        double probability;
        Function function = Function.createFunction(variables);
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case TABLE:
      jj_consume_token(TABLE);
                int numberOfCells = computeNumberOfCells(variables);
                ArrayList<Double> cells = new ArrayList<Double>(numberOfCells);
      label_7:
      while (true) {
        probability = NonNegativeFloat();
                  cells.add(probability);
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case NON_NEGATIVE_FLOAT:
          ;
          break;
        default:
          jj_la1[9] = jj_gen;
          break label_7;
        }
      }
      jj_consume_token(50);
                function.setCells(variables, cells);
                {if (true) return function;}
      break;
    case 48:
      label_8:
      while (true) {
        jj_consume_token(48);
                        ArrayList<Integer> states =
                                new ArrayList<Integer>(variables.size());
                        states.add(0);  // start the child state from zero
                        int stateIndex;
        label_9:
        while (true) {
          // the states.size() indicate the index of the next parent
                                  stateIndex = State(variables.get(states.size()));
                                states.add(stateIndex);
          switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
          case IDENTIFIER:
            ;
            break;
          default:
            jj_la1[10] = jj_gen;
            break label_9;
          }
        }
        jj_consume_token(51);
                        if (states.size() != variables.size())
                                {if (true) throw new ParseException(
                                        "The parent states are not completed specified.");}
        label_10:
        while (true) {
          probability = NonNegativeFloat();
                        function.setCell(variables, states, probability);
                        states.set(0, states.get(0) + 1);
          switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
          case NON_NEGATIVE_FLOAT:
            ;
            break;
          default:
            jj_la1[11] = jj_gen;
            break label_10;
          }
        }
                        if (states.get(0) != variables.get(0).getStates().size())
                                {if (true) throw new ParseException(
                                        "The probabilities are not specified for all of the states.");}
        jj_consume_token(50);
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case 48:
          ;
          break;
        default:
          jj_la1[12] = jj_gen;
          break label_8;
        }
      }
                {if (true) return function;}
      break;
    default:
      jj_la1[13] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

  final public ArrayList<String> VariableStatesDeclaration() throws ParseException {
        int numberOfStates;
        String state;
        ArrayList<String> states;
    jj_consume_token(TYPE);
    jj_consume_token(DISCRETE);
    jj_consume_token(54);
    numberOfStates = NonNegativeInteger();
    jj_consume_token(55);
                  states = new ArrayList<String>(numberOfStates);
    jj_consume_token(46);
    label_11:
    while (true) {
      state = IdentifierValue();
                                       states.add(state);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case 52:
        jj_consume_token(52);
        break;
      default:
        jj_la1[14] = jj_gen;
        ;
      }
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case IDENTIFIER:
        ;
        break;
      default:
        jj_la1[15] = jj_gen;
        break label_11;
      }
    }
    jj_consume_token(47);
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case 50:
      jj_consume_token(50);
      break;
    default:
      jj_la1[16] = jj_gen;
      ;
    }
                if (numberOfStates != states.size()) {
                        String message = String.format(
                                "The number of states (%d)" +
                                        " does not match that declared (%d)",
                                states.size(), numberOfStates);
                        {if (true) throw new ParseException(message);}
                }
                {if (true) return states;}
    throw new Error("Missing return statement in function");
  }

  final public void VariableProperty(BeliefNode node, Properties properties) throws ParseException {
  BeliefNodeProperty property = properties.getBeliefNodeProperty(node);
    jj_consume_token(PROPERTY);
    VariablePropertyString(property);
    jj_consume_token(PROPERTY_END_CHAR);
  }

  final public void EdgeProperty(Edge edge, Properties properties) throws ParseException {
  int x;
  int y;
  DependencyEdgeProperty property = properties.getDependencyEdgeProperty(edge);
    jj_consume_token(48);
    x = NonNegativeInteger();
    jj_consume_token(52);
    y = NonNegativeInteger();
    jj_consume_token(51);
                property.addPoint(new Point(x,y));
  }

// Reads a property string from the input and put it into the property object.
  final public void VariablePropertyString(BeliefNodeProperty property) throws ParseException {
        int x;
        int y;
        int angle;
        BeliefNodeProperty.FrameType frame;
        String text;
        Color color;
    jj_consume_token(PROPERTY_DOUBLE_QUOTE);
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case POSITION:
      jj_consume_token(POSITION);
      jj_consume_token(PROPERTY_EQUAL);
      jj_consume_token(PROPERTY_LPARENTHESE);
      x = NonNegativeInteger();
      jj_consume_token(PROPERTY_COMMA);
      y = NonNegativeInteger();
      jj_consume_token(PROPERTY_RPARENTHESE);
                property.setPosition(new Point(x,y));
      break;
    case ROTATION:
      jj_consume_token(ROTATION);
      jj_consume_token(PROPERTY_EQUAL);
      angle = NonNegativeInteger();
                property.setRotation(angle);
      break;
    case FRAME:
      jj_consume_token(FRAME);
      jj_consume_token(PROPERTY_EQUAL);
      frame = FrameType();
                property.setFrame(frame);
      break;
    case LABEL:
      jj_consume_token(LABEL);
      jj_consume_token(PROPERTY_EQUAL);
      text = QuotedStringValue();
                property.setLabel(text);
      break;
    case FORECOLOR:
      jj_consume_token(FORECOLOR);
      jj_consume_token(PROPERTY_EQUAL);
      color = ColorValue();
                property.setForeColor(color);
      break;
    case BACKCOLOR:
      jj_consume_token(BACKCOLOR);
      jj_consume_token(PROPERTY_EQUAL);
      color = ColorValue();
                property.setBackColor(color);
      break;
    case LINECOLOR:
      jj_consume_token(LINECOLOR);
      jj_consume_token(PROPERTY_EQUAL);
      color = ColorValue();
                property.setLineColor(color);
      break;
    case FONT:
      jj_consume_token(FONT);
      jj_consume_token(PROPERTY_EQUAL);
      text = QuotedStringValue();
                property.setFontName(text);
      break;
    case FONTSIZE:
      jj_consume_token(FONTSIZE);
      jj_consume_token(PROPERTY_EQUAL);
      x = NonNegativeInteger();
                property.setFontSize(x);
      break;
    case CONNECTION_CONSTRAINT:
      jj_consume_token(CONNECTION_CONSTRAINT);
      jj_consume_token(PROPERTY_EQUAL);
      text = QuotedStringValue();
                property.setConnectionConstraint(
                        Enum.valueOf(ConnectionConstraint.class, text));
      break;
    default:
      jj_la1[17] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    jj_consume_token(PROPERTY_DOUBLE_QUOTE);
  }

  final public String IdentifierValue() throws ParseException {
  Token t;
    t = jj_consume_token(IDENTIFIER);
                {if (true) return t.image.substring(1, t.image.length() - 1);}
    throw new Error("Missing return statement in function");
  }

  final public String QuotedStringValue() throws ParseException {
  Token t;
    t = jj_consume_token(SINGLE_QUOTED_STRING);
                // trim the quotes
                {if (true) return t.image.substring(1, t.image.length() - 1);}
    throw new Error("Missing return statement in function");
  }

  final public int State(Variable variable) throws ParseException {
        String name;
    name = IdentifierValue();
                                 {if (true) return variable.indexOf(name);}
    throw new Error("Missing return statement in function");
  }

  final public int NonNegativeInteger() throws ParseException {
  Token t;
    t = jj_consume_token(NON_NEGATIVE_INTEGER);
                                   {if (true) return Integer.parseInt(t.image);}
    throw new Error("Missing return statement in function");
  }

  final public double NonNegativeFloat() throws ParseException {
  Token t;
    t = jj_consume_token(NON_NEGATIVE_FLOAT);
                                 {if (true) return Double.parseDouble(t.image);}
    throw new Error("Missing return statement in function");
  }

  final public BeliefNodeProperty.FrameType FrameType() throws ParseException {
  BeliefNodeProperty.FrameType type;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case NONE:
      jj_consume_token(NONE);
                 type = BeliefNodeProperty.FrameType.NONE;
      break;
    case OVAL:
      jj_consume_token(OVAL);
                 type = BeliefNodeProperty.FrameType.OVAL;
      break;
    case RECTANGLE:
      jj_consume_token(RECTANGLE);
                      type = BeliefNodeProperty.FrameType.RECTANGLE;
      break;
    default:
      jj_la1[18] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
          {if (true) return type;}
    throw new Error("Missing return statement in function");
  }

  final public Color ColorValue() throws ParseException {
  int r, g, b;
    jj_consume_token(PROPERTY_LPARENTHESE);
    r = NonNegativeInteger();
    jj_consume_token(PROPERTY_COMMA);
    g = NonNegativeInteger();
    jj_consume_token(PROPERTY_COMMA);
    b = NonNegativeInteger();
    jj_consume_token(PROPERTY_RPARENTHESE);
          {if (true) return new Color(r,g,b);}
    throw new Error("Missing return statement in function");
  }

  /** Generated Token Manager. */
  public org.latlab.io.bif.BifParserTokenManager token_source;
  SimpleCharStream jj_input_stream;
  /** Current token. */
  public Token token;
  /** Next token. */
  public Token jj_nt;
  private int jj_ntk;
  private int jj_gen;
  final private int[] jj_la1 = new int[19];
  static private int[] jj_la1_0;
  static private int[] jj_la1_1;
  static {
      jj_la1_init_0();
      jj_la1_init_1();
   }
   private static void jj_la1_init_0() {
      jj_la1_0 = new int[] {0x80,0x1000,0x2000,0x10000,0x200000,0x0,0x2000000,0x0,0x0,0x1000000,0x2000000,0x1000000,0x0,0x100000,0x0,0x2000000,0x0,0xf8000000,0x0,};
   }
   private static void jj_la1_init_1() {
      jj_la1_1 = new int[] {0x0,0x0,0x0,0x0,0x0,0x10000,0x300000,0x300000,0x300000,0x0,0x0,0x0,0x10000,0x10000,0x100000,0x0,0x40000,0x1f,0xe0,};
   }

  /** Constructor with InputStream. */
  public BifParser(java.io.InputStream stream) {
     this(stream, null);
  }
  /** Constructor with InputStream and supplied encoding */
  public BifParser(java.io.InputStream stream, String encoding) {
    try { jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source = new org.latlab.io.bif.BifParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 19; i++) jj_la1[i] = -1;
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
    for (int i = 0; i < 19; i++) jj_la1[i] = -1;
  }

  /** Constructor. */
  public BifParser(java.io.Reader stream) {
    jj_input_stream = new SimpleCharStream(stream, 1, 1);
    token_source = new org.latlab.io.bif.BifParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 19; i++) jj_la1[i] = -1;
  }

  /** Reinitialise. */
  public void ReInit(java.io.Reader stream) {
    jj_input_stream.ReInit(stream, 1, 1);
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 19; i++) jj_la1[i] = -1;
  }

  /** Constructor with generated Token Manager. */
  public BifParser(org.latlab.io.bif.BifParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 19; i++) jj_la1[i] = -1;
  }

  /** Reinitialise. */
  public void ReInit(BifParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 19; i++) jj_la1[i] = -1;
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

  private int jj_ntk() {
    if ((jj_nt=token.next) == null)
      return (jj_ntk = (token.next=token_source.getNextToken()).kind);
    else
      return (jj_ntk = jj_nt.kind);
  }

  private java.util.List<int[]> jj_expentries = new ArrayList<int[]>();
  private int[] jj_expentry;
  private int jj_kind = -1;

  /** Generate ParseException. */
  public ParseException generateParseException() {
    jj_expentries.clear();
    boolean[] la1tokens = new boolean[56];
    if (jj_kind >= 0) {
      la1tokens[jj_kind] = true;
      jj_kind = -1;
    }
    for (int i = 0; i < 19; i++) {
      if (jj_la1[i] == jj_gen) {
        for (int j = 0; j < 32; j++) {
          if ((jj_la1_0[i] & (1<<j)) != 0) {
            la1tokens[j] = true;
          }
          if ((jj_la1_1[i] & (1<<j)) != 0) {
            la1tokens[32+j] = true;
          }
        }
      }
    }
    for (int i = 0; i < 56; i++) {
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

  /** Enable tracing. */
  final public void enable_tracing() {
  }

  /** Disable tracing. */
  final public void disable_tracing() {
  }

}
