package org.jbpm.addons.processor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.services.api.RuntimeDataService;
import org.jbpm.services.api.UserTaskService;
import org.jbpm.services.api.model.ProcessInstanceDesc;
import org.jbpm.services.api.model.UserTaskInstanceDesc;
import org.kie.api.runtime.query.QueryContext;
import org.springframework.context.ApplicationContext;
import org.thymeleaf.Arguments;
import org.thymeleaf.Configuration;
import org.thymeleaf.dom.Element;
import org.thymeleaf.dom.Node;
import org.thymeleaf.processor.IElementNameProcessorMatcher;
import org.thymeleaf.processor.element.AbstractMarkupSubstitutionElementProcessor;
import org.thymeleaf.spring4.context.SpringWebContext;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.standard.expression.StandardExpressions;

import static org.jbpm.addons.util.KieServerDialectUtils.getFragmentName;

public class ProcessInstancesProcessor extends AbstractMarkupSubstitutionElementProcessor {

    private static final String ATTR_NAME = "processinstances";
    private static final String DEFAULT_FRAGMENT_NAME = "kieserverdialect :: showprocessinstances";
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

        Configuration configuration = arguments.getConfiguration();
        IStandardExpressionParser parser =
                StandardExpressions.getExpressionParser(configuration);

        RuntimeDataService runtimeDataService = (RuntimeDataService) appCtx.getBean("runtimeDataService");
        Collection<ProcessInstanceDesc> processInstances = runtimeDataService.getProcessInstances(new QueryContext());

        Map<Long, List<UserTaskInstanceDesc>> createdTasks = new HashMap<Long, List<UserTaskInstanceDesc>>();

        for(ProcessInstanceDesc pdesc : processInstances) {
            List<Long> processinstancetasks = runtimeDataService.getTasksByProcessInstanceId(pdesc.getId());
            for(Long nexttaskid : processinstancetasks) {
                UserTaskInstanceDesc usertaskdesc =  runtimeDataService.getTaskById(nexttaskid);
                if(usertaskdesc.getStatus() != null && (usertaskdesc.getStatus().equals("Created") || usertaskdesc.getStatus().equals("Reserved"))) {
                    if(createdTasks.containsKey(pdesc.getId())) {
                        createdTasks.get(pdesc.getId()).add(usertaskdesc);
                    } else {
                        createdTasks.put(pdesc.getId(), new ArrayList<UserTaskInstanceDesc>());
                        createdTasks.get(pdesc.getId()).add(usertaskdesc);
                    }
                }
            }
        }

        arguments.getContext().getVariables().put("processinstances",
                                                  processInstances);

        arguments.getContext().getVariables().put("createdtasks",
                                                  createdTasks);

        Element container = new Element("div");
        container.setAttribute("th:replace",
                               getFragmentName(element.getAttributeValue("fragment"),
                                               DEFAULT_FRAGMENT_NAME,
                                               parser,
                                               configuration,
                                               arguments));

        List<Node> nodes = new ArrayList<Node>();
        nodes.add(container);
        return nodes;
    }
}

