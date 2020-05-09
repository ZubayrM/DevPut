package object.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InitResponseDto {
    private String title = "DevPut";
    private String subtitle = "Рассказы разработчиков";
    private String phone = "+7 938 203-24-99";
    private String email = "zubayr_@live.com";
    private String copyright = "Зубайр Мухтаров";
    private String copyrightFrom = "2020";
}