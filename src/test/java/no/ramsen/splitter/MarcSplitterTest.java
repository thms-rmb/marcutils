package no.ramsen.splitter;

import org.junit.Test;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParserFactory;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static org.junit.Assert.assertTrue;

public class MarcSplitterTest {
    private class RecordCounter extends DefaultHandler {
        private Map<ContentHandler, Integer> counts;
        public RecordCounter(Map<ContentHandler, Integer> counts) {
            this.counts = counts;
            this.counts.put(this, 0);
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            if (localName.equals("record")) {
                var currentCount = this.counts.get(this);
                this.counts.put(this, currentCount + 1);
            }
        }
    }

    public static String testFile = "src/test/xml/unov.xml";

    @Test
    public void testCountRecords() throws Exception {
        var records = new InputSource(new FileInputStream(MarcSplitterTest.testFile));
        Map<ContentHandler, Integer> counts = new HashMap<>();

        Supplier<ContentHandler> getHandlers = () -> new RecordCounter(counts);

        var readerFactory = SAXParserFactory.newDefaultInstance();
        readerFactory.setNamespaceAware(true);
        var reader = readerFactory.newSAXParser();

        var splitter = new MarcSplitter(reader.getXMLReader(), getHandlers, 1);
        splitter.parse(records);

        for (var count : counts.values()) {
            assertTrue(1 == count);
        }
    }

    @Test
    public void testCountRecordsGroupsOf() throws Exception {
        var records = new InputSource(new FileInputStream(MarcSplitterTest.testFile));
        Map<ContentHandler, Integer> counts = new HashMap<>();

        Supplier<ContentHandler> getHandlers = () -> new RecordCounter(counts);

        var readerFactory = SAXParserFactory.newDefaultInstance();
        readerFactory.setNamespaceAware(true);
        var reader = readerFactory.newSAXParser();

        var splitter = new MarcSplitter(reader.getXMLReader(), getHandlers, 2);
        splitter.parse(records);

        for (var count : counts.values()) {
            assertTrue(2 >= count);
        }
    }
}
