package microservices.order_processing.order_service.utils;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.PropertyDescriptor;
import java.util.HashSet;
import java.util.Set;


/**
 * Утилитарный класс, предоставляющий метод для определения имен свойств объекта, значение которых равно {@code null}.
 * <p>Полезен при копировании свойств из одного объекта в другой, позволяя игнорировать {@code null}-поля и тем самым
 * избегать перезаписи значений по умолчанию или существующих данных.</p>
 */
public class NullPropertyNames {
    public static String[] getNullPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<>();
        for (PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) emptyNames.add(pd.getName());
        }
        return emptyNames.toArray(new String[0]);
    }
}
