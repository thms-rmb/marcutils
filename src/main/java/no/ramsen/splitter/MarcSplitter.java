package no.ramsen.splitter;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.XMLFilterImpl;

import java.util.List;
import java.util.function.Supplier;

public class MarcSplitter extends XMLFilterImpl {
    private static String uri = "http://www.loc.gov/MARC21/slim";
    private static List<String> prefixes = List.of("", "marc");

    private Supplier<ContentHandler> getHandler;
    private int recordsProcessed = 0;
    private int groupsOf;

    public MarcSplitter(Supplier<ContentHandler> getHandler, int groupsOf) {
        this.getHandler = getHandler;
        this.groupsOf = groupsOf;

        this.newHandler();
    }

    public MarcSplitter(XMLReader parent, Supplier<ContentHandler> getHandler, int groupsOf) {
        super(parent);
        this.getHandler = getHandler;
        this.groupsOf = groupsOf;

        this.newHandler();
    }

    private void newHandler() {
        var handler = this.getHandler.get();
        this.setContentHandler(handler);
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        if (localName.equals("record") && this.recordsProcessed > 0 && this.recordsProcessed % this.groupsOf == 0) {
            this.newHandler();
            this.startDocument();
            for (var prefix : MarcSplitter.prefixes) {
                this.startPrefixMapping(prefix, MarcSplitter.uri);
            }
            super.startElement(uri, "collection", "collection", new AttributesImpl());
        }

        if (!localName.equals("collection")) {
            super.startElement(uri, localName, qName, atts);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (localName.equals("record")) {
            super.endElement(uri, localName, qName);
            this.recordsProcessed++;
            if (this.recordsProcessed % this.groupsOf == 0) {
                super.endElement(uri, "collection", "collection");
                for (int i = MarcSplitter.prefixes.size() - 1; i >= 0; i--) {
                    var prefix = MarcSplitter.prefixes.get(i);
                    this.endPrefixMapping(prefix);
                }
                super.endDocument();
            }
        }

        else if (!localName.equals("collection")) {
            super.endElement(uri, localName, qName);
        }
    }

    @Override
    public void endDocument() throws SAXException {}
}