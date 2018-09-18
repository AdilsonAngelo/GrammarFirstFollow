package br.ufpe.cin.if688.table;


import br.ufpe.cin.if688.parsing.analysis.*;
import br.ufpe.cin.if688.parsing.grammar.*;
import java.util.*;


public final class Table {
	private Table() {    }

	public static Map<LL1Key, List<GeneralSymbol>> createTable(Grammar g) throws NotLL1Exception {
		if (g == null) throw new NullPointerException();

		System.out.print(g);

		Map<Nonterminal, Set<GeneralSymbol>> first =
				SetGenerator.getFirst(g);
		Map<Nonterminal, Set<GeneralSymbol>> follow =
				SetGenerator.getFollow(g, first);

		Map<LL1Key, List<GeneralSymbol>> parsingTable =
				new HashMap<LL1Key, List<GeneralSymbol>>();

		/*
		 * Implemente aqui o método para retornar a parsing table
		 */

		for(Production prod : g.getProductions()) {
			List<GeneralSymbol> gss = prod.getProduction();

			if(gss.get(0) instanceof Terminal || SpecialSymbol.EOF.equals(gss.get(0))) {
				LL1Key key = new LL1Key(prod.getNonterminal(), gss.get(0));

				if(parsingTable.containsKey(key)) throw new NotLL1Exception("");

				parsingTable.put(key, gss);
			} else if(gss.get(0) instanceof Nonterminal) {
				Set<GeneralSymbol> s = first.get(gss.get(0));

				for(GeneralSymbol a : s) {
					LL1Key key = new LL1Key(prod.getNonterminal(), a);

					if(parsingTable.containsKey(key)) throw new NotLL1Exception("");

					parsingTable.put(key, gss);
				}

				if(s.contains(SpecialSymbol.EPSILON)) {
					for(GeneralSymbol b : follow.get(prod.getNonterminal())) {
						LL1Key key = new LL1Key(prod.getNonterminal(), b);

						if(parsingTable.containsKey(key)) throw new NotLL1Exception("");

						parsingTable.put(key, gss);
					}
				}
			} else if(gss.get(0).equals(SpecialSymbol.EPSILON)) {
				for(GeneralSymbol b : follow.get(prod.getNonterminal())) {
					LL1Key key = new LL1Key(prod.getNonterminal(), b);

					if(parsingTable.containsKey(key)) throw new NotLL1Exception("");

					parsingTable.put(key, gss);
				}
			}
		}

		System.out.println(parsingTable);

		return parsingTable;
	}
}