package com.tracing.graphql.controller;

import com.sun.net.httpserver.HttpExchange;
import com.tracing.graphql.GraphqlApplication;
import com.tracing.graphql.model.Author;
import com.tracing.graphql.model.Book;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.context.propagation.TextMapGetter;
import io.opentelemetry.context.propagation.TextMapSetter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Tracer;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.net.http.HttpHeaders;

@Controller
public class BookController {
    @Autowired
    private HttpServletRequest request;

    private final Tracer tracer;

    private final OpenTelemetry openTelemetry;

    TextMapGetter<Iterable<String>> getter =
            new TextMapGetter<>() {
                @Override
                public String get(Iterable headers, String s) {
                    assert headers != null;
                    return headers.toString();
                }

                @Override
                public Iterable<String> keys(Iterable<String> headers) {
                    return headers;
                }
            };

    public BookController(OpenTelemetry openTelemetry) {
        this.tracer = openTelemetry.getTracer("@opentelemetry/instrumentation-http");
        this.openTelemetry = openTelemetry;
    }

    @QueryMapping
    public Book bookById(@Argument String id) {


        try {
            Enumeration<String> a = request.getHeaders("b3");
            Iterable<String> aaa = Collections.list(a);

            Context extractedContext = GlobalOpenTelemetry.getPropagators().getTextMapPropagator()
                    .extract(Context.current(), aaa, getter);

            extractedContext.makeCurrent();

            Span serverSpan = tracer.spanBuilder("GET /resource")
                    .setSpanKind(SpanKind.SERVER)
                    .startSpan();

            String traceid = serverSpan.getSpanContext().getTraceId();
            System.out.println(traceid + "huisien111");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        /*
        try (Scope scope = extractedContext.makeCurrent()) {
            Scope aaaa = scope;
            System.out.println(scope.toString());
        } */

        //Tracer tracer = GlobalOpenTelemetry.getTracer("@opentelemetry/instrumentation-http");

        //Span span = tracer.spanBuilder("test").startSpan();
        //String traceId = span.getSpanContext().getTraceId();

        return Book.getById(id);
    }

    @SchemaMapping
    public Author author(Book book) {
        return Author.getById(book.authorId());
    }
}
