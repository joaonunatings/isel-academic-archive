package pt.isel.tsma.util;

import com.microsoft.graph.authentication.IAuthenticationProvider;
import com.microsoft.graph.models.CategoryColor;
import com.microsoft.graph.models.DateTimeTimeZone;
import com.microsoft.graph.models.Event;
import com.microsoft.graph.models.OutlookCategory;
import com.microsoft.graph.requests.GraphServiceClient;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.ExtensionMethod;
import lombok.val;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import pt.isel.tsma.exception.security.InvalidTokenException;
import pt.isel.tsma.exception.security.MissingTokenException;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.concurrent.CompletableFuture;

@Component
@ExtensionMethod(Utils.StringExtensions.class)
public class GraphUtils {

	public static GraphServiceClient<?> getClient() {
		val auth = SecurityContextHolder.getContext().getAuthentication();
		if (!auth.isAuthenticated()) throw new MissingTokenException("User is not authenticated");
		val accessToken = (String) auth.getCredentials();
		if (accessToken.isNullOrEmpty()) throw new MissingTokenException();

		try {
			val authProvider = new GraphTokenAuthProvider(accessToken);
			return GraphServiceClient.builder().authenticationProvider(authProvider).buildClient();
		} catch (Exception e) {
			throw new InvalidTokenException("Invalid or expired access token");
		}
	}

	@AllArgsConstructor
	private static class GraphTokenAuthProvider implements IAuthenticationProvider {

		private final String token;

		@NonNull
		@Override
		public CompletableFuture<String> getAuthorizationTokenAsync(@NonNull URL url) {
			CompletableFuture<String> future = new CompletableFuture<>();
			future.complete(token);
			return future;
		}
	}

	public static Event addStartEnd(Event event, LocalDate date, LocalTime startTime, LocalTime endTime) {
		val startDTTZ = new DateTimeTimeZone();
		val endDTTZ = new DateTimeTimeZone();
		startDTTZ.timeZone = endDTTZ.timeZone = "Europe/London";
		var endDate = date;
		if (startTime != null && endTime != null) {
			if (endTime.isBefore(startTime) || endTime.equals(startTime))
				endDate = endDate.plusDays(1);
			startDTTZ.dateTime = date + "T" + startTime;
			endDTTZ.dateTime = endDate + "T" + endTime;
			event.isAllDay = false;
		} else {
			startDTTZ.dateTime = date + "T00:00:00.0000000";
			endDTTZ.dateTime = date.plusDays(1) + "T00:00:00.0000000";
			event.isAllDay = true;
		}
		event.start = startDTTZ;
		event.end = endDTTZ;
		return event;
	}

	// Refer to: https://docs.microsoft.com/en-us/graph/api/resources/outlookcategory?view=graph-rest-1.0#properties
	public static CategoryColor getColor(String color) {
		switch (color) {
			case ("Red"):
				return CategoryColor.PRESET0;
			case ("Orange"):
				return CategoryColor.PRESET1;
			case ("Brown"):
				return CategoryColor.PRESET2;
			case ("Yellow"):
				return CategoryColor.PRESET3;
			case ("Green"):
				return CategoryColor.PRESET4;
			case ("Teal"):
				return CategoryColor.PRESET5;
			case ("Olive"):
				return CategoryColor.PRESET6;
			case ("Blue"):
				return CategoryColor.PRESET7;
			case ("Purple"):
				return CategoryColor.PRESET8;
			case ("Cranberry"):
				return CategoryColor.PRESET9;
			case ("Steel"):
				return CategoryColor.PRESET10;
			case ("DarkSteel"):
				return CategoryColor.PRESET11;
			case ("Gray"):
				return CategoryColor.PRESET12;
			case ("DarkGray"):
				return CategoryColor.PRESET13;
			case ("Black"):
				return CategoryColor.PRESET14;
			case ("DarkRed"):
				return CategoryColor.PRESET15;
			case ("DarkOrange"):
				return CategoryColor.PRESET16;
			case ("DarkBrown"):
				return CategoryColor.PRESET17;
			case ("DarkGreen"):
				return CategoryColor.PRESET18;
			case ("DarkTeal"):
				return CategoryColor.PRESET19;
			case ("DarkOlive"):
				return CategoryColor.PRESET20;
			case ("DarkBlue"):
				return CategoryColor.PRESET21;
			case ("DarkPurple"):
				return CategoryColor.PRESET22;
			case ("DarkCranberry"):
				return CategoryColor.PRESET23;
			default:
				return CategoryColor.NONE;
		}
	}

	public static OutlookCategory createCategory(String displayName, String color) {
		val category = new OutlookCategory();
		category.displayName = displayName;
		category.color = getColor(color);
		return category;
	}
}