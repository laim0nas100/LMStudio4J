package com.github.laim0nas100.lmstudio4j.demo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.github.laim0nas100.lmstudio4j.model.v1.*;
import com.github.laim0nas100.uncheckedutils.SafeOpt;
import com.github.mizosoft.methanol.AdapterCodec;
import com.github.mizosoft.methanol.MediaType;
import com.github.mizosoft.methanol.Methanol;
import com.github.mizosoft.methanol.MoreBodyHandlers;
import com.github.mizosoft.methanol.MutableRequest;
import com.github.mizosoft.methanol.adapter.jackson.JacksonAdapterFactory;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 *
 * @author Lemmin
 */
public class SimpleChatting {

    protected final JsonMapper mapper = new JsonMapper();
    protected final AdapterCodec adapterCodec = AdapterCodec.newBuilder()
            .encoder(JacksonAdapterFactory.createJsonEncoder(mapper))
            .decoder(JacksonAdapterFactory.createJsonDecoder(mapper))
            .build();

    protected final Methanol methanol;
    protected final ChatSession session;
    protected List<ChatResponse> responses = new ArrayList<>();
    protected String bearerToken = null;

    public SimpleChatting(ChatSession session) {
        this("http://localhost:1234", session);
    }

    public SimpleChatting(String baseURI, ChatSession session) {
        Objects.requireNonNull(baseURI);
        this.session = Objects.requireNonNull(session);
        methanol = Methanol.newBuilder()
                .baseUri(baseURI)
                .adapterCodec(adapterCodec)
                .build();
    }

    public ChatResponse sendSync(ChatRequest request) throws IOException, InterruptedException {
        if (request.getStream()) {
            throw new IllegalArgumentException("Doesn't suport streamed responses");
        }
        MutableRequest POST = MutableRequest.POST(
                "/api/v1/chat",
                request,
                MediaType.APPLICATION_JSON
        );
        if (bearerToken != null) {
            POST.setHeader("Authorization", "Bearer " + bearerToken);
        }
        HttpResponse<String> response = methanol.send(
                POST, HttpResponse.BodyHandlers.ofString()
        );

        return mapper.readValue(response.body(), ChatResponse.class);
    }

    public InputStream sendAsyncRaw(ChatRequest request) throws IOException, InterruptedException {
        if (!request.getStream()) {
            throw new IllegalArgumentException("Doesn't suport non-streamed responses");
        }
        MutableRequest POST = MutableRequest.POST(
                "/api/v1/chat",
                request,
                MediaType.APPLICATION_JSON
        );
        if (bearerToken != null) {
            POST.setHeader("Authorization", "Bearer " + bearerToken);
        }

        HttpResponse<InputStream> response = methanol.send(
                POST, HttpResponse.BodyHandlers.ofInputStream()
        );

        return response.body();
    }

    public Stream<LLMEvent> sendAsyncStream(ChatRequest request) throws IOException, InterruptedException {
        if (!request.getStream()) {
            throw new IllegalArgumentException("Doesn't suport non-streamed responses");
        }
        MutableRequest POST = MutableRequest.POST(
                "/api/v1/chat",
                request,
                MediaType.APPLICATION_JSON
        );
        if (bearerToken != null) {
            POST.setHeader("Authorization", "Bearer " + bearerToken);
        }

        HttpResponse<Stream<String>> response = methanol.send(
                POST, BodyHandlers.ofLines()
        );

        return response.body().map(this::parseEvent).filter(event -> event != null);
    }

    public Reader sendAsyncReader(ChatRequest request) throws IOException, InterruptedException {
        if (!request.getStream()) {
            throw new IllegalArgumentException("Doesn't suport non-streamed responses");
        }
        MutableRequest POST = MutableRequest.POST(
                "/api/v1/chat",
                request,
                MediaType.APPLICATION_JSON
        );
        if (bearerToken != null) {
            POST.setHeader("Authorization", "Bearer " + bearerToken);
        }

        HttpResponse<Reader> response = methanol.send(
                POST, MoreBodyHandlers.ofReader()
        );

        return response.body();
    }

    public Iterator<LLMEvent> sendAsyncIterator(ChatRequest request) throws IOException, InterruptedException {
        return toIterator(sendAsyncReader(request));
    }

    protected LLMEvent parseEvent(String msg) {
        // mangle the msg
        int dataIndex = msg.indexOf("data:");
        if (dataIndex < 0) {
            return null;
        }
        String data = msg.substring(dataIndex + 6).trim();
        try {
            return mapper.readValue(data, LLMEvent.class);
        } catch (JsonProcessingException ex) {
            throw new RuntimeException(ex);
        }
    }

    protected Iterator<LLMEvent> toIterator(final Reader reader) {
        return new Iterator<LLMEvent>() {
            LLMEvent next = LLMEvent.DUMMY;

            @Override
            public boolean hasNext() {
                if (next == null) {
                    return false;
                }
                if (next == LLMEvent.DUMMY) {
                    return resolveNext() != null;
                }
                return true;

            }

            @Override
            public LLMEvent next() {
                if (next == null) {
                    // no more events;
                    throw new IndexOutOfBoundsException("No more tokens");
                }
                if (next == LLMEvent.DUMMY) { //
                    LLMEvent resolved = resolveNext();
                    if (resolved == null) {
                        throw new IndexOutOfBoundsException("No more tokens");
                    }
                    next = LLMEvent.DUMMY;
                    return resolved;

                }
                LLMEvent resolved = next;
                next = LLMEvent.DUMMY;
                return resolved;
            }

            protected char[] buffer = new char[10000];

            protected LLMEvent resolveNext() {
                try {
                    while (true) {
                        int read = reader.read(buffer);
                        if (read < 0) { // EOL
                            next = null;
                            reader.close();
                            return null;
                        } else {
                            LLMEvent parsed = parseEvent(String.valueOf(buffer, 0, read));
                            if(parsed == null){
                                continue;
                            }
                            return next = parsed;
                        }
                    }

                } catch (IOException io) {
                    throw new RuntimeException(io);
                }

            }
        };
    }

    public Iterator<LLMEvent> sendAsync(ChatRequest request) throws IOException, InterruptedException {
        return toIterator(new InputStreamReader(new BufferedInputStream(sendAsyncRaw(request))));
    }

    public SafeOpt<String> chat(String input) {
        return SafeOpt.ofAsync(input).map(in -> {
            ChatRequest request = new ChatRequest(session);
            request.setInput(in);
            if (!responses.isEmpty()) {
                request.setPreviousResponseId(responses.getLast().getResponseId());
            }
            return sendSync(request).resolveFirstMessage();
        });
    }

    public String getBearerToken() {
        return bearerToken;
    }

    public void setBearerToken(String bearerToken) {
        this.bearerToken = bearerToken;
    }

}
