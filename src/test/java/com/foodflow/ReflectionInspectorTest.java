package com.foodflow;

import com.foodflow.model.Student;
import com.foodflow.model.User;
import com.foodflow.util.ReflectionInspector;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ReflectionInspectorTest {

    @Test
    public void testInspectStudentClassHierarchyAndMetadata() {
        ReflectionInspector.ClassMetadataReport report = ReflectionInspector.inspect(Student.class);

        assertNotNull(report);
        assertEquals("com.foodflow.model.Student", report.getClassName());
        assertEquals("com.foodflow.model.User", report.getSuperclass());
        assertTrue(report.getInterfaces().contains("Manageable"));
        assertTrue(report.getInterfaces().contains("Reportable"));
        assertTrue(report.getTotalFieldCount() > 0);
        assertTrue(report.getTotalMethodCount() > 0);

        String summary = report.getFormattedSummary();
        assertNotNull(summary);
        assertTrue(summary.contains("REFLECTION METADATA: Student"));
    }
}
