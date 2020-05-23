package object.dto.response.post;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Data
@AllArgsConstructor
public class CalendarDto {
    private Set<String> years;
    private Map<String, Integer> posts;

    public CalendarDto() {
        this.years = new HashSet<>();
        this.posts = new HashMap<>();
    }
}
