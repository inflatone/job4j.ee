package ru.job4j.todo.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static ru.job4j.todo.web.ItemRequestDispatcher.getDispatcher;
import static ru.job4j.todo.web.json.JsonUtil.writeValue;

public class ItemServlet extends HttpRestServlet {
    private final ItemRequestDispatcher dispatcher = getDispatcher();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        var id = dispatcher.getId(request, false);
        String json;
        if (id == null) {
            var items = dispatcher.findAll(request);
            json = writeValue(items);
        } else {
            var item = dispatcher.find(id);
            json = writeValue(item);
        }
        response.getWriter().write(json);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        var returned = dispatcher.createOrUpdate(request);
        String json = writeValue(returned);
        response.getWriter().write(json);
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) {
        dispatcher.delete(request);
    }

    @Override
    public void doPatch(HttpServletRequest request, HttpServletResponse response) throws IOException {
        dispatcher.complete(request);
    }
}