package faang.school.postservice.protobuf.generate;// Generated by the protocol buffer compiler.  DO NOT EDIT!
// NO CHECKED-IN PROTOBUF GENCODE
// source: comment_published_event.proto
// Protobuf Java Version: 4.28.3

public final class CommentEventProto {
  private CommentEventProto() {}
  static {
    com.google.protobuf.RuntimeVersion.validateProtobufGencodeVersion(
      com.google.protobuf.RuntimeVersion.RuntimeDomain.PUBLIC,
      /* major= */ 4,
      /* minor= */ 28,
      /* patch= */ 3,
      /* suffix= */ "",
      CommentEventProto.class.getName());
  }
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  public interface CommentEventOrBuilder extends
      // @@protoc_insertion_point(interface_extends:CommentEvent)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>int64 commentId = 1;</code>
     * @return The commentId.
     */
    long getCommentId();

    /**
     * <code>int64 authorId = 2;</code>
     * @return The authorId.
     */
    long getAuthorId();

    /**
     * <code>int64 postId = 3;</code>
     * @return The postId.
     */
    long getPostId();

    /**
     * <code>.google.protobuf.Timestamp date = 4;</code>
     * @return Whether the date field is set.
     */
    boolean hasDate();
    /**
     * <code>.google.protobuf.Timestamp date = 4;</code>
     * @return The date.
     */
    com.google.protobuf.Timestamp getDate();
    /**
     * <code>.google.protobuf.Timestamp date = 4;</code>
     */
    com.google.protobuf.TimestampOrBuilder getDateOrBuilder();
  }
  /**
   * Protobuf type {@code CommentEvent}
   */
  public static final class CommentEvent extends
      com.google.protobuf.GeneratedMessage implements
      // @@protoc_insertion_point(message_implements:CommentEvent)
      CommentEventOrBuilder {
  private static final long serialVersionUID = 0L;
    static {
      com.google.protobuf.RuntimeVersion.validateProtobufGencodeVersion(
        com.google.protobuf.RuntimeVersion.RuntimeDomain.PUBLIC,
        /* major= */ 4,
        /* minor= */ 28,
        /* patch= */ 3,
        /* suffix= */ "",
        CommentEvent.class.getName());
    }
    // Use CommentEvent.newBuilder() to construct.
    private CommentEvent(com.google.protobuf.GeneratedMessage.Builder<?> builder) {
      super(builder);
    }
    private CommentEvent() {
    }

    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return CommentEventProto.internal_static_CommentEvent_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return CommentEventProto.internal_static_CommentEvent_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              CommentEventProto.CommentEvent.class, CommentEventProto.CommentEvent.Builder.class);
    }

    private int bitField0_;
    public static final int COMMENTID_FIELD_NUMBER = 1;
    private long commentId_ = 0L;
    /**
     * <code>int64 commentId = 1;</code>
     * @return The commentId.
     */
    @java.lang.Override
    public long getCommentId() {
      return commentId_;
    }

    public static final int AUTHORID_FIELD_NUMBER = 2;
    private long authorId_ = 0L;
    /**
     * <code>int64 authorId = 2;</code>
     * @return The authorId.
     */
    @java.lang.Override
    public long getAuthorId() {
      return authorId_;
    }

    public static final int POSTID_FIELD_NUMBER = 3;
    private long postId_ = 0L;
    /**
     * <code>int64 postId = 3;</code>
     * @return The postId.
     */
    @java.lang.Override
    public long getPostId() {
      return postId_;
    }

    public static final int DATE_FIELD_NUMBER = 4;
    private com.google.protobuf.Timestamp date_;
    /**
     * <code>.google.protobuf.Timestamp date = 4;</code>
     * @return Whether the date field is set.
     */
    @java.lang.Override
    public boolean hasDate() {
      return ((bitField0_ & 0x00000001) != 0);
    }
    /**
     * <code>.google.protobuf.Timestamp date = 4;</code>
     * @return The date.
     */
    @java.lang.Override
    public com.google.protobuf.Timestamp getDate() {
      return date_ == null ? com.google.protobuf.Timestamp.getDefaultInstance() : date_;
    }
    /**
     * <code>.google.protobuf.Timestamp date = 4;</code>
     */
    @java.lang.Override
    public com.google.protobuf.TimestampOrBuilder getDateOrBuilder() {
      return date_ == null ? com.google.protobuf.Timestamp.getDefaultInstance() : date_;
    }

    private byte memoizedIsInitialized = -1;
    @java.lang.Override
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized == 1) return true;
      if (isInitialized == 0) return false;

      memoizedIsInitialized = 1;
      return true;
    }

    @java.lang.Override
    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      if (commentId_ != 0L) {
        output.writeInt64(1, commentId_);
      }
      if (authorId_ != 0L) {
        output.writeInt64(2, authorId_);
      }
      if (postId_ != 0L) {
        output.writeInt64(3, postId_);
      }
      if (((bitField0_ & 0x00000001) != 0)) {
        output.writeMessage(4, getDate());
      }
      getUnknownFields().writeTo(output);
    }

    @java.lang.Override
    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      if (commentId_ != 0L) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt64Size(1, commentId_);
      }
      if (authorId_ != 0L) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt64Size(2, authorId_);
      }
      if (postId_ != 0L) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt64Size(3, postId_);
      }
      if (((bitField0_ & 0x00000001) != 0)) {
        size += com.google.protobuf.CodedOutputStream
          .computeMessageSize(4, getDate());
      }
      size += getUnknownFields().getSerializedSize();
      memoizedSize = size;
      return size;
    }

    @java.lang.Override
    public boolean equals(final java.lang.Object obj) {
      if (obj == this) {
       return true;
      }
      if (!(obj instanceof CommentEventProto.CommentEvent)) {
        return super.equals(obj);
      }
      CommentEventProto.CommentEvent other = (CommentEventProto.CommentEvent) obj;

      if (getCommentId()
          != other.getCommentId()) return false;
      if (getAuthorId()
          != other.getAuthorId()) return false;
      if (getPostId()
          != other.getPostId()) return false;
      if (hasDate() != other.hasDate()) return false;
      if (hasDate()) {
        if (!getDate()
            .equals(other.getDate())) return false;
      }
      if (!getUnknownFields().equals(other.getUnknownFields())) return false;
      return true;
    }

    @java.lang.Override
    public int hashCode() {
      if (memoizedHashCode != 0) {
        return memoizedHashCode;
      }
      int hash = 41;
      hash = (19 * hash) + getDescriptor().hashCode();
      hash = (37 * hash) + COMMENTID_FIELD_NUMBER;
      hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
          getCommentId());
      hash = (37 * hash) + AUTHORID_FIELD_NUMBER;
      hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
          getAuthorId());
      hash = (37 * hash) + POSTID_FIELD_NUMBER;
      hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
          getPostId());
      if (hasDate()) {
        hash = (37 * hash) + DATE_FIELD_NUMBER;
        hash = (53 * hash) + getDate().hashCode();
      }
      hash = (29 * hash) + getUnknownFields().hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static CommentEventProto.CommentEvent parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static CommentEventProto.CommentEvent parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static CommentEventProto.CommentEvent parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static CommentEventProto.CommentEvent parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static CommentEventProto.CommentEvent parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static CommentEventProto.CommentEvent parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static CommentEventProto.CommentEvent parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessage
          .parseWithIOException(PARSER, input);
    }
    public static CommentEventProto.CommentEvent parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessage
          .parseWithIOException(PARSER, input, extensionRegistry);
    }

    public static CommentEventProto.CommentEvent parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessage
          .parseDelimitedWithIOException(PARSER, input);
    }

    public static CommentEventProto.CommentEvent parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessage
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static CommentEventProto.CommentEvent parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessage
          .parseWithIOException(PARSER, input);
    }
    public static CommentEventProto.CommentEvent parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessage
          .parseWithIOException(PARSER, input, extensionRegistry);
    }

    @java.lang.Override
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    public static Builder newBuilder(CommentEventProto.CommentEvent prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
    }
    @java.lang.Override
    public Builder toBuilder() {
      return this == DEFAULT_INSTANCE
          ? new Builder() : new Builder().mergeFrom(this);
    }

    @java.lang.Override
    protected Builder newBuilderForType(
        com.google.protobuf.GeneratedMessage.BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
    }
    /**
     * Protobuf type {@code CommentEvent}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessage.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:CommentEvent)
        CommentEventProto.CommentEventOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return CommentEventProto.internal_static_CommentEvent_descriptor;
      }

      @java.lang.Override
      protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return CommentEventProto.internal_static_CommentEvent_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                CommentEventProto.CommentEvent.class, CommentEventProto.CommentEvent.Builder.class);
      }

      // Construct using CommentEventProto.CommentEvent.newBuilder()
      private Builder() {
        maybeForceBuilderInitialization();
      }

      private Builder(
          com.google.protobuf.GeneratedMessage.BuilderParent parent) {
        super(parent);
        maybeForceBuilderInitialization();
      }
      private void maybeForceBuilderInitialization() {
        if (com.google.protobuf.GeneratedMessage
                .alwaysUseFieldBuilders) {
          getDateFieldBuilder();
        }
      }
      @java.lang.Override
      public Builder clear() {
        super.clear();
        bitField0_ = 0;
        commentId_ = 0L;
        authorId_ = 0L;
        postId_ = 0L;
        date_ = null;
        if (dateBuilder_ != null) {
          dateBuilder_.dispose();
          dateBuilder_ = null;
        }
        return this;
      }

      @java.lang.Override
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return CommentEventProto.internal_static_CommentEvent_descriptor;
      }

      @java.lang.Override
      public CommentEventProto.CommentEvent getDefaultInstanceForType() {
        return CommentEventProto.CommentEvent.getDefaultInstance();
      }

      @java.lang.Override
      public CommentEventProto.CommentEvent build() {
        CommentEventProto.CommentEvent result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      @java.lang.Override
      public CommentEventProto.CommentEvent buildPartial() {
        CommentEventProto.CommentEvent result = new CommentEventProto.CommentEvent(this);
        if (bitField0_ != 0) { buildPartial0(result); }
        onBuilt();
        return result;
      }

      private void buildPartial0(CommentEventProto.CommentEvent result) {
        int from_bitField0_ = bitField0_;
        if (((from_bitField0_ & 0x00000001) != 0)) {
          result.commentId_ = commentId_;
        }
        if (((from_bitField0_ & 0x00000002) != 0)) {
          result.authorId_ = authorId_;
        }
        if (((from_bitField0_ & 0x00000004) != 0)) {
          result.postId_ = postId_;
        }
        int to_bitField0_ = 0;
        if (((from_bitField0_ & 0x00000008) != 0)) {
          result.date_ = dateBuilder_ == null
              ? date_
              : dateBuilder_.build();
          to_bitField0_ |= 0x00000001;
        }
        result.bitField0_ |= to_bitField0_;
      }

      @java.lang.Override
      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof CommentEventProto.CommentEvent) {
          return mergeFrom((CommentEventProto.CommentEvent)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(CommentEventProto.CommentEvent other) {
        if (other == CommentEventProto.CommentEvent.getDefaultInstance()) return this;
        if (other.getCommentId() != 0L) {
          setCommentId(other.getCommentId());
        }
        if (other.getAuthorId() != 0L) {
          setAuthorId(other.getAuthorId());
        }
        if (other.getPostId() != 0L) {
          setPostId(other.getPostId());
        }
        if (other.hasDate()) {
          mergeDate(other.getDate());
        }
        this.mergeUnknownFields(other.getUnknownFields());
        onChanged();
        return this;
      }

      @java.lang.Override
      public final boolean isInitialized() {
        return true;
      }

      @java.lang.Override
      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        if (extensionRegistry == null) {
          throw new java.lang.NullPointerException();
        }
        try {
          boolean done = false;
          while (!done) {
            int tag = input.readTag();
            switch (tag) {
              case 0:
                done = true;
                break;
              case 8: {
                commentId_ = input.readInt64();
                bitField0_ |= 0x00000001;
                break;
              } // case 8
              case 16: {
                authorId_ = input.readInt64();
                bitField0_ |= 0x00000002;
                break;
              } // case 16
              case 24: {
                postId_ = input.readInt64();
                bitField0_ |= 0x00000004;
                break;
              } // case 24
              case 34: {
                input.readMessage(
                    getDateFieldBuilder().getBuilder(),
                    extensionRegistry);
                bitField0_ |= 0x00000008;
                break;
              } // case 34
              default: {
                if (!super.parseUnknownField(input, extensionRegistry, tag)) {
                  done = true; // was an endgroup tag
                }
                break;
              } // default:
            } // switch (tag)
          } // while (!done)
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          throw e.unwrapIOException();
        } finally {
          onChanged();
        } // finally
        return this;
      }
      private int bitField0_;

      private long commentId_ ;
      /**
       * <code>int64 commentId = 1;</code>
       * @return The commentId.
       */
      @java.lang.Override
      public long getCommentId() {
        return commentId_;
      }
      /**
       * <code>int64 commentId = 1;</code>
       * @param value The commentId to set.
       * @return This builder for chaining.
       */
      public Builder setCommentId(long value) {

        commentId_ = value;
        bitField0_ |= 0x00000001;
        onChanged();
        return this;
      }
      /**
       * <code>int64 commentId = 1;</code>
       * @return This builder for chaining.
       */
      public Builder clearCommentId() {
        bitField0_ = (bitField0_ & ~0x00000001);
        commentId_ = 0L;
        onChanged();
        return this;
      }

      private long authorId_ ;
      /**
       * <code>int64 authorId = 2;</code>
       * @return The authorId.
       */
      @java.lang.Override
      public long getAuthorId() {
        return authorId_;
      }
      /**
       * <code>int64 authorId = 2;</code>
       * @param value The authorId to set.
       * @return This builder for chaining.
       */
      public Builder setAuthorId(long value) {

        authorId_ = value;
        bitField0_ |= 0x00000002;
        onChanged();
        return this;
      }
      /**
       * <code>int64 authorId = 2;</code>
       * @return This builder for chaining.
       */
      public Builder clearAuthorId() {
        bitField0_ = (bitField0_ & ~0x00000002);
        authorId_ = 0L;
        onChanged();
        return this;
      }

      private long postId_ ;
      /**
       * <code>int64 postId = 3;</code>
       * @return The postId.
       */
      @java.lang.Override
      public long getPostId() {
        return postId_;
      }
      /**
       * <code>int64 postId = 3;</code>
       * @param value The postId to set.
       * @return This builder for chaining.
       */
      public Builder setPostId(long value) {

        postId_ = value;
        bitField0_ |= 0x00000004;
        onChanged();
        return this;
      }
      /**
       * <code>int64 postId = 3;</code>
       * @return This builder for chaining.
       */
      public Builder clearPostId() {
        bitField0_ = (bitField0_ & ~0x00000004);
        postId_ = 0L;
        onChanged();
        return this;
      }

      private com.google.protobuf.Timestamp date_;
      private com.google.protobuf.SingleFieldBuilder<
          com.google.protobuf.Timestamp, com.google.protobuf.Timestamp.Builder, com.google.protobuf.TimestampOrBuilder> dateBuilder_;
      /**
       * <code>.google.protobuf.Timestamp date = 4;</code>
       * @return Whether the date field is set.
       */
      public boolean hasDate() {
        return ((bitField0_ & 0x00000008) != 0);
      }
      /**
       * <code>.google.protobuf.Timestamp date = 4;</code>
       * @return The date.
       */
      public com.google.protobuf.Timestamp getDate() {
        if (dateBuilder_ == null) {
          return date_ == null ? com.google.protobuf.Timestamp.getDefaultInstance() : date_;
        } else {
          return dateBuilder_.getMessage();
        }
      }
      /**
       * <code>.google.protobuf.Timestamp date = 4;</code>
       */
      public Builder setDate(com.google.protobuf.Timestamp value) {
        if (dateBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          date_ = value;
        } else {
          dateBuilder_.setMessage(value);
        }
        bitField0_ |= 0x00000008;
        onChanged();
        return this;
      }
      /**
       * <code>.google.protobuf.Timestamp date = 4;</code>
       */
      public Builder setDate(
          com.google.protobuf.Timestamp.Builder builderForValue) {
        if (dateBuilder_ == null) {
          date_ = builderForValue.build();
        } else {
          dateBuilder_.setMessage(builderForValue.build());
        }
        bitField0_ |= 0x00000008;
        onChanged();
        return this;
      }
      /**
       * <code>.google.protobuf.Timestamp date = 4;</code>
       */
      public Builder mergeDate(com.google.protobuf.Timestamp value) {
        if (dateBuilder_ == null) {
          if (((bitField0_ & 0x00000008) != 0) &&
            date_ != null &&
            date_ != com.google.protobuf.Timestamp.getDefaultInstance()) {
            getDateBuilder().mergeFrom(value);
          } else {
            date_ = value;
          }
        } else {
          dateBuilder_.mergeFrom(value);
        }
        if (date_ != null) {
          bitField0_ |= 0x00000008;
          onChanged();
        }
        return this;
      }
      /**
       * <code>.google.protobuf.Timestamp date = 4;</code>
       */
      public Builder clearDate() {
        bitField0_ = (bitField0_ & ~0x00000008);
        date_ = null;
        if (dateBuilder_ != null) {
          dateBuilder_.dispose();
          dateBuilder_ = null;
        }
        onChanged();
        return this;
      }
      /**
       * <code>.google.protobuf.Timestamp date = 4;</code>
       */
      public com.google.protobuf.Timestamp.Builder getDateBuilder() {
        bitField0_ |= 0x00000008;
        onChanged();
        return getDateFieldBuilder().getBuilder();
      }
      /**
       * <code>.google.protobuf.Timestamp date = 4;</code>
       */
      public com.google.protobuf.TimestampOrBuilder getDateOrBuilder() {
        if (dateBuilder_ != null) {
          return dateBuilder_.getMessageOrBuilder();
        } else {
          return date_ == null ?
              com.google.protobuf.Timestamp.getDefaultInstance() : date_;
        }
      }
      /**
       * <code>.google.protobuf.Timestamp date = 4;</code>
       */
      private com.google.protobuf.SingleFieldBuilder<
          com.google.protobuf.Timestamp, com.google.protobuf.Timestamp.Builder, com.google.protobuf.TimestampOrBuilder> 
          getDateFieldBuilder() {
        if (dateBuilder_ == null) {
          dateBuilder_ = new com.google.protobuf.SingleFieldBuilder<
              com.google.protobuf.Timestamp, com.google.protobuf.Timestamp.Builder, com.google.protobuf.TimestampOrBuilder>(
                  getDate(),
                  getParentForChildren(),
                  isClean());
          date_ = null;
        }
        return dateBuilder_;
      }

      // @@protoc_insertion_point(builder_scope:CommentEvent)
    }

    // @@protoc_insertion_point(class_scope:CommentEvent)
    private static final CommentEventProto.CommentEvent DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new CommentEventProto.CommentEvent();
    }

    public static CommentEventProto.CommentEvent getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<CommentEvent>
        PARSER = new com.google.protobuf.AbstractParser<CommentEvent>() {
      @java.lang.Override
      public CommentEvent parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        Builder builder = newBuilder();
        try {
          builder.mergeFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          throw e.setUnfinishedMessage(builder.buildPartial());
        } catch (com.google.protobuf.UninitializedMessageException e) {
          throw e.asInvalidProtocolBufferException().setUnfinishedMessage(builder.buildPartial());
        } catch (java.io.IOException e) {
          throw new com.google.protobuf.InvalidProtocolBufferException(e)
              .setUnfinishedMessage(builder.buildPartial());
        }
        return builder.buildPartial();
      }
    };

    public static com.google.protobuf.Parser<CommentEvent> parser() {
      return PARSER;
    }

    @java.lang.Override
    public com.google.protobuf.Parser<CommentEvent> getParserForType() {
      return PARSER;
    }

    @java.lang.Override
    public CommentEventProto.CommentEvent getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_CommentEvent_descriptor;
  private static final 
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_CommentEvent_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\023comment_event.proto\032\037google/protobuf/t" +
      "imestamp.proto\"m\n\014CommentEvent\022\021\n\tcommen" +
      "tId\030\001 \001(\003\022\020\n\010authorId\030\002 \001(\003\022\016\n\006postId\030\003 " +
      "\001(\003\022(\n\004date\030\004 \001(\0132\032.google.protobuf.Time" +
      "stampB\023B\021CommentEventProtob\006proto3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
          com.google.protobuf.TimestampProto.getDescriptor(),
        });
    internal_static_CommentEvent_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_CommentEvent_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessage.FieldAccessorTable(
        internal_static_CommentEvent_descriptor,
        new java.lang.String[] { "CommentId", "AuthorId", "PostId", "Date", });
    descriptor.resolveAllFeaturesImmutable();
    com.google.protobuf.TimestampProto.getDescriptor();
  }

  // @@protoc_insertion_point(outer_class_scope)
}
