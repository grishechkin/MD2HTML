package md2html.markup;

import java.util.List;

public class Header extends MarkElement {
    final private int level;

    public Header(List<Mark> elements, int level) {
        super(elements);
        this.level = level;
    }

    @Override
    protected String getStart() {
        return "<h" + level + ">";
    }

    @Override
    protected String getEnd() {
        return "</h" + level + ">";
    }
}
