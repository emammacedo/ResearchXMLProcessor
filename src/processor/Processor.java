package processor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import generated.Catalog;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.util.JAXBSource;

import org.xml.sax.SAXException;

public class Processor {

	public static void main(String[] args) throws FileNotFoundException {

		isValid("data_and_stats.xml", "data_and_stats.xsd");

		Catalog catalogO = read("selector_output.xml");
		Catalog catalogS = read("data_and_stats.xml");

		int n_jc = 0;
		int n_cit = 0;
		List<String> top = new ArrayList();
		List<Integer> top_cit = new ArrayList();
		String hAuthor = "";
		int hAuthorCits = 0;

		int cits_pub = 0;
		int author_cits = 0;

		for (int researcher = 0; researcher < catalogO.getResearcher().size(); researcher++) {
			author_cits = 0;

			for (int pub = 0; pub < catalogO.getResearcher().get(researcher).getPublication().size(); pub++) {
				if (!catalogO.getResearcher().get(researcher).getPublication().get(pub).getConference().equals("")
						|| !catalogO.getResearcher().get(researcher).getPublication().get(pub).getJournal()
								.equals("")) {
					n_jc++;
				}
				cits_pub = catalogO.getResearcher().get(researcher).getPublication().get(pub).getCitations().intValue();
				n_cit = n_cit + cits_pub;
				author_cits = author_cits + cits_pub;

				if (top_cit.size() < 3) {
					top_cit.add(cits_pub);
					top.add(catalogO.getResearcher().get(researcher).getPublication().get(pub).getTitle());
				} else {
					int smallestInd = 0;
					for (int i = 1; i < 3; i++) {
						if (top_cit.get(i) < top_cit.get(smallestInd)) {
							smallestInd = i;
						}
					}
					if (cits_pub > top_cit.get(smallestInd)) {
						top_cit.remove(smallestInd);
						top.remove(smallestInd);
						top_cit.add(cits_pub);
						top.add(catalogO.getResearcher().get(researcher).getPublication().get(pub).getTitle());
					}
				}
			}

			if (author_cits > hAuthorCits) {
				hAuthorCits = author_cits;
				hAuthor = catalogO.getResearcher().get(researcher).getName();
			}
		}

		for (int i = catalogS.getStatistics().getTop().size() - 1; i >= 0; i--) {
			catalogS.getStatistics().getTop().remove(i);
		}

		BigInteger noRes = new BigInteger(String.valueOf(catalogO.getResearcher().size()));
		BigInteger B_n_jc = new BigInteger(String.valueOf(n_jc));
		BigInteger B_n_cit = new BigInteger(String.valueOf(n_cit));
		catalogS.getStatistics().setNoResearchers(noRes);
		catalogS.getStatistics().setNoJournalsConferences(B_n_jc);
		catalogS.getStatistics().setTotalCitations(B_n_cit);
		catalogS.getStatistics().setHighestCitedAuthor(hAuthor);

		for (int v = 0; v < top_cit.size(); v++) {
			int toptop = 0;
			for (int i = 1; i < top_cit.size(); i++) {
				if (top_cit.get(i) > top_cit.get(toptop)) {
					toptop = i;
				}
			}
			catalogS.getStatistics().getTop().add(top.get(toptop));
			top_cit.set(toptop,-1);
		}

		for (int researcher = catalogS.getResearcher().size() - 1; researcher >= 0; researcher--) {
			catalogS.getResearcher().remove(researcher);
		}

		for (int researcher = 0; researcher < catalogO.getResearcher().size(); researcher++) {
			catalogS.getResearcher().add(catalogO.getResearcher().get(researcher));
		}

		write(catalogS, "processor_output.xml");
		transformXMLToHTML(catalogS, "structure_tab.xsl", "html_output.html");
	}

	
	public static Catalog read(String fileName) {

		Catalog catalog = null;
		File xmlFile = new File(fileName);

		JAXBContext jaxbContext;

		try {
			jaxbContext = JAXBContext.newInstance(Catalog.class);

			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

			catalog = (Catalog) jaxbUnmarshaller.unmarshal(xmlFile);

		} catch (JAXBException e) {
			e.printStackTrace();
		}

		return catalog;
	}

	
	public static void write(Catalog catalog, String fileName) throws FileNotFoundException {
		try {
			JAXBContext contextObj = JAXBContext.newInstance(Catalog.class);

			Marshaller marshallerObj = contextObj.createMarshaller();
			marshallerObj.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			marshallerObj.marshal(catalog, new FileOutputStream(fileName));
		} catch (JAXBException e) {
			e.printStackTrace();
		}

	}


	public static void isValid(String xmlPath, String xsdPath) {

		try {

			SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema schema = factory.newSchema(new File(xsdPath));

			Validator validator = schema.newValidator();
			validator.validate(new StreamSource(new File(xmlPath)));

			System.out.println("Success! XML file is valid.\n");

		} catch (IOException | SAXException e) {
			System.out.println("Exception: " + e.getMessage() + "\n");
			System.exit(1);
		}
	}


	public static void transformXMLToHTML(Catalog catalog, String xsltFile, String htmlFile) {

		try {

			// Create Transformer
			TransformerFactory tf = TransformerFactory.newInstance();
			StreamSource xslt = new StreamSource(xsltFile);
			Transformer transformer = tf.newTransformer(xslt);

			// Source
			JAXBContext jaxb = JAXBContext.newInstance(Catalog.class);
			JAXBSource source = new JAXBSource(jaxb, catalog);

			// Result
			StreamResult result = new StreamResult(new File(htmlFile));

			// Transform
			transformer.transform(source, result);

		} catch (Exception e) {

			e.printStackTrace();
		}

	}

}
