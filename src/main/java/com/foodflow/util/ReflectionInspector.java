package com.foodflow.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Java Reflection tool providing deep structural inspection of classes and JPA entities.
 * Demonstrates:
 * - java.lang.reflect (Class, Field, Method, Constructor, Modifier)
 * - Annotation runtime introspection
 * - Type hierarchy traversal (Superclasses & Interfaces)
 */
public class ReflectionInspector {

    /**
     * DTO containing formatted reflection inspection metadata.
     */
    public static class ClassMetadataReport {
        private final String className;
        private final String simpleName;
        private final String packageName;
        private final String superclass;
        private final List<String> interfaces;
        private final List<String> annotations;
        private final List<String> fields;
        private final List<String> constructors;
        private final List<String> methods;
        private final int totalFieldCount;
        private final int totalMethodCount;

        public ClassMetadataReport(Class<?> clazz) {
            this.className = clazz.getName();
            this.simpleName = clazz.getSimpleName();
            this.packageName = clazz.getPackageName();
            this.superclass = (clazz.getSuperclass() != null) ? clazz.getSuperclass().getName() : "None (Object)";
            
            this.interfaces = Arrays.stream(clazz.getInterfaces())
                    .map(Class::getSimpleName)
                    .collect(Collectors.toList());

            this.annotations = Arrays.stream(clazz.getAnnotations())
                    .map(a -> "@" + a.annotationType().getSimpleName())
                    .collect(Collectors.toList());

            // Declared Fields
            Field[] declaredFields = clazz.getDeclaredFields();
            this.totalFieldCount = declaredFields.length;
            this.fields = new ArrayList<>();
            for (Field f : declaredFields) {
                String mods = Modifier.toString(f.getModifiers());
                String type = f.getType().getSimpleName();
                this.fields.add(String.format("%s %s %s", mods, type, f.getName()));
            }

            // Constructors
            Constructor<?>[] declaredConstructors = clazz.getDeclaredConstructors();
            this.constructors = new ArrayList<>();
            for (Constructor<?> c : declaredConstructors) {
                String params = Arrays.stream(c.getParameterTypes())
                        .map(Class::getSimpleName)
                        .collect(Collectors.joining(", "));
                this.constructors.add(String.format("%s(%s)", c.getName().substring(c.getName().lastIndexOf('.') + 1), params));
            }

            // Declared Methods
            Method[] declaredMethods = clazz.getDeclaredMethods();
            this.totalMethodCount = declaredMethods.length;
            this.methods = new ArrayList<>();
            for (Method m : declaredMethods) {
                String mods = Modifier.toString(m.getModifiers());
                String returnType = m.getReturnType().getSimpleName();
                String params = Arrays.stream(m.getParameterTypes())
                        .map(Class::getSimpleName)
                        .collect(Collectors.joining(", "));
                this.methods.add(String.format("%s %s %s(%s)", mods, returnType, m.getName(), params));
            }
        }

        public String getFormattedSummary() {
            StringBuilder sb = new StringBuilder();
            sb.append("======================================================================\n");
            sb.append(" REFLECTION METADATA: ").append(simpleName).append("\n");
            sb.append("======================================================================\n");
            sb.append("Full Class Name: ").append(className).append("\n");
            sb.append("Package:         ").append(packageName).append("\n");
            sb.append("Superclass:      ").append(superclass).append("\n");
            sb.append("Interfaces:      ").append(interfaces.isEmpty() ? "None" : String.join(", ", interfaces)).append("\n");
            sb.append("Annotations:     ").append(annotations.isEmpty() ? "None" : String.join(", ", annotations)).append("\n\n");

            sb.append("--- DECLARED FIELDS (").append(totalFieldCount).append(") ---\n");
            for (String f : fields) {
                sb.append("  • ").append(f).append("\n");
            }

            sb.append("\n--- CONSTRUCTORS (").append(constructors.size()).append(") ---\n");
            for (String c : constructors) {
                sb.append("  • ").append(c).append("\n");
            }

            sb.append("\n--- DECLARED METHODS (").append(totalMethodCount).append(") ---\n");
            for (String m : methods) {
                sb.append("  • ").append(m).append("\n");
            }
            sb.append("======================================================================\n");
            return sb.toString();
        }

        public String getClassName() { return className; }
        public String getSimpleName() { return simpleName; }
        public String getPackageName() { return packageName; }
        public String getSuperclass() { return superclass; }
        public List<String> getInterfaces() { return interfaces; }
        public List<String> getAnnotations() { return annotations; }
        public List<String> getFields() { return fields; }
        public List<String> getConstructors() { return constructors; }
        public List<String> getMethods() { return methods; }
        public int getTotalFieldCount() { return totalFieldCount; }
        public int getTotalMethodCount() { return totalMethodCount; }
    }

    public static ClassMetadataReport inspect(Class<?> clazz) {
        return new ClassMetadataReport(clazz);
    }

    public static ClassMetadataReport inspectByName(String fullClassName) throws ClassNotFoundException {
        Class<?> clazz = Class.forName(fullClassName);
        return new ClassMetadataReport(clazz);
    }
}
