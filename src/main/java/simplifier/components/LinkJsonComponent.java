package simplifier.components;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.boot.jackson.JsonComponent;
import simplifier.model.Link;

import java.io.IOException;

@JsonComponent
public class LinkJsonComponent {

    public static class Serializer extends JsonSerializer<Link> {

        @Override
        public void serialize(Link link, JsonGenerator jsonGenerator,
                        SerializerProvider serializerProvider) throws IOException {

            jsonGenerator.writeStartObject();
            jsonGenerator.writeObjectField("originalLink", link.getOriginalLink());
            jsonGenerator.writeObjectField("shortenedLink", link.getShortenedLink());
            jsonGenerator.writeObjectField("description", link.getDescription());
            jsonGenerator.writeObjectField("tags", link.getTagsAsStrings());
            jsonGenerator.writeObjectField("author", link.getAuthor().getUsername());
            jsonGenerator.writeObjectField("clicks", link.getClicks());
            jsonGenerator.writeEndObject();

        }
    }
}
