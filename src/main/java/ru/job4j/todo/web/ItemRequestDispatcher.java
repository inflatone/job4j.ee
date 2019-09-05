package ru.job4j.todo.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.job4j.todo.model.Item;
import ru.job4j.todo.service.ItemService;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;
import static ru.job4j.todo.service.ItemService.getService;
import static ru.job4j.todo.web.json.JsonUtil.readValue;

public class ItemRequestDispatcher {
    private static final ItemRequestDispatcher INSTANCE_HOLDER = new ItemRequestDispatcher();
    private static final ObjectMapper JSON = new ObjectMapper();

    public static ItemRequestDispatcher getDispatcher() {
        return INSTANCE_HOLDER;
    }

    private static final Logger log = LoggerFactory.getLogger(ItemRequestDispatcher.class);

    private final ItemService service = getService();


    Item createOrUpdate(HttpServletRequest request) throws IOException {
        Item item = createObject(request);
        if (item.isNew()) {
            log.info("Create {}", item);
            item.setCreated(new Date());
            item = service.create(item);
        } else {
            log.info("Update {}", item);
            service.update(item);
        }
        return item;
    }

    void delete(HttpServletRequest request) {
        int id = getId(request, true);
        log.info("delete {}", id);
        service.delete(id);
    }

    void complete(HttpServletRequest request) throws IOException {
        Item item = createObject(request);
        log.info(item.isDone() ? "Complete {}" : "Undone {}", item.getId());
        service.done(item.getId(), item.isDone());
    }

    List<Item> findAll(HttpServletRequest request) {
        boolean showAll = needShowAll(request);
        log.info(showAll ? "findAll" : "findUncompleated");
        return service.findAll(showAll);
    }

    Item find(int id) {
        log.info("find with id={}", id);
        return service.find(id);
    }

    private boolean needShowAll(HttpServletRequest request) {
        Boolean showAll = getParameter(request, "showAll", Boolean::valueOf, false);
        return showAll == null ? true : showAll;
    }

    Integer getId(HttpServletRequest request, boolean required) {
        return getParameter(request, "id", Integer::valueOf, required);
    }

    private <T> T getParameter(HttpServletRequest request, String paramKey, Function<String, T> paramMapper, boolean required) {
        String paramValue = request.getParameter(paramKey);
        return required ? paramMapper.apply(requireNonNull(paramValue, paramKey + " must be set"))
                : Strings.isEmpty(paramValue) ? null : paramMapper.apply(paramValue);
    }

    private Item createObject(HttpServletRequest request) throws IOException {
        return readValue(request.getReader(), Item.class);
    }
}
