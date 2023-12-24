package pt.isel.tsma.service.graph;

import com.microsoft.graph.models.Calendar;
import com.microsoft.graph.models.Event;
import com.microsoft.graph.models.OutlookCategory;
import com.microsoft.graph.requests.GraphServiceClient;
import lombok.AllArgsConstructor;
import lombok.experimental.ExtensionMethod;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pt.isel.tsma.config.GraphConfiguration;
import pt.isel.tsma.entity.model.State;
import pt.isel.tsma.entity.model.shift.Type;
import pt.isel.tsma.entity.proxy.calendar.CalendarCreateProxy;
import pt.isel.tsma.entity.proxy.calendar.CalendarDeleteProxy;
import pt.isel.tsma.entity.proxy.calendar.CalendarUpdateProxy;
import pt.isel.tsma.entity.proxy.event.EventCreateProxy;
import pt.isel.tsma.entity.proxy.event.EventDeleteProxy;
import pt.isel.tsma.entity.proxy.event.EventUpdateProxy;
import pt.isel.tsma.exception.GraphSyncException;
import pt.isel.tsma.repository.MemberRepository;
import pt.isel.tsma.repository.ShiftRepository;
import pt.isel.tsma.repository.calendar.CalendarRepository;
import pt.isel.tsma.util.GraphUtils;
import pt.isel.tsma.util.Utils;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
@ExtensionMethod({Utils.StringExtensions.class, Utils.ListExtensions.class})
@Slf4j(topic = "graph")
public class GraphServiceImpl implements GraphService {

	private final CalendarRepository calendarRepository;

	private final ShiftRepository shiftRepository;

	private final MemberRepository memberRepository;

	private final ModelMapper mapper = GraphConfiguration.modelMapper();

	public void sync() {
		log.debug("Syncing...");

		val futures = new LinkedList<CompletableFuture<Void>>();
		val client = GraphUtils.getClient();

		// Delete calendars -> delete events
		futures.add(deleteCalendars(client)
			.thenComposeAsync(r1 -> deleteEvents(client)));

		// Create categories & calendars -> Create & update events
		futures.add(CompletableFuture.allOf(createCategories(client), createCalendars(client))
			.thenComposeAsync(r1 ->
				CompletableFuture.allOf(createEvents(client), updateEvents(client))));

		// Update calendars
		futures.add(updateCalendars(client));

		val future = CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new))
			.thenAcceptAsync(r -> memberRepository.permanentDelete());

		try {
			future.get();
		} catch (InterruptedException | ExecutionException e) {
			throw new GraphSyncException(e);
		}
		log.debug("Sync complete");
	}

	private CompletableFuture<Void> createCalendars(GraphServiceClient<?> client) {
		val calendarsToCreate = calendarRepository.findByState(State.CREATED, false);
		if (calendarsToCreate.isEmpty()) {
			log.debug("No calendars to create");
			return CompletableFuture.completedFuture(null);
		}
		log.debug("Creating " + calendarsToCreate.size() + " calendars...");
		val proxies = calendarsToCreate.mapList(CalendarCreateProxy.class, mapper);

		return CompletableFuture.allOf(proxies
				.stream()
				.map(proxy -> createCalendar(proxy, client))
				.toArray(CompletableFuture[]::new))
			.thenAcceptAsync(r -> log.debug("Created " + proxies.size() + " calendars"));
	}

	private CompletableFuture<Calendar> createCalendar(CalendarCreateProxy proxy, GraphServiceClient<?> client) {
		log.debug("Creating calendar...");
		return client
			.me()
			.calendars()
			.buildRequest()
			.postAsync(proxy.build())
			.thenApplyAsync(calendar -> {
				proxy.setGraphId(calendar.id);
				proxy.save(calendarRepository);
				log.debug("Calendar created");
				return calendar;
			});
	}

	private CompletableFuture<Void> createEvents(GraphServiceClient<?> client) {
		val shiftsToCreate = shiftRepository.findByState(State.CREATED, false);
		if (shiftsToCreate.isEmpty()) {
			log.debug("No events to create");
			return CompletableFuture.completedFuture(null);
		}
		log.debug("Creating " + shiftsToCreate.size() + " events...");
		val proxies = shiftsToCreate.mapList(EventCreateProxy.class, mapper);

		return CompletableFuture.allOf(proxies
				.stream()
				.map(proxy -> createEvent(proxy, client))
				.toArray(CompletableFuture[]::new))
			.thenAcceptAsync(r -> log.debug("Created " + proxies.size() + " events"));
	}

	private CompletableFuture<Event> createEvent(EventCreateProxy proxy, GraphServiceClient<?> client) {
		log.debug("Creating event...");
		return client
			.me()
			.calendars(proxy.getCalendarGraphId())
			.events()
			.buildRequest()
			.postAsync(proxy.build())
			.thenApplyAsync(event -> {
				proxy.setGraphId(event.id);
				proxy.save(shiftRepository);
				log.debug("Event created");
				return event;
			});
	}

	private CompletableFuture<Void> createCategories(GraphServiceClient<?> client) {
		return getCategories(client).thenComposeAsync(categories -> {
			val categoriesToCreate = GraphConfiguration.CATEGORIES;
			for (val category : categories) {
				try {
					categoriesToCreate.remove(Type.valueOf(category.displayName));
				} catch (IllegalArgumentException e) { /* do nothing */ }
			}
			if (categoriesToCreate.isEmpty()) {
				log.debug("No categories to create");
				return CompletableFuture.completedFuture(null);
			}
			log.debug("Creating " + categoriesToCreate.size() + " categories...");
			CompletableFuture<OutlookCategory> current = CompletableFuture.completedFuture(null);
			for (val category : categoriesToCreate.values()) {
				current = current.thenComposeAsync(r -> createCategory(category, client));
			}
			return CompletableFuture.allOf(current);
		});
	}

	private CompletableFuture<OutlookCategory> createCategory(OutlookCategory category, GraphServiceClient<?> client) {
		log.debug("Creating category...");
		return client
			.me()
			.outlook()
			.masterCategories()
			.buildRequest()
			.postAsync(category)
			.thenApplyAsync(event -> {
				log.debug("Category created");
				return event;
			});
	}

	private CompletableFuture<List<OutlookCategory>> getCategories(GraphServiceClient<?> client) {
		log.debug("Getting categories...");
		return client
			.me()
			.outlook()
			.masterCategories()
			.buildRequest()
			.getAsync()
			.thenApplyAsync(categories -> {
				log.debug("Categories retrieved");
				return categories.getCurrentPage();
			});
	}

	private CompletableFuture<Void> updateCalendars(GraphServiceClient<?> client) {
		val calendarsToUpdate = calendarRepository.findByState(State.NOT_SYNCED, false);
		if (calendarsToUpdate.isEmpty()) {
			log.debug("No calendars to update");
			return CompletableFuture.completedFuture(null);
		}
		log.debug("Updating " + calendarsToUpdate.size() + " calendars...");
		val proxies = calendarsToUpdate.mapList(CalendarUpdateProxy.class, mapper);

		return CompletableFuture.allOf(proxies
				.stream()
				.map(proxy -> updateCalendar(proxy, client))
				.toArray(CompletableFuture[]::new))
			.thenAcceptAsync(r -> log.debug("Updated " + proxies.size() + " calendars"));
	}

	private CompletableFuture<Calendar> updateCalendar(CalendarUpdateProxy proxy, GraphServiceClient<?> client) {
		log.debug("Updating calendar...");
		return client
			.me()
			.calendars(proxy.getGraphId())
			.buildRequest()
			.patchAsync(proxy.build())
			.thenApplyAsync(calendar -> {
				proxy.save(calendarRepository);
				log.debug("Calendar updated");
				return calendar;
			});
	}

	private CompletableFuture<Void> updateEvents(GraphServiceClient<?> client) {
		val shiftsToUpdate = shiftRepository.findByState(State.NOT_SYNCED, false);
		if (shiftsToUpdate.isEmpty()) {
			log.debug("No events to update");
			return CompletableFuture.completedFuture(null);
		}
		log.debug("Updating " + shiftsToUpdate.size() + " events...");
		val proxies = shiftsToUpdate.mapList(EventUpdateProxy.class, mapper);

		return CompletableFuture.allOf(proxies
				.stream()
				.map(proxy -> updateEvent(proxy, client))
				.toArray(CompletableFuture[]::new))
			.thenAcceptAsync(r -> log.debug("Updated " + proxies.size() + " events"));
	}

	private CompletableFuture<Event> updateEvent(EventUpdateProxy proxy, GraphServiceClient<?> client) {
		log.debug("Updating event...");
		return client
			.me()
			.calendars(proxy.getCalendarGraphId())
			.events(proxy.getGraphId())
			.buildRequest()
			.patchAsync(proxy.build())
			.thenApplyAsync(event -> {
				proxy.save(shiftRepository);
				log.debug("Event updated");
				return event;
			});
	}

	private CompletableFuture<Void> deleteCalendars(GraphServiceClient<?> client) {
		val calendarsToDelete = calendarRepository.findByState(State.NOT_SYNCED, true);
		if (calendarsToDelete.isEmpty()) {
			log.debug("No calendars to delete");
			return CompletableFuture.completedFuture(null);
		}
		log.debug("Deleting " + calendarsToDelete.size() + " calendars...");
		val proxies = calendarsToDelete.mapList(CalendarDeleteProxy.class, mapper);

		return CompletableFuture.allOf(proxies
				.stream()
				.map(proxy -> deleteCalendar(proxy, client))
				.toArray(CompletableFuture[]::new))
			.thenAcceptAsync(r -> log.debug("Deleted " + proxies.size() + " calendars"));
	}

	private CompletableFuture<Calendar> deleteCalendar(CalendarDeleteProxy proxy, GraphServiceClient<?> client) {
		log.debug("Deleting calendar...");
		return client
			.me()
			.calendars(proxy.getGraphId())
			.buildRequest()
			.deleteAsync()
			.thenApplyAsync(r -> {
				shiftRepository.permanentDeleteByCalendar(proxy.getCalendar().getId());
				calendarRepository.permanentDelete(proxy.getCalendar().getId());
				log.debug("Calendar deleted");
				return r;
			});
	}

	private CompletableFuture<Void> deleteEvents(GraphServiceClient<?> client) {
		val shiftsToDelete = shiftRepository.findByState(State.NOT_SYNCED, true);
		if (shiftsToDelete.isEmpty()) {
			log.debug("No events to delete");
			return CompletableFuture.completedFuture(null);
		}
		log.debug("Deleting " + shiftsToDelete.size() + " events...");
		val proxies = shiftsToDelete.mapList(EventDeleteProxy.class, mapper);

		return CompletableFuture.allOf(proxies
				.stream()
				.map(proxy -> deleteEvent(proxy, client)).toArray(CompletableFuture[]::new))
			.thenAcceptAsync(r -> log.debug("Deleted " + proxies.size() + " events"));
	}

	private CompletableFuture<Event> deleteEvent(EventDeleteProxy proxy, GraphServiceClient<?> client) {
		log.debug("Deleting event...");
		return client
			.me()
			.calendars(proxy.getCalendarGraphId())
			.events(proxy.getGraphId())
			.buildRequest()
			.deleteAsync()
			.thenApplyAsync(r -> {
				val shiftId = proxy.getShift().getId();
				shiftRepository.permanentDelete(shiftId.getCalendarId(), shiftId.getMemberId(), shiftId.getDate());
				log.debug("Event deleted");
				return r;
			});
	}
}
