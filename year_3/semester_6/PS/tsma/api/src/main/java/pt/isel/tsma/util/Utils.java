package pt.isel.tsma.util;

import lombok.experimental.ExtensionMethod;
import lombok.val;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import pt.isel.tsma.exception.model.paging.InvalidPageNoException;
import pt.isel.tsma.exception.model.paging.InvalidPageSizeException;
import pt.isel.tsma.exception.model.paging.InvalidSortException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@ExtensionMethod(Utils.StringExtensions.class)
public class Utils {

	public static boolean isValidEmail(String email) {
		if (email.isNullOrEmpty()) return false;
		String regex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
		Pattern emailPattern = Pattern.compile(regex);
		Matcher matcher = emailPattern.matcher(email);
		return matcher.matches();
	}

	public static void validatePage(Pageable page, List<String> validProperties) {
		if (page.getPageNumber() < 0) throw new InvalidPageNoException(page.getPageNumber());
		if (page.getPageSize() < 0) throw new InvalidPageSizeException(page.getPageSize());
		page.getSort().get().forEach(order -> {
			val property = order.getProperty();
			if (!property.isValid(validProperties)) throw new InvalidSortException(property, validProperties);
		});
	}

	public static class ListExtensions {
		public static <S, T> List<T> mapList(List<S> source, Class<T> targetClass, ModelMapper mapper) {
			return source
				.stream()
				.map(element -> mapper.map(element, targetClass))
				.collect(Collectors.toList());
		}

		public static boolean isNullOrEmpty(List<?> source) {
			return source == null || source.isEmpty();
		}
	}

	public static class MapExtensions {

		public static <K, S, T> Map<K, T> mapMap(Map<K, S> source, Class<T> targetClass, ModelMapper mapper) {
			val destination = new HashMap<K, T>();
			for (Map.Entry<K, S> entry : source.entrySet()) {
				destination.put(entry.getKey(), mapper.map(entry.getValue(), targetClass));
			}
			return destination;
		}

		public static boolean isNullOrEmpty(Map<?, ?> source) {
			return source == null || source.isEmpty();
		}
	}

	public static class StringExtensions {
		public static boolean isNullOrEmpty(String in) {
			return in == null || in.isEmpty();
		}

		public static boolean isValid(String param, List<String> validParams) {
			if (isNullOrEmpty(param)) return false;
			for (String validParam : validParams) {
				if (param.equals(validParam)) return true;
			}
			return false;
		}
	}

	public static <E> Specification<E> byDeleted(boolean deleted) {
		return (root, query, cb) -> cb.equal(root.get("deleted"), deleted);
	}

}
