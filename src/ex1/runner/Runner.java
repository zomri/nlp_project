package ex1.runner;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import ex1.eval.Eval;
import ex1.lm.LM;

public class Runner {

	public static void main(String[] args) {
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.WARN);
		int n = 4;
		System.out.println("n = " + n);
		LM.main(new String[] { "-n", "" + n, "-i", "sam.1.2+kin.1.2", "-o",
				"model.txt", "-s", "ls", "-lmd", "0.0015" });
		System.out.println("ls");
		eval("gen");
		LM.main(new String[] { "-n", "" + n, "-i", "sam.1.2+kin.1.2", "-o",
				"model.txt", "-s", "wb" });
		System.out.println("wb");
		eval("gen");
	}

	private static void eval(String testFile) {
		System.out.println(testFile);
		Eval.main(new String[] { "-m", "model.txt", "-i", testFile });
	}

}
