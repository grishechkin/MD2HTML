package md2html.markup;

import java.util.List;

public class Emphasis extends MarkElement {
    public Emphasis(List<Mark> elements) {
        super(elements);
    }

    @Override
    protected String getStart() {
        return "<em>";
    }

    @Override
    protected String getEnd() {
        return "</em>";
    }
}
