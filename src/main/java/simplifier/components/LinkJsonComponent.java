package simplifier.components;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import org.springframework.boot.jackson.JsonComponent;
import simplifier.model.Link;
import simplifier.model.Tag;
import simplifier.model.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@JsonComponent
public class LinkJsonComponent {

    public static class LinkSerializer extends JsonSerializer<Link> {

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

    public static class LinkDeserializer extends JsonDeserializer<Link> {

        @Override
        public Link deserialize(JsonParser jsonParser,
                                DeserializationContext deserializationContext) throws IOException, JsonProcessingException {

            JsonNode node = jsonParser.getCodec().readTree(jsonParser);

            String originalLink = node.get("originalLink").asText();
            String shortenedLink = null;
            if (node.get("shortenedLink") != null) {
                shortenedLink = node.get("shortenedLink").asText();
            }
            String description = node.get("description").asText();

            JsonNode tagsNode = node.path("tags");
            List<Tag> tags = new ArrayList<>();
            for (int i = 0; i < tagsNode.size(); i++) {
                String tagName = tagsNode.get(i).asText();
                Tag tag = new Tag();
                tag.setName(tagName);
                tags.add(tag);
            }

            String authorName = node.get("author").asText();

            User author = new User();
            author.setUsername(authorName);

            Link link = new Link();
            link.setOriginalLink(originalLink);
            link.setShortenedLink(shortenedLink);
            link.setDescription(description);
            link.setTags(tags);
            link.setAuthor(author);

            return link;
        }
    }
}
