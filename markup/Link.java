package md2html.markup;

import java.util.List;

public class Link extends MarkElement {
    private final String href;

    public Link(List<Mark> elements, String href) {
        super(elements);
        this.href = href;
    }

    @Override
    protected String getStart() {
        return "<a href='" + href + "'>";
    }

    @Override
    protected String getEnd() {
        return "</a>";
    }
}
