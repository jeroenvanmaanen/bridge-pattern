package org.leialearns.bridge.crossings.far;

import org.leialearns.bridge.BridgeOverride;
import org.leialearns.common.ExceptionWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.leialearns.common.Display.display;
import static org.leialearns.common.Static.getLoggingClass;

public class PuzzleDAO {
    private final Logger logger = LoggerFactory.getLogger(getLoggingClass(this));
    private static final int TAG = 1;
    private static final int TEXT = 3;
    private final Map<String,Class<?>> tagMap = new HashMap<>();
    private final Map<Class<?>,String> defaultMap = new HashMap<>();

    public PuzzleDAO() {
        tagMap.put("puzzle", PuzzleDTO.class);
        tagMap.put("word", WordDTO.class);
        defaultMap.put(WordDTO.class, "setDescription");
    }

    @BridgeOverride
    public PuzzleDTO getPuzzle(RootDTO root, URL location) {
        if (location == null) {
            throw new IllegalArgumentException("The location should not be null");
        }
        Reader reader;
        try {
            InputStream inputStream;
            inputStream = location.openStream();
            reader = new InputStreamReader(inputStream, "UTF-8");
        } catch (Exception exception) {
            logger.warn("URL: {}", location);
            throw ExceptionWrapper.wrap(exception);
        }
        InputSource inputSource = new InputSource(reader);
        return getPuzzle(inputSource);
    }

    @BridgeOverride
    public PuzzleDTO getPuzzle(RootDTO root, String descriptionXml) {
        Reader reader = new StringReader(descriptionXml);
        InputSource inputSource = new InputSource(reader);
        return getPuzzle(inputSource);
    }

    public PuzzleDTO getPuzzle(InputSource inputSource) {
        Document document;
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
            document = builder.parse(inputSource);
        } catch (Exception exception) {
            throw ExceptionWrapper.wrap(exception);
        }
        Collection<?> list = new ArrayList<>();
        getPuzzle("", document, Collection.class, new Context(list, ArrayList.class));
        logger.debug("List size: {}", list.size());
        Object result = list.size() >= 1 ? list.iterator().next() : null;
        logger.debug("Result: {}: #{}", result, System.identityHashCode(result));
        if (result instanceof Collection) {
            list = (Collection<?>) result;
            result = list.size() >= 1 ? list.iterator().next() : null;
            logger.debug("Result: {}: #{}", result, System.identityHashCode(result));
        }
        return (PuzzleDTO) result;
    }

    public Object getPuzzle(String prefix, Node node, Class<?> type, Context context) {
        Class<?> childType = type;
        Context childContext = null;
        String propertyName = null;
        StringBuilder builder = new StringBuilder();
        short nodeType = node.getNodeType();
        builder.append(Short.toString(nodeType));
        builder.append(": ");
        switch (nodeType) {
            case TAG:
                String nodeName = node.getNodeName();
                builder.append('<');
                builder.append(nodeName);
                builder.append('>');
                Object childObject;
                if (tagMap.containsKey(nodeName)) {
                    childType = tagMap.get(nodeName);
                    try {
                        childObject = childType.newInstance();
                    } catch (Exception exception) {
                        throw ExceptionWrapper.wrap(exception);
                    }
                } else {
                    childType = StringBuilder.class;
                    childObject = new StringBuilder();
                }
                propertyName = nodeName;
                childContext = new Context(childObject, childType);
                break;
            case TEXT:
                String textContent = node.getTextContent();
                builder.append(display(textContent));
                context.getBuilder().append(textContent);
                break;
            default:
                childContext = new Context(new ArrayList<>(), Collection.class);
                builder.append("node(#");
                builder.append(nodeType);
                builder.append(")");
        }
        if (childContext == null) {
            childContext = new Context(null, null);
        }
        builder.append(": ");
        builder.append(showType(context.get()));
        builder.append(": ");
        builder.append(showType(childContext.get()));
        logger.info(prefix + builder.toString());
        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node childNode = childNodes.item(i);
            getPuzzle(".  " + prefix, childNode, childType, childContext);
        }
        context.invoke(propertyName, childContext, prefix);
        return context.get();
    }

    protected Method getMethod(Class<?> type, String methodName) {
        Method result = null;
        try {
            int rank = 0;
            Method[] methods = type.getMethods();
            for (Method method : methods) {
                if (method.getName().equals(methodName) && method.getParameterTypes().length == 1) {
                    int newRank = rank(method.getParameterTypes()[0]);
                    if (newRank > rank) {
                        result = method;
                        if (newRank > 2) {
                            break;
                        }
                    }
                }
            }
        } catch (Exception exception) {
            throw ExceptionWrapper.wrap(exception);
        }
        return result;
    }

    protected String showType(Object object) {
        return (object == null ? "null" : object.getClass().getSimpleName());
    }

    protected int rank(Class<?> parameterType) {
        int result;
        if (String.class.isAssignableFrom(parameterType)) {
            result = 3;
        } else if (Integer.class.isAssignableFrom(parameterType)) {
            result = 2;
        } else {
            result = 1;
        }
        return result;
    }

    protected class Context {
        private final Class<?> type;
        private final Object object;
        private final StringBuilder builder;
        protected Context(Object object, Class<?> type) {
            this.type = type;
            this.object = object;
            this.builder = new StringBuilder();
        }
        public Object get() {
            return object;
        }
        public StringBuilder getBuilder() {
            return builder;
        }
        public void invoke(String propertyName, Context childContext, String prefix) {
            logger.trace("{}Invoke: {}: {}: {}", new Object[] {prefix, object, propertyName, childContext});
            boolean warn = true;
            Method method = null;
            try {
                if (object instanceof StringBuilder) {
                    ((StringBuilder) object).append(childContext.builder.toString());
                    warn = false;
                } else if (Collection.class.isAssignableFrom(type)) {
                    method = getMethod(Collection.class, "add");
                } else if (propertyName == null) {
                    propertyName = defaultMap.get(type);
                    if (propertyName != null) {
                        method = getMethod(type, propertyName);
                    } else {
                        logger.trace("{}No default property name for: {}", prefix, type);
                        warn = false;
                    }
                } else {
                    String methodName = propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
                    logger.trace("{}Method name: {}: {}", new Object[] {prefix, "set" + methodName, type.getSimpleName()});
                    method = getMethod(type, "set" + methodName);
                    if (method == null) {
                        logger.trace("{}Method name: {}: {}", new Object[] {prefix, "add" + methodName, type.getSimpleName()});
                        method = getMethod(type, "add" + methodName);
                    }
                    if (method == null) {
                        throw new IllegalStateException("No method found for '" + propertyName + "'");
                    }
                }
                if (method != null) {
                    Class<?> parameterType = method.getParameterTypes()[0];
                    Object parameter;
                    if (String.class.isAssignableFrom(parameterType)) {
                        parameter = childContext.getBuilder().toString();
                    } else if (Integer.class.isAssignableFrom(parameterType) || int.class.isAssignableFrom(parameterType)) {
                        parameter = Integer.parseInt(childContext.getBuilder().toString());
                    } else {
                        parameter = childContext.get();
                    }
                    logger.trace("{}Parameter type: {}: {}", new Object[] {prefix, parameterType.getSimpleName(), showType(parameter)});
                    method.invoke(object, parameter);
                } else if (warn && logger.isWarnEnabled()) {
                    logger.warn("Skipped: {}.???({})", new Object[] {object, childContext});
                }
            } catch (Exception exception) {
                logger.warn("Setter failed: {}.{}({}) // {}: {}", new Object[] {object, (method == null ? "???" : method.getName()), childContext, exception.getMessage(), propertyName});
            }
        }

        public String toString() {
            String result;
            if (object == null) {
                result = "[Context#" + System.identityHashCode(Context.this) + "|" + builder.length() + "]";
            } else {
                result = "[Context|" + object + "|" + builder.length() + "]";
            }
            return result;
        }
    }

}
