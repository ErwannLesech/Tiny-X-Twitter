package com.epita.payloads.sentiment;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AnalysePost {
    private String postId;
    private String content;
    /**
     * can be ('creation' or 'deletion')
     */
    private String method;
}
