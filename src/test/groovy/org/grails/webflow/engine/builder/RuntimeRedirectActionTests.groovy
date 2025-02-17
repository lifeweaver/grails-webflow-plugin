package org.grails.webflow.engine.builder

import grails.util.GrailsWebMockUtil
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.PropertyExpression
import org.grails.web.mapping.DefaultUrlMappingsHolder
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.webflow.test.MockExternalContext
import org.springframework.webflow.test.MockRequestContext

class RuntimeRedirectActionTests extends GroovyTestCase{

    void testRedirectWithPropertyExpression() {
        GrailsWebMockUtil.bindMockWebRequest()

        try {
            def action   = new RuntimeRedirectAction()
            action.controller = "book"
            action.action = "show"
            action.params = [id: new PropertyExpression(new ConstantExpression("flow.id"), "flow.id")]
            action.urlMapper = new DefaultUrlMappingsHolder([])
            def ext = new MockExternalContext()
            def context = new MockRequestContext()
            context.setExternalContext(ext)
            context.getFlowScope().put("id", "1")
            action.execute(context)
            assert "contextRelative:/book/show/1" == ext.getExternalRedirectUrl()

            context.getFlowScope().put("id", "2")
            action.execute(context)
            assert "contextRelative:/book/show/2" == ext.getExternalRedirectUrl()
        }
        finally {
            RequestContextHolder.setRequestAttributes null
        }
    }
}
