package ru.job4j.todo.web;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

// https://technology.amis.nl/2017/12/15/handle-http-patch-request-with-java-servlet/
public abstract class HttpRestServlet extends HttpServlet {
    @Override
    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getMethod().equalsIgnoreCase("PATCH")) {
            doPatch(request, response);
        } else {
            super.service(request, response);
        }
    }

    public abstract void doPatch(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;
}
