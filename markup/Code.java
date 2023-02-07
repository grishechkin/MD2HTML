package md2html.markup;

import java.util.List;

public class Code extends MarkElement {
    public Code(List<Mark> elements) {
        super(elements);
    }

    @Override
    protected String getStart() {
        return "<code>";
    }

    @Override
    protected String getEnd() {
        return "</code>";
    }
}
