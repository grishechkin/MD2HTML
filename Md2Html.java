package md2html;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import md2html.markup.Header;
import md2html.markup.Mark;
import md2html.markup.Paragraph;
import md2html.markup.Text;

public class Md2Html {
    public static void main(String[] args) {
        try {
            try (BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(args[0]), StandardCharsets.UTF_8))) {
                String line = in.readLine();
                StringBuilder html = new StringBuilder();
                while (line != null) {
                    while (line != null && line.isEmpty()) {
                        line = in.readLine();
                    }
                    if (line == null) {
                        break;
                    }

                    StringBuilder segment = new StringBuilder();
                    segment.append(line);
                    line = in.readLine();
                    while (line != null && !line.isEmpty()) {
                        segment.append("\n");
                        segment.append(line);
                        line = in.readLine();
                    }

                    stringToMark(segment).toHtml(html);
                    html.append('\n');
                }

                try {
                    try (Writer writer = new BufferedWriter(
                            new OutputStreamWriter(
                                    new FileOutputStream(args[1]), StandardCharsets.UTF_8))) {
                        writer.write(new String(html));
                    }
                } catch (IOException e) {
                    System.err.println("Output exception " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Input error " + e.getMessage());
        }
    }

    private static Mark stringToMark(StringBuilder segment) {
        int headerLevel = getHeaderLevel(segment);
        List<String> elements = parseToStrings(segment, headerLevel);

        Mark mark;
        if (headerLevel > 0) {
            mark = new Header(parseToMarks(elements), headerLevel);
        } else {
            mark = new Paragraph(parseToMarks(elements));
        }
        return mark;
    }

    private static List<String> parseToStrings(StringBuilder segment, int headerLevel) {
        List<String> elements = new ArrayList<>();
        StringBuilder block = new StringBuilder();
        int currentPosition = headerLevel + 1;
        while (currentPosition < segment.length()) {
            if (segment.charAt(currentPosition) == '\\') {
                currentPosition++;
                block.append(segment.charAt(currentPosition++));
                continue;
            }
            if (segment.charAt(currentPosition) == '[') {
                if (!block.isEmpty()) {
                    elements.add(block.toString());
                    block.setLength(0);
                }
                elements.add(Character.toString('['));
                currentPosition++;
                continue;
            }
            if (segment.charAt(currentPosition) == ']') {
                currentPosition = linkProcessing(segment, elements, block, currentPosition);
                continue;
            }

            int[] markSize = getMarkSize(segment, currentPosition);
            if (markSize[0] == 0) {
                block.append(MdSymbols.getHtmlCode(segment.substring(currentPosition, currentPosition + markSize[1])));
            } else {
                if (!block.isEmpty()) {
                    elements.add(new String(block));
                    block.setLength(0);
                }
                elements.add(segment.substring(currentPosition, currentPosition + markSize[0]));
            }
            currentPosition += markSize[1];
        }
        if (!block.isEmpty()) {
            elements.add(new String(block));
        }
        return elements;
    }

    private static List<Mark> parseToMarks(List<String> elements) {
        Deque<List<Mark>> marks = new ArrayDeque<>();
        marks.push(new ArrayList<>());
        Deque<String> starts = new ArrayDeque<>();
        for (int i = 0; i < elements.size(); i++) {
            String element = elements.get(i);
            if (MdSymbols.isMark(element)) {
                if (!starts.isEmpty() && MdSymbols.checkPair(starts.peek(), element)) {
                    Mark newMark;
                    if (element.equals("]")) {
                        newMark = MdSymbols.getLink(marks.pop(), elements.get(++i));
                    } else {
                        newMark = MdSymbols.getMark(marks.pop(), element);
                    }
                    marks.peek().add(newMark);
                    starts.pop();
                } else {
                    starts.push(element);
                    marks.push(new ArrayList<>());
                }
            } else {
                marks.peek().add(new Text(element));
            }
        }

        if (!starts.isEmpty()) {
            marks.peek().addAll(starts.stream().map(Text::new).toList());
        }

        return marks.peek();
    }

    private static int[] getMarkSize(StringBuilder segment, int pos) {  // int[0] - size of markdown block
        char currentChar = segment.charAt(pos);                         // int[1] - position shift
        if (!MdSymbols.isMarkCondidate(currentChar)) {
            return new int[]{0, 1};  // No mark
        }
        String currentString = "";
        if (pos + 1 < segment.length()) {
            currentString = segment.substring(pos, pos + 2);
        }

        if (MdSymbols.isMark(currentString)) {
            if (isSingle(segment, pos - 1, pos + 2)) {
                return new int[]{0, 2};  //  " __ " case
            }
            return new int[]{2, 2};  // Mark, lenght = 2
        }
        if (MdSymbols.isMark(currentChar)) {
            if (isSingle(segment, pos - 1, pos + 1)) {
                return new int[]{0, 1};  // " _ " case
            }
            return new int[]{1, 1};  // Mark, length = 1
        }
        return new int[]{0, 1};  // No mark
    }

    private static int linkProcessing(StringBuilder segment, List<String> elements, StringBuilder block, int pos) {
        // returns new position
        if (!block.isEmpty()) {
            elements.add(new String(block));
            block.setLength(0);
        }
        int beginHref = pos + 2;
        while (segment.charAt(pos) != ')') {
            pos++;
        }
        int endHref = pos;
        elements.add("]");
        elements.add(segment.substring(beginHref, endHref));
        pos++;
        return pos;
    }

    private static boolean isSingle(StringBuilder segment, int prefPos, int nextPos) {
        return (prefPos < 0 || Character.isWhitespace(segment.charAt(prefPos))) &&
                (nextPos >= segment.length() || Character.isWhitespace(segment.charAt(nextPos)));
    }

    private static int getHeaderLevel(StringBuilder segment) {
        int i = 0;
        while (i <= 6 && i < segment.length() && segment.charAt(i) == '#') {
            i++;
        }
        if (i == 0 || i > 6) {
            return -1;
        }

        if (i != segment.length() && Character.isWhitespace(segment.charAt(i))) {
            return i;
        } else {
            return -1;
        }
    }
}
