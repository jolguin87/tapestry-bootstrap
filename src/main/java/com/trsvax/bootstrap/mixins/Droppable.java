package com.trsvax.bootstrap.mixins;

import org.apache.tapestry5.ClientElement;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.annotations.AfterRender;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.MixinAfter;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.corelib.components.Grid;
import org.apache.tapestry5.dom.Element;
import org.apache.tapestry5.dom.Visitor;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;
import org.got5.tapestry5.jquery.ImportJQueryUI;


@ImportJQueryUI(value = {"jquery.ui.widget", "jquery.ui.mouse", "jquery.ui.droppable"})
@Import(library = { "classpath:com/trsvax/bootstrap/assets/mixins/droppable/droppable.js" })
@MixinAfter
public class Droppable {	
	@Parameter
	Object context;
	@Parameter(defaultPrefix="literal")
	String event;
	@Parameter(defaultPrefix="literal")
	String zoneSelector;
	
	@Inject
	JavaScriptSupport javaScriptSupport;
	
	@Parameter
	private String elementName;
	
	@Inject
	ComponentResources resources;
	
	private Element element;
	
	@AfterRender
	void afterRender(MarkupWriter writer) {
		String id = null;
		if ( elementName == null ) {
			elementName = "ul";
		}
		if ( event == null ) {
			event = "drop";
		}
		Object compoment =  resources.getContainer();
		if ( ClientElement.class.isAssignableFrom(compoment.getClass()) ) {
			id = ((ClientElement)compoment).getClientId();
		} else {
			id = javaScriptSupport.allocateClientId(resources);
		}
		if ( Grid.class.isAssignableFrom(compoment.getClass()) ) {
			elementName = "tbody";
		}
		
		element = writer.getElement();
		
		if ( elementName != null ) {
			element.visit( new Visitor() {
				
				public void visit(Element e) {
					if ( e.getName().equals(elementName)) {
						element = e;
					}
					if ( e.getName().equals("tr"))  {
						String c = e.getAttribute("class");
						if ( c != null ) {
							e.forceAttributes("id",c.split(" ")[0]);
						}
					}
					
				}
			});
		}
		if ( element != null ) {
			String currentID = element.getAttribute("id");
			if ( currentID != null ) {
				id = currentID;
			} else {
				element.forceAttributes("id",id);
			}	
		}
		String link = resources.getContainerResources().createEventLink(event).toAbsoluteURI();
		if ( context != null ) {
				resources.getContainerResources().createEventLink(event,context).toAbsoluteURI();
		}
		JSONObject params = new JSONObject();
		//spec.put("disabled",false);
		//spec.put("accept", "*");
		params.put("activeClass", "ui-state-default");
		//spec.put("addClasses",true);
		//spec.put("greedy",false);
		params.put("hoverClass","ui-state-hover");
		//spec.put("scope","default");
		//spec.put("tolerance","intersect");
		
		JSONObject spec = new JSONObject();

		if ( zoneSelector != null ) {
			spec.put("zoneSelector", zoneSelector);
		}
		spec.put("params", params);
		spec.put("selector", "#"+id);
		spec.put("BaseURL",link);
		javaScriptSupport.addInitializerCall("jqDroppable", spec);
		
	}

}