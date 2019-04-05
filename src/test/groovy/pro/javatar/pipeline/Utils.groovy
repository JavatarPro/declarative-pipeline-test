package pro.javatar.pipeline

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import groovy.json.JsonSlurper
import groovy.text.GStringTemplateEngine
import org.apache.commons.io.IOUtils
import org.apache.commons.lang3.Validate
import javax.ws.rs.core.HttpHeaders
import javax.ws.rs.core.MediaType
import static org.junit.Assert.assertTrue

class Utils<T> {

    static ConfigObject loadConfig(String configName) {
        String env = System.getProperty("test_env", "local")
        return loadConfig(configName, env)
    }

    static ConfigObject loadConfig(String configName, String env) {
        Validate.notBlank(env)
        URL url = Utils.class.getClassLoader().getResource(configName)
        return new ConfigSlurper(env).parse(url)
    }

    //returns Template from specified templatePath as String with replaced variables from specified binding
    static def renderTemplateFromFile(String templatePath, Map binding) throws FileNotFoundException {
        String templateFile = IOUtils.toString(Utils.class.getClassLoader().getResourceAsStream(templatePath))
        def engine = new GStringTemplateEngine()
        def template = engine.createTemplate(templateFile).make(binding)
        return template.toString()
    }

    static def renderTemplate(String classpath, Map binding) throws FileNotFoundException {
        String content = IOUtils.toString(Utils.class.getClassLoader().getResourceAsStream(classpath))
        return applyVariablesBinding(content);
    }

    static boolean compareJsonStrings(String json1, String json2) {
        JsonParser parser = new JsonParser();
        JsonElement jsonObj1 = parser.parse(json1)
        JsonElement jsonObj2 = parser.parse(json2)
        return jsonObj1.equals(jsonObj2)
    }

    static InputStream getResourceStream(String classpath) {
        return Utils.class.getClassLoader().getResourceAsStream(classpath)
    }

    static String getFileAsString(String classpath) {
        return IOUtils.toString(Utils.class.getClassLoader().getResourceAsStream(classpath))
    }

    def static getFileAsString(classpath, Map vars) {
        def body = IOUtils.toString(Utils.class.getClassLoader().getResourceAsStream(classpath))
        return applyVariablesBinding(body, vars)
    }

    static String applyVariablesBinding(String body, Map binding) {
        def engine = new GStringTemplateEngine()
        def template = engine.createTemplate(body).make(binding)
        return template.toString()
    }

    static <T> T getFileAsJson(String classpath) {
        def content = IOUtils.toString(Utils.class.getClassLoader().getResourceAsStream(classpath))
        def parser = new JsonSlurper()
        return parser.parseText(content)
    }

    static <T> T getFileAsXml(String classpath) {
        def content = IOUtils.toString(Utils.class.getClassLoader().getResourceAsStream(classpath))
        def parser = new XmlSlurper()
        return parser.parseText(content)
    }

    static String getFullFileName(String classpath) {
        return Utils.class.getClassLoader().getResource(classpath).getFile()
    }

    static File getFile(String classpath) {
        File file = new File(getFullFileName(classpath))
        if (file == null || !file.exists()) return null
        return file
    }

    //returns converted JSON data into class that describes those
    static T convert(def data, Class<T> clazz) {
        return new ObjectMapper().readValue(new ObjectMapper().writeValueAsString(data), clazz)
    }

    static String convert(def object) {
        return new ObjectMapper().writeValueAsString(object)
    }

    static void assertContentType(def resp) {
        assertContentType(resp, MediaType.APPLICATION_JSON)
    }

    static void assertContentType(def resp, String mimeType) {
        assertTrue(resp.headers."${HttpHeaders.CONTENT_TYPE}".equalsIgnoreCase(mimeType))
    }

    static def getValueByNameFromJsonFile(String propertyName, String classpath) {
        def jsonSlurper = new JsonSlurper()
        Map jsonAsMap = jsonSlurper.parseText(Utils.getFileAsString(classpath))
        return jsonAsMap.get(propertyName)
    }

    //returns value/values of property within JSON array which is the value of another JSON property by names
    static def getValueByNameFromJsonFile(String propertyName, String propertyWithinArrayName, String classpath) {
        def jsonSlurper = new JsonSlurper()
        List values = new ArrayList()
        Map jsonAsMap = jsonSlurper.parseText(Utils.getFileAsString(classpath))
        for (Map array : jsonAsMap.get(propertyName)) {
            values.add(array.get(propertyWithinArrayName))
        }
        return values.size() > 1 ? values : values.get(0)
    }
}