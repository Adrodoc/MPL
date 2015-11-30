package de.adrodoc55.antlr.mpl;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.tree.CommonTree;

import de.adrodoc55.antlr.mpl.MplParser.program_return;

public class AntlrTest {
	public static void main(String[] args) throws RecognitionException {
		String expression = "repeat   , chain : /ger eaf H 0 = we98 elloword\n";
		ANTLRStringStream input = new ANTLRStringStream(expression);
		MplLexer lexer = new MplLexer(input);
//		while (true) {
//			Token token = lexer.nextToken();
//			if (token.getType() == Token.EOF) {
//				break;
//			}
//
//			System.out.println("Token: ‘" + token.getText() + "’");
//		}
		TokenStream tokens = new CommonTokenStream(lexer);
		MplParser parser = new MplParser(tokens);
		program_return ret = parser.program();
		CommonTree ast = (CommonTree) ret.getTree();
		System.out.println(ast.toStringTree());
//		List<? extends Object> children = ast.toStringTree()
//		for (Object o : children) {
//			CommonTree tree = o;
//			for(Object c : tree.) {
//				System.out.println(o.getClass());
//				System.out.println(o);
//			}
//			System.out.println(o.getClass());
//			System.out.println(o);
//		}

//		System.out.println(ast.getText());
	}
}
