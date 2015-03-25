package svu.testing;

import java.io.File;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

import org.webbitserver.HttpControl;
import org.webbitserver.HttpHandler;
import org.webbitserver.HttpRequest;
import org.webbitserver.HttpResponse;
import org.webbitserver.WebServer;
import org.webbitserver.WebServers;
import org.webbitserver.handler.StaticFileHandler;

import svu.meclassifier.MinkowskiDistanceFunction;
import svu.meclassifier.MultimodalEvolutionaryClassifier;
import svu.util.CSVUtil;

public class WebApplication {

	private static MultimodalEvolutionaryClassifier mec = null;
	
	private static double param(HttpRequest req, String key) {
		return Double.parseDouble(req.queryParam(key));
	}
	
	private static double[] params(HttpRequest req) {
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
	
	public static class PredictionHandler implements HttpHandler {
		@Override
		public void handleHttpRequest(HttpRequest req, HttpResponse res, HttpControl ctrl) throws Exception {
			if (mec == null) {
				res.status(500).end();
			}
			else {
				double[] newUserParams = params(req);
				int[] prediction = mec.predict(new double[][]{ newUserParams });
				// send reply to client
				res.header("Content-Type", "application/json")
					.content("{\"result\":" + prediction[0] + "}")
					.end();
			}
		}
	}

	public static void main(String[] args) throws Exception {
		
		URL diabetesLocation = WebApplication.class.getResource("/svu/testing/diabetes.csv");
		File path = new File(new File(diabetesLocation.toURI()).getParentFile().getParentFile().getParentFile().getParentFile(), "web");
		
		WebServer server = WebServers.createWebServer(8080)
			.add("/predict", new PredictionHandler())
			.add(new StaticFileHandler(path))
			.start().get();
		
		System.out.println("Web-server started, please open " + server.getUri() + "index.html");
		
		MultimodalEvolutionaryClassifier classifier =
				new MultimodalEvolutionaryClassifier(100, MinkowskiDistanceFunction.Factory(3.0));
		
		// read dataset
		Reader reader = new InputStreamReader(MECMain.class.getResourceAsStream("diabetes.csv"));
		double[][] data = CSVUtil.load(reader);
		double[][] X = CSVUtil.attributes(data);
		int[]      Y = CSVUtil.classes(data);

		classifier.fit(X, Y); // train the classifier
		mec = classifier; // make it ready for the web app
		
		System.out.println("Classifier ready for use.");
	}
}
