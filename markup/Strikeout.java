package md2html.markup;

import java.util.List;

public class Strikeout extends MarkElement {
    public Strikeout(List<Mark> elements) {
        super(elements);
    }

    @Override
    protected String getStart() {
        return "<s>";
    }

    @Override
    protected String getEnd() {
        return "</s>";
    }
}
