package grails.plugins.webflow

import org.grails.webflow.WebFlowPluginSupport

class WebflowGrailsPlugin {
    def observe = ['controllers']
    def loadAfter = ['hibernate','hibernate4']
    def grailsVersion = "2.4 > *"
    def author = "Graeme Rocher"
    def authorEmail = "grocher@gopivotal.com"
    def title = "Spring Web Flow Plugin"
    def description = 'Integrates Spring Web Flow with Grails'
    def documentation = "http://grails-plugins.github.io/grails-webflow-plugin/"

    def license = "APACHE"
    def organization = [name: 'Pivotal', url: 'http://www.gopivotal.com/oss/']
    def issueManagement = [system: 'JIRA', url: 'http://jira.grails.org/browse/GPWEBFLOW']
    def scm = [url: 'https://github.com/grails-plugins/grails-webflow-plugin']

    def doWithSpring = WebFlowPluginSupport.doWithSpring
    def doWithDynamicMethods = WebFlowPluginSupport.doWithDynamicMethods
    def doWithApplicationContext = WebFlowPluginSupport.doWithApplicationContext
    def onChange = WebFlowPluginSupport.onChange
}
