package weka.classifiers.rules;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Vector;

import svu.meclassifier.DistanceFunctionFactory;
import weka.classifiers.AbstractClassifier;
import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.Utils;

public class MultimodalEvolutionaryClassifier extends AbstractClassifier implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2591924465094937298L;

	private int numIterations = 100;
	private String distanceFunction = "stoean";
	
	transient private svu.meclassifier.MultimodalEvolutionaryClassifier classifier_;
	
	public MultimodalEvolutionaryClassifier() {
	}
	
    public Capabilities getCapabilities() {
        Capabilities result = super.getCapabilities();

        // attributes
        result.disable(Capability.NOMINAL_ATTRIBUTES);
        result.enable(Capability.NUMERIC_ATTRIBUTES);
        result.disable(Capability.DATE_ATTRIBUTES);
        result.disable(Capability.MISSING_VALUES);

        // class
        result.enable(Capability.NOMINAL_CLASS);
        result.disable(Capability.NUMERIC_CLASS);
        result.disable(Capability.MISSING_CLASS_VALUES);

        return result;
    }
    
    @Override
    public Enumeration listOptions() {
        Option opt_I = new Option("\tNumber of iterations (default=100)", "I", 1, "-I <num>");
        Option opt_D = new Option("\tDistance function (default=stoean)", "D", 1, "-D <function>");

        Vector all = new Vector();
        all.add(opt_I);
        all.add(opt_D);
        return all.elements();
    }

    @Override
    public void setOptions(String[] options) throws Exception {

        String I = Utils.getOption('I', options);
        String D = Utils.getOption('D', options);

        if (I != null)
        	setNumIterations(Integer.parseInt(I));
        if (D != null)
        	setDistanceFunction(D);
    }

    @Override
    public String[] getOptions() {
        return new String[] {
                "-I", String.valueOf(numIterations),
                "-D", distanceFunction,

        };
    }

    @Override
    public void buildClassifier(Instances instances) throws Exception {

    	int n = instances.numInstances();
    	int m = instances.numAttributes();

    	double[][] X = new double[n][m - 1];
    	int[] y = new int[n];
    	
    	for (int i = 0; i < n; i++) {
    		// the instance from weka
    		double[] instance = instances.instance(i).toDoubleArray();

    		int idx = 0;
    		for (int j = 0; j < instance.length; j++) {
    			if (j == instances.classIndex())
    				y[i] = (int) instance[j];
    			else
    				X[i][idx++] = instance[j];
    		}
    	}
    	
    	// System.out.println("instances: " + instances);
    	// System.out.println("X: " + ListUtil.prettyArray(X));
    	// System.out.println("Y: " + ListUtil.prettyArray(y));

    	DistanceFunctionFactory df = DistanceFunctionHelper.buildFactory(distanceFunction);
    	
    	classifier_ = new svu.meclassifier.MultimodalEvolutionaryClassifier(numIterations, df);
    	classifier_.fit(X, y);
    }

    @Override
    public String toString() {
        return classifier_ != null ? classifier_.toString() : "Classifier not trained yet";
    }
    
    @Override
    public double classifyInstance(Instance instance) throws Exception {
    	double[] X = new double[instance.numAttributes() - 1];
    	
    	int idx = 0;
    	for (int i = 0; i < instance.numAttributes(); i++)
    		if (i != instance.classIndex())
    			X[idx++] = instance.value(i);

    	int[] y_pred = classifier_.predict(new double[][]{ X });
    	return y_pred[0];
    }

    /*
    @Override
    public double[] distributionForInstance(Instance instance) throws Exception {
    	double[] X = new double[instance.numAttributes() - 1];
    	
    	int idx = 0;
    	for (int i = 0; i < instance.numAttributes(); i++)
    		if (i != instance.classIndex())
    			X[idx++] = instance.value(i);

    	double[][] proba = classifier_.predictProba(new double[][] { X });
    	
    	System.out.println(ListUtil.prettyArray(proba));
    	return proba[0];
    }
    */

    public int getNumIterations() {
		return numIterations;
	}

	public void setNumIterations(int numIterations) {
		this.numIterations = numIterations;
	}
	
	public String numIterationsTipText() {
		return "Number of iterations";
	}

	public String getDistanceFunction() {
		return distanceFunction;
	}

	public void setDistanceFunction(String distanceFunction) {
		this.distanceFunction = distanceFunction;
	}
	
	public String distanceFunctionTipText() {
		return "Distance function";
	}

    public static void main(String[] argv) {
        runClassifier(new weka.classifiers.rules.MultimodalEvolutionaryClassifier(), argv);
    }

}
