/* Copyright 2006-2007 Graeme Rocher
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.grails.webflow.context.servlet

import grails.web.mapping.UrlCreator
import grails.web.servlet.mvc.GrailsParameterMap
import org.grails.web.servlet.mvc.GrailsWebRequest

import javax.servlet.http.HttpServletRequest

import grails.web.mapping.UrlMappingsHolder
import org.codehaus.groovy.grails.web.servlet.GrailsApplicationAttributes
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.util.Assert
import org.springframework.webflow.context.servlet.DefaultFlowUrlHandler
import org.springframework.webflow.core.collection.AttributeMap

/**
 * Changes the default FlowUrlHandler to take into account that Grails request run as part of a forward.
 *
 * @author Graeme Rocher
 * @since 1.1
 */
class GrailsFlowUrlHandler extends DefaultFlowUrlHandler implements ApplicationContextAware {

    ApplicationContext applicationContext

    String getFlowId(HttpServletRequest request) {
        request.getAttribute(GrailsApplicationAttributes.CONTROLLER_NAME_ATTRIBUTE) + "/" +
        request.getAttribute(GrailsApplicationAttributes.ACTION_NAME_ATTRIBUTE)
    }

    String createFlowExecutionUrl(String flowId, String flowExecutionKey, HttpServletRequest request) {
        UrlMappingsHolder holder = applicationContext.getBean(UrlMappingsHolder.BEAN_ID)
        def controllerName = request.getAttribute(GrailsApplicationAttributes.CONTROLLER_NAME_ATTRIBUTE)
        GrailsWebRequest webRequest= GrailsWebRequest.lookup(request)
        Map params = webRequest.params
        def newParams = [execution:flowExecutionKey]
        for (entry in params) {
            def key = entry.key
            if (entry.value instanceof GrailsParameterMap) {
                // GrailsParameterMap objects in the GrailsWebRequest parameters are synthetically created
                // and added when the "params" attribute (of a redirect call in a controller, for instance)
                // contains structured keys, e.g., "params: ['customer.id': 123]". In this case a new
                // GrailsParameterMap object will be created and added to the controller's params map
                // with the key/value, customer: [id:123]. These synthetic GrailsParameterMap parameters
                // should not be included in the resulting flow execution URL as they can interfere
                // with the original parameters, 'customer.id' in this case, when the flow execution URL
                // is parsed by the flow execution engine.
                continue
            } else if (key instanceof String) {
                if (key.startsWith("_event") || key == 'execution') continue

                newParams[key] = entry.value
            }
            else {
                newParams[key] = entry.value
            }
        }
		
		String actionName = flowId.substring(flowId.lastIndexOf('/') + 1)
		UrlCreator creator = holder.getReverseMapping(controllerName, actionName, newParams)

        String url = creator.createURL(controllerName, actionName, newParams, 'utf-8')
        return getValidFlowURL(request, url, flowExecutionKey)
    }

    private getValidFlowURL(HttpServletRequest request, String url, String flowExecutionKey = null) {
        if ("GET" != request.method) {
            url = trimParams(url)
            return flowExecutionKey ? "$url?execution=$flowExecutionKey" : url
        }

        return url
    }

    String trimParams(String url) {
        if (url.contains('?')) {
            url = url[0..url.indexOf('?') - 1]
        }
        return url
    }

    String createFlowDefinitionUrl(String flowId, AttributeMap input, HttpServletRequest request) {

        Assert.notNull applicationContext, "Property [applicationContext] must be set!"

        UrlMappingsHolder holder = applicationContext.getBean(UrlMappingsHolder.BEAN_ID)
        def controllerName = request.getAttribute(GrailsApplicationAttributes.CONTROLLER_NAME_ATTRIBUTE)
        Map params = GrailsWebRequest.lookup(request).params
        def newParams = [:]
        newParams.putAll(params)
        newParams.remove('execution')
        def inputParams = input?.asMap()
        if (inputParams) {
            newParams.putAll(inputParams)
        }

        String actionName = flowId.substring(flowId.lastIndexOf('/') + 1)
        UrlCreator creator = holder.getReverseMapping(controllerName, actionName, newParams)

        String url = creator.createURL(controllerName, actionName, newParams, 'utf-8')
        return getValidFlowURL(request, url)
    }
}
