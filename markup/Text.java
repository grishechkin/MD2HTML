package md2html.markup;

public class Text implements Mark {
    private final String text;

    public Text(String text) {
        this.text = text;
    }

    @Override
    public void toHtml(StringBuilder string) {
        string.append(text);
    }
}
