package svu.testing;

import java.io.File;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Optional;

import ro.pippo.core.Application;
import ro.pippo.core.Pippo;
import ro.pippo.core.route.RouteContext;
import ro.pippo.core.route.RouteHandler;
import svu.meclassifier.MinkowskiDistanceFunction;
import svu.meclassifier.MultimodalEvolutionaryClassifier;
import svu.mprsa.MPRSA3;
import svu.proteinsequences.DecisionRuleClassifier;
import svu.proteinsequences.ProteinSequence;
import svu.proteinsequences.ProteinSequenceClassifiers;
import svu.userdb.UserDAO;
import svu.util.CSVUtil;

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
	
	public static class Measures {
		private double preg, plas, pres, skin, insu, mass, pedi, age;

		public double getPreg() {
			return preg;
		}

		public void setPreg(double preg) {
			this.preg = preg;
		}

		public double getPlas() {
			return plas;
		}

		public void setPlas(double plas) {
			this.plas = plas;
		}

		public double getPres() {
			return pres;
		}

		public void setPres(double pres) {
			this.pres = pres;
		}

		public double getSkin() {
			return skin;
		}

		public void setSkin(double skin) {
			this.skin = skin;
		}

		public double getInsu() {
			return insu;
		}

		public void setInsu(double insu) {
			this.insu = insu;
		}

		public double getMass() {
			return mass;
		}

		public void setMass(double mass) {
			this.mass = mass;
		}

		public double getPedi() {
			return pedi;
		}

		public void setPedi(double pedi) {
			this.pedi = pedi;
		}

		public double getAge() {
			return age;
		}

		public void setAge(double age) {
			this.age = age;
		}
	}
	
	private static String measuresString(double[] m) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < m.length; i++) {
			if (i > 0)
				sb.append(";");
			sb.append(String.valueOf(m[i]));
		}
		return sb.toString();
	}
	
	private static double[] measuresDoubles(Optional<String> s) {
		if (!s.isPresent())
			return null;
		String[] a = s.get().split(";");
		double[] d = new double[a.length];
		for (int i = 0; i < a.length; i++)
			d[i] = Double.valueOf(a[i]);
		return d;
	}

	private static Result createResult(int code) {
		return createResult(code, null);
	}
	
	private static Result createResult(int code, String message) {
		Result result = new Result();
		result.setResult(code);
		result.setMessage(message);
		return result;
	}
	
	private static Measures createMeasures(double[] m) {
		Measures m2 = new Measures();
		m2.setPreg(m[0]);
		m2.setPlas(m[1]);
		m2.setPres(m[2]);
		m2.setSkin(m[3]);
		m2.setInsu(m[4]);
		m2.setMass(m[5]);
		m2.setPedi(m[6]);
		m2.setAge(m[7]);
		return m2;
	}
	
	public static void main(String[] args) throws Exception {

		String userDir = System.getProperty("user.dir");
		File path = new File(new File(new File(userDir, "src"), "main"), "public");
		System.out.println("path = " + path);

		final UserDAO dao = new UserDAO();
		
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
				GET("/load", new RouteHandler() {
					@Override
					public void handle(RouteContext routeContext) {
						String username = routeContext.getParameter("username").toString();
						String privateKey = routeContext.getParameter("privateKey").toString();
						Optional<String> measures = dao.loadMeasures(username, privateKey);
						routeContext.json().send(createMeasures(measuresDoubles(measures)));
					}
				});
				POST("/save", new RouteHandler() {
					@Override
					public void handle(RouteContext routeContext) {
						double[] newUserParams = params(routeContext);
						String username = routeContext.getParameter("username").toString();
						String publicKey = routeContext.getParameter("publicKey").toString();
						dao.saveMeasures(username, measuresString(newUserParams), publicKey);
						routeContext.status(200).send("OK");
					}
				});
				GET("/genkey", new RouteHandler() {
					@Override
					public void handle(RouteContext routeContext) {
						MPRSA3 instance = MPRSA3.generateKeys();
						Keys keys = new Keys();
						keys.setPrivateKey(instance.getPrivateKey());
						keys.setPublicKey(instance.getPublicKey());
						routeContext.json().send(keys);
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
		private String message;
		public void setResult(int result) {
			this.result = result;
		}
		public int getResult() {
			return result;
		}
		public void setMessage(String message) {
			this.message = message;
		}
		public String getMessage() {
			return message;
		}
	}
	
	public static class Keys {
		private String publicKey;
		private String privateKey;
		public String getPublicKey() {
			return publicKey;
		}
		public void setPublicKey(String publicKey) {
			this.publicKey = publicKey;
		}
		public String getPrivateKey() {
			return privateKey;
		}
		public void setPrivateKey(String privateKey) {
			this.privateKey = privateKey;
		}
	}
}
