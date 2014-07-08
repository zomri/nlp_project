package ex1.lm;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.beust.jcommander.JCommander;

import ex1.common.ArpaFormatReadWrite;
import ex1.common.Model;
import ex1.lm.ls.LsModelCreator;
import ex1.lm.wb.WbModelCreator;

public class LM {

	public static void main(String[] args) {
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.WARN);
		LmArgs cliArgs = new LmArgs();
		JCommander jCommander = new JCommander(cliArgs, args);
		if (cliArgs.inputfile() == null || cliArgs.outputFile() == null
				|| cliArgs.smooth() == null) {
			System.out.println("missing arguments");
			jCommander.setProgramName("lm");
			jCommander.usage();
			System.exit(1);
		}

		ModelCreator mc = null;
		if (cliArgs.smooth().compareTo("ls") == 0) {
			mc = new LsModelCreator(cliArgs);
		} else if (cliArgs.smooth().compareTo("wb") == 0) {
			mc = new WbModelCreator(cliArgs);
		}

		Model model = mc.createModel();
		ArpaFormatReadWrite fw = new ArpaFormatReadWrite();
		fw.writeToFile(cliArgs, model);
	}

}
