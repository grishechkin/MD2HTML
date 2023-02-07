package md2html.markup;

import java.util.List;

public class Strong extends MarkElement {
    public Strong(List<Mark> elements) {
        super(elements);
    }

    @Override
    protected String getStart() {
        return "<strong>";
    }

    @Override
    protected String getEnd() {
        return "</strong>";
    }
}
