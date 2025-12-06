package org.yyubin.gesellschaftboot.dto.input;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PaginationInput {
    private Integer first;
    private String after;
    private Integer last;
    private String before;
}
