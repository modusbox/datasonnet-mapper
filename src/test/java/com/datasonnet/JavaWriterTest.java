package com.datasonnet;

import com.datasonnet.document.Document;
import com.datasonnet.document.StringDocument;
import com.datasonnet.javatest.Gizmo;
import com.datasonnet.javatest.Manufacturer;
import com.datasonnet.spi.DataFormatService;
import com.datasonnet.util.TestResourceReader;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;


public class JavaWriterTest {

    @Test
    void testJavaWriter() throws Exception {
        //Test with output as Gizmo class
        String json = TestResourceReader.readFileAsString("javaTest.json");
        String mapping = TestResourceReader.readFileAsString("writeJavaTest.ds");

        Document data = new StringDocument(json, "application/json");

        Mapper mapper = new Mapper(mapping, new ArrayList<>(), true);
        mapper.findAndRegisterPlugins();

        Document mapped = mapper.transform(data, new HashMap<>(), "application/java");

        Object result = mapped.getContentsAsObject();
        assertTrue(result instanceof Gizmo);

        Gizmo gizmo = (Gizmo)result;
        assertEquals("gizmo", gizmo.getName());
        assertEquals(123, gizmo.getQuantity());
        assertEquals(true, gizmo.isInStock());
        assertEquals(Arrays.asList("red","white","blue"), gizmo.getColors());
        assertEquals("ACME Corp.", gizmo.getManufacturer().getManufacturerName());
        assertEquals("ACME123", gizmo.getManufacturer().getManufacturerCode());

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        assertEquals("2020-01-06", df.format(gizmo.getDate()));

        //Test with default output, i.e. java.util.HashMap
        mapping = mapping.substring(mapping.lastIndexOf("*/") + 2);

        mapper = new Mapper(mapping, new ArrayList<>(), true);
        mapper.findAndRegisterPlugins();

        mapped = mapper.transform(data, new HashMap<>(), "application/java");

        result = mapped.getContentsAsObject();
        assertTrue(result instanceof java.util.HashMap);

        Map gizmoMap = (Map)result;
        assertTrue(gizmoMap.get("colors") instanceof java.util.ArrayList);
        assertTrue(gizmoMap.get("manufacturer") instanceof java.util.HashMap);


    }

    @Test
    void testJavaWriteFunction() throws Exception {
        String json = TestResourceReader.readFileAsString("javaTest.json");
        Document data = new StringDocument(json, "application/json");

        //Test calling write() function
        String mapping = TestResourceReader.readFileAsString("writeJavaFunctionTest.ds");
        Mapper mapper = new Mapper(mapping, new ArrayList<>(), true);
        mapper.findAndRegisterPlugins();

        try {
            mapper.transform(data, new HashMap<>(), "application/java");
            fail("Should not succeed");
        } catch(Exception e) {
            assertTrue(e.getMessage().contains("does not return output that can be rendered as a String"), "Failed with wrong message: " + e.getMessage());
        }


    }
}
