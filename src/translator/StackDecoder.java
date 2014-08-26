package translator;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import com.beust.jcommander.internal.Lists;

public class StackDecoder {

	private List<String> origin;
	
	private List<SortedSet<Hypothesis>> stacks = Lists.newArrayList();
	
	public StackDecoder(List<String> origin) {
		super();
		this.origin = origin;
		for (int i = 0; i < origin.size(); i++) {
			stacks.add(new TreeSet<>());
		}
	}

	public List<String> translate(){
		stacks.get(0).add(new Hypothesis());
		for (int stackIndex = 0; stackIndex < stacks.size(); stackIndex++) {
			for (Hypothesis hypothesis : stacks.get(stackIndex)) {
//				for all translation options do
//					if applicable then
//						create new hypothesis
//						place in stack
//						recombine with existing hypothesis if possible
//						prune stack if too big
//					end if
//				end for
			}
		}
		return getTranslationFromStacks();
	}



	private List<String> getTranslationFromStacks() {
		List<String> translation = Lists.newArrayList();
		return translation;
	}
}
