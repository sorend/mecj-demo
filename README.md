
Multimodal Evolutionary Classifier, Java implementation
==============

This is a Java implementation of the Multimodal Evolutionary classifier.

The multi-modal evolutionary classifier is learns a reference vector for each
of the classes in the training data by applying a genetic algorithm to the
the following optimization problem:

    Minimize: Sum(d(c, x_i))  for all i

where *c* is a chromosome in the GA, and x_i is a training example, and *d(..)* is a distance measure. It is called multi-modal because the GA is able to capture minimization that an uni-modal optimization can not (e.g. gradient descent). Meaning, it can deal with data containing more local optima.

The distance measure *d(a, b)* can be substituted by any of the available distance measures. For example the stoean and minkowski distance measures are expected to work better for non-normalized data, while manhattan and euclidean are faster (expected to be the same) on normalized data. 

Demo usage
----------

See the class svu.testing.MECMain to run some experiments with the classifier

You can also start the included demonstrator web application through Gradle:

    ./gradlew run

Then open one of the following URLs in your browser:

- http://127.0.0.1:8080/public/index.html (prediction using MEC)
- http://127.0.0.1:8080/public/ps.html    (prediction using RBDC)

Using with WEKA
---------------

Build and add the jar file to WEKAs class path. You can now find the MEC under the
"rules" section in the classifiers.

Using in JAVA code
------------------

You can use the code directly in JAVA without the WEKA wrapper. The **fit** method is used for training and expects input as a matrix of double (array of array of double) and an array of integers representing the classes. After this you can use the **predict** method to predict new examples. It expects the same input, matrix of double and will give array of integers back.
 
 A more complete example below:
 
    DistanceFactory df = MinkowskiDistanceFunction.Factory(1.5);
    MultimodalEvolutionaryClassifier mec = 
       new MultimodalEvolutionaryClassifier(100, df);

    // define some training data and associated classes
    double[][] X_train = new double[][]{
		{ 1, 2, 4 },
		{ 2, 4, 8 }
	};
	int[] Y_train = new int[] {	0, 1 };
	
	// train the classifier	
	mec.fit(X_train, Y_train);

	// define some testing data
	double[][] X_test = new double[][]{
		{0.9, 1.7, 4.5},
		{2.1, 3.9, 7.8}
	};

	// predict classes of the testing data		
	int[] Y_test = mec.predict(X_test);
		
	System.out.println("Y_actual = " + ListUtil.prettyArray(new int[]{ 0, 1 }));
	System.out.println("Y_test   = " + ListUtil.prettyArray(Y_test));	

	
Credits
-------

The code has been implemented by Soren A. Davidsen as part of his research work at [S. V. University, India](http://www.svuniversity.ac.in/). You can contact E. Sreedevi and K. Vijayalakshmi for information regarding the demonstration applications (part of their research work).