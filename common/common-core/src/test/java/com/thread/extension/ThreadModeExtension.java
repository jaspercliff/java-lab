package com.thread.extension;

import com.jasper.constants.ThreadType;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.extension.*;

import java.util.List;
import java.util.stream.Stream;

public class ThreadModeExtension implements TestTemplateInvocationContextProvider {


    @Override
    public boolean supportsTestTemplate(@NonNull ExtensionContext context) {
        return true;
    }

    @Override
    @NonNull
    public Stream<? extends TestTemplateInvocationContext>
    provideTestTemplateInvocationContexts(@NonNull ExtensionContext context) {
        return Stream.of(ThreadType.values()).map(ThreadModeInvocationContext::new);
    }

    private record ThreadModeInvocationContext(ThreadType threadType) implements TestTemplateInvocationContext {

        @Override
            @NonNull
            public String getDisplayName(int invocationIndex) {
                return "[" + threadType + " thread mode]";
            }

            @Override
            @NonNull
            public List<Extension> getAdditionalExtensions() {
                return List.of(
                        new ThreadModeLifecycleExtension(threadType),
                        new ThreadTypeParameterResolver(threadType)
                );
            }
        }
}
