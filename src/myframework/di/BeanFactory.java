package myframework.di;

import java.io.*;
import java.lang.reflect.Method;
import java.util.*;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.beanutils.BeanUtils;
import org.w3c.dom.*;

public class BeanFactory {

	private Map<String, Object> beans = new HashMap<String, Object>();
	private NodeList beanNodes;

	public BeanFactory(String configLocation) {

		try {

			InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(
					configLocation);

			Document document = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder().parse(inputStream);

			Element root = document.getDocumentElement();
			beanNodes = root.getElementsByTagName("bean");

			for (int i = 0; i < beanNodes.getLength(); i++) {
				processBeanNode((Element)beanNodes.item(i));
			}


		} catch (Exception e) {

			e.printStackTrace();

		}

	}
	
	
	private void processBeanNode(Element beanElement) throws Exception {
		String id = beanElement.getAttribute("id");
		if (beans.containsKey(id)) return;
		String className = beanElement.getAttribute("class");
		Object beanObject = Class.forName(className).newInstance();
		beans.put(id, beanObject);
		
		NodeList propertiesList = beanElement.getElementsByTagName("property");
		for (int i = 0; i < propertiesList.getLength(); i++) {
			Element property = (Element) propertiesList.item(i);
			String name = property.getAttribute("name");
			String ref = property.getAttribute("ref");
			String value = property.getAttribute("value");
			
			if (!value.equals("")) {
				BeanUtils.copyProperty(beanObject, name, value);
			} else if (!ref.equals("")) {
				Element refElement = getElementById(name);
				if (refElement != null) {
					processBeanNode(refElement);
					BeanUtils.copyProperty(beanObject, name, beans.get(name));
				}
			}
		}
		
		String initMethodName = beanElement.getAttribute("init-method");
		if (!initMethodName.equals("")) {
			Method initMethod = beanObject.getClass().getMethod(initMethodName, new Class[]{});
			initMethod.invoke(beanObject, new Object[]{});
		}
		
	}

	
	private Element getElementById(String name) {
		for (int i = 0; i < beanNodes.getLength(); i++) {
			Element beanElement = (Element)beanNodes.item(i);
			String id = beanElement.getAttribute("id");
			if (id.equals(name)) return beanElement;
		}
		return null;
	}


	@SuppressWarnings("unchecked")
	public <T> T getBean(String name, Class<T> requiredType) {
		return (T) beans.get(name);
	}

}
