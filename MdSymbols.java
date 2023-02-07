package md2html;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import md2html.markup.Code;
import md2html.markup.Emphasis;
import md2html.markup.Link;
import md2html.markup.Mark;
import md2html.markup.Strikeout;
import md2html.markup.Strong;
import md2html.markup.Text;

public class MdSymbols {
    private static final Set<Character> MARKDOWN_SYMBOLS = Set.of('*', '`', '_');
    private static final Set<Character> MARKDOWN_CANDIDATES = new HashSet<>();
    private static final Map<String, String> MARKDOWN_PAIRS = new HashMap<>();
    private static final Set<String> MARKDOWN_STRINGS = new HashSet<>();
    private static final Map<String, String> HTML_SPECIAL_CASES = new HashMap<>();

    static {
        MARKDOWN_PAIRS.put("**", "**");
        MARKDOWN_PAIRS.put("*", "*");
        MARKDOWN_PAIRS.put("_", "_");
        MARKDOWN_PAIRS.put("__", "__");
        MARKDOWN_PAIRS.put("--", "--");
        MARKDOWN_PAIRS.put("`", "`");
        MARKDOWN_PAIRS.put("[", "]");

        MARKDOWN_CANDIDATES.addAll(MARKDOWN_SYMBOLS);
        for (String mdString : MARKDOWN_PAIRS.keySet()) {
            MARKDOWN_CANDIDATES.add(mdString.charAt(0));
        }

        MARKDOWN_STRINGS.addAll(MARKDOWN_PAIRS.keySet());
        MARKDOWN_STRINGS.addAll(MARKDOWN_PAIRS.values());

        HTML_SPECIAL_CASES.put("<", "&lt;");
        HTML_SPECIAL_CASES.put(">", "&gt;");
        HTML_SPECIAL_CASES.put("&", "&amp;");
    }

    public static boolean isMark(char c) {
        return MARKDOWN_SYMBOLS.contains(c);
    }

    public static boolean isMark(String s) {
        return MARKDOWN_STRINGS.contains(s);
    }

    public static boolean isMarkCondidate(char c) {
        return MARKDOWN_CANDIDATES.contains(c);
    }

    public static boolean checkPair(String open, String close) {
        return MARKDOWN_PAIRS.get(open).equals(close);
    }

    public static String getHtmlCode(String s) {
        return HTML_SPECIAL_CASES.getOrDefault(s, s);
    }

    public static Mark getMark(List<Mark> marks, String mark) {
        return switch (mark) {
            case "*", "_" -> new Emphasis(marks);
            case "**", "__" -> new Strong(marks);
            case "--" -> new Strikeout(marks);
            case "`" -> new Code(marks);
            default -> new Text("");
        };
    }

    public static Mark getLink(List<Mark> marks, String href) {
        return new Link(marks, href);
    }
}
