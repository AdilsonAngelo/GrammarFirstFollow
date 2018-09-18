package br.ufpe.cin.if688.parsing.analysis;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import br.ufpe.cin.if688.parsing.grammar.Grammar;
import br.ufpe.cin.if688.parsing.grammar.Nonterminal;
import br.ufpe.cin.if688.parsing.grammar.Production;
import br.ufpe.cin.if688.parsing.grammar.Terminal;


public final class SetGenerator {

	public static Map<Nonterminal, Set<GeneralSymbol>> getFirst(Grammar g) {

		if (g == null) throw new NullPointerException("g nao pode ser nula.");

		Map<Nonterminal, Set<GeneralSymbol>> first = initializeNonterminalMapping(g);

		/*
		 * Implemente aqui o método para retornar o conjunto first
		 */

		for(Nonterminal nt : g.getNonterminals()) {
			first.get(nt).addAll(getFirstAux(nt, g.getProductions()));
		}

		System.out.println(first);

		return first;

	}

	private static Set<GeneralSymbol> getFirstAux(Nonterminal nt, Collection<Production> prods) {
		Set<GeneralSymbol> res = new HashSet<GeneralSymbol>();

		for(Production prod : prods) {
			if(!prod.getNonterminal().equals(nt)) continue;

			GeneralSymbol gs = prod.getProduction().get(0);
			if(gs instanceof Terminal || SpecialSymbol.EPSILON.equals(gs)) {
				res.add(gs);
			} else if(gs instanceof Nonterminal) {
				boolean flag = false;

				for(int i = 0; i < prod.getProduction().size(); i++) {
					GeneralSymbol gs2 = prod.getProduction().get(i);	
					if(!(gs2 instanceof Nonterminal)) break;

					Set<GeneralSymbol> y = getFirstAux((Nonterminal) gs2, prods);

					res.addAll(y);

					flag = y.contains(SpecialSymbol.EPSILON);
					if(!flag) break;
				}

				if(flag) res.add(SpecialSymbol.EPSILON);
				else res.remove(SpecialSymbol.EPSILON);
			}
		}

		return res;
	}

	public static Map<Nonterminal, Set<GeneralSymbol>> getFollow(Grammar g, Map<Nonterminal, Set<GeneralSymbol>> first) {

		if (g == null || first == null)
			throw new NullPointerException();

		Map<Nonterminal, Set<GeneralSymbol>> follow = initializeNonterminalMapping(g);

		/*
		 * implemente aqui o método para retornar o conjunto follow
		 */

		Iterator<Production> it = g.getProductions().iterator();
		Production S = it.next();
		follow.get(S.getNonterminal())
			.add(SpecialSymbol.EOF);

		long size = 0;
		boolean stop = false;

		do {

			for(Nonterminal nt : g.getNonterminals()) {
				
				for(Production prod : g.getProductions()) {
					
					if(!prod.getProduction().contains(nt)) continue;
					
					for(int i = 0; i < prod.getProduction().size(); i++) {
						
						if(!prod.getProduction().get(i).equals(nt)) continue;

						if(i < prod.getProduction().size()-1) {
							
							GeneralSymbol next = prod.getProduction().get(i+1);
							
							if(next instanceof Terminal) {
							
								follow.get(nt).add(next);
							
							} else if(next instanceof Nonterminal) {
								
								Set<GeneralSymbol> firstSet = new HashSet<GeneralSymbol>();
								
								firstSet.addAll(getFirstAux((Nonterminal)next, g.getProductions()));
								
								if(firstSet.contains(SpecialSymbol.EPSILON)) {
									follow.get(nt).addAll(follow.get(next));
								}
								
								firstSet.remove(SpecialSymbol.EPSILON);
								follow.get(nt).addAll(firstSet);
							}
						} else {
							follow.get(nt).addAll(follow.get(prod.getNonterminal()));
						}
					}
				}
			}

			long tempSize = 0;
			for(Map.Entry<Nonterminal, Set<GeneralSymbol>> entry : follow.entrySet())
					tempSize += entry.getValue().size();

			if(tempSize == size)
				stop = true;
			size = tempSize;
		} while(!stop);

		System.out.println(follow);

		return follow;
	}

	//método para inicializar mapeamento nãoterminais -> conjunto de símbolos
	private static Map<Nonterminal, Set<GeneralSymbol>>
	initializeNonterminalMapping(Grammar g) {
		Map<Nonterminal, Set<GeneralSymbol>> result =
				new HashMap<Nonterminal, Set<GeneralSymbol>>();

		for (Nonterminal nt: g.getNonterminals())
			result.put(nt, new HashSet<GeneralSymbol>());

		return result;
	}

}