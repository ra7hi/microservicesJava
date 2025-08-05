package microservices.order_processing.order_service.utils;

import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class NullPropertyNamesTest {

    @Getter
    @Setter
    static class TestObject {
        private String nonNullField = "value";
        private String nullField = null;
        private Integer anotherNullField = null;
        private Integer nonNullInteger = 42;
    }

    @Test
    void testGetNullPropertyNamesReturnsCorrectNullProperties() {
        TestObject obj = new TestObject();

        String[] nullProperties = NullPropertyNames.getNullPropertyNames(obj);
        List<String> nullPropsList = Arrays.asList(nullProperties);

        assertTrue(nullPropsList.contains("nullField"));
        assertTrue(nullPropsList.contains("anotherNullField"));
        assertFalse(nullPropsList.contains("nonNullField"));
        assertFalse(nullPropsList.contains("nonNullInteger"));
    }

    @Test
    void testGetNullPropertyNamesWithAllNonNullFieldsReturnsEmptyArray() {
        TestObject obj = new TestObject();
        obj.setNullField("not null");
        obj.setAnotherNullField(100);

        String[] nullProperties = NullPropertyNames.getNullPropertyNames(obj);

        assertEquals(0, nullProperties.length);
    }

    @Test
    void testGetNullPropertyNamesWithAllFieldsNullReturnsAllFieldNames() {
        TestObject obj = new TestObject();
        obj.setNonNullField(null);
        obj.setNonNullInteger(null);

        String[] nullProperties = NullPropertyNames.getNullPropertyNames(obj);
        List<String> nullPropsList = Arrays.asList(nullProperties);

        assertTrue(nullPropsList.contains("nullField"));
        assertTrue(nullPropsList.contains("anotherNullField"));
        assertTrue(nullPropsList.contains("nonNullField"));
        assertTrue(nullPropsList.contains("nonNullInteger"));
    }
}

