package com.example.guestblur.comment;

import com.example.guestblur.post.PostEntity;
import com.example.guestblur.user.UserEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Entity
@Table(name = "comments")
@EntityListeners(AuditingEntityListener.class)
public class CommentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private PostEntity post;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "author_id", nullable = false)
    private UserEntity author;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    protected CommentEntity() {
    }

    public CommentEntity(String content, PostEntity post, UserEntity author) {
        this.content = content;
        this.post = post;
        this.author = author;
    }

    public Long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public PostEntity getPost() {
        return post;
    }

    public UserEntity getAuthor() {
        return author;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
