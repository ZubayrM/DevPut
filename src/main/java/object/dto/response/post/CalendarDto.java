package object.dto.response.post;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Data
@AllArgsConstructor
public class CalendarDto {
    private Set<String> years;
    private Map<String, Integer> posts;

    public CalendarDto() {
        this.years = new TreeSet<>();
        this.posts = new TreeMap<>();
    }
}
