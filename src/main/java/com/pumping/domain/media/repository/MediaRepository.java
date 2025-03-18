package com.pumping.domain.media.repository;

import com.pumping.domain.media.model.Media;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MediaRepository extends JpaRepository<Media, Long> {
}
