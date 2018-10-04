package org.jbpm.addons.processor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jbpm.services.api.RuntimeDataService;
import org.jbpm.services.api.model.ProcessInstanceDesc;
import org.kie.api.runtime.query.QueryContext;
import org.springframework.context.ApplicationContext;
import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.dom.Node;
import org.thymeleaf.processor.IElementNameProcessorMatcher;
import org.thymeleaf.processor.element.AbstractMarkupSubstitutionElementProcessor;
import org.thymeleaf.spring4.context.SpringWebContext;

public class ProcessInstancesProcessor extends AbstractMarkupSubstitutionElementProcessor {

    private static final String ATTR_NAME = "processinstances";
    private static final int PRECEDENCE = 10000;

    public ProcessInstancesProcessor(String elementName) {
        super(elementName);
    }

    public ProcessInstancesProcessor() {
        super(ATTR_NAME);
    }

    public ProcessInstancesProcessor(IElementNameProcessorMatcher matcher) {
        super(matcher);
    }

    @Override
    public int getPrecedence() {
        return PRECEDENCE;
    }

    @Override
    protected List<Node> getMarkupSubstitutes(
            final Arguments arguments,
            final Element element) {

        ApplicationContext appCtx =
                ((SpringWebContext) arguments.getContext()).getApplicationContext();

        RuntimeDataService runtimeDataService = (RuntimeDataService) appCtx.getBean("runtimeDataService");
        Collection<ProcessInstanceDesc> processInstances = runtimeDataService.getProcessInstances(new QueryContext());
        arguments.getContext().getVariables().put("processinstances",
                                                  processInstances);

        Element container = new Element("div");
        container.setAttribute("th:replace",
                               "kieserverdialect :: showprocessinstances");

        List<Node> nodes = new ArrayList<Node>();
        nodes.add(container);
        return nodes;
    }
}
