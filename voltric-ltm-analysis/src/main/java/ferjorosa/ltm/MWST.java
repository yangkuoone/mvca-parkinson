package ferjorosa.ltm;


import voltric.data.dataset.DiscreteDataCase;
import voltric.data.dataset.DiscreteDataSet;
import voltric.graph.DirectedNode;
import voltric.graph.UndirectedGraph;
import voltric.model.BeliefNode;
import voltric.model.LTM;
import voltric.reasoner.CliqueTreePropagation;
import voltric.util.Function;
import voltric.util.StringPair;
import voltric.util.Utils;
import voltric.variables.DiscreteVariable;

import java.util.*;

/**
 * Created by equipo on 10/02/2017.
 */
public class MWST {

    /**
     * The collection of posterior distributions P(Y|d) for each latent variable
     * Y at the root of a hierarchy and each data case d in the training data.
     * USELESS in PEM version But keep it for future use if we need soft
     * assignment or to keep record
     */
    private Map<DiscreteVariable, Map<DiscreteDataCase, Function>> _latentPosts;

    private Collection<LTM> islands;

    private DiscreteDataSet dataSet;

    public MWST(Collection<LTM> islands, DiscreteDataSet dataSet){
        this._latentPosts = new HashMap<DiscreteVariable, Map<DiscreteDataCase, Function>>();
        this.islands = islands;
        this.dataSet = dataSet;
        for(LTM partition : islands){
            updateStats(partition, dataSet);
        }
    }

    public UndirectedGraph learnMWST(){
        // hierarchies are simply the islands with their associated LV
        Map<DiscreteVariable, LTM> hierarchies = new HashMap<>();
        for(LTM island: islands){
            hierarchies.put(island.getRoot().getVariable(), island);
        }
        return learnMaximumSpanningTree(hierarchies, dataSet);
    }

    private UndirectedGraph learnMaximumSpanningTree(Map<DiscreteVariable, LTM> hierarchies, DiscreteDataSet _data) {

        // initialize the data structure for pairwise MI
        List<StringPair> pairs = new ArrayList<StringPair>();

        // the collection of latent variables.
        List<DiscreteVariable> vars = new ArrayList<DiscreteVariable>(hierarchies.keySet());

        List<DiscreteVariable> varPair = new ArrayList<DiscreteVariable>(2);
        varPair.add(null);
        varPair.add(null);

        int nVars = vars.size();

        // enumerate all pairs of latent variables
        for (int i = 0; i < nVars; i++) {
            DiscreteVariable vi = vars.get(i);
            varPair.set(0, vi);

            for (int j = i + 1; j < nVars; j++) {
                DiscreteVariable vj = vars.get(j);
                varPair.set(1, vj);

                // compute empirical MI
                Function pairDist = computeEmpDist(varPair, _data);
                double mi = Utils.computeMutualInformation(pairDist);

                // keep both I(vi; vj) and I(vj; vi)
                pairs.add(new StringPair(vi.getName(), vj.getName(), mi));
            }
        }

        // sort the pairwise MI.
        Collections.sort(pairs);

        // building MST using Kruskal's algorithm
        UndirectedGraph mst = new UndirectedGraph();

        // nVars = latentTree.getNumberOfNodes();
        HashMap<String, ArrayList<String>> components = new HashMap<String, ArrayList<String>>();
        // move the node with more than 2 variables to the first place
        boolean flag = false;
        for (DiscreteVariable var : hierarchies.keySet()) {
            String name = var.getName();
            mst.addNode(name);

            if (hierarchies.get(var).getLeafVars().size() >= 3 && !flag) {
                mst.move2First(name);
                flag = true;
            }
            ArrayList<String> component = new ArrayList<String>(nVars);
            component.add(name);
            components.put(name, component);
        }

        // examine pairs in descending order w.r.t. MI
        for (int i = pairs.size() - 1; i >= 0; i--) {
            StringPair pair = pairs.get(i);
            String a = pair.GetStringA();
            String b = pair.GetStringB();
            ArrayList<String> aComponent = components.get(a);
            ArrayList<String> bComponent = components.get(b);

            // check whether a and b are in the same connected component
            if (aComponent != bComponent) {
                // connect nodes
                mst.addEdge(mst.getNode(a), mst.getNode(b));

                if (aComponent.size() + bComponent.size() == nVars) {
                    // early termination: the tree is done
                    break;
                }

                // merge connected component
                aComponent.addAll(bComponent);
                for (String c : bComponent) {
                    components.put(c, aComponent);
                }
            }
        }

        return mst;
    }

    /**
     * Compute the empirical distribution of the given pair of variables
     */
    private Function computeEmpDist(List<DiscreteVariable> varPair, DiscreteDataSet _data) {
        DiscreteVariable[] vars = _data.getVariables();

        DiscreteVariable vi = varPair.get(0);
        DiscreteVariable vj = varPair.get(1);

        int viIdx = -1, vjIdx = -1;

        // retrieve P(Y|d) for latent variables and locate manifest variables
        Map<DiscreteDataCase, Function> viPosts = _latentPosts.get(vi);
        if (viPosts == null) {
            viIdx = Arrays.binarySearch(vars, vi);
        }

        Map<DiscreteDataCase, Function> vjPosts = _latentPosts.get(vj);
        if (vjPosts == null) {
            vjIdx = Arrays.binarySearch(vars, vj);
        }

        Function empDist = Function.createFunction(varPair);

        for (DiscreteDataCase datum : _data.getData()) {
            int[] states = datum.getStates();

            // If there is missing data, continue;
            if ((viIdx != -1 && states[viIdx] == -1)
                    || (vjIdx != -1 && states[vjIdx] == -1)) {
                continue;
            }
            // P(vi, vj|d) = P(vi|d) * P(vj|d)
            Function freq;

            if (viPosts == null) {
                freq = Function.createIndicatorFunction(vi, states[viIdx]);
            } else {
                freq = viPosts.get(datum);
            }

            if (vjPosts == null) {
                freq = freq.times(Function.createIndicatorFunction(vj, states[vjIdx]));
            } else {
                freq = freq.times(vjPosts.get(datum));
            }

            freq = freq.times(datum.getWeight());

            empDist.plus(freq);
        }

        empDist.normalize();

        return empDist;
    }

    /**
     * Update the collections of P(Y|d). Specifically, remove the entries for
     * all the latent variables in the given sub-model except the root, and
     * compute P(Y|d) for the latent variable at the root and each data case d.
     */
    private void updateStats(LTM subModel, DiscreteDataSet _data) {
        BeliefNode root = subModel.getRoot();
        DiscreteVariable latent = root.getVariable();
        // Function prior = root.getCpt();

        for (DirectedNode child : root.getChildren()) {
            _latentPosts.remove(((BeliefNode) child).getVariable());
        }

        Map<DiscreteDataCase, Function> latentPosts = new HashMap<DiscreteDataCase, Function>();

        CliqueTreePropagation ctp = new CliqueTreePropagation(subModel);

        Map<DiscreteVariable, Integer> varIdx = _data.createVariableToIndexMap();

        DiscreteVariable[] subVars = subModel.getManifestVars().toArray(new DiscreteVariable[0]);
        int nSubVars = subVars.length;
        int[] subStates = new int[nSubVars];

        // update for every data case
        for (DiscreteDataCase dataCase : _data.getData()) {
            // project states
            int[] states = dataCase.getStates();
            for (int i = 0; i < nSubVars; i++) {
                subStates[i] = states[varIdx.get(subVars[i])];
            }

            // set evidence and propagate
            ctp.setEvidence(subVars, subStates);
            ctp.propagate();

            // compute P(Y|d)
            Function post = ctp.computeBelief(latent);
            latentPosts.put(dataCase, post);
        }

        _latentPosts.put(latent, latentPosts);
    }
}
