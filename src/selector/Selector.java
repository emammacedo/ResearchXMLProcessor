package selector;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.SAXException;

import generated.Catalog;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import java.io.FileOutputStream;
import java.io.IOException;

public class Selector {

	public static void main(String[] args) throws FileNotFoundException {

		isValid("data.xml", "data.xsd");

		Catalog catalog = read("data.xml");
		Scanner scan = new Scanner(System.in);

		int hostInst_yn = verifyZeroOne(scan, "Do you wish to filter the researchers by host instituition?");

		String hostInst = "error";

		if (hostInst_yn == 1) {
			System.out.println("Which host instituition? ");
			scan.nextLine();
			hostInst = scan.nextLine();
		}

		int researchInt_yn = verifyZeroOne(scan, "Do you wish to filter the researchers by research insterests?");

		List<String> researchInt = new ArrayList();

		if (researchInt_yn == 1) {
			researchInt.add("1");
			scan.nextLine();

			while (!researchInt.get(researchInt.size() - 1).equals("0")) {
				System.out.println("Insert a reasearch interest. To exit insert '0'.");
				researchInt.add(scan.nextLine());
			}

			researchInt.remove(0);
			researchInt.remove(researchInt.size() - 1);

			if (researchInt.size() == 0) {
				researchInt_yn = 0;
			}
		}

		int citations_yn = verifyZeroOne(scan, "Do you wish to filter the researchers by number of citations?");
		int citations = -5;
		while (citations_yn == 1 && citations < 0) {
			System.out.println("What is the minimum number of citations that should be considered? ");
			if (scan.hasNextInt()) {
				citations = scan.nextInt();
				if (citations<0) {
				System.out.println("The number should be at least 0");
				}
			} else {
				System.out.println("Insert a number.");
				scan.next();
			}
		}

		scan.close();

		System.out.println();

		List<Integer> researchersToRemove = new ArrayList<Integer>();

		boolean hostInstMatch = false;
		int researcherCit = 0;

		for (int researcher = 0; researcher < catalog.getResearcher().size(); researcher++) {
			hostInstMatch = catalog.getResearcher().get(researcher).getHostInstitution().toLowerCase()
					.equals(hostInst.toLowerCase());
			researcherCit = 0;
			for (int pub = 0; pub < catalog.getResearcher().get(researcher).getPublication().size(); pub++) {
				researcherCit = researcherCit
						+ catalog.getResearcher().get(researcher).getPublication().get(pub).getCitations().intValue();
			}

			if (hostInst_yn == 1 && !hostInstMatch) {
				researchersToRemove.add(researcher);
			}

			else if (citations_yn == 1 && researcherCit < citations) {
				researchersToRemove.add(researcher);
			}

			else if (researchInt_yn == 1) {
				Set<String> resInterests = new HashSet<>();
				Set<String> Interests = new HashSet<>();
				for (int i = 0; i < catalog.getResearcher().get(researcher).getResearchInterest().size(); i++) {
					String lowercaseStr = catalog.getResearcher().get(researcher).getResearchInterest().get(i)
							.toLowerCase();
					resInterests.add(lowercaseStr);
				}
				for (int i = 0; i < researchInt.size(); i++) {
					String lowercaseStr = researchInt.get(i).toLowerCase();
					Interests.add(lowercaseStr);
				}

				HashSet<String> commonInterests = new HashSet<>(resInterests);
				commonInterests.retainAll(Interests);

				if (commonInterests.isEmpty()) {
					researchersToRemove.add(researcher);
				}
			}

		}

		for (int i = researchersToRemove.size() - 1; i > -1; i--) {
			int resToRemove = (int) researchersToRemove.get(i);
			catalog.getResearcher().remove(resToRemove);
		}

		write(catalog, "selector_output.xml");

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

	
	public static int verifyZeroOne(Scanner scan2, String question) {
		int yn = 3;
		int answer = 100;

		while (yn != 1 && yn != 0) {
			System.out.print(question);
			System.out.println(" Yes - 1. No - 0");
			if (scan2.hasNextInt()) {
				answer = scan2.nextInt();
				if (answer == 1 || answer == 0) {
					yn = answer;
				} else {
					System.out.println("Insert 1 for Yes and 0 for No.");
					scan2.next();
				}
			} else {
				System.out.println("Insert 1 for Yes and 0 for No.");
				scan2.nextLine();
			}
		}

		return yn;
	}

}
