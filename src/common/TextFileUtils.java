package common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.List;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;

/**
 * @author oshai
 */
public class TextFileUtils {

	/**
	 * Change the contents of text file in its entirety, overwriting any
	 * existing text.
	 * 
	 * This style of implementation throws all exceptions to the caller.
	 * 
	 * @param aFile
	 *            is an existing file which can be written to.
	 * @throws IllegalArgumentException
	 *             if param does not comply.
	 * @throws FileNotFoundException
	 *             if the file does not exist.
	 * @throws IOException
	 *             if problem encountered during write.
	 */
	public static void setContents(String aFile, String aContents) {
		setContents(new File(aFile), aContents);
	}

	public static void setContents(File aFile, String aContents) {
		try {
			if (!aFile.exists()) {
				aFile.createNewFile();
			}
			// use buffering
			Writer output = getWriter(aFile);
			try {
				// FileWriter always assumes default encoding is OK!
				output.write(aContents);
			} finally {
				output.close();
			}
		} catch (Exception ex) {
			throw ExceptionUtils.asUnchecked(ex);
		}
	}

	/**
	 * make sure to close writer after use!!!
	 */
	public static OutputStreamWriter getWriter(File aFile) {
		try {
			OutputStreamWriter output = new OutputStreamWriter(
					new FileOutputStream(aFile), Charset.forName("UTF-8")
							.newEncoder());
			return output;
		} catch (Exception ex) {
			throw ExceptionUtils.asUnchecked(ex);
		}
	}

	public static void getContentByLines(String file,
			Predicate<String> linePredicate) {
		try {
			File fileDir = new File(file);

			BufferedReader in = new BufferedReader(new InputStreamReader(
					new FileInputStream(fileDir), "UTF8"));
			String str;
			while ((str = in.readLine()) != null && linePredicate.apply(str)) {
			}
			in.close();
		} catch (Exception e) {
			throw ExceptionUtils.asUnchecked(e);
		}
	}

	public static List<String> getContent(String file) {
		final List<String> $ = Lists.newArrayList();
		Predicate<String> linePredicate = new Predicate<String>() {
			@Override
			public boolean apply(String arg0) {
				$.add(arg0);
				return true;
			}
		};
		getContentByLines(file, linePredicate);
		return $;
	}

}
