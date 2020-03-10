package org.pac4j.oauth.profile.linkedin2;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.TokenBuffer;

/**
 *
 * @author Vassilis Virvilis
 * @since 3.8.0
 */
public class LinkedIn2ProfilePicture implements Serializable {
    private static final long serialVersionUID = 100L;

    public static class DisplayImageTilde implements Serializable {
        private static final long serialVersionUID = 1L;

        public static class Paging implements Serializable {
            private static final long serialVersionUID = 1L;

            private int count;
            private int start;
            private String[] links;

            public int getCount() {
                return count;
            }

            public void setCount(final int count) {
                this.count = count;
            }

            public int getStart() {
                return start;
            }

            public void setStart(final int start) {
                this.start = start;
            }

            public String[] getLinks() {
                return deepCopy(links);
            }

            public void setLinks(final String[] links) {
                this.links = deepCopy(links);
            }

            @Override
            public String toString() {
                return String.format("{count: %d, start: %d, links.length: %s}", count, start, Arrays.asList(links));
            }
        }

        public static class Element implements Serializable {
            private static final long serialVersionUID = 1L;

            public static class Data implements Serializable {
                private static final long serialVersionUID = 1L;

                public static class StillImage implements Serializable {
                    private static final long serialVersionUID = 1L;

                    public static class Size implements Serializable {
                        private static final long serialVersionUID = 1L;

                        private int width;
                        private int height;

                        public int getWidth() {
                            return width;
                        }

                        public void setWidth(final int width) {
                            this.width = width;
                        }

                        public int getHeight() {
                            return height;
                        }

                        public void setHeight(final int height) {
                            this.height = height;
                        }

                        protected String getBaseString() {
                            return String.format("width: %d, height: %d", width, height);
                        }

                        @Override
                        public String toString() {
                            return String.format("{%s}", getBaseString());
                        }
                    }

                    public static class DisplaySize extends Size implements Serializable {
                        private static final long serialVersionUID = 1L;

                        private String uom;

                        public String getUom() {
                            return uom;
                        }

                        public void setUom(final String uom) {
                            this.uom = uom;
                        }

                        @Override
                        public String toString() {
                            return String.format("{%s, uom: %s}", getBaseString(), uom);
                        }
                    }

                    public static class AspectRatio implements Serializable {
                        private static final long serialVersionUID = 1L;

                        private double widthAspect;
                        private double heightAspect;
                        private String formatted;

                        public double getWidthAspect() {
                            return widthAspect;
                        }

                        public void setWidthAspect(final double widthAspect) {
                            this.widthAspect = widthAspect;
                        }

                        public double getHeightAspect() {
                            return heightAspect;
                        }

                        public void setHeightAspect(final double heightAspect) {
                            this.heightAspect = heightAspect;
                        }

                        public String getFormatted() {
                            return formatted;
                        }

                        public void setFormatted(final String formatted) {
                            this.formatted = formatted;
                        }

                        @Override
                        public String toString() {
                            return String.format("{widthAspect: %g, heightAspect: %s, formatted: %s}", widthAspect, heightAspect,
                                    formatted);
                        }
                    }

                    public static class RawCodecSpec implements Serializable {
                        private static final long serialVersionUID = 1L;

                        private String name;
                        private String type;

                        public String getName() {
                            return name;
                        }

                        public void setName(final String name) {
                            this.name = name;
                        }

                        public String getType() {
                            return type;
                        }

                        public void setType(final String type) {
                            this.type = type;
                        }

                        @Override
                        public String toString() {
                            return String.format("{name: %s, type: %s}", name, type);
                        }
                    }

                    private Size storageSize;
                    private AspectRatio storageAspectRatio;
                    private String mediaType;
                    private RawCodecSpec rawCodecSpec;
                    private DisplaySize displaySize;
                    private AspectRatio displayAspectRatio;

                    public Size getStorageSize() {
                        return storageSize;
                    }

                    public void setStorageSize(final Size storageSize) {
                        this.storageSize = storageSize;
                    }

                    public AspectRatio getStorageAspectRatio() {
                        return storageAspectRatio;
                    }

                    public void setStorageAspectRatio(final AspectRatio storageAspectRatio) {
                        this.storageAspectRatio = storageAspectRatio;
                    }

                    public String getMediaType() {
                        return mediaType;
                    }

                    public void setMediaType(final String mediaType) {
                        this.mediaType = mediaType;
                    }

                    public RawCodecSpec getRawCodecSpec() {
                        return rawCodecSpec;
                    }

                    public void setRawCodecSpec(final RawCodecSpec rawCodecSpec) {
                        this.rawCodecSpec = rawCodecSpec;
                    }

                    public DisplaySize getDisplaySize() {
                        return displaySize;
                    }

                    public void setDisplaySize(final DisplaySize displaySize) {
                        this.displaySize = displaySize;
                    }

                    public AspectRatio getDisplayAspectRatio() {
                        return displayAspectRatio;
                    }

                    public void setDisplayAspectRatio(final AspectRatio displayAspectRatio) {
                        this.displayAspectRatio = displayAspectRatio;
                    }

                    @Override
                    public String toString() {
                        return String.format(
                                "{storageSize: %s, storageAspectRatio: %s, mediaType: %s, rawCodecSpec: %s, "
                                        + "displaySize: %s, displayAspectRatio: %s}",
                                storageSize, storageAspectRatio, mediaType, rawCodecSpec, displaySize, displayAspectRatio);
                    }
                }

                // looks like they forgot to change this one
                // the tilde ~ thingy in the names is worse though.
                @JsonProperty("com.linkedin.digitalmedia.mediaartifact.StillImage")
                private StillImage stillImage;

                public StillImage getStillImage() {
                    return stillImage;
                }

                public void setStillImage(final StillImage stillImage) {
                    this.stillImage = stillImage;
                }

                @Override
                public String toString() {
                    return String.format("{stillImage: %s}", stillImage);
                }
            }

            public static class Identifier implements Serializable {
                private static final long serialVersionUID = 1L;

                private String identifier;
                private String file;
                private int index;
                private String mediaType;
                private String identifierType;
                private int identifierExpiresInSeconds;

                public String getIdentifier() {
                    return identifier;
                }

                public void setIdentifier(final String identifier) {
                    this.identifier = identifier;
                }

                public String getFile() {
                    return file;
                }

                public void setFile(final String file) {
                    this.file = file;
                }

                public int getIndex() {
                    return index;
                }

                public void setIndex(final int index) {
                    this.index = index;
                }

                public String getMediaType() {
                    return mediaType;
                }

                public void setMediaType(final String mediaType) {
                    this.mediaType = mediaType;
                }

                public String getIdentifierType() {
                    return identifierType;
                }

                public void setIdentifierType(final String identifierType) {
                    this.identifierType = identifierType;
                }

                public int getIdentifierExpiresInSeconds() {
                    return identifierExpiresInSeconds;
                }

                public void setIdentifierExpiresInSeconds(final int identifierExpiresInSeconds) {
                    this.identifierExpiresInSeconds = identifierExpiresInSeconds;
                }

                @Override
                public String toString() {
                    return String.format(
                            "{identifier: %s, file: %s, index: %d, mediaType: %s, identifierType: %s, identifierExpiresInSeconds: %d}",
                            identifier, file, index, mediaType, identifierType, identifierExpiresInSeconds);
                }
            }

            private String artifact;
            private String authorizationMethod;
            private Data data;
            private Identifier[] identifiers;

            public String getArtifact() {
                return artifact;
            }

            public void setArtifact(final String artifact) {
                this.artifact = artifact;
            }

            public String getAuthorizationMethod() {
                return authorizationMethod;
            }

            public void setAuthorizationMethod(final String authorizationMethod) {
                this.authorizationMethod = authorizationMethod;
            }

            public Data getData() {
                return data;
            }

            public void setData(final Data data) {
                this.data = data;
            }

            public Identifier[] getIdentifiers() {
                return deepCopy(identifiers);
            }

            public void setIdentifiers(final Identifier[] identifiers) {
                this.identifiers = deepCopy(identifiers);
            }

            @Override
            public String toString() {
                return String.format("{artifact: %s, authorizationMethod: %s, data: %s, identifiers: %s}", artifact, authorizationMethod,
                        data, Arrays.asList(identifiers));
            }
        }

        private Paging paging;
        private Element[] elements;

        public Paging getPaging() {
            return paging;
        }

        public void setPaging(final Paging paging) {
            this.paging = paging;
        }

        public Element[] getElements() {
            return deepCopy(elements);
        }

        public void setElements(final Element[] elements) {
            this.elements = deepCopy(elements);
        }

        @Override
        public String toString() {
            return String.format("{paging: %s, elements: %s}", paging, Arrays.asList(elements));
        }
    }

    private String displayImage;
    @JsonProperty("displayImage~")
    private DisplayImageTilde displayImageTilde;

    public String getDisplayImage() {
        return displayImage;
    }

    public void setDisplayImage(final String displayImage) {
        this.displayImage = displayImage;
    }

    public DisplayImageTilde getDisplayImageTilde() {
        return displayImageTilde;
    }

    public void setDisplayImageTilde(final DisplayImageTilde displayImageTilde) {
        this.displayImageTilde = displayImageTilde;
    }

    @Override
    public String toString() {
        return String.format("{displayImage: %s, displayImage~: %s}", displayImage, displayImageTilde);
    }

    public static <T> T[] deepCopy(final T[] array) {
        final ObjectMapper mapper = new ObjectMapper();
        // per https://stackoverflow.com/questions/6349421/how-to-use-jackson-to-deserialise-an-array-of-objects
        // per https://stackoverflow.com/questions/49903859/deep-copy-using-jackson-string-or-jsonnode
        final TokenBuffer tb = new TokenBuffer(mapper, false);
        try {
            mapper.writeValue(tb, array);
            return (T[]) mapper.readValue(tb.asParser(), array.getClass());
        } catch (final IOException e) {
            return null;
        }
    }
}
