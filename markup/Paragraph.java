package md2html.markup;

import java.util.List;

public class Paragraph extends MarkElement {
    public Paragraph(List<Mark> elements) {
        super(elements);
    }

    @Override
    protected String getStart() {
        return "<p>";
    }

    @Override
    protected String getEnd() {
        return "</p>";
    }
}
