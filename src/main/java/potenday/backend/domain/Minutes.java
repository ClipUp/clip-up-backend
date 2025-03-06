package potenday.backend.domain;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Minutes {

    private final List<Discussion> discussions = new ArrayList<>();
    private final List<String> decisions = new ArrayList<>();

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        if (!discussions.isEmpty()) {
            sb.append("# 논의 내용\n\n");
            for (Discussion discussion : discussions) {
                sb.append("## ").append(discussion.title()).append("\n");
                if (discussion.content() != null && !discussion.content().isEmpty()) {
                    sb.append(discussion.content().stream()
                        .map(line -> "- " + line)
                        .collect(Collectors.joining("\n"))
                    ).append("\n\n");
                }
            }
        }

        if (!decisions.isEmpty()) {
            sb.append("# 결정 사항\n\n");
            sb.append(decisions.stream()
                .map(decision -> "- " + decision)
                .collect(Collectors.joining("\n"))
            ).append("\n");
        }

        return sb.toString();
    }

    record Discussion(
        String title,
        List<String> content
    ) {

    }

}