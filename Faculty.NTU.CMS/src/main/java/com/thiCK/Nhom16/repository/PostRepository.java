package com.thiCK.Nhom16.repository;

import com.thiCK.Nhom16.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
