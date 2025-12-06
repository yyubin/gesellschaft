package org.yyubin.gesellschaftboot.resolver;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.yyubin.gesellschaftboot.dto.SinnerResponseDto;
import service.SinnerService;

@Controller
@RequiredArgsConstructor
public class SinnerResolver {

    private final SinnerService sinnerService;

    @QueryMapping
    public SinnerResponseDto sinner(@Argument Long id) {
        var response = sinnerService.getSinnerById(id);
        return SinnerResponseDto.from(response);
    }
}
