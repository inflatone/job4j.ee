package ru.job4j.auto.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static ru.job4j.auto.web.AbstractControllerTest.RequestWrapper.wrap;

public class AbstractSecondLevelControllerTest extends AbstractControllerTest {
    public AbstractSecondLevelControllerTest(String url) {
        super(url);
    }

    @Override
    protected RequestWrapper doGet(int rootId) {
        return super.doGet("", rootId);
    }

    protected RequestWrapper doGet(int rootId, int id) {
        return doGet("{id}", rootId, id);
    }

    @Override
    protected RequestWrapper doPost(int id) {
        return super.doPost("", id);
    }

    protected RequestWrapper doPost(int rootId, int id) {
        return super.doPost("{id}", rootId, id);
    }

    protected RequestWrapper doDelete(String urlTemplatePad, Object... uriVars) {
        return wrap(delete(url + urlTemplatePad, uriVars));
    }

    @Override
    protected RequestWrapper doDelete(int id) {
        return doDelete("", id);
    }

    protected RequestWrapper doDelete(int rootId, int id) {
        return doDelete("{id}", rootId, id);
    }
}
