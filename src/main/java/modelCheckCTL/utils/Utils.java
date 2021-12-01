package modelCheckCTL.utils;

import modelCheckCTL.controller.ctl.Parser.Token;
import modelCheckCTL.controller.types.kripke.State;
import modelCheckCTL.controller.types.kripke.Transition;

import java.io.IOException;
import java.util.*;

/**
 * Generic utility methods for dealing with {@link State}s, {@link Transition}s, CTL labels, {@link Set}s and some other miscellaneous items.
 * The state utilities here are: isStateName, getStatesStr, getState and statesWithLabel.
 * The transition utilities here are: getTransition and getTransitionsStr.
 * The label utility here is: getLabelsStr
 * The set utilities here are: contains, containsStateName, areEqual, copy, intersection, union and subtract.
 * The miscellaneous utilities here are: removeByteOrderMark, handleError and isTxtFile
 */
public class Utils {

    // States Utils

    /**
     * Checks if {@link String} is a state name like s1 or s2 and returns true or false
     * Checks that string is 2 or more chars, that first char is "s" and than the char(s) after the "s" are integers
     * Used in the Controller method that parses a Kripke text file
     * @param str a {@link String} that one wants to make sure it's a state name like s1, s2, etc
     * @return
     */
    public static Boolean isStateName(String str) {
        if (str == null) { throw new NullPointerException("null argument in isStateName call"); }
        if (str.length() < 2) {
            return false;
        } else if (str.charAt(0) != 's') {
            return false;
        } else {
            // checks if char(s) after the "s" are numbers
            String charsAfterS = str.substring(1);
            if (!charsAfterS.matches("\\d+")) {
                return false;
            } else {
                return true;
            }
        }
    }

    /**
     * Gets the names of states in a Set and returns them in a comma separated string with a colon at the end."
     * The states are sorted ascending by each state's number.
     * This is used for printing out Kripke contents and also for just printing out state set contents.
     * @param states The states that one needs the names of outputted
     * @return a string formatted like this "s1, s2, s3;"
     */
    public static String getStatesStr(Set states) throws IOException {
        if (states == null) { throw new NullPointerException("getStatesStr was passed a null Set param."); }
        if (states.size() == 0) { throw new IOException("State set is empty in getStatesStr"); }
        for (Object stateObj : states) {
            if (stateObj == null) { throw new NullPointerException("one of the states passed to getStatesStr is null"); }
        }
        List statesList = new ArrayList<State>(states);
        Collections.sort(statesList);

        String statesStr = "";
        for (Object stateObj : statesList) {
            State state = (State) stateObj;
            statesStr = statesStr + state.toString() + ", ";
        }
        statesStr = statesStr.trim();
        statesStr = statesStr.substring(0,statesStr.length() - 1); // remove trailing comma
        statesStr = statesStr + ";";
        return statesStr;
    }

    /**
     * Gets a state from a state set, given its state number.
     * Throws IOExceptionError if state is not found. Throws NullPointerError if either param is null.
     * @param stateNum {@link Integer} of state needed from state set
     * @param states The {@link Set} of {@link State}s one needs a particular state from
     * @return the {@link State} from the state set, if found
     */
    public static State getState(Integer stateNum, Set states) throws IOException {
        if (stateNum == null) { throw new NullPointerException("stateNum param in getState is null"); }
        if (states == null) { throw new NullPointerException("states param in getState is null"); }
        for (Object stateObj : states) {
            if (stateObj == null) { throw new NullPointerException("a state in state set in getState is null"); }
            State state = (State) stateObj;
            if (state.getNumber() == stateNum) {
                return state;
            }
        }
        throw new IOException("state number " + stateNum + " not found in state set in getState");
    }

    /**
     * Takes a JavaCC {@link Token} of a CTL label and searches through a {@link Set} of {@link State}s for any states with that label
     * @param states is a {@link Set} of {@link State}s of which we want to know if any of the states contain the specified label
     * @param t {@link Token} of a label of which we want to know which states have it
     * @return the {@link Set} of {@link State}s which contain the specified label
     * @throws IOException
     */
    public static Set statesWithLabel(Set states, Token t) throws IOException {
       Set statesWithLabel = new HashSet();
       String label = t.toString();
       for (Object stateObj : states) {
           State state = (State) stateObj;
           if (state.hasLabel(label)) {
               statesWithLabel.add(state);
           }
       }
       return statesWithLabel;
    }


    // Transitions Utils

    /**
     * Gets a transition from a transition set, given its transition number.
     * Throws IOExceptionError if state is not found. Throws NullPointerError if either param is null.
     * @param transitionNum {@link Integer} of state needed from state set
     * @param transitions The {@link Set} of {@link Transition}s one needs a particular transition from
     * @return the {@link Transition} from the transition set, if found
     */
    public static Transition getTransition(Integer transitionNum, Set transitions) throws IOException {
        if (transitionNum == null) { throw new NullPointerException("stateNum param in getState is null"); }
        if (transitions == null) { throw new NullPointerException("states param in getState is null"); }
        for (Object transitionObj : transitions) {
            if (transitionObj == null) { throw new NullPointerException("a state in state set in getState is null"); }
            Transition transition = (Transition) transitionObj;
            if (transition.getNumber() == transitionNum) {
                return transition;
            }
        }
        throw new IOException("transition number " + transitionNum + " not found in state set in getState");
    }

    /**
     * Gets the names of transitions in a Set and returns them in a multiline format like:
     * t1 : s1 - s2,
     * t2 : s1 - s3;
     * The transitions are sorted ascending by each state's number.
     * This is used for printing out Kripke contents.
     * @param {@link Set} of {@link Transition}s that one needs the names of outputted
     * @return returns a multiline {@String} like:
     * t1 : s1 - s2,
     * t2 : s1 - s3;
     */
    public static String getTransitionsStr(Set transitions) throws IOException {
        if (transitions == null) { throw new NullPointerException("Param in getTransitionStr is null"); }
        if (transitions.size() == 0) { throw new IOException("Transition set is empty in getTransitionStr"); }
        List transitionsList = new ArrayList<Transition>(transitions);
        Collections.sort(transitionsList);

        String transitionsStr = "";
        for (Object transitionObj : transitionsList) {
            if (transitionObj == null) { throw new NullPointerException("A transition in transitions set in getTransitionStr is null"); }
            Transition transition = (Transition) transitionObj;
            transitionsStr = transitionsStr + transition.toStringDetailed() + ",\n";
        }
        transitionsStr = transitionsStr.substring(0,transitionsStr.length() - 2); // remove trailing comma and newline
        transitionsStr = transitionsStr + ";\n";
        return transitionsStr;
    }


    // Label Utils

    /**
     * Gets a label string from a {@link Set} of labels (a label is just a {@link Character} p-z) in format like:
     * s1 : p q, (propositional atom names are separated by a space; a name consists of letters, it is casesensitive)
     * s2 : q t r,
     * s3 : , (i.e. set of propositional atoms for state s3 is empty)
     * s4 : t;
     * @param states A {@link Set} of labels (a label is just a {@link Character}
     * @return Gets a label string from a {@link Set} of labels (a label is just a {@link Character} p-z) in format like:
     * s1 : p q, (propositional atom names are separated by a space; a name consists of letters, it is casesensitive)
     * s2 : q t r,
     * s3 : , (i.e. set of propositional atoms for state s3 is empty)
     * s4 : t;
     */
    public static String getLabelsStr(Set states) {
        List statesList = new ArrayList<State>(states);
        Collections.sort(statesList);

        String labelsStr = "";
        int stateNum = 0;
        int numStates = statesList.size();
        for (Object stateObj : statesList) {
            State state = (State) stateObj;
            labelsStr = labelsStr + state.toStringDetailed();
            if (numStates > 1 && stateNum < numStates - 1) {
                labelsStr = labelsStr + ",\n";
                stateNum++;
            }
        }
        labelsStr = labelsStr + ";";
        return labelsStr;
    }


    // Set Utils

    /**
     * Checks if a set of {@link State}s has a certain state in it. Takes the "needle" state and uses the number field and searches for that number in the "haystack" states of the set.
     * Note that only a state of the same state number is checked for. ie, 1 for s1. So this is not checking for the exact state by memory address. This is a loose implementation - might have to change this when I add Jung GUI back in
     * @param states set of {@link State}s to be searched for the state
     * @param state a {@link State} to be checked if it is in the set of states
     * @return true if state is in set, false otherwise
     */
    public static Boolean contains(Set states, State state) {
        if (states == null) { throw new NullPointerException("States param in contains is null"); }
        if (state == null) { throw new NullPointerException("State param in contains is null"); }
        Integer stateNum = state.getNumber();
        for (Object stateObj : states) {
            State thisState = (State) stateObj;
            if (thisState.getNumber() == stateNum) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if a set of {@link State}s has a certain state in it by using the supplied state name as the "needle" and searching for that name field on all the "haystack" states of the set
     * Note that only a state of the same state name is checked for. ie, "s1". So this is not checking for the exact state by memory address. This is a loose implementation - might have to change this when I add Jung GUI back in
     * @param states set of {@link State}s to be searched for the state
     * @param stateName a {@link String} to be checked if it is in the set of states' name fields
     * @return true if state is in set, false otherwise
     */
    public static Boolean containsStateName(Set states, String stateName) {
        if (states == null) { throw new NullPointerException("States param in contains is null"); }
        String stateNumStr = stateName.replace("s","");
        stateNumStr = stateNumStr.replaceAll("\uFEFF", "");
        try {
           int stateNum = Integer.parseInt(stateNumStr);
           for (Object stateObj : states) {
               State thisState = (State) stateObj;
               if (thisState.getNumber() == stateNum) {
                   return true;
               }
           }
        } catch (NumberFormatException nfe) {
          // Handle the condition when str is not a number.
        }
        return false;
    }

    /**
     * Tests if two sets of states are "equal", ie. do they each have the same number of states and does the second set have a state with the same num as a state in the first set
     * Note that only a state of the same state number is checked for. ie, 1 for s1. So this is not checking for the exact state by memory address. This is a loose implementation - might have to change this when I add Jung GUI back in
     * @param setA set of {@link State}s to be compared to setB
     * @param setB set of {@link State}s to be compared to setA
     * @return true if sets are the same size and they have states of the same state numbers.
     */
    public static Boolean areEqual(Set setA, Set setB) {
        if (setA == null || setB == null) { throw new NullPointerException("areEqual param(s) is null."); }
        if (setA.size() == 0 && setB.size() == 0) return true;
        if (setA.size() > 0 && setA.size() == setB.size()) {
            for (Object stateObj : setA) {
                State state = (State) stateObj;
                if (!contains(setB,state)) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * Copies a {@link Set} of {@link State}s. Iterates through the set and instantiates an new state for each state found. The instantiated state has the same number value, name value, but obviously has a new memory address. The same goes for all the transitions - they are re-instantiated one by one using the same number value, from value and to value, but the transition has a different memory address than the original. The copied labels, on the other hand, have the same memory address.
     * @param set is a {@link Set} of {@link State}s to be copied
     * @return a {@link Set} of {@link State}s with the same number, name, labels and transitions. The state itself as well as the {@link Transition}s all have different memory addresses than the originals. The copied labels on the states have the same memory addresses as the original labels.
     */
    public static Set copy(Set set) {
        Set copy = new HashSet();
        for (Object stateObj : set) {
            State state = (State) stateObj;
            Integer stateNum = state.getNumber();
            Set copyLabels = state.getLabels();
            State copyState = new State(stateNum);
            Set stateTransitions = state.getTransitions();
            Set copyTransitions = new HashSet();
            for (Object transitionObj : stateTransitions) {
                Transition transition = (Transition) transitionObj;
                Transition transitionCopy = transition.copy();
                copyTransitions.add(transitionCopy);
            }
            copyState.setTransitions(copyTransitions);
            copyState.setLabels(copyLabels);
            copy.add(copyState);
        }
        return copy;
    }

    /**
     * Set intersection operation. Takes two {@link Set}s of {@link State}s and returns the states in both. If set a is {1,2,3} and set b is {2,3,4} the intersection of a and b returns {2,3}.
     * @param a first {@link Set} of {@link State}s for the intersection operation
     * @param b second {@link Set} of {@link State}s for the intersection operation
     * @return the {@link Set} of {@link State}s which are in both supplied sets. If set a is {1,2,3} and set b is {2,3,4} the intersection of a and b returns {2,3}.
     */
    public static Set intersection(Set a, Set b) {
        Set intersection = new HashSet();
        for (Object stateObj : a) {
            State state = (State) stateObj;
            if (contains(b,state)) {
                intersection.add(state);
            }
        }
        return intersection;
    }

    /**
     * Set union operation. Takes two {@link Set}s of {@link State}s and returns one set with all the states in either set. If set a is {1,2,3} and set b is {2,3,4} the union of a and b returns {1,2,3,4}.
     * @param a first {@link Set} of {@link State}s for the union operation
     * @param b second {@link Set} of {@link State}s for the union operation
     * @return the {@link Set} of {@link State}s which in either set. If set a is {1,2,3} and set b is {2,3,4} the union of a and b returns {1,2,3,4}.
     */
    public static Set union(Set a, Set b) {
        Set union = new HashSet();
        for (Object stateObj : a) {
            union.add((State) stateObj);
        }
        for (Object stateObj : b) {
            State state = (State) stateObj;
            if (!contains(union,state)) {
                union.add(state);
            }
        }
        return union;
    }

    /**
     * Set subtraction operation. Takes two {@link Set}s of {@link State}s and returns all the states in the second set which are not in the first. If set a is {1,2,3} and set b is {2,3,4} then a subtract b returns {4}.
     * @param a first {@link Set} of {@link State}s for the subtraction operation
     * @param b second {@link Set} of {@link State}s for the subtraction operation
     * @return the {@link Set} of {@link State}s of all the states in the second set which are not in the first. If set a is {1,2,3} and set b is {2,3,4} then a subtract b returns {4}.
     */
    public static Set subtract(Set a, Set b) throws IOException {
        Set aCopy = copy(a);
        for (Object stateObj : b) {
            State thisState = (State) stateObj;
            Integer stateNumToRemove = thisState.getNumber();
            State stateToRemove = getState(stateNumToRemove,aCopy);
            aCopy.remove(stateToRemove);
        }
        return aCopy;
    }


    // Misc Utils

    /**
     * This removes an invisible "byte order mark" from a string. I believe some of the test files have this byte order mark, which was manifesting as a space character or something. The only way to remove the space was by using this code to remove the byte order mark.
     * See https://en.wikipedia.org/wiki/Byte_order_mark for more details on the byte order mark
     * This code approach to removing byte order marks from https://www.postgresql.org/message-id/20180717101246.GA41457%40elch.exwg.net
     * @param str {@link String} to remove the byte order mark from
     * @return the same {@link String} as the param, but with the byte order mark removed
     */
    public static String removeByteOrderMark(String str) {
        char firstChar = str.toCharArray()[0];
        int asciiNum = (int) firstChar;
        if (asciiNum == 65279) {
            str = str.substring(1);
        }
        return str;
    }

    /**
     * For debugging vs production. For debugging, we want to throw exception and halt program. For production, we want to print the error and keep going
     * @param errorMessage is a {@link String} of the message we want to throw/print
     * @param printExceptions {@link Boolean} of whether to print the exceptions to the console and not stop the program (true) or if we want to throw an exception and stop the program there (false)
     * @throws Exception
     */
    public static void handleError(String errorMessage, Boolean printExceptions) throws Exception {
        if (printExceptions) {
            System.out.println(errorMessage);
        } else {
            throw new Exception(errorMessage);
        }
    }

    /**
     *  Checks if {@link String} ends in .txt and has at least one character before the period.
     * Throws NullPointerException if {@link String} is null.
     * @param str The {@link String} one wants to check if it ends in .txt
     * @return false if string doesn't match the conditions above, otherwise returns true.
     * @throws IOException
     */
    public static Boolean isTxtFile(String str) throws IOException {
        if (str == null) { throw new NullPointerException("isTextFile param is null"); }
        int indexOfPeriod = str.lastIndexOf(".");
        if (indexOfPeriod == -1 || indexOfPeriod == 0) { return false; }
        String extension = str.substring(indexOfPeriod);
        if (!extension.equals(".txt")) {
            return false;
        }
        return true;
    }

}
