package com.epita.repository.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class Like {
    public String userId;
    public String postsLikedId;
    public LocalDateTime dateTime;
}
