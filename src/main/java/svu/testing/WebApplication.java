package svu.testing;

import java.io.File;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import svu.meclassifier.MinkowskiDistanceFunction;
import svu.proteinsequences.DecisionRuleClassifier;
import svu.proteinsequences.ProteinSequence;
import svu.proteinsequences.ProteinSequenceClassifiers;
import svu.util.CSVUtil;
import svu.meclassifier.MultimodalEvolutionaryClassifier;

import ro.pippo.core.Application;
import ro.pippo.core.Pippo;
import ro.pippo.core.route.RouteContext;
import ro.pippo.core.route.RouteHandler;

public class WebApplication {

	private static MultimodalEvolutionaryClassifier mec = null;
	
	private static DecisionRuleClassifier psCls = null;

	private static double param(RouteContext c, String key) {
		return c.getParameter(key).toDouble();
	}
	
	private static double[] params(RouteContext req) {
		return new double[]{ 
				param(req, "preg"),
				param(req, "plas"),
				param(req, "pres"),
				param(req, "skin"),
				param(req, "insu"),
				param(req, "mass"),
				param(req, "pedi"),
				param(req, "age")
		};
	}
	
	private static Result createResult(int code) {
		Result result = new Result();
		result.setResult(code);
		return result;
	}

	public static void main(String[] args) throws Exception {

		String userDir = System.getProperty("user.dir");
		File path = new File(new File(new File(userDir, "src"), "main"), "public");
		System.out.println("path = " + path);

		Application app = new Application() {
			@Override
			protected void onInit() {
				GET("/predict", new RouteHandler() {
					@Override
					public void handle(RouteContext routeContext) {
						if (mec == null) {
							routeContext.status(500).send("Classifier not ready, try again in a few moments.");
							return;
						}
						double[] newUserParams = params(routeContext);
						int[] prediction = mec.predict(new double[][]{ newUserParams });
						routeContext.json().send(createResult(prediction[0]));
					}
				});
				GET("/predict-ps", new RouteHandler() {
					@Override
					public void handle(RouteContext routeContext) {
						String seq = routeContext.getParameter("seq").toString();
						double[] seqD = ProteinSequence.stringToDoubles(seq);
						int[] prediction = psCls.predict(new double[][]{ seqD });
						routeContext.json().send(createResult(prediction[0]));
					}
				});
			}
		};
		
		Pippo server = new Pippo(app);
		server.addFileResourceRoute("/public", new File("src/main/public"));
		server.start(8080);
		
		System.out.println("Web-server started, please open http://127.0.0.1:8080/public/index.html");
		
		MultimodalEvolutionaryClassifier classifier =
				new MultimodalEvolutionaryClassifier(100, MinkowskiDistanceFunction.Factory(3.0));
		
		psCls = ProteinSequenceClassifiers.newStatic();
		
		// read dataset
		Reader reader = new InputStreamReader(WebApplication.class.getResourceAsStream("/diabetes.csv"));
		double[][] data = CSVUtil.load(reader);
		double[][] X = CSVUtil.attributes(data);
		int[]      Y = CSVUtil.classes(data);

		classifier.fit(X, Y); // train the classifier
		mec = classifier; // make it ready for the web app
		
		System.out.println("Classifier ready for use.");
	}
	
	public static class Result {
		private int result;
		public void setResult(int result) {
			this.result = result;
		}
		public int getResult() {
			return result;
		}
	}
}
