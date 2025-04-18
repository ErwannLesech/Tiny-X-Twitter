package com.epita.payloads.sentiment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class AnalysePost {
    private String postId;
    private String content;
    /**
     * can be ('creation' or 'deletion')
     */
    private String method;
}
