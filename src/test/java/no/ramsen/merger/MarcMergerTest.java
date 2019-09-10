package no.ramsen.merger;

import org.junit.Test;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParserFactory;
import java.io.FileInputStream;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class MarcMergerTest {
    private class RecordCounter extends DefaultHandler {
        int counts;

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            if (localName.equals("record")) {
                this.counts++;
            }
        }

        RecordCounter() {
            this.counts = 0;
        }
    }

    private static String testFile = "src/test/xml/unov.xml";

    @Test
    public void testCountRecords() throws Exception {
        var recordsSet = List.of(
                new InputSource(new FileInputStream(MarcMergerTest.testFile)),
                new InputSource(new FileInputStream(MarcMergerTest.testFile)),
                new InputSource(new FileInputStream(MarcMergerTest.testFile)));

        var readerFactory = SAXParserFactory.newDefaultInstance();
        readerFactory.setNamespaceAware(true);
        var reader = readerFactory.newSAXParser();

        var merger = new MarcMerger(reader.getXMLReader());
        var counter = new RecordCounter();

        merger.setContentHandler(counter);
        merger.beginMerge();
        for (var records : recordsSet) {
            merger.parse(records);
        }
        merger.endMerge();

        assertEquals(30, counter.counts);
    }
}
