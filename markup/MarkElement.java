package md2html.markup;

import java.util.List;

abstract class MarkElement implements Mark {
    final private List<Mark> elements;

    MarkElement(List<Mark> elements) {
        this.elements = elements;
    }

    @Override
    public void toHtml(StringBuilder string) {
        string.append(getStart());
        for (Mark element : elements) {
            element.toHtml(string);
        }
        string.append(getEnd());
    }

    abstract protected String getStart();
    abstract protected String getEnd();
}
