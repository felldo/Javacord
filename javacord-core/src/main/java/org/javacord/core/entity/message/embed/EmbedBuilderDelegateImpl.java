package org.javacord.core.entity.message.embed;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.javacord.api.entity.Icon;
import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.entity.message.embed.EditableEmbedField;
import org.javacord.api.entity.message.embed.EmbedField;
import org.javacord.api.entity.message.embed.internal.EmbedBuilderDelegate;
import org.javacord.api.entity.user.User;
import org.javacord.core.util.FileContainer;
import org.javacord.core.util.io.FileUtils;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * The implementation of {@link EmbedBuilderDelegate}.
 */
public class EmbedBuilderDelegateImpl implements EmbedBuilderDelegate {

    // General embed stuff
    private String title = null;
    private String description = null;
    private String url = null;
    private Instant timestamp = null;
    private Color color = null;

    // Footer
    private String footerText = null;
    private String footerIconUrl = null;
    private FileContainer footerIconContainer = null;

    // Image
    private String imageUrl = null;
    private FileContainer imageContainer = null;

    // Author
    private String authorName = null;
    private String authorUrl = null;
    private String authorIconUrl = null;
    private FileContainer authorIconContainer = null;

    // Thumbnail
    private String thumbnailUrl = null;
    private FileContainer thumbnailContainer = null;

    // Fields
    private final List<EmbedFieldImpl> fields = new ArrayList<>();

    @Override
    public void setTitle(final String title) {
        this.title = title;
    }

    @Override
    public void setDescription(final String description) {
        this.description = description;
    }

    @Override
    public void setUrl(final String url) {
        this.url = url;
    }

    @Override
    public void setTimestampToNow() {
        this.timestamp = Instant.now();
    }

    @Override
    public void setTimestamp(final Instant timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public void setColor(final Color color) {
        this.color = color;
    }

    @Override
    public void setFooter(final String text) {
        footerText = text;
        footerIconUrl = null;
        footerIconContainer = null;
    }

    @Override
    public void setFooter(final String text, final String iconUrl) {
        footerText = text;
        footerIconUrl = iconUrl;
        footerIconContainer = null;
    }

    @Override
    public void setFooter(final String text, final Icon icon) {
        footerText = text;
        footerIconUrl = icon.getUrl().toString();
        footerIconContainer = null;
    }

    @Override
    public void setFooter(final String text, final File icon) {
        footerText = text;
        footerIconUrl = null;
        if (icon == null) {
            footerIconContainer = null;
        } else {
            footerIconContainer = new FileContainer(icon);
            footerIconContainer.setFileTypeOrName(UUID.randomUUID() + "." + FileUtils.getExtension(icon));
        }
    }

    @Override
    public void setFooter(final String text, final InputStream icon) {
        setFooter(text, icon, "png");
    }

    @Override
    public void setFooter(final String text, final InputStream icon, final String fileType) {
        footerText = text;
        footerIconUrl = null;
        if (icon == null) {
            footerIconContainer = null;
        } else {
            footerIconContainer = new FileContainer(icon, fileType);
            footerIconContainer.setFileTypeOrName(UUID.randomUUID() + "." + fileType);
        }
    }

    @Override
    public void setFooter(final String text, final byte[] icon) {
        setFooter(text, icon, "png");
    }

    @Override
    public void setFooter(final String text, final byte[] icon, final String fileType) {
        footerText = text;
        footerIconUrl = null;
        if (icon == null) {
            footerIconContainer = null;
        } else {
            footerIconContainer = new FileContainer(icon, fileType);
            footerIconContainer.setFileTypeOrName(UUID.randomUUID() + "." + fileType);
        }
    }

    @Override
    public void setFooter(final String text, final BufferedImage icon) {
        setFooter(text, icon, "png");
    }

    @Override
    public void setFooter(final String text, final BufferedImage icon, final String fileType) {
        footerText = text;
        footerIconUrl = null;
        if (icon == null) {
            footerIconContainer = null;
        } else {
            footerIconContainer = new FileContainer(icon, fileType);
            footerIconContainer.setFileTypeOrName(UUID.randomUUID() + "." + fileType);
        }
    }

    @Override
    public void setImage(final String url) {
        imageUrl = url;
        imageContainer = null;
    }

    @Override
    public void setImage(final Icon image) {
        imageUrl = image.getUrl().toString();
        imageContainer = null;
    }

    @Override
    public void setImage(final File image) {
        imageUrl = null;
        if (image == null) {
            imageContainer = null;
        } else {
            imageContainer = new FileContainer(image);
            imageContainer.setFileTypeOrName(UUID.randomUUID() + "." + FileUtils.getExtension(image));
        }
    }

    @Override
    public void setImage(final InputStream image) {
        setImage(image, "png");
    }

    @Override
    public void setImage(final InputStream image, final String fileType) {
        imageUrl = null;
        if (image == null) {
            imageContainer = null;
        } else {
            imageContainer = new FileContainer(image, fileType);
            imageContainer.setFileTypeOrName(UUID.randomUUID() + "." + fileType);
        }
    }

    @Override
    public void setImage(final byte[] image) {
        setImage(image, "png");
    }

    @Override
    public void setImage(final byte[] image, final String fileType) {
        imageUrl = null;
        if (image == null) {
            imageContainer = null;
        } else {
            imageContainer = new FileContainer(image, fileType);
            imageContainer.setFileTypeOrName(UUID.randomUUID() + "." + fileType);
        }
    }

    @Override
    public void setImage(final BufferedImage image) {
        setImage(image, "png");
    }

    @Override
    public void setImage(final BufferedImage image, final String fileType) {
        imageUrl = null;
        if (image == null) {
            imageContainer = null;
        } else {
            imageContainer = new FileContainer(image, fileType);
            imageContainer.setFileTypeOrName(UUID.randomUUID() + "." + fileType);
        }
    }

    @Override
    public void setAuthor(final MessageAuthor author) {
        authorName = author.getDisplayName();
        authorUrl = null;
        authorIconUrl = author.getAvatar().getUrl().toString();
        authorIconContainer = null;
    }

    @Override
    public void setAuthor(final User author) {
        authorName = author.getName();
        authorUrl = null;
        authorIconUrl = author.getAvatar().getUrl().toString();
        authorIconContainer = null;
    }

    @Override
    public void setAuthor(final String name) {
        authorName = name;
        authorUrl = null;
        authorIconUrl = null;
        authorIconContainer = null;
    }

    @Override
    public void setAuthor(final String name, final String url, final String iconUrl) {
        authorName = name;
        authorUrl = url;
        authorIconUrl = iconUrl;
        authorIconContainer = null;
    }

    @Override
    public void setAuthor(final String name, final String url, final Icon icon) {
        authorName = name;
        authorUrl = url;
        authorIconUrl = icon.getUrl().toString();
        authorIconContainer = null;
    }

    @Override
    public void setAuthor(final String name, final String url, final File icon) {
        authorName = name;
        authorUrl = url;
        authorIconUrl = null;
        if (icon == null) {
            authorIconContainer = null;
        } else {
            authorIconContainer = new FileContainer(icon);
            authorIconContainer.setFileTypeOrName(UUID.randomUUID() + "." + FileUtils.getExtension(icon));
        }
    }

    @Override
    public void setAuthor(final String name, final String url, final InputStream icon) {
        setAuthor(name, url, icon, "png");
    }

    @Override
    public void setAuthor(final String name, final String url, final InputStream icon, final String fileType) {
        authorName = name;
        authorUrl = url;
        authorIconUrl = null;
        if (icon == null) {
            authorIconContainer = null;
        } else {
            authorIconContainer = new FileContainer(icon, fileType);
            authorIconContainer.setFileTypeOrName(UUID.randomUUID() + "." + fileType);
        }
    }

    @Override
    public void setAuthor(final String name, final String url, final byte[] icon) {
        setAuthor(name, url, icon, "png");
    }

    @Override
    public void setAuthor(final String name, final String url, final byte[] icon, final String fileType) {
        authorName = name;
        authorUrl = url;
        authorIconUrl = null;
        if (icon == null) {
            authorIconContainer = null;
        } else {
            authorIconContainer = new FileContainer(icon, fileType);
            authorIconContainer.setFileTypeOrName(UUID.randomUUID() + "." + fileType);
        }
    }

    @Override
    public void setAuthor(final String name, final String url, final BufferedImage icon) {
        setAuthor(name, url, icon, "png");
    }

    @Override
    public void setAuthor(final String name, final String url, final BufferedImage icon, final String fileType) {
        authorName = name;
        authorUrl = url;
        authorIconUrl = null;
        if (icon == null) {
            authorIconContainer = null;
        } else {
            authorIconContainer = new FileContainer(icon, fileType);
            authorIconContainer.setFileTypeOrName(UUID.randomUUID() + "." + fileType);
        }
    }

    @Override
    public void setThumbnail(final String url) {
        thumbnailUrl = url;
        thumbnailContainer = null;
    }

    @Override
    public void setThumbnail(final Icon thumbnail) {
        thumbnailUrl = thumbnail.getUrl().toString();
        thumbnailContainer = null;
    }

    @Override
    public void setThumbnail(final File thumbnail) {
        thumbnailUrl = null;
        if (thumbnail == null) {
            thumbnailContainer = null;
        } else {
            thumbnailContainer = new FileContainer(thumbnail);
            thumbnailContainer.setFileTypeOrName(
                    UUID.randomUUID() + "." + FileUtils.getExtension(thumbnail));
        }
    }

    @Override
    public void setThumbnail(final InputStream thumbnail) {
        setThumbnail(thumbnail, "png");
    }

    @Override
    public void setThumbnail(final InputStream thumbnail, final String fileType) {
        thumbnailUrl = null;
        if (thumbnail == null) {
            thumbnailContainer = null;
        } else {
            thumbnailContainer = new FileContainer(thumbnail, fileType);
            thumbnailContainer.setFileTypeOrName(UUID.randomUUID() + "." + fileType);
        }
    }

    @Override
    public void setThumbnail(final byte[] thumbnail) {
        setThumbnail(thumbnail, "png");
    }

    @Override
    public void setThumbnail(final byte[] thumbnail, final String fileType) {
        thumbnailUrl = null;
        if (thumbnail == null) {
            thumbnailContainer = null;
        } else {
            thumbnailContainer = new FileContainer(thumbnail, fileType);
            thumbnailContainer.setFileTypeOrName(UUID.randomUUID() + "." + fileType);
        }
    }

    @Override
    public void setThumbnail(final BufferedImage thumbnail) {
        setThumbnail(thumbnail, "png");
    }

    @Override
    public void setThumbnail(final BufferedImage thumbnail, final String fileType) {
        thumbnailUrl = null;
        if (thumbnail == null) {
            thumbnailContainer = null;
        } else {
            thumbnailContainer = new FileContainer(thumbnail, fileType);
            thumbnailContainer.setFileTypeOrName(UUID.randomUUID() + "." + fileType);
        }
    }

    @Override
    public void addField(final String name, final String value, final boolean inline) {
        fields.add(new EmbedFieldImpl(name, value, inline));
    }

    @Override
    public void updateFields(final Predicate<EmbedField> predicate, final Consumer<EditableEmbedField> updater) {
        fields.stream()
                .filter(predicate)
                .map(EditableEmbedFieldImpl::new)
                .forEach(updater.andThen(field -> ((EditableEmbedFieldImpl) field).clearDelegate()));
    }

    @Override
    public void removeFields(final Predicate<EmbedField> predicate) {
        fields.removeIf(predicate);
    }

    @Override
    public boolean requiresAttachments() {
        return footerIconContainer != null
                || imageContainer != null
                || authorIconContainer != null
                || thumbnailContainer != null;
    }


    /**
     * Gets the required attachments for this embed.
     *
     * @return The required attachments for this embed.
     */
    public List<FileContainer> getRequiredAttachments() {
        final List<FileContainer> requiredAttachments = new ArrayList<>();
        if (footerIconContainer != null) {
            requiredAttachments.add(footerIconContainer);
        }
        if (imageContainer != null) {
            requiredAttachments.add(imageContainer);
        }
        if (authorIconContainer != null) {
            requiredAttachments.add(authorIconContainer);
        }
        if (thumbnailContainer != null) {
            requiredAttachments.add(thumbnailContainer);
        }
        return requiredAttachments;
    }

    /**
     * Gets the embed as a {@link ObjectNode}. This is what is sent to Discord.
     *
     * @return The embed as a ObjectNode.
     */
    public ObjectNode toJsonNode() {
        final ObjectNode object = JsonNodeFactory.instance.objectNode();
        return toJsonNode(object);
    }

    /**
     * Adds the json data to the given object node.
     *
     * @param object The object, the data should be added to.
     * @return The provided object with the data of the embed.
     */
    public ObjectNode toJsonNode(final ObjectNode object) {
        object.put("type", "rich");
        if (title != null && !title.equals("")) {
            object.put("title", title);
        }
        if (description != null && !description.equals("")) {
            object.put("description", description);
        }
        if (url != null && !url.equals("")) {
            object.put("url", url);
        }
        if (color != null) {
            object.put("color", color.getRGB() & 0xFFFFFF);
        }
        if (timestamp != null) {
            object.put("timestamp", DateTimeFormatter.ISO_INSTANT.format(timestamp));
        }
        if ((footerText != null && !footerText.equals("")) || (footerIconUrl != null && !footerIconUrl.equals(""))) {
            final ObjectNode footer = object.putObject("footer");
            if (footerText != null && !footerText.equals("")) {
                footer.put("text", footerText);
            }
            if (footerIconUrl != null && !footerIconUrl.equals("")) {
                footer.put("icon_url", footerIconUrl);
            }
            if (footerIconContainer != null) {
                footer.put("icon_url", "attachment://" + footerIconContainer.getFileTypeOrName());
            }
        }
        if (imageUrl != null && !imageUrl.equals("")) {
            object.putObject("image").put("url", imageUrl);
        }
        if (imageContainer != null) {
            object.putObject("image").put("url", "attachment://" + imageContainer.getFileTypeOrName());
        }
        if (authorName != null && !authorName.equals("")) {
            final ObjectNode author = object.putObject("author");
            author.put("name", authorName);
            if (authorUrl != null && !authorUrl.equals("")) {
                author.put("url", authorUrl);
            }
            if (authorIconUrl != null && !authorIconUrl.equals("")) {
                author.put("icon_url", authorIconUrl);
            }
            if (authorIconContainer != null) {
                author.put("icon_url", "attachment://" + authorIconContainer.getFileTypeOrName());
            }
        }
        if (thumbnailUrl != null && !thumbnailUrl.equals("")) {
            object.putObject("thumbnail").put("url", thumbnailUrl);
        }
        if (thumbnailContainer != null) {
            object.putObject("thumbnail").put("url", "attachment://" + thumbnailContainer.getFileTypeOrName());
        }
        if (fields.size() > 0) {
            final ArrayNode jsonFields = object.putArray("fields");
            for (final EmbedField field : fields) {
                final ObjectNode jsonField = jsonFields.addObject();
                jsonField.put("name", field.getName());
                jsonField.put("value", field.getValue());
                jsonField.put("inline", field.isInline());
            }
        }
        return object;
    }

}
