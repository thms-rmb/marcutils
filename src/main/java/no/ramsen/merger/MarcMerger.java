package no.ramsen.merger;

import no.ramsen.splitter.MarcSplitter;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.XMLFilterImpl;

import java.util.List;

public class MarcMerger extends XMLFilterImpl {
    private String uri = "http://www.loc.gov/MARC21/slim";
    private static List<String> prefixes = List.of("", "marc");

    public void beginMerge() throws SAXException {
        super.startDocument();
        for (var prefix : MarcMerger.prefixes) {
            this.startPrefixMapping(prefix, this.uri);
        }
        super.startElement(this.uri, "collection", "collection", new AttributesImpl());
    }

    public void endMerge() throws SAXException {
        super.endElement(this.uri, "collection", "collection");
        for (int i = MarcMerger.prefixes.size() - 1; i >= 0; i--) {
            var prefix = MarcMerger.prefixes.get(i);
            this.endPrefixMapping(prefix);
        }
        super.endDocument();
    }

    @Override
    public void startPrefixMapping(String prefix, String uri) throws SAXException { }

    @Override
    public void endPrefixMapping(String prefix) throws SAXException { }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        if (!localName.equals("collection")) {
            super.startElement(uri, localName, qName, atts);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (!localName.equals("collection")) {
            super.endElement(uri, localName, qName);
        }
    }

    @Override
    public void startDocument() { }

    @Override
    public void endDocument() { }

    public MarcMerger() {
        super();
    }

    public MarcMerger(XMLReader parent) {
        super(parent);
    }
}
