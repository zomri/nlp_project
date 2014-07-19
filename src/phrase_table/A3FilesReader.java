package phrase_table;

import java.util.List;

import com.google.common.collect.Lists;
import common.TextFileUtils;

/**
 * Simply reads corresponding lines from given A3 files
 * 
 * @author zomri
 *
 */


public class A3FilesReader {
	
	List<A3SentenceContainer> readSentenceAndAlignment(String A3file) {
		
		List<A3SentenceContainer> listOfPairs = Lists.newArrayList();
		
		int size = -1;
		List<String> content = TextFileUtils.getContent(A3file);
		if (size == -1) {
			size = content.size();
		}	
		else {
			if (size != content.size()) {
				throw new RuntimeException("file length not match " + size + " " + content.size());
			}
		}
		
		//TODO - read every 2 lines (skip first every time)
		for (int i=0; i<size; ++i) 
		{
			listOfPairs.add(new A3SentenceContainer(content.get(i++),content.get(i++), content.get(i)));
		}
		
		return listOfPairs;	
	}
	
}
