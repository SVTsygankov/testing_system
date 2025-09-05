package com.svtsygankov.test_system.servlet.user;

import com.svtsygankov.test_system.servlet.BaseListTestsServlet;
import jakarta.servlet.annotation.WebServlet;

@WebServlet("/secure/tests")
public class ListTestsServlet extends BaseListTestsServlet {

        @Override
        protected String getContentPage() {
                return "/WEB-INF/views/secure/test-list.jsp";
        }

        @Override
        protected String getResourceName() {
                return "/WEB-INF/views/secure/tests.jsp";
        }
}