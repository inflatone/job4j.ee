package ru.job4j.auto.web;

import lombok.RequiredArgsConstructor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import ru.job4j.auto.web.converter.UrlConverter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RequiredArgsConstructor
public class DataUrlInterceptor extends HandlerInterceptorAdapter {
    private final UrlConverter urlConverter;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        if (modelAndView != null && !modelAndView.isEmpty()) {
            modelAndView.getModelMap().addAttribute("dataUrl", urlConverter.buildUrl(DataController.URL));
        }
    }
}
